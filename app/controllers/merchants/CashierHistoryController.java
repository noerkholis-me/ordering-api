package controllers.merchants;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Query;
import com.avaje.ebean.Transaction;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hokeba.api.BaseResponse;
import controllers.BaseController;
import dtos.cashier.CashierHistoryResponse;
import dtos.cashier.CashierOpenPosRequest;
import models.Member;
import models.Merchant;
import models.Store;
import models.UserMerchant;
import models.merchant.CashierHistoryMerchant;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import repository.UserMerchantRepository;
import repository.cashierhistory.CashierHistoryMerchantRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class CashierHistoryController extends BaseController {

    private static final Logger.ALogger LOGGER = Logger.of(CashierHistoryController.class);
    private static final BaseResponse response = new BaseResponse();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * controller for open POS
     */
    public static Result openPos() {
        Merchant merchant = checkMerchantAccessAuthorization();
        if (merchant != null) {
            JsonNode json = request().body().asJson();
            try {
                CashierOpenPosRequest cashierOpenPosRequest = objectMapper.readValue(json.toString(), CashierOpenPosRequest.class);
                String message = validateRequest(cashierOpenPosRequest);
                if (message != null) {
                    response.setBaseResponse(0, 0, 0, inputParameter + " " + message, null);
                    return badRequest(Json.toJson(response));
                }
                Transaction trx = Ebean.beginTransaction();
                try {

                    UserMerchant userMerchant = UserMerchantRepository.findAccountById(cashierOpenPosRequest.getUserMerchantId());
                    if (userMerchant == null) {
                        response.setBaseResponse(0, 0, 0, inputParameter + " user merchant tidak ditemukan", null);
                        return badRequest(Json.toJson(response));
                    }

                    Store store = Store.findById(cashierOpenPosRequest.getStoreId());
                    if (store == null) {
                        response.setBaseResponse(0, 0, 0, inputParameter + " store tidak ditemukan", null);
                        return badRequest(Json.toJson(response));
                    }


                    CashierHistoryMerchant cashier = new CashierHistoryMerchant();
                    cashier.setSessionCode(CashierHistoryMerchant.generateSessionCode(cashierOpenPosRequest.getUserMerchantId(), cashierOpenPosRequest.getStoreId()));
                    cashier.setIsActive(Boolean.TRUE);
                    cashier.setStartTime(new Date());
                    cashier.setUserMerchant(userMerchant);
                    cashier.setStartTotalAmount(cashierOpenPosRequest.getStartTotalAmount());
                    cashier.setStore(store);

                    cashier.save();

                    trx.commit();

                    response.setBaseResponse(1, 0, 0, success, cashier.id);
                    return ok(Json.toJson(response));
                } catch (Exception e) {
                    LOGGER.error("Error while creating session cashier", e);
                    e.printStackTrace();
                    trx.rollback();
                } finally {
                    trx.end();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                response.setBaseResponse(0, 0, 0, error, null);
                return internalServerError(Json.toJson(response));
            }
        } else {
            response.setBaseResponse(0, 0, 0, unauthorized, null);
            return unauthorized(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, error, null);
        return internalServerError(Json.toJson(response));
    }

    public static Result getAllCashierHistory(int limit, int offset, Long storeId) {
        Merchant merchant = checkMerchantAccessAuthorization();
        if (merchant != null) {
            try {

                System.out.println("merchant id >>> " + merchant);

                Query<CashierHistoryMerchant> query = null;
                query = CashierHistoryMerchantRepository.findAllCashierHistoryByMerchantId(merchant.id);
                if (storeId != null && storeId != 0L) {
                    Store store = Store.findById(storeId);
                    if (store == null) {
                        response.setBaseResponse(0, 0, 0, "store tidak ditemukan", null);
                        return badRequest(Json.toJson(response));
                    }
                    query = CashierHistoryMerchantRepository.findAllCashierHistoryByStoreId(storeId);
                }
                List<CashierHistoryMerchant> cashierHistoryMerchants = CashierHistoryMerchantRepository.findAllCashierHistory(query, offset, limit);
                Integer totalData = CashierHistoryMerchantRepository.getTotalData(query);
                List<CashierHistoryResponse> responses = new ArrayList<>();
                for (CashierHistoryMerchant historyMerchant : cashierHistoryMerchants) {
                    CashierHistoryResponse response = new CashierHistoryResponse();
                    response.setCashierName(historyMerchant.getUserMerchant().getFullName());
                    response.setSessionCode(historyMerchant.getSessionCode());
                    response.setStartTime(historyMerchant.getStartTime());
                    response.setEndTime(historyMerchant.getEndTime());
                    response.setStoreName(historyMerchant.getStore().storeName);
                    responses.add(response);
                }
                response.setBaseResponse(totalData, offset, limit, success + " menampilkan data history cashier", responses);
                return ok(Json.toJson(response));
            } catch (Exception ex) {
                ex.printStackTrace();
                response.setBaseResponse(0, 0, 0, error, null);
                return internalServerError(Json.toJson(response));
            }
        } else {
            response.setBaseResponse(0, 0, 0, unauthorized, null);
            return unauthorized(Json.toJson(response));
        }
    }

    private static String validateRequest(CashierOpenPosRequest cashierOpenPosRequest) {
        if (cashierOpenPosRequest.getStartTotalAmount() == null)
            return "start total amount tidak boleh kosong";
        if (cashierOpenPosRequest.getStartTotalAmount().compareTo(new BigDecimal(1000)) < 0)
            return "start total amount tidak boleh kurang dari 1000";
        if (cashierOpenPosRequest.getStartTotalAmount().compareTo(new BigDecimal(100000000)) > 0)
            return "start total amount tidak boleh lebih dari 100000000";

        return null;
    }

}
