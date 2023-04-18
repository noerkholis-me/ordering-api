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
import models.transaction.Order;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import repository.OrderRepository;
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
                if (startDate.compareTo(endDate) > 0) {
                        response.setBaseResponse(0, 0, 0, "tanggal awal tidak boleh melebihi tanggal akhir", null);
                        return badRequest(Json.toJson(response));
                }
                if (storeId != null && storeId != 0L) {
                    Store store = Store.findById(storeId);
                    if (store == null) {
                        response.setBaseResponse(0, 0, 0, "store id does not exists", null);
                        return badRequest(Json.toJson(response));
                    }
                }

                List<FinanceTransaction> financeTransactions = FinanceTransactionRepository.getAllTransactions(ownMerchant.id, storeId, startDate, endDate, status, sort, offset, limit);
                Integer totalData = FinanceTransactionRepository.getAllTransactions(ownMerchant.id, storeId, startDate, endDate, status, sort, 0, 0).size();
                List<FinanceTransactionResponse> financeTransactionResponses = new ArrayList<>();
                for (FinanceTransaction transaction : financeTransactions) {
                    FinanceTransactionResponse trxRes = new FinanceTransactionResponse();
                    Order order = OrderRepository.find.where().eq("id", transaction.id).eq("isDeleted", false).findUnique();
                    FinanceTransaction trx = FinanceTransactionRepository.find.where().raw("reference_number = '" + order.getOrderNumber() + "' AND is_deleted = false ORDER BY date LIMIT 1").findUnique();
                    trxRes.setReferenceNumber(trx.getReferenceNumber());
                    trxRes.setDate(trx.getDate());
                    trxRes.setTransactionType(trx.getTransactionType());
                    trxRes.setStatus(trx.getStatus());
                    trxRes.setAmount(trx.getAmount());
                    financeTransactionResponses.add(trxRes);
                }

                response.setBaseResponse(totalData, offset, limit, success + " Showing data transaction", financeTransactionResponses);
                return ok(Json.toJson(response));
            } catch (Exception e) {
                LOGGER.error("Error when getting transaction data");
                e.printStackTrace();
            }
        } else {
            response.setBaseResponse(0, 0, 0, unauthorized, null);
            return unauthorized(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, "Error saat menampilkan data", null);
        return badRequest(Json.toJson(response));

    }

    public static Result downloadTransaction(Long storeId, String startDate, String endDate, String sort,
                                             int offset, int limit, String status) {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            try {                
                if (startDate.compareTo(endDate) > 0) {
                        response.setBaseResponse(0, 0, 0, "tanggal awal tidak boleh melebihi tanggal akhir", null);
                        return badRequest(Json.toJson(response));
                }
                if (storeId != null && storeId != 0L) {
                    Store store = Store.findById(storeId);
                    if (store == null) {
                        response.setBaseResponse(0, 0, 0, "store id does not exists", null);
                        return badRequest(Json.toJson(response));
                    }
                }

                List<FinanceTransaction> financeTransactions = FinanceTransactionRepository.getAllTransactions(ownMerchant.id, storeId, startDate, endDate, status, sort, 0, 0);
                List<FinanceTransaction> financeTransactionResponses = new ArrayList<>();
                for (FinanceTransaction transaction : financeTransactions) {
                    FinanceTransaction trxRes = new FinanceTransaction();
                    Order order = OrderRepository.find.where().eq("id", transaction.id).eq("isDeleted", false).findUnique();
                    FinanceTransaction trx = FinanceTransactionRepository.find.where().raw("reference_number = '" + order.getOrderNumber() + "' AND is_deleted = false ORDER BY date LIMIT 1").findUnique();
                    trxRes.setEventId(trx.getEventId());
                    trxRes.setReferenceNumber(trx.getReferenceNumber());
                    trxRes.setDate(trx.getDate());
                    trxRes.setTransactionType(trx.getTransactionType());
                    trxRes.setStatus(trx.getStatus());
                    trxRes.setAmount(trx.getAmount());
                    financeTransactionResponses.add(trxRes);
                }

                File file = DownloadTransactionService.downloadTransaction(financeTransactionResponses, FinanceTransaction.TRANSACTION, new ArrayList<>());
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

                    Query<FinanceTransaction> query = null;
                    // default query find by merchant id
                    query = FinanceTransactionRepository.findAllTransactionByMerchantId(merchant.id);
                    if (store != null) {
                        query = FinanceTransactionRepository.findAllTransactionByStoreId(store.id);
                    }
                    List<FinanceTransaction> financeTransactions = FinanceTransactionRepository.findAllTransaction(query, "", "", "", 0, 0, "");
                    List<FinanceTransactionResponse> financeTransactionResponses = new ArrayList<>();
                    String refNumber = "";
                    for (FinanceTransaction transaction : financeTransactions) {
                        FinanceTransactionResponse trxRes = new FinanceTransactionResponse();
                        if (!transaction.getReferenceNumber().equals(refNumber)){
                            System.out.println("1");
                            System.out.println(transaction.getStatus());
                            refNumber = transaction.getReferenceNumber();
                            if (transaction.getStatus().equals("IN")){
                                totalActiveBalance = totalActiveBalance.add(transaction.getAmount());
                                System.out.print("2");
                                System.out.println(totalActiveBalance);
                            } else if (transaction.getStatus().equals("OUT")) {
                                totalActiveBalance = totalActiveBalance.subtract(transaction.getAmount());
                                System.out.print("3");
                                System.out.println(totalActiveBalance);
                            } else if (transaction.getStatus().equals("WITHDRAW")) {
                                totalActiveBalance = totalActiveBalance.subtract(transaction.getAmount());
                                System.out.print("4");
                                System.out.println(totalActiveBalance);
                            }
                        }
                    }

                    // totalActiveBalance = merchant.totalActiveBalance;

                    ActiveBalanceResponse activeBalanceResponse = new ActiveBalanceResponse();
                    activeBalanceResponse.setActiveBalance(totalActiveBalance);
                    activeBalanceResponse.setTotalActiveBalance(totalActiveBalance);

                    trx.commit();

                    response.setBaseResponse(1, 0, 0, success + " Showing data active balance", activeBalanceResponse);
                    return ok(Json.toJson(response));
                } catch (Exception e) {
                    LOGGER.error("Error pada saat get active balance", e);
                    e.printStackTrace();
                    trx.rollback();
                    response.setBaseResponse(0, 0, 0, "error pada saat get active balance", null);
                    return badRequest(Json.toJson(response));
                } finally {
                    trx.end();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                LOGGER.error("error while get active balance ", ex);
                response.setBaseResponse(0, 0, 0, "Error pada saat get active balance", null);
                return badRequest(Json.toJson(response));
            }
        } else {
            response.setBaseResponse(0, 0, 0, unauthorized, null);
            return unauthorized(Json.toJson(response));
        }
        // response.setBaseResponse(0, 0, 0, "Error pada saat get active balance", null);
        // return badRequest(Json.toJson(response));
    }

}
