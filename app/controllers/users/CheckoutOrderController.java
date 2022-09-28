package controllers.users;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Transaction;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hokeba.api.BaseResponse;
import com.hokeba.http.response.global.ServiceResponse;
import controllers.BaseController;
import dtos.loyalty.OrderForLoyaltyData;
import dtos.order.OrderStatusChanges;
import dtos.order.OrderTransaction;
import dtos.order.OrderTransactionResponse;
import dtos.order.ProductOrderAddOn;
import dtos.order.ProductOrderDetail;
import dtos.payment.InitiatePaymentRequest;
import dtos.payment.InitiatePaymentResponse;
import dtos.payment.PaymentRequest;
import dtos.payment.PaymentServiceRequest;
import models.Member;
import models.Merchant;
import models.Store;
import models.SubsCategoryMerchant;
import models.UserMerchant;
import models.loyalty.LoyaltyPointHistory;
import models.loyalty.LoyaltyPointMerchant;
import models.merchant.MerchantPayment;
import models.merchant.ProductMerchant;
import models.merchant.TableMerchant;
import models.productaddon.ProductAddOn;
import models.pupoint.PickUpPointMerchant;
import models.transaction.Order;
import models.transaction.OrderDetail;
import models.transaction.OrderDetailAddOn;
import models.transaction.OrderPayment;
import models.transaction.OrderStatus;
import models.transaction.PaymentDetail;
import models.transaction.PaymentStatus;
import org.json.JSONObject;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import repository.OrderPaymentRepository;
import repository.OrderRepository;
import repository.ProductAddOnRepository;
import repository.ProductMerchantRepository;
import repository.TableMerchantRepository;
import repository.loyalty.LoyaltyPointHistoryRepository;
import repository.loyalty.LoyaltyPointMerchantRepository;
import repository.pickuppoint.PickUpPointRepository;
import service.PaymentService;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import java.math.RoundingMode;

public class CheckoutOrderController extends BaseController {

    private static final String API_KEY_SHIPPER = "Q2JSCJ6lPZcraO4P6zDBr6vmoQVWsa3j6HLvaHWbgoPMyKrWljKG9vOteIELOz2u";
    private static final String API_SHIPPER_ADDRESS = "https://api.sandbox.shipper.id/public/v1/";
    private static final String API_SHIPPER_DOMESTIC_ORDER = "orders/domestics?apiKey=";

    private final static Logger.ALogger logger = Logger.of(CheckoutOrderController.class);

    private static BaseResponse response = new BaseResponse();

    private static ObjectMapper objectMapper = new ObjectMapper();

