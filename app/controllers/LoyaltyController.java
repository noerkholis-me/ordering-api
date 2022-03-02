package controllers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlQuery;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hokeba.api.BaseResponse;
import com.hokeba.mapping.response.MapBannerMegaMenu;
import com.hokeba.mapping.response.MapLoyaltyPoint;
import com.wordnik.swagger.annotations.Api;

import models.LoyaltyPoint;
import models.Member;
import models.Product;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;

@Api(value = "/loyaltypoint")
public class LoyaltyController extends BaseController {
	@SuppressWarnings("rawtypes")
	private static BaseResponse response = new BaseResponse();

	public static Result countPoint() throws JsonProcessingException {
		Member actor = checkMemberAccessAuthorization();
		Date currentDate = new Date(System.currentTimeMillis());
		long sum = 0;
		//        ObjectMapper om = new ObjectMapper();
		if (actor != null) {
			List<LoyaltyPoint> data = LoyaltyPoint.find.where()
					.eq("is_deleted", false)
					.eq("member_id",actor.id)
					.ge("point", 0)
					.ge("expired_date", currentDate)
					.findList();
			for(LoyaltyPoint sumPoints: data) {
				sum += sumPoints.point-sumPoints.used;
			}
			response.setBaseResponse(1, offset, 1, success, sum);
			return ok(Json.toJson(response));
		}
		response.setBaseResponse(0, 0, 0, unauthorized, null);
		return unauthorized(Json.toJson(response));
	}

	public static Result history(){
		Member actor = checkMemberAccessAuthorization();
		if (actor != null) {
			List<LoyaltyPoint> data = LoyaltyPoint.find.where()
					.eq("is_deleted",false)
					.eq("member_id",actor.id)
					.orderBy("created_at DESC")
					.findList();

			List<MapLoyaltyPoint> loyalty = new ArrayList<MapLoyaltyPoint>();
			for(LoyaltyPoint d: data) {
				loyalty.add(new MapLoyaltyPoint(d));
			}
			response.setBaseResponse(data.size(), offset, data.size(), success, loyalty);
			return ok(Json.toJson(response));
		}
		response.setBaseResponse(0, 0, 0, unauthorized, null);
		return unauthorized(Json.toJson(response));
	}

	public static Result reducePoint(long point) {
		Member actor = checkMemberAccessAuthorization();
		Date currentDate = new Date(System.currentTimeMillis());
		List sql = null;
		int pointer = 0;
		LoyaltyPoint lp = null;
		if (actor != null) {
			Ebean.beginTransaction();
			try {
				String query = "SELECT *" +
						" FROM loyaltypoint" +
						" WHERE" +
						" is_deleted = false "
						+ " and member_id = " + actor.id
						+ " and point > 0"
						+ " and expired_date > " + "'"+currentDate+"'"
						+ " and point - used > 0" +
						" ORDER BY created_at ASC";
				SqlQuery sqlQuery = Ebean.createSqlQuery(query);
				sql = sqlQuery.findList();
//				while(point > 0) {
//					lp = (LoyaltyPoint) sql.get(pointer);
//					if(point >= lp.point-lp.used) {
//						lp.used += lp.point-lp.used;
//						point -= lp.point-lp.used;
//					}
//					else {
//						lp.used += point;
//						point -= lp.used;
//					}
//					lp.update();
//					pointer ++;
//				}
				Ebean.commitTransaction();
				response.setBaseResponse(sql.size(), offset, sql.size(), success, sql);
				//response.setBaseResponse(1, offset, 1, success, query);
				return ok(Json.toJson(response));
			}catch (Exception e) {
				// Logger.error("Internal CONTROLLER", e);
				Ebean.rollbackTransaction();
				response.setBaseResponse(0, 0, 0, notFound, null);
				return badRequest(Json.toJson(response));
			} finally {
				Ebean.endTransaction();
			}
		}
		response.setBaseResponse(0, 0, 0, unauthorized, null);
		return unauthorized(Json.toJson(response));
	}
}
