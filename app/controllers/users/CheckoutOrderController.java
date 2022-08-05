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
import models.merchant.*;
import models.productaddon.ProductAddOn;
import models.pupoint.PickUpPointMerchant;
import models.transaction.*;
import org.json.JSONObject;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import repository.*;
import models.loyalty.*;
import repository.loyalty.*;
import dtos.loyalty.*;
import repository.pickuppoint.PickUpPointRepository;
import service.PaymentService;
import com.avaje.ebean.Query;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.io.File;
import java.io.FileOutputStream;

import java.math.RoundingMode;

import service.DownloadOrderReport;

public class CheckoutOrderController extends BaseController {

    private final static Logger.ALogger logger = Logger.of(CheckoutOrderController.class);

    private static BaseResponse response = new BaseResponse();

    private static ObjectMapper objectMapper = new ObjectMapper();

    public static Result checkoutOrder() {
        // int authority = checkAccessAuthorization("all");
        // if (authority == 200 || authority == 203) {
            JsonNode jsonNode = request().body().asJson();
            Transaction txn = Ebean.beginTransaction();
            try {
                logger.info(">>> incoming order request..." + jsonNode.toString());

                // request order
                OrderTransaction orderRequest = objectMapper.readValue(jsonNode.toString(), OrderTransaction.class);
                Order order = new Order();
                Store store = Store.findByStoreCode(orderRequest.getStoreCode());
                if (store == null) {
                    response.setBaseResponse(0, 0, 0, "Store code is not null", null);
                    return badRequest(Json.toJson(response));
                }

                Member member = null;
                if (orderRequest.getCustomerEmail() != null && orderRequest.getCustomerEmail().equalsIgnoreCase("")) {
                    member = Member.find.where().eq("t0.email", orderRequest.getCustomerEmail()).eq("merchant", store.merchant).eq("t0.is_deleted", false).setMaxRows(1).findUnique();
                    if (orderRequest.getCustomerPhoneNumber() != null && !orderRequest.getCustomerPhoneNumber().equalsIgnoreCase("")) {
                        member = Member.find.where().eq("t0.phone", orderRequest.getCustomerPhoneNumber()).eq("t0.is_deleted", false).findUnique();
                    }
                    if(member == null){
                        Member memberData = new Member();
                        memberData.fullName = orderRequest.getCustomerName() != null && orderRequest.getCustomerName() != "" ? orderRequest.getCustomerName() : null;
                        memberData.email = orderRequest.getCustomerEmail() != null && orderRequest.getCustomerEmail() != "" ? orderRequest.getCustomerEmail() : null;
                        memberData.phone = orderRequest.getCustomerPhoneNumber() != null && orderRequest.getCustomerPhoneNumber() != "" ? orderRequest.getCustomerPhoneNumber() : null;
                        memberData.save();
                        order.setMember(memberData);
                    }
                    if(member != null) {
                        member.fullName = orderRequest.getCustomerName() != null && orderRequest.getCustomerName() != "" ? orderRequest.getCustomerName() : null;
                        member.update();
                        order.setMember(member);
                    }
                }

                if (member != null && orderRequest.getUseLoyalty() == true) {
                    if(orderRequest.getLoyaltyUsage().compareTo(member.loyaltyPoint) > 0){
                        response.setBaseResponse(0, 0, 0, "Ups, point yang anda gunakan lebih besar dari point yang anda miliki", null);
                        return badRequest(Json.toJson(response));
                    }
                }

                if (member == null && orderRequest.getUseLoyalty() == true) {
                    response.setBaseResponse(0, 0, 0, "Ups, anda tidak dapat menggunakan loyalty point", null);
                    return badRequest(Json.toJson(response));
                }

                // new oders
                String orderNumber = Order.generateOrderNumber();
                order.setOrderDate(new Date());
                order.setOrderNumber(orderNumber);
                order.setOrderType(orderRequest.getOrderType());
                order.setStatus(OrderStatus.NEW_ORDER.getStatus());
                order.setStore(store);
                order.setDeviceType(orderRequest.getDeviceType());
                if (orderRequest.getDeviceType().equalsIgnoreCase("MINIPOS")) {
                    System.out.println("Out");
                    UserMerchant userMerchant = checkUserMerchantAccessAuthorization();
                    if(userMerchant != null){
                        System.out.println("in");
                        order.setUserMerchant(userMerchant);
                    }
                }

                // pickup point and table
                if (orderRequest.getOrderType().equalsIgnoreCase("TAKEAWAY") && orderRequest.getDeviceType().equalsIgnoreCase("KIOSK")) {
                    // check pickup point
                    PickUpPointMerchant pickUpPointMerchant = null;
                    if(orderRequest.getPickupPointId() != null && orderRequest.getPickupPointId() != 0){
                        pickUpPointMerchant = PickUpPointRepository.findById(orderRequest.getPickupPointId());
                        if (pickUpPointMerchant == null) {
                            response.setBaseResponse(0, 0, 0, "Pickup Point not found", null);
                            return badRequest(Json.toJson(response));
                        }
                    }
                    order.setPickUpPointMerchant(pickUpPointMerchant != null ? pickUpPointMerchant : null);
                    order.setPickupPointName(pickUpPointMerchant != null ? pickUpPointMerchant.getPupointName() : null);
                    order.setTableMerchant(null);
                    order.setTableName(null);
                } else if (orderRequest.getOrderType().equalsIgnoreCase("DINEIN")) {
                    Optional<TableMerchant> tableMerchant = null;
                    if(orderRequest.getTableId() != null && orderRequest.getTableId() != 0){
                        tableMerchant = TableMerchantRepository.findById(orderRequest.getTableId());
                        if (!tableMerchant.isPresent()) {
                            response.setBaseResponse(0, 0, 0, "Table not found", null);
                            return badRequest(Json.toJson(response));
                        }
                    }
                    order.setTableMerchant(tableMerchant != null ? tableMerchant.get() : null);
                    order.setTableName(tableMerchant != null ? tableMerchant.get().getName() : null);
                    order.setPickUpPointMerchant(null);
                    order.setPickupPointName(null);
                }
                if(orderRequest.getUseLoyalty() == true){
                    order.setTotalLoyaltyUsage(orderRequest.getLoyaltyUsage());
                }

                order.save();
                List<ProductOrderDetail> productOrderDetails = orderRequest.getProductOrderDetail();
                StringBuilder message = new StringBuilder();
                List<OrderForLoyaltyData> listOrderData = new ArrayList<>();
                for (ProductOrderDetail productOrderDetail : productOrderDetails) {
                    OrderForLoyaltyData listDataOrder = new OrderForLoyaltyData();
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

                        // ADD FOR LOYALTY
                        SubsCategoryMerchant subsCategoryMerchant = productMerchant.getSubsCategoryMerchant();
                        if (subsCategoryMerchant != null) {
                            listDataOrder.setSubsCategoryId(subsCategoryMerchant.id);
                            listDataOrder.setSubsCategoryName(subsCategoryMerchant.getSubscategoryName());
                            listDataOrder.setProductName(productMerchant.getProductName());
                            listDataOrder.setProductPrice(productOrderDetail.getProductPrice());
                            listDataOrder.setSubTotal(productOrderDetail.getSubTotal());

                            if (productOrderDetail.getProductOrderAddOns().size() != 0 || !productOrderDetail.getProductOrderAddOns().isEmpty()) {
                                for (ProductOrderAddOn productOrderAddOn : productOrderDetail.getProductOrderAddOns()) {
                                    // create product add on
                                    ProductAddOn productAddOn = ProductAddOnRepository.findByProductAssignIdAndProductId(productOrderAddOn.getProductAssignId(), productOrderAddOn.getProductId());
                                    if (productAddOn != null) {
                                        ProductMerchant addOn = ProductMerchantRepository.findById(productAddOn.getProductAssignId());
                                        if (addOn != null) {
                                            OrderForLoyaltyData listDataForLoyalty = new OrderForLoyaltyData();
                                            OrderDetailAddOn orderDetailAddOn = new OrderDetailAddOn();
                                            orderDetailAddOn.setOrderDetail(orderDetail);
                                            orderDetailAddOn.setProductAddOn(productAddOn);
                                            orderDetailAddOn.setQuantity(productOrderAddOn.getProductQty());
                                            orderDetailAddOn.setNotes(productOrderAddOn.getNotes());
                                            orderDetailAddOn.setProductPrice(productOrderAddOn.getProductPrice());
                                            orderDetailAddOn.setProductName(addOn.getProductName());
                                            orderDetailAddOn.setProductAssignId(productAddOn.getProductAssignId());
                                            orderDetailAddOn.setSubTotal(productOrderAddOn.getSubTotal());
                                            orderDetailAddOn.save();
                                            listDataForLoyalty.setSubsCategoryId(addOn.getSubsCategoryMerchant().id);
                                            listDataForLoyalty.setSubsCategoryName(addOn.getSubsCategoryMerchant().getSubscategoryName());
                                            listDataForLoyalty.setProductName(addOn.getProductName());
                                            listDataForLoyalty.setProductPrice(productOrderAddOn.getProductPrice());
                                            listDataForLoyalty.setSubTotal(productOrderAddOn.getSubTotal());
                                            listOrderData.add(listDataForLoyalty);
                                        }
                                    }
                                }
                            }
                            listOrderData.add(listDataOrder);
                        }
                    }
                }
                List<LoyaltyPointMerchant> lpMerchant = LoyaltyPointMerchantRepository.find.where().eq("merchant", store.merchant).eq("t0.is_deleted", false).findList();
                
                if(orderRequest.getUseLoyalty() == true && orderRequest.getLoyaltyUsage() != null){
                    member.loyaltyPoint = member.loyaltyPoint.subtract(orderRequest.getLoyaltyUsage());
                    member.update();
                    if(member != null){
                    BigDecimal loyaltyMine = member.loyaltyPoint;
                    BigDecimal loyaltyPointGet = BigDecimal.ZERO;
                        if(!lpMerchant.isEmpty()){
                            for(LoyaltyPointMerchant lPoint: lpMerchant){
                                BigDecimal subTotalPerCategory = BigDecimal.ZERO;
                                BigDecimal totalLoyalty = BigDecimal.ZERO;
                                for(OrderForLoyaltyData ofLD : listOrderData) {
                                    if(ofLD.getSubsCategoryId() == lPoint.getSubsCategoryMerchant().id){
                                        System.out.print("Category "+ ofLD.getSubsCategoryName() + " Total : ");
                                        subTotalPerCategory = subTotalPerCategory.add(ofLD.getSubTotal());
                                        System.out.println(subTotalPerCategory);
                                        System.out.println("=================");
                                    }
                                }
                                // GET TOTAL LOYALTY
                                if(lPoint.getCashbackType().equalsIgnoreCase("Percentage")){
                                    totalLoyalty = subTotalPerCategory.multiply(lPoint.getCashbackValue());
                                    totalLoyalty = totalLoyalty.divide(new BigDecimal(100), 0, RoundingMode.DOWN);
                                    if(totalLoyalty.compareTo(lPoint.getMaxCashbackValue()) > 0){
                                        System.out.print("Loyalty nya (max): ");
                                        System.out.println(lPoint.getMaxCashbackValue());
                                        loyaltyPointGet = loyaltyPointGet.add(lPoint.getMaxCashbackValue());
                                        loyaltyMine = loyaltyMine.add(lPoint.getMaxCashbackValue());
                                        member.loyaltyPoint = loyaltyMine;
                                        member.update();
                                    } else {
                                        System.out.print("Loyalty nya: ");
                                        System.out.println(totalLoyalty);
                                        loyaltyMine = loyaltyMine.add(totalLoyalty);
                                        loyaltyPointGet = loyaltyPointGet.add(totalLoyalty);
                                        member.loyaltyPoint = loyaltyMine;
                                        member.update();
                                    }
                                } else {
                                    totalLoyalty = lPoint.getCashbackValue();
                                    System.out.print("Loyalty nya (max point): ");
                                    System.out.println(totalLoyalty);
                                    loyaltyPointGet = loyaltyPointGet.add(totalLoyalty);
                                    member.loyaltyPoint = loyaltyMine.add(totalLoyalty);
                                    member.update();
                                }
                            }
                        }
                        System.out.print("Existing loyalty: ");
                        System.out.println(loyaltyMine);
                        System.out.print("Get Loyalty: ");
                        System.out.println(loyaltyPointGet);
                        LoyaltyPointHistory lPointHistory = LoyaltyPointHistoryRepository.findByMember(member);
                        Date date = new Date();
                        if(lPointHistory != null){
                            date = lPointHistory.getExpiredDate();
                            date.setYear(date.getYear()+1);
                        } else {
                            date.setYear(date.getYear()+1);
                        }

                        LoyaltyPointHistory lpHistory = new LoyaltyPointHistory();
                        lpHistory.setPoint(loyaltyMine);
                        lpHistory.setAdded(loyaltyPointGet);
                        lpHistory.setUsed(orderRequest.getLoyaltyUsage());
                        lpHistory.setMember(member);
                        lpHistory.setOrder(order);
                        lpHistory.setExpiredDate(date);
                        lpHistory.setMerchant(store.merchant);
                        lpHistory.save();
                    }
                }

                order.setSubTotal(orderRequest.getSubTotal());
                order.setTotalPrice(orderRequest.getTotalPrice());
                order.update();

                // CHECK USAGE PAYMENT
                MerchantPayment mPayment = MerchantPayment.findPayment.where().eq("merchant", store.merchant).eq("t0.device", orderRequest.getDeviceType()).eq("paymentMethod.paymentCode", orderRequest.getPaymentDetailResponse().getPaymentChannel()).findUnique();
                System.out.print("Payload Merchant Payment: ");
                System.out.println(Json.toJson(mPayment));
                if(mPayment == null){
                    response.setBaseResponse(0, 0, 0, "Tipe pembayaran tidak ditemukan", null);
                    return notFound(Json.toJson(response));
                }
                
                OrderPayment orderPayment = new OrderPayment();
                orderPayment.setOrder(order);
                orderPayment.setInvoiceNo(OrderPayment.generateInvoiceCode());
                orderPayment.setStatus(mPayment.typePayment.equalsIgnoreCase("DIRECT_PAYMENT") ? PaymentStatus.PAID.getStatus() : PaymentStatus.PENDING.getStatus());
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

                if(mPayment.typePayment.equalsIgnoreCase("PAYMENT_GATEWAY")){
                    // do initiate payment
                    InitiatePaymentRequest request = new InitiatePaymentRequest();
                    request.setOrderNumber(orderNumber);
                    request.setDeviceType(orderRequest.getDeviceType());
                    if (member == null) {
                        request.setCustomerName("GENERAL CUSTOMER");
                    } else {
                        request.setCustomerName(member.fullName != null && member.fullName != "" ? member.fullName : "GENERAL CUSTOMER");
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
                        orderTransactionResponse.setStatus(order.getStatus());
                        orderTransactionResponse.setPaymentMethod(orderPayment.getPaymentChannel());
                        orderTransactionResponse.setMetadata(initiatePaymentResponse.getMetadata());

                        response.setBaseResponse(1, offset, 1, success, orderTransactionResponse);
                        return ok(Json.toJson(response));
                    }
                } else {
                    System.out.println("PENDING / DIRECT");
                    InitiatePaymentRequest request = new InitiatePaymentRequest();
                    request.setOrderNumber(orderNumber);
                    request.setDeviceType(orderRequest.getDeviceType());
                    if (member == null) {
                        request.setCustomerName("GENERAL CUSTOMER");
                    } else {
                        request.setCustomerName(member.fullName != null && member.fullName != "" ? member.fullName : "GENERAL CUSTOMER");
                        request.setCustomerEmail(member.email);
                        request.setCustomerPhoneNumber(member.phone);
                    }

                    // please
                    PaymentServiceRequest paymentServiceRequest = PaymentServiceRequest.builder()
                            .paymentChannel(orderRequest.getPaymentDetailResponse().getPaymentChannel())
                            .paymentType(orderRequest.getPaymentDetailResponse().getPaymentType())
                            .bankCode(null)
                            .totalAmount(orderRequest.getPaymentDetailResponse().getTotalAmount())
                            .build();
                    request.setPaymentServiceRequest(paymentServiceRequest);
                    request.setProductOrderDetails(productOrderDetails);
                    request.setStoreCode(store.storeCode);

                    // update payment status
                    order.setStatus(mPayment.typePayment.equalsIgnoreCase("DIRECT_PAYMENT") ? OrderStatus.NEW_ORDER.getStatus() : OrderStatus.PENDING.getStatus());
                    order.setOrderQueue(createQueue(store.id));
                    order.update();

                    if (mPayment.getTypePayment().equalsIgnoreCase("DIRECT_PAYMENT")) {
                        orderPayment.setStatus(PaymentStatus.PAID.getStatus());
                    } else {
                        orderPayment.setStatus(PaymentStatus.PENDING.getStatus());
                    }

                    orderPayment.update();

                    PaymentDetail payDetail = new PaymentDetail();
                    payDetail.setOrderNumber(order.getOrderNumber());
                    payDetail.setPaymentChannel(orderRequest.getPaymentDetailResponse().getPaymentChannel());
                    payDetail.setCreationTime(new Date());
                    payDetail.setStatus(mPayment.typePayment.equalsIgnoreCase("DIRECT_PAYMENT") ? PaymentStatus.PAID.getStatus() : PaymentStatus.PENDING.getStatus());
                    payDetail.setReferenceId(null);
                    payDetail.setTotalAmount(orderRequest.getPaymentDetailResponse().getTotalAmount());
                    payDetail.setOrderPayment(orderPayment);
                    payDetail.save();

                    txn.commit();

                    OrderTransactionResponse orderTransactionResponse = new OrderTransactionResponse();
                    orderTransactionResponse.setOrderNumber(order.getOrderNumber());
                    orderTransactionResponse.setInvoiceNumber(orderPayment.getInvoiceNo());
                    orderTransactionResponse.setTotalAmount(orderRequest.getPaymentDetailResponse().getTotalAmount());
                    orderTransactionResponse.setQueueNumber(order.getOrderQueue());
                    orderTransactionResponse.setStatus(order.getStatus());
                    orderTransactionResponse.setPaymentMethod(orderPayment.getPaymentChannel());
                    orderTransactionResponse.setMetadata(null);

                    response.setBaseResponse(1, offset, 1, success, orderTransactionResponse);
                    return ok(Json.toJson(response));
                }
            } catch (Exception e) {
                e.printStackTrace();
                txn.rollback();
            } finally {
                txn.end();
            }
        // } else if (authority == 403) {
        //     response.setBaseResponse(0, 0, 0, forbidden, null);
        //     return forbidden(Json.toJson(response));
        // } else {
        //     response.setBaseResponse(0, 0, 0, unauthorized, null);
        //     return unauthorized(Json.toJson(response));
        // }
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
                        Order orders = OrderRepository.find.where().eq("t0.order_number", statusRequest.getOrderNumber()).findUnique();
                        if(orders != null && orders.getStatus().equalsIgnoreCase("PENDING")) {
                            System.out.println("Checkout change status");
                            OrderPayment ordpayment = OrderPaymentRepository.find.where().eq("order", orders).findUnique();
                            if(ordpayment != null){
                                ordpayment.setStatus("PAID");
                                ordpayment.update();
                            }
                            PaymentDetail paydetails = PaymentDetail.find.where().eq("t0.order_number", statusRequest.getOrderNumber()).findUnique();
                            if(paydetails != null){
                                paydetails.setStatus("PAID");
                                paydetails.update();
                            }
                        }
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

