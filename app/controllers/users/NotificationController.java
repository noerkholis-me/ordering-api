package controllers.users;

import com.avaje.ebean.Query;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hokeba.api.ApiResponse;
import com.hokeba.api.BaseResponse;
import com.hokeba.mapping.response.MapNotificationMerchant;
import com.hokeba.social.requests.FirebaseNotificationHelper;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import controllers.BaseController;
import models.Member;
import models.NotificationMember;
import models.NotificationMerchant;
import models.Promo;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;

/**
 * Created by hendriksaragih on 4/8/17.
 */
@Api(value = "/users/notification", description = "Notifications")
public class NotificationController extends BaseController {
    @SuppressWarnings("rawtypes")
    private static BaseResponse response = new BaseResponse();

    public static Result index() {
        return ok();
    }

    @ApiOperation(value = "Get all notification list.", notes = "Returns list of notification.\n" + swaggerInfo
            + "", response = NotificationMerchant.class, responseContainer = "List", httpMethod = "GET")
    public static Result lists(int offset, int limit) {
        Member actor = checkMemberAccessAuthorization();
        if (actor != null) {
            String message;
            Query<NotificationMember> query = NotificationMember.find.where()
                    .eq("member", actor)
                    .eq("is_deleted", false)
                    .order("id DESC");
            BaseResponse<NotificationMember> responseIndex;
            try {
                responseIndex = ApiResponse.getInstance().setResponseV2(query, "", "", offset, limit);
                responseIndex.setData(new ObjectMapper().convertValue(responseIndex.getData(), MapNotificationMerchant[].class));
                return ok(Json.toJson(responseIndex));
            } catch (IOException e) {
                message = e.getMessage();
                Logger.error("notif", e);
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
        Member currentMember = checkMemberAccessAuthorization();
        if (currentMember != null) {
            NotificationMember model = NotificationMember.find.where().eq("member", currentMember).eq("is_deleted", false).eq("id", id).setMaxRows(1).findUnique();
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
    
    @ApiOperation(value = "Get all notification list.", notes = "Returns list of notification.\n" + swaggerInfo
            + "", response = NotificationMerchant.class, responseContainer = "List", httpMethod = "GET")
    public static Result sendCampaignNotification() {
    	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	DateTime now = new DateTime();
    	List<Promo> promos = Promo.find.where().eq("status", true).ge("active_to", now).eq("is_deleted", false).findList();
    	System.out.println(promos.size());
    	if(!(promos == null)) {
    		for (Promo promo:promos) {
    			String title = promo.name + " Notification";
    			String message = promo.description;
    			ObjectNode ob = Json.newObject();
    			ob.put("data", promo.description);
    			ObjectNode type = ob;
    			String topic = "campaign";
//    			FirebaseNotificationHelper.getInstance().sendToTopic(title, message, type, topic);
    		}
    		response.setBaseResponse(promos.size(), 0, 0, success, 0);
    		return ok(Json.toJson(response));
    	} else {
    		response.setBaseResponse(0, 0, 0, error, 0);
    		return internalServerError(Json.toJson(response));
    	}
    }
}
