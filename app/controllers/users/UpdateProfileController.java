package controllers.users;

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
import com.hokeba.util.CommonFunction;
import com.hokeba.util.Encryption;
import com.hokeba.util.MailConfig;
import com.hokeba.util.Helper;
import dtos.profile.*;
import models.*;
import models.Photo;
import play.Logger;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Http;
import play.mvc.Result;
import repository.*;

import java.io.File;
import java.util.*;
import com.avaje.ebean.Query;
import utils.ImageDirectory;
import utils.ImageUtil;

import java.io.IOException;


@Api(value = "/users/update/data", description = "User and Merchant Profile")
public class UpdateProfileController extends BaseController {

    private final static Logger.ALogger logger = Logger.of(UpdateProfileController.class);

    private static BaseResponse response = new BaseResponse();

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Result updateMerchantData() {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if(ownMerchant != null){
            Transaction trx = Ebean.beginTransaction();
            try {
                JsonNode json = request().body().asJson();
                ProfileMerchantRequest request = objectMapper.readValue(json.toString(), ProfileMerchantRequest.class);

                // VALIDATION
                // =======================================================================
                if(request.getEmail() == null || request.getEmail() == ""){
                    response.setBaseResponse(0, 0, 0, "Email tidak boleh kosong", null);
                    return badRequest(Json.toJson(response));
                }
                if(request.getName() == null || request.getName() == ""){
                    response.setBaseResponse(0, 0, 0, "Nama Merchant tidak boleh kosong", null);
                    return badRequest(Json.toJson(response));
                }
                if(request.getPhone() == null || request.getPhone() == ""){
                    response.setBaseResponse(0, 0, 0, "Telepon tidak boleh kosong", null);
                    return badRequest(Json.toJson(response));
                }
                if(request.getAddress() == null || request.getAddress() == ""){
                    response.setBaseResponse(0, 0, 0, "Alamat tidak boleh kosong", null);
                    return badRequest(Json.toJson(response));
                }
                if(request.getProvinceId() == null || request.getProvinceId() == 0){
                    response.setBaseResponse(0, 0, 0, "Provinsi tidak boleh kosong", null);
                    return badRequest(Json.toJson(response));
                }
                if(request.getCityId() == null || request.getCityId() == 0){
                    response.setBaseResponse(0, 0, 0, "Kota tidak boleh kosong", null);
                    return badRequest(Json.toJson(response));
                }
                if(request.getDistrictId() == null || request.getDistrictId() == 0){
                    response.setBaseResponse(0, 0, 0, "Kecamatan tidak boleh kosong", null);
                    return badRequest(Json.toJson(response));
                }
                if(request.getSubDistrictId() == null || request.getSubDistrictId() == 0){
                    response.setBaseResponse(0, 0, 0, "Desa/Kelurahan tidak boleh kosong", null);
                    return badRequest(Json.toJson(response));
                }
                // =======================================================================

                Merchant getMerchantData = Merchant.merchantGetId(ownMerchant.id);
                if(getMerchantData == null) {
                    response.setBaseResponse(0, 0, 0, "Merchant tidak ditemukan", null);
                    return notFound(Json.toJson(response));
                }

                ShipperProvince shipperProvince = ShipperProvince.findById(request.getProvinceId());
                if(shipperProvince == null) {
                    response.setBaseResponse(0, 0, 0, "Provinsi tidak ditemukan", null);
                    return notFound(Json.toJson(response));
                }
                ShipperCity shipperCity = ShipperCity.findById(request.getCityId());
                if(shipperCity == null) {
                    response.setBaseResponse(0, 0, 0, "Kota tidak ditemukan", null);
                    return notFound(Json.toJson(response));
                }
                ShipperArea shipperArea = ShipperArea.findById(request.getDistrictId());
                if(shipperArea == null) {
                    response.setBaseResponse(0, 0, 0, "District tidak ditemukan", null);
                    return notFound(Json.toJson(response));
                }
                ShipperSuburb shipperSuburb = ShipperSuburb.findById(request.getSubDistrictId());
                if(shipperSuburb == null) {
                    response.setBaseResponse(0, 0, 0, "Sub Urban tidak ditemukan", null);
                    return notFound(Json.toJson(response));
                }

                if(request.getEmail() != null && request.getEmail() != "" && !request.getEmail().equals(getMerchantData.email)){
                    getMerchantData.email = request.getEmail();
                    getMerchantData.isActive = Boolean.FALSE;
                    getMerchantData.status = "PENDING APPROVAL";
                    String forActivation = Encryption.EncryptAESCBCPCKS5Padding(String.valueOf(getMerchantData.id) + String.valueOf(System.currentTimeMillis()));

                    getMerchantData.activationCode = forActivation;

                    // START TO SEND EMAIL VERIFICATION
                    Thread thread = new Thread(() -> {
                        try {
                            MailConfig.sendmail(request.getEmail(), MailConfig.subjectActivation,
                                    MailConfig.renderVerificationAccount(forActivation, getMerchantData.fullName));
        
                        } catch (Exception e) {
                            e.printStackTrace();
                            trx.rollback();
                        } finally {
                            trx.end();
                        }
                    });
                    thread.start();
                }
                getMerchantData.fullName = request.getName();
                getMerchantData.name = request.getName();
                getMerchantData.companyName = request.getName();
                getMerchantData.domain = request.getMerchantUrl() != null && request.getMerchantUrl() != "" ? request.getMerchantUrl() : null;
                getMerchantData.merchantUrlPage = request.getMerchantUrl();
                getMerchantData.phone = request.getPhone();
                getMerchantData.address = request.getAddress();
                getMerchantData.province = shipperProvince;
                getMerchantData.city = shipperCity;
                getMerchantData.area = shipperArea;
                getMerchantData.suburb = shipperSuburb;
                getMerchantData.postalCode = request.getPostalCode();
                getMerchantData.update();
                trx.commit();

                response.setBaseResponse(1, 0, 1, "Berhasil mengupdate data", getMerchantData.id);
                return ok(Json.toJson(response));
            
            } catch (Exception e) {
                logger.error("Error saat parsing json", e);
                e.printStackTrace();
                trx.rollback();
            } finally {
                trx.end();
            }
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result updatePasswordMerchantData() {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if(ownMerchant != null){
            Transaction trx = Ebean.beginTransaction();
            try {
                JsonNode json = request().body().asJson();
                ProfileMerchantRequest request = objectMapper.readValue(json.toString(), ProfileMerchantRequest.class);

                Merchant getMerchantData = Merchant.merchantGetId(ownMerchant.id);
                if(getMerchantData == null) {
                    response.setBaseResponse(0, 0, 0, "Merchant tidak ditemukan", null);
                    return notFound(Json.toJson(response));
                }

                // VALIDATION
                // =======================================================================
                if(request.getPassword() == null || request.getPassword() == ""){
                    response.setBaseResponse(0, 0, 0, "Password tidak boleh kosong", null);
                    return badRequest(Json.toJson(response));
                }
                if(request.getConfirmPassword() == null || request.getConfirmPassword() == ""){
                    response.setBaseResponse(0, 0, 0, "Confirmation Password tidak boleh kosong", null);
                    return badRequest(Json.toJson(response));
                }
                if(!request.getConfirmPassword().equals(request.getPassword())){
                    response.setBaseResponse(0, 0, 0, "Confirmation Password tidak sama", null);
                    return badRequest(Json.toJson(response));
                }
                if(Encryption.EncryptAESCBCPCKS5Padding(request.getPassword()).equals(getMerchantData.password)){
                    response.setBaseResponse(0, 0, 0, "Password tidak boleh sama dengan sebelumnya", null);
                    return badRequest(Json.toJson(response));
                }
                if(!Encryption.EncryptAESCBCPCKS5Padding(request.getOldPassword()).equals(getMerchantData.password)){
                    response.setBaseResponse(0, 0, 0, "Password lama yang anda masukkan salah", null);
                    return badRequest(Json.toJson(response));
                }
                // =======================================================================

                getMerchantData.password = Encryption.EncryptAESCBCPCKS5Padding(request.getPassword());
                getMerchantData.update();
                trx.commit();

                response.setBaseResponse(1, 0, 1, "Berhasil mengupdate password", getMerchantData.id);
                return ok(Json.toJson(response));
            
            } catch (Exception e) {
                logger.error("Error saat parsing json", e);
                e.printStackTrace();
                trx.rollback();
            } finally {
                trx.end();
            }
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result updateUserMerchantData(Long userMerchantId) {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if(ownMerchant != null){
            Transaction trx = Ebean.beginTransaction();
            try {
                JsonNode json = request().body().asJson();
                ProfileUserMerchantRequest request = objectMapper.readValue(json.toString(), ProfileUserMerchantRequest.class);

                // VALIDATION
                // =======================================================================
                if(request.getEmail() == null || request.getEmail() == ""){
                    response.setBaseResponse(0, 0, 0, "Email tidak boleh kosong", null);
                    return badRequest(Json.toJson(response));
                }
                if(request.getFirstName() == null || request.getFirstName() == ""){
                    response.setBaseResponse(0, 0, 0, "Nama tidak boleh kosong", null);
                    return badRequest(Json.toJson(response));
                }
                if(request.getLastName() == null || request.getLastName() == ""){
                    response.setBaseResponse(0, 0, 0, "Nama tidak boleh kosong", null);
                    return badRequest(Json.toJson(response));
                }
                // =======================================================================

                System.out.println(ownMerchant.id);
                
                UserMerchant getUserMerchantData = UserMerchantRepository.findAccountById(userMerchantId);
                if(getUserMerchantData == null) {
                    response.setBaseResponse(0, 0, 0, "User Merchant tidak ditemukan", null);
                    return notFound(Json.toJson(response));
                }

                if(request.getEmail() != null && request.getEmail() != "" && !request.getEmail().equals(getUserMerchantData.email)){
                    getUserMerchantData.setEmail(request.getEmail());
                    getUserMerchantData.isActive = Boolean.FALSE;
                    String forActivation = Encryption.EncryptAESCBCPCKS5Padding(String.valueOf(getUserMerchantData.id) + String.valueOf(System.currentTimeMillis()));

                    getUserMerchantData.setActivationCode(forActivation);

                    // START TO SEND EMAIL VERIFICATION
                    Thread thread = new Thread(() -> {
                        try {
                            MailConfig.sendmail(request.getEmail(), MailConfig.subjectActivation,
                                    MailConfig.renderVerificationAccount(forActivation, getUserMerchantData.fullName));
        
                        } catch (Exception e) {
                            e.printStackTrace();
                            trx.rollback();
                        } finally {
                            trx.end();
                        }
                    });
                    thread.start();
                }
                getUserMerchantData.setFullName(request.getFirstName()+" "+request.getLastName());
                getUserMerchantData.setFirstName(request.getFirstName());
                getUserMerchantData.setLastName(request.getLastName());
                getUserMerchantData.setRole(getUserMerchantData.getRole());
                getUserMerchantData.update();
                trx.commit();

                response.setBaseResponse(1, 0, 1, "Berhasil mengupdate data", getUserMerchantData.id);
                return ok(Json.toJson(response));
            
            } catch (Exception e) {
                logger.error("Error saat parsing json", e);
                e.printStackTrace();
                trx.rollback();
            } finally {
                trx.end();
            }
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result verifyUserMerchantEmail(String activationCode) {
        Transaction trx = Ebean.beginTransaction();
        try {

            // VALIDATION
            // =======================================================================
            if(activationCode == null || activationCode == ""){
                response.setBaseResponse(0, 0, 0, "Activation code tidak boleh kosong", null);
                return badRequest(Json.toJson(response));
            }
            // =======================================================================
            
            UserMerchant getUserMerchantData = UserMerchantRepository.findByActivationCode(activationCode);
            Merchant getMerchantData = Merchant.findByActivationCode(activationCode);
            if(getUserMerchantData != null) {
                getUserMerchantData.isActive = Boolean.TRUE;
                getUserMerchantData.setActivationCode("");
                getUserMerchantData.update();
                trx.commit();
                return redirect(Helper.MERCHANT_URL+"/activation?success=1");
            } else if(getMerchantData != null) {
                getMerchantData.isActive = Boolean.TRUE;
                getMerchantData.activationCode = "";
                getMerchantData.update();
                trx.commit();
                return redirect(Helper.MERCHANT_URL+"/activation?success=1");
            }
            response.setBaseResponse(0, 0, 0, "Profile tidak ditemukan", null);
            return notFound(Json.toJson(response));
            
        } catch (Exception e) {
            logger.error("Error saat parsing json", e);
            e.printStackTrace();
            trx.rollback();
        } finally {
            trx.end();
        }
        return redirect(Helper.MERCHANT_URL);
    }

    public static Result updatePasswordUserMerchantData(Long userMerchantId) {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if(ownMerchant != null){
            Transaction trx = Ebean.beginTransaction();
            try {
                JsonNode json = request().body().asJson();
                ProfileUserMerchantRequest request = objectMapper.readValue(json.toString(), ProfileUserMerchantRequest.class);
                
                UserMerchant getUserMerchantData = UserMerchantRepository.findAccountById(userMerchantId);
                if(getUserMerchantData == null) {
                    response.setBaseResponse(0, 0, 0, "User Merchant tidak ditemukan", null);
                    return notFound(Json.toJson(response));
                }

                // VALIDATION
                // =======================================================================
                if(request.getPassword() == null || request.getPassword() == ""){
                    response.setBaseResponse(0, 0, 0, "Password tidak boleh kosong", null);
                    return badRequest(Json.toJson(response));
                }
                if(request.getConfirmPassword() == null || request.getConfirmPassword() == ""){
                    response.setBaseResponse(0, 0, 0, "Confirmation Password tidak boleh kosong", null);
                    return badRequest(Json.toJson(response));
                }
                if(!request.getConfirmPassword().equals(request.getPassword())){
                    response.setBaseResponse(0, 0, 0, "Confirmation Password tidak sama", null);
                    return badRequest(Json.toJson(response));
                }
                if(Encryption.EncryptAESCBCPCKS5Padding(request.getPassword()).equals(getUserMerchantData.getPassword())){
                    response.setBaseResponse(0, 0, 0, "Password tidak boleh sama dengan sebelumnya", null);
                    return badRequest(Json.toJson(response));
                }
                if(!Encryption.EncryptAESCBCPCKS5Padding(request.getOldPassword()).equals(getUserMerchantData.getPassword())){
                    response.setBaseResponse(0, 0, 0, "Password lama yang anda masukkan salah", null);
                    return badRequest(Json.toJson(response));
                }
                // =======================================================================                

                getUserMerchantData.setPassword(Encryption.EncryptAESCBCPCKS5Padding(request.getPassword()));
                getUserMerchantData.update();
                trx.commit();

                response.setBaseResponse(1, 0, 1, "Berhasil mengupdate data", getUserMerchantData.id);
                return ok(Json.toJson(response));
            
            } catch (Exception e) {
                logger.error("Error saat parsing json", e);
                e.printStackTrace();
                trx.rollback();
            } finally {
                trx.end();
            }
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }


}
