package controllers.masters;

import com.hokeba.api.BaseResponse;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import controllers.BaseController;
import dtos.ConfigSettingResponse;
import models.ConfigSettings;
import models.ShipperProvince;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import utils.ConfigSettingEnum;

@Api(value = "/master/config-setting", description = "Get Config Setting")
public class ConfigSettingController extends BaseController {

    private final static Logger.ALogger logger = Logger.of(ConfigSettingController.class);
    private static BaseResponse response = new BaseResponse();

    @ApiOperation(value = "get config setting", notes = "Returns object of config setting.\n" + swaggerInfo
            + "", responseContainer = "List", httpMethod = "GET")
    public static Result getConfigSettingByKey (String key) {
        int authority = checkAccessAuthorization("all");
        if (authority == 200 || authority == 203) {
            try {
                String keyName = key.toLowerCase();
                if (!keyName.equals(ConfigSettingEnum.RADIUS.getValue())) {
                    response.setBaseResponse(0, 0, 0, " config key is not valid.", null);
                    return badRequest(Json.toJson(response));
                }
                ConfigSettings configSetting = ConfigSettings.findByKey(keyName);
                if (configSetting == null) {
                    response.setBaseResponse(0, 0, 0, " config key is does not exists.", null);
                    return badRequest(Json.toJson(response));
                }
                ConfigSettingResponse configSettingResponse = ConfigSettingResponse.builder()
                        .key(configSetting.key)
                        .name(configSetting.name)
                        .value(configSetting.value)
                        .module(configSetting.module)
                        .build();
                response.setBaseResponse(1, 0, 0, success, configSettingResponse);
                return ok(Json.toJson(response));
            } catch (Exception e) {
                logger.error("Error in getConfigSettingByKey: ", e);
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
