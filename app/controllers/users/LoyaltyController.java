package controllers.users;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.elasticsearch.common.joda.time.DateTime;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlQuery;
import com.avaje.ebean.SqlRow;
import com.avaje.ebean.Transaction;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hokeba.api.BaseResponse;
import com.hokeba.mapping.request.MapOrder;
import com.hokeba.mapping.request.MapOrderDetail;
import com.hokeba.mapping.request.MapOrderSeller;
import com.hokeba.mapping.response.MapBannerMegaMenu;
import com.hokeba.mapping.response.MapEligibleLoyalty;
import com.hokeba.mapping.response.MapLoyaltyPoint;
import com.hokeba.mapping.response.MapLoyaltySetting;
import com.wordnik.swagger.annotations.Api;

import controllers.BaseController;
import models.BannerMegaMenu;
import models.Category;
import models.CategoryLoyalty;
import models.ConfigSettings;
import models.LoyaltyPoint;
import models.Member;
import models.Product;
import models.ProductDetailVariance;
import models.SalesOrder;
import models.SalesOrderDetail;
import models.SalesOrderSeller;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;

@Api(value = "/loyaltypoint")
public class LoyaltyController extends BaseController {
	@SuppressWarnings("rawtypes")
	private static BaseResponse response = new BaseResponse();

