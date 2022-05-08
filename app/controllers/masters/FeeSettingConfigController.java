package controllers.masters;

import com.hokeba.api.BaseResponse;
import controllers.BaseController;
import dtos.feesetting.FeeSettingConfigResponse;
import models.Merchant;
import models.internal.FeeSetting;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;

import java.util.Optional;

public class FeeSettingConfigController extends BaseController {

    private static final Logger.ALogger logger = Logger.of(FeeSettingConfigController.class);
    private static final BaseResponse response = new BaseResponse();

    public static Result getFeeSettingConfig() {
        Merchant merchant = checkMerchantAccessAuthorization();
        if (merchant != null) {
            try {
                Optional<FeeSetting> feeSetting = FeeSetting.findByLastUpdated();
                if (!feeSetting.isPresent()) {
                    response.setBaseResponse(0, 0, 0, "fee setting config does not exists", null);
                    return badRequest(Json.toJson(response));
                }
                FeeSettingConfigResponse feeSettingConfigResponse = new FeeSettingConfigResponse();
                feeSettingConfigResponse.setDate(feeSetting.get().getDate());
                feeSettingConfigResponse.setPlatformFee(feeSetting.get().getPlatformFee());
                feeSettingConfigResponse.setUpdatedBy(feeSetting.get().getUserCms().fullName);
                response.setBaseResponse(1,offset, 1, success + " showing fee setting config ", feeSettingConfigResponse);
                return ok(Json.toJson(response));
            } catch (Exception e) {
                logger.error("Error while getting fee setting config", e);
                e.printStackTrace();
            }
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

}
