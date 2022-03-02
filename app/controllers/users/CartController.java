package controllers.users;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Page;
import com.avaje.ebean.Transaction;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hellobisnis.mapping.mobile.response.CartResponse;
import com.hokeba.api.BaseResponse;
import com.hokeba.http.response.global.ServiceResponse;
import com.hokeba.payment.midtrans.MidtransService;
import com.hokeba.payment.midtrans.request.MainTransactionSimple;
import com.hokeba.util.Secured;

import assets.Tool;
import controllers.BaseController;
import models.Cart;
import models.CartAdditionalDetail;
import models.Member;
import models.Product;
import models.SOrder;
import models.SOrderDetail;
import models.SOrderDetailAdditional;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Security;

public class CartController extends BaseController {
	@SuppressWarnings("rawtypes")
	private static BaseResponse response = new BaseResponse();
	private Map<String, String> messageDescription = new LinkedHashMap<>();

	@Security.Authenticated(Secured.class)
	public Result cartsByMemberId(Long memberId) {
		Page<Cart> page = Cart.getCartsByMemberId(memberId);
		response.setBaseResponse(page.getTotalRowCount(), page.getPageIndex(), page.getTotalPageCount(), success,
				new ObjectMapper().convertValue(page.getList(), CartResponse[].class));
		return ok(Json.toJson(response));
	}

	@Security.Authenticated(Secured.class)
	public Result addCart() {
		JsonNode node = request().body().asJson();

		Long memberId = node.get("memberId").asLong();
		Long productId = node.get("productId").asLong();
		Integer quantity = node.get("quantity").asInt();

		Cart cart = new Cart();
		Product product = Product.find.byId(productId);
		Member member = Member.find.byId(memberId);

		if (product == null) {
			messageDescription.put("deskripsi", "Product not found");
			response.setBaseResponse(1, 0, 1, error, messageDescription);
			return notFound(Json.toJson(response));
		}

		if (member == null) {
			messageDescription.put("deskripsi", "Member not found");
			response.setBaseResponse(1, 0, 1, error, messageDescription);
			return notFound(Json.toJson(response));
		}

		cart.note = node.has("note") ? node.get("note").asText() : "";
		cart.member = member;
		cart.product = product;
		cart.quantity = quantity;
		cart.discount = product.discount;
		cart.price = product.price;
		cart.totalPrice = product.price;

		Transaction tx = Ebean.beginTransaction();
		try {
			cart.save();
			if (node.has("additionals")) {
				JsonNode jsonAdditionals = node.get("additionals");
				if (jsonAdditionals.isArray()) {
					for (JsonNode jsonAdditional : jsonAdditionals) {
						CartAdditionalDetail detail = new CartAdditionalDetail();
						detail.cart = cart;
						Product p = Product.find.byId(jsonAdditional.get("productId").asLong());
						if (p != null) {
							detail.product = p;
							detail.price = p.price;
							detail.save();
							cart.totalPrice += detail.price;
						}
					}
				}
			}
			cart.update();
			tx.commit();
			Page<Cart> page = Cart.getCartsByMemberId(memberId);
			response.setBaseResponse(page.getTotalRowCount(), page.getPageIndex(), page.getTotalRowCount(), success,
					new ObjectMapper().convertValue(page.getList(), CartResponse[].class));
			return ok(Json.toJson(response));
		} catch (Exception e) {
			e.printStackTrace();
			tx.rollback();
			messageDescription.put("deskripsi", "Cart added failed.");
			response.setBaseResponse(1, 0, 1, error, messageDescription);
			return internalServerError(Json.toJson(response));
		} finally {
			tx.end();
		}
	}

	@Security.Authenticated(Secured.class)
	public Result delete(Long id) {
		Cart cart = Cart.find.byId(id);
		if (cart == null) {
			messageDescription.put("deskripsi", "Cart not found");
			response.setBaseResponse(1, 0, 1, error, messageDescription);
			return notFound(Json.toJson(response));
		}
		
		Transaction tx = Ebean.beginTransaction();
		try {
			if (cart.additionalDetails.size() > 0) {
				for (CartAdditionalDetail detail : cart.additionalDetails) {
					detail.delete();
				}
			}
			cart.delete();
			tx.commit();
			Page<Cart> page = Cart.getCartsByMemberId(cart.member.id);
			response.setBaseResponse(page.getTotalRowCount(), page.getPageIndex(), page.getTotalRowCount(), success,
					new ObjectMapper().convertValue(page.getList(), CartResponse[].class));
			return ok(Json.toJson(response));
		} catch (Exception e) {
			e.printStackTrace();
			tx.rollback();
			messageDescription.put("deskripsi", "Cart was not successfully removed.");
			response.setBaseResponse(1, 0, 1, error, messageDescription);
			return internalServerError(Json.toJson(response));
		} finally {
			tx.end();
		}
	}
	
