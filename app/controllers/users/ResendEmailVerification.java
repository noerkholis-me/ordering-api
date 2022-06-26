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

@Api(value = "/users/resend/verification", description = "User and Merchant Profile")
public class ResendEmailVerification extends BaseController {

    private final static Logger.ALogger logger = Logger.of(ResendEmailVerification.class);

    private static BaseResponse response = new BaseResponse();

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Result resendEmailVerificationUserMerchant(String emailAddress) {
        try {
            // VALIDATION
            // =======================================================================
            if (emailAddress == null || emailAddress == "") {
                response.setBaseResponse(0, 0, 0, "Email tidak boleh kosong", null);
                return badRequest(Json.toJson(response));
            }
            // =======================================================================

            UserMerchant getUserMerchantData = UserMerchantRepository.forResendEmail(emailAddress);

            if (getUserMerchantData != null && getUserMerchantData.getPassword() != null
                    && getUserMerchantData.isActive == false) {
                getUserMerchantData.isActive = Boolean.FALSE;

                // CREATE ACTIVATION CODE
                String forActivation = Encryption.EncryptAESCBCPCKS5Padding(
                        String.valueOf(getUserMerchantData.id) + String.valueOf(System.currentTimeMillis()));
                getUserMerchantData.setActivationCode(forActivation);

                // START TO SEND EMAIL VERIFICATION
                Thread thread = new Thread(() -> {
                    try {
                        MailConfig.sendmail(getUserMerchantData.getEmail(), MailConfig.subjectActivation,
                                MailConfig.renderVerificationAccount(forActivation, getUserMerchantData.fullName));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                thread.start();

                getUserMerchantData.update();

                response.setBaseResponse(0, 0, 0, "Berhasil mengirim ulang pesan. Silahkan cek Email!", null);
                return notFound(Json.toJson(response));
            } else if (getUserMerchantData != null && getUserMerchantData.getPassword() == null
                    && getUserMerchantData.isActive == false) {
                getUserMerchantData.isActive = Boolean.FALSE;

                // CREATE ACTIVATION CODE
                String forActivation = Encryption.EncryptAESCBCPCKS5Padding(
                        String.valueOf(getUserMerchantData.id) + String.valueOf(System.currentTimeMillis()));
                getUserMerchantData.setActivationCode(forActivation);

                // START TO SEND EMAIL VERIFICATION
                Thread thread = new Thread(() -> {
                    try {
                        MailConfig.sendmail(getUserMerchantData.getEmail(), MailConfig.subjectActivation,
                                MailConfig.renderMailSendCreatePasswordCMSTemplate(forActivation,
                                        getUserMerchantData.fullName));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                thread.start();

                getUserMerchantData.update();

                response.setBaseResponse(0, 0, 0, "Berhasil mengirim ulang pesan. Silahkan cek Email!", null);
                return notFound(Json.toJson(response));
            }
            response.setBaseResponse(0, 0, 0, "Profile tidak ditemukan", null);
            return notFound(Json.toJson(response));

        } catch (Exception e) {
            logger.error("Error saat parsing json", e);
            e.printStackTrace();
        }
        response.setBaseResponse(0, 0, 0, "Error json parsing", null);
        return badRequest(Json.toJson(response));
    }

}
