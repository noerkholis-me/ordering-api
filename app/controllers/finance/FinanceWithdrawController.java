package controllers.finance;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Query;
import com.avaje.ebean.Transaction;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hokeba.api.BaseResponse;
import com.hokeba.util.MailConfig;

import controllers.BaseController;
import dtos.finance.FinanceWithdrawRequest;
import dtos.finance.FinanceWithdrawResponse;
import dtos.store.StoreWithdrawEmail;
import models.Merchant;
import models.Store;
import models.finance.FinanceTransaction;
import models.finance.FinanceWithdraw;
import models.merchant.BankAccountMerchant;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import repository.BankAccountMerchantRepository;
import repository.finance.FinanceTransactionRepository;
import repository.finance.FinanceWithdrawRepository;
import service.DownloadTransactionService;
import service.EmailService;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

public class FinanceWithdrawController extends BaseController {

    private final static Logger.ALogger LOGGER = Logger.of(FinanceWithdrawController.class);

    private static BaseResponse response = new BaseResponse();

    public static Result getAllWithdraw(Long storeId, String startDate, String endDate, String sort,
                                        int offset, int limit, String status) {
        Merchant merchant = checkMerchantAccessAuthorization();
        if (merchant != null) {
            try {
                Query<FinanceWithdraw> query = null;
                // default query find by merchant id
                query = FinanceWithdrawRepository.finaAllWithdrawByMerchantId(merchant.id);
                if (storeId != null && storeId != 0L) {
                    Store store = Store.findById(storeId);
                    if (store == null) {
                        response.setBaseResponse(0, 0, 0, "store id does not exists", null);
                        return badRequest(Json.toJson(response));
                    }
                    query = FinanceWithdrawRepository.finaAllWithdrawByStoreId(storeId);
                }
                if (startDate.compareTo(endDate) > 0) {
                        response.setBaseResponse(0, 0, 0, "tanggal awal tidak boleh melebihi tanggal akhir", null);
                        return badRequest(Json.toJson(response));
                }
                List<FinanceWithdraw> financeWithdraws = FinanceWithdrawRepository.findAllWithdraw(query, startDate, endDate, sort, offset, limit, status);
                Integer totalData = FinanceWithdrawRepository.getTotalPage(query);
                List<FinanceWithdrawResponse> financeWithdrawResponses = new ArrayList<>();
                for (FinanceWithdraw financeWithdraw : financeWithdraws) {
                    FinanceWithdrawResponse financeWithdrawResponse = new FinanceWithdrawResponse();
                    financeWithdrawResponse.setRequestNumber(financeWithdraw.getRequestNumber());
                    financeWithdrawResponse.setDate(financeWithdraw.getDate());
                    financeWithdrawResponse.setStatus(financeWithdraw.getStatus());
                    financeWithdrawResponse.setAmount(financeWithdraw.getAmount());
                    financeWithdrawResponse.setStoreId(financeWithdraw.getStore().id);
                    financeWithdrawResponse.setStoreName(financeWithdraw.getStore().storeName);
                    financeWithdrawResponse.setRequestBy(financeWithdraw.getRequestBy());
                    financeWithdrawResponses.add(financeWithdrawResponse);
                }
                response.setBaseResponse(totalData, offset, limit, success + " Showing data transaction", financeWithdrawResponses);
                return ok(Json.toJson(response));
            } catch (Exception ex) {
                LOGGER.error("Error when getting transaction withdraw data");
                ex.printStackTrace();
                response.setBaseResponse(0, offset, limit, error + " Showing data transaction", null);
                return internalServerError(Json.toJson(response));
            }
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result requestWithdraw() {
        Merchant merchant = checkMerchantAccessAuthorization();
        if (merchant != null) {
            JsonNode json = request().body().asJson();
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                FinanceWithdrawRequest request = objectMapper.readValue(json.toString(), FinanceWithdrawRequest.class);
                Store store = Store.findById(request.getStoreId());
                if (store == null) {
                    response.setBaseResponse(0, 0, 0, inputParameter + " store not found.", null);
                    return badRequest(Json.toJson(response));
                }

                BigDecimal currentBalance = store.getActiveBalance();
                BigDecimal requestAmount = request.getAmount();

                if (requestAmount.compareTo(currentBalance) == 1) {
                    response.setBaseResponse(0, 0, 0, inputParameter + " amount should be less than active balance", null);
                    return badRequest(Json.toJson(response));
                }

                Optional<BankAccountMerchant> bankAccountMerchant = BankAccountMerchantRepository.findByAccountNumber(request.getAccountNumber());
                if (!bankAccountMerchant.isPresent()) {
                    response.setBaseResponse(0, 0, 0, inputParameter + " account number not found", null);
                    return badRequest(Json.toJson(response));
                }

                Transaction trx = Ebean.beginTransaction();
                try {
                    FinanceWithdraw financeWithdraw = new FinanceWithdraw();
                    financeWithdraw.setEventId(UUID.randomUUID().toString());
                    financeWithdraw.setRequestNumber(FinanceWithdraw.generateRequestNumber());
                    financeWithdraw.setDate(new Date());
                    financeWithdraw.setStatus(FinanceWithdraw.WAITING_CONFIRMATION);
                    financeWithdraw.setAmount(request.getAmount());
                    financeWithdraw.setAccountNumber(bankAccountMerchant.get().getAccountNumber());
                    financeWithdraw.setAccountName(bankAccountMerchant.get().getAccountName());
                    financeWithdraw.setBankName(bankAccountMerchant.get().getBankName());
                    financeWithdraw.setRequestBy(request.getRequestBy());
                    financeWithdraw.setStore(store);
                    financeWithdraw.save();

                    // do minus from store
                    BigDecimal currentActiveBalance = store.getActiveBalance();
                    LOGGER.info("current active balance store : " + currentActiveBalance);
                    BigDecimal lastActiveBalance = currentActiveBalance.subtract(request.getAmount());
                    LOGGER.info("last active balance store : " + lastActiveBalance);
                    store.setActiveBalance(lastActiveBalance);
                    store.update();

                    // do minus from merchant
                    BigDecimal currentTotalBalance = merchant.totalActiveBalance;
                    LOGGER.info("current total balance : " + currentTotalBalance);
                    BigDecimal lastTotalBalance = currentTotalBalance.subtract(request.getAmount());
                    LOGGER.info("last total balance : " + lastTotalBalance);
                    merchant.totalActiveBalance = lastTotalBalance;
                    merchant.update();

                    FinanceTransaction financeTransaction = new FinanceTransaction();
                    financeTransaction.setEventId(UUID.randomUUID().toString());
                    financeTransaction.setDate(financeWithdraw.getDate());
                    financeTransaction.setTransactionType(FinanceTransaction.WITHDRAW);
                    financeTransaction.setStatus(FinanceTransaction.OUT);
                    financeTransaction.setAmount(financeWithdraw.getAmount());
                    financeTransaction.setReferenceNumber(financeWithdraw.getRequestNumber());
                    financeTransaction.setStore(store);
                    financeTransaction.save();

                    trx.commit();
                    
                    StoreWithdrawEmail dto = StoreWithdrawEmail.getInstance(financeWithdraw);
                    EmailService.renderMailInformationWithdraw(dto, merchant);

                    response.setBaseResponse(1, offset, 1, success + " Request withdraw", financeWithdraw.getEventId());
                    return ok(Json.toJson(response));
                } catch (Exception e) {
                    LOGGER.error("Error while request withdraw", e);
                    e.printStackTrace();
                    trx.rollback();
                } finally {
                    trx.end();
                }
            } catch (Exception e) {
                LOGGER.error("Error while creating request withdraw", e);
                e.printStackTrace();
            }
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result downloadWithdraw(Long storeId, String startDate, String endDate, String sort,
                                             int offset, int limit, String status) {
        Merchant merchant = checkMerchantAccessAuthorization();
        if (merchant != null) {
            try {
                Query<FinanceWithdraw> query = null;
                // default query find by merchant id
                query = FinanceWithdrawRepository.finaAllWithdrawByMerchantId(merchant.id);
                if (storeId != null && storeId != 0L) {
                    Store store = Store.findById(storeId);
                    if (store == null) {
                        response.setBaseResponse(0, 0, 0, "store id does not exists", null);
                        return badRequest(Json.toJson(response));
                    }
                    query = FinanceWithdrawRepository.finaAllWithdrawByStoreId(storeId);
                }
                List<FinanceWithdraw> financeWithdraws = FinanceWithdrawRepository.findAllWithdraw(query, startDate, endDate, sort, offset, limit, status);
                File file = DownloadTransactionService.downloadTransaction(new ArrayList<>(), FinanceTransaction.WITHDRAW, financeWithdraws);
                response().setContentType("application/vnd.ms-excel");
                response().setHeader("Content-disposition", "attachment; filename=withdraw.xlsx");
                return ok(file);
            } catch (Exception ex) {
                ex.printStackTrace();
                LOGGER.error("Error while download transaction ", ex);
            }
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }
    
    public static Result callbackWithdrawSuccess (String reqNum) {
    	FinanceWithdraw model = FinanceWithdraw.find.where().eq("t0.request_number", reqNum).findUnique();
    	if (model != null) {
    		EmailService.renderMailSuccessWithdraw(StoreWithdrawEmail.getInstance(model), model.getStore().getMerchant());
    		response.setBaseResponse(0, 0, 0, "Email Sent Successfully", null);
    		return ok(Json.toJson(response));
    	}
    	response.setBaseResponse(0, 0, 0, "Error", null);
    	return badRequest(Json.toJson(response));
    }

}
