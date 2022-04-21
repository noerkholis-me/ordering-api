package controllers.payment;

import com.hokeba.api.BaseResponse;
import controllers.BaseController;
import dtos.payment.OrderPaymentResponse;
import models.Member;
import models.transaction.Order;
import models.transaction.OrderPayment;
import play.libs.Json;
import play.mvc.Result;
import repository.OrderPaymentRepository;
import repository.OrderRepository;

import java.util.Optional;

public class PaymentController extends BaseController {

    private static BaseResponse response = new BaseResponse();

    public static Result checkStatusPayment(String orderNumber) {
        Member member = checkMemberAccessAuthorization();
        if (member != null) {
            Optional<Order> order = OrderRepository.findByOrderNumber(orderNumber);
            if (!order.isPresent()) {
                response.setBaseResponse(0, 0, 0, inputParameter, null);
                return badRequest(Json.toJson(response));
            }
            Optional<OrderPayment> orderPayment = OrderPaymentRepository.findByOrderId(order.get());
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
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

}
