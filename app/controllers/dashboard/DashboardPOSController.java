package controllers.dashboard;

import com.avaje.ebean.Query;
import com.hokeba.api.BaseResponse;
import controllers.BaseController;
import models.UserMerchant;
import models.merchant.CashierHistoryMerchant;
import models.transaction.Order;
import models.transaction.OrderPayment;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import repository.OrderPaymentRepository;
import repository.OrderRepository;
import repository.cashierhistory.CashierHistoryMerchantRepository;

import java.math.BigDecimal;
import java.util.*;

public class DashboardPOSController extends BaseController {

    private final static Logger.ALogger LOGGER = Logger.of(DashboardPOSController.class);

    private static BaseResponse response = new BaseResponse();


    public static Result getTotalCashPos(Long storeId) {
        UserMerchant userMerchant = checkUserMerchantAccessAuthorization();
        if (userMerchant == null) {
            response.setBaseResponse(0, 0, 0, unauthorized, null);
            return unauthorized(Json.toJson(response));
        } else {
            Optional<CashierHistoryMerchant> cashierHistoryMerchant = CashierHistoryMerchantRepository.findByUserActiveCashier(userMerchant.id, storeId);
            if (!cashierHistoryMerchant.isPresent()) {
                response.setBaseResponse(0, 0, 0, inputParameter, null);
                return badRequest(Json.toJson(response));
            }
            BigDecimal totalOpenCash = BigDecimal.ZERO;
            Query<Order> orderQuery = OrderRepository.findAllOrderByStoreId(storeId);
            List<Order> orders = OrderRepository.findOrders(orderQuery, 0, 0);
            for (Order order : orders) {
                Optional<OrderPayment> orderPayment = OrderPaymentRepository.findByOrderIdAndStatusAndPaymentChannel(order.id, "PAID");
                if (orderPayment.isPresent()) {
                    totalOpenCash = totalOpenCash.add(cashierHistoryMerchant.get().getStartTotalAmount()).add(orderPayment.get().getTotalAmount());
                }
                continue;
            }

            Map<String, Integer> responses = new HashMap<>();
            responses.put("total_amount_open_cash", totalOpenCash.intValue());

            response.setBaseResponse(1, 0, 0, success, responses);
            return unauthorized(Json.toJson(response));
        }
    }

    public static Result getOrderInformation(Long storeId) {
        UserMerchant userMerchant = checkUserMerchantAccessAuthorization();
        if (userMerchant == null) {
            response.setBaseResponse(0, 0, 0, unauthorized, null);
            return unauthorized(Json.toJson(response));
        } else {
            Map<String, Integer> responses = new HashMap<>();

            Query<Order> orderQuery = OrderRepository.findAllOrderByStoreId(storeId);

            Integer totalOrder = OrderRepository.getTotalOrder(orderQuery, "");
            responses.put("total_order", totalOrder);


            List<Order> orders = OrderRepository.findOrders(orderQuery, 0, 0);
            Integer totalOrderWaitingPayment = 0;
            for (Order order : orders) {
                Optional<OrderPayment> orderPayment = OrderPaymentRepository.findByOrderIdAndStatus(order.id, "PENDING");
                if (orderPayment.isPresent()) {
                    totalOrderWaitingPayment = totalOrderWaitingPayment + 1;
                }
            }
            responses.put("total_order_waiting_payment", totalOrderWaitingPayment);


            Integer totalOrderPaid = 0;
            for (Order order : orders) {
                Optional<OrderPayment> orderPayment = OrderPaymentRepository.findByOrderIdAndStatus(order.id, "PAID");
                if (orderPayment.isPresent()) {
                    totalOrderPaid = totalOrderPaid + 1;
                }
            }
            responses.put("total_order_paid", totalOrderPaid);

            Integer totalOrderCancelled = OrderRepository.getTotalOrder(orderQuery, "CANCELLED");
            responses.put("total_order_cancelled", totalOrderCancelled);

            response.setBaseResponse(1, 0, 0, success, responses);
            return ok(Json.toJson(response));
        }
    }


}
