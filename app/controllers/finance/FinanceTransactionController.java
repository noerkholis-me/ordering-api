package controllers.finance;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlRow;
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
import java.util.List;

public class FinanceTransactionController extends BaseController {

    private final static Logger.ALogger LOGGER = Logger.of(FinanceTransactionController.class);

    private static BaseResponse response = new BaseResponse();

    public static Result getAllTransaction(Long storeId, String startDate, String endDate, String sort,
                                           int offset, int limit, String status, String statusOrder) {
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

                List<SqlRow> financeTransactions = FinanceTransactionRepository.getAllTransactionsV2(ownMerchant.id, storeId, startDate, endDate, status, statusOrder, sort, offset, limit);
                int totalData = FinanceTransactionRepository.getAllTransactionsV2(ownMerchant.id, storeId, startDate, endDate, status, statusOrder, sort, 0, 0).size();
                List<FinanceTransactionResponse> financeTransactionResponses = new ArrayList<>();
                for (SqlRow transaction : financeTransactions) {
                    FinanceTransactionResponse trxRes = new FinanceTransactionResponse();
                    String trxNumber = transaction.getString("reference_number");
                    FinanceTransaction trx = FinanceTransactionRepository.find.where().raw("reference_number = '" + trxNumber + "' AND is_deleted = false LIMIT 1").findUnique();
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
                                             int offset, int limit, String status, String statusOrder) {
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

                List<SqlRow> financeTransactions = FinanceTransactionRepository.getAllTransactionsV2(ownMerchant.id, storeId, startDate, endDate, status, statusOrder, sort, 0, 0);
                List<FinanceTransaction> financeTransactionResponses = new ArrayList<>();
                for (SqlRow transaction : financeTransactions) {
                    FinanceTransaction trxRes = new FinanceTransaction();
                    String trxNumber = transaction.getString("reference_number");
                    FinanceTransaction trx = FinanceTransactionRepository.find.where().raw("reference_number = '" + trxNumber + "' AND is_deleted = false LIMIT 1").findUnique();
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

    public static Result getActiveBalance(Long storeId, String startDate, String endDate, String status, String statusOrder) {
        Merchant merchant = checkMerchantAccessAuthorization();
        if (merchant != null) {
            try {
                Transaction trx = Ebean.beginTransaction();
                try {
                    BigDecimal totalActiveBalance = BigDecimal.ZERO;
                    BigDecimal activeBalanceStore = BigDecimal.ZERO;
                    BigDecimal filteredActiveBalance = BigDecimal.ZERO;

                    Store store = Store.findById(storeId);
                    if (store == null) {
                        activeBalanceStore = BigDecimal.ZERO;
                    } else {
                        activeBalanceStore = store.getActiveBalance();
                    }

                    // default query find by merchant id
                    List<FinanceTransaction> financeTransactions = FinanceTransactionRepository.findAllTransactionByMerchantIdAndOrderClosed(merchant.id);
                    if (store != null) {
                        financeTransactions = FinanceTransactionRepository.findAllTransactionByStoreIdAndOrderClosed(store.id);
                    }

                    String refNumber = "";
                    System.out.println("=== ACTIVE BALANCE ===");
                    for (FinanceTransaction transaction : financeTransactions) {
                        if (!transaction.getReferenceNumber().equals(refNumber)){
                            System.out.print("STATUS : " + transaction.getStatus());
                            refNumber = transaction.getReferenceNumber();
                            if (transaction.getStatus().equals("IN")){
                                totalActiveBalance = totalActiveBalance.add(transaction.getAmount());
                                System.out.println(" + Rp " + transaction.getAmount() + " , total = Rp " + totalActiveBalance);
                            } else if (transaction.getStatus().equals("OUT")) {
                                totalActiveBalance = totalActiveBalance.subtract(transaction.getAmount());
                                System.out.println(" - Rp " + transaction.getAmount() + " , total = Rp " + totalActiveBalance);
                            } else if (transaction.getStatus().equals("WITHDRAW")) {
                                totalActiveBalance = totalActiveBalance.subtract(transaction.getAmount());
                                System.out.println(" - Rp " + transaction.getAmount() + " , total = Rp " + totalActiveBalance);
                            }
                        }
                    }

                    String refNumberFilter = "";
                    List<FinanceTransaction> transactions = FinanceTransactionRepository.filteredActiveBalance(merchant.id, storeId, startDate, endDate, status, statusOrder);
                    System.out.println("\n=== FILTER ACTIVE BALANCE BY : status = '" + status + "' - order = '" + statusOrder + "'  ===");
                    for (FinanceTransaction transaction : transactions) {
                        if (!transaction.getReferenceNumber().equals(refNumberFilter)) {
                            refNumberFilter = transaction.getReferenceNumber();
                            if (status.equalsIgnoreCase("IN")) {
                                if (transaction.getStatus().equals("IN")) {
                                    filteredActiveBalance = filteredActiveBalance.add(transaction.getAmount());
                                    System.out.println(transaction.getReferenceNumber() + " : " + transaction.getStatus() + " + Rp " + transaction.getAmount() + " , total = Rp " + filteredActiveBalance);
                                }
                            } else if (status.equalsIgnoreCase("OUT")) {
                                if (transaction.getStatus().equals("OUT") || transaction.getStatus().equals("WITHDRAW")) {
                                    filteredActiveBalance = filteredActiveBalance.subtract(transaction.getAmount());
                                    System.out.println("STATUS : " + transaction.getStatus() + " - Rp " + transaction.getAmount() + " , total = Rp " + filteredActiveBalance);
                                }
                            } else  {
                                if (transaction.getStatus().equals("IN")){
                                    filteredActiveBalance = filteredActiveBalance.add(transaction.getAmount());
                                    System.out.println(" + Rp " + transaction.getAmount() + " , total = Rp " + filteredActiveBalance);
                                } else {
                                    filteredActiveBalance = filteredActiveBalance.subtract(transaction.getAmount());
                                    System.out.println(" - Rp " + transaction.getAmount() + " , total = Rp " + filteredActiveBalance);
                                }
                            }
                        }
                    }

                    ActiveBalanceResponse activeBalanceResponse = new ActiveBalanceResponse();
                    activeBalanceResponse.setActiveBalance(totalActiveBalance);
                    activeBalanceResponse.setTotalActiveBalance(totalActiveBalance);
                    activeBalanceResponse.setFilteredActiveBalance(filteredActiveBalance);

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
