package controllers.payment;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hokeba.api.BaseResponse;
import com.hokeba.http.response.global.ServiceResponse;
import controllers.BaseController;
import dtos.payment.OrderPaymentResponse;
import dtos.payment.ShowQrCodeResponse;
import dtos.payment.PaymentListResponsePOS;
import models.Member;
import models.Merchant;
import models.transaction.Order;
import models.transaction.OrderPayment;
import models.transaction.PaymentDetail;
import play.libs.Json;
import play.mvc.Result;
import repository.OrderPaymentRepository;
import repository.OrderRepository;
import service.InvoiceMailService;
import service.PaymentService;

import javax.swing.text.html.Option;
import java.util.Optional;

public class PaymentController extends BaseController {

    private static BaseResponse response = new BaseResponse();

    public static Result checkStatusPayment(String orderNumber) {
        int authority = checkAccessAuthorization("all");
        if (authority == 200 || authority == 203) {
            Optional<Order> order = OrderRepository.findByOrderNumber(orderNumber);
            if (!order.isPresent()) {
                response.setBaseResponse(0, 0, 0, inputParameter, null);
                return badRequest(Json.toJson(response));
            }
            Optional<OrderPayment> orderPayment = OrderPaymentRepository.findByOrderId(order.get().id);
            if (!orderPayment.isPresent()) {
                response.setBaseResponse(0, 0, 0, inputParameter, null);
                return badRequest(Json.toJson(response));
            }
            OrderPayment getOrderPayment = orderPayment.get();
            OrderPaymentResponse orderPaymentResponse = OrderPaymentResponse.builder()
                    .orderNumber(getOrderPayment.getOrder().getOrderNumber())
                    .invoiceNo(getOrderPayment.getInvoiceNo())
                    .status(getOrderPayment.getStatus())
                    .paymentChannel(getOrderPayment.getPaymentChannel())
                    .totalAmount(getOrderPayment.getTotalAmount())
                    .paymentDate(getOrderPayment.getPaymentDate())
                    .build();
            response.setBaseResponse(1, offset, 1, success, orderPaymentResponse);
            return ok(Json.toJson(response));
        } else if (authority == 403) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        } else {
            response.setBaseResponse(0, 0, 0, unauthorized, null);
            return unauthorized(Json.toJson(response));
        }
    }

    public static Result viewQrCode(String orderNumber) {
        int authority = checkAccessAuthorization("all");
        if (authority == 200 || authority == 203) {
            Optional<Order> order = OrderRepository.findByOrderNumber(orderNumber);
            if (!order.isPresent()) {
                response.setBaseResponse(0, 0, 0, inputParameter, null);
                return badRequest(Json.toJson(response));
            }
            Optional<OrderPayment> orderPayment = OrderPaymentRepository.findByOrderId(order.get().id);
            if (!orderPayment.isPresent()) {
                response.setBaseResponse(0, 0, 0, inputParameter, null);
                return badRequest(Json.toJson(response));
            }
            OrderPayment getOrderPayment = orderPayment.get();
            if (!getOrderPayment.getPaymentChannel().equalsIgnoreCase("qr_code")) {
                response.setBaseResponse(0, 0, 0, inputParameter, null);
                return badRequest(Json.toJson(response));
            }
            PaymentDetail paymentDetail = PaymentDetail.find.where().eq("orderPayment", getOrderPayment).findUnique();

            ShowQrCodeResponse showQrCodeResponse = new ShowQrCodeResponse();
            showQrCodeResponse.setQrCode(paymentDetail.getQrCode());
            showQrCodeResponse.setInvoiceNo(order.get().getOrderNumber());
            showQrCodeResponse.setStatus(order.get().getStatus());
            showQrCodeResponse.setOrderNumber(orderNumber);
            response.setBaseResponse(1, offset, 1, success, showQrCodeResponse);
            return ok(Json.toJson(response));
        } else if (authority == 403) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        } else {
            response.setBaseResponse(0, 0, 0, unauthorized, null);
            return unauthorized(Json.toJson(response));
        }
    }

    public static Result checkAvailableBank() {
        int authority = checkAccessAuthorization("all");
        if (authority == 200 || authority == 203) {
            ServiceResponse serviceResponse = PaymentService.getInstance().checkAvailableBank();
            if (serviceResponse.getCode() == 408) {
                ObjectNode result = Json.newObject();
                result.put("error_messages", Json.toJson(new String[]{"Request timeout, please try again later"}));
                response.setBaseResponse(1, offset, 1, timeOut, result);
                return badRequest(Json.toJson(response));
            } else if (serviceResponse.getCode() == 400) {
                response.setBaseResponse(1, offset, 1, inputParameter, serviceResponse.getData());
                return badRequest(Json.toJson(response));
            } else {
                return ok(Json.toJson(serviceResponse.getData()));
            }
        } else if (authority == 403) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        } else {
            response.setBaseResponse(0, 0, 0, unauthorized, null);
            return unauthorized(Json.toJson(response));
        }
    }

    public static Result checkPaymentMethod() {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if(ownMerchant != null) {
            if(ownMerchant.isPos){
                PaymentListResponsePOS paylistPOS = new PaymentListResponsePOS();
                paylistPOS.setMerchantId(ownMerchant.id);
                // paylistPOS.setIsCash(ownMerchant.isCash);
                // paylistPOS.setTypeCash(ownMerchant.isCash ? ownMerchant.typeCash : null);
                // paylistPOS.setIsDebitCredit(ownMerchant.isDebitCredit);
                // paylistPOS.setTypeDebitCredit(ownMerchant.isDebitCredit ? ownMerchant.typeDebitCredit : null);
                // paylistPOS.setIsQris(ownMerchant.isQris);
                // paylistPOS.setTypeQris(ownMerchant.isQris ? ownMerchant.typeQris : null);
                response.setBaseResponse(0, 0, 0, "Sukses menampilkan type pembayaran", paylistPOS);
                return ok(Json.toJson(response));
            }
            response.setBaseResponse(0, 0, 0, "Anda tidak mengaktifkan fitur POS", null);
            return unauthorized(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result sendMailInvoice() {
        Boolean checkService = checkInternalServiceKey();
        if (checkService == Boolean.FALSE) {
            response.setBaseResponse(0, 0, 0, unauthorized, null);
            return unauthorized(Json.toJson(response));
        } else {
            JsonNode json = request().body().asJson();
            String orderNumber = json.get("order_number").asText();
            if (orderNumber == null) {
                response.setBaseResponse(0, 0, 0, "order number tidak boleh kosong", null);
                return badRequest(Json.toJson(response));
            }

            Optional<Order> order = OrderRepository.findByOrderNumber(orderNumber);
            if (!order.isPresent()) {
                response.setBaseResponse(0, 0, 0, "order number tidak ditemukan", null);
                return badRequest(Json.toJson(response));
            }

            InvoiceMailService.handleCallbackAndSendEmail(order.get());

            response.setBaseResponse(1, 0, 0, success, "SENT");
            return ok(Json.toJson(response));
        }

    }

}
