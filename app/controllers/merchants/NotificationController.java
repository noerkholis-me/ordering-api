package controllers.merchants;

import com.avaje.ebean.Query;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hokeba.api.ApiResponse;
import com.hokeba.api.BaseResponse;
import com.hokeba.mapping.response.MapNotificationMerchant;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import controllers.BaseController;
import models.Merchant;
import models.NotificationMerchant;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;

import java.io.IOException;

/**
 * Created by hendriksaragih on 4/8/17.
 */
@Api(value = "/merchants/notification", description = "Notifications")
public class NotificationController extends BaseController {
    @SuppressWarnings("rawtypes")
    private static BaseResponse response = new BaseResponse();

    public static Result index() {
        return ok();
    }

    @ApiOperation(value = "Get all notification list.", notes = "Returns list of notification.\n" + swaggerInfo
            + "", response = NotificationMerchant.class, responseContainer = "List", httpMethod = "GET")
    public static Result lists(int offset, int limit) {
        Merchant actor = checkMerchantAccessAuthorization();
        if (actor != null) {
            String message;
            Query<NotificationMerchant> query = NotificationMerchant.find.where()
                    .eq("merchant", actor)
                    .eq("is_deleted", false)
                    .order("id DESC");
            BaseResponse<NotificationMerchant> responseIndex;
            try {
                responseIndex = ApiResponse.getInstance().setResponseV2(query, "", "", offset, limit);
                responseIndex.setData(new ObjectMapper().convertValue(responseIndex.getData(), MapNotificationMerchant[].class));
                return ok(Json.toJson(responseIndex));
            } catch (IOException e) {
                message = e.getMessage();
                Logger.error("allDetail", e);
            }
            response.setBaseResponse(0, 0, 0, error, message);
            return internalServerError(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    @ApiOperation(value = "Delete Notification.", notes = "Delete Notification.\n" + swaggerInfo
            + "", response = NotificationMerchant.class, responseContainer = "Map", httpMethod = "DELETE")
    public static Result delete(Long id) {
        Merchant actor = checkMerchantAccessAuthorization();
        if (actor != null) {
            NotificationMerchant model = NotificationMerchant.find.where().eq("merchant", actor).eq("is_deleted", false).eq("id", id).setMaxRows(1).findUnique();
            if (model != null) {
                model.isDeleted = true; // SOFT DELETE
                model.save();

                response.setBaseResponse(1, offset, 1, deleted, null);
                return ok(Json.toJson(response));
            }
            response.setBaseResponse(0, 0, 0, notFound, null);
            return notFound(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }
}