	public static long countPoint()  {
		Member actor = checkMemberAccessAuthorization();
		Date currentDate = new Date(System.currentTimeMillis());
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

//		String currentDateString = dateFormat.format(currentDate);
		long sum = 0;
		//ObjectMapper om = new ObjectMapper();
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
		}
		return sum;
	}

	public static Result resultCountPoint() throws JsonProcessingException{
		Member actor = checkMemberAccessAuthorization();
		long sum = 0;
		if (actor != null) {
			sum = countPoint();
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

	public static boolean reducePoint(Long point,Long transactionId) {
		Member actor = checkMemberAccessAuthorization();
		Date currentDate = new Date(System.currentTimeMillis());
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String currentDateString = dateFormat.format(currentDate);
		List<SqlRow> sql = null;
		int pointer = 0;
		LoyaltyPoint lp = null;
		if (actor != null) {
			//			JsonNode json = request().body().asJson();
			//			if(!json.has("loyaltypoint")) {
			//				response.setBaseResponse(0, 0, 0, "invalid json request", null);
			//				return badRequest(Json.toJson(response));
			//			}
			//			long point = json.get("loyaltypoint").asLong();
			long total = point;
			if(point>=countPoint()) {
				try {
					Ebean.beginTransaction();
//					String query = "SELECT *" +
//							" FROM loyaltypoint" +
//							" WHERE" +
//							" is_deleted = false "
//							+ " and member_id = " + actor.id
//							+ " and point > 0"
//							+ " and expired_date > " + "'"+currentDateString+"'"
//							+ " and point - used > 0" +
//							" ORDER BY created_at ASC";
//					SqlQuery sqlQuery = Ebean.createSqlQuery(query);

					SqlQuery sqlQuery = Ebean.createSqlQuery(
							"SELECT * FROM loyaltypoint WHERE is_deleted = false and member_id = :actorId and point > 0 and expired_date > ' :currentDateString ' and point - used > 0 ORDER BY created_at ASC");
					sqlQuery.setParameter("actorId",actor.id);
					sqlQuery.setParameter("currentDateString",currentDate);
					
					sql = sqlQuery.findList();

					while(point > 0) {
						lp = LoyaltyPoint.find.byId(sql.get(pointer).getLong("id"));
						if(point >= lp.point-lp.used) {
							point = point-lp.point-lp.used;
							lp.setUsed(lp.point);
						}
						else {
							lp.setUsed(lp.used+point);
							point = 0L;
						}
						lp.update();
						pointer ++;
					}
					//				Long tobereduced = -1*json.get("loyaltypoint").asLong();
					LoyaltyPoint usedLoyalty = new LoyaltyPoint(actor.id,transactionId,-1*total,0L,null,"Used Point");
					usedLoyalty.save();
					Ebean.commitTransaction();
				}catch(Exception e){
					Ebean.rollbackTransaction();
					Logger.info(e.toString());
//					response.setBaseResponse(0, 0, 0, notFound, null);
//					return badRequest(Json.toJson(response));
					return false;
				}finally {
					Ebean.endTransaction();
				}
//				response.setBaseResponse(1, 0, 1, success, "successfully updated " + pointer +" row(s)");
//				return ok(Json.toJson(response));
				return true;
			}
//			response.setBaseResponse(0, 0, 0, "insufficient point", null);
//			return badRequest(Json.toJson(response));
			return false;
		}
//		response.setBaseResponse(0, 0, 0, unauthorized, null);
//		return unauthorized(Json.toJson(response));
		return false;
	}

	public static boolean addPoint(Long points,Long transactionId,Date expireDate) {
		//	public static Result addPoint() {
		Member actor = checkMemberAccessAuthorization();
		if (actor != null) {
			try{
				Ebean.beginTransaction();
				LoyaltyPoint newLoyalty = new LoyaltyPoint(actor.id,transactionId,points,0L,expireDate,"Got Ponts fron transaction "+transactionId);
				//				LoyaltyPoint newLoyalty = new LoyaltyPoint(actor.id,1612L,500L,0L,"2021-04-17 07:07:22","Got Ponts fron transaction "+1612L);
				newLoyalty.save();
				Ebean.commitTransaction();
			}catch(Exception e) {
				Ebean.rollbackTransaction();
				Logger.info(e.toString());
//				response.setBaseResponse(0, 0, 0, notFound, null);
//				return badRequest(Json.toJson(response));
				return false;
			}finally {
				Ebean.endTransaction();
			}
//			response.setBaseResponse(1, 0, 1, success, "successfully added " + points +" point to member " + actor.id);
//			//			response.setBaseResponse(1, 0, 1, success, "successfully added " + 500L +" point to member " + actor.id);
//			return ok(Json.toJson(response));
			return true;
		}
//		response.setBaseResponse(0, 0, 0, unauthorized, null);
//		return unauthorized(Json.toJson(response));
		return false;
	}

	public  static Result soonExpired() {
		Member actor = checkMemberAccessAuthorization();
		Date currentDate = new Date(System.currentTimeMillis());
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String currentDateString = dateFormat.format(currentDate);
		List<SqlRow> sql = null;
		LoyaltyPoint lp = null;
//		Logger.info("current date string date : " +currentDateString);
		if (actor != null) {
			String query = "SELECT *" +
					" FROM loyaltypoint" +
					" WHERE" +
					" is_deleted = false "
					+ " and member_id = " + actor.id
					+ " and point > 0"
					+ " and expired_date > " + "'"+currentDateString+"'"
					+ " and point - used > 0" +
					" ORDER BY expired_date ASC";
			SqlQuery sqlQuery = Ebean.createSqlQuery(query);
//			SqlQuery sqlQuery = Ebean.createSqlQuery(
//					"SELECT * FROM loyaltypoint WHERE is_deleted = false and member_id = :actorId and point > 0 and expired_date > ' :currentDateString ' and point - used > 0 ORDER BY expired_date ASC");
//			sqlQuery.setParameter("actorId",actor.id);
//			sqlQuery.setParameter("currentDateString",currentDate);
			
			sql = sqlQuery.findList();
			if(sql.size() != 0) {
				lp = LoyaltyPoint.find.byId(sql.get(0).getLong("id"));
				MapLoyaltyPoint mapper = new MapLoyaltyPoint(lp); 
				response.setBaseResponse(1, 0, 1, success, mapper);
				return ok(Json.toJson(response));
			}
			response.setBaseResponse(0, 0, 0, notFound, null);
			return badRequest(Json.toJson(response));
			
		}
		response.setBaseResponse(0, 0, 0, unauthorized, null);
		return unauthorized(Json.toJson(response));
	}
	
	public static Result calculateEligibleLoyaltyPoint()
	{
		long totalEligible = 0, totalCashback = 0;
		Member actor = checkMemberAccessAuthorization();
		if (actor != null) {
            JsonNode json = request().body().asJson();
            ObjectMapper mapper = new ObjectMapper();

            try {
                MapOrder map = mapper.readValue(json.toString(), MapOrder.class);
                for (MapOrderSeller mos : map.getSellers())
                {
                    for (MapOrderDetail mod : mos.getItems())
                    {
                        Product product = Product.find.byId(mod.getProductId());
                		totalEligible += product.getEligiblePointUsed()*mod.getQuantity();
                		totalCashback += product.getEligiblePointEarned()*mod.getQuantity();
//                        check loyalty point eligible to be used
//                        if (product.category.getLoyaltyUsageType() == CategoryLoyalty.NOMINAL)
//                        {
//                        	float value = product.category.getLoyaltyUsageValue();
//                    		Logger.info(String.valueOf(value));
//                        	totalEligible += value * mod.getQuantity();
//                        }
//                        else 
//                        {
//                        	if (product.category.getLoyaltyUsageValue() * product.buyPrice /100 > product.category.getMaxLoyaltyUsageValue() && product.category.getMaxLoyaltyUsageValue() != 0)
//                        	{
//                        		float value = product.category.getMaxLoyaltyUsageValue();
//                        		Logger.info(String.valueOf(value));
//                        		totalEligible += value * mod.getQuantity();
//                        	}
//                        	else
//                        	{
//                        		float value = product.category.getLoyaltyUsageValue() * product.buyPrice.floatValue() /100;
//                        		Logger.info(String.valueOf(value));
//                        		totalEligible += value * mod.getQuantity();
//                        	}
//                        }
                        //check loyalty point eligible to be earned
//                        if (product.category.getCashbackType() == CategoryLoyalty.NOMINAL)
//                        {
//                        	totalCashback += product.category.getCashbackValue() * mod.getQuantity();
//                        }
//                        else 
//                        {
//                        	if (product.category.getCashbackValue() * product.buyPrice /100 > product.category.getMaxCashbackValue() && product.category.getMaxCashbackValue() != 0)
//                        	{
//                        		float value = product.category.getMaxCashbackValue();
//                        		Logger.info(String.valueOf(value));
//                        		totalCashback += value * mod.getQuantity();
//                        	}
//                        	else
//                        	{
//                        		float value = product.category.getCashbackValue() * product.buyPrice.floatValue() /100;
//                        		Logger.info(String.valueOf(value));
//                        		totalCashback += value * mod.getQuantity();
//                        	}
//                        }
                    }
                }
                MapEligibleLoyalty results = new MapEligibleLoyalty(totalEligible, totalCashback);
        		response.setBaseResponse(1, 0, 1, success, results);
        		return ok(Json.toJson(response));
			} catch (Exception e) {
				// TODO: handle exception
                e.printStackTrace();
        		response.setBaseResponse(0, 0, 0, error, null);
        		return unauthorized(Json.toJson(response));
			}
		}
		response.setBaseResponse(0, 0, 0, unauthorized, null);
		return unauthorized(Json.toJson(response));
	}
	
	public static boolean allocateEligiblePoint(Long id, long usedPoint) {
		SalesOrder so = SalesOrder.find.byId(id);
		double orderPrice = 0
    			,orderAfterVoucherDisc = 0
    			,validPoint = 0
        		,deservedUsedPoint = 0
        		,deservedEarnedPoint = 0
        		,deservedEarnedPointReferral = 0
        		,totalDeservedUsedPoints = 0
        		,difference = 0;
		
    	
    	for(SalesOrderSeller sos: so.salesOrderSellers) {
    		for(SalesOrderDetail sod: sos.salesOrderDetail) {
    			orderAfterVoucherDisc += (sod.product.buyPrice*sod.quantity)-sod.voucher;
    			orderPrice += sod.product.getPriceDisplay() * sod.quantity;
    			validPoint += sod.product.getEligiblePointUsed()*sod.quantity;
    		}
    	}

    	Logger.info("Order After Voucher Disc " + orderAfterVoucherDisc);
    	Logger.info("valid point " + validPoint);
    	
    	for(SalesOrderSeller sos: so.salesOrderSellers) {
    		for(SalesOrderDetail sod: sos.salesOrderDetail) {
    			Logger.info("eligible used "+ sod.product.getEligiblePointUsed());
    			Logger.info("quantity "+ sod.quantity);
    			Logger.info("used points "+ usedPoint);
    			
    			if(validPoint==0) {
    				deservedUsedPoint = 0;
    			}
    			else {
    				deservedUsedPoint = sod.product.getEligiblePointUsed()*sod.quantity*usedPoint/validPoint;
    			}
    			sod.setLoyaltyEligibleUse((Long.valueOf((long)deservedUsedPoint)));
//	    		deservedEarnedPoint = sod.product.getEligiblePointEarn(((sod.product.buyPrice*sod.quantity)-sod.voucher)*(so.subtotal-so.shipping)/orderAfterVoucherDisc);	

    			
    			//penambahan point member
    			deservedEarnedPoint = sod.product.getEligiblePointEarn((((sod.product.getPriceDisplay()*sod.quantity)-sod.voucher)-deservedUsedPoint));
    			sod.setLoyaltyEligibleEarn((Long.valueOf((long)deservedEarnedPoint)));
    			
    			//penambahan point referral
    			deservedEarnedPointReferral = sod.product.getEligiblePointEarnReferral((((sod.product.getPriceDisplay()*sod.quantity)-sod.voucher)-deservedUsedPoint));
    			sod.setLoyaltyEligibleEarnReferral((Long.valueOf((long)deservedEarnedPointReferral)));
    			
    			
    	    	Logger.info("deserved Used Point " + deservedUsedPoint);
    	    	Logger.info("deserved Earned Point " + deservedEarnedPoint);
    	    	Logger.info("deserved Earned Point Referral " + deservedEarnedPointReferral);
    	    	Logger.info("SOD ID " + sod.id);	
    			sod.update();
    			totalDeservedUsedPoints += (long)deservedUsedPoint;
    		}
    	}
    	
    	difference = Math.abs(so.getLoyaltyPoint()) - totalDeservedUsedPoints;
    	if(difference > 0) {
    		SalesOrderSeller lastSos = so.salesOrderSellers.get(so.salesOrderSellers.size()-1);
    		SalesOrderDetail lastSod = lastSos.salesOrderDetail.get(lastSos.salesOrderDetail.size()-1);
    		lastSod.setLoyaltyEligibleUse((long)lastSod.loyaltyEligibleUse+(long)difference);
    		lastSod.update();
    	}
    	
    	return true;
    }
	
	public static boolean allocateEligiblePointRedeem(Long id, long usedPoint){
		SalesOrder so = SalesOrder.find.byId(id);
		double 
				orderPrice = 0
//    			,orderAfterVoucherDisc = 0
//    			,validPoint = 0
        		,deservedUsedPoint = 0
        		,deservedEarnedPoint = 0;
//        		,deservedEarnedPointReferral = 0
//        		,totalDeservedUsedPoints = 0
//        		,difference = 0;
		
    	
    	for(SalesOrderSeller sos: so.salesOrderSellers) {
    		for(SalesOrderDetail sod: sos.salesOrderDetail) {
//    			orderAfterVoucherDisc += (sod.product.buyPrice*sod.quantity)-sod.voucher;
    			orderPrice += sod.product.buyPrice * sod.quantity;
//    			validPoint += sod.product.getEligiblePointUsed()*sod.quantity;
    		}
    	}

    	for(SalesOrderSeller sos: so.salesOrderSellers) {
    		for(SalesOrderDetail sod: sos.salesOrderDetail) {
    			deservedUsedPoint = sod.product.buyPrice*sod.quantity*usedPoint/orderPrice;
    			sod.setLoyaltyEligibleUse((Long.valueOf((long)deservedUsedPoint)));
    			sod.setLoyaltyEligibleEarn((Long.valueOf((long)deservedEarnedPoint)));
    			sod.update();
    		}
    	}
    	return true;
	}
	
	public static Result getLoyaltySetting() {
    	DateFormat df = new SimpleDateFormat("yy-MM-dd");
		ConfigSettings configSetting = ConfigSettings.find.where().eq("module", "loyaltysetting").findUnique();

		String value[] = configSetting.value.split("##");
		int status = Integer.parseInt(value[0]);
		Date startDate, endDate;
		try {
    		startDate =  df.parse(value[1]);
    		endDate = df.parse(value[2]);
		} catch (Exception e) {
			// TODO: handle exception
			startDate = new Date();
			endDate = new Date();
		}
		long loyaltyBonus = Long.parseLong(value[3]);
		int expiredDays = Integer.parseInt(value[4]);
		String type = Integer.parseInt(value[5]) == 0 ? "Web" : (Integer.parseInt(value[5]) == 1 ? "Mobile" : "Web & Mobile");

		MapLoyaltySetting setting = new MapLoyaltySetting(startDate, endDate, loyaltyBonus, expiredDays);
		response.setBaseResponse(1, 0, 1, success, setting);
		return ok(Json.toJson(response));
	}
}
