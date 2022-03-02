package controllers.users;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Page;
import com.avaje.ebean.Transaction;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hellobisnis.mapping.mobile.response.SOrderAdditionalDetailResponse;
import com.hellobisnis.mapping.mobile.response.SOrderDetailsResponse;
import com.hellobisnis.mapping.mobile.response.SOrderResponse;
import com.hokeba.api.BaseResponse;
import com.hokeba.http.response.global.ServiceResponse;
import com.hokeba.payment.midtrans.MidtransService;
import com.hokeba.payment.midtrans.request.MainTransactionSimple;
import com.hokeba.util.Secured;

import assets.Tool;
import controllers.BaseController;
import models.Product;
import models.SOrder;
import models.SOrderDetail;
import models.SOrderDetailAdditional;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Security;

public class SOrderController extends BaseController {
	@SuppressWarnings("rawtypes")
	private static BaseResponse response = new BaseResponse();
	private static Map<String, String> messageDescription = new LinkedHashMap<>();

	@SuppressWarnings("deprecation")
	@Security.Authenticated(Secured.class)
	public Result checkOutOrder() {
		
		JsonNode node = request().body().asJson();
		SOrder order = new SOrder();
		order.orderDate = new Date();
		order.orderNumber = getOrderNumber();
		order.discount = node.has("discount") ? node.get("discount").asDouble() : 0d;
		order.orderType = node.has("orderType") ? node.get("orderType").asText() : "";

		Transaction tx = Ebean.beginTransaction();
		try {
			Double orderTotalPrice = 0d;
			if (node.has("details")) {
				if (node.get("details").isArray()) {
					JsonNode jsonDetails = node.get("details");
					List<SOrderDetail> details = new ArrayList<>();
					for (JsonNode jsonDetail : jsonDetails) {
						SOrderDetail detail = new SOrderDetail();
						Product product = Product.find.byId(jsonDetail.get("productId").asLong());
						if (product != null) {
							detail.order = order;
							detail.product = product;
							detail.price = product.price;
							detail.quantity = jsonDetail.has("quantity") ? jsonDetail.get("quantity").asInt() : 0;
							detail.note = jsonDetail.has("note") ? jsonDetail.get("note").asText() : "";
							detail.totalPrice = product.price;
							Double orderDetailTotalPrice = detail.totalPrice * detail.quantity;
							List<SOrderDetailAdditional> additionals = new ArrayList<>();
							if (jsonDetail.has("additionals")) {
								if (jsonDetail.get("additionals").isArray()) {
									JsonNode jsonAdditionals = jsonDetail.get("additionals");
									for (JsonNode jsonAdditional : jsonAdditionals) {
										SOrderDetailAdditional additional = new SOrderDetailAdditional();
										Product p = Product.find.byId(jsonAdditional.get("productId").asLong());
										if (p != null) {
											additional.detail = detail;
											additional.product = p;
											additional.price = p.price;
											additional.discount = p.discount;
											orderDetailTotalPrice += additional.price * detail.quantity;
											additionals.add(additional);
											detail.totalPrice += additional.price;
										}
									}
								}
							}
							detail.additionals = additionals;
							orderTotalPrice += orderDetailTotalPrice;
							details.add(detail);
						}
					}
					order.totalPrice = orderTotalPrice;
					order.details = details;
				}
			}

        	MainTransactionSimple mainTransaction = new MainTransactionSimple(order, "kiosk");
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
				order.userQueue = createQueue();
				order.status = SOrder.ORDER_STATUS_CHECKOUT;
				order.device = SOrder.DEVICE_KIOSK;

				order.save();
				for (SOrderDetail detail : order.details) {
					detail.save();
					for (SOrderDetailAdditional additional : detail.additionals) {
						additional.save();
					}
				}
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
	
    public static String getOrderNumber(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMM");
        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-MM");
        SOrder so = SOrder.find.where("t0.created_at > '"+simpleDateFormat2.format(new Date())+"-01 00:00:00'")
                .order("t0.created_at desc").setMaxRows(1).findUnique();
        String seqNum = "";
        if(so == null){
            seqNum = "00001";
        }else{
            seqNum = so.orderNumber.substring(so.orderNumber.length() - 5);
            int seq = Integer.parseInt(seqNum)+1;
            seqNum = "00000" + String.valueOf(seq);
            seqNum = seqNum.substring(seqNum.length() - 5);
        }
        String code = "HSO";
        code += simpleDateFormat.format(new Date()) + seqNum;
        return code;
    }
    
	@Security.Authenticated(Secured.class)
    public static Result listOrder(int page, int pageSize, Long memberId, String status) {
    	try {
    		Page<SOrder> p = SOrder.page(page, pageSize, memberId, status);
    		
    		List<SOrderResponse> orderResponses = new ArrayList<>();
    		List<SOrder> orders = p.getList();
    		
    		for (SOrder order : orders) {
    			SOrderResponse orderResponse = new ObjectMapper().convertValue(order, SOrderResponse.class);
    			List<SOrderDetailsResponse> detailsResponses = new ArrayList<>();
    			List<SOrderDetail> details = SOrderDetail.find.where().eq("order_id", order.id).findList();
				for (SOrderDetail detail : details) {
					SOrderDetailsResponse detailsResponse = new ObjectMapper().convertValue(detail, SOrderDetailsResponse.class);
					List<SOrderDetailAdditional> additionals = SOrderDetailAdditional.find.where().eq("detail_id", detail.id).findList();
					List<SOrderAdditionalDetailResponse> additionalDetailResponses = Arrays.asList(new ObjectMapper().convertValue(additionals, SOrderAdditionalDetailResponse[].class));
					detailsResponse.setAdditionals(additionalDetailResponses);
					detailsResponses.add(detailsResponse);
				}
				orderResponse.setDetails(detailsResponses);
				orderResponses.add(orderResponse);
			}
    		
    		response.setBaseResponse(p.getTotalPageCount(), page, pageSize, "Success", orderResponses);
    		return ok(Json.toJson(response));
		} catch (Exception e) {
			e.printStackTrace();
	    	return internalServerError();
		}
    }
	
	@Security.Authenticated(Secured.class)
	public static Integer createQueue() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		SOrder so = SOrder.find.where("t0.created_at > '" + simpleDateFormat.format(new Date()) + " 00:00:00' and member_id ISNULL and user_queue IS NOT NULL ")
				.order("user_queue desc, id desc").setMaxRows(1).findUnique();

		return so == null ? 1 : so.userQueue + 1;
	}

	@Security.Authenticated(Secured.class)
	public static Result getQueue(String orderId) {
		Map<String, Object> map = new HashMap<>();

		try {
			SOrder so = SOrder.find.where().eq("orderNumber", orderId).setMaxRows(1).findUnique();

			if (so == null) {
				messageDescription.put("deskripsi", "Order not found");
				response.setBaseResponse(1, 0, 1, error, messageDescription);
				return notFound(Json.toJson(response));
			}
			
			map.put("queue", so.userQueue == null ? 1 : so.userQueue);
			response.setBaseResponse(1, offset, 1, success, Json.toJson(map));
			return ok(Json.toJson(response));
		} catch (Exception e) {
			e.printStackTrace();
	    	return internalServerError();
		}
	}
	
	@Security.Authenticated(Secured.class)
	public static Result detailOrder (String orderNumber) {
		SOrder order = SOrder.find.where().eq("orderNumber", orderNumber).setMaxRows(1).findUnique();
		
		if (order == null) {
			messageDescription.put("deskripsi", "Order not found");
			response.setBaseResponse(1, 0, 1, error, messageDescription);
			return notFound(Json.toJson(response));
		}
		
		SOrderResponse orderResponse = new ObjectMapper().convertValue(order, SOrderResponse.class);
		List<SOrderDetailsResponse> detailsResponses = new ArrayList<>();
		List<SOrderDetail> details = SOrderDetail.find.where().eq("order_id", order.id).findList();
		for (SOrderDetail detail : details) {
			SOrderDetailsResponse detailsResponse = new ObjectMapper().convertValue(detail, SOrderDetailsResponse.class);
			List<SOrderDetailAdditional> additionals = SOrderDetailAdditional.find.where().eq("detail_id", detail.id).findList();
			List<SOrderAdditionalDetailResponse> additionalDetailResponses = Arrays.asList(new ObjectMapper().convertValue(additionals, SOrderAdditionalDetailResponse[].class));
			detailsResponse.setAdditionals(additionalDetailResponses);
			detailsResponses.add(detailsResponse);
		}
		orderResponse.setDetails(detailsResponses);
		response.setBaseResponse(1, offset, 1, success, Json.toJson(orderResponse));
		return ok(Json.toJson(response));
	}
	
	

}
