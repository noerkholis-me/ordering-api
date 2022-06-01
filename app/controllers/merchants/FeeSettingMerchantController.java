package controllers.merchants;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Query;
import com.avaje.ebean.Transaction;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hokeba.api.BaseResponse;
import controllers.BaseController;
import dtos.feesetting.FeeSettingRequest;
import dtos.feesetting.FeeSettingResponse;
import models.Merchant;
import models.merchant.FeeSettingMerchant;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import repository.FeeSettingMerchantRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class FeeSettingMerchantController extends BaseController {

    private static final Logger.ALogger logger = Logger.of(FeeSettingMerchantController.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final BaseResponse response = new BaseResponse();

    public static Result addFeeSetting() {
        Merchant merchant = checkMerchantAccessAuthorization();
        if (merchant != null) {
            JsonNode json = request().body().asJson();
            try {
                FeeSettingRequest feeSettingRequest = objectMapper.readValue(json.toString(), FeeSettingRequest.class);
                Transaction trx = Ebean.beginTransaction();
                try {
                    FeeSettingMerchant feeSetting = new FeeSettingMerchant();
                    feeSetting.setDate(new Date());
                    feeSetting.setTax(feeSettingRequest.getTax());
                    feeSetting.setService(feeSettingRequest.getService());
                    feeSetting.setPlatformFeeType(feeSettingRequest.getPlatformFeeType());
                    feeSetting.setPlatformFee(feeSettingRequest.getPlatformFee());
                    feeSetting.setPaymentFeeType(feeSettingRequest.getPaymentFeeType());
                    feeSetting.setPaymentFee(feeSettingRequest.getPaymentFee());
                    feeSetting.setUpdatedBy(feeSettingRequest.getUpdatedBy());
                    feeSetting.setMerchant(merchant);
                    feeSetting.save();

                    trx.commit();

                    response.setBaseResponse(1,offset, 1, success + " Create Fee Setting", feeSetting.id);
                    return ok(Json.toJson(response));
                } catch (Exception e) {
                    logger.error("Error while creating fee setting", e);
                    e.printStackTrace();
                    trx.rollback();
                } finally {
                    trx.end();
                }
            } catch (Exception ex) {
                logger.error("Error while creating fee setting", ex);
                ex.printStackTrace();
            }
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result getAllFeeSetting(String filter, String sort, int offset, int limit) {
        Merchant merchant = checkMerchantAccessAuthorization();
        if (merchant != null) {
            try {
                Query<FeeSettingMerchant> feeSettingMerchantQuery = FeeSettingMerchantRepository.findAllByMerchantQuery(merchant);
                List<FeeSettingMerchant> getTotalPage = FeeSettingMerchantRepository.getTotalPage(feeSettingMerchantQuery);
                List<FeeSettingMerchant> getFeeSettings = FeeSettingMerchantRepository.findAllWithPaging(feeSettingMerchantQuery, sort, filter, offset, limit);
                List<FeeSettingResponse> feeSettingResponses = new ArrayList<>();
                for (FeeSettingMerchant feeSetting : getFeeSettings) {
                    FeeSettingResponse feeSettingResponse = new FeeSettingResponse();
                    feeSettingResponse.setId(feeSetting.id);
                    feeSettingResponse.setDate(feeSetting.getDate());
                    feeSettingResponse.setTax(feeSetting.getTax());
                    feeSettingResponse.setService(feeSetting.getService());
                    feeSettingResponse.setPlatformFeeType(feeSetting.getPlatformFeeType());
                    feeSettingResponse.setPaymentFeeType(feeSetting.getPaymentFeeType());
                    feeSettingResponse.setUpdatedBy(feeSetting.getUpdatedBy());
                    feeSettingResponses.add(feeSettingResponse);
                }
                response.setBaseResponse(filter == null || filter.equals("") ? getTotalPage.size() : feeSettingResponses.size(), offset, limit, success + " Showing data fee setting", feeSettingResponses);
                return ok(Json.toJson(response));
            } catch (Exception ex) {
                logger.error("Error while getting list fee setting", ex);
                ex.printStackTrace();
            }
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result getFeeSetting(Long merchantId) {
        int authority = checkAccessAuthorization("all");
        if (authority == 200 || authority == 203) {
            try {

                Optional<FeeSettingMerchant> feeSettingMerchant = FeeSettingMerchantRepository.findByLatestFeeSetting(merchantId);
                if (!feeSettingMerchant.isPresent()) {
                    response.setBaseResponse(0, 0, 0, notFound, null);
                    return notFound(Json.toJson(response));
                }

                FeeSettingResponse feeSettingResponse = new FeeSettingResponse();
                feeSettingResponse.setId(feeSettingMerchant.get().id);
                feeSettingResponse.setDate(feeSettingMerchant.get().getDate());
                feeSettingResponse.setTax(feeSettingMerchant.get().getTax());
                feeSettingResponse.setService(feeSettingMerchant.get().getService());
                feeSettingResponse.setPlatformFeeType(feeSettingMerchant.get().getPlatformFeeType());
                feeSettingResponse.setPaymentFeeType(feeSettingMerchant.get().getPaymentFeeType());
                feeSettingResponse.setUpdatedBy(feeSettingMerchant.get().getUpdatedBy());

                response.setBaseResponse(1, 0, 0, success + " Showing data fee setting", feeSettingResponse);
                return ok(Json.toJson(response));
            } catch (Exception ex) {
                logger.error("Error while getting fee setting", ex);
                ex.printStackTrace();
            }
        } else if (authority == 403) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        } else {
            response.setBaseResponse(0, 0, 0, unauthorized, null);
            return unauthorized(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, error, null);
        return ok(Json.toJson(response));
    }




}
