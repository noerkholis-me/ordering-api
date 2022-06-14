package controllers.finance;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Transaction;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hokeba.api.BaseResponse;
import controllers.BaseController;
import dtos.finance.FinanceWithdrawRequest;
import dtos.finance.FinanceWithdrawResponse;
import models.Merchant;
import models.Store;
import models.finance.FinanceTransaction;
import models.finance.FinanceWithdraw;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import repository.finance.FinanceTransactionRepository;
import repository.finance.FinanceWithdrawRepository;
import service.DownloadTransactionService;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class FinanceWithdrawController extends BaseController {

    private final static Logger.ALogger LOGGER = Logger.of(FinanceWithdrawController.class);

    private static BaseResponse response = new BaseResponse();

    public static Result getAllWithdraw(Long storeId, String startDate, String endDate, String sort,
                                        int offset, int limit, String status) {
        Merchant merchant = checkMerchantAccessAuthorization();
        if (merchant != null) {
            try {
                List<FinanceWithdraw> financeWithdraws = FinanceWithdrawRepository.findAllWithdraw(startDate, endDate, storeId, sort, offset, limit, status);
                Integer totalData = FinanceWithdrawRepository.getTotalPage(storeId).size();
                List<FinanceWithdrawResponse> financeWithdrawResponses = new ArrayList<>();
                for (FinanceWithdraw financeWithdraw : financeWithdraws) {
                    FinanceWithdrawResponse financeWithdrawResponse = new FinanceWithdrawResponse();
                    financeWithdrawResponse.setRequestNumber(financeWithdraw.getRequestNumber());
                    financeWithdrawResponse.setDate(financeWithdraw.getDate());
                    financeWithdrawResponse.setStatus(financeWithdraw.getStatus());
                    financeWithdrawResponse.setAmount(financeWithdraw.getAmount());
                    financeWithdrawResponses.add(financeWithdrawResponse);
                }
                response.setBaseResponse(totalData, offset, limit, success + " Showing data transaction", financeWithdrawResponses);
                return ok(Json.toJson(response));
            } catch (Exception ex) {
                LOGGER.error("Error when getting transaction withdraw data");
                ex.printStackTrace();
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
                Transaction trx = Ebean.beginTransaction();
                try {
                    FinanceWithdraw financeWithdraw = new FinanceWithdraw();
                    financeWithdraw.setEventId(UUID.randomUUID().toString());
                    financeWithdraw.setRequestNumber(FinanceWithdraw.generateRequestNumber());
                    financeWithdraw.setDate(new Date());
                    financeWithdraw.setStatus(FinanceWithdraw.WAITING_CONFIRMATION);
                    financeWithdraw.setAmount(request.getAmount());
                    financeWithdraw.setAccountNumber(request.getAccountNumber());
                    financeWithdraw.setStore(store);
                    financeWithdraw.save();

                    // do minus from store
                    BigDecimal currentActiveBalance = store.getActiveBalance();
                    LOGGER.info("current active balance : " + currentActiveBalance);
                    BigDecimal lastActiveBalance = currentActiveBalance.subtract(request.getAmount());
                    LOGGER.info("last active balance : " + lastActiveBalance);
                    store.setActiveBalance(lastActiveBalance);
                    store.update();

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
                List<FinanceWithdraw> financeWithdraws = FinanceWithdrawRepository.findAllWithdraw(startDate, endDate, storeId, sort, offset, limit, status);
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

}
