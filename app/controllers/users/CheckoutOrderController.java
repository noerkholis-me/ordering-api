package controllers.users;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Transaction;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hokeba.api.BaseResponse;
import com.hokeba.http.response.global.ServiceResponse;
import controllers.BaseController;
import dtos.order.*;
import dtos.payment.*;
import models.*;
import models.merchant.ProductMerchant;
import models.merchant.TableMerchant;
import models.productaddon.ProductAddOn;
import models.pupoint.PickUpPointMerchant;
import models.transaction.*;
import org.json.JSONObject;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import repository.*;
import repository.pickuppoint.PickUpPointRepository;
import service.PaymentService;
import com.avaje.ebean.Query;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class CheckoutOrderController extends BaseController {

    private final static Logger.ALogger logger = Logger.of(CheckoutOrderController.class);

    private static BaseResponse response = new BaseResponse();

    private static ObjectMapper objectMapper = new ObjectMapper();

    public static Result checkoutOrder() {
        int authority = checkAccessAuthorization("all");
        if (authority == 200 || authority == 203) {
            JsonNode jsonNode = request().body().asJson();
            Transaction txn = Ebean.beginTransaction();
            try {
                logger.info(">>> incoming order request..." + jsonNode.toString());
                // request order
                OrderTransaction orderRequest = objectMapper.readValue(jsonNode.toString(), OrderTransaction.class);
                Store store = Store.findByStoreCode(orderRequest.getStoreCode());
                if (store == null) {
                    response.setBaseResponse(0, 0, 0, "Store code is not null", null);
                    return badRequest(Json.toJson(response));
                }

                Member member = null;
                if (orderRequest.getCustomerEmail() != null || !orderRequest.getCustomerEmail().equalsIgnoreCase("")) {
                    member = Member.findByEmail(orderRequest.getCustomerEmail());
                }

                // new oders
                Order order = new Order();
                String orderNumber = Order.generateOrderNumber();
                order.setOrderDate(new Date());
                order.setOrderNumber(orderNumber);
                order.setOrderType(orderRequest.getOrderType());
                order.setStatus(OrderStatus.NEW_ORDER.getStatus());
                order.setStore(store);
                order.setMember(member);
                // pickup point and table
                if (orderRequest.getOrderType().equalsIgnoreCase("TAKEAWAY") && orderRequest.getDeviceType().equalsIgnoreCase("KIOSK")) {
                    // check pickup point
                    PickUpPointMerchant pickUpPointMerchant = PickUpPointRepository.findById(orderRequest.getPickupPointId());
                    if (pickUpPointMerchant == null) {
                        response.setBaseResponse(0, 0, 0, "Pickup Point not found", null);
                        return badRequest(Json.toJson(response));
                    }
                    order.setPickUpPointMerchant(pickUpPointMerchant);
                    order.setPickupPointName(pickUpPointMerchant.getPupointName());
                } else if (orderRequest.getOrderType().equalsIgnoreCase("DINEIN")) {
                    Optional<TableMerchant> tableMerchant = TableMerchantRepository.findById(orderRequest.getTableId());
                    if (!tableMerchant.isPresent()) {
                        response.setBaseResponse(0, 0, 0, "Pickup Point not found", null);
                        return badRequest(Json.toJson(response));
                    }
                    order.setTableMerchant(tableMerchant.get());
                    order.setTableName(tableMerchant.get().getName());
                }

                order.save();

                List<ProductOrderDetail> productOrderDetails = orderRequest.getProductOrderDetail();
                StringBuilder message = new StringBuilder();
                for (ProductOrderDetail productOrderDetail : productOrderDetails) {
                    ProductMerchant productMerchant = ProductMerchantRepository.findById(productOrderDetail.getProductId());
                    if (productMerchant != null) {
                        OrderDetail orderDetail = new OrderDetail();
                        orderDetail.setProductMerchant(productMerchant);
                        orderDetail.setProductName(productMerchant.getProductName());
                        // =============================================================== //
                        orderDetail.setProductPrice(productOrderDetail.getProductPrice());
                        orderDetail.setQuantity(productOrderDetail.getProductQty());
                        orderDetail.setNotes(productOrderDetail.getNotes());
                        orderDetail.setSubTotal(productOrderDetail.getSubTotal());
                        orderDetail.setIsCustomizable(productOrderDetail.getIsCustomizable());
                        orderDetail.setOrder(order);
                        orderDetail.save();
                        if (productOrderDetail.getProductOrderAddOns().size() != 0 || !productOrderDetail.getProductOrderAddOns().isEmpty()) {
                            for (ProductOrderAddOn productOrderAddOn : productOrderDetail.getProductOrderAddOns()) {
                                // create product add on
                                ProductAddOn productAddOn = ProductAddOnRepository.findByProductAssignIdAndProductId(productOrderAddOn.getProductAssignId(), productOrderAddOn.getProductId());
                                if (productAddOn != null) {
                                    OrderDetailAddOn orderDetailAddOn = new OrderDetailAddOn();
                                    orderDetailAddOn.setOrderDetail(orderDetail);
                                    orderDetailAddOn.setProductAddOn(productAddOn);
                                    orderDetailAddOn.setQuantity(productOrderAddOn.getProductQty());
                                    orderDetailAddOn.setNotes(productOrderAddOn.getNotes());
                                    orderDetailAddOn.setProductPrice(productOrderAddOn.getProductPrice());
                                    orderDetailAddOn.setProductName(productAddOn.getProductMerchant().getProductName());
                                    orderDetailAddOn.setProductAssignId(productAddOn.getProductAssignId());
                                    orderDetailAddOn.setSubTotal(productOrderAddOn.getSubTotal());
                                    orderDetailAddOn.save();
                                }
                            }
                        }
                    }
                }
                // validate product
//                if (!message.toString().isEmpty()) {
//                    txn.rollback();
//                    response.setBaseResponse(0, 0, 0, message.toString(), null);
//                    return badRequest(Json.toJson(response));
//                }
//                BigDecimal subTotal = new BigDecimal(0);
//                BigDecimal totalPrice = new BigDecimal(0);
//                for (OrderDetail orderDetail : orderDetails) {
//                    subTotal = subTotal.add(orderDetail.getProductPrice().multiply(new BigDecimal(orderDetail.getQuantity())));
//                    totalPrice = subTotal;
//                    orderDetail.save();
//                }

                order.setSubTotal(orderRequest.getSubTotal());
                order.setTotalPrice(orderRequest.getTotalPrice());
                order.update();

                OrderPayment orderPayment = new OrderPayment();
                orderPayment.setOrder(order);
                orderPayment.setInvoiceNo(OrderPayment.generateInvoiceCode());
                orderPayment.setStatus(PaymentStatus.PENDING.getStatus());
                orderPayment.setPaymentType(orderRequest.getPaymentDetailResponse().getPaymentType());
                orderPayment.setPaymentChannel(orderRequest.getPaymentDetailResponse().getPaymentChannel());
                orderPayment.setPaymentDate(new Date());
                orderPayment.setTaxPercentage(orderRequest.getPaymentDetailResponse().getTaxPercentage());
                orderPayment.setServicePercentage(orderRequest.getPaymentDetailResponse().getServicePercentage());
                orderPayment.setTaxPrice(orderRequest.getPaymentDetailResponse().getTaxPrice());
                orderPayment.setServicePrice(orderRequest.getPaymentDetailResponse().getServicePrice());
                orderPayment.setPaymentFeeType(orderRequest.getPaymentDetailResponse().getPaymentFeeType());
                orderPayment.setPaymentFeeCustomer(orderRequest.getPaymentDetailResponse().getPaymentFeeCustomer());
                orderPayment.setPaymentFeeOwner(orderRequest.getPaymentDetailResponse().getPaymentFeeOwner());
                orderPayment.setTotalAmount(orderRequest.getPaymentDetailResponse().getTotalAmount());
                orderPayment.save();

                // do initiate payment
                InitiatePaymentRequest request = new InitiatePaymentRequest();
                request.setOrderNumber(orderNumber);
                request.setDeviceType(orderRequest.getDeviceType());
                if (member == null) {
                    request.setCustomerName(store.storeName);
                } else {
                    request.setCustomerName(member.fullName);
                    request.setCustomerEmail(member.email);
                    request.setCustomerPhoneNumber(member.phone);
                }

                // please
                PaymentServiceRequest paymentServiceRequest = PaymentServiceRequest.builder()
                        .paymentChannel(orderRequest.getPaymentDetailResponse().getPaymentChannel())
                        .paymentType(orderRequest.getPaymentDetailResponse().getPaymentType())
                        .bankCode(orderRequest.getPaymentDetailResponse().getBankCode())
                        .totalAmount(orderRequest.getPaymentDetailResponse().getTotalAmount())
                        .build();
                request.setPaymentServiceRequest(paymentServiceRequest);
                request.setProductOrderDetails(productOrderDetails);
                request.setStoreCode(store.storeCode);

                ServiceResponse serviceResponse = PaymentService.getInstance().initiatePayment(request);

                if (serviceResponse.getCode() == 408) {
                    txn.rollback();
                    ObjectNode result = Json.newObject();
                    result.put("error_messages", Json.toJson(new String[]{"Request timeout, please try again later"}));
                    response.setBaseResponse(1, offset, 1, timeOut, result);
                    return badRequest(Json.toJson(response));
                } else if (serviceResponse.getCode() == 400) {
                    txn.rollback();
                    response.setBaseResponse(1, offset, 1, inputParameter, serviceResponse.getData());
                    return badRequest(Json.toJson(response));
                } else {
                    // update payment status
                    order.setStatus(OrderStatus.NEW_ORDER.getStatus());
                    order.setOrderQueue(createQueue(store.id));
                    order.update();

                    orderPayment.setStatus(PaymentStatus.PENDING.getStatus());
                    orderPayment.update();

                    String object = objectMapper.writeValueAsString(serviceResponse.getData());
                    JSONObject jsonObject = new JSONObject(object);
                    String initiate = jsonObject.getJSONObject("data").toString();
                    InitiatePaymentResponse initiatePaymentResponse = objectMapper.readValue(initiate, InitiatePaymentResponse.class);

                    PaymentDetail payDetail = new PaymentDetail();
                    payDetail.setOrderNumber(order.getOrderNumber());
                    payDetail.setPaymentChannel(orderRequest.getPaymentDetailResponse().getPaymentChannel());
                    payDetail.setCreationTime(initiatePaymentResponse.getCreationTime());
                    payDetail.setStatus(initiatePaymentResponse.getStatus());
                    if (orderRequest.getPaymentDetailResponse().getPaymentChannel().equalsIgnoreCase("qr_code")) {
                        payDetail.setQrCode(initiatePaymentResponse.getMetadata().getQrCode());
                    } else if (orderRequest.getPaymentDetailResponse().getPaymentChannel().equalsIgnoreCase("virtual_account")){
                        payDetail.setAccountNumber(initiatePaymentResponse.getMetadata().getAccountNumber());
                    }
                    payDetail.setReferenceId(initiatePaymentResponse.getMetadata().getReferenceId());
                    payDetail.setTotalAmount(initiatePaymentResponse.getTotalAmount());
                    payDetail.setOrderPayment(orderPayment);
                    payDetail.save();

                    txn.commit();

                    OrderTransactionResponse orderTransactionResponse = new OrderTransactionResponse();
                    orderTransactionResponse.setOrderNumber(order.getOrderNumber());
                    orderTransactionResponse.setInvoiceNumber(orderPayment.getInvoiceNo());
                    orderTransactionResponse.setTotalAmount(initiatePaymentResponse.getTotalAmount());
                    orderTransactionResponse.setQueueNumber(order.getOrderQueue());
                    orderTransactionResponse.setMetadata(initiatePaymentResponse.getMetadata());

                    response.setBaseResponse(1, offset, 1, success, orderTransactionResponse);
                    return ok(Json.toJson(response));
                }
            } catch (Exception e) {
                e.printStackTrace();
                txn.rollback();
            } finally {
                txn.end();
            }
        } else if (authority == 403) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        } else {
            response.setBaseResponse(0, 0, 0, unauthorized, null);
            return unauthorized(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, error, null);
        return ok(Json.toJson(response));
    }

    public static Result payment() {
        // Member member = checkMemberAccessAuthorization();
        // if (member != null) {
            JsonNode jsonNode = request().body().asJson();
            Transaction txn = Ebean.beginTransaction();
            try {
                PaymentRequest paymentRequest = objectMapper.readValue(jsonNode.toString(), PaymentRequest.class);
                Optional<Order> order = OrderRepository.findByOrderNumber(paymentRequest.getOrderNumber());
                if (!order.isPresent()) {
                    response.setBaseResponse(0, 0, 0, inputParameter, null);
                    return badRequest(Json.toJson(response));
                }
                ServiceResponse serviceResponse = PaymentService.getInstance().doPayment(paymentRequest);

                if (serviceResponse.getCode() == 408) {
                    txn.rollback();
                    ObjectNode result = Json.newObject();
                    result.put("error_messages", Json.toJson(new String[]{"Request timeout, please try again later"}));
                    response.setBaseResponse(1, offset, 1, timeOut, result);
                    return badRequest(Json.toJson(response));
                } else {
                    txn.commit();

                    response.setBaseResponse(1, offset, 1, success, serviceResponse.getData());
                    return ok(Json.toJson(response));
                }
            } catch (Exception e) {
                e.printStackTrace();
                txn.rollback();
            } finally {
                txn.end();
            }

        // }
        response.setBaseResponse(0, 0, 0, "Error", null);
        return badRequest(Json.toJson(response));
    }

    public static Result listOrderMerchant(Long storeId, int offset, int limit, String statusOrder) {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if(ownMerchant != null) {
            String querySqlStore = "";
            if(storeId != null && storeId != 0){
                querySqlStore = "t0.store_id in (select st.id from STORE st where st.merchant_id = "+ownMerchant.id+" and st.id = " +storeId+ " and st.is_active = true and st.is_deleted = false)";
            } else {
                querySqlStore = "t0.store_id in (select st.id from STORE st where st.merchant_id = "+ownMerchant.id+" and st.is_active = true and st.is_deleted = false)";
            }
            String querySqlOrderPayment = "t0.id in (select op.id from order_payment op where op.status = 'PAID' AND  op.is_deleted = " +false+ ")";
            Query<Order> queryData = OrderRepository.find.where().raw(querySqlStore).raw(querySqlOrderPayment).eq("t0.status", statusOrder).order("t0.created_at desc");
            List<Order> dataOrder = OrderRepository.findOrderByStatus(queryData, offset, limit);
            List<OrderList> orderListResponses = new ArrayList<>();
            for(Order orderData: dataOrder){
                OrderList orderListResponse = new OrderList();

                List<OrderDetail> orderDetailList = OrderRepository.findDataOrderDetail(orderData.id);

                Member member = null;
                member = Member.findByIdMember(orderData.getMember().id);

                orderListResponse.setInvoiceNumber(orderData.getOrderPayment().getInvoiceNo());
                orderListResponse.setOrderNumber(orderData.getOrderNumber());
                orderListResponse.setCustomerName(member != null ? member.fullName : null);                
                orderListResponse.setMerchantName(orderData.getStore().getMerchant().fullName);
                orderListResponse.setTotalAmount(orderData.getTotalPrice());
                orderListResponse.setOrderType(orderData.getOrderType());
                orderListResponse.setStatusOrder(orderData.getStatus());
                orderListResponse.setOrderQueue(orderData.getOrderQueue());
                
                List<ProductOrderDetail> responsesOrderDetail = new ArrayList<>();
                for(OrderDetail oDetail : orderDetailList) {
                    ProductOrderDetail responseOrderDetail = new ProductOrderDetail();
                    responseOrderDetail.setProductId(oDetail.getProductMerchant().id);
                    responseOrderDetail.setProductPrice(oDetail.getProductPrice());
                    responseOrderDetail.setProductQty(oDetail.getQuantity());
                    responseOrderDetail.setNotes(oDetail.getNotes());
                    responsesOrderDetail.add(responseOrderDetail);
                }

                orderListResponse.setProductOrderDetail(responsesOrderDetail);

                OrderPayment oPayment = OrderRepository.findDataOrderPayment(orderData.id);
                orderListResponse.setPaymentType(oPayment.getPaymentType());
                orderListResponse.setPaymentChannel(oPayment.getPaymentChannel());
                orderListResponse.setTotalAmountPayment(oPayment.getTotalAmount());
                orderListResponse.setPaymentDate(oPayment.getPaymentDate());
                orderListResponse.setStatus(oPayment.getStatus());
                orderListResponses.add(orderListResponse);
            }
            response.setBaseResponse(dataOrder.size(), offset, limit, success, orderListResponses);
            return ok(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Integer createQueue(Long storeCode) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Order order = Order.find.where("t0.created_at > '" + simpleDateFormat.format(new Date()) + " 00:00:00' and order_queue IS NOT NULL and t0.store_id = '" + storeCode +  "'")
                .order("order_queue desc, id desc").setMaxRows(1).findUnique();

        return order == null ? 1 : order.getOrderQueue() + 1;
    }

    public static Result changeStatusFromMerchant() {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if(ownMerchant != null) {
            try{
                JsonNode json = request().body().asJson();
                OrderStatusChanges statusRequest = objectMapper.readValue(json.toString(), OrderStatusChanges.class);
                Transaction trx = Ebean.beginTransaction();
                try{
                    Optional<Order> orderData = OrderRepository.findByOrderNumber(statusRequest.getOrderNumber());
                    if(orderData.isPresent()){
                        orderData.get().setStatus(statusRequest.getStatusOrder());
                        orderData.get().update();

                        trx.commit();
                        response.setBaseResponse(1, 0, 0, "Berhasil mengubah status Nomor Order " + orderData.get().getOrderNumber(), orderData.get().getOrderNumber());
                        return ok(Json.toJson(response));
                    }
                    response.setBaseResponse(0, 0, 0, "Nomor order tidak ditemukan", null);
                    return badRequest(Json.toJson(response));
                } catch (Exception e) {
                    logger.error("Error saat mengubah status", e);
                    e.printStackTrace();
                    trx.rollback();
                } finally {
                    trx.end();
                }
                response.setBaseResponse(0, 0, 0, error, null);
                return badRequest(Json.toJson(response));
            } catch (Exception e) {
                logger.error("Error saat parsing json", e);
                e.printStackTrace();
            }
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result checkStatusOrderNumber(String email, String phoneNumber) {
        if(email != null && email != "" || phoneNumber != null && phoneNumber != "") {
            Member memberUser = Member.findDataCustomer(email, phoneNumber);

            if(memberUser != null){
                List<Order> dataOrder = OrderRepository.findOrderDataByUser(memberUser.id);
                List<OrderList> orderListResponses = new ArrayList<>();
                for(Order orderData: dataOrder){
                    OrderList orderListResponse = new OrderList();

                    List<OrderDetail> orderDetailList = OrderRepository.findDataOrderDetail(orderData.id);

                    orderListResponse.setInvoiceNumber(orderData.getOrderPayment().getInvoiceNo());
                    orderListResponse.setOrderNumber(orderData.getOrderNumber());
                    orderListResponse.setMerchantName(orderData.getStore().getMerchant().fullName);
                    orderListResponse.setTotalAmount(orderData.getTotalPrice());
                    orderListResponse.setOrderType(orderData.getOrderType());
                    orderListResponse.setStatusOrder(orderData.getStatus());
                    orderListResponse.setOrderQueue(orderData.getOrderQueue());
                    
                    List<ProductOrderDetail> responsesOrderDetail = new ArrayList<>();
                    for(OrderDetail oDetail : orderDetailList) {
                        ProductOrderDetail responseOrderDetail = new ProductOrderDetail();
                        responseOrderDetail.setProductId(oDetail.getProductMerchant().id);
                        responseOrderDetail.setProductPrice(oDetail.getProductPrice());
                        responseOrderDetail.setProductQty(oDetail.getQuantity());
                        responseOrderDetail.setNotes(oDetail.getNotes());
                        responsesOrderDetail.add(responseOrderDetail);
                    }

                    orderListResponse.setProductOrderDetail(responsesOrderDetail);

                    OrderPayment oPayment = OrderRepository.findDataOrderPayment(orderData.id);
                    orderListResponse.setPaymentType(oPayment.getPaymentType());
                    orderListResponse.setPaymentChannel(oPayment.getPaymentChannel());
                    orderListResponse.setTotalAmountPayment(oPayment.getTotalAmount());
                    orderListResponse.setPaymentDate(oPayment.getPaymentDate());
                    orderListResponse.setStatus(oPayment.getStatus());
                    orderListResponses.add(orderListResponse);
                }
                response.setBaseResponse(dataOrder.size(), 0, 0, "Berhasil menampilkan data", orderListResponses);
                return ok(Json.toJson(response));
            }

            response.setBaseResponse(0, 0, 0, "Email atau Nomor Telepon tidak ditemukan", null);
            return badRequest(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, "Email atau Nomor Telepon dibutuhkan", null);
        return badRequest(Json.toJson(response));
    }

}