	@Security.Authenticated(Secured.class)
	public Result updateAdditionals(Long id) {
		JsonNode node = request().body().asJson();
		Cart cart = Cart.find.byId(id);
		if (cart == null) {
			messageDescription.put("deskripsi", "Cart not found");
			response.setBaseResponse(1, 0, 1, error, messageDescription);
			return notFound(Json.toJson(response));
		}
		
		Transaction tx = Ebean.beginTransaction();
		try {

			if (node.has("quantity"))
				cart.quantity = node.get("quantity").asInt();

			if (node.has("note"))
				cart.note = node.get("note").asText();

			if (node.has("additionals")) {
				JsonNode nodeAdditionals = node.get("additionals");
				if (nodeAdditionals.isArray()) {
					Double totalPrice = cart.totalPrice;
					for (JsonNode nodeAdditional : nodeAdditionals) {
						String action = nodeAdditional.get("action").asText();
						if ("add".equals(action)) {
							CartAdditionalDetail detail = new CartAdditionalDetail();
							Product product = Product.find.byId(nodeAdditional.get("productId").asLong());
							if (product != null) {
								detail.cart = cart;
								detail.product = product;
								detail.price = product.price;
								detail.save();
								totalPrice += detail.price;
							}
						} else if ("delete".equals(action)) {
							CartAdditionalDetail detail = CartAdditionalDetail.find.byId(nodeAdditional.get("id").asLong());
							if (detail != null) {
								detail.delete();
								totalPrice -= detail.price;
							}
						}
					}
					cart.totalPrice = totalPrice;
					cart.update();
				}
			}
			tx.commit();
			Page<Cart> page = Cart.getCartsByMemberId(cart.member.id);
			response.setBaseResponse(page.getTotalRowCount(), page.getPageIndex(), page.getTotalRowCount(), success,
					new ObjectMapper().convertValue(page.getList(), CartResponse[].class));
			return ok(Json.toJson(response));

		} catch (Exception e) {
			e.printStackTrace();
			tx.rollback();
			messageDescription.put("deskripsi", "Additionals product changes failed.");
			response.setBaseResponse(1, 0, 1, error, messageDescription);
			return internalServerError(Json.toJson(response));
		} finally {
			tx.end();
		}
	}
	
	public Result viewDetail(Long id) {
		Cart cart = Cart.find.byId(id);
		if (cart == null) {
			messageDescription.put("deskripsi", "Cart not found");
			response.setBaseResponse(1, 0, 1, error, messageDescription);
			return notFound(Json.toJson(response));
		}
		
		CartResponse cartResponse = new ObjectMapper().convertValue(cart, CartResponse.class);
		response.setBaseResponse(1, 0, 1, success, cartResponse);
		return ok(Json.toJson(response));
	}
	
