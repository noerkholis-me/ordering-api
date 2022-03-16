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
import dtos.merchant.UserMerchantRequest;
import models.Merchant;
import models.Role;
import models.UserMerchant;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import repository.UserMerchantRepository;

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
                        Role role = Role.find.byId(request.getRoleId());
                        if (role == null) {
                            response.setBaseResponse(0, 0, 0, error + " Role id not found.", null);
                            return badRequest(Json.toJson(response));
                        }
                        UserMerchant newUserMerchant = new UserMerchant();
                        newUserMerchant.setFirstName(request.getFirstName());
                        newUserMerchant.setLastName(request.getLastName());
                        newUserMerchant.setFullName(request.getFirstName() + " " + request.getLastName());
                        newUserMerchant.setEmail(request.getEmail());
                        newUserMerchant.setRole(role);
                        newUserMerchant.setPassword(Encryption.EncryptAESCBCPCKS5Padding(request.getPassword()));
                        newUserMerchant.setActive(Boolean.TRUE);
                        newUserMerchant.setMerchant(ownMerchant);
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
        if (request.getPassword() == null)
            return "Password is null or empty";
        if (!request.getPassword().isEmpty()){
            if (!CommonFunction.passwordValidation(request.getPassword())) {
                return "Password must be at least 8 character";
            }
            if (!request.getConfirmPassword().equals(request.getPassword())) {
                return "Password and confirm password did not match.";
            }
        }
        // =============================================================================================================== //
        UserMerchant userMerchant = UserMerchantRepository.findByEmailAndMerchantId(request.getEmail(), request.getMerchantId());
        if (userMerchant != null)
            return "User email has been used.";
        return null;
    }

}
