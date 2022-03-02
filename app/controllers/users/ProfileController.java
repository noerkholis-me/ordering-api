package controllers.users;

import assets.JsonMask;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.Transaction;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hokeba.api.BaseResponse;
import com.hokeba.api.UserSession;
import com.hokeba.mapping.response.MapAddress;
import com.hokeba.mapping.response.MapMember;
import com.hokeba.social.requests.MailchimpCustomerRequest;
import com.hokeba.social.service.MailchimpService;
import com.hokeba.util.Secured;
import com.wordnik.swagger.annotations.ApiImplicitParam;
import com.wordnik.swagger.annotations.ApiImplicitParams;
import com.wordnik.swagger.annotations.ApiOperation;
import controllers.BaseController;
import models.*;
import play.Logger;
import play.Play;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Security;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by hendriksaragih on 3/19/17.
 */
public class ProfileController extends BaseController {
    @SuppressWarnings("rawtypes")
    private static BaseResponse response = new BaseResponse();
    private static final String featureKey = "profile";

    public static Result index() {
        return ok();
    }

    public static Result getMemberProfile() throws JsonProcessingException {
        Member actor = checkMemberAccessAuthorization();
        if (actor != null) {
            ObjectMapper om = new ObjectMapper();
            om.addMixInAnnotations(Member.class, JsonMask.class);
            response.setBaseResponse(1, offset, 1, success, Json.parse(om.writeValueAsString(actor)));
            return ok(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }


    public static Result myAccount() throws JsonProcessingException {
        Member actor = checkMemberAccessAuthorization();
        if (actor != null) {
            actor.shippingAddress = Address.getPrimaryAddress(actor.id, Address.SHIPPING_ADDRESS);
            actor.billingAddress = Address.getPrimaryAddress(actor.id, Address.BILLING_ADDRESS);
            actor.orders = SalesOrder.getOrderByMember(actor.id);
            response.setBaseResponse(1, offset, 1, success, new ObjectMapper().convertValue(actor, MapMember.class));
            return ok(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    //fullname,gender,birthdate
    public static Result updateProfile() {
        Member actor = checkMemberAccessAuthorization();
        if (actor != null) {
            JsonNode json = request().body().asJson();
            if (json.has("full_name")) {
            	String gender = json.has("gender") ? json.findPath("gender").asText() : "";
                String birthDate = json.has("birth_date") ? json.findPath("birth_date").asText() : "";
                String phone = json.has("phone") ? json.findPath("phone").asText() : "";
                String fullName = json.findPath("full_name").asText();
                String validation = Member.updateProfileValidation(actor.id, fullName, phone);
                if (validation == null) {
                    Transaction txn = Ebean.beginTransaction();
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        Member model = Member.find.byId(actor.id);
                        model.phone = !phone.isEmpty() ? phone : null;
                        model.fullName = json.findPath("full_name").asText();
                        model.gender = json.findPath("gender").asText();
                        model.birthDate = new SimpleDateFormat("yyyy-MM-dd").parse(json.findPath("birth_date").asText());
                        model.update();
                        txn.commit();
                        
                        //mailchimp
                        mailchimpAddOrUpdateCustomer(model);
                        
                        String apiKey = request().headers().get(API_KEY)[0];
                        String token = request().headers().get(TOKEN)[0];
                        MemberLog log = MemberLog.isMemberAuthorized(token, apiKey);
                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                        UserSession session = new UserSession(log.token, df.format(log.expiredDate), log.memberType);
                        ObjectMapper om = new ObjectMapper();
                        om.addMixInAnnotations(Member.class, JsonMask.class);
                        try {
                            session.setProfile_data(Json.parse(om.writeValueAsString(model)));
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                        }

                        response.setBaseResponse(1, offset, 1, updated, session);
                        return ok(Json.toJson(response));
                    } catch (Exception e) {
                        e.printStackTrace();
                        txn.rollback();
                    } finally {
                        txn.end();
                    }
                }
                response.setBaseResponse(0, 0, 0, validation, null);
                return badRequest(Json.toJson(response));
            }
            response.setBaseResponse(0, 0, 0, inputParameter, null);
            return badRequest(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

	@Security.Authenticated(Secured.class)
    public static Result updateProfileHello() {
		JsonNode json = request().body().asJson();

		Long id = json.has("id") ? json.get("id").asLong() : 0L;
		
		Member actor = Member.find.byId(id);
    	if (actor != null) {
    		if (json.has("full_name")) {
    			String phone = json.has("phone") ? json.findPath("phone").asText() : "";
    			String fullName = json.findPath("full_name").asText();
    			String validation = Member.updateProfileValidation(actor.id, fullName, phone);
    			if (validation == null) {
    				Transaction tx = Ebean.beginTransaction();
    				try {
    					Member model = Member.find.byId(actor.id);
    					model.phone = !phone.isEmpty() ? phone : null;
    					model.fullName = json.findPath("full_name").asText();
//    					model.gender = json.findPath("gender").asText();
//    					model.birthDate = new SimpleDateFormat("yyyy-MM-dd").parse(json.findPath("birth_date").asText());
    					model.update();
    					tx.commit();
    					
    					//mailchimp
//    					mailchimpAddOrUpdateCustomer(model);
    					
//    					String apiKey = request().headers().get("X-API-KEY")[0];
//    					String token = request().headers().get("X-API-TOKEN")[0];
    					
    					UserSession session = new UserSession(SessionsController.createToken(model), null, null);
    					ObjectMapper om = new ObjectMapper();
    					om.addMixInAnnotations(Member.class, JsonMask.class);
    					try {
    						session.setProfile_data(Json.parse(om.writeValueAsString(model)));
    					} catch (JsonProcessingException e) {
    						e.printStackTrace();
    					}
    					
    					response.setBaseResponse(1, offset, 1, updated, session);
    					return ok(Json.toJson(response));
    				} catch (Exception e) {
    					if (tx.isActive())
    						tx.rollback();
    					e.printStackTrace();
    				} finally {
    					if (tx.isActive())
    						tx.end();
    				}
    			}
    			response.setBaseResponse(0, 0, 0, validation, null);
    			return badRequest(Json.toJson(response));
    		}
    		response.setBaseResponse(0, 0, 0, inputParameter, null);
    		return badRequest(Json.toJson(response));
    	}
    	response.setBaseResponse(0, 0, 0, notFound, null);
    	return notFound(Json.toJson(response));
    }
    
    @ApiOperation(value = "Create new address.", notes = "Create new address.\n" + swaggerInfo + "\n\n"
            + swaggerParamInfo + "", response = Address.class, httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "body", dataType = "Address", required = true, paramType = "body", value = "Address data")})
    public static Result createAddress() {
        Member currentMember = checkMemberAccessAuthorization();
        if (currentMember != null) {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode json = request().body().asJson();
            if (json.has("type") && json.has("address") && json.has("phone")) {
                Transaction txn = Ebean.beginTransaction();
                try {
                    Address model = mapper.readValue(json.toString(), Address.class);
                    
                    // province -> regions
                    if (json.has("regions")){
                        model.region = Region.find.byId(json.get("regions").asLong(0));
                    }
                    // city -> district 
                    if (json.has("districts")){
                        model.district = District.find.byId(json.get("districts").asLong(0));
                    }                    

                    // subdistrict -> township 
                    if (json.has("city")){
                        model.township = Township.find.byId(json.get("city").asLong(0));
                    } 
//                  model.township = Township.find.byId(json.get("city").asLong(0));
                    
//                    if (model.villages != null){
//                        model.village = Village.find.byId(json.get("villages").asLong(0));
//                    }
                    
                    model.member = currentMember;
                    String check = Address.validation(model);
                    if (check != null) {
                        response.setBaseResponse(0, 0, 0, check, null);
                        return badRequest(Json.toJson(response));
                    }
                    model.save();
                    if (model.isPrimary){
                        Address.find.where()
                                .eq("isPrimary", true)
                                .eq("type", model.type)
                                .ne("id", model.id)
                                .eq("member_id", currentMember.id)
                                .findList().forEach(a->{
                            a.isPrimary = false;
                            a.update();
                        });
                    }
                    txn.commit();
                    response.setBaseResponse(1, offset, 1, created, new MapAddress(model));
                    return ok(Json.toJson(response));
                } catch (IOException e) {
                    Logger.error("create", e);
                    txn.rollback();
                }finally {
                    txn.end();
                }
                response.setBaseResponse(0, 0, 0, error, null);
                return badRequest(Json.toJson(response));
            }
            response.setBaseResponse(0, 0, 0, inputParameter, null);
            return badRequest(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));

    }

    @ApiOperation(value = "Update an address with specific id.", notes = "Update an address with specific id.\n"
            + swaggerInfo + "\n\n" + swaggerParamInfo + "", response = Address.class, httpMethod = "PATCH")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "address_id", dataType = "number", required = true, paramType = "path", value = "address id"),
            @ApiImplicitParam(name = "body", dataType = "Address", required = true, paramType = "body", value = "Address data")})
    public static Result updateAddress(Long id) {
        Member currentMember = checkMemberAccessAuthorization();
        if (currentMember != null) {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode json = request().body().asJson();
            if (json.has("type") && json.has("address") && json.has("phone")) {
                Transaction txn = Ebean.beginTransaction();
                try {
                    Address model = mapper.readValue(json.toString(), Address.class);

                    Address savedModel = Address.find.byId(id);
                    if (savedModel != null && Objects.equals(savedModel.member.id, currentMember.id)) {
                        model.id = id;
                        
//                        if (json.has("province")){
//                            model.district = District.find.byId(json.get("province").asLong(0));
//                        }                    
//                        model.township = Township.find.byId(json.get("city").asLong(0));
//                        
//                        if (model.regions != null){
//                            model.region = Region.find.byId(json.get("regions").asLong(0));
//                        }
//                        
//                        if (model.villages != null){
//                            model.village = Village.find.byId(json.get("villages").asLong(0));
//                        }
                        
                        // province -> regions
                        if (json.has("regions")){
                            model.region = Region.find.byId(json.get("regions").asLong(0));
                        }
                        // city -> district 
                        if (json.has("districts")){
                            model.district = District.find.byId(json.get("districts").asLong(0));
                        }     
                        
                        // subdistrict -> township 
                        if (json.has("city")){
                            model.township = Township.find.byId(json.get("city").asLong(0));
                        } 
//                      model.township = Township.find.byId(json.get("city").asLong(0));
                        
                        String check = Address.validation(model);
                        if (check != null) {
                            response.setBaseResponse(0, 0, 0, check, null);
                            return badRequest(Json.toJson(response));
                        }

                        model.update();
                        if (model.isPrimary){
                            Address.find.where()
                                    .eq("isPrimary", true)
                                    .eq("type", model.type)
                                    .ne("id", model.id)
                                    .eq("member_id", currentMember.id)
                                    .eq("is_deleted", false)
                                    .findList().forEach(a->{
                                a.isPrimary = false;
                                a.update();
                            });
                        }

                        txn.commit();
                        response.setBaseResponse(1, offset, 1, updated, new MapAddress(model));
                        return ok(Json.toJson(response));

                    }
                    response.setBaseResponse(0, 0, 0, notFound, null);
                    return notFound(Json.toJson(response));
                } catch (IOException e) {
                    Logger.error("update", e);
                    txn.rollback();
                } finally {
                    txn.end();
                }
                response.setBaseResponse(0, 0, 0, error, null);
                return badRequest(Json.toJson(response));
            }
            response.setBaseResponse(0, 0, 0, inputParameter, null);
            return badRequest(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result updatePrimaryAddress(Long id) {
        Member currentMember = checkMemberAccessAuthorization();
        if (currentMember != null) {
            Transaction txn = Ebean.beginTransaction();
            try {
                Address savedModel = Address.find.byId(id);
                if (savedModel != null && Objects.equals(savedModel.member.id, currentMember.id)) {
                    savedModel.isPrimary = true;
                    savedModel.update();

                    Address.find.where().eq("isPrimary", true)
                            .eq("type", savedModel.type)
                            .eq("is_deleted", false)
                            .eq("member_id", currentMember.id)
                            .ne("id", savedModel.id)
                            .findList().forEach(a->{
                        a.isPrimary = false;
                        a.update();
                    });

                    txn.commit();
                    response.setBaseResponse(1, offset, 1, updated, null);
                    return ok(Json.toJson(response));
                }
                response.setBaseResponse(0, 0, 0, notFound, null);
                return notFound(Json.toJson(response));
            } catch (Exception e) {
                Logger.error("update", e);
                txn.rollback();
            } finally {
                txn.end();
            }
            response.setBaseResponse(0, 0, 0, error, null);
            return badRequest(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result addressLists(Integer type) throws JsonProcessingException {
        Member actor = checkMemberAccessAuthorization();
        if (actor != null) {
            List<Address> data = Address.find.where()
                    .eq("member_id", actor.id)
                    .eq("is_deleted", false)
                    .eq("type", type)
                    .orderBy("is_primary DESC, created_at DESC")
                    .findList();
            
            List<MapAddress> listAddress = new ArrayList<MapAddress>();
            data.forEach((d) -> {
            	listAddress.add(new MapAddress(d));
    		});

            response.setBaseResponse(data.size(), offset, data.size(), success, listAddress);
            return ok(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    @ApiOperation(value = "Get detail of an address with specific id.", notes = "Get detail of an address with specific id.\n"
            + swaggerInfo + "", response = Address.class, httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "address_id", dataType = "number", required = true, paramType = "path", value = "address id")})
    public static Result addressDetail(Long id) {
        Member currentMember = checkMemberAccessAuthorization();
        if (currentMember != null) {
            Address model = Address.find.byId(id);
            if (model != null && Objects.equals(model.member.id, currentMember.id)) {
                response.setBaseResponse(1, offset, 1, success, new MapAddress(model));
                return ok(Json.toJson(response));
            }
            response.setBaseResponse(0, 0, 0, notFound, null);
            return notFound(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    @ApiOperation(value = "Delete an address with specific id.", notes = "Delete an address with specific id.", response = BaseResponse.class, httpMethod = "DELETE")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "address_id", dataType = "number", required = true, paramType = "path", value = "address id")})
    public static Result deleteAddress(Long id) {
        Member currentMember = checkMemberAccessAuthorization();
        if (currentMember != null) {
            Address model = Address.find.byId(id);
            if (model != null && Objects.equals(model.member.id, currentMember.id)) {
                Transaction txn = Ebean.beginTransaction();
                try {
                    model.isDeleted = true; // SOFT DELETE
                    model.save();

                    if (model.isPrimary){
                        Address last = Address.find.where()
                                .eq("is_deleted", false)
                                .eq("member_id", currentMember.id)
                                .eq("type", model.type)
                                .orderBy("created_at DESC").setMaxRows(1).findUnique();
                        if (last != null){
                            last.isPrimary = true;
                            last.update();
                        }
                    }
                    txn.commit();
                    response.setBaseResponse(1, offset, 1, deleted, null);
                    return ok(Json.toJson(response));
                }
                catch (Exception e) {
                    Logger.error("update", e);
                    txn.rollback();
                } finally {
                    txn.end();
                }
            }
            response.setBaseResponse(0, 0, 0, notFound, null);
            return notFound(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }
    
    public static void mailchimpAddOrUpdateCustomer(Member member) {
    	boolean mailchimpEnabled = Play.application().configuration().getBoolean("whizliz.social.mailchimp.enabled");
        if (mailchimpEnabled) {
            MailchimpCustomerRequest request = new MailchimpCustomerRequest(member);
            MailchimpService.getInstance().AddOrUpdateCustomer(request);
        }
    }

    
    public static Result registerWithReferral() {
    	Member actor = checkMemberAccessAuthorization();
        if (actor != null) {
            Member data = Member.find.where().eq("email", actor.email).setMaxRows(1).findUnique();
            if (data != null){
                String referral_code = data.referral_code;
                String link_referral_code = Play.application().configuration().getString("whizliz.frontend.url");
                String link = link_referral_code+"/register?ref="+referral_code;
                       
                response.setBaseResponse(1, offset, 1, success, link);
                return ok(Json.toJson(response));
            }
            response.setBaseResponse(0, 0, 0, notFound, null);
            return forbidden(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }
}
