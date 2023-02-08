package controllers.merchants;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Transaction;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hokeba.api.BaseResponse;
import com.hokeba.util.CommonFunction;
import com.hokeba.util.Encryption;
import com.hokeba.util.MailConfig;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiImplicitParam;
import com.wordnik.swagger.annotations.ApiImplicitParams;
import com.wordnik.swagger.annotations.ApiOperation;
import controllers.BaseController;
import dtos.merchant.*;
import models.Merchant;
import models.RoleMerchant;
import models.UserMerchant;
import models.store.*;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import repository.UserMerchantRepository;
import repository.RoleMerchantRepository;
import repository.StoreAccessRepository;

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
                String validate = validateCreateUserRequest(request, ownMerchant, Boolean.FALSE);
                if (validate == null) {
                    Transaction trx = Ebean.beginTransaction();
                    try {
                        Merchant merchantData = Merchant.findByEmail(request.getEmail(), false);
//                        if(merchantData != null) {
//                            response.setBaseResponse(0, 0, 0, "Email terdaftar sebagai merchant, tidak dapat digunakan kembali", null);
//                            return badRequest(Json.toJson(response));
//                        }
                        RoleMerchant role = RoleMerchantRepository.find.byId(request.getRoleId());
                        if (role == null) {
                            response.setBaseResponse(0, 0, 0, error + " Role id not found.", null);
                            return badRequest(Json.toJson(response));
                        }

                        UserMerchant newUserMerchant = new UserMerchant();
                        constructRequestModel(newUserMerchant, request, role, ownMerchant, Boolean.FALSE);
                        newUserMerchant.save();

                        String forActivation = Encryption.EncryptAESCBCPCKS5Padding(String.valueOf(newUserMerchant.id) + String.valueOf(System.currentTimeMillis()));

                        newUserMerchant.setActivationCode(forActivation);
                        newUserMerchant.update();

                        Thread thread = new Thread(() -> {
                            try {
                                MailConfig.sendmail(newUserMerchant.getEmail(), MailConfig.subjectActivation,
                                        MailConfig.renderMailSendCreatePasswordCMSTemplate(forActivation, newUserMerchant.fullName));
            
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });

                        thread.start();

                        trx.commit();

                        response.setBaseResponse(1,offset, 1, "Berhasil menambahkan user", newUserMerchant);
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
                String validate = validateCreateUserRequest(request, ownMerchant, Boolean.TRUE);
                if (validate == null) {
                    Transaction trx = Ebean.beginTransaction();
                    try {
                        UserMerchant userMerchant = UserMerchantRepository.findById(id, ownMerchant);
                        if (userMerchant == null) {
                            response.setBaseResponse(0, 0, 0, "User merchant tidak ditemukan", null);
                            return badRequest(Json.toJson(response));
                        }
                        // need to refactor to role_merchant
                        RoleMerchant role = RoleMerchantRepository.find.byId(request.getRoleId());
                        if (role == null) {
                            response.setBaseResponse(0, 0, 0, "Id Role tidak ditemukan", null);
                            return badRequest(Json.toJson(response));
                        }

                        if(!request.getEmail().equalsIgnoreCase(userMerchant.getEmail())){
                            Merchant merchantData = Merchant.findByEmail(request.getEmail(), false);
                            System.out.println("Kesini engga ya");
                            if(merchantData != null) {
                                response.setBaseResponse(0, 0, 0, "Email terdaftar sebagai merchant, tidak dapat digunakan kembali", null);
                                return badRequest(Json.toJson(response));
                            }
                            System.out.println("Kesini engga ya 2222");
                            UserMerchant userMerchantData = UserMerchantRepository.find.where().ne("t0.id", userMerchant.id).eq("t0.email", request.getEmail()).eq("role.merchant", ownMerchant).setMaxRows(1).findUnique();
                            if(userMerchantData != null) {
                                response.setBaseResponse(0, 0, 0, "Email sudah terdaftar, tidak bisa digunakan kembali.", null);
                                return badRequest(Json.toJson(response));
                            }
                            System.out.println("Kesini engga ya 333");
                            constructRequestModel(userMerchant, request, role, ownMerchant, Boolean.FALSE);
                            System.out.println("Kesini engga ya 444");
                            String forActivation = Encryption.EncryptAESCBCPCKS5Padding(String.valueOf(userMerchant.id) + String.valueOf(System.currentTimeMillis()));

                            userMerchant.setActivationCode(forActivation);
                            userMerchant.update();

                            Thread thread = new Thread(() -> {
                                try {
                                    MailConfig.sendmail(userMerchant.getEmail(), MailConfig.subjectActivation,
                                            MailConfig.renderVerificationAccount(forActivation, userMerchant.getFullName()));
                
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            });

                            thread.start();
                        } else {
                            constructRequestModel(userMerchant, request, role, ownMerchant, userMerchant.isActive);
                            userMerchant.update();
                        }
                        trx.commit();

                        response.setBaseResponse(1,offset, 1, success + " update user successfully", userMerchant);
                        return ok(Json.toJson(response));
                    } catch (Exception e) {
                        logger.error("Ada kesalahan pada saat update user", e);
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

    @ApiOperation(value = "Delete User", notes = "Delete User.\n" + swaggerInfo
            + "", response = BaseResponse.class, httpMethod = "DELETE")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "user form", dataType = "temp.swaggermap.UserForm", required = true, paramType = "body", value = "user form") })
    public static Result deleteUsers(Long id) {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            Transaction trx = Ebean.beginTransaction();
            try {
                UserMerchant userMerchant = UserMerchantRepository.findById(id, ownMerchant);
                if (userMerchant == null) {
                    response.setBaseResponse(0, 0, 0, error + " User merchant not found.", null);
                    return badRequest(Json.toJson(response));
                }
                StoreAccess newStoreAccess = StoreAccessRepository.find.where().eq("t0.user_merchant_id", userMerchant.id).eq("merchant", ownMerchant).findUnique();
                if (newStoreAccess != null) {
                    newStoreAccess.setIsActive(Boolean.FALSE);
                    newStoreAccess.isDeleted = Boolean.TRUE;
                    newStoreAccess.update();
                    Query<StoreAccessDetail> queryDetail = StoreAccessRepository.findDetail.where()
                            .eq("t0.store_access_id", newStoreAccess.id).eq("t0.is_deleted", false).order("t0.id");
                    List<StoreAccessDetail> storeDetailData = StoreAccessRepository.findByIdAssign(queryDetail);
                    for (StoreAccessDetail storeaccess : storeDetailData) {
                        storeaccess.isDeleted = Boolean.TRUE;
                        storeaccess.update();
                    }
                }
                userMerchant.isDeleted = true;
                userMerchant.isActive = false;
                userMerchant.update();
                trx.commit();

                response.setBaseResponse(1,offset, 1, "Sukses menghapus user", userMerchant.isDeleted);
                return ok(Json.toJson(response));
            } catch (Exception e) {
                logger.error("Error while delete user", e);
                e.printStackTrace();
                trx.rollback();
            } finally {
                trx.end();
            }
            response.setBaseResponse(0, 0, 0, error, null);
            return badRequest(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result setStatus(Long id) {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        JsonNode json = request().body().asJson();
        if (ownMerchant != null) {
            Transaction trx = Ebean.beginTransaction();
            try {
                UserMerchant userMerchant = UserMerchantRepository.findById(id, ownMerchant);
                UserMerchantRequest request = objectMapper.readValue(json.toString(), UserMerchantRequest.class);
                if (userMerchant == null) {
                    response.setBaseResponse(0, 0, 0, "User merchant tidak ditemukan", null);
                    return badRequest(Json.toJson(response));
                }
                if (userMerchant.getPassword() == null && userMerchant.isActive == false && request.getIsActive() == true) {
                    response.setBaseResponse(0, 0, 0, "Tidak bisa mengaktifkan! Silahkan kirim email verifikasi kepada user!", null);
                    return badRequest(Json.toJson(response));
                }
                userMerchant.isActive = request.getIsActive();
                userMerchant.update();
                trx.commit();

                response.setBaseResponse(1,offset, 1, "Sukses mengubah status merchant", userMerchant.isDeleted);
                return ok(Json.toJson(response));
            } catch (Exception e) {
                logger.error("Error while delete user", e);
                e.printStackTrace();
                trx.rollback();
            } finally {
                trx.end();
            }
            response.setBaseResponse(0, 0, 0, error, null);
            return badRequest(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    @ApiOperation(value = "Read User", notes = "Read User.\n" + swaggerInfo
            + "", response = BaseResponse.class, httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "user form", dataType = "temp.swaggermap.UserForm", required = true, paramType = "body", value = "user form") })
    public static Result viewUser(Long id) {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            if (id != null) {
                Transaction trx = Ebean.beginTransaction();
                try {
                    UserMerchant userMerchant = UserMerchantRepository.findById(id, ownMerchant);
                    if (userMerchant == null) {
                        response.setBaseResponse(0, 0, 0, error + " user merchant not found.", null);
                        return badRequest(Json.toJson(response));
                    }

                    if (userMerchant.isDeleted != true) {
                        response.setBaseResponse(1,offset, 1, success + " showing detail user", userMerchant);
                        return ok(Json.toJson(response));
                    } else {
                        response.setBaseResponse(0, 0, 0, error + " users not found", null);
                        return ok(Json.toJson(response));
                    }
                } catch (Exception e) {
                    logger.error("Error while showing detail user", e);
                    e.printStackTrace();
                    trx.rollback();
                } finally {
                    trx.end();
                }
                response.setBaseResponse(0, 0, 0, error, null);
                return badRequest(Json.toJson(response));
            }
            response.setBaseResponse(0, 0, 0, "Cannot find user Id", null);
            return badRequest(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    private static UserMerchant constructRequestModel(UserMerchant userMerchant, UserMerchantRequest request, RoleMerchant role, Merchant merchant, Boolean statusActive) {
        userMerchant.setFirstName(request.getFirstName());
        userMerchant.setLastName(request.getLastName());
        userMerchant.setFullName(request.getFirstName() + " " + request.getLastName());
        userMerchant.setEmail(request.getEmail());
        userMerchant.setActive(statusActive);
        userMerchant.setRole(role);
        return userMerchant;
    }

    public static String validateCreateUserRequest(UserMerchantRequest request, Merchant merchant, Boolean isEdit) {
        if (request == null)
            return "Request is null or empty";
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
//        if (isEdit == Boolean.FALSE) {
//            UserMerchant userMerchant = UserMerchantRepository.find.where().eq("t0.email", request.getEmail()).eq("is_deleted", Boolean.FALSE).findUnique();
//            if (userMerchant != null)
//                return "Email telah terdaftar";
//        }
        return null;
    }

    @ApiOperation(value = "Get all user list.", notes = "Returns list of user.\n" + swaggerInfo
            + "", response = UserMerchant.class, responseContainer = "List", httpMethod = "GET")
    public static Result listUsers(String filter, String sort, int offset, int limit) {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            Query<UserMerchant> query = UserMerchantRepository.find.where().eq("t0.is_deleted", false).eq("role.merchant", ownMerchant).order("id");
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
                    response.setId(data.id);
                    response.setFullName(data.getFullName());
                    response.setFirstName(data.getFirstName());
                    response.setLastName(data.getLastName());
                    response.setEmail(data.getEmail());
                    response.setIsActive(statusActive);
                    response.setGender(data.getGender());
                    response.setMerchantId(data.getRole().getMerchant().id);
                    response.setRoleId(data.getRole().id);
                    response.setRoleName(data.getRole().getName());
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

    // ===================================== FOR VERIFICATION =========================================
    
    @ApiOperation(value = "Create New Password", notes = "Create New Password.\n" + swaggerInfo
            + "", response = BaseResponse.class, httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "new password form", dataType = "temp.swaggermap.NewPasswordForm", required = true, paramType = "body", value = "new password form") })
    public static Result createNewPassword(String activationCode) {
            JsonNode json = request().body().asJson();
            try {
                UserMerchantRequest request = objectMapper.readValue(json.toString(), UserMerchantRequest.class);
                String validate = validateNewPassword(request);
                if (validate == null) {
                    Transaction trx = Ebean.beginTransaction();
                    try {
                        UserMerchant userMerchant = UserMerchantRepository.findDataActivationCode(activationCode);
                        if (userMerchant == null) {
                            response.setBaseResponse(0, 0, 0, error + " Activation Code id not found. Please call administrator for resend activation code.", null);
                            return badRequest(Json.toJson(response));
                        }
                        userMerchant.isDeleted = false;
                        userMerchant.setPassword(Encryption.EncryptAESCBCPCKS5Padding(request.getPassword()));
                        userMerchant.setActive(true);
                        userMerchant.setActivationCode("");
                        userMerchant.update();
                        
                        Thread thread = new Thread( () -> {
                        	try {
                                MailConfig.sendmail(userMerchant.getEmail(), MailConfig.subjectSuccessActivation,
                                		MailConfig.renderMailResendActivation(userMerchant));
							} catch (Exception e) {
								e.printStackTrace();
							}
                        });
                        thread.start();

                        trx.commit();

                        response.setBaseResponse(1,offset, 1, success + " Password created successfully", userMerchant);
                        return ok(Json.toJson(response));
                    } catch (Exception e) {
                        logger.error("Error while creating password", e);
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
            return null;
    }

    public static String validateNewPassword(UserMerchantRequest request) {
        if (request == null)
            return "Request is null or empty";
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
        return null;
    }

}
