package controllers.settings;

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
import dtos.appsetting.*;
import models.Merchant;
import models.appsettings.*;
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

@Api(value = "/merchants/appsetting", description = "App Merchant")
public class AppSettingsController extends BaseController {

    private final static Logger.ALogger logger = Logger.of(AppSettingsController.class);

    private static BaseResponse response = new BaseResponse();

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Result createAppSetting() {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            try {
                JsonNode json = request().body().asJson();

                AppSettingResponse request = objectMapper.readValue(json.toString(), AppSettingResponse.class);
                String validate = validateData(request);
                AppSettings appSetting = AppSettingRepository.findByMerchantId(ownMerchant.id);
                if(appSetting == null) {
                    if (validate == null) {
                        Transaction trx = Ebean.beginTransaction();
                        try {
                            AppSettings newAppSetting = new AppSettings();
                            newAppSetting.setMerchantName(request.getMerchantName());
                            newAppSetting.setPrimaryColor(request.getPrimaryColor());
                            newAppSetting.setSecondaryColor(request.getSecondaryColor());
                            newAppSetting.setAppLogo(request.getAppLogo());
                            newAppSetting.setFavicon(request.getFavicon());
                            newAppSetting.setThreshold(request.getThreshold());
                            newAppSetting.setMerchant(ownMerchant);
                            newAppSetting.isDeleted = Boolean.FALSE;
                            newAppSetting.save();
                            
                            trx.commit();
                            response.setBaseResponse(1, offset, 1, success + " menyimpan pengaturan aplikasi", newAppSetting);
                            return ok(Json.toJson(response));
                        } catch (Exception e) {
                            logger.error("Error saat menyimpan pengaturan aplikasi", e);
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
                }
                response.setBaseResponse(0, 0, 0, "Maaf, pengaturan sudah tersedia.", null);
                return badRequest(Json.toJson(response));
            } catch (Exception e) {
                logger.error("Error saat parsing json", e);
                e.printStackTrace();
            }
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result updateAppSetting(Long id) {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            try {
                JsonNode json = request().body().asJson();

                AppSettingResponse request = objectMapper.readValue(json.toString(), AppSettingResponse.class);
                // System.out.print(appSetting.id);
                String validate = validateData(request);
                if (validate == null) {
                    Transaction trx = Ebean.beginTransaction();
                    try {
                        // System.out.print(id);
                        // System.out.print(merchantId);
                        AppSettings appSetting = AppSettingRepository.findByIdandMerchantId(id, ownMerchant.id);
                        if (appSetting == null) {
                            response.setBaseResponse(0, 0, 0, error + " pengaturan tidak tersedia.", null);
                            return badRequest(Json.toJson(response));
                        }
                        appSetting.setMerchantName(request.getMerchantName());
                        appSetting.setPrimaryColor(request.getPrimaryColor());
                        appSetting.setSecondaryColor(request.getSecondaryColor());
                        appSetting.setAppLogo(request.getAppLogo());
                        appSetting.setFavicon(request.getFavicon());
                        appSetting.setThreshold(request.getThreshold());
                        appSetting.setMerchant(ownMerchant);
                        appSetting.isDeleted = request.getIsDeleted();
                        appSetting.update();
                        trx.commit();
                        response.setBaseResponse(1, offset, 1, success + " mengupdate pengaturan aplikasi", appSetting);
                        return ok(Json.toJson(response));
                    } catch (Exception e) {
                        logger.error("Error saat mengupdate pengaturan aplikasi", e);
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
            } catch (Exception e) {
                logger.error("Error saat parsing json", e);
                e.printStackTrace();
            }
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static String validateData(AppSettingResponse request) {
        if (request == null)
            return "Bidang tidak boleh nol atau kosong";
        if (request.getMerchantName() == null)
            return "Nama Merchant tidak boleh nol atau kosong";
        if (request.getPrimaryColor() == null)
            return "Warna Utama tidak boleh nol atau kosong";
        if (request.getSecondaryColor() == null)
            return "Warna Kedua tidak boleh nol atau kosong";
        if (request.getAppLogo() == null)
            return "Logo Aplikasi tidak boleh nol atau kosong";
        if (request.getFavicon() == null)
            return "Favicon tidak boleh nol atau kosong";
        if (request.getThreshold() == null)
            return "Threshold tidak boleh nol atau kosong";

        return null;
    }

    public static Result getAppSetting(Long merchantId) {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        AppSettings appSetting = new AppSettings();
        if(merchantId != null || ownMerchant != null) {
            if (merchantId != 0){
                appSetting = AppSettingRepository.findByMerchantId(merchantId);
            } else if (ownMerchant != null) {
                appSetting = AppSettingRepository.findByMerchantId(ownMerchant.id);
            }
            try {
                AppSettingResponse responseApp = new AppSettingResponse();
                if (appSetting != null) {
                    responseApp.setId(appSetting.id);
                    responseApp.setMerchantName(appSetting.getMerchantName());
                    responseApp.setPrimaryColor(appSetting.getPrimaryColor());
                    responseApp.setSecondaryColor(appSetting.getSecondaryColor());
                    responseApp.setAppLogo(appSetting.getAppLogo());
                    responseApp.setFavicon(appSetting.getFavicon());
                    responseApp.setThreshold(appSetting.getThreshold());
                    responseApp.setMerchantId(appSetting.getMerchant().id);
                    responseApp.setIsDeleted(appSetting.isDeleted);

                    response.setBaseResponse(1, 0, 1, success + " menampilkan data", responseApp);
                    return ok(Json.toJson(response));
                } else {
                    response.setBaseResponse(0, 0, 0, "Pengaturan Aplikasi masih kosong", null);
                    return ok(Json.toJson(response));
                }
            } catch (Exception e) {
                Logger.error("ERROR", e);
            }
        } else {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result getApplicationSetting() {
        Merchant merchant = checkMerchantAccessAuthorization();
        if (merchant == null) {
            response.setBaseResponse(0, 0, 0, unauthorized, null);
            return unauthorized(Json.toJson(response));
        } else {
            try {
                AppSettings appSettings = AppSettingRepository.findByMerchantId(merchant.id);
                if (appSettings == null) {
                    response.setBaseResponse(0, 0, 0, inputParameter + " app setting tidak ditemukan.", null);
                    return badRequest(Json.toJson(response));
                }

                SettingApplicationResponse settingApps = new SettingApplicationResponse();
                settingApps.setId(appSettings.id);
                settingApps.setMerchantId(merchant.id);
                settingApps.setMerchantName(merchant.name);
                settingApps.setAppSettingKioskResponse(new AppSettingKioskResponse(
                        appSettings.getPrimaryColorKiosk(),
                        appSettings.getSecondaryColorKiosk(),
                        appSettings.getAppLogoKiosk(),
                        appSettings.getFaviconKiosk()
                ));
                settingApps.setAppSettingPaymentTypeResponse(
                        new AppSettingPaymentTypeResponse(
                                // merchant.isCash(),
                                // merchant.getTypeCash(),
                                // merchant.isDebitCredit(),
                                // merchant.getTypeDebitCredit(),
                                // merchant.isQris(),
                                // merchant.getTypeQris()
                        )
                );
                settingApps.setAppSettingMobileQrResponse(
                        new AppSettingMobileQrResponse(
                                appSettings.getPrimaryColor(),
                                appSettings.getSecondaryColor(),
                                appSettings.getAppLogo(),
                                appSettings.getFavicon(),
                                appSettings.getThreshold()
                        )
                );

                response.setBaseResponse(1, 0, 1, success + " menampilkan data", settingApps);
                return ok(Json.toJson(response));
            } catch (Exception e) {
                response.setBaseResponse(0, 0, 0, error, null);
                return internalServerError(Json.toJson(response));
            }
        }
    }





}