package controllers.shop.member;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.avaje.ebean.Expr;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Query;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hokeba.api.BaseResponse;

import controllers.BaseController;
import dtos.order.OrderList;
import dtos.shop.ShopMemberCheckOrderResponse;
import dtos.shop.ShopMemberOrderDetail;
import dtos.voucher.CheckVoucherCodeRequest;
import models.Member;
import models.Merchant;
import models.ProductRatings;
import models.Store;
import models.merchant.ProductMerchantDetail;
import models.transaction.Order;
import models.transaction.OrderDetail;
import models.transaction.OrderDetailAddOn;
import models.transaction.OrderPayment;
import models.voucher.VoucherMerchant;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import repository.OrderPaymentRepository;
import repository.OrderRepository;
import repository.ProductMerchantDetailRepository;
import repository.ratings.ProductRatingRepository;

public class ShopMemberController extends BaseController {

    @SuppressWarnings("rawtypes")
    private static BaseResponse response = new BaseResponse();
    private final static Logger.ALogger logger = Logger.of(ShopMemberController.class);

    public static Result getMemberOrderList(Long storeId, int offset, int limit, String statusOrder, String email, String phoneNumber) throws Exception {
        try {
            if (email == null || email == "" || phoneNumber == null || phoneNumber == "") {
                throw new Exception("Email and Phone number cannot be empty");
            }

            Query<Order> query = null;
            // check store id --> mandatory
            Store store = null;

            if (storeId != null && storeId != 0L) {
                store = Store.findById(storeId);    
            }

            if (store == null) {
                throw new Exception("Store id does not exists");
            }

            Member memberUser = Member.findByEmailAndMerchantId(email, store.getMerchant().id);

            if (memberUser == null) {
                throw new Exception("Member dengan email tersebut tidak ditemukan.");
            }

            if (statusOrder.equalsIgnoreCase("ALL")) {
                query = OrderRepository.find.where()
                    .eq("store", store)
                    .order("t0.id desc");
            } else if (statusOrder.equalsIgnoreCase("CANCELLED")) {
                query = OrderRepository.find.where()
                    .eq("t0.status", statusOrder)
                    .eq("store", store)
                    .order("t0.id desc");
            } else if (statusOrder.equalsIgnoreCase("PENDING")) {
                query = OrderRepository.find.where()
                    .or(
                        Expr.ne("t0.device_type", "MINIPOS"),
                        Expr.isNull("t0.user_merchant_id")
                    )
                    .ne("t0.status", "CANCELLED")
                    .ne("t0.status", "CANCELED")
                    .eq("orderPayment.status", statusOrder)
                    .eq("store", store)
                    .order("t0.id desc");
            } else {
                query = OrderRepository.find.where()
                    .eq("t0.status", statusOrder)
                    .eq("store", store)
                    .eq("orderPayment.status", "PAID")
                    .order("t0.id desc");
            }

            ExpressionList<Order> exp = query.where();
            exp.eq("t0.user_id", memberUser.id);

            query = exp.query();

            List<OrderList> orderLists = new ArrayList<>();
            List<Order> orders = query.findPagingList(limit).getPage(offset).getList();
            Integer totalData = query.findList().size();

            if (orders.isEmpty() || orders.size() == 0) {
                response.setBaseResponse(totalData, offset, limit, success + " Showing data order", orderLists);
                return ok(Json.toJson(response));
            }

            for (Order order : orders) {
                OrderList orderRes = new OrderList();
                // looping order
                Optional<OrderPayment> orderPayment = OrderPaymentRepository.findByOrderId(order.id);

                if (!orderPayment.isPresent()) {
                    System.out.println(">>>>> order payment does not exists <<<<< ");
                    break;
                }

                OrderPayment getOrderPayment = orderPayment.get();
                orderRes.setInvoiceNumber(getOrderPayment.getInvoiceNo());
                orderRes.setDeliveryFee(getOrderPayment.getDeliveryFee());
                orderRes.setServicePrice(getOrderPayment.getServicePrice());
                orderRes.setOrderNumber(order.getOrderNumber());
                orderRes.setDestinationAddress(order.getDestinationAddress());

                if (order.getMember() != null) {
                    orderRes.setCustomerName(memberUser.fullName != null && memberUser.fullName != "" ? memberUser.fullName : "GENERAL CUSTOMER (" + order.getStore().storeName + ")");
                    orderRes.setCustomerEmail(memberUser.email);
                } else {
                    String customerName = "GENERAL CUSTOMER (" + order.getStore().storeName + ")";
                    orderRes.setCustomerName(customerName);
                }

                orderRes.setCustomerPhone(order.getPhoneNumber());

                // get rating
                List<ProductRatings> productRatings = ProductRatingRepository.findByProductRating(order.getOrderNumber());

                if (productRatings.isEmpty() || productRatings.size() == 0) {
                    orderRes.setIsRated(false);
                } else {
                    orderRes.setIsRated(true);
                }

                // get store
                orderRes.setMerchantName(order.getStore().getMerchant().name != null || order.getStore().getMerchant().name != "" ? order.getStore().getMerchant().name : null);
                orderRes.setMerchantAddress(order.getStore().getMerchant().address != null || order.getStore().getMerchant().address != "" ? order.getStore().getMerchant().address : null);
                orderRes.setTotalAmount(order.getTotalPrice());
                orderRes.setSubtotal(order.getSubTotal());
                orderRes.setOrderType(order.getOrderType());
                orderRes.setOrderQueue(order.getOrderQueue());
                orderRes.setStatusOrder(order.getStatus());
                orderRes.setStatus(order.getStatus());
                orderRes.setTableName(order.getTableName());
                orderRes.setPaymentType(getOrderPayment.getPaymentType());
                orderRes.setPaymentStatus(getOrderPayment.getStatus());
                orderRes.setPaymentChannel(getOrderPayment.getPaymentChannel());
                orderRes.setTotalAmountPayment(getOrderPayment.getTotalAmount());
                orderRes.setPaymentDate(getOrderPayment.getPaymentDate());
                orderRes.setBankCode(getOrderPayment.getBankCode());
                orderRes.setLoyaltyPoint(order.getTotalLoyaltyUsage());
                orderRes.setDiscountAmount(order.getDiscountAmount());
                orderRes.setVoucherCode(order.getVoucherCode());

                List<OrderDetail> orderDetails = OrderRepository.findOrderDetailByOrderId(order.id);
                List<OrderList.ProductOrderDetail> productOrderDetails = new ArrayList<>();

                for (OrderDetail orderDetail : orderDetails) {
                    OrderList.ProductOrderDetail productDetail = new OrderList.ProductOrderDetail();
                    ProductMerchantDetail pMD = ProductMerchantDetailRepository.findMainProduct(orderDetail.getProductMerchant());

                    productDetail.setProductId(orderDetail.getProductMerchant().id);
                    productDetail.setProductName(orderDetail.getProductName());
                    productDetail.setProductImage(pMD == null ? null : pMD.getProductImageMain());
                    productDetail.setProductPrice(orderDetail.getProductPrice());
                    productDetail.setProductQty(orderDetail.getQuantity());
                    productDetail.setNotes(orderDetail.getNotes());

                    List<OrderList.ProductOrderDetail.ProductOrderDetailAddOn> productDetailAddOns = new ArrayList<>();

                    for (OrderDetailAddOn orderDetailAddOn : orderDetail.getOrderDetailAddOns()) {
                        OrderList.ProductOrderDetail.ProductOrderDetailAddOn productAddOn = new OrderList.ProductOrderDetail.ProductOrderDetailAddOn();
                        productAddOn.setProductId(orderDetailAddOn.getProductAddOn().getProductAssignId());
                        productAddOn.setProductName(orderDetailAddOn.getProductName());
                        productAddOn.setProductPrice(orderDetailAddOn.getProductPrice());
                        productAddOn.setProductQty(orderDetailAddOn.getQuantity());
                        productAddOn.setNotes(orderDetailAddOn.getNotes());
                        productDetailAddOns.add(productAddOn);
                    }

                    productDetail.setProductAddOn(productDetailAddOns);
                    productOrderDetails.add(productDetail);
                }

                orderRes.setProductOrderDetail(productOrderDetails);
                orderLists.add(orderRes);
            }

            response.setBaseResponse(totalData, offset, limit, success + " Berhasil menampilkan data order", orderLists);
            return ok(Json.toJson(response));
        } catch (Exception ex) {
            logger.error("Error when getting list data orders");
            ex.printStackTrace();

            response.setBaseResponse(0, 0, 0, ex.getMessage(), null);
            return badRequest(Json.toJson(response));
        } catch (Error er) {
            logger.error(er.getMessage());
            er.printStackTrace();
            response.setBaseResponse(0, 0, 0, error, null);
            return badRequest(Json.toJson(response));
        }
    }

