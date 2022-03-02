package controllers.merchants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import controllers.BaseController;
import models.Merchant;
import models.PickUpPoint;

import com.avaje.ebean.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hokeba.api.BaseResponse;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.hokeba.mapping.request.MapPickUpPoint;
import com.hokeba.mapping.response.MapPickUpPointRes;

import play.Logger;
import play.libs.Json;
import play.mvc.Result;

@Api(value = "/merchants/pickup", description = "Pick Up Point")
public class PickUpPointController extends BaseController  {

    @SuppressWarnings("rawtypes")
    private static BaseResponse response = new BaseResponse();
    
    public static Result addEntry() {
    	Merchant actor = checkMerchantAccessAuthorization();
        if (actor != null) {
        	JsonNode json = request().body().asJson();
            ObjectMapper mapper = new ObjectMapper();
            try {
            	Ebean.beginTransaction();
            	MapPickUpPoint map = mapper.readValue(json.toString(), MapPickUpPoint.class);
            	PickUpPoint newEntry = new PickUpPoint(map);
            	newEntry.save();
            	Ebean.commitTransaction();
                response.setBaseResponse(1, offset, 1, success, 1);
                return ok(Json.toJson(response));
            } catch (IOException e) {
                e.printStackTrace();
            	Ebean.rollbackTransaction();
            }
            finally {
            	Ebean.endTransaction();
            }
	    	response.setBaseResponse(0, 0, 0, error, null);
	    	return badRequest(Json.toJson(response));  
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }
    
    public static Result list() {
    	Merchant actor = checkMerchantAccessAuthorization();
    	if (actor != null) {
    		List<PickUpPoint> pickups = PickUpPoint.find.where()
    				.eq("is_deleted",false)
//    				.or(Expr.or("merchant_id",actor.id),Expr.or("merchant_id",null)
    				.raw("merchant_id = " + actor.id + " or merchant_id is null")
//    				.in("merchant_id",actor.id,null)
    				.orderBy("created_at DESC")
    				.findList();
    		
    		List<MapPickUpPointRes> maps = new ArrayList<MapPickUpPointRes>();
    		for(PickUpPoint points : pickups) {
    			maps.add(new MapPickUpPointRes(points));
    		}
    		response.setBaseResponse(maps.size(), offset, maps.size(), success, maps);
    		return ok(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));    	
    }
    
    public static Result getPickUpPoint(Long id) {
    	Merchant actor = checkMerchantAccessAuthorization();
    	if (actor != null) {
            PickUpPoint point = PickUpPoint.find.where()
            		.eq("is_deleted",false)
            		.eq("id",id)
            		.setMaxRows(1)
            		.findUnique();
            if(point!= null) {
            	MapPickUpPointRes map = new MapPickUpPointRes(point);
            	response.setBaseResponse(1, offset, 1, success, map);
            	return ok(Json.toJson(response));
            }
            response.setBaseResponse(0, 0, 0, notFound, null);
            return badRequest(Json.toJson(response));    
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));    	
    }
    
    public static Result editEntry(Long id) {
    	Merchant actor = checkMerchantAccessAuthorization();
    	if (actor != null) {
        	JsonNode json = request().body().asJson();
            ObjectMapper mapper = new ObjectMapper();
    		try {
    			PickUpPoint pickup = PickUpPoint.find.byId(id);
    			Ebean.beginTransaction();
    			
    			//edit transaction here
            	MapPickUpPoint map = mapper.readValue(json.toString(), MapPickUpPoint.class);
            	pickup.editPickUp(map);
    			pickup.update();
    			
    			Ebean.commitTransaction();
    			
    	    	response.setBaseResponse(1, 0, 1, updated, null);
    	    	return ok(Json.toJson(response));    
    		}
    		catch(Exception e) {
    			Logger.info(e.toString());
    			Ebean.rollbackTransaction();
    		}
    		finally {
    			Ebean.endTransaction();
    		}
	    	response.setBaseResponse(0, 0, 0, error, null);
	    	return badRequest(Json.toJson(response));    
    	}
    	response.setBaseResponse(0, 0, 0, unauthorized, null);
    	return unauthorized(Json.toJson(response));        	
    }
    
    public static Result removeEntry(Long id) {
    	Merchant actor = checkMerchantAccessAuthorization();
    	if (actor != null) {
    		try {
    			PickUpPoint pickup = PickUpPoint.find.byId(id);
    			
    			if(pickup.merchant == null) { // can't delete "general" pick up point
    				response.setBaseResponse(0, 0, 0, forbidden, null);
    		    	return unauthorized(Json.toJson(response));
    			}
    			
    			Ebean.beginTransaction();
    			pickup.isDeleted = true;
    			pickup.update();
    			Ebean.commitTransaction();
    			
    	    	response.setBaseResponse(1, 0, 1, deleted, null);
    	    	return ok(Json.toJson(response));    
    		}
    		catch(Exception e) {
    			Logger.info(e.toString());
    			Ebean.rollbackTransaction();
    		}
    		finally {
    			Ebean.endTransaction();
    		}
	    	response.setBaseResponse(0, 0, 0, error, null);
	    	return badRequest(Json.toJson(response));    
    	}
    	response.setBaseResponse(0, 0, 0, unauthorized, null);
    	return unauthorized(Json.toJson(response));    
    }
}
