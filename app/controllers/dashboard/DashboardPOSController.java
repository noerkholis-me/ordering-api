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


    /**
     * order vs opening cashier
     * @param storeId
     * @return
     */
    public static Result getTotalCashPos(Long storeId) {
        UserMerchant userMerchant = checkUserMerchantAccessAuthorization();
        if (userMerchant == null) {
            response.setBaseResponse(0, 0, 0, unauthorized, null);
            return unauthorized(Json.toJson(response));
        } else {
            Map<String, Integer> responses = new HashMap<>();
            Optional<CashierHistoryMerchant> cashierHistoryMerchant = CashierHistoryMerchantRepository.findByUserActiveCashierAndStoreIdOpen(userMerchant.id, storeId);
            if (!cashierHistoryMerchant.isPresent()) {
                responses.put("total_amount_open_cash", 0);
                response.setBaseResponse(0, 0, 0, inputParameter + " cashier history tidak ditemukan", responses);
                return ok(Json.toJson(response));
            }

            BigDecimal totalOpenCash = BigDecimal.ZERO;
            Query<Order> orderQuery = OrderRepository.findAllOrderByUserMerchantIdAndStoreId(userMerchant.id, storeId);
            List<Order> orders = OrderRepository.findOrdersByRangeToday(orderQuery, cashierHistoryMerchant.get().getStartTime(), new Date());
            BigDecimal totalAmountFromOrder = BigDecimal.ZERO;
            for (Order order : orders) {
                Optional<OrderPayment> orderPayment = OrderPaymentRepository.findByOrderIdAndStatusAndPaymentChannel(order.id, OrderPayment.PAID);
                if (orderPayment.isPresent()) {
                    totalAmountFromOrder = totalAmountFromOrder.add(orderPayment.get().getTotalAmount());
                }
                continue;
            }

            totalOpenCash = totalOpenCash.add(cashierHistoryMerchant.get().getStartTotalAmount()).add(totalAmountFromOrder);

            responses.put("total_amount_open_cash", totalOpenCash.intValue());

            response.setBaseResponse(1, 0, 0, success, responses);
            return ok(Json.toJson(response));
        }
    }

    public static Result getOrderInformation(Long storeId) {
        UserMerchant userMerchant = checkUserMerchantAccessAuthorization();
        if (userMerchant == null) {
            response.setBaseResponse(0, 0, 0, unauthorized, null);
            return unauthorized(Json.toJson(response));
        } else {

            Optional<CashierHistoryMerchant> cashierHistoryMerchant = CashierHistoryMerchantRepository.findByUserActiveCashierAndStoreIdOpen(userMerchant.id, storeId);
            if (!cashierHistoryMerchant.isPresent()) {
                response.setBaseResponse(0, 0, 0, inputParameter + " cashier history tidak ditemukan", null);
                return ok(Json.toJson(response));
            }

            Map<String, Integer> responses = new HashMap<>();

            Query<Order> orderQuery = OrderRepository.findAllOrderByUserMerchantIdAndStoreId(userMerchant.id, storeId);

            Integer totalOrder = OrderRepository.getTotalOrder(orderQuery, "", cashierHistoryMerchant.get().getStartTime(), new Date());
            responses.put("total_order", totalOrder);

            List<Order> orders = OrderRepository.findOrdersByRangeToday(orderQuery, cashierHistoryMerchant.get().getStartTime(), new Date());
            Integer totalOrderWaitingPayment = 0;
            Integer totalOrderPaid = 0;

            for (Order order : orders) {
                if ("PENDING".equals(order.getOrderPayment().getStatus())) {
                    totalOrderWaitingPayment = totalOrderWaitingPayment + 1;
                }else if ("PAID".equals(order.getOrderPayment().getStatus())) {
                    totalOrderPaid = totalOrderPaid + 1;
                }
            }

            responses.put("total_order_waiting_payment", totalOrderWaitingPayment);
            responses.put("total_order_paid", totalOrderPaid);

            Integer totalOrderCancelled = OrderRepository.getTotalOrder(orderQuery, "CANCELLED", cashierHistoryMerchant.get().getStartTime(), new Date());
            responses.put("total_order_cancelled", totalOrderCancelled);

            response.setBaseResponse(1, 0, 0, success, responses);
            return ok(Json.toJson(response));
        }
    }


}
