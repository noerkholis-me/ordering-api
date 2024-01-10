package controllers.delivery;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Transaction;
import com.fasterxml.jackson.databind.JsonNode;
import com.hokeba.api.BaseResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hokeba.api.BaseResponse;
import com.hokeba.http.response.global.ServiceResponse;
import controllers.BaseController;
import dtos.delivery.DeliveryDirectionRequest;
import dtos.delivery.DeliveryDirectionResponse;
import dtos.delivery.DeliverySettingRequest;
import dtos.payment.InitiatePaymentResponse;
import dtos.stock.StockHistoryResponse;
import models.Merchant;
import models.StockHistory;
import models.DeliverySettings;

import models.Store;
import org.json.JSONObject;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import repository.DeliverySettingRepository;
import service.DeliveryService;
import service.PaymentService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class DeliverySettingController extends BaseController {

    private final static Logger.ALogger logger = Logger.of(DeliverySettingController.class);

    private static BaseResponse response = new BaseResponse();

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Result listDelivery(String sort, int offset, int limit) {

        try {

            Merchant ownMerchant = checkMerchantAccessAuthorization();
            if (ownMerchant == null) {
                response.setBaseResponse(0, 0, 0, unauthorized, null);
                return unauthorized(Json.toJson(response));
            }

            int total = DeliverySettingRepository.countAll(ownMerchant.id);
            List<DeliverySettings> deliverySettings = DeliverySettingRepository.findAll(ownMerchant.id, sort, offset, limit);

            response.setBaseResponse(total, 0, 0, "", deliverySettings);
            return ok(Json.toJson(response));

        } catch (Exception e) {
            e.printStackTrace();
            response.setBaseResponse(0, 0, 0, "ada kesalahan pada saat list setting delivery", null);
            return badRequest(Json.toJson(response));
        }
    }

    public static Result getDelivery(Long store_id) {

        try {

            Merchant ownMerchant = checkMerchantAccessAuthorization();
            if (ownMerchant == null) {
                response.setBaseResponse(0, 0, 0, unauthorized, null);
                return unauthorized(Json.toJson(response));
            }

            DeliverySettings deliverySettings = DeliverySettingRepository.findBystoreId(store_id);

            response.setBaseResponse(0, 0, 0, "", deliverySettings);
            return ok(Json.toJson(response));

        } catch (Exception e) {
            e.printStackTrace();
            response.setBaseResponse(0, 0, 0, "ada kesalahan pada saat get setting delivery", null);
            return badRequest(Json.toJson(response));
        }
    }

    public static Result addDelivery() {

        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant == null) {
            response.setBaseResponse(0, 0, 0, unauthorized, null);
            return unauthorized(Json.toJson(response));
        }

        JsonNode rawRequest = request().body().asJson();
        try {
            DeliverySettingRequest deliverySettingRequest = objectMapper.readValue(rawRequest.toString(), DeliverySettingRequest.class);
            Store store = Store.findById(deliverySettingRequest.getStore_id());

            DeliverySettings deliverySettings = DeliverySettingRepository.findBystoreId(deliverySettingRequest.getStore_id());
            DeliverySettings responses = new DeliverySettings();
            if (deliverySettings == null) {
                responses = DeliverySettingRepository.addDeliverySettings(store, ownMerchant, deliverySettingRequest);
            } else {
                responses = DeliverySettingRepository.updateDeliverySettings(deliverySettings, store, ownMerchant, deliverySettingRequest);
            }
            response.setBaseResponse(1, offset, 1, success, responses);
            return ok(Json.toJson(response));

        } catch (Exception e){
            e.printStackTrace();
            response.setBaseResponse(0, 0, 0, "ada kesalahan pada saat melakukan setting delivery", null);
            return badRequest(Json.toJson(response));
        }
    }

    public static Result checkDistance() {

        try {

            DeliveryDirectionRequest base = new DeliveryDirectionRequest();
            base.setLat(107.5689333);
            base.setLong(-6.9377524);

            DeliveryDirectionRequest target = new DeliveryDirectionRequest();
            target.setLat(107.5735131);
            target.setLong(-6.9323057);

            ServiceResponse serviceResponse = DeliveryService.getInstance().checkDistance(base, target);

            String object = objectMapper.writeValueAsString(serviceResponse.getData());
            JSONObject jsonObject = new JSONObject(object);
            String initiate = jsonObject.getJSONArray("routes").getJSONObject(0).getJSONObject("summary").toString();
            DeliveryDirectionResponse initiatePaymentResponse = objectMapper.readValue(initiate, DeliveryDirectionResponse.class);


            response.setBaseResponse(1, offset, 1, success, initiatePaymentResponse);
            return ok(Json.toJson(response));

        } catch (Exception e) {
            e.printStackTrace();
            response.setBaseResponse(0, 0, 0, "ada kesalahan pada saat melakukan setting delivery", null);
            return badRequest(Json.toJson(response));
        }
    }

}
