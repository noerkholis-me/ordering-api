package controllers.merchants;

import assets.JsonMask;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.Transaction;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hokeba.api.BaseResponse;
import com.hokeba.api.UserSession;
import com.hokeba.mapping.request.MapMerchantRegister;
import com.hokeba.mapping.request.MapMerchantUpdateProfile;
import com.hokeba.mapping.request.MapProfilePhoto;
import com.hokeba.mapping.response.MapDashboard;
import com.hokeba.util.*;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiImplicitParam;
import com.wordnik.swagger.annotations.ApiImplicitParams;
import com.wordnik.swagger.annotations.ApiOperation;
import controllers.BaseController;
import dtos.FeatureAndPermissionSession;
import dtos.merchant.MerchantSessionResponse;
import models.*;
import play.Logger;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Http;
import play.mvc.Result;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by hendriksaragih on 2/28/17.
 */
@Api(value = "/merchants/sessions", description = "Session")
public class SessionsController extends BaseController {
    private final static Logger.ALogger logger = Logger.of(SessionsController.class);
    @SuppressWarnings("rawtypes")
    private static BaseResponse response = new BaseResponse();

    @ApiOperation(value = "Sign in", notes = "Sign in.\n" + swaggerInfo
            + "", response = UserSession.class, httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "login form", dataType = "temp.swaggermap.LoginForm", required = true, paramType = "body", value = "login form") })
    public static Result signIn() {
        JsonNode json = request().body().asJson();
        if (checkAccessAuthorization("guest") == 200 && json.has("email") && json.has("password")
                && json.has("device_model") && json.has("device_id") && json.has("device_type")) {

            String email = json.findPath("email").asText();
            String password = json.findPath("password").asText();
            String deviceModel = json.findPath("device_model").asText();
            String deviceType = json.findPath("device_type").asText();
            String deviceId = json.findPath("device_id").asText();
            Merchant member = null;
            if (email.matches(CommonFunction.emailRegex)) {
                member = Merchant.login(email, password);
            }
            if (member != null) {
            	if (!Merchant.STATUS_APPROVED.equals(member.status)) {
            		response.setBaseResponse(0, 0, 0, "Your account hasn't been approved, please contact our support", null);
                    return badRequest(Json.toJson(response));
            	}
                if (member.isActive){
                    try {
                        MerchantLog log = MerchantLog.loginMerchant(deviceModel, deviceType, deviceId, member);
                        if (log == null) {
                            response.setBaseResponse(0, 0, 0, inputParameter, null);
                            return badRequest(Json.toJson(response));
                        }
                        // modify session response for merchant can be reusable for property
                        MerchantSessionResponse profileData = toMerchantSessionResponse(member);
                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                        ObjectMapper om = new ObjectMapper();
                        om.addMixIn(Merchant.class, JsonMask.class);
//                        HashMap<String, Boolean> features = member.checkPrivilegeList();
                        List<FeatureAndPermissionSession> featureAndPermissionSessions = member.checkFeatureAndPermissions();
                        UserSession session = new UserSession(log.token, df.format(log.expiredDate), log.memberType, Json.parse(om.writeValueAsString(profileData)), featureAndPermissionSessions);
//                        session.setProfile_data(Json.parse(om.writeValueAsString(profileData)));
                        response.setBaseResponse(1, 0, 1, success, session);
                        return ok(Json.toJson(response));
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    response.setBaseResponse(0, 0, 0, error, null);
                    return badRequest(Json.toJson(response));
                }else{
                    response.setBaseResponse(0, 0, 0, "Your account hasn't actived, please check and verify from your email", null);
                    return badRequest(Json.toJson(response));
                }
            }
            response.setBaseResponse(0, 0, 0, "Wrong username or password", null);
            return badRequest(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    private static MerchantSessionResponse toMerchantSessionResponse(Merchant member) {

        MerchantSessionResponse profileData = new MerchantSessionResponse();
        profileData.setEmail(member.email);
        profileData.setBirthDate(member.birthDate);
        profileData.setGender(member.gender);
        profileData.setFullName(member.fullName);
        profileData.setDomain(member.domain);
        profileData.setAccountNumber(member.accountNumber);
        profileData.setMerchantCode(member.getCode());
        profileData.setName(member.name);
        profileData.setLogo(member.getLogo());
        profileData.setDisplay(member.display);
        profileData.setType(member.getType());
        profileData.setStatus(member.status);
        profileData.setCompanyName(member.companyName);
        profileData.setCityName(member.cityName);
        profileData.setPostalCode(member.postalCode);
        profileData.setProvince(member.province);
        profileData.setCommissionType(member.commissionType);
        profileData.setAddress(member.address);
        profileData.setPhone(member.phone);
        profileData.setMetaDescription(member.metaDescription);
        profileData.setStory(member.story);
        profileData.setUrl(member.url);
        profileData.setMerchantUrlPage(member.merchantUrlPage);
        profileData.setAnchor(member.anchor);
        profileData.setUrlBanner(member.urlBanner);
        profileData.setQuickResponse(member.quickResponse);
        profileData.setProductAvailability(member.productAvailability);
        profileData.setRating(member.rating);
        profileData.setCountRating(member.countRating);
        profileData.setBalance(member.getBalance());
        profileData.setResetTime(member.resetTime);
        profileData.setActive(member.isActive);

        return profileData;
    }


    @ApiOperation(value = "Sign out", notes = "Sign out.", response = BaseResponse.class, httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "access token", dataType = "string", required = true, paramType = "header", value = "access token") })
    public static Result signOut() {
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        if (checkAccessAuthorization("merchant") == 200) {
            String token = request().headers().get(TOKEN)[0];
            if (MerchantLog.logoutMerchant(token)) {
                response.setBaseResponse(1, 0, 1, success, null);
                return ok(Json.toJson(response));
            }
        }
        return unauthorized(Json.toJson(response));
    }

    //odoo
    @ApiOperation(value = "Sign up", notes = "Sign up.", response = BaseResponse.class, httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "sign-up form", dataType = "temp.swaggermap.SignUpForm", required = true, paramType = "body", value = "sign-up form") })
    public static Result signUp() {
        if (checkAccessAuthorization("guest") == 200) {
            JsonNode json = request().body().asJson();
            ObjectMapper mapper = new ObjectMapper();

            try {
                MapMerchantRegister map = mapper.readValue(json.toString(), MapMerchantRegister.class);
                String redirect = Constant.getInstance().getMerchantUrl() + "/activate/";

                String validation = Merchant.validation(map.getEmail(), map.getPassword(), map.getConfirmPassword());
                if (validation == null) {
                    Transaction txn = Ebean.beginTransaction();
                    try {
                        Merchant newMember = new Merchant(map);
                        newMember.save();
                        
                        //odoo
//                        OdooService.getInstance().createVendor(newMember);

//                        Thread thread = new Thread(() -> {
//                            try {
//                                MailConfig.sendmail2(newMember.email, MailConfig.subjectActivation,
//                                        MailConfig.renderMailActivationTemplate(newMember, redirect));
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        });
//                        thread.start();

                        txn.commit();
                        response.setBaseResponse(1, offset, 1, success + ", please check your mail", null);
                        return ok(Json.toJson(response));

                    } catch (Exception e) {
                        e.printStackTrace();
                        txn.rollback();
                    } finally {
                        txn.end();
                    }
                    response.setBaseResponse(0, 0, 0, error, null);
                    return badRequest(Json.toJson(response));
                }
                response.setBaseResponse(0, 0, 0, validation, null);
                return badRequest(Json.toJson(response));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    @ApiOperation(value = "Update Profile", notes = "Update Profile.", response = BaseResponse.class, httpMethod = "PATCH")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Update Profile form", dataType = "temp.swaggermap.SignUpForm", required = true, paramType = "body", value = "Update Profile") })
    @BodyParser.Of(value = BodyParser.Json.class, maxLength = 1024 * 1024)
    public static Result updateProfile() {
        Merchant actor = checkMerchantAccessAuthorization();
        if (actor != null) {
            if(!request().body().isMaxSizeExceeded()) {
                JsonNode json = request().body().asJson();
                ObjectMapper mapper = new ObjectMapper();

                try {
                    MapMerchantUpdateProfile map = mapper.readValue(json.toString(), MapMerchantUpdateProfile.class);

                    String validation = Merchant.validation(map.getPassword(), map.getConfirmPassword());
                    if (validation == null) {
                        Merchant newMember = Merchant.fromMap(actor, map);
                        File newFile = Photo.uploadImageRaw(map.getPhoto(), "merchant", Helper.getRandomString(10), null, "jpg");
                        if (newFile != null){
                            newMember.logo = Photo.createUrl("merchant", newFile.getName());
                        }
                        newMember.update();

                        response.setBaseResponse(1, offset, 1, success, null);
                        return ok(Json.toJson(response));
                    }
                    response.setBaseResponse(0, 0, 0, validation, null);
                    return badRequest(Json.toJson(response));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else{
                response.setBaseResponse(0, 0, 0, "Max Size Exceeded (1MB)", null);
                return badRequest(Json.toJson(response));
            }


        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    @ApiOperation(value = "Update Profile", notes = "Update Profile.", response = BaseResponse.class, httpMethod = "PATCH")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "Update Profile form", dataType = "temp.swaggermap.SignUpForm", required = true, paramType = "body", value = "Update Profile") })
    public static Result updateProfileV2() {
        Merchant actor = checkMerchantAccessAuthorization();
        if (actor != null) {
            ObjectMapper mapper = new ObjectMapper();

            try {
                Http.MultipartFormData body = request().body().asMultipartFormData();
                JsonNode json = null;
                if (body != null) {
                    Map<String, String[]> mapData = body.asFormUrlEncoded();
                    json = Json.parse(mapData.get("data")[0]);
                }else {
                    json = request().body().asJson();
                }

                MapMerchantUpdateProfile map = mapper.readValue(json.toString(), MapMerchantUpdateProfile.class);

                String validation = Merchant.validation(map.getPassword(), map.getConfirmPassword());
                if (validation == null) {
                    Http.MultipartFormData.FilePart imageFile = body.getFile("image");
                    File newFile = Photo.uploadImage(imageFile, "merchant", Helper.getRandomString(10), null, "jpg");

                    Merchant newMember = Merchant.fromMap(actor, map);
                    if (newFile != null) {
                        newMember.logo = Photo.createUrl("merchant", newFile.getName());
                    }
                    newMember.update();

                    response.setBaseResponse(1, offset, 1, success, null);
                    return ok(Json.toJson(response));
                }
                response.setBaseResponse(0, 0, 0, validation, null);
                return badRequest(Json.toJson(response));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    @ApiOperation(value = "Activate Merchant", notes = "Activate Merchant", response = BaseResponse.class, httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", dataType = "string", required = true, paramType = "path", value = "activation code") })
    public static Result activate() {
        JsonNode json = request().body().asJson();
        if (checkAccessAuthorization("guest") == 200 && json.has("code")) {
            String code = json.findPath("code").asText();
            Merchant member = Merchant.find.where().eq("is_active", false).eq("activation_code", code).setMaxRows(1).findUnique();
            if (member != null) {
                member.activationCode = "";
                member.isActive = true;
                member.save();
                response.setBaseResponse(1, 0, 1, success, null);
                return ok(Json.toJson(response));
            }
            response.setBaseResponse(0, 0, 0, error, null);
            return badRequest(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result forgetPassword() {
        JsonNode json = request().body().asJson();
        if (checkAccessAuthorization("guest") == 200 && json.has("email")) {
            String email = json.findPath("email").asText();
            Merchant member = Merchant.find.where().eq("is_active", true).eq("email", email).setMaxRows(1).findUnique();
            if (member != null) {
                Long now = System.currentTimeMillis();
                String merchantEmail = member.email;
                String forgotPasswordCode = Encryption.EncryptAESCBCPCKS5Padding(merchantEmail + "-" + String.valueOf(now));
                String redirect = Constant.getInstance().getMerchantUrl() + "/reset-password" + "/" + forgotPasswordCode;
                try {
                    member.resetToken = Encryption.EncryptAESCBCPCKS5Padding(member.email+now);
                    member.resetTime = now;
                    member.update();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Thread thread = new Thread(() -> {
                    try {
                        MailConfig.sendmail(member.email, MailConfig.subjectForgotPassword, MailConfig.renderMailForgotPasswordMerchantTemplate(member, redirect));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                thread.start();
                response.setBaseResponse(1, 0, 1, success, null);
                return ok(Json.toJson(response));
            }
            response.setBaseResponse(0, 0, 0, notFound, null);
            return notFound(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result resetPassword() {
        JsonNode json = request().body().asJson();
        if (checkAccessAuthorization("guest") == 200 && json.has("key") && json.has("password") && json.has("confirm_password")) {
            String key = json.findPath("key").asText();
            String newPass = json.findPath("password").asText();
            String confPass = json.findPath("confirm_password").asText();

            String check = CommonFunction.passwordValidation(newPass, confPass);
            if (check != null) {
                response.setBaseResponse(0, 0, 0, check, null);
                return badRequest(Json.toJson(response));
            }

            Transaction txn = Ebean.beginTransaction();
            try {
                Merchant member = Merchant.find.where().eq("is_active", true).eq("reset_token", key).setMaxRows(1).findUnique();
                if (member != null) {
                    Date requestDate = new Date(member.resetTime);

                    Calendar cal = Calendar.getInstance();
                    cal.setTime(requestDate);
                    cal.add(Calendar.HOUR, 1);
                    if (cal.getTime().before(new Date(System.currentTimeMillis()))) {
                        response.setBaseResponse(0, 0, 0, "Session has expired", null);
                        return badRequest(Json.toJson(response));
                    }

                    member.password = Encryption.EncryptAESCBCPCKS5Padding(newPass);
                    member.resetToken = "";
                    member.save();
                    Merchant.removeAllToken(member.id);
                    txn.commit();
                    response.setBaseResponse(1, 0, 1, success, null);
                    return ok(Json.toJson(response));
                }
                response.setBaseResponse(0, 0, 0, notFound, null);
                return notFound(Json.toJson(response));
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
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

    @BodyParser.Of(value = BodyParser.Json.class, maxLength = 10 * 1024 * 1024)
    public static Result updatePhoto() {
        Merchant actor = checkMerchantAccessAuthorization();
        if (actor != null) {
            String errorMessage = error;
            try {
                JsonNode json = request().body().asJson();
                if (json != null) {
                    ObjectMapper mapper = new ObjectMapper();
                    MapProfilePhoto map = mapper.readValue(json.toString(), MapProfilePhoto.class);
                    File newFile = Photo.uploadImageRaw(map.getPhoto(), "merchant", Helper.getRandomString(10), null, "jpg");
                    if (newFile != null){
                        actor.logo = Photo.createUrl("merchant", newFile.getName());
                    }
                    actor.update();
                    response.setBaseResponse(1, 0, 1, success, null);
                    return ok(Json.toJson(response));
                }
            } catch (Exception e) {
                errorMessage = e.getMessage();
                Logger.error("create", e);
            }
            response.setBaseResponse(0, 0, 0, errorMessage, null);
            return badRequest(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    @ApiOperation(value = "Refresh token", notes = "Refresh your current token.\n" + swaggerInfo
            + "", response = UserSession.class, httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "email", dataType = "temp.swaggermap.LoginForm", required = true, paramType = "body", value = "account email") })
    public static Result refreshToken() {
        if (request().headers().get(API_KEY) != null && request().headers().get(TOKEN) != null) {
            String apiKey = request().headers().get(API_KEY)[0];
            String token = request().headers().get(TOKEN)[0];
            MerchantLog targetLog = MerchantLog.isMerchantAuthorized(token, apiKey);
            if (targetLog != null) {
                Merchant targetMember = targetLog.merchant;
                // create new token
                MerchantLog log = MerchantLog.loginMerchant(targetLog.deviceModel, targetLog.deviceType, targetLog.deviceId, targetMember);
                if (log == null) {
                    response.setBaseResponse(0, 0, 0, inputParameter, null);
                    return badRequest(Json.toJson(response));
                }
                // deactivate old token
                targetLog.isActive = false;
                targetLog.save();
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                UserSession session = new UserSession(log.token, df.format(log.expiredDate), log.memberType);
                ObjectMapper om = new ObjectMapper();
                om.addMixInAnnotations(Member.class, JsonMask.class);
                try {
                    session.setProfile_data(Json.parse(om.writeValueAsString(targetMember)));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                response.setBaseResponse(1, 0, 1, success, session);
                return ok(Json.toJson(response));
            }
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result getProfile() throws JsonProcessingException {
        Merchant actor = checkMerchantAccessAuthorization();
        if (actor != null) {
            ObjectMapper om = new ObjectMapper();
            om.addMixInAnnotations(Merchant.class, JsonMask.class);
            response.setBaseResponse(1, offset, 1, success, Json.parse(om.writeValueAsString(actor)));
            return ok(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result dashboard() throws JsonProcessingException {
        Merchant actor = checkMerchantAccessAuthorization();
        if (actor != null) {

            MapDashboard md = new MapDashboard();

            Date sdate = Helper.getLast30day();
            Date edate = Helper.getEndCurrentDay();
            md.setOrderSummary(SalesOrderSeller.getOrderSummary(actor.id, sdate, edate));
            md.setOrderSummaryDetails(SalesOrderSeller.getOrderSummaryList(actor.id, sdate, edate));
            md.setProducts(Product.getProductSummaryMerchant(actor.id));
            md.setRecommendations(Product.getRecommendation(actor.id));
            md.setSellerData(SalesOrderSeller.getOrderSellers(actor.id));
            ObjectMapper om = new ObjectMapper();
            response.setBaseResponse(1, offset, 1, success, Json.parse(om.writeValueAsString(md)));
            return ok(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result mywallet() {
        Merchant actor = checkMerchantAccessAuthorization();
        if (actor != null) {
            Map<String, Object> wallet = new HashMap<>();
            wallet.put("balance", actor.getBalance());
            wallet.put("currency", Constant.defaultCurrency);
            response.setBaseResponse(1, offset, 1, success, wallet);
            return ok(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result changePassword() throws InvalidKeyException, InvalidAlgorithmParameterException,
            NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        Merchant actor = checkMerchantAccessAuthorization();
        if (actor != null) {
            JsonNode json = request().body().asJson();
            if (json.has("old_password") && json.has("new_password") && json.has("confirm_password")) {
                String oldPass = json.findPath("old_password").asText();
                String newPass = json.findPath("new_password").asText();
                String conPass = json.findPath("confirm_password").asText();
                String message = (actor.hasSetPassword()) ?
                        actor.changePassword(oldPass, newPass, conPass) :
                        actor.changePassword(newPass, conPass);
                if (message == null) {
                    response.setBaseResponse(1, 0, 1, updated, null);
                    return ok(Json.toJson(response));
                } else if (message.equals("500")) {
                    response.setBaseResponse(0, 0, 0, error, null);
                    return internalServerError(Json.toJson(response));
                } else {
                    response.setBaseResponse(0, 0, 0, message, null);
                    return badRequest(Json.toJson(response));
                }
            }
            response.setBaseResponse(0, 0, 0, inputParameter, null);
            return badRequest(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }
}
