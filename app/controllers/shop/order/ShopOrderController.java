package controllers.shop.order;

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
import dtos.order.OrderTransaction;
import dtos.order.OrderTransactionResponse;
import dtos.order.ProductOrderAddOn;
import dtos.order.ProductOrderDetail;
import dtos.payment.InitiatePaymentRequest;
import dtos.payment.InitiatePaymentResponse;
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

import org.json.JSONObject;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import repository.*;
import repository.loyalty.LoyaltyPointHistoryRepository;
import repository.loyalty.LoyaltyPointMerchantRepository;
import repository.pickuppoint.PickUpPointRepository;
import service.EmailService;
import service.PaymentService;
import service.firebase.FirebaseService;
import service.shop.order.ShopOrderService;

import java.math.BigDecimal;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class ShopOrderController extends BaseController {

    @SuppressWarnings("rawtypes")
    private static BaseResponse response = new BaseResponse();
    private static ObjectMapper objectMapper = new ObjectMapper();
    private final static Logger.ALogger logger = Logger.of(ShopOrderController.class);

    @SuppressWarnings("deprecation")
    public static Result checkout() {
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

            Boolean storeIsClosed = ShopOrderService.IsStoreClosed(store);

            if (storeIsClosed) {
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
                member = Member.findByGGUID(gguid);
            }

            if (member == null && email != null && !email.trim().isEmpty()) {
                member = Member.findByEmailAndMerchantId(email, store.getMerchant().id);
            }

            if (member == null) {
                if (orderRequest.getCustomerName() != null && orderRequest.getCustomerName() != "") {
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

            if (member != null) {
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
                if (orderRequest.getLoyaltyUsage().compareTo(member.loyaltyPoint) > 0) {
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

            String address = null;

            if (orderType.equalsIgnoreCase("DELIVERY")) {
                address = orderRequest.getDestinationAddressResponse().getAddress() + ',' + orderRequest.getDestinationAddressResponse().getSubDistrict() + ',' + orderRequest.getDestinationAddressResponse().getDistrict() + ',' + orderRequest.getDestinationAddressResponse().getCity() + ',' + orderRequest.getDestinationAddressResponse().getState() + ' ' + orderRequest.getDestinationAddressResponse().getPostcode();
            }

            // new oders
            String orderNumber = Order.generateOrderNumber();
            order.setOrderDate(new Date());
            order.setOrderNumber(orderNumber);
            order.setOrderType(orderRequest.getOrderType());
            order.setStatus(OrderStatus.NEW_ORDER.getStatus());
            order.setStore(store);
            order.setDeviceType(orderRequest.getDeviceType());
            order.setDestinationAddress(address);
            order.setReferenceNumber(orderRequest.getReferenceNumber());

            if (orderRequest.getDeviceType().equalsIgnoreCase("MINIPOS")) {
                System.out.println("Out");
                UserMerchant userMerchant = checkUserMerchantAccessAuthorization();

                if (userMerchant != null) {
                    System.out.println("in");
                    order.setUserMerchant(userMerchant);
                }
            }

            // pickup point and table
            if (orderRequest.getOrderType().equalsIgnoreCase("TAKE AWAY") && orderRequest.getDeviceType().equalsIgnoreCase("KIOSK")) {
                // check pickup point
                PickUpPointMerchant pickUpPointMerchant = null;

                if (orderRequest.getPickupPointId() != null && orderRequest.getPickupPointId() != 0) {
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

                if (orderRequest.getTableId() != null && orderRequest.getTableId() != 0) {
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

            if (orderRequest.getUseLoyalty() == true) {
                order.setTotalLoyaltyUsage(orderRequest.getLoyaltyUsage());
            }

            order.save();

            List<ProductOrderDetail> productOrderDetails = orderRequest.getProductOrderDetail();
            ArrayNode countersNode = ((ObjectNode) jsonRequest.get("package")).putArray("items");

            List<OrderForLoyaltyData> listOrderData = new ArrayList<>();

            for (ProductOrderDetail productOrderDetail : productOrderDetails) {
                OrderForLoyaltyData listDataOrder = new OrderForLoyaltyData();
                ProductMerchant productMerchant = ProductMerchantRepository.findById(productOrderDetail.getProductId());

                if (productMerchant != null) {
                    ProductStore psStore = ProductStoreRepository.findForCust(productMerchant.id, store.id, store.getMerchant());
                        
                    if (psStore.getStock() == null) {
                        response.setBaseResponse(0, 0, 0, "Stok Produk Kosong", null);
                        return notFound(Json.toJson(response));
                    }

                    if (productOrderDetail.getProductQty().longValue() > psStore.getStock()) {
                        response.setBaseResponse(0, 0, 0, "Stok Produk Kurang", null);
                        return notFound(Json.toJson(response));
                    }

                    psStore.setStock(psStore.getStock() - productOrderDetail.getProductQty().longValue());
                    psStore.update();

                    Long stock = productOrderDetail.getProductQty().longValue();
                    Long stockChanges = psStore.getStock();
                    String notes = order.getOrderNumber();
                    StockHistory newStockHistory = new StockHistory(merchant, store, productMerchant, psStore, stockChanges, stock, notes);
                    newStockHistory.save();

                    OrderDetail orderDetail = new OrderDetail();
                    orderDetail.setProductMerchant(productMerchant);
                    orderDetail.setProductName(productMerchant.getProductName());
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

            if (orderRequest.getUseLoyalty() == true && orderRequest.getLoyaltyUsage() != null) {
                member.loyaltyPoint = member.loyaltyPoint.subtract(orderRequest.getLoyaltyUsage());
                member.update();
            }

            if (member != null) {
                BigDecimal loyaltyMine = member.loyaltyPoint != null ? member.loyaltyPoint : BigDecimal.ZERO;
                BigDecimal loyaltyPointGet = BigDecimal.ZERO;

                if (!lpMerchant.isEmpty()) {
                    for (LoyaltyPointMerchant lPoint: lpMerchant) {
                        BigDecimal subTotalPerCategory = BigDecimal.ZERO;
                        BigDecimal totalLoyalty = BigDecimal.ZERO;

                        for (OrderForLoyaltyData ofLD : listOrderData) {
                            if (ofLD.getSubsCategoryId() == lPoint.getSubsCategoryMerchant().id) {
                                System.out.print("Category "+ ofLD.getSubsCategoryName() + " Total : ");
                                subTotalPerCategory = subTotalPerCategory.add(ofLD.getSubTotal());
                                System.out.println(subTotalPerCategory);
                                System.out.println("=================");
                            }
                        }

                        // GET TOTAL LOYALTY
                        if (lPoint.getCashbackType().equalsIgnoreCase("Percentage")) {
                            loyaltyPointGet = BigDecimal.ZERO;
                            totalLoyalty = subTotalPerCategory.multiply(lPoint.getCashbackValue());
                            totalLoyalty = totalLoyalty.divide(new BigDecimal(100), 0, RoundingMode.DOWN);

                            if (totalLoyalty.compareTo(lPoint.getMaxCashbackValue()) > 0) {
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

                if (lPointHistory != null) {
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

            if (mPayment == null) {
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
            orderPayment.setDeliveryFee(orderRequest.getPaymentDetailResponse().getDeliveryFee());
            orderPayment.setServicePrice(orderRequest.getPaymentDetailResponse().getServicePrice());
            orderPayment.setPaymentFeeType(orderRequest.getPaymentDetailResponse().getPaymentFeeType());
            orderPayment.setPaymentFeeCustomer(orderRequest.getPaymentDetailResponse().getPaymentFeeCustomer());
            orderPayment.setPaymentFeeOwner(orderRequest.getPaymentDetailResponse().getPaymentFeeOwner());
            orderPayment.setTotalAmount(orderRequest.getPaymentDetailResponse().getTotalAmount());
            orderPayment.save();

            if (mPayment.typePayment.equalsIgnoreCase("PAYMENT_GATEWAY")) {
                // do initiate payment
                InitiatePaymentRequest request = new InitiatePaymentRequest();
                request.setOrderNumber(orderNumber);
                request.setDeviceType(orderRequest.getDeviceType());

                if (member == null) {
                    request.setCustomerName(memberData.fullName != null && memberData.fullName != "" ? memberData.fullName : "GENERAL CUSTOMER");
                } else {
                    if (
                        member.fullName != null 
                        && member.fullName != "" 
                        || member.firstName != null 
                        && member.firstName != "" 
                        || member.lastName != null 
                        && member.lastName != ""
                    ) {
                        request.setCustomerName(member.fullName != null && member.fullName != "" ? member.fullName : member.firstName + " " + member.lastName);
                    } else {
                        request.setCustomerName("GENERAL CUSTOMER");
                    }

                    request.setCustomerEmail(member.email);
                    request.setCustomerPhoneNumber(member.phone);
                }

                ((ObjectNode) jsonRequest).put("external_id", orderNumber);

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
                    orderTransactionResponse.setStatus(order.getStatus());
                    orderTransactionResponse.setPaymentMethod(orderPayment.getPaymentChannel());
                    orderTransactionResponse.setMetadata(initiatePaymentResponse.getMetadata());

                    if (member != null) {
                        member.lastPurchase = new Date();
                        member.update();
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

                if (member != null) {
                    member.lastPurchase = new Date();
                    member.update();
                }

                txn.commit();

                OrderTransactionResponse orderTransactionResponse = new OrderTransactionResponse();
                orderTransactionResponse.setOrderNumber(order.getOrderNumber());

                if (orderRequest.getOrderType().equalsIgnoreCase("DINE IN")) {
                    orderTransactionResponse.setTableId(order.getTableMerchant().id);
                    orderTransactionResponse.setTableName(order.getTableName());
                }

                orderTransactionResponse.setInvoiceNumber(orderPayment.getInvoiceNo());
                orderTransactionResponse.setTotalAmount(orderRequest.getPaymentDetailResponse().getTotalAmount());
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

        response.setBaseResponse(0, 0, 0, "ada kesalahan pada saat pembayaran, silahkan refresh halaman dan ulangi pemesanan", null);
        return badRequest(Json.toJson(response));
    }
}