	@Security.Authenticated(Secured.class)
	public Result checkOutOrder(Long memberId) {
		JsonNode node = request().body().asJson();
		SOrder order = new SOrder();
		order.paymentType = node.has("paymentType") ? node.get("paymentType").asText() : "";
		order.orderDate = new Date();
		order.orderNumber = SOrderController.getOrderNumber();
		order.discount = node.has("discount") ? node.get("discount").asDouble() : 0d;
		order.orderType = node.has("orderType") ? node.get("orderType").asText() : "";
		
		order.deliveryRates = node.has("deliveryRates") ? node.get("deliveryRates").asDouble() : 0d;
		order.consigneeName = node.has("consigneeName") ? node.get("consigneeName").asText() : "";
		order.consigneePhoneNumber = node.has("consigneePhoneNumber") ? node.get("consigneePhoneNumber").asText() : "";
		order.consignerName = node.has("consignerName") ? node.get("consignerName").asText() : "";
		order.consignerPhoneNumber = node.has("consignerPhoneNumber") ? node.get("consignerPhoneNumber").asText() : "";
		order.originAddress = node.has("originAddress") ? node.get("originAddress").asText() : "";
		order.destinationAddress = node.has("destinationAddress") ? node.get("destinationAddress").asText() : "";
		order.orderIdShipper = node.has("orderIdShipper") ? node.get("orderIdShipper").asText() : "";
		
		Member member = Member.find.byId(memberId);
		if (member == null) {
			messageDescription.put("deskripsi", "Member not found");
			response.setBaseResponse(1, 0, 1, error, messageDescription);
			return notFound(Json.toJson(response));
		}
		order.member = member;

		Transaction tx = Ebean.beginTransaction();
		try {
			Double orderTotalPrice = 0d;
			
			Page<Cart> pCarts = Cart.getCartsByMemberId(memberId);
			List<Cart> carts = pCarts.getList();

			List<SOrderDetail> details = new ArrayList<>();
			for (Cart cart : carts) {
				SOrderDetail detail = new SOrderDetail();
				Product product = cart.product;
				detail.order = order;
				detail.product = product;
				detail.price = product.price;
				detail.quantity = cart.quantity;
				detail.totalPrice = product.price;
				detail.note = cart.note;
				Double orderDetailTotalPrice = detail.totalPrice * detail.quantity;

				List<CartAdditionalDetail> cartAdditionals = cart.additionalDetails;
				List<SOrderDetailAdditional> additionals = new ArrayList<>();
				
				for (CartAdditionalDetail cartAdditional : cartAdditionals) {
					SOrderDetailAdditional additional = new SOrderDetailAdditional();
					Product p = cartAdditional.product;
					additional.detail = detail;
					additional.product = p;
					additional.price = p.price;
					additional.discount = p.discount;
					orderDetailTotalPrice += additional.price * detail.quantity;
					additionals.add(additional);
					detail.totalPrice += additional.price;
				}
				
				detail.additionals = additionals;
				orderTotalPrice += orderDetailTotalPrice;
				details.add(detail);

			}
			
			order.totalPrice = orderTotalPrice;
			order.details = details;

	       	MainTransactionSimple mainTransaction = new MainTransactionSimple(order, "mobile");
            System.out.println("REQUEST LOG \n" + Tool.prettyPrint(Json.toJson(mainTransaction)));
            ServiceResponse responseMidtrans = MidtransService.getInstance().checkout(mainTransaction);
			System.out.println("RESPONSE LOG \n" + Tool.prettyPrint(Json.toJson(responseMidtrans)));
			
			if (responseMidtrans.getCode() == 408) {
				tx.rollback();
				ObjectNode result = Json.newObject();
				result.put("error_messages", Json.toJson(new String[]{"Request timeout, please try again later"}));
				response.setBaseResponse(1, offset, 1, timeOut, result);
    			return badRequest(Json.toJson(response));
			} else if (responseMidtrans.getCode() != 200 && responseMidtrans.getCode() != 201) {
				tx.rollback();
				response.setBaseResponse(1, offset, 1, error, Json.toJson(responseMidtrans.getData()));
				return badRequest(Json.toJson(response));
			} else {
				order.status = SOrder.ORDER_STATUS_CHECKOUT;
				order.device = SOrder.DEVICE_MOBILE;
				order.save();
				for (SOrderDetail detail : order.details) {
					detail.save();
					for (SOrderDetailAdditional additional : detail.additionals) {
						additional.save();
					}
				}
				
/*				for (Cart c : carts) {
					for (CartAdditionalDetail ca : c.additionalDetails) {
						ca.delete();
					}
					c.delete();
				}
*/				
				tx.commit();
				
				Map<String, Object> finalResponse = new LinkedHashMap<>();
				finalResponse.put("orderNumber", order.orderNumber);
				finalResponse.put("midtransObject", responseMidtrans.getData());
				response.setBaseResponse(1, offset, 1, created, Json.toJson(finalResponse));
    			return ok(Json.toJson(response));
			}
            
		} catch (Exception e) {
			e.printStackTrace();
			tx.rollback();
			messageDescription.put("deskripsi", "Order failed.");
			response.setBaseResponse(1, 0, 1, error, messageDescription);
			return internalServerError(Json.toJson(response));
		} finally {
			tx.end();
		}
	}

}
