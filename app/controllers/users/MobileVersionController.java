package controllers.users;

import java.util.List;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Transaction;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hokeba.api.BaseResponse;
import com.hokeba.mapping.response.MapMobileVersion;

import controllers.BaseController;
import models.MobileVersion;
import play.libs.Json;
import play.mvc.Result;

public class MobileVersionController extends BaseController {
	private static BaseResponse response = new BaseResponse();

	// get lastest version
	public static Result getLastestVersion(int versionCode, String deviceType) {
		JsonNode json = request().body().asJson();
		int authority = checkAccessAuthorization("all");
		if (authority == 200 || authority == 203) {
			int majorVersion;
			String orderBy = deviceType.equals(MobileVersion.DEVICETYPE_ANDROID) ? "mobile_version" : "mobile_version_ios";
			MobileVersion query = MobileVersion.find.where().eq("is_deleted", false).eq("major_minor_update", true)
					.order(orderBy + " desc").setMaxRows(1).findUnique();
			MobileVersion queryLastestVersion = MobileVersion.find.where().eq("is_deleted", false)
					.order(orderBy + " desc").setMaxRows(1).findUnique();
			majorVersion = deviceType.equals(MobileVersion.DEVICETYPE_ANDROID) ? query.mobileVersion : query.mobileVersionIos;
			if(versionCode < majorVersion) {
				queryLastestVersion.majorMinorUpdate = true;
			}
			response.setBaseResponse(1, offset, 1, success,
					new ObjectMapper().convertValue(queryLastestVersion, MapMobileVersion.class));
			return ok(Json.toJson(response));
		} else if (authority == 403) {
			response.setBaseResponse(0, 0, 0, forbidden, null);
			return forbidden(Json.toJson(response));
		}
		response.setBaseResponse(0, 0, 0, unauthorized, null);
		return unauthorized(Json.toJson(response));
	}

	// get lastest major version
	public static Result getLastestMajorVersion(String deviceType) {
		int authority = checkAccessAuthorization("all");
		if (authority == 200 || authority == 203) {
			String orderBy = deviceType.equals(MobileVersion.DEVICETYPE_ANDROID) ? "mobile_version" : "mobile_version_ios";
			List<MobileVersion> query;
			query = MobileVersion.find.where().eq("is_deleted", false).eq("major_minor_update", true)
					.order(orderBy + " desc").setMaxRows(1).findList();
			response.setBaseResponse(query.size(), offset, query.size(), success,
					new ObjectMapper().convertValue(query, MapMobileVersion[].class));
			return ok(Json.toJson(response));
		} else if (authority == 403) {
			response.setBaseResponse(0, 0, 0, forbidden, null);
			return forbidden(Json.toJson(response));
		}
		response.setBaseResponse(0, 0, 0, unauthorized, null);
		return unauthorized(Json.toJson(response));
	}

}
