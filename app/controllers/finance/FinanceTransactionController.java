package controllers.finance;

import com.hokeba.api.BaseResponse;
import controllers.BaseController;
import dtos.finance.FinanceTransactionResponse;
import models.Merchant;
import models.finance.FinanceTransaction;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import repository.finance.FinanceTransactionRepository;
import service.DownloadTransactionService;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class FinanceTransactionController extends BaseController {

    private final static Logger.ALogger LOGGER = Logger.of(FinanceTransactionController.class);

    private static BaseResponse response = new BaseResponse();

    public static Result getAllTransaction(Long storeId, String startDate, String endDate, String sort,
                                           int offset, int limit, String status) {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            try {
                List<FinanceTransaction> financeTransactions = FinanceTransactionRepository.findAllTransaction(startDate, endDate, storeId, sort, offset, limit, status);
                Integer totalData = FinanceTransactionRepository.getTotalPage(storeId).size();
                FinanceTransactionResponse financeTransactionResponse = new FinanceTransactionResponse();
                List<FinanceTransactionResponse.TransactionResponse> transactionResponses = new ArrayList<>();
                BigDecimal activeBalance = BigDecimal.ZERO;
                for (FinanceTransaction transaction : financeTransactions) {
                    FinanceTransactionResponse.TransactionResponse trxRes = new FinanceTransactionResponse.TransactionResponse();
                    trxRes.setReferenceNumber(transaction.getReferenceNumber());
                    trxRes.setDate(transaction.getDate());
                    trxRes.setTransactionType(transaction.getTransactionType());
                    trxRes.setStatus(transaction.getStatus());
                    trxRes.setAmount(transaction.getAmount());
                    transactionResponses.add(trxRes);
                    activeBalance = activeBalance.add(transaction.getAmount());
                }
                financeTransactionResponse.setActiveBalance(activeBalance);
                financeTransactionResponse.setTransactionResponses(transactionResponses);
                response.setBaseResponse(totalData, offset, limit, success + " Showing data transaction", financeTransactionResponse);
                return ok(Json.toJson(response));
            } catch (Exception e) {
                LOGGER.error("Error when getting transaction data");
                e.printStackTrace();
            }
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));

    }

    public static Result downloadTransaction(Long storeId, String startDate, String endDate, String sort,
                                             int offset, int limit, String status) {
        Merchant merchant = checkMerchantAccessAuthorization();
        if (merchant != null) {
            try {
                List<FinanceTransaction> financeTransactions = FinanceTransactionRepository.findAllTransaction(startDate, endDate, storeId, sort, offset, limit, status);
                File file = DownloadTransactionService.downloadTransaction(financeTransactions);
                response().setContentType("application/vnd.ms-excel");
                response().setHeader("Content-disposition", "attachment; filename=transaction.xlsx");
                return ok(file);
            } catch (Exception ex) {
                ex.printStackTrace();
                LOGGER.error("Error while download transaction ", ex);
            }
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

}
