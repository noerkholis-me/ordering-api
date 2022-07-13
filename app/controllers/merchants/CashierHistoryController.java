package controllers.merchants;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Transaction;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hokeba.api.BaseResponse;
import controllers.BaseController;
import dtos.cashier.CashierOpenPosRequest;
import models.Merchant;
import models.Store;
import models.UserMerchant;
import models.merchant.CashierHistoryMerchant;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import repository.UserMerchantRepository;

import java.math.BigDecimal;
import java.util.Date;

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

                    }

                    CashierHistoryMerchant cashier = new CashierHistoryMerchant();
                    cashier.setSessionCode(CashierHistoryMerchant.generateSessionCode(cashierOpenPosRequest.getUserMerchantId(), cashierOpenPosRequest.getStoreId()));
                    cashier.setIsActive(Boolean.TRUE);
                    cashier.setStartTime(new Date());
                    return null;
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
