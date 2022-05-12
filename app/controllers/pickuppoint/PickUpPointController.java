package controllers.pickuppoint;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Transaction;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hokeba.api.BaseResponse;
import com.wordnik.swagger.annotations.Api;
import controllers.BaseController;
import dtos.pupoint.*;
import models.Merchant;
import models.Store;
import models.pupoint.*;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import repository.pickuppoint.*;

import java.util.*;
import com.avaje.ebean.Query;

@Api(value = "/merchants/pickuppoint", description = "Pick Up Point")
public class PickUpPointController extends BaseController {

    private final static Logger.ALogger logger = Logger.of(PickUpPointController.class);

    private static BaseResponse response = new BaseResponse();

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Result createPickUpPoint() {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            try {
                JsonNode json = request().body().asJson();

                PickUpPointResponse request = objectMapper.readValue(json.toString(), PickUpPointResponse.class);
                Store store = Store.findById(request.getStoreId());
                String validate = validateData(request);
                if (validate == null) {
                    Transaction trx = Ebean.beginTransaction();
                    try {
                        PickUpPointMerchant puPoint = new PickUpPointMerchant();
                        puPoint.setPupointName(request.getPupointName());
                        puPoint.setStore(store);
                        puPoint.setMerchant(ownMerchant);
                        puPoint.isActive = request.getIsActive();
                        puPoint.isDeleted = Boolean.FALSE;
                        puPoint.save();
                    
                        trx.commit();
                        response.setBaseResponse(1, 0, 1, success + " menyimpan data pick up point", puPoint);
                        return ok(Json.toJson(response));
                    } catch (Exception e) {
                        logger.error("Error saat menyimpan data pick up point", e);
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

    public static Result updatePickUpPoint(Long id) {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            try {
                JsonNode json = request().body().asJson();

                PickUpPointResponse request = objectMapper.readValue(json.toString(), PickUpPointResponse.class);
                Store store = Store.findById(request.getStoreId());
                String validate = validateData(request);
                if (validate == null) {
                    Transaction trx = Ebean.beginTransaction();
                    PickUpPointMerchant pickuppoint = PickUpPointRepository.findByIdandMerchantId(id, ownMerchant.id);

                    if(pickuppoint != null){
                        try {
                            pickuppoint.setPupointName(request.getPupointName());
                            pickuppoint.setStore(store);
                            pickuppoint.setMerchant(ownMerchant);
                            pickuppoint.setIsActive(request.getIsActive());
                            pickuppoint.isDeleted = Boolean.FALSE;
                            pickuppoint.update();
                        
                            trx.commit();
                            response.setBaseResponse(1, 0, 1, success + " mengubah data pick up point", pickuppoint);
                            return ok(Json.toJson(response));
                        } catch (Exception e) {
                            logger.error("Error saat mengubah data pick up point", e);
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

    public static String validateData(PickUpPointResponse request) {
        if (request == null)
            return "Bidang tidak boleh nol atau kosong";
        if (request.getPupointName() == null)
            return "Nama Pick Up Point tidak boleh nol atau kosong";
        if (request.getStoreId() == null)
            return "Store tidak boleh nol atau kosong";
        if (request.getPupointName().length() > 20)
            return "Tidak boleh lebih dari 20 karakter";

        return null;
    }

    public static Result getPickupPoint(Long id) {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            try {
                JsonNode json = request().body().asJson();

                PickUpPointMerchant pickuppoint = PickUpPointRepository.findByIdandMerchantId(id, ownMerchant.id);
                Transaction trx = Ebean.beginTransaction();
                if(pickuppoint != null){
                    try {
                        PickUpPointResponse puPointResponse = new PickUpPointResponse();
                        puPointResponse.setId(pickuppoint.id);
                        puPointResponse.setPupointName(pickuppoint.getPupointName());
                        puPointResponse.setStoreId(pickuppoint.getStore().id);
                        puPointResponse.setMerchantId(pickuppoint.getMerchant().id);
                        puPointResponse.setIsActive(pickuppoint.getIsActive());
                        puPointResponse.setIsDeleted(pickuppoint.isDeleted);
                    
                        trx.commit();
                        response.setBaseResponse(1, 0, 1, success + " menampilkan data pick up point", puPointResponse);
                        return ok(Json.toJson(response));
                    } catch (Exception e) {
                        logger.error("Error saat menampilkan data pick up point", e);
                        e.printStackTrace();
                        trx.rollback();
                    } finally {
                        trx.end();
                    }
                    response.setBaseResponse(0, 0, 0, error, null);
                    return badRequest(Json.toJson(response));
                } else {
                    response.setBaseResponse(0, 0, 0, "Data pickup point tidak ditemukan", null);
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

    public static Result listPickupPoint(String filter, String sort, int offset, int limit, Long storeId) {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            Query<PickUpPointMerchant> query = PickUpPointRepository.find.where().eq("t0.is_deleted", false).eq("t0.merchant_id", ownMerchant.id).order("t0.id");
            try {
                List<PickUpPointResponse> responses = new ArrayList<>();
                List<PickUpPointMerchant> totalData = PickUpPointRepository.getTotalData(query);
                List<PickUpPointMerchant> responseIndex = PickUpPointRepository.getListPickUpPoint(query, sort, filter, offset, limit, storeId);
                for (PickUpPointMerchant data : responseIndex) {
                    PickUpPointResponse puPointResponse = new PickUpPointResponse();
                    puPointResponse.setId(data.id);
                    puPointResponse.setPupointName(data.getPupointName());
                    puPointResponse.setStoreId(data.getStore().id);
                    puPointResponse.setMerchantId(data.getMerchant().id);
                    puPointResponse.setIsActive(data.getIsActive());
                    puPointResponse.setIsDeleted(data.isDeleted);
                    responses.add(puPointResponse);
                    
                }
                response.setBaseResponse(filter == null || filter.equals("") ? totalData.size() : responseIndex.size(), offset, limit, success + " menampilkan data pick up point", responses);
                return ok(Json.toJson(response));
            } catch (Exception e) {
                logger.error("Error", e);
                e.printStackTrace();
            }
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result deletePickUpPoint(Long id) {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            try {
                JsonNode json = request().body().asJson();

                Transaction trx = Ebean.beginTransaction();
                PickUpPointMerchant pickuppoint = PickUpPointRepository.findByIdandMerchantId(id, ownMerchant.id);

                if(pickuppoint != null){
                    try {
                        pickuppoint.isDeleted = Boolean.TRUE;
                        pickuppoint.update();
                    
                        trx.commit();
                        response.setBaseResponse(0, 0, 0, success + " menghapus data pick up point", null);
                        return ok(Json.toJson(response));
                    } catch (Exception e) {
                        logger.error("Error saat menghapus data pick up point", e);
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

    public static Result updateStatusPickUpPoint(Long id) {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            try {
                JsonNode json = request().body().asJson();

                PickUpPointResponse request = objectMapper.readValue(json.toString(), PickUpPointResponse.class);
                Transaction trx = Ebean.beginTransaction();
                PickUpPointMerchant pickuppoint = PickUpPointRepository.findByIdandMerchantId(id, ownMerchant.id);

                if(pickuppoint != null){
                    try {
                        pickuppoint.setIsActive(request.getIsActive());
                        pickuppoint.update();
                        
                        trx.commit();
                        response.setBaseResponse(1, 0, 1, success + " mengubah status pick up point", pickuppoint);
                        return ok(Json.toJson(response));
                    } catch (Exception e) {
                        logger.error("Error saat mengubah status pick up point", e);
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