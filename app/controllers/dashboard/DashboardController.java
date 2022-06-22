package controllers.dashboard;

import com.hokeba.api.BaseResponse;
import controllers.BaseController;
import controllers.finance.FinanceWithdrawController;
import models.Member;
import models.Merchant;
import models.finance.FinanceTransaction;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import repository.ProductMerchantRepository;
import repository.finance.FinanceTransactionRepository;
import repository.finance.FinanceWithdrawRepository;
import scala.Int;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

public class DashboardController extends BaseController {

    private final static Logger.ALogger LOGGER = Logger.of(DashboardController.class);

    private static BaseResponse response = new BaseResponse();

    public static Result getTotalTransactionAndMember(String startDate, String endDate) {
        Merchant merchant = checkMerchantAccessAuthorization();
        if (merchant != null) {
            try {
                return ok();

            } catch (Exception ex) {
                 ex.printStackTrace();
                response.setBaseResponse(0, 0, 0, error, null);
                return unauthorized(Json.toJson(response));
            }
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result getTotalCustomer(String startDate, String endDate) {
        Merchant merchant = checkMerchantAccessAuthorization();
        if (merchant != null) {
            try {
                Map<String, Integer> data = new HashMap<>();
                data.put("total_customer", 0);
                response.setBaseResponse(1, offset, limit, success + " Showing total member ", data);
                return ok(Json.toJson(response));
            } catch (Exception ex) {
                ex.printStackTrace();
                response.setBaseResponse(0, 0, 0, error, null);
                return unauthorized(Json.toJson(response));
            }
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result getTotalProduct(String startDate, String endDate) {
        Merchant merchant = checkMerchantAccessAuthorization();
        if (merchant != null) {
            try {
                Integer totalProduct = ProductMerchantRepository.getTotalProduct(merchant, startDate, endDate);
                Map<String, Integer> data = new HashMap<>();
                data.put("total_product", totalProduct);
                response.setBaseResponse(1, offset, limit, success + " Showing total product ", data);
                return ok(Json.toJson(response));
            } catch (Exception ex) {
                ex.printStackTrace();
                response.setBaseResponse(0, 0, 0, error, null);
                return unauthorized(Json.toJson(response));
            }
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result getTotalTransactionAmount() {
        Merchant merchant = checkMerchantAccessAuthorization();
        if (merchant != null) {
            try {
                BigDecimal totalActiveBalance = merchant.totalActiveBalance;
                Map<String, Integer> data = new HashMap<>();
                data.put("total_transaction_amount", totalActiveBalance.intValue());
                response.setBaseResponse(1, offset, limit, success + " Showing total transaction amount", data);
                return ok(Json.toJson(response));
            } catch (Exception ex) {
                ex.printStackTrace();
                response.setBaseResponse(0, 0, 0, error, null);
                return unauthorized(Json.toJson(response));
            }
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result getTotalTransaction(String startDate, String endDate) {
        Merchant merchant = checkMerchantAccessAuthorization();
        if (merchant != null) {
            try {
                Integer totalTransaction = FinanceTransactionRepository.getTotalTransaction(merchant.id, startDate, endDate);
                Map<String, Integer> data = new HashMap<>();
                data.put("total_transaction", totalTransaction);
                response.setBaseResponse(1, offset, limit, success + " Showing total transaction", data);
                return ok(Json.toJson(response));
            } catch (Exception ex) {
                ex.printStackTrace();
                response.setBaseResponse(0, 0, 0, error, null);
                return unauthorized(Json.toJson(response));
            }
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result getTotalWithdraw(String startDate, String endDate) {
        Merchant merchant = checkMerchantAccessAuthorization();
        if (merchant != null) {
            try {
                Integer totalTransaction = FinanceWithdrawRepository.getTotalWithdraw(merchant.id, startDate, endDate);
                Map<String, Integer> data = new HashMap<>();
                data.put("total_withdraw", totalTransaction);
                response.setBaseResponse(1, offset, limit, success + " Showing total transaction", data);
                return ok(Json.toJson(response));
            } catch (Exception ex) {
                ex.printStackTrace();
                response.setBaseResponse(0, 0, 0, error, null);
                return unauthorized(Json.toJson(response));
            }
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

}
