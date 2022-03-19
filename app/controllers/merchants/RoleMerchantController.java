package controllers.merchants;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Transaction;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hokeba.api.BaseResponse;
import com.hokeba.util.CommonFunction;
import com.hokeba.util.Encryption;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiImplicitParam;
import com.wordnik.swagger.annotations.ApiImplicitParams;
import com.wordnik.swagger.annotations.ApiOperation;
import controllers.BaseController;
import dtos.role.*;
import models.Merchant;
import models.RoleMerchant;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import repository.RoleMerchantRepository;
import controllers.BaseController;
import java.text.SimpleDateFormat;
import java.util.*;
import com.avaje.ebean.Query;

import java.io.IOException;

@Api(value = "/merchants/role", description = "Role Merchant")
public class RoleMerchantController extends BaseController {

    private final static Logger.ALogger logger = Logger.of(RoleMerchantController.class);

    private static BaseResponse response = new BaseResponse();

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @ApiOperation(value = "Create Role", notes = "Create Role.\n" + swaggerInfo
            + "", response = BaseResponse.class, httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "role form", dataType = "temp.swaggermap.RoleForm", required = true, paramType = "body", value = "role form") })
    public static Result createRole() {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            JsonNode json = request().body().asJson();
            try {
                RoleMerchantResponse request = objectMapper.readValue(json.toString(), RoleMerchantResponse.class);
                String validate = validateCreateRole(request);
                if (validate == null) {
                    Transaction trx = Ebean.beginTransaction();
                    try {
                        RoleMerchant newRoleMerchant = new RoleMerchant();
                        newRoleMerchant.setName(request.getName());
                        newRoleMerchant.setDescription(request.getDescription());
                        newRoleMerchant.setKey(request.getName().toLowerCase().replace(' ', '_'));
//                        newRoleMerchant.setIsDeleted(Boolean.FALSE);
                        newRoleMerchant.setMerchant(ownMerchant);
                        newRoleMerchant.save();

                        // FeatureMerchant newFeatureMerchant = newFeatureMerchant();

                        trx.commit();

                        response.setBaseResponse(1, offset, 1, success + " creating new role", newRoleMerchant);
                        return ok(Json.toJson(response));
                    } catch (Exception e) {
                        logger.error("Error while creating role", e);
                        e.printStackTrace();
                        trx.rollback();
                    } finally {
                        trx.end();
                    }
                    response.setBaseResponse(0, 0, 0, error, null);
                    return badRequest(Json.toJson(response));
                }
                response.setBaseResponse(0, 0, 0, validate, null);
                return badRequest(Json.toJson(response));
            } catch (IOException e) {
                logger.error("Error while parsing json", e);
                e.printStackTrace();
            }
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static String validateCreateRole(RoleMerchantResponse request) {
        if (request == null)
            return "Field must not null or empty";
        if (request.getName() == null)
            return "Name must not null or empty";
        if (request.getDescription() == null)
            return "Description must not null or empty.";

        return null;
    }

    @ApiOperation(value = "Get all role list.", notes = "Returns list of role.\n" + swaggerInfo
            + "", response = RoleMerchant.class, responseContainer = "List", httpMethod = "GET")
    public static Result listRole(String filter, String sort, int offset, int limit) {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            Query<RoleMerchant> query = RoleMerchantRepository.find.where().eq("t0.is_deleted", false).eq("t0.merchant_id", getUserMerchant().id).order("t0.id");
            try {
                List<RoleMerchantResponse> responses = new ArrayList<>();
                List<RoleMerchant> totalData = RoleMerchantRepository.getTotalData(query);
                List<RoleMerchant> responseIndex = RoleMerchantRepository.getDataRole(query, sort, filter, offset, limit);
                for (RoleMerchant data : responseIndex) {
                    RoleMerchantResponse response = new RoleMerchantResponse();

                    response.setId(data.id);
                    response.setName(data.getName());
                    response.setDescription(data.getDescription());
                    response.setKey(data.getKey());
//                    response.setIsDeleted(data.getIsDeleted());
                    response.setMerchantId(data.getMerchantId());
                    // response.setMerchant(data.getMerchant());
                    responses.add(response);
                }
                response.setBaseResponse(filter == null || filter == "" ? totalData.size() : responseIndex.size() , offset, limit, success + " showing data", responses);
                // System.out.println(ok(Json.toJson(response)));
                return ok(Json.toJson(response));
            } catch (IOException e) {
                Logger.error("allDetail", e);
            }
        } else if (ownMerchant == null) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    @ApiOperation(value = "Edit Role", notes = "Edit Role.\n" + swaggerInfo
            + "", response = BaseResponse.class, httpMethod = "PUT")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "role form", dataType = "temp.swaggermap.RoleForm", required = true, paramType = "body", value = "role form") })
    public static Result editRole(Long id) {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            JsonNode json = request().body().asJson();
            try {
                RoleMerchantResponse request = objectMapper.readValue(json.toString(), RoleMerchantResponse.class);
                String validate = validateCreateRole(request);
                if (validate == null) {
                    Transaction trx = Ebean.beginTransaction();
                    try {
                        RoleMerchant roleMerchant = RoleMerchantRepository.findByIdAndMerchantId(id, ownMerchant.id);
                        if (roleMerchant == null) {
                            response.setBaseResponse(0, 0, 0, error + " role merchant not found.", null);
                            return badRequest(Json.toJson(response));
                        }
                        
                        
                        roleMerchant.setName(request.getName());
                        roleMerchant.setDescription(request.getDescription());
                        roleMerchant.setKey(request.getName().toLowerCase().replace(' ', '_'));
//                        roleMerchant.id(id);
                        roleMerchant.update();
                        trx.commit();

                        response.setBaseResponse(1,offset, 1, success + " updating role", roleMerchant);
                        return ok(Json.toJson(response));
                    } catch (Exception e) {
                        logger.error("Error while updating role", e);
                        e.printStackTrace();
                        trx.rollback();
                    } finally {
                        trx.end();
                    }
                    response.setBaseResponse(0, 0, 0, error, null);
                    return badRequest(Json.toJson(response));
                }
                response.setBaseResponse(0, 0, 0, validate, null);
                return badRequest(Json.toJson(response));
            } catch (IOException e) {
                logger.error("Error while parsing json", e);
                e.printStackTrace();
            }
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    @ApiOperation(value = "Delete Role", notes = "Delete Role.\n" + swaggerInfo
            + "", response = BaseResponse.class, httpMethod = "DELETE")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "role form", dataType = "temp.swaggermap.RoleForm", required = true, paramType = "body", value = "role form") })
    public static Result deleteRole(Long id) {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
                if (id != null) {
                    Transaction trx = Ebean.beginTransaction();
                    try {
                        RoleMerchant roleMerchant = RoleMerchantRepository.findByIdAndMerchantId(id, ownMerchant.id);
                        if (roleMerchant == null) {
                            response.setBaseResponse(0, 0, 0, error + " role merchant not found.", null);
                            return badRequest(Json.toJson(response));
                        }
                        
                        
                        roleMerchant.isDeleted = true;
//                        roleMerchant.id(id);
                        roleMerchant.update();
                        trx.commit();

                        response.setBaseResponse(1,offset, 1, success + " deleting role", roleMerchant);
                        return ok(Json.toJson(response));
                    } catch (Exception e) {
                        logger.error("Error while deleting role", e);
                        e.printStackTrace();
                        trx.rollback();
                    } finally {
                        trx.end();
                    }
                    response.setBaseResponse(0, 0, 0, error, null);
                    return badRequest(Json.toJson(response));
                }
                response.setBaseResponse(0, 0, 0, "Cannot find role Id", null);
                return badRequest(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    @ApiOperation(value = "Read Role", notes = "Read Role.\n" + swaggerInfo
            + "", response = BaseResponse.class, httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "role form", dataType = "temp.swaggermap.RoleForm", required = true, paramType = "body", value = "role form") })
    public static Result viewRole(Long id) {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
                if (id != null) {
                    Transaction trx = Ebean.beginTransaction();
                    try {
                        RoleMerchant roleMerchant = RoleMerchantRepository.findByIdAndMerchantId(id, ownMerchant.id);
                        if (roleMerchant == null) {
                            response.setBaseResponse(0, 0, 0, error + " role merchant not found.", null);
                            return badRequest(Json.toJson(response));
                        }
                        
                        
                        // roleMerchant.setId(data.getId());
                        // roleMerchant.setName(data.getName());
                        // roleMerchant.setDescription(data.getDescription());
                        // roleMerchant.setKey(data.getKey());
                        // roleMerchant.setIsDeleted(data.getIsDeleted());
                        // roleMerchant.setMerchantId(data.getMerchantId());
                        // roleMerchant.add(roleMerchant);
                        // trx.commit();

                        response.setBaseResponse(1,offset, 1, success + " showing detail role", roleMerchant);
                        return ok(Json.toJson(response));
                    } catch (Exception e) {
                        logger.error("Error while showing detail role", e);
                        e.printStackTrace();
                        trx.rollback();
                    } finally {
                        trx.end();
                    }
                    response.setBaseResponse(0, 0, 0, error, null);
                    return badRequest(Json.toJson(response));
                }
                response.setBaseResponse(0, 0, 0, "Cannot find role Id", null);
                return badRequest(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

}
