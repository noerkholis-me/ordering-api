package controllers.settings;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Transaction;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hokeba.api.BaseResponse;
import com.hokeba.util.Constant;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiImplicitParam;
import com.wordnik.swagger.annotations.ApiImplicitParams;
import com.wordnik.swagger.annotations.ApiOperation;
import controllers.BaseController;
import dtos.appsetting.*;
import models.Merchant;
import models.appsettings.*;
import models.Photo;
import models.internal.DeviceType;
import models.internal.PaymentMethod;
import models.internal.PaymentMethodConfig;
import models.merchant.MerchantPayment;
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
import java.util.stream.Collectors;

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

                AppSettingRequest request = objectMapper.readValue(json.toString(), AppSettingRequest.class);
                String validate = validateData(request);
                AppSettings appSetting = AppSettingRepository.findByMerchantId(ownMerchant.id);
                if(appSetting == null) {
                    if (validate == null) {
                        Transaction trx = Ebean.beginTransaction();
                        try {
                            AppSettings newAppSetting = new AppSettings();
                            newAppSetting.setMerchantName(ownMerchant.name);
                            newAppSetting.setMerchant(ownMerchant);
                            // mobile qr
                            newAppSetting.setMobileQrName(request.getAppSettingMobileQrRequest().getMobileQrName());
                            newAppSetting.setPrimaryColor(request.getAppSettingMobileQrRequest().getPrimaryColor());
                            newAppSetting.setSecondaryColor(request.getAppSettingMobileQrRequest().getSecondaryColor());
                            newAppSetting.setAppLogo(request.getAppSettingMobileQrRequest().getAppLogo());
                            newAppSetting.setFavicon(request.getAppSettingMobileQrRequest().getFavicon());
                            newAppSetting.setThreshold(request.getAppSettingMobileQrRequest().getThreshold());
                            // kiosk
                            newAppSetting.setKioskName(request.getAppSettingKioskRequest().getKioskName());
                            newAppSetting.setPrimaryColorKiosk(request.getAppSettingKioskRequest().getPrimaryColor());
                            newAppSetting.setSecondaryColorKiosk(request.getAppSettingKioskRequest().getSecondaryColor());
                            newAppSetting.setAppLogoKiosk(request.getAppSettingKioskRequest().getAppLogo());
                            newAppSetting.setFaviconKiosk(request.getAppSettingKioskRequest().getFavicon());

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

                AppSettingRequest request = objectMapper.readValue(json.toString(), AppSettingRequest.class);
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
                        appSetting.setMerchantName(ownMerchant.name);
                        appSetting.setMerchant(ownMerchant);
                        // mobile qr
                        appSetting.setMobileQrName(request.getAppSettingMobileQrRequest().getMobileQrName());
                        appSetting.setPrimaryColor(request.getAppSettingMobileQrRequest().getPrimaryColor());
                        appSetting.setSecondaryColor(request.getAppSettingMobileQrRequest().getSecondaryColor());
                        appSetting.setAppLogo(request.getAppSettingMobileQrRequest().getAppLogo());
                        appSetting.setFavicon(request.getAppSettingMobileQrRequest().getFavicon());
                        appSetting.setThreshold(request.getAppSettingMobileQrRequest().getThreshold());
                        // kiosk
                        appSetting.setKioskName(request.getAppSettingKioskRequest().getKioskName());
                        appSetting.setPrimaryColorKiosk(request.getAppSettingKioskRequest().getPrimaryColor());
                        appSetting.setSecondaryColorKiosk(request.getAppSettingKioskRequest().getSecondaryColor());
                        appSetting.setAppLogoKiosk(request.getAppSettingKioskRequest().getAppLogo());
                        appSetting.setFaviconKiosk(request.getAppSettingKioskRequest().getFavicon());

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

    public static String validateData(AppSettingRequest request) {
        if (request == null)
            return "Bidang tidak boleh nol atau kosong";
        if (request.getAppSettingKioskRequest().getKioskName() == null)
            return "Nama Merchant kiosk tidak boleh nol atau kosong";
        if (request.getAppSettingKioskRequest().getPrimaryColor() == null)
            return "Warna Utama kiosk tidak boleh nol atau kosong";
        if (request.getAppSettingKioskRequest().getSecondaryColor() == null)
            return "Warna Kedua kiosk tidak boleh nol atau kosong";
        if (request.getAppSettingKioskRequest().getAppLogo() == null)
            return "Logo Aplikasi kiosk tidak boleh nol atau kosong";
        if (request.getAppSettingKioskRequest().getFavicon() == null)
            return "Favicon kiosk tidak boleh nol atau kosong";
        if (request.getAppSettingMobileQrRequest().getMobileQrName() == null)
            return "Nama Merchant mobile qr tidak boleh nol atau kosong";
        if (request.getAppSettingMobileQrRequest().getPrimaryColor() == null)
            return "Warna Utama mobule qr tidak boleh nol atau kosong";
        if (request.getAppSettingMobileQrRequest().getSecondaryColor() == null)
            return "Warna Kedua mobile qr tidak boleh nol atau kosong";
        if (request.getAppSettingMobileQrRequest().getAppLogo() == null)
            return "Logo Aplikasi mobile qr tidak boleh nol atau kosong";
        if (request.getAppSettingMobileQrRequest().getFavicon() == null)
            return "Favicon mobile qr tidak boleh nol atau kosong";
        if (request.getAppSettingMobileQrRequest().getThreshold() == null)
            return "Threshold tidak boleh nol atau kosong";

        return null;
    }

    public static Result getAppSetting(Long merchantId, String deviceType) {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        AppSettings appSetting = new AppSettings();
        if(merchantId != null || ownMerchant != null) {
            if (merchantId != 0){
                appSetting = AppSettingRepository.findByMerchantId(merchantId);
            } else if (ownMerchant != null) {
                appSetting = AppSettingRepository.findByMerchantId(ownMerchant.id);
            }
            if (deviceType == null || deviceType.equalsIgnoreCase("")) {
                response.setBaseResponse(1, 0, 1, inputParameter + " device type tidak boleh kosong", null);
                return badRequest(Json.toJson(response));
            }
            try {
                if (appSetting != null) {
                    if (deviceType.equalsIgnoreCase(DeviceType.KIOSK.getDevice())) {
                        AppSettingResponse appSettingResponse = new AppSettingResponse();
                        appSettingResponse.setId(appSetting.id);
                        appSettingResponse.setMerchantId(appSetting.getMerchant().id);
                        appSettingResponse.setMerchantName(appSetting.getKioskName());
                        appSettingResponse.setPrimaryColor(appSetting.getPrimaryColorKiosk());
                        appSettingResponse.setSecondaryColor(appSetting.getSecondaryColorKiosk());
                        appSettingResponse.setAppLogo(appSetting.getAppLogoKiosk());
                        appSettingResponse.setFavicon(appSetting.getFaviconKiosk());
                        appSettingResponse.setThreshold(0);
                        appSettingResponse.setIsDeleted(appSetting.isDeleted);

                        response.setBaseResponse(1, 0, 1, success + " menampilkan data", appSettingResponse);
                        return ok(Json.toJson(response));
                    } else if (deviceType.equalsIgnoreCase(DeviceType.MOBILEQR.getDevice())) {
                        AppSettingResponse appSettingResponse = new AppSettingResponse();
                        appSettingResponse.setId(appSetting.id);
                        appSettingResponse.setMerchantId(appSetting.getMerchant().id);
                        appSettingResponse.setMerchantName(appSetting.getMobileQrName());
                        appSettingResponse.setPrimaryColor(appSetting.getPrimaryColor());
                        appSettingResponse.setSecondaryColor(appSetting.getSecondaryColor());
                        appSettingResponse.setAppLogo(appSetting.getAppLogo());
                        appSettingResponse.setFavicon(appSetting.getAppLogo());
                        appSettingResponse.setThreshold(appSetting.getThreshold());
                        appSettingResponse.setIsDeleted(appSetting.isDeleted);
                        response.setBaseResponse(1, 0, 1, success + " menampilkan data", appSettingResponse);
                        return ok(Json.toJson(response));
                    } else {
                        response.setBaseResponse(1, 0, 1, inputParameter + " device type tidak sesuai", null);
                        return badRequest(Json.toJson(response));
                    }
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
                String url = Constant.getInstance().getImageUrl();

                // main list
                List<MerchantPayment> merchantPayments = MerchantPayment.findByMerchantId(merchant.id);

                SettingApplicationResponse settingApps = new SettingApplicationResponse();
                settingApps.setId(appSettings.id);
                settingApps.setMerchantId(merchant.id);

                String imageGuideKiosk = url.concat("assets/images/kiosk-setting-legend.png");

                // filter list
                List<MerchantPayment> merchantKioskPayments = merchantPayments.stream().filter(mp -> mp.getDevice().equalsIgnoreCase(DeviceType.KIOSK.getDevice())).collect(Collectors.toList());
                AppSettingPaymentTypeResponse paymentSettingKiosk = new AppSettingPaymentTypeResponse();
                for (MerchantPayment merchantPayment : merchantKioskPayments) {
                    PaymentMethod paymentMethod = merchantPayment.getPaymentMethod();
                    PaymentMethodConfig paymentMethodConfig = PaymentMethodConfig.convertToPaymentMethodConfig(paymentMethod.getPaymentCode());
                    constructPaymentType(paymentSettingKiosk, paymentMethodConfig, merchantPayment);
                }
                settingApps.setAppSettingKioskResponse(new AppSettingKioskResponse(
                        appSettings.getKioskName(),
                        appSettings.getPrimaryColorKiosk(),
                        appSettings.getSecondaryColorKiosk(),
                        appSettings.getAppLogoKiosk(),
                        appSettings.getFaviconKiosk(),
                        imageGuideKiosk,
                        paymentSettingKiosk
                ));

                // filter list
                List<MerchantPayment> merchantPosPayments = merchantPayments.stream().filter(mp -> mp.getDevice().equalsIgnoreCase(DeviceType.MINIPOS.getDevice())).collect(Collectors.toList());
                AppSettingPaymentTypeResponse paymentSettingPos = new AppSettingPaymentTypeResponse();
                for (MerchantPayment merchantPayment : merchantPosPayments) {
                    PaymentMethod paymentMethod = merchantPayment.getPaymentMethod();
                    PaymentMethodConfig paymentMethodConfig = PaymentMethodConfig.convertToPaymentMethodConfig(paymentMethod.getPaymentCode());
                    constructPaymentType(paymentSettingPos, paymentMethodConfig, merchantPayment);
                }
                settingApps.setAppSettingPosResponse(new AppSettingPosResponse(paymentSettingPos));

                // filter list
                List<MerchantPayment> merchantMobileQrPayments = merchantPayments.stream().filter(mp -> mp.getDevice().equalsIgnoreCase(DeviceType.MOBILEQR.getDevice())).collect(Collectors.toList());
                AppSettingPaymentTypeResponse paymentSettingMobileQr = new AppSettingPaymentTypeResponse();
                for (MerchantPayment merchantPayment : merchantMobileQrPayments) {
                    PaymentMethod paymentMethod = merchantPayment.getPaymentMethod();
                    PaymentMethodConfig paymentMethodConfig = PaymentMethodConfig.convertToPaymentMethodConfig(paymentMethod.getPaymentCode());
                    constructPaymentType(paymentSettingMobileQr, paymentMethodConfig, merchantPayment);
                }
                String imageGuideMobileQr = url.concat("assets/images/mobile-qr-legend.png");
                settingApps.setAppSettingMobileQrResponse(new AppSettingMobileQrResponse(
                        appSettings.getMobileQrName(),
                        appSettings.getPrimaryColor(),
                        appSettings.getSecondaryColor(),
                        appSettings.getAppLogo(),
                        appSettings.getFavicon(),
                        appSettings.getThreshold(),
                        imageGuideMobileQr,
                        paymentSettingMobileQr
                ));

                response.setBaseResponse(1, 0, 1, success + " menampilkan data", settingApps);
                return ok(Json.toJson(response));
            } catch (Exception e) {
                response.setBaseResponse(0, 0, 0, error, null);
                return internalServerError(Json.toJson(response));
            }
        }
    }

    private static void constructPaymentType(AppSettingPaymentTypeResponse appSettingPaymentTypeResponse, PaymentMethodConfig paymentMethodConfig, MerchantPayment merchantPayment) {
        switch (paymentMethodConfig) {
            case QRCODE:
                appSettingPaymentTypeResponse.setIsQris(Boolean.TRUE);
                appSettingPaymentTypeResponse.setTypeQris(merchantPayment.getTypePayment());
                break;
            case CASH:
                appSettingPaymentTypeResponse.setIsCash(Boolean.TRUE);
                appSettingPaymentTypeResponse.setTypeCash(merchantPayment.getTypePayment());
                break;
            case DEBITCREDIT:
                appSettingPaymentTypeResponse.setIsDebitCredit(Boolean.TRUE);
                appSettingPaymentTypeResponse.setTypeDebitCredit(merchantPayment.getTypePayment());
                break;
            case VIRTUALACCOUNT:
                appSettingPaymentTypeResponse.setIsVirtualAccount(Boolean.TRUE);
                appSettingPaymentTypeResponse.setTypeVirtualAccount(merchantPayment.getTypePayment());
                break;
            default:
                break;
        }
    }





}