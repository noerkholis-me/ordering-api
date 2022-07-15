package controllers.dashboard;

import com.hokeba.api.BaseResponse;
import controllers.BaseController;
import models.UserMerchant;
import models.merchant.CashierHistoryMerchant;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import repository.cashierhistory.CashierHistoryMerchantRepository;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
            BigDecimal totalOpenCash = cashierHistoryMerchant.get().getStartTotalAmount();

            Map<String, Integer> responses = new HashMap<>();
            responses.put("total_amount_open_cash", totalOpenCash.intValue());

            response.setBaseResponse(1, 0, 0, success, responses);
            return unauthorized(Json.toJson(response));
        }
    }

    public static Result getOrderInformation() {
        UserMerchant userMerchant = checkUserMerchantAccessAuthorization();
        if (userMerchant == null) {
            response.setBaseResponse(0, 0, 0, unauthorized, null);
            return unauthorized(Json.toJson(response));
        } else {
            Map<String, Integer> responses = new HashMap<>();
            responses.put("total_order", 10);
            responses.put("total_order_waiting_payment", 10);
            responses.put("total_order_paid", 10);
            responses.put("total_order_cancelled", 10);

            response.setBaseResponse(1, 0, 0, success, responses);
            return ok(Json.toJson(response));
        }
    }


}
