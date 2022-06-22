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
import repository.finance.FinanceTransactionRepository;

import java.text.SimpleDateFormat;
import java.util.*;

public class DashboardController extends BaseController {

    private final static Logger.ALogger LOGGER = Logger.of(DashboardController.class);

    private static BaseResponse response = new BaseResponse();

    public static Result getTotalTransactionAndMember(String startDate, String endDate) {
        Merchant merchant = checkMerchantAccessAuthorization();
        if (merchant != null) {
            try {
                String start = null;
                String end = null;
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                List<Member> members = new ArrayList<>();
                List<FinanceTransaction> financeTransactions = new ArrayList<>();
                Map<List<Member>, List<FinanceTransaction>> mapData = new HashMap<>();
                if (startDate.equalsIgnoreCase("") && endDate.equalsIgnoreCase("")) {
                    // do find seven days ago
                    long DAY_IN_MS = 1000 * 60 * 60 * 24;
                    start = new Date(System.currentTimeMillis() - (7 * DAY_IN_MS)).toString();
                    end = new Date().toString();
                    members = Member.findAllMemberByCreatedAt(start, end);
                    financeTransactions = FinanceTransactionRepository.findAllTransactionByDate(start, end);

                    mapData.put()

                } else {

                }

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