    public static Result checkVoucherCode() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = request().body().asJson();
            CheckVoucherCodeRequest request = objectMapper.readValue(jsonNode.toString(), CheckVoucherCodeRequest.class);

            // Member member = Member.findByEmail(request.getEmail());

            // if (member == null) {
            //     response.setBaseResponse(0, 0, 0, "Member Tidak Ditemukan", null);
            //     return badRequest(Json.toJson(response));
            // }

            Store store = Store.findByStoreCode(request.getStoreCode());

            if (store == null) {
                response.setBaseResponse(0, 0, 0, "Store tidak ditemukan", null);
                return badRequest(Json.toJson(response));
            }

            Merchant merchant = Merchant.find.byId(store.getMerchant().id);

            if (merchant == null) {
                response.setBaseResponse(0, 0, 0, "Merchant tidak ditemukan", null);
                return badRequest(Json.toJson(response));
            }
            Logger.info("merchant: "+merchant.id);
            VoucherMerchant voucher = VoucherMerchant.findByCode(request.getVoucherCode(), merchant);

            if (voucher == null) {
                response.setBaseResponse(0, 0, 0, "Voucher Tidak Ditemukan atau Sudah Tidak Berlaku", null);
                return badRequest(Json.toJson(response));
            }

            Date createdAt = voucher.createdAt;
            Calendar cal = Calendar.getInstance();
            cal.setTime(createdAt);
            cal.add(Calendar.DATE, voucher.getExpiryDay());