    public static Result checkoutOrder() {
        // int authority = checkAccessAuthorization("all");
        // if (authority == 200 || authority == 203) {
            JsonNode jsonNode = request().body().asJson();
            ObjectNode nodeBaru = (ObjectNode) jsonNode;
            Transaction txn = Ebean.beginTransaction();
            try {
                logger.info(">>> incoming order request..." + jsonNode.toString());

                nodeBaru.set("o", jsonNode.get("origin_area_id"));
                nodeBaru.set("d", jsonNode.get("destination_area_id"));
                nodeBaru.set("l", jsonNode.get("length"));
                nodeBaru.set("w", jsonNode.get("wide"));
                nodeBaru.set("h", jsonNode.get("height"));
                nodeBaru.set("wt", jsonNode.get("weight"));
                nodeBaru.set("v", jsonNode.get("total_price"));
                nodeBaru.set("rateID", jsonNode.get("rate_id"));
                nodeBaru.set("contents", jsonNode.get("content"));
                nodeBaru.set("packageType", jsonNode.get("package_type"));
                nodeBaru.set("consigneeName", jsonNode.get("customer_name"));
                nodeBaru.set("consigneePhoneNumber", jsonNode.get("customer_phone_number"));
                nodeBaru.set("originAddress", jsonNode.get("origin_address"));
                nodeBaru.set("destinationAddress", jsonNode.get("destination_address"));

                for (JsonNode jNode : jsonNode) {
                    if (jNode instanceof ObjectNode) {
                        ObjectNode objectNode = (ObjectNode) jNode;
                        objectNode.remove("origin_area_id");
                        objectNode.remove("destination_area_id");
                        objectNode.remove("length");
                        objectNode.remove("wide");
                        objectNode.remove("height");
                        objectNode.remove("weight");
                        objectNode.remove("total_price");
                        objectNode.remove("rate_id");
                        objectNode.remove("content");
                        objectNode.remove("package_type");
                        objectNode.remove("customer_name");
                        objectNode.remove("customer_phone_number");
                        objectNode.remove("origin_address");
                        objectNode.remove("destination_address");
                    }
                }
//                order.orderIdShipper = node.has("shipperName") ? node.get("shipperName").asText() : "";

                // request order
                OrderTransaction orderRequest = objectMapper.readValue(jsonNode.toString(), OrderTransaction.class);
                Order order = new Order();
                Store store = Store.findByStoreCode(orderRequest.getStoreCode());
                if (store == null) {
                    response.setBaseResponse(0, 0, 0, "Store code is not null", null);
                    return badRequest(Json.toJson(response));
                }

                ((ObjectNode) jsonNode).put("store_name", store.storeName);
                ((ObjectNode) jsonNode).put("store_number", store.storePhone);
                nodeBaru.set("consignerName", jsonNode.get("store_name"));
                nodeBaru.set("consignerPhoneNumber", jsonNode.get("store_number"));

                Member member = null;
                Member memberData = new Member();
                if (orderRequest.getCustomerEmail() != null && !orderRequest.getCustomerEmail().equalsIgnoreCase("")) {
                    member = Member.find.where().eq("t0.email", orderRequest.getCustomerEmail()).eq("merchant", store.merchant).eq("t0.is_deleted", false).setMaxRows(1).findUnique();
                }
                if (orderRequest.getCustomerEmail().isEmpty() && orderRequest.getCustomerPhoneNumber() != null && !orderRequest.getCustomerPhoneNumber().equalsIgnoreCase("")) {
                    member = Member.find.where().eq("t0.phone", orderRequest.getCustomerPhoneNumber()).eq("merchant", store.merchant).eq("t0.is_deleted", false).setMaxRows(1).findUnique();
                }
                if(member == null){
                    if (orderRequest.getCustomerName() != null && orderRequest.getCustomerName() != ""){
                        memberData.fullName = orderRequest.getCustomerName() != null && orderRequest.getCustomerName() != "" ? orderRequest.getCustomerName() : null;
                        memberData.email = orderRequest.getCustomerEmail() != null && orderRequest.getCustomerEmail() != "" ? orderRequest.getCustomerEmail() : null;
                        memberData.phone = orderRequest.getCustomerPhoneNumber() != null && orderRequest.getCustomerPhoneNumber() != "" ? orderRequest.getCustomerPhoneNumber() : null;
                        memberData.setMerchant(store.getMerchant());
                        memberData.save();
                        order.setMember(memberData);
                        order.setPhoneNumber(memberData.phone);
                        order.setMemberName(memberData.fullName != null ? memberData.fullName : memberData.firstName + " " + memberData.lastName);
                    } else {
                        order.setMemberName("GENERAL CUSTOMER");
                    }
                }
                if(member != null) {
                    member.fullName = orderRequest.getCustomerName() != null && orderRequest.getCustomerName() != "" ? orderRequest.getCustomerName() : null;
                    member.update();
                    order.setMember(member);
                    order.setPhoneNumber(member.phone);
                    order.setMemberName(member.fullName != null ? member.fullName : member.firstName + " " + member.lastName);
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
                        tableMerchant = TableMerchantRepository.findByIdAndAvailable(orderRequest.getTableId());
                        if (!tableMerchant.isPresent()) {
                            response.setBaseResponse(0, 0, 0, "Table not found or table is not available", null);
                            return badRequest(Json.toJson(response));
                        }
                        tableMerchant.get().setIsAvailable(Boolean.FALSE);
                        tableMerchant.get().update();
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
                ArrayNode countersNode = nodeBaru.putArray("itemName");
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

                        ObjectNode counterNode = countersNode.addObject();
                        counterNode.put("name", productMerchant.getProductName());
                        counterNode.put("qty", productOrderDetail.getProductQty());
                        counterNode.put("value", productOrderDetail.getProductPrice());

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
                }
                if(member != null){
                    BigDecimal loyaltyMine = member.loyaltyPoint != null ? member.loyaltyPoint : BigDecimal.ZERO;
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
                                    loyaltyPointGet = BigDecimal.ZERO;
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
                                    loyaltyPointGet = BigDecimal.ZERO;
                                    totalLoyalty = lPoint.getCashbackValue();
                                    System.out.print("Loyalty nya (max point): ");
                                    System.out.println(totalLoyalty);
                                    loyaltyPointGet = loyaltyPointGet.add(totalLoyalty);
                                    member.loyaltyPoint = loyaltyMine != null ? loyaltyMine.add(totalLoyalty) : totalLoyalty;
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
                        request.setCustomerName(memberData.fullName != null && memberData.fullName != "" ? memberData.fullName : "GENERAL CUSTOMER");
                    } else {
                        if (member.fullName != null && member.fullName != "" || member.firstName != null && member.firstName != "" || member.lastName != null && member.lastName != "") {
                            request.setCustomerName(member.fullName != null && member.fullName != "" ? member.fullName : member.firstName + " " + member.lastName);
                        } else {
                            request.setCustomerName("GENERAL CUSTOMER");
                        }
                        request.setCustomerEmail(member.email);
                        request.setCustomerPhoneNumber(member.phone);
                    }

                    ((ObjectNode) jsonNode).put("externalID", orderNumber);
                    nodeBaru.set("externalID", jsonNode.get("externalID"));

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

                        if (member != null){
                            member.lastPurchase = new Date();
                            member.update();
                        }

                        if (orderRequest.getOrderType().equalsIgnoreCase("DELIVERY")) {
                            String domesticUrl = API_SHIPPER_ADDRESS + API_SHIPPER_DOMESTIC_ORDER + API_KEY_SHIPPER;
                            String bodyRequest = nodeBaru.toString();
                            ProcessBuilder shipperBuilder = new ProcessBuilder(
                                    "curl",
                                    "-XPOST",
                                    "-H", "Content-Type:application/json",
                                    "-H", "user-agent: Shipper/1.0",
                                    domesticUrl,
                                    "-d", bodyRequest
                            );


                            Process prosesBuilder = shipperBuilder.start();
                            InputStream is = prosesBuilder.getInputStream();
                            InputStreamReader isr = new InputStreamReader(is);
                            BufferedReader br = new BufferedReader(isr);


                            String line =  br.readLine();
                            JsonNode jsonResponse = new ObjectMapper().readValue(line, JsonNode.class);
                            String hasil = (String)jsonResponse.get("status").asText();

                            if (hasil.equals("success")) {
                                String idShipperOrder = (String)jsonResponse.get("data").get("id").asText();
                                order.setShipperOrderId(idShipperOrder);
                            }
                        }

                        response.setBaseResponse(1, offset, 1, success, orderTransactionResponse);
                        return ok(Json.toJson(response));
                    }
                } else {
                    System.out.println("PENDING / DIRECT");
                    InitiatePaymentRequest request = new InitiatePaymentRequest();
                    request.setOrderNumber(orderNumber);
                    request.setDeviceType(orderRequest.getDeviceType());
                    if (member == null) {
                        request.setCustomerName(memberData.fullName != null && memberData.fullName != "" ? memberData.fullName : "GENERAL CUSTOMER");
                    } else {
                        if (member.fullName != null && member.fullName != "" || member.firstName != null && member.firstName != "" || member.lastName != null && member.lastName != "") {
                            request.setCustomerName(member.fullName != null && member.fullName != "" ? member.fullName : member.firstName + " " + member.lastName);
                        } else {
                            request.setCustomerName("GENERAL CUSTOMER");
                        }
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

                    if (member != null){
                        member.lastPurchase = new Date();
                        member.update();
                    }

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

                PaymentDetail paymentDetail = orderPayment.get().getPaymentDetail();
                if (paymentDetail == null) {
                    response.setBaseResponse(0, 0, 0, "payment detail tidak ditemukan", null);
                    return badRequest(Json.toJson(response));
                }
                paymentDetail.setStatus(PaymentDetail.INACTIVE);
                paymentDetail.update();

                orderPayment.get().setStatus(OrderPayment.CANCELLED);
                orderPayment.get().update();

                order.get().setStatus(Order.CANCELLED);
                order.get().update();

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
