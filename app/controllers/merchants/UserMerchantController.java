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
import dtos.merchant.*;
import models.Merchant;
import models.Role;
import models.UserMerchant;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import repository.UserMerchantRepository;
import controllers.BaseController;
import java.text.SimpleDateFormat;
import java.util.*;
import com.avaje.ebean.Query;

import java.io.IOException;

@Api(value = "/merchants/user", description = "User Merchant")
public class UserMerchantController extends BaseController {

    private final static Logger.ALogger logger = Logger.of(UserMerchantController.class);

    private static BaseResponse response = new BaseResponse();

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @ApiOperation(value = "Create User", notes = "Create User.\n" + swaggerInfo
            + "", response = BaseResponse.class, httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "user form", dataType = "temp.swaggermap.UserForm", required = true, paramType = "body", value = "user form") })
    public static Result createUser() {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            JsonNode json = request().body().asJson();
            try {
                UserMerchantRequest request = objectMapper.readValue(json.toString(), UserMerchantRequest.class);
                String validate = validateCreateUserRequest(request);
                if (validate == null) {
                    Transaction trx = Ebean.beginTransaction();
                    try {
                        // need to refactor to role_merchant
                        Role role = Role.find.byId(request.getRoleId());
                        if (role == null) {
                            response.setBaseResponse(0, 0, 0, error + " Role id not found.", null);
                            return badRequest(Json.toJson(response));
                        }
                        UserMerchant newUserMerchant = new UserMerchant();
                        constructRequestModel(newUserMerchant, request, role, ownMerchant);
                        newUserMerchant.save();

                        trx.commit();

                        response.setBaseResponse(1,offset, 1, success + " User created successfully", null);
                        return ok(Json.toJson(response));
                    } catch (Exception e) {
                        logger.error("Error while creating user", e);
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

    @ApiOperation(value = "Edit User", notes = "Edit User.\n" + swaggerInfo
            + "", response = BaseResponse.class, httpMethod = "PUT")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "user form", dataType = "temp.swaggermap.UserForm", required = true, paramType = "body", value = "user form") })
    public static Result editUser(Long id) {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            JsonNode json = request().body().asJson();
            try {
                UserMerchantRequest request = objectMapper.readValue(json.toString(), UserMerchantRequest.class);
                String validate = validateCreateUserRequest(request);
                if (validate == null) {
                    Transaction trx = Ebean.beginTransaction();
                    try {
                        UserMerchant userMerchant = UserMerchantRepository.findByIdAndMerchantId(id, ownMerchant.id);
                        if (userMerchant == null) {
                            response.setBaseResponse(0, 0, 0, error + " User merchant not found.", null);
                            return badRequest(Json.toJson(response));
                        }
                        // need to refactor to role_merchant
                        Role role = Role.find.byId(request.getRoleId());
                        if (role == null) {
                            response.setBaseResponse(0, 0, 0, error + " Role id not found.", null);
                            return badRequest(Json.toJson(response));
                        }
                        constructRequestModel(userMerchant, request, role, ownMerchant);
                        userMerchant.update();
                        trx.commit();

                        response.setBaseResponse(1,offset, 1, success + " User created successfully", null);
                        return ok(Json.toJson(response));
                    } catch (Exception e) {
                        logger.error("Error while creating user", e);
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

    private static UserMerchant constructRequestModel(UserMerchant userMerchant, UserMerchantRequest request, Role role, Merchant merchant) {
        userMerchant.setFirstName(request.getFirstName());
        userMerchant.setLastName(request.getLastName());
        userMerchant.setFullName(request.getFirstName() + " " + request.getLastName());
        userMerchant.setActive(Boolean.TRUE);
        userMerchant.setRole(role);
        userMerchant.setMerchant(merchant);
        return userMerchant;
    }

    public static String validateCreateUserRequest(UserMerchantRequest request) {
        if (request == null)
            return "Request is null or empty";
        if (request.getMerchantId() == null || request.getMerchantId() < 0)
            return "Merchant id is null or empty";
        if (request.getEmail() == null)
            return "Email is null or empty";
        if (request.getEmail() != null && !request.getEmail().matches(CommonFunction.emailRegex))
            return "Email format not valid.";
        if (request.getFirstName() == null)
            return "First name is null or empty";
        if (request.getFirstName() != null && request.getFirstName().length() > 50)
            return "First name cannot be more than 50 characters.";
        if (request.getLastName() != null && request.getLastName().length() > 50)
            return "Last name cannot be more than 50 characters.";
        // =============================================================================================================== //
        UserMerchant userMerchant = UserMerchantRepository.findByEmailAndMerchantId(request.getEmail(), request.getMerchantId());
        if (userMerchant != null)
            return "User email has been used.";
        return null;
    }

    @ApiOperation(value = "Get all user list.", notes = "Returns list of user.\n" + swaggerInfo
            + "", response = UserMerchant.class, responseContainer = "List", httpMethod = "GET")
    public static Result listUsers(String filter, String sort, int offset, int limit) {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            Query<UserMerchant> query = UserMerchantRepository.find.where().eq("t0.is_deleted", false).eq("t0.merchant_id", getUserMerchant().id).order("t0.id");
            try {
                List<UserMerchantResponse> responses = new ArrayList<>();
                List<UserMerchant> totalData = UserMerchantRepository.getTotalData(query);
                List<UserMerchant> responseIndex = UserMerchantRepository.getDataUser(query, sort, filter, offset, limit);
                for (UserMerchant data : responseIndex) {
                    UserMerchantResponse response = new UserMerchantResponse();
                    Boolean statusActive = false;
                    if(data.getIsActive() == "Active"){
                        statusActive = true;
                    }
                    response.setId(data.getId());
                    response.setFullName(data.getFullName());
                    response.setFirstName(data.getFirstName());
                    response.setLastName(data.getLastName());
                    response.setEmail(data.getEmail());
                    response.setIsActive(statusActive);
                    response.setGender(data.getGender());
                    response.setMerchantId(data.getMerchantId());
                    response.setRoleId(data.getRoleId());
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

}