            Date expiryDate = cal.getTime();
            Date now = new Date();

            if (now.after(expiryDate)) {
                response.setBaseResponse(0, 0, 0, "Masa Berlaku Voucher Sudah Habis", null);
                return notFound(Json.toJson(response));
            }

            response.setBaseResponse(1, offset, 1, success, voucher);
            return ok(Json.toJson(response));
        } catch (Exception ex) {
            logger.error("Error when checking voucher code");
            ex.printStackTrace();

            response.setBaseResponse(0, 0, 0, ex.getMessage(), null);
            return badRequest(Json.toJson(response));
        } catch (Error er) {
            logger.error(er.getMessage());
            er.printStackTrace();
            response.setBaseResponse(0, 0, 0, error, null);
            return badRequest(Json.toJson(response));
        }
    }

    public static Result getMemberOrderDetail(String orderNumber) throws Exception {
        try {
            if (orderNumber == null || orderNumber.isEmpty() || orderNumber == "") {
                throw new Exception("order number cannot be null");
            }

            Optional<Order> order = OrderRepository.findByOrderNumber(orderNumber);

            if (!order.isPresent()) {
                throw new Exception("Nomor transaksi tidak valid atau tidak ditemukan. Mohon periksa kembali Nomor Transaksi Anda atau hubungi layanan pelanggan untuk bantuan.");
            }

            ShopMemberOrderDetail orderRes = new ShopMemberOrderDetail(order.get());

            System.out.println("Success get detail order");

            response.setBaseResponse(1, offset, 1, success, orderRes);
            return ok(Json.toJson(response));
        } catch (Exception ex) {
            System.out.println("Error when getting detail order : " + ex.getMessage());
            logger.error("Error when getting detail order");
            ex.printStackTrace();

            response.setBaseResponse(0, 0, 0, ex.getMessage(), null);
            return badRequest(Json.toJson(response));
        }
    }
    
    public static Result getMemberOrderCheckOrder(String orderNumber, String deviceToken) throws Exception {
        try {
            if (orderNumber == null || orderNumber.isEmpty() || orderNumber == "") {
                throw new Exception("order number cannot be null");
            }

            
            System.out.println("deviceToken : " + deviceToken);
            if (deviceToken == null || deviceToken.isEmpty() || deviceToken == "") {
                throw new Exception("device token cannot be null");
            }

            Optional<Order> order = OrderRepository.findByOrderNumber(orderNumber);

            if (!order.isPresent()) {
                throw new Exception("Nomor transaksi tidak valid atau tidak ditemukan. Mohon periksa kembali Nomor Transaksi Anda atau hubungi layanan pelanggan untuk bantuan.");
            }

            order.get().setDeviceToken(deviceToken);
            order.get().update();

            System.out.println("Success update device token");

            String storeName = order.get().getStore().getStoreName();
            String storeNamTemp = storeName.replaceAll("\\s","").toLowerCase() + "-1";
            String status = order.get().getStatus();
            String url = storeNamTemp + "/check-order/detail/" + status.toLowerCase() + "?order=" + orderNumber;

            System.out.println("url : " + url);

            ShopMemberCheckOrderResponse urlResponse = new ShopMemberCheckOrderResponse();
            urlResponse.setUrl(url);

            System.out.println("Success get detail order");

            response.setBaseResponse(0, 0, 0, success + " mendapatkan detail order dengan orderNumber " + orderNumber, urlResponse);
            return ok(Json.toJson(response));
        } catch (Exception e) {
            // TODO: handle exception
            logger.error("Error when getting list data orders");
            e.printStackTrace();

            response.setBaseResponse(0, 0, 0, e.getMessage(), null);
            return badRequest(Json.toJson(response));
        } catch (Error er) {
            logger.error(er.getMessage());
            er.printStackTrace();
            response.setBaseResponse(0, 0, 0, error, null);
            return badRequest(Json.toJson(response));
        }

    }
}
