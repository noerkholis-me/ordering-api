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
import dtos.delivery.DeliveryFeeRequest;
import dtos.delivery.DeliveryFeeResponse;
import dtos.delivery.DeliverySettingRequest;
import dtos.payment.InitiatePaymentResponse;
import dtos.stock.StockHistoryResponse;
import models.Merchant;
import models.StockHistory;
import models.DeliverySettings;

import models.Store;
import models.transaction.Order;

import org.json.JSONObject;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import repository.DeliverySettingRepository;
import service.DeliveryService;
import service.PaymentService;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

    public static Result checkFeeDelivery() {

        JsonNode rawRequest = request().body().asJson();

        try {
            DeliveryFeeRequest deliveryFeeRequest = objectMapper.readValue(rawRequest.toString(), DeliveryFeeRequest.class);
            Store store = Store.findById(deliveryFeeRequest.getStore_id());

            // store
            DeliveryDirectionRequest base = new DeliveryDirectionRequest();
            base.setLat(store.getStoreLatitude());
            base.setLong(store.getStoreLongitude());
            // base.setLat(107.5689333);
            // base.setLong(-6.9377524);

            //customer
            DeliveryDirectionRequest target = new DeliveryDirectionRequest();
            target.setLat(deliveryFeeRequest.getLatitude());
            target.setLong(deliveryFeeRequest.getLongitude());
            

            ServiceResponse serviceResponse = DeliveryService.getInstance().checkDistance(base, target);

            String object = objectMapper.writeValueAsString(serviceResponse.getData());
            JSONObject jsonObject = new JSONObject(object);
            String initiate = jsonObject.getJSONArray("routes").getJSONObject(0).getJSONObject("summary").toString();
            DeliveryDirectionResponse distance = objectMapper.readValue(initiate, DeliveryDirectionResponse.class);

            double changeKM = (double) distance.getDistance() / 1000.0;

            DeliverySettings deliverySettings = DeliverySettingRepository.findBystoreId(deliveryFeeRequest.getStore_id());
            DeliverySettings responses = new DeliverySettings();
            if (deliverySettings != null) {
                if (changeKM > deliverySettings.getMaxRangeDelivery()) {
                    response.setBaseResponse(0, 0, 0, "jarak melebihi batas maksimal", null);
                    return badRequest(Json.toJson(response));
                }

                int feeTotal = 0;
                if (deliverySettings.getCalculateMethod().equals("tarif_km")) {
                    if (deliverySettings.getEnableFlatPrice() == true) {
                        int remainder = (int) changeKM - deliverySettings.getMaxRangeFlatPrice();

                        feeTotal = (int) (remainder * deliverySettings.getKmPriceValue()) + deliverySettings.getFlatPriceValue();
                    } else {
                        feeTotal = (int) changeKM * deliverySettings.getKmPriceValue();
                    }
                    
                } else if (deliverySettings.getCalculateMethod().equals("tarif_flat")) {
                    if (changeKM > deliverySettings.getMaxRangeFlatPrice()) {
                        feeTotal = (int) deliverySettings.getDeliverFee();
                    } else {
                        feeTotal = (int) deliverySettings.getFlatPriceValue();
                    } 
                }

                DeliveryFeeResponse responsesFee = new DeliveryFeeResponse();
                responsesFee.setDistance(changeDistance(changeKM));
                responsesFee.setDuration((int) distance.getDuration());
                responsesFee.setFeeDelivery(feeTotal);
                response.setBaseResponse(1, offset, 1, success, responsesFee);
                return ok(Json.toJson(response));

            } else {
               response.setBaseResponse(0, 0, 0, "store tidak di temukan", null);
               return badRequest(Json.toJson(response));
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.setBaseResponse(0, 0, 0, "ada kesalahan pada saat melakukan setting delivery", null);
            return badRequest(Json.toJson(response));
        }
    }

    public static Double changeDistance(Double changeKM) {
        double x = changeKM;
        int angkaSignifikan = 2;
        double temp = Math.pow(10, angkaSignifikan);
        double y = (double) Math.round(x*temp)/temp;
        return y;
    }


}