    public static Result cancelOrder() {
        int authority = checkAccessAuthorization("all");
        if (authority == 200 || authority == 203) {

            JsonNode jsonNode = request().body().asJson();
            String orderNumber = jsonNode.get("order_number").asText();
            Transaction trx = Ebean.beginTransaction();
            try {
                Optional<Order> order = OrderRepository.findByOrderNumber(orderNumber);
                if (!order.isPresent()) {
                    response.setBaseResponse(0, 0, 0, "Nomor order tidak ditemukan", null);
                    return badRequest(Json.toJson(response));
                }

                Optional<OrderPayment> orderPayment = OrderPaymentRepository.findByOrderId(order.get().id);
                if (!orderPayment.isPresent()) {
                    response.setBaseResponse(0, 0, 0, "order payment tidak ditemukan", null);
                    return badRequest(Json.toJson(response));
                }

                order.get().setStatus(Order.CANCELLED);
                order.get().update();

                orderPayment.get().setStatus(OrderPayment.CANCELLED);
                orderPayment.get().update();

                trx.commit();

                response.setBaseResponse(1, 0, 0, "Berhasil membatalkan pesanan. Dengan nomor order " + orderNumber, orderNumber);
                return ok(Json.toJson(response));
            } catch (Exception e) {
                logger.error("Error saat mengubah status", e);
                e.printStackTrace();
                trx.rollback();
            } finally {
                trx.end();
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

}
