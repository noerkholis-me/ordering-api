package controllers.store;

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
import dtos.store.*;
import models.store.*;
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

@Api(value = "/merchants/category", description = "Category Merchant")
public class StoreAccessController extends BaseController {

    private final static Logger.ALogger logger = Logger.of(StoreAccessController.class);

    private static BaseResponse response = new BaseResponse();

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Result assignStoreAccess() {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            try {
                // Http.MultipartFormData body = request().body().asMultipartFormData();
                JsonNode json = request().body().asJson();
                StoreAccessRequest request = objectMapper.readValue(json.toString(), StoreAccessRequest.class);
                UserMerchant um = UserMerchantRepository.findById(request.getUserMerchantId(), ownMerchant);
                if (um == null) {
                    response.setBaseResponse(0, 0, 0, "User tidak ditemukan", null);
                    return notFound(Json.toJson(response));
                }
                List<Store> store = request.getStoreId();
                String validate = validateAssignStore(request);
                if (validate == null) {
                    Transaction trx = Ebean.beginTransaction();
                    try {
                        StoreAccess dataStoreAccess = StoreAccessRepository.findById(um.id);
                        if (dataStoreAccess != null) {
                            response.setBaseResponse(0, 0, 0, "User sudah terdaftar", null);
                            return badRequest(Json.toJson(response));
                        }
                        StoreAccess newStoreAccess = new StoreAccess();
                        newStoreAccess.setUserMerchant(um);
                        newStoreAccess.setMerchant(ownMerchant);
                        newStoreAccess.setIsActive(Boolean.TRUE);
                        newStoreAccess.isDeleted = Boolean.FALSE;
                        newStoreAccess.save();
                        for (Store stores : store) {
                            Store dataStore = Store.findById(stores.id);
                            if (dataStore != null) {
                                StoreAccessDetail storeAccessDetail = new StoreAccessDetail();
                                storeAccessDetail.setStoreAccess(newStoreAccess);
                                storeAccessDetail.isDeleted = Boolean.FALSE;
                                storeAccessDetail.setStore(dataStore);
                                storeAccessDetail.save();
                            }
                        }

                        trx.commit();
                        response.setBaseResponse(1, offset, 1, "berhasil assign user ke store", newStoreAccess);
                        return ok(Json.toJson(response));
                    } catch (Exception e) {
                        logger.error("Error saat assign store", e);
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

    public static String validateAssignStore(StoreAccessRequest request) {
        if (request == null)
            return "Bidang tidak boleh nol atau kosong";
        if (request.getUserMerchantId() == null)
            return "User tidak boleh nol atau kosong";
        if (request.getStoreId() == null)
            return "Store tidak boleh nol atau kosong";

        return null;
    }

    public static Result listUserAssign(String filter, String sort, int offset, int limit) {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            Query<StoreAccess> query = StoreAccessRepository.find.fetch("userMerchant").where()
                    .eq("t0.is_deleted", false).eq("t0.is_active", true).eq("merchant", ownMerchant)
                    .order("t0.id desc");
            try {
                List<StoreAccessResponse> responsesStoreAccess = new ArrayList<>();
                List<StoreAccess> totalData = StoreAccessRepository.getTotalData(query);
                List<StoreAccess> responseIndex = StoreAccessRepository.getDataStoreAccess(query, sort, filter, offset,
                        limit);
                for (StoreAccess data : responseIndex) {
                    Query<StoreAccessDetail> queryDetail = StoreAccessRepository.findDetail.where()
                            .eq("t0.store_access_id", data.id).eq("t0.is_deleted", false).order("t0.id");
                    List<StoreAccessDetail> dataDetail = StoreAccessRepository.getDetailData(queryDetail);
                    List<StoreAccessResponse.StoreAccessDetail> responsesDetail = new ArrayList<>();

                    StoreAccessResponse responseStoreAccess = new StoreAccessResponse();
                    responseStoreAccess.setId(data.id);
                    responseStoreAccess.setUserMerchantId(data.getUserMerchant().id);
                    responseStoreAccess.setUserName(data.getUserMerchant().getFullName());
                    responseStoreAccess.setMerchantId(data.getMerchant().id);
                    responseStoreAccess.setIsActive(data.getIsActive());
                    for (StoreAccessDetail storeDetail : dataDetail) {
                        StoreAccessResponse.StoreAccessDetail responseDetail = new StoreAccessResponse.StoreAccessDetail();
                        Store storeDataFetch = Store.findById(storeDetail.getStore().id);
                        responseDetail.setId(storeDataFetch.id);
                        responseDetail.setStoreName(storeDataFetch.storeName);
                        responseDetail.setIsActive(storeDataFetch.isActive);
                        responsesDetail.add(responseDetail);
                        responseStoreAccess.setStoreData(responseDetail != null ? responsesDetail : null);
                    }
                    responsesStoreAccess.add(responseStoreAccess);
                }
                response.setBaseResponse(filter == null || filter.equals("") ? totalData.size() : responseIndex.size(),
                        offset, limit, "berhasil menampilkan data", responsesStoreAccess);
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

    public static Result detailUserAssign(Long userId) {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            Query<StoreAccess> query = StoreAccessRepository.find.where().eq("t0.is_deleted", false)
                    .eq("t0.is_active", true).eq("merchant", ownMerchant).eq("t0.user_merchant_id", userId)
                    .order("t0.id desc");
            try {
                StoreAccess dataAccess = StoreAccessRepository.findById(userId);

                if (dataAccess != null) {
                    Query<StoreAccessDetail> queryDetail = StoreAccessRepository.findDetail.where()
                            .eq("t0.store_access_id", dataAccess.id).eq("t0.is_deleted", false).order("t0.id");
                    List<StoreAccessDetail> dataDetail = StoreAccessRepository.getDetailData(queryDetail);
                    List<StoreAccessResponse.StoreAccessDetail> responsesDetail = new ArrayList<>();

                    StoreAccessResponse responseStoreAccess = new StoreAccessResponse();
                    if (dataAccess != null) {
                        UserMerchant userMerchant = UserMerchantRepository.findById(dataAccess.getUserMerchant().id,
                                ownMerchant);
                        responseStoreAccess.setId(dataAccess.id);
                        responseStoreAccess.setUserMerchantId(dataAccess.getUserMerchant().id);
                        responseStoreAccess.setUserName(userMerchant.fullName);
                        responseStoreAccess.setMerchantId(dataAccess.getMerchant().id);
                        responseStoreAccess.setIsActive(dataAccess.getIsActive());
                        for (StoreAccessDetail storeDetail : dataDetail) {
                            StoreAccessResponse.StoreAccessDetail responseDetail = new StoreAccessResponse.StoreAccessDetail();
                            Store storeDataFetch = Store.findById(storeDetail.getStore().id);
                            responseDetail.setId(storeDataFetch.id);
                            responseDetail.setStoreName(storeDataFetch.storeName);
                            responseDetail.setIsActive(storeDataFetch.isActive);
                            responsesDetail.add(responseDetail);
                            responseStoreAccess.setStoreData(responseDetail != null ? responsesDetail : null);
                        }
                    } else {
                        response.setBaseResponse(0, 0, 0, "Tidak ditemukan data", null);
                        return badRequest(Json.toJson(response));
                    }

                    response.setBaseResponse(1, 0, 1, "berhasil menampilkan data", responseStoreAccess);
                    return ok(Json.toJson(response));
                } else {
                    response.setBaseResponse(0, 0, 0, "data tidak ditemukan", null);
                    return notFound(Json.toJson(response));
                }
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

    public static Result updateStoreAccess(Long idAssign) {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            try {
                // Http.MultipartFormData body = request().body().asMultipartFormData();
                JsonNode json = request().body().asJson();
                StoreAccessRequest request = objectMapper.readValue(json.toString(), StoreAccessRequest.class);
                UserMerchant um = UserMerchantRepository.findById(request.getUserMerchantId(), ownMerchant);
                if (um == null) {
                    response.setBaseResponse(0, 0, 0, "User tidak ditemukan", null);
                    return notFound(Json.toJson(response));
                }
                List<Store> store = request.getStoreId();
                String validate = validateAssignStore(request);
                if (validate == null) {
                    Transaction trx = Ebean.beginTransaction();
                    try {
                        StoreAccess newStoreAccess = StoreAccessRepository.findByIdAndMerchantId(idAssign, ownMerchant);
                        if (newStoreAccess != null) {
                            newStoreAccess.setUserMerchant(um);
                            newStoreAccess.setMerchant(ownMerchant);
                            // newStoreAccess.setIsActive(request.getIsActive());
                            // newStoreAccess.isDeleted = request.getIsDeleted();
                            newStoreAccess.update();

                            // SOFT DELETE ALL DATA WITH RELATED ID
                            List<StoreAccessDetail> storeDetailDataList = StoreAccessRepository
                                    .findByIdStoreAccess(newStoreAccess.id);
                            for (StoreAccessDetail storeAccessDetail : storeDetailDataList) {
                                storeAccessDetail.isDeleted = Boolean.TRUE;
                                storeAccessDetail.update();
                            }

                            // SEARCH DATA THAT RELATED TO STORE ID REQUEST
                            for (Store stores : store) {
                                Store dataStore = Store.findById(stores.id);
                                if (dataStore != null) {
                                    StoreAccessDetail storeDetailData = StoreAccessRepository
                                            .findByIdStore(dataStore.id, newStoreAccess.id);
                                    // UPDATE DATA THAT RELATED TO STORE ID REQUEST
                                    if (storeDetailData != null) {
                                        storeDetailData.setStoreAccess(newStoreAccess);
                                        storeDetailData.setStore(dataStore);
                                        storeDetailData.isDeleted = Boolean.FALSE;
                                        storeDetailData.update();
                                    } else {
                                        // ADD DATA IF NOT EXIST
                                        StoreAccessDetail storeAccessDetail = new StoreAccessDetail();
                                        storeAccessDetail.setStoreAccess(newStoreAccess);
                                        storeAccessDetail.isDeleted = Boolean.FALSE;
                                        storeAccessDetail.setStore(dataStore);
                                        storeAccessDetail.save();
                                    }
                                } else {
                                    response.setBaseResponse(0, 0, 0, "Data Store tidak ditemukan", null);
                                    return notFound(Json.toJson(response));
                                }
                            }
                            trx.commit();
                            response.setBaseResponse(1, 0, 1, "berhasil update assign user ke store", newStoreAccess);
                            return ok(Json.toJson(response));
                        } else {
                            response.setBaseResponse(0, 0, 0, "Data tidak ditemukan", null);
                            return notFound(Json.toJson(response));
                        }
                    } catch (Exception e) {
                        logger.error("Error saat update assign store", e);
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

    public static Result deleteStoreAssign(Long idAssign) {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            try {
                Transaction trx = Ebean.beginTransaction();
                try {
                    StoreAccess newStoreAccess = StoreAccessRepository.findByIdAndMerchantId(idAssign, ownMerchant);
                    if (newStoreAccess != null) {
                        newStoreAccess.setIsActive(Boolean.FALSE);
                        newStoreAccess.isDeleted = Boolean.TRUE;
                        newStoreAccess.update();
                        Query<StoreAccessDetail> queryDetail = StoreAccessRepository.findDetail.where()
                                .eq("t0.store_access_id", idAssign).eq("t0.is_deleted", false).order("t0.id");
                        List<StoreAccessDetail> storeDetailData = StoreAccessRepository.findByIdAssign(queryDetail);
                        for (StoreAccessDetail storeaccess : storeDetailData) {
                            storeaccess.isDeleted = Boolean.TRUE;
                            storeaccess.update();
                        }
                        trx.commit();
                        response.setBaseResponse(1, 0, 1, "berhasil menghapus store access", null);
                        return ok(Json.toJson(response));
                    } else {
                        response.setBaseResponse(0, 0, 0, "Data tidak ditemukan", null);
                        return notFound(Json.toJson(response));
                    }
                } catch (Exception e) {
                    logger.error("Error saat hapus assign store", e);
                    e.printStackTrace();
                    trx.rollback();
                } finally {
                    trx.end();
                }
                response.setBaseResponse(0, 0, 0, error, null);
                return badRequest(Json.toJson(response));
            } catch (Exception e) {
                logger.error("Error saat parsing json", e);
                e.printStackTrace();
            }
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

}
