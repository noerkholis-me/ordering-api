package controllers.merchants;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Transaction;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hokeba.api.BaseResponse;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiImplicitParam;
import com.wordnik.swagger.annotations.ApiImplicitParams;
import com.wordnik.swagger.annotations.ApiOperation;
import controllers.BaseController;
import dtos.feature.FeatureAssignRequest;
import dtos.role.*;
import models.Feature;
import models.Merchant;
import models.RoleMerchant;
import models.RoleMerchantFeature;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import repository.RoleMerchantRepository;

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
                RoleMerchantRequest request = objectMapper.readValue(json.toString(), RoleMerchantRequest.class);
                String validate = validateCreateRole(request);
                if (validate == null) {
                    Transaction trx = Ebean.beginTransaction();
                    try {
                        RoleMerchant newRoleMerchant = new RoleMerchant();
                        newRoleMerchant.setName(request.getName());
                        newRoleMerchant.setDescription(request.getDescription());
                        newRoleMerchant.setKey(request.getName().toLowerCase().replace(' ', '_'));
                        newRoleMerchant.setMerchant(ownMerchant);
                        newRoleMerchant.setCashier(request.getIsCashier());
                        newRoleMerchant.setActive(Boolean.TRUE);
                        newRoleMerchant.save();
                        // ============================ start to set role feature ============================ //
                        if (request.getFeatures() != null) {
                            for (FeatureAssignRequest featureAssignRequest : request.getFeatures()) {
                                Feature feature = Feature.find.where().eq("id", featureAssignRequest.getFeatureId()).findUnique();
                                RoleMerchantFeature newRoleMerchantFeature = new RoleMerchantFeature();
                                newRoleMerchantFeature(newRoleMerchant, newRoleMerchantFeature, featureAssignRequest, feature);
                            }
                        }
                        // ============================ end to set role feature ============================ //
                        newRoleMerchant.update();
                        trx.commit();
                        response.setBaseResponse(1, offset, 1, success + " creating new role", null);
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

    private static void newRoleMerchantFeature(RoleMerchant newRoleMerchant, RoleMerchantFeature roleMerchantFeature, FeatureAssignRequest featureAssignRequest, Feature feature) {
        roleMerchantFeature.setFeature(feature);
        roleMerchantFeature.setRoleMerchant(newRoleMerchant);
        roleMerchantFeature.setIsView(featureAssignRequest.getIsView());
        roleMerchantFeature.setIsAdd(featureAssignRequest.getIsAdd());
        roleMerchantFeature.setIsEdit(featureAssignRequest.getIsEdit());
        roleMerchantFeature.setIsDelete(featureAssignRequest.getIsDelete());
        roleMerchantFeature.save();
    }

    public static String validateCreateRole(RoleMerchantRequest request) {
        if (request == null)
            return "Field must not null or empty";
        if (request.getName() == null)
            return "Name must not null or empty";
        if (request.getDescription() == null)
            return "Description must not null or empty.";
        if (request.getIsCashier() == null)
            return "Is Cashier must not null or empty.";

        return null;
    }

    @ApiOperation(value = "Get all role list.", notes = "Returns list of role.\n" + swaggerInfo
            + "", response = RoleMerchant.class, responseContainer = "List", httpMethod = "GET")
    public static Result listRole(String filter, String sort, int offset, int limit) {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            Query<RoleMerchant> query = RoleMerchantRepository.find.where().eq("isDeleted", false).eq("merchant", ownMerchant).order("id");
            try {
                List<RoleMerchantResponse> responses = new ArrayList<>();
                List<RoleMerchant> totalData = RoleMerchantRepository.getTotalData(query);
                List<RoleMerchant> responseIndex = RoleMerchantRepository.getDataRole(query, sort, filter, offset, limit);
                for (RoleMerchant data : responseIndex) {
                    RoleMerchantResponse response = new RoleMerchantResponse();
                    List<FeatureAssignRequest> featureAssignResponses = new ArrayList<>();
                    List<RoleMerchantFeature> roleMerchantFeatures = RoleMerchantFeature.getFeaturesByRole(data.id);
                    if(roleMerchantFeatures != null) {
                        featureAssignResponses = toFeaturesResponse(roleMerchantFeatures);
                    }
                    response.setId(data.id);
                    if(data.isCashier()){
                        response.setName(data.getName()+" (POS)");
                    } else {
                        response.setName(data.getName());
                    }
                    response.setDescription(data.getDescription());
                    response.setKey(data.getKey());
                    response.setMerchantId(data.getMerchant().id);
                    response.setFeatures(featureAssignResponses);
                    response.setIsCashier(data.isCashier());
                    responses.add(response);
                }
                response.setBaseResponse(filter == null || filter.equals("") ? totalData.size() : responseIndex.size() , offset, limit, success + " showing data", responses);
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

    private static List<FeatureAssignRequest> toFeaturesResponse(List<RoleMerchantFeature> roleMerchantFeatures) {
        if (roleMerchantFeatures.size() == 0) {
            return null;
        } else {
            List<FeatureAssignRequest> featureAssignRequests = new ArrayList<>();
            for (RoleMerchantFeature roleMerchantFeature : roleMerchantFeatures) {
                FeatureAssignRequest featureAssignRequest = new FeatureAssignRequest();
                Feature feature = Feature.find.where().eq("t0.id", roleMerchantFeature.featureId).findUnique();
                featureAssignRequest.setFeatureId(feature.id);
                featureAssignRequest.setFeatureName(feature.name);
                featureAssignRequest.setKey(feature.key);
                featureAssignRequest.setIsView(roleMerchantFeature.getIsView());
                featureAssignRequest.setIsAdd(roleMerchantFeature.getIsAdd());
                featureAssignRequest.setIsEdit(roleMerchantFeature.getIsEdit());
                featureAssignRequest.setIsDelete(roleMerchantFeature.getIsDelete());
                featureAssignRequests.add(featureAssignRequest);
            }
            return featureAssignRequests;
        }
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
                RoleMerchantRequest request = objectMapper.readValue(json.toString(), RoleMerchantRequest.class);
                String validate = validateCreateRole(request);
                if (validate == null) {
                    Transaction trx = Ebean.beginTransaction();
                    try {
                        RoleMerchant roleMerchant = RoleMerchantRepository.findByIdAndMerchantId(id, ownMerchant);
                        if (roleMerchant == null) {
                            response.setBaseResponse(0, 0, 0, error + " role merchant not found.", null);
                            return badRequest(Json.toJson(response));
                        }
                        roleMerchant.setName(request.getName());
                        roleMerchant.setDescription(request.getDescription());
                        roleMerchant.setKey(request.getName().toLowerCase().replace(' ', '_'));
                        roleMerchant.setMerchant(ownMerchant);
                        roleMerchant.setActive(Boolean.TRUE);
                        roleMerchant.setCashier(request.getIsCashier());
                        // ============================ start to set role feature ============================ //
                        if (request.getFeatures() != null) {
                            for (FeatureAssignRequest featureAssignRequest : request.getFeatures()) {
                                Feature feature = Feature.find.where().eq("id", featureAssignRequest.getFeatureId()).findUnique();
                                RoleMerchantFeature roleMerchantFeature = RoleMerchantFeature.find.where().eq("roleMerchant", roleMerchant).eq("feature", feature).findUnique();
                                if (roleMerchantFeature == null) {
                                    RoleMerchantFeature newRoleMerchantFeature = new RoleMerchantFeature();
                                    newRoleMerchantFeature(roleMerchant, newRoleMerchantFeature, featureAssignRequest, feature);
                                } else {
                                    roleMerchantFeature.setFeature(feature);
                                    roleMerchantFeature.setRoleMerchant(roleMerchant);
                                    roleMerchantFeature.setIsView(featureAssignRequest.getIsView());
                                    roleMerchantFeature.setIsAdd(featureAssignRequest.getIsAdd());
                                    roleMerchantFeature.setIsEdit(featureAssignRequest.getIsEdit());
                                    roleMerchantFeature.setIsDelete(featureAssignRequest.getIsDelete());
                                    roleMerchantFeature.save();
                                }
                            }


                        }
                        // ============================ end to set role feature ============================ //
                        roleMerchant.update();
                        trx.commit();
                        response.setBaseResponse(1,offset, 1, success + " updating role", null);
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
                        RoleMerchant roleMerchant = RoleMerchantRepository.findByIdAndMerchantId(id, ownMerchant);
                        if (roleMerchant == null) {
                            response.setBaseResponse(0, 0, 0, error + " role merchant not found.", null);
                            return badRequest(Json.toJson(response));
                        }

                        roleMerchant.isDeleted = true;
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
                    RoleMerchant roleMerchant = RoleMerchantRepository.findByIdAndMerchantId(id, ownMerchant);
                    if (roleMerchant == null) {
                        response.setBaseResponse(0, 0, 0, error + " role merchant not found.", null);
                        return badRequest(Json.toJson(response));
                    }
                    RoleMerchantResponse roleMerchantResponse = new RoleMerchantResponse();
                    List<RoleMerchantFeature> roleMerchantFeatures = RoleMerchantFeature.findByRoleMerchantId(roleMerchant.id);
                    List<FeatureAssignRequest> featureAssignResponses = toFeaturesResponse(roleMerchantFeatures);
                    roleMerchantResponse.setId(roleMerchant.id);
                    roleMerchantResponse.setName(roleMerchant.getName());
                    roleMerchantResponse.setDescription(roleMerchant.getDescription());
                    roleMerchantResponse.setKey(roleMerchant.getKey());
                    roleMerchantResponse.setIsCashier(roleMerchant.isCashier());
                    roleMerchantResponse.setMerchantId(roleMerchant.getMerchant().id);
                    roleMerchantResponse.setFeatures(featureAssignResponses);

                    response.setBaseResponse(1,offset, 1, success + " showing detail role", roleMerchantResponse);
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
