package controllers.pickuppoint;

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
import dtos.pupoint.*;
import models.Merchant;
import models.Store;
import models.pupoint.*;
import play.Logger;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Http;
import play.mvc.Result;
import repository.pickuppoint.*;

import java.io.File;
import java.util.*;
import com.avaje.ebean.Query;
import utils.ImageDirectory;
import utils.ImageUtil;

import java.io.IOException;

@Api(value = "/merchants/pupointsetup", description = "Pick Up Point Setup")
public class PickUpPointSetupController extends BaseController {

    private final static Logger.ALogger logger = Logger.of(PickUpPointSetupController.class);

    private static BaseResponse response = new BaseResponse();

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Result createPickUpPointSetup() {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            try {
                JsonNode json = request().body().asJson();

                PickUpPointSetupResponse request = objectMapper.readValue(json.toString(), PickUpPointSetupResponse.class);
                Store store = Store.findById(request.getStoreId());
                String validate = validateData(request);
                if (validate == null) {
                    Transaction trx = Ebean.beginTransaction();
                    try {
                        PickUpPointSetup puPointSetup = new PickUpPointSetup();
                        puPointSetup.setImagePupointSetup(request.getImagePupointSetup());
                        puPointSetup.setStore(store);
                        puPointSetup.setMerchant(ownMerchant);
                        puPointSetup.isDeleted = Boolean.FALSE;
                        puPointSetup.save();
                    
                        trx.commit();
                        response.setBaseResponse(1, 0, 1, success + " menyimpan data pick up point setup", puPointSetup);
                        return ok(Json.toJson(response));
                    } catch (Exception e) {
                        logger.error("Error saat menyimpan data pick up point setup", e);
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

    public static Result updatePickUpPointSetup(Long id) {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            try {
                JsonNode json = request().body().asJson();

                PickUpPointSetupResponse request = objectMapper.readValue(json.toString(), PickUpPointSetupResponse.class);
                Store store = Store.findById(request.getStoreId());
                String validate = validateData(request);
                if (validate == null) {
                    Transaction trx = Ebean.beginTransaction();
                    PickUpPointSetup pickuppointsetup = PickUpPointSetupRepository.findByIdandMerchantId(id, ownMerchant.id);

                    if(pickuppointsetup != null){
                        try {
                            pickuppointsetup.setImagePupointSetup(request.getImagePupointSetup());
                            pickuppointsetup.setStore(store);
                            pickuppointsetup.setMerchant(ownMerchant);
                            pickuppointsetup.isDeleted = Boolean.FALSE;
                            pickuppointsetup.update();
                        
                            trx.commit();
                            response.setBaseResponse(1, 0, 1, success + " mengubah data pick up point setup", pickuppointsetup);
                            return ok(Json.toJson(response));
                        } catch (Exception e) {
                            logger.error("Error saat mengubah data pick up point setup", e);
                            e.printStackTrace();
                            trx.rollback();
                        } finally {
                            trx.end();
                        }
                        response.setBaseResponse(0, 0, 0, error, null);
                        return badRequest(Json.toJson(response));
                    } else {
                        response.setBaseResponse(0, 0, 0, "Data tidak ditemukan", null);
                        return badRequest(Json.toJson(response));
                    }
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

    public static String validateData(PickUpPointSetupResponse request) {
        if (request == null)
            return "Bidang tidak boleh nol atau kosong";
        if (request.getImagePupointSetup() == null)
            return "Gambar Pick Up Point tidak boleh nol atau kosong";
        if (request.getStoreId() == null)
            return "Store tidak boleh nol atau kosong";

        return null;
    }

    public static Result getPickupPointSetup(Long id) {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            try {
                JsonNode json = request().body().asJson();

                PickUpPointSetup pickuppointsetup = PickUpPointSetupRepository.findByIdandMerchantId(id, ownMerchant.id);
                Transaction trx = Ebean.beginTransaction();
                if(pickuppointsetup != null){
                    try {
                        PickUpPointSetupResponse puPointSetupResponse = new PickUpPointSetupResponse();
                        puPointSetupResponse.setId(pickuppointsetup.id);
                        puPointSetupResponse.setImagePupointSetup(pickuppointsetup.getImagePupointSetup());
                        puPointSetupResponse.setStoreId(pickuppointsetup.getStore().id);
                        puPointSetupResponse.setMerchantId(pickuppointsetup.getMerchant().id);
                        puPointSetupResponse.setIsDeleted(pickuppointsetup.isDeleted);
                    
                        trx.commit();
                        response.setBaseResponse(1, 0, 1, success + " menampilkan data pick up point setup", puPointSetupResponse);
                        return ok(Json.toJson(response));
                    } catch (Exception e) {
                        logger.error("Error saat menampilkan data pick up point setup", e);
                        e.printStackTrace();
                        trx.rollback();
                    } finally {
                        trx.end();
                    }
                    response.setBaseResponse(0, 0, 0, error, null);
                    return badRequest(Json.toJson(response));
                } else {
                    response.setBaseResponse(0, 0, 0, "Data pickup point setup tidak ditemukan", null);
                        return ok(Json.toJson(response));
                }
            } catch (Exception e) {
                logger.error("Error saat parsing json", e);
                e.printStackTrace();
            }
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result listPickupPointSetup(String filter, String sort, int offset, int limit) {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            Query<PickUpPointSetup> query = PickUpPointSetupRepository.find.where().eq("t0.is_deleted", false).eq("t0.merchant_id", ownMerchant.id).order("t0.id");
            try {
                List<PickUpPointSetupResponse> responses = new ArrayList<>();
                List<PickUpPointSetup> totalData = PickUpPointSetupRepository.getTotalData(query);
                List<PickUpPointSetup> responseIndex = PickUpPointSetupRepository.getListPickUpPointSetup(query, sort, filter, offset, limit);
                for (PickUpPointSetup data : responseIndex) {
                    PickUpPointSetupResponse puPointSetupResponse = new PickUpPointSetupResponse();
                    puPointSetupResponse.setId(data.id);
                    puPointSetupResponse.setImagePupointSetup(data.getImagePupointSetup());
                    puPointSetupResponse.setStoreId(data.getStore().id);
                    puPointSetupResponse.setMerchantId(data.getMerchant().id);
                    puPointSetupResponse.setIsDeleted(data.isDeleted);
                    responses.add(puPointSetupResponse);
                    
                }
                response.setBaseResponse(filter == null || filter.equals("") ? totalData.size() : responseIndex.size(), offset, limit, success + " menampilkan data pick up point setup", responses);
                return ok(Json.toJson(response));
            } catch (Exception e) {
                logger.error("Error", e);
                e.printStackTrace();
            }
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result deletePickUpPointSetup(Long id) {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            try {
                JsonNode json = request().body().asJson();

                Transaction trx = Ebean.beginTransaction();
                PickUpPointSetup pickuppointsetup = PickUpPointSetupRepository.findByIdandMerchantId(id, ownMerchant.id);

                if(pickuppointsetup != null){
                    try {
                        pickuppointsetup.isDeleted = Boolean.TRUE;
                        pickuppointsetup.update();
                    
                        trx.commit();
                        response.setBaseResponse(0, 0, 0, success + " menghapus data pick up point setup", null);
                        return ok(Json.toJson(response));
                    } catch (Exception e) {
                        logger.error("Error saat menghapus data pick up point setup", e);
                        e.printStackTrace();
                        trx.rollback();
                    } finally {
                        trx.end();
                    }
                    response.setBaseResponse(0, 0, 0, error, null);
                    return badRequest(Json.toJson(response));
                } else {
                    response.setBaseResponse(0, 0, 0, "Data tidak ditemukan", null);
                    return badRequest(Json.toJson(response));
                }
            } catch (Exception e) {
                logger.error("Error saat parsing json", e);
                e.printStackTrace();
            }
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }
}