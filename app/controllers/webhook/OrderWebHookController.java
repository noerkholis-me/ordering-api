package controllers.webhook;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Transaction;
import com.fasterxml.jackson.databind.JsonNode;
import com.hokeba.api.BaseResponse;
import com.hokeba.util.Constant;
import controllers.BaseController;
import dtos.order.InvoicePrintResponse;
import dtos.order.OrderDetailAddOnResponse;
import dtos.order.OrderDetailResponse;
import models.Store;
import models.appsettings.AppSettings;
import models.transaction.Order;
import models.transaction.OrderDetail;
import models.transaction.OrderDetailAddOn;
import models.transaction.OrderPayment;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import repository.AppSettingRepository;
import repository.OrderRepository;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class OrderWebHookController extends BaseController {

    private final static Logger.ALogger LOGGER = Logger.of(OrderWebHookController.class);
    private static BaseResponse response = new BaseResponse();

    public static Result getOrderDetailWebhook(String orderNumber) {
        LOGGER.debug("Incoming request >>>>> ", orderNumber);
        Boolean apiKeyCheck = checkInternalServiceKey();
        if (apiKeyCheck == Boolean.FALSE) {
            response.setBaseResponse(0, 0, 0, unauthorized, null);
            return unauthorized(Json.toJson(response));
        } else {
            byte[] decodeOrderNumber = Base64.getDecoder().decode(orderNumber);
            String stringOrderNumber = new String(decodeOrderNumber);

            Optional<Order> order = OrderRepository.findByOrderNumber(stringOrderNumber);
            if (!order.isPresent()) {
                response.setBaseResponse(0, 0, 0, "order number does not exists.", null);
                return badRequest(Json.toJson(response));
            }
            Order getOrder = order.get();

            Store store = getOrder.getStore();

            InvoicePrintResponse invoicePrintResponse = new InvoicePrintResponse();

            AppSettings appSettings = AppSettingRepository.findByMerchantId(store.getMerchant().id);
            String sandboxImage = Constant.getInstance().getImageUrl().concat("/assets/images/logo-sandbox.png");
            if (appSettings == null) {
                invoicePrintResponse.setImageStoreUrl(sandboxImage);
            }

            invoicePrintResponse.setImageStoreUrl(appSettings.getAppLogo() != null && appSettings.getAppLogo() != "" ? appSettings.getAppLogo() : sandboxImage);

            invoicePrintResponse.setStoreName(store.storeName);
            invoicePrintResponse.setStoreAddress(store.storeAddress);
            invoicePrintResponse.setStorePhoneNumber(store.storePhone);

            OrderPayment orderPayment = getOrder.getOrderPayment();
            invoicePrintResponse.setInvoiceNumber(orderPayment.getInvoiceNo());
            invoicePrintResponse.setOrderNumber(getOrder.getOrderNumber());
            invoicePrintResponse.setOrderType(getOrder.getOrderType());
            invoicePrintResponse.setOrderDate(getOrder.getOrderDate());
            invoicePrintResponse.setOrderTime(getOrder.getOrderDate());

            List<OrderDetail> orderDetails = getOrder.getOrderDetails();
            List<OrderDetailResponse> orderDetailResponses = new ArrayList<>();
            for (OrderDetail orderDetail : orderDetails) {
                OrderDetailResponse orderDetailResponse = new OrderDetailResponse();
                orderDetailResponse.setProductName(orderDetail.getProductName());
                orderDetailResponse.setQty(orderDetail.getQuantity());
                orderDetailResponse.setTotal(orderDetail.getSubTotal());
                orderDetailResponse.setNoSku(orderDetail.getProductMerchant().getNoSKU());
                List<OrderDetailAddOnResponse> orderDetailAddOns = new ArrayList<>();
                List<OrderDetailAddOn> orderDetailAddOnList = orderDetail.getOrderDetailAddOns();
                for (OrderDetailAddOn orderDetailAddOn : orderDetailAddOnList) {
                    OrderDetailAddOnResponse orderDetailAddOnResponse = new OrderDetailAddOnResponse();
                    orderDetailAddOnResponse.setProductName(orderDetailAddOn.getProductName());
                    orderDetailAddOnResponse.setNoSku(orderDetailAddOn.getProductAddOn().getProductMerchant().getNoSKU());
                    orderDetailAddOns.add(orderDetailAddOnResponse);
                }
                orderDetailResponse.setOrderDetailAddOns(orderDetailAddOns);
                orderDetailResponses.add(orderDetailResponse);
            }
            invoicePrintResponse.setOrderDetails(orderDetailResponses);
            invoicePrintResponse.setSubTotal(getOrder.getSubTotal());
            invoicePrintResponse.setTaxPrice(orderPayment.getTaxPrice());
            invoicePrintResponse.setTaxPercentage(orderPayment.getTaxPercentage());
            invoicePrintResponse.setServicePercentage(orderPayment.getServicePercentage());
            invoicePrintResponse.setServiceFee(orderPayment.getServicePrice());

            invoicePrintResponse.setPaymentFeeType(orderPayment.getPaymentFeeType());
            invoicePrintResponse.setPaymentFeeOwner(orderPayment.getPaymentFeeOwner());
            invoicePrintResponse.setPaymentFeeCustomer(orderPayment.getPaymentFeeCustomer());
            invoicePrintResponse.setTotal(getOrder.getTotalPrice());
            invoicePrintResponse.setOrderQueue(getOrder.getOrderQueue());
            invoicePrintResponse.setPaymentStatus(orderPayment.getStatus());
            invoicePrintResponse.setReferenceNumber("-");
            if (getOrder.getMember() != null) {
                invoicePrintResponse.setCustomerName(getOrder.getMember().fullName != null ? getOrder.getMember().fullName : getOrder.getMember().firstName + " " + getOrder.getMember().lastName);
            } else {
                invoicePrintResponse.setCustomerName("GENERAL CUSTOMER" + getOrder.getStore().storeName);
            }

            if (getOrder.getUserMerchant() != null) {
                invoicePrintResponse.setCashierName(getOrder.getUserMerchant().getFullName() != null ? getOrder.getUserMerchant().getFullName() : getOrder.getUserMerchant().getFirstName() + " " + getOrder.getUserMerchant().getLastName());
            } else {
                invoicePrintResponse.setCashierName("Admin");
            }
            invoicePrintResponse.setOrderQrCode(Base64.getEncoder().encodeToString(getOrder.getOrderNumber().getBytes(StandardCharsets.UTF_8)));

            response.setBaseResponse(1, offset, limit, success + " showing data order detail.",
                    invoicePrintResponse);
            return ok(Json.toJson(response));
        }
    }

    public static Result updateOrderStatus() {
        Boolean apiKeyCheck = checkInternalServiceKey();
        if (apiKeyCheck == Boolean.FALSE) {
            response.setBaseResponse(0, 0, 0, unauthorized, null);
            return unauthorized(Json.toJson(response));
        } else {
            JsonNode json = request().body().asJson();
            String orderNumber = json.get("order_number").asText();
            byte[] decodeOrderNumber = Base64.getDecoder().decode(orderNumber);
            String stringOrderNumber = new String(decodeOrderNumber);
            if (orderNumber.equalsIgnoreCase("") || orderNumber == null) {
                response.setBaseResponse(0, 0, 0, "order number cannot be null or empty.", null);
                return badRequest(Json.toJson(response));
            }
            Optional<Order> order = OrderRepository.findByOrderNumber(stringOrderNumber);
            if (!order.isPresent()) {
                response.setBaseResponse(0, 0, 0, "order number does not exists.", null);
                return badRequest(Json.toJson(response));
            }
            Order getOrder = order.get();
            Transaction trx = Ebean.beginTransaction();
            try {
                getOrder.setStatus(Order.NEW_ORDER);
                getOrder.update();

                OrderPayment orderPayment = getOrder.getOrderPayment();
                orderPayment.setStatus(OrderPayment.PAID);
                orderPayment.update();

                trx.commit();

                Map<String, Object> mapResponse = new HashMap<>();
                mapResponse.put("order_number", getOrder.getOrderNumber());

                response.setBaseResponse(1, offset, limit, success + " update order status.", mapResponse);
                return ok(Json.toJson(response));
            } catch (Exception ex) {
                LOGGER.error("Error while update order", ex);
                ex.printStackTrace();
                trx.rollback();
            } finally {
                trx.end();
            }
        }
        response.setBaseResponse(1, offset, limit,  error, null);
        return internalServerError(Json.toJson(response));
    }

}
