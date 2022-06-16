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
import java.io.File;
import java.io.FileOutputStream;

import service.DownloadOrderReport;

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
            Query<Order> orderData = OrderRepository.find.where().eq("t0.status", statusOrder).order("t0.created_at desc");
            List<Order> orderList = OrderRepository.findOrderByStatus(orderData, offset, limit);
            List<OrderList> orderListResponse = new ArrayList<>();
            System.out.println("Awal");
            for(Order data: orderList){
                OrderList responseOrder = new OrderList();

                System.out.println("Looping Order");
                // GET ORDER PAYMENT DETAIL
                OrderPayment orderPayment = OrderPaymentRepository.find.where().eq("t0.id", data.id).findUnique();
                if(orderPayment.getStatus().equals("PAID")){
                    System.out.println("Cek Status Paid");
                    responseOrder.setInvoiceNumber(orderPayment != null ? orderPayment.getInvoiceNo() : null);
                    responseOrder.setOrderNumber(data.getOrderNumber());

                    // GET DATA OF STORE
                    Store storeData = Store.findById(data.getStore().id);

                    // GET NAME OF CUSTOMER
                    Member member = null;
                    if(data.getMember() != null) {
                        member = Member.findByIdMember(data.getMember().id);
                    }
                    responseOrder.setCustomerName(member != null ? member.fullName : "General Customer ("+ storeData.storeName +")");

                    // GET NAME OF MERCHANT
                    Merchant merchant = null;
                    if(data.getStore().getMerchant() != null) {
                        merchant = Merchant.merchantGetId(data.getStore().getMerchant().id);
                    }
                    responseOrder.setMerchantName(merchant != null ? merchant.fullName : null);
                    responseOrder.setTotalAmount(orderPayment.getTotalAmount());
                    responseOrder.setOrderType(data.getOrderType());
                    responseOrder.setStatusOrder(data.getStatus());
                    responseOrder.setOrderQueue(data.getOrderQueue());

                    // GET PRODUCT DETAIL ORDER ON MAIN PRODUCT
                    List<OrderList.ProductOrderDetail> responsesOrderDetail = new ArrayList<>();
                    List<OrderDetail> orderDetail = OrderRepository.findDataOrderDetail(data.id, "MAIN");
                    for(OrderDetail oDetail : orderDetail) {
                        System.out.println("Looping Product Main");
                        OrderList.ProductOrderDetail orderDetailResponse = new OrderList.ProductOrderDetail();
                        orderDetailResponse.setProductId(oDetail.getProductMerchant().id);
                        orderDetailResponse.setProductName(oDetail.getProductName());
                        orderDetailResponse.setProductPrice(oDetail.getProductPrice());
                        orderDetailResponse.setProductQty(oDetail.getQuantity());
                        orderDetailResponse.setNotes(oDetail.getNotes());

                        // GET PRODUCT ADD ON FROM PRODUCT MAIN
                        List<OrderList.ProductOrderDetail.ProductOrderDetailAddOn> responsesOrderDetailAddOn = new ArrayList<>();
                        List<OrderDetailAddOn> orderDetailAddOnList = OrderRepository.findOrderDataProductAddOn(oDetail.id);
                        for(OrderDetailAddOn orderDetailAddOn: orderDetailAddOnList) {
                            System.out.println("Looping Additional");
                            OrderList.ProductOrderDetail.ProductOrderDetailAddOn responseAddOn = new OrderList.ProductOrderDetail.ProductOrderDetailAddOn();
                            // ProductAddOn addOnData = ProductAddOnRepository.findByProductAssignIdAndProductId(orderDetailAddOn.getProductAssignId(), oDetail.getProductMerchant().id);
                            responseAddOn.setProductId(orderDetailAddOn.getProductAssignId());
                            responseAddOn.setProductName(orderDetailAddOn.getProductName());
                            responseAddOn.setProductPrice(orderDetailAddOn.getProductPrice());
                            responseAddOn.setProductQty(orderDetailAddOn.getQuantity());
                            responseAddOn.setNotes(orderDetailAddOn.getNotes());
                            responsesOrderDetailAddOn.add(responseAddOn);
                        }
                        orderDetailResponse.setProductAddOn(responsesOrderDetailAddOn);
                        responsesOrderDetail.add(orderDetailResponse);
                    }
                    responseOrder.setProductOrderDetail(responsesOrderDetail);
                    responseOrder.setPaymentType(orderPayment.getPaymentType());
                    responseOrder.setPaymentChannel(orderPayment.getPaymentChannel());
                    responseOrder.setTotalAmountPayment(orderPayment.getTotalAmount());
                    responseOrder.setPaymentDate(orderPayment.getPaymentDate());
                    responseOrder.setStatus(orderPayment.getStatus());

                    orderListResponse.add(responseOrder);
                }
            }
            response.setBaseResponse(orderList.size(), offset, limit, success, orderListResponse);
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
                List<OrderList> orderListResponse = new ArrayList<>();
                for(Order data: dataOrder){
                    OrderList responseOrder = new OrderList();

                    // GET ORDER PAYMENT DETAIL
                    OrderPayment orderPayment = OrderPaymentRepository.find.where().eq("t0.id", data.id).findUnique();
                    responseOrder.setInvoiceNumber(orderPayment != null ? orderPayment.getInvoiceNo() : null);
                    responseOrder.setOrderNumber(data.getOrderNumber());

                    // GET DATA OF STORE
                    Store storeData = Store.findById(data.getStore().id);

                    // GET NAME OF CUSTOMER
                    Member member = null;
                    if(data.getMember() != null) {
                        member = Member.findByIdMember(data.getMember().id);
                    }
                    responseOrder.setCustomerName(member != null ? member.fullName : "General Customer ("+ storeData.storeName +")");

                    // GET NAME OF MERCHANT
                    Merchant merchant = null;
                    if(data.getStore().getMerchant() != null) {
                        merchant = Merchant.merchantGetId(data.getStore().getMerchant().id);
                    }
                    responseOrder.setMerchantName(merchant != null ? merchant.fullName : null);
                    responseOrder.setTotalAmount(orderPayment.getTotalAmount());
                    responseOrder.setOrderType(data.getOrderType());
                    responseOrder.setStatusOrder(data.getStatus());
                    responseOrder.setOrderQueue(data.getOrderQueue());

                    // GET PRODUCT DETAIL ORDER ON MAIN PRODUCT
                    List<OrderList.ProductOrderDetail> responsesOrderDetail = new ArrayList<>();
                    List<OrderDetail> orderDetail = OrderRepository.findDataOrderDetail(data.id, "MAIN");
                    for(OrderDetail oDetail : orderDetail) {
                        OrderList.ProductOrderDetail orderDetailResponse = new OrderList.ProductOrderDetail();
                        orderDetailResponse.setProductId(oDetail.getProductMerchant().id);
                        orderDetailResponse.setProductName(oDetail.getProductName());
                        orderDetailResponse.setProductPrice(oDetail.getProductPrice());
                        orderDetailResponse.setProductQty(oDetail.getQuantity());
                        orderDetailResponse.setNotes(oDetail.getNotes());

                        // GET PRODUCT ADD ON FROM PRODUCT MAIN
                        List<OrderList.ProductOrderDetail.ProductOrderDetailAddOn> responsesOrderDetailAddOn = new ArrayList<>();
                        List<OrderDetailAddOn> orderDetailAddOnList = OrderRepository.findOrderDataProductAddOn(oDetail.id);
                        for(OrderDetailAddOn orderDetailAddOn: orderDetailAddOnList) {
                            OrderList.ProductOrderDetail.ProductOrderDetailAddOn responseAddOn = new OrderList.ProductOrderDetail.ProductOrderDetailAddOn();
                            // ProductAddOn addOnData = ProductAddOnRepository.findByProductAssignIdAndProductId(orderDetailAddOn.getProductAssignId(), oDetail.getProductMerchant().id);
                            responseAddOn.setProductId(orderDetailAddOn.getProductAssignId());
                            responseAddOn.setProductName(orderDetailAddOn.getProductName());
                            responseAddOn.setProductPrice(orderDetailAddOn.getProductPrice());
                            responseAddOn.setProductQty(orderDetailAddOn.getQuantity());
                            responseAddOn.setNotes(orderDetailAddOn.getNotes());
                            responsesOrderDetailAddOn.add(responseAddOn);
                        }
                        orderDetailResponse.setProductAddOn(responsesOrderDetailAddOn);
                        responsesOrderDetail.add(orderDetailResponse);
                    }
                    responseOrder.setProductOrderDetail(responsesOrderDetail);
                    responseOrder.setPaymentType(orderPayment.getPaymentType());
                    responseOrder.setPaymentChannel(orderPayment.getPaymentChannel());
                    responseOrder.setTotalAmountPayment(orderPayment.getTotalAmount());
                    responseOrder.setPaymentDate(orderPayment.getPaymentDate());
                    responseOrder.setStatus(orderPayment.getStatus());

                    orderListResponse.add(responseOrder);
                }
                response.setBaseResponse(dataOrder.size(), 0, 0, "Berhasil menampilkan data", orderListResponse);
                return ok(Json.toJson(response));
            }
            
            response.setBaseResponse(0, 0, 0, "Email atau Nomor Telepon tidak ditemukan", null);
            return badRequest(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, "Email atau Nomor Telepon dibutuhkan", null);
        return badRequest(Json.toJson(response));
    }
    
    public static Result orderReportMerchant(String startDate, String endDate, int offset, int limit, String statusOrder) {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if(ownMerchant != null) {
            try {
                String querySqlStore = "t0.store_id in (select st.id from STORE st where st.merchant_id = "+ownMerchant.id+" and st.is_active = true and st.is_deleted = false)";
                String querySqlOrderPayment = "t0.id in (select op.id from order_payment op where op.status = 'PAID' AND op.is_deleted = " +false+ ")";
                Query<Order> queryData = null;
                if(statusOrder != null && statusOrder != "" && startDate != null && startDate != ""){
                    if(endDate == null || endDate == ""){
                        response.setBaseResponse(0, 0, 0, "Tanggal akhir tidak boleh null atau kosong", null);
                        return badRequest(Json.toJson(response));
                    }

                    queryData = OrderRepository.find.where().raw(querySqlStore).raw(querySqlOrderPayment).eq("t0.status", statusOrder).raw("t0.order_date between '"+startDate+"' and '"+endDate+"'").order("t0.created_at desc");
                } else if(statusOrder != null && statusOrder != ""){
                    queryData = OrderRepository.find.where().raw(querySqlStore).raw(querySqlOrderPayment).eq("t0.status", statusOrder).order("t0.created_at desc");
                } else if(startDate != null && startDate != ""){
                    if(endDate == null || endDate == ""){
                        response.setBaseResponse(0, 0, 0, "Tanggal akhir tidak boleh null atau kosong", null);
                        return badRequest(Json.toJson(response));
                    }
                    queryData = OrderRepository.find.where().raw(querySqlStore).raw(querySqlOrderPayment).raw("t0.order_date between '"+startDate+"' and '"+endDate+"'").order("t0.created_at desc");
                } else {
                    queryData = OrderRepository.find.where().raw(querySqlStore).raw(querySqlOrderPayment).order("t0.created_at desc");
                }
                List<Order> orderList = OrderRepository.findOrderByStatus(queryData, offset, limit);

                List<OrderList> orderListResponse = new ArrayList<>();
                for(Order data: orderList){
                    OrderList responseOrder = new OrderList();

                    // GET ORDER PAYMENT DETAIL
                    OrderPayment orderPayment = OrderPaymentRepository.find.where().eq("t0.id", data.id).findUnique();
                    if(orderPayment.getStatus().equals("PAID")){
                        responseOrder.setInvoiceNumber(orderPayment != null ? orderPayment.getInvoiceNo() : null);
                        responseOrder.setOrderNumber(data.getOrderNumber());

                        // GET DATA OF STORE
                        Store storeData = Store.findById(data.getStore().id);

                        // GET NAME OF CUSTOMER
                        Member member = null;
                        if(data.getMember() != null) {
                            member = Member.findByIdMember(data.getMember().id);
                        }
                        responseOrder.setCustomerName(member != null ? member.fullName : "General Customer ("+ storeData.storeName +")");

                        // GET NAME OF MERCHANT
                        Merchant merchant = null;
                        if(data.getStore().getMerchant() != null) {
                            merchant = Merchant.merchantGetId(data.getStore().getMerchant().id);
                        }
                        responseOrder.setMerchantName(merchant != null ? merchant.fullName : null);
                        responseOrder.setTotalAmount(orderPayment.getTotalAmount());
                        responseOrder.setOrderType(data.getOrderType());
                        responseOrder.setStatusOrder(data.getStatus());
                        responseOrder.setOrderQueue(data.getOrderQueue());

                        // GET PRODUCT DETAIL ORDER ON MAIN PRODUCT
                        List<OrderList.ProductOrderDetail> responsesOrderDetail = new ArrayList<>();
                        List<OrderDetail> orderDetail = OrderRepository.findDataOrderDetail(data.id, "MAIN");
                        for(OrderDetail oDetail : orderDetail) {
                            OrderList.ProductOrderDetail orderDetailResponse = new OrderList.ProductOrderDetail();
                            orderDetailResponse.setProductId(oDetail.getProductMerchant().id);
                            orderDetailResponse.setProductName(oDetail.getProductName());
                            orderDetailResponse.setProductPrice(oDetail.getProductPrice());
                            orderDetailResponse.setProductQty(oDetail.getQuantity());
                            orderDetailResponse.setNotes(oDetail.getNotes());

                            // GET PRODUCT ADD ON FROM PRODUCT MAIN
                            List<OrderList.ProductOrderDetail.ProductOrderDetailAddOn> responsesOrderDetailAddOn = new ArrayList<>();
                            List<OrderDetailAddOn> orderDetailAddOnList = OrderRepository.findOrderDataProductAddOn(oDetail.id);
                            for(OrderDetailAddOn orderDetailAddOn: orderDetailAddOnList) {
                                OrderList.ProductOrderDetail.ProductOrderDetailAddOn responseAddOn = new OrderList.ProductOrderDetail.ProductOrderDetailAddOn();
                                // ProductAddOn addOnData = ProductAddOnRepository.findByProductAssignIdAndProductId(orderDetailAddOn.getProductAssignId(), oDetail.getProductMerchant().id);
                                responseAddOn.setProductId(orderDetailAddOn.getProductAssignId());
                                responseAddOn.setProductName(orderDetailAddOn.getProductName());
                                responseAddOn.setProductPrice(orderDetailAddOn.getProductPrice());
                                responseAddOn.setProductQty(orderDetailAddOn.getQuantity());
                                responseAddOn.setNotes(orderDetailAddOn.getNotes());
                                responsesOrderDetailAddOn.add(responseAddOn);
                            }
                            orderDetailResponse.setProductAddOn(responsesOrderDetailAddOn);
                            responsesOrderDetail.add(orderDetailResponse);
                        }
                        responseOrder.setProductOrderDetail(responsesOrderDetail);
                        responseOrder.setPaymentType(orderPayment.getPaymentType());
                        responseOrder.setPaymentChannel(orderPayment.getPaymentChannel());
                        responseOrder.setTotalAmountPayment(orderPayment.getTotalAmount());
                        responseOrder.setPaymentDate(orderPayment.getPaymentDate());
                        responseOrder.setStatus(orderPayment.getStatus());

                        orderListResponse.add(responseOrder);
                    }
                }
                response.setBaseResponse(orderList.size(), offset, limit, "Berhasil menampilkan order report", orderListResponse);
                return ok(Json.toJson(response));
            } catch (Exception e) {
                logger.error("Error", e);
                e.printStackTrace();
            }
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result downloadTransaction(String startDate, String endDate, int offset, int limit, String statusOrder) {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            try {
                String querySqlStore = "t0.store_id in (select st.id from STORE st where st.merchant_id = "+ownMerchant.id+" and st.is_active = true and st.is_deleted = false)";
                String querySqlOrderPayment = "t0.id in (select op.id from order_payment op where op.status = 'PAID' AND op.is_deleted = " +false+ ")";
                Query<Order> queryData = null;
                if(statusOrder != null && statusOrder != "" && startDate != null && startDate != ""){
                    if(endDate == null || endDate == ""){
                        response.setBaseResponse(0, 0, 0, "Tanggal akhir tidak boleh null atau kosong", null);
                        return badRequest(Json.toJson(response));
                    }

                    queryData = OrderRepository.find.where().raw(querySqlStore).raw(querySqlOrderPayment).eq("t0.status", statusOrder).raw("t0.order_date between '"+startDate+"' and '"+endDate+"'").order("t0.created_at desc");
                } else if(statusOrder != null && statusOrder != ""){
                    queryData = OrderRepository.find.where().raw(querySqlStore).raw(querySqlOrderPayment).eq("t0.status", statusOrder).order("t0.created_at desc");
                } else if(startDate != null && startDate != ""){
                    if(endDate == null || endDate == ""){
                        response.setBaseResponse(0, 0, 0, "Tanggal akhir tidak boleh null atau kosong", null);
                        return badRequest(Json.toJson(response));
                    }
                    queryData = OrderRepository.find.where().raw(querySqlStore).raw(querySqlOrderPayment).raw("t0.order_date between '"+startDate+"' and '"+endDate+"'").order("t0.created_at desc");
                } else {
                    queryData = OrderRepository.find.where().raw(querySqlStore).raw(querySqlOrderPayment).order("t0.created_at desc");
                }
                List<Order> orderList = OrderRepository.findOrderByStatus(queryData, offset, limit);
                File file = DownloadOrderReport.downloadOrderReport(orderList, ownMerchant.id);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
                String filenameOrderReport = "OrderReport-" +simpleDateFormat.format(new Date()).toString()+".xlsx";
                response().setContentType("application/vnd.ms-excel");
                response().setHeader("Content-disposition", "attachment; filename="+filenameOrderReport);
                return ok(file);
            } catch (Exception ex) {
                ex.printStackTrace();
                logger.error("Error while download order report ", ex);
            }
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }
}
