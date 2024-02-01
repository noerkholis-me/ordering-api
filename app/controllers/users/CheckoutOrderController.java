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
import models.*;
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
import models.voucher.VoucherMerchant;

import org.joda.time.DateTime;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;

import org.json.JSONObject;
import play.Logger;
import play.Play;
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
import service.EmailService;
import service.PaymentService;
import service.firebase.FirebaseService;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class CheckoutOrderController extends BaseController {

    private static final String API_KEY_SHIPPER = Play.application().configuration().getString("sandbox.shipping.shipperapi.apikey");
    private static final String API_SHIPPER_ADDRESS = Play.application().configuration().getString("sandbox.shipping.shipperapi.v1.url");
    private static final String API_SHIPPER_DOMESTIC_ORDER = "/v3/order";
    private static final String API_SHIPPER_TRACKING = "orders?apiKey=";
    private static final String API_SHIPPER_ADDRESS_V3 = Play.application().configuration().getString("sandbox.shipping.shipperapi.v3.url");
    private static final String API_SHIPPER_AREAS_V3 = "/v3/location/areas?area_ids=";
    private static final String API_SHIPPER_DETAIL = "/v3/order";
    private final static Logger.ALogger logger = Logger.of(CheckoutOrderController.class);

    private static BaseResponse response = new BaseResponse();

    private static ObjectMapper objectMapper = new ObjectMapper();

    private static Boolean IsStoreClosed(Store store) {
        boolean storeIsClosed = false;

        if(store.getStatusOpenStore() == null) {
            storeIsClosed = true;
            String featureCreationDateStr = "2023-05-17";
            LocalDate featureOnOfStoreCreationDate = LocalDate.parse(featureCreationDateStr);
            if(store.merchant.createdAt.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isBefore(featureOnOfStoreCreationDate)) {
                storeIsClosed = false;
            }
        } else {
            if(!store.getStatusOpenStore()) {
                storeIsClosed = true;
            } else {
                if((store.getOpenAt() == null || store.getClosedAt() == null)
                        || ("".equals(store.getOpenAt()) || "".equals(store.getClosedAt()))
                ) {
                    storeIsClosed = false;
                } else {
                    LocalTime currentTime = LocalTime.now();
                    LocalTime openTime = LocalTime.parse(store.getOpenAt());
                    LocalTime closeTime = LocalTime.parse(store.getClosedAt());
                    if(currentTime.isAfter(openTime) && currentTime.isBefore(closeTime) ) {
                        storeIsClosed = false;
                    } else {
                        storeIsClosed = true;
                    }
                }
            }
        }

        return storeIsClosed;
    }

    public static Result checkoutOrder() {
        // int authority = checkAccessAuthorization("all");
        // if (authority == 200 || authority == 203) {
            JsonNode jsonNode = request().body().asJson();

            Transaction txn = Ebean.beginTransaction();
            try {
                logger.info(">>> incoming order request..." + jsonNode.toString());

                String orderType = jsonNode.get("order_type").asText();

                ObjectMapper mapper = new ObjectMapper();
                String requestDomesticOrderShipper = "{\n" + "\"consignee\": {\n" + "\"name\": \"Penerima\",\n" +
                        "\"phone_number\": \"62852280038095\"\n" + "},\n" + "\"consigner\": {\n" +
                        "\"name\": \"Pengirim\",\n" + "\"phone_number\": \"62852280038095\"\n" + "},\n" +
                        "\"courier\": {\n" + "\"cod\": false,\n" + "\"rate_id\": 58,\n" + "\"use_insurance\": true\n" + "},\n" +
                        "\"coverage\": \"domestic\",\n" + "\"destination\": {\n" + "\"address\": \"Jalan Kenangan\",\n" +
                        "\"area_id\": 12212,\n" + "\"lat\": \"-6.123123123\",\n" + "\"lng\": \"104.12312312\"\n" + "},\n" +
                        "\"external_id\": \"KRN1231123121\",\n" +
                        "\"origin\": {\n" + "\"address\": \"Jalan Kenangan\",\n" + "\"area_id\": 12212,\n" +
                        "\"lat\": \"-6.123123123\",\n" + "\"lng\": \"104.12312312\"\n" + "},\n" +
                        "\"package\": {\n" + "\"height\": 60,\n" + "\"items\": [\n" + "{\n" +
                        "\"name\": \"Baju Baju\",\n" + "\"price\": 120000,\n" + "\"qty\": 12\n" + "}\n" + "],\n" +
                        "\"length\": 30,\n" + "\"package_type\": 2,\n" + "\"price\": 1440000,\n" +
                        "\"weight\": 1.1231,\n" + "\"width\": 40\n" + "},\n" + "\"payment_type\": \"postpay\"\n" + "}";
                JsonNode jsonRequest = mapper.readTree(requestDomesticOrderShipper);

                if (orderType.equalsIgnoreCase("DELIVERY")) {
                    ((ObjectNode) jsonRequest.get("consignee")).put("name", jsonNode.get("customer_name").asText());
                    ((ObjectNode) jsonRequest.get("consignee")).put("phone_number", jsonNode.get("customer_phone_number").asText());

                    ((ObjectNode) jsonRequest.get("courier")).put("rate_id", jsonNode.get("rate_id").asInt());
                    ((ObjectNode) jsonRequest.get("courier")).put("cod", jsonNode.get("cod").asBoolean());
                    ((ObjectNode) jsonRequest.get("courier")).put("use_insurance", jsonNode.get("use_insurance").asBoolean());

                    ((ObjectNode) jsonRequest).put("coverage", "domestic");

                    ProcessBuilder shipperBuilderForAreas = new ProcessBuilder(
                            "curl",
                            "-XGET",
                            "-H", "Content-Type:application/json",
                            "-H", "user-agent: Shipper/1.0",
                            "-H", "X-API-Key: "+API_KEY_SHIPPER,
                            API_SHIPPER_ADDRESS_V3+API_SHIPPER_AREAS_V3+jsonNode.get("destination_area_id").asInt()
                    );


                    Process prosesBuilderForAreas = shipperBuilderForAreas.start();
                    InputStream isAreas = prosesBuilderForAreas.getInputStream();
                    InputStreamReader isrAreas = new InputStreamReader(isAreas);
                    BufferedReader brAreas = new BufferedReader(isrAreas);


                    String lineAreas =  brAreas.readLine();
                    JsonNode jsonResponseAreas = new ObjectMapper().readValue(lineAreas, JsonNode.class);
                    String lattitude = (String) jsonResponseAreas.get("data").get(0).get("lat").asText();
                    String longitude = (String) jsonResponseAreas.get("data").get(0).get("lng").asText();

                    ((ObjectNode) jsonRequest.get("destination")).put("address", jsonNode.get("destination_address").asText());
                    ((ObjectNode) jsonRequest.get("destination")).put("area_id", jsonNode.get("destination_area_id").asInt());
                    ((ObjectNode) jsonRequest.get("destination")).put("lat", lattitude);
                    ((ObjectNode) jsonRequest.get("destination")).put("lng", longitude);
                    ((ObjectNode) jsonRequest.get("package")).put("height", jsonNode.get("height").asInt());
                    ((ObjectNode) jsonRequest.get("package")).put("length", jsonNode.get("length").asInt());
                    ((ObjectNode) jsonRequest.get("package")).put("width", jsonNode.get("wide").asInt());
                    ((ObjectNode) jsonRequest.get("package")).put("weight", jsonNode.get("weight").asDouble());
                    ((ObjectNode) jsonRequest.get("package")).put("price", jsonNode.get("sub_total").asInt());
                    ((ObjectNode) jsonRequest.get("package")).put("package_type", jsonNode.get("package_type").asInt());
                    ((ObjectNode) jsonRequest.get("package")).remove("items");
                }

                // request order
                OrderTransaction orderRequest = objectMapper.readValue(jsonNode.toString(), OrderTransaction.class);
                Order order = new Order();
                Store store = Store.findByStoreCode(orderRequest.getStoreCode());
                if (store == null) {
                    response.setBaseResponse(0, 0, 0, "Store code is not null", null);
                    return badRequest(Json.toJson(response));
                }

                Merchant merchant = Merchant.find.byId(store.getMerchant().id);
                if (merchant.isActive == false) {
                    response.setBaseResponse(0, 0, 0, "Merchant tidak aktif", null);
                    return badRequest(Json.toJson(response));
                }

                Boolean storeIsClosed = IsStoreClosed(store);

                if(storeIsClosed) {
                    response.setBaseResponse(0, 0, 0, "Toko sedang tutup", null);
                    return badRequest(Json.toJson(response));
                }

                ((ObjectNode) jsonRequest.get("consigner")).put("name", store.storeName);
                ((ObjectNode) jsonRequest.get("consigner")).put("phone_number", store.storePhone);

                ((ObjectNode) jsonRequest.get("origin")).put("address", store.storeAddress);
                ((ObjectNode) jsonRequest.get("origin")).put("area_id", store.shipperArea.id);
                ((ObjectNode) jsonRequest.get("origin")).put("lat", String.valueOf(store.storeLatitude));
                ((ObjectNode) jsonRequest.get("origin")).put("lng", String.valueOf(store.storeLongitude));

                ((ObjectNode) jsonRequest).put("payment_type", "postpay");

                Member member = null;
                Member memberData = new Member();
                String email = orderRequest.getCustomerEmail();
                String phone = orderRequest.getCustomerPhoneNumber();
                String gguid = orderRequest.getCustomerGoogleId();
                if (gguid != null && !gguid.trim().isEmpty()) {
                	member = Member.find.where().eq("t0.google_user_id", gguid)
                			.eq("merchant", store.merchant).eq("t0.is_deleted", false).setMaxRows(1).findUnique();
                }
                if (member == null && phone != null && !phone.trim().isEmpty()) {
                	member = Member.find.where().eq("t0.phone", phone)
                			.eq("merchant", store.merchant).eq("t0.is_deleted", false).setMaxRows(1).findUnique();
                }
                if (member == null && email != null && !email.trim().isEmpty()) {
                	member = Member.find.where().eq("t0.email", email)
                			.eq("merchant", store.merchant).eq("t0.is_deleted", false).setMaxRows(1).findUnique();
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
                    if (member.email == null && email != null && !email.trim().isEmpty()) {
                    	member.email = email; 
                    }
                    if (member.phone == null && phone != null && !phone.trim().isEmpty()) {
                    	Member memberDuplicate = Member.find.where().eq("t0.phone", phone).eq("t0.is_deleted", false).setMaxRows(1).findUnique();
                    	if (memberDuplicate == null) {
                    		member.phone = phone;
                    	} else {
                    		//currently do nothing, can throw error here
                    	}
                    }
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
                
                if (member != null && orderRequest.getUseVoucher()) {
                    BigDecimal discount = BigDecimal.ZERO;
                    List<VoucherMerchant> vouchers = new ArrayList<>();
                    for (int i = 0; i < orderRequest.getVoucherId().size(); i++) {
                        VoucherMerchant voucher = VoucherMerchant.findById(orderRequest.getVoucherId().get(i));
                        vouchers.add(voucher);
                    }
                    if (vouchers.isEmpty()) {
                        response.setBaseResponse(0, 0, 0, "Voucher Tidak Ditemukan", null);
                        return notFound(Json.toJson(response));
                    }
                    for (VoucherMerchant data : vouchers) {
                        if (data.getValueText() != null && data.getValueText().equalsIgnoreCase(VoucherMerchant.NOMINAL)) {
                            discount = discount.add(data.getValue());
                        } else if (data.getValueText() != null && data.getValueText().equalsIgnoreCase(VoucherMerchant.PERCENT)) {
                            BigDecimal countDiscount = data.getValue().divide(new BigDecimal(100).setScale(2, RoundingMode.DOWN));
                            discount = discount.add(countDiscount);
                        }
                    }
                    order.setDiscountAmount(discount);
                }
                
                if (member == null && orderRequest.getUseVoucher()) {
                    response.setBaseResponse(0, 0, 0, "Ups, anda tidak dapat menggunakan voucher", null);
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
                order.setDestinationAddress(orderRequest.getDestinationAddress());
                order.setReferenceNumber(orderRequest.getReferenceNumber());
                if (orderRequest.getDeviceType().equalsIgnoreCase("MINIPOS")) {
                    System.out.println("Out");
                    UserMerchant userMerchant = checkUserMerchantAccessAuthorization();
                    if(userMerchant != null){
                        System.out.println("in");
                        order.setUserMerchant(userMerchant);
                    }
                }

                // pickup point and table
                if (orderRequest.getOrderType().equalsIgnoreCase("TAKE AWAY") && orderRequest.getDeviceType().equalsIgnoreCase("KIOSK")) {
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
                } else if (orderRequest.getOrderType().equalsIgnoreCase("DINE IN")) {
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
                ArrayNode countersNode = ((ObjectNode) jsonRequest.get("package")).putArray("items");
//                ArrayNode countersNode = nodeBaru.putArray("itemName");
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
                        counterNode.put("price", productOrderDetail.getProductPrice().intValue());

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
                orderPayment.setBankCode(orderRequest.getPaymentDetailResponse().getBankCode());
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

                    ((ObjectNode) jsonRequest).put("external_id", orderNumber);
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
                    System.out.println(Json.toJson(paymentServiceRequest));

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

                        // tambah lat dan long saat order shipper ke shipper API v3
                        if (orderRequest.getOrderType().equalsIgnoreCase("DELIVERY")) {
                            String domesticUrl = API_SHIPPER_ADDRESS_V3 + API_SHIPPER_DOMESTIC_ORDER;

                            //start find lat and long from areaId;

                            ObjectNode requestNode = (ObjectNode) jsonRequest;

                            System.out.println("incoming shipper order request : "+requestNode.toString());

                            String bodyRequest = requestNode.toString();
                            System.out.println("domestic order request : "+bodyRequest);
                            ProcessBuilder shipperBuilder = new ProcessBuilder(
                                    "curl",
                                    "-XPOST",
                                    "-H", "Content-Type:application/json",
                                    "-H", "user-agent: Shipper/1.0",
                                    "-H", "X-API-Key: "+API_KEY_SHIPPER,
                                    domesticUrl,
                                    "-d", bodyRequest
                            );


                            Process prosesBuilder = shipperBuilder.start();
                            InputStream is = prosesBuilder.getInputStream();
                            InputStreamReader isr = new InputStreamReader(is);
                            BufferedReader br = new BufferedReader(isr);


                            String line =  br.readLine();
                            JsonNode jsonResponse = new ObjectMapper().readValue(line, JsonNode.class);
                            System.out.println("domestic response : "+jsonResponse.toString());
                            String hasil = (String)jsonResponse.get("metadata").get("http_status").asText();
                            System.out.println("status domestic order : "+hasil);

                            if (hasil.equals("Created")) {
                                String idShipperOrder = (String)jsonResponse.get("data").get("order_id").asText();
                                System.out.println("order id shipper : "+idShipperOrder);
                                order.setShipperOrderId(idShipperOrder);
                                order.save();
                                orderTransactionResponse.setShipperOrderId(idShipperOrder);
                            } else {
                                String messageShipper = (String) jsonResponse.get("metadata").get("http_status").asText();
                                response.setBaseResponse(1, offset, 1, messageShipper, orderTransactionResponse);
                                return ok(Json.toJson(response));
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

                    if (member != null){
                        member.lastPurchase = new Date();
                        member.update();
                    }

                    txn.commit();

                    OrderTransactionResponse orderTransactionResponse = new OrderTransactionResponse();
                    orderTransactionResponse.setOrderNumber(order.getOrderNumber());
                    orderTransactionResponse.setInvoiceNumber(orderPayment.getInvoiceNo());
                    orderTransactionResponse.setTotalAmount(orderRequest.getPaymentDetailResponse().getTotalAmount());
                    orderTransactionResponse.setQueueNumber(order.getOrderQueue());
                    orderTransactionResponse.setStatus(order.getStatus());
                    orderTransactionResponse.setPaymentMethod(orderPayment.getPaymentChannel());
                    orderTransactionResponse.setMetadata(null);

                    
                    if (mPayment.getTypePayment().equalsIgnoreCase("DIRECT_PAYMENT")) {
                    	FirebaseService.getInstance().sendFirebaseNotifOrderToStore(order);
                    }

                    if (order.getDeviceType().equalsIgnoreCase("MINIPOS")) {
                        if (!orderPayment.getPaymentType().equalsIgnoreCase("virtual_account") || !orderPayment.getPaymentType().equalsIgnoreCase("qr_code")) {
                            if (member != null && (member.email != null && member.fullName != null)) EmailService.handleCallbackAndSendEmail(order, false);
                        }
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
        response.setBaseResponse(0, 0, 0, "ada kesalahan pada saat pembayaran, silahkan refresh halaman dan ulangi pemesanan", null);
        return badRequest(Json.toJson(response));
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
                        
                        if ("NEW_ORDER".equals(statusRequest.getStatusOrder())) {
                        	FirebaseService.getInstance().sendFirebaseNotifOrderToStore(orderData.get());
                        }
                        
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

    public static Result getTrackingShipper(String id){

        String domesticTrackingUrl = API_SHIPPER_ADDRESS + API_SHIPPER_TRACKING + API_KEY_SHIPPER;

        try{

            StringBuilder output = new StringBuilder();
            StringBuilder outputError = new StringBuilder();

            domesticTrackingUrl += "&id="+id;

            ProcessBuilder pb2 = new ProcessBuilder(
                    "curl",
                    "-XGET",
                    "-H", "user-agent: Shipper/",
                    domesticTrackingUrl
            );

            Process p = pb2.start();

            InputStream is = p.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);

            String line = br.readLine();
            JsonNode jsonResponse = new ObjectMapper().readValue(line, JsonNode.class);

            return ok(Json.toJson(jsonResponse));

        }
        catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }

    public static Result getShipmentStatus(String orderShipperId) {
        String shipperDetailUrl = API_SHIPPER_ADDRESS_V3 + API_SHIPPER_DETAIL+"/"+orderShipperId;

        try{

            StringBuilder output = new StringBuilder();
            StringBuilder outputError = new StringBuilder();

//            shipperDetailUrl;

            ProcessBuilder pb2 = new ProcessBuilder(
                    "curl",
                    "-XGET",
                    "-H", "X-API-Key: "+API_KEY_SHIPPER,
                    shipperDetailUrl
            );

            Process p = pb2.start();

            InputStream is = p.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);

            String line = br.readLine();
            JsonNode jsonResponse = new ObjectMapper().readValue(line, JsonNode.class);

            return ok(Json.toJson(jsonResponse));

        }
        catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }

//    public static Result deliveryStatus (String memberId) {
//
//        //System.out.println("==============>>>>>>>>>>>>>>>>>>> " + memberId);
//        Member member = Member.find.byId(Long.parseLong(memberId));
//
//        if (member == null) {
//            messageDescription.put("deskripsi", "Member not found");
//            response.setBaseResponse(1, 0, 1, error, messageDescription);
//            return notFound(Json.toJson(response));
//        }
//
//
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
//        SOrder dataOrder = SOrder.find.where()
//                .eq("t0.member_id", member.id)
//                .eq("t0.is_deleted",false)
//                .orderBy("t0.id desc")
//                .setMaxRows(1).findUnique();
//
//        if (dataOrder == null) {
//            messageDescription.put("deskripsi", "Order not found");
//            response.setBaseResponse(1, 0, 1, error, messageDescription);
//            return notFound(Json.toJson(response));
//        }
//
//
//        try {
//
//            List<Map<String, Object>> details = new LinkedList<>();
//            Map<String, Object> f = new HashMap<>();
//
//            String tmpStatusDescription = "";
//            String tmpStatusPayment = "";
//
//            // System.out.println("======= -------- " + dataOrder.id);
//            //System.out.println(">>>>>>>>>>> "  +dataOrder.getPaymentStatus());
//
//            if(dataOrder.getPaymentStatus()=="Paid"){
//                tmpStatusDescription = "Your order is being prepared";
//                tmpStatusPayment = "PAYMENT_VERIFY";
//            }
//            else if(dataOrder.getPaymentStatus()=="Process"){
//                tmpStatusDescription = "Your order is being process";
//                tmpStatusPayment = "PROCESS";
//            }
//            else if(dataOrder.getPaymentStatus()=="Pickup"){
//                tmpStatusDescription = "Your order is ready to deliver";
//                tmpStatusPayment= "PICK_UP";
//            }
//            else if(dataOrder.getPaymentStatus()=="On Delivery"){
//                tmpStatusDescription = "Your order is being delivered";
//                tmpStatusPayment= "DELIVERY";
//            }
//            else if(dataOrder.getPaymentStatus()=="Closed"){
//                tmpStatusDescription = "Your order is already closed";
//                tmpStatusPayment= "CLOSED";
//            }
//            else if(dataOrder.getPaymentStatus()=="Cancel"){
//                tmpStatusDescription = "Your order is cancel";
//                tmpStatusPayment= "CANCEL";
//            }
//            else if(dataOrder.getPaymentStatus()=="Waiting for Confirmation"){
//                // tmpStatusDescription = "Your order is waiting for confirmation";
//                // tmpStatusDescription = "Waiting for confirmation";
//                tmpStatusDescription = "Your order need to confirm";
//                tmpStatusPayment= "WAITING_CONFIRMATION";
//            }
//            else{
//                tmpStatusDescription = "Your payment is unpaid";
//                tmpStatusPayment= "UNPAID";
//            }
//
//            Double tmpDiscount = dataOrder.discount!=null ? dataOrder.discount:0.0;
//            Double tmpServiceFee = dataOrder.serviceFee!=null ? dataOrder.serviceFee:0.0;
//
//            List<SOrderDetail> dataDetails = SOrderDetail.find.where().eq("order_id", dataOrder.id).findList();
//
//            f.put("total_item", dataDetails.size());
//            // f.put("total_price",CommonFunction.numberFormat(dataOrder.totalPrice-tmpDiscount+tmpServiceFee));
//            // f.put("total_price",Double.valueOf(dataOrder.totalPrice-tmpDiscount+tmpServiceFee));
//            f.put("total_price",Double.valueOf(dataOrder.totalPrice-tmpDiscount+tmpServiceFee+dataOrder.fullServicesFee));
//            f.put("status",tmpStatusPayment);
//            f.put("status_description",tmpStatusDescription);
//            f.put("order_number",dataOrder.orderNumber);
//
//            details.add(f);
//
//            // response.setBaseResponse(1, 1, 1, "Success", details);
//            response.setBaseResponse(1, 1, 1, "Success", f);
//            return ok(Json.toJson(response));
//
//
//        }catch(Exception err){
//            err.printStackTrace();
//        }
//
//        return null;
//    }

}
