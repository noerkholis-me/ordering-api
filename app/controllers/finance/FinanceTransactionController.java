package controllers.finance;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Query;
import com.avaje.ebean.Transaction;
import com.hokeba.api.BaseResponse;
import controllers.BaseController;
import dtos.finance.ActiveBalanceResponse;
import dtos.finance.FinanceTransactionResponse;
import models.Merchant;
import models.Store;
import models.finance.FinanceTransaction;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import repository.finance.FinanceTransactionRepository;
import service.DownloadTransactionService;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FinanceTransactionController extends BaseController {

    private final static Logger.ALogger LOGGER = Logger.of(FinanceTransactionController.class);

    private static BaseResponse response = new BaseResponse();

    public static Result getAllTransaction(Long storeId, String startDate, String endDate, String sort,
                                           int offset, int limit, String status) {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            try {
                Query<FinanceTransaction> query = null;
                // default query find by merchant id
                query = FinanceTransactionRepository.findAllTransactionByMerchantId(ownMerchant.id);
                if (storeId != null && storeId != 0L) {
                    Store store = Store.findById(storeId);
                    if (store == null) {
                        response.setBaseResponse(0, 0, 0, "store id does not exists", null);
                        return badRequest(Json.toJson(response));
                    }
                    query = FinanceTransactionRepository.findAllTransactionByStoreId(storeId);
                }
                List<FinanceTransaction> financeTransactions = FinanceTransactionRepository.findAllTransaction(query, startDate, endDate, sort, offset, limit, status);
                Integer totalData = FinanceTransactionRepository.getTotalPage(query);
                List<FinanceTransactionResponse> financeTransactionResponses = new ArrayList<>();
                for (FinanceTransaction transaction : financeTransactions) {
                    FinanceTransactionResponse trxRes = new FinanceTransactionResponse();
                    trxRes.setReferenceNumber(transaction.getReferenceNumber());
                    trxRes.setDate(transaction.getDate());
                    trxRes.setTransactionType(transaction.getTransactionType());
                    trxRes.setStatus(transaction.getStatus());
                    trxRes.setAmount(transaction.getAmount());
                    financeTransactionResponses.add(trxRes);
                }
                response.setBaseResponse(totalData, offset, limit, success + " Showing data transaction", financeTransactionResponses);
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
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            try {
                Query<FinanceTransaction> query = null;
                // default query find by merchant id
                query = FinanceTransactionRepository.findAllTransactionByMerchantId(ownMerchant.id);
                if (storeId != null && storeId != 0L) {
                    Store store = Store.findById(storeId);
                    if (store == null) {
                        response.setBaseResponse(0, 0, 0, "store id does not exists", null);
                        return badRequest(Json.toJson(response));
                    }
                    query = FinanceTransactionRepository.findAllTransactionByStoreId(storeId);
                }
                List<FinanceTransaction> financeTransactions = FinanceTransactionRepository.findAllTransaction(query, startDate, endDate, sort, offset, limit, status);
                File file = DownloadTransactionService.downloadTransaction(financeTransactions, FinanceTransaction.TRANSACTION, new ArrayList<>());
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

    public static Result getActiveBalance(Long storeId) {
        Merchant merchant = checkMerchantAccessAuthorization();
        if (merchant != null) {
            try {
                Transaction trx = Ebean.beginTransaction();
                try {
                    BigDecimal totalActiveBalance = BigDecimal.ZERO;
                    BigDecimal activeBalanceStore = BigDecimal.ZERO;
                    Store store = Store.findById(storeId);
                    if (store == null) {
                        activeBalanceStore = BigDecimal.ZERO;
                    } else {
                        activeBalanceStore = store.getActiveBalance();
                    }
                    totalActiveBalance = merchant.totalActiveBalance;

                    ActiveBalanceResponse activeBalanceResponse = new ActiveBalanceResponse();
                    activeBalanceResponse.setActiveBalance(activeBalanceStore);
                    activeBalanceResponse.setTotalActiveBalance(totalActiveBalance);

                    trx.commit();

                    response.setBaseResponse(1, 0, 0, success + " Showing data active balance", activeBalanceResponse);
                    return ok(Json.toJson(response));
                } catch (Exception e) {
                    LOGGER.error("Error pada saat assign produk", e);
                    e.printStackTrace();
                    trx.rollback();
                } finally {
                    trx.end();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                LOGGER.error("error while get active balance ", ex);
            }
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

}
