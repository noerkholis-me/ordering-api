package controllers.merchants;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Transaction;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hokeba.api.BaseResponse;
import com.hokeba.util.CommonFunction;
import com.hokeba.util.Constant;
import com.hokeba.util.Helper;
import com.wordnik.swagger.annotations.Api;
import controllers.BaseController;
import dtos.store.MultiStoreRequest;
import dtos.store.MultiStoreResponse;
import dtos.store.StoreResponse;
import models.Merchant;
import models.MultiStore;
import models.Store;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import utils.ShipperHelper;


@Api(value = "/merchants/multi-store", description = "Multi Store Management")
public class MultiStoreController extends BaseController {

    private final static Logger.ALogger logger = Logger.of(StoreController.class);
    private static BaseResponse response = new BaseResponse();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Result settingMultiStore() {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            JsonNode json = request().body().asJson();
            try {
                MultiStoreRequest multiStoreRequest = objectMapper.readValue(json.toString(), MultiStoreRequest.class);
                //check if the store address is at the same address
                if(multiStoreRequest.getAddressType().equalsIgnoreCase("ONE_ADDRESS")){
                    String validation = validateRequest(multiStoreRequest);
                    if (validation != null) {
                        response.setBaseResponse(0, 0, 0, validation, null);
                        return badRequest(Json.toJson(response));
                    }
                }
                Transaction trx = Ebean.beginTransaction();
                try {
                    MultiStore getMultiStore = MultiStore.find.where().eq("t0.merchant_id", ownMerchant.id).findUnique();
                    MultiStore multiStore = new MultiStore();
                    if (getMultiStore != null) {
                        getMultiStore.setAddressType(multiStoreRequest.getAddressType());
                        if (multiStoreRequest.getAddressType().equalsIgnoreCase("ONE_ADDRESS")) {
                            getMultiStore.setStoreAddress(multiStoreRequest.getAddress());
                            getMultiStore.setStorePhone(multiStoreRequest.getStorePhone());
                            getMultiStore.setStoreGmap(multiStoreRequest.getGoogleMapsUrl());
                            String[] finalLotLang = getLongitudeLatitude(getMultiStore.storeGmap);
                            getMultiStore.setStoreLatitude(Double.parseDouble(finalLotLang[0]));
                            getMultiStore.setStoreLongitude(Double.parseDouble(finalLotLang[1]));
                            if (getMultiStore.multiStoreCode == null || getMultiStore.multiStoreCode.trim().isEmpty()) {
                                getMultiStore.setMultiStoreCode(CommonFunction.generateRandomString(8));
                                getMultiStore.setMultiStoreQrCode(Constant.getInstance().getFrontEndUrl().concat(getMultiStore.multiStoreCode));
                            }
                        }
                        getMultiStore.update();
                    } else {
                        multiStore.setMerchant(ownMerchant);
                        multiStore.addressType = multiStoreRequest.getAddressType();
                        if (multiStoreRequest.getAddressType().equalsIgnoreCase("ONE_ADDRESS")) {
                            multiStore.storeAddress = multiStoreRequest.getAddress();
                            multiStore.storePhone = multiStoreRequest.getStorePhone();
                            multiStore.storeGmap = multiStoreRequest.getGoogleMapsUrl();
                            String[] finalLotLang = getLongitudeLatitude(multiStore.storeGmap);
                            multiStore.storeLatitude = Double.parseDouble(finalLotLang[0]);
                            multiStore.storeLongitude = Double.parseDouble(finalLotLang[1]);
                            multiStore.multiStoreCode = CommonFunction.generateRandomString(8);
                            multiStore.multiStoreQrCode = Constant.getInstance().getFrontEndUrl().concat(multiStore.multiStoreCode);
                        }
                        multiStore.save();
                    }

                    trx.commit();

                    response.setBaseResponse(1, 0, 1, success + " Setting QR multi store successfully",
                        toResponse(getMultiStore != null ? getMultiStore : multiStore));
                    return ok(Json.toJson(response));
                } catch (Exception e) {
                    logger.error("Error while setting QR multi store", e);
                    e.printStackTrace();
                    trx.rollback();
                } finally {
                    trx.end();
                }
            } catch (Exception e) {
                Logger.info("Error: " + e.getMessage());
                response.setBaseResponse(0, 0, 0, error, null);
                return internalServerError(Json.toJson(response));
            }
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result getSettingMultiStore() {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            try {
                MultiStore multiStore = MultiStore.find.where().eq("t0.merchant_id", ownMerchant.id).findUnique();
                if (multiStore == null) {
                    response.setBaseResponse(0, 0, 0, " Setting QR multi store not set", null);
                    return badRequest(Json.toJson(response));
                }
                response.setBaseResponse(1, 0, 1, success + " Show setting QR multi store",
                    toResponse(multiStore));
                return ok(Json.toJson(response));
            } catch (Exception e) {
                Logger.info("Error: " + e.getMessage());
                response.setBaseResponse(0, 0, 0, error, null);
                return internalServerError(Json.toJson(response));
            }
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    private static String validateRequest(MultiStoreRequest multiStoreRequest) {
        if (multiStoreRequest == null)
            return "Request is null or empty";
        if (multiStoreRequest.getStorePhone() == null || multiStoreRequest.getStorePhone().trim().isEmpty())
            return "Phone is null or empty";
        if (multiStoreRequest.getAddress() == null || multiStoreRequest.getAddress().trim().isEmpty())
            return "Address is null or empty";
        if (multiStoreRequest.getGoogleMapsUrl() == null || multiStoreRequest.getGoogleMapsUrl().trim().isEmpty())
            return "Google maps url is null or empty";

        // ========== Validate Shipper ========== //
        if (!multiStoreRequest.getStorePhone().matches(CommonFunction.phoneRegex)){
            return "Format nomor telepon tidak valid.";
        }
        return null;
    }

    public static String [] getLongitudeLatitude(String paramGmap){
        String [] tmpLongLat = paramGmap.split("@");
        String [] finalLotLang = tmpLongLat[1].split("/");
        String [] output = finalLotLang[0].split(",");
        return output;
    }

    private static MultiStoreResponse toResponse(MultiStore multiStore) {
        return MultiStoreResponse.builder()
            .id(multiStore.id)
            .multiStoreCode(multiStore.multiStoreCode)
            .storePhone(multiStore.storePhone)
            .addressType(multiStore.addressType)
            .address(multiStore.storeAddress)
            .googleMapsUrl(multiStore.getStoreGmap())
            .latitude(multiStore.storeLatitude)
            .longitude(multiStore.storeLongitude)
            .multiStoreQrCode(multiStore.getMultiStoreQrCode())
            .merchantId(multiStore.merchant.id)
            .build();
    }
}
