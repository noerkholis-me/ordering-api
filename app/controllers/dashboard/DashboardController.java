package controllers.dashboard;

import com.avaje.ebean.Query;
import com.hokeba.api.BaseResponse;
import controllers.BaseController;
import controllers.finance.FinanceWithdrawController;
import dtos.ChartCustomerTransactionResponse;
import models.Member;
import models.Merchant;
import models.finance.FinanceTransaction;
import models.finance.FinanceWithdraw;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import repository.MemberRepository;
import repository.ProductMerchantRepository;
import repository.finance.FinanceTransactionRepository;
import repository.finance.FinanceWithdrawRepository;
import scala.Int;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import static java.time.temporal.TemporalAdjusters.*;

public class DashboardController extends BaseController {

    private final static Logger.ALogger LOGGER = Logger.of(DashboardController.class);

    private static BaseResponse response = new BaseResponse();

    public static Result getTotalTransactionAndMember(String startDate, String endDate) {
        Merchant merchant = checkMerchantAccessAuthorization();
        if (merchant != null) {
            try {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM");
                Date start = formatter.parse(startDate);
                Date end = formatter.parse(endDate);

                LocalDate startLocalDate = start.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                LocalDate endLocalDate = end.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

                List<ChartCustomerTransactionResponse> chartResponse = new ArrayList<>();
                for (LocalDate date = startLocalDate; !date.isAfter(endLocalDate); date = date.plusMonths(1)) {
                    ChartCustomerTransactionResponse chartCustomerTransactionResponse = new ChartCustomerTransactionResponse();

                    Integer totalTransaction = FinanceTransactionRepository.getTotalTransaction(merchant.id, date.with(firstDayOfMonth()).toString(), date.with(lastDayOfMonth()).toString());
                    Integer totalMember = MemberRepository.getTotalMember(merchant, date.with(firstDayOfMonth()).toString(), date.with(lastDayOfMonth()).toString());

                    chartCustomerTransactionResponse.setTotalTransaction(totalTransaction);
                    chartCustomerTransactionResponse.setTotalMember(totalMember);

                    Date dateParse = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
                    chartCustomerTransactionResponse.setDate(dateParse);

                    chartResponse.add(chartCustomerTransactionResponse);
		        }
                response.setBaseResponse(chartResponse.size(), 0, 0, success, chartResponse);
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

    public static Result getTotalCustomer(String startDate, String endDate) {
        Merchant merchant = checkMerchantAccessAuthorization();
        if (merchant != null) {
            try {
                Integer totalCustomer = MemberRepository.getTotalMember(merchant, startDate, endDate);
                Map<String, Integer> data = new HashMap<>();
                data.put("total_customer", totalCustomer);
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
                data.put("total_transaction_amount", totalActiveBalance != null ? totalActiveBalance.intValue() : 0);
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
                BigDecimal totalAmountWithdraw = BigDecimal.ZERO;
                Query<FinanceWithdraw> financeWithdrawQuery = FinanceWithdrawRepository.finaAllWithdrawByMerchantId(merchant.id);
                List<FinanceWithdraw> financeWithdraws = FinanceWithdrawRepository.findAllWithdrawList(financeWithdrawQuery, startDate, endDate);
                for (FinanceWithdraw financeWithdraw : financeWithdraws) {
                    totalAmountWithdraw = totalAmountWithdraw.add(financeWithdraw.getAmount());
                }
                Map<String, Integer> data = new HashMap<>();
                data.put("total_withdraw", totalAmountWithdraw.intValue());
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
