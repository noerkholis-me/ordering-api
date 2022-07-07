package controllers.loyalty;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Transaction;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hokeba.api.BaseResponse;
import com.wordnik.swagger.annotations.Api;
import controllers.BaseController;
import dtos.loyalty.*;
import models.loyalty.*;
import models.*;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import repository.loyalty.*;
import repository.*;

import java.util.*;
import com.avaje.ebean.Query;

public class LoyaltyPointController extends BaseController {

    private final static Logger.ALogger logger = Logger.of(LoyaltyPointController.class);

    private static BaseResponse response = new BaseResponse();

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Result createLoyaltyPoint(Long subsCategoryId) {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            try {
                JsonNode json = request().body().asJson();

                LoyaltyPointMerchantRequest request = objectMapper.readValue(json.toString(), LoyaltyPointMerchantRequest.class);
                SubsCategoryMerchant subsCategory = SubsCategoryMerchantRepository.findById(subsCategoryId);
                if(subsCategory == null){
                    response.setBaseResponse(0, 0, 0, "Subs category tidak ditemukan", null);
                    return notFound(Json.toJson(response));
                }
                LoyaltyPointMerchant loyaltyPoint = LoyaltyPointMerchantRepository.findBySubsCategoryId(subsCategoryId, ownMerchant.id);
                if(loyaltyPoint != null){
                    response.setBaseResponse(0, 0, 0, "Loyalty setting sudah tersedia untuk category" + subsCategory.getSubscategoryName(), null);
                    return badRequest(Json.toJson(response));
                }
                String validate = validateData(request);
                if (validate == null) {
                    Transaction trx = Ebean.beginTransaction();
                    try {
                        LoyaltyPointMerchant newLoyaltyData = new LoyaltyPointMerchant();
                        newLoyaltyData.setUsageType(request.getUsageType());
                        newLoyaltyData.setLoyaltyUsageValue(request.getLoyaltyUsageValue());
                        newLoyaltyData.setMaxLoyaltyUsageValue(request.getMaxLoyaltyUsageValue());
                        newLoyaltyData.setCashbackType(request.getCashbackType());
                        newLoyaltyData.setCashbackValue(request.getCashbackValue());
                        newLoyaltyData.setMaxCashbackValue(request.getMaxCashbackValue());
                        newLoyaltyData.setSubsCategoryMerchant(subsCategory);
                        newLoyaltyData.setMerchant(ownMerchant);
                        newLoyaltyData.isDeleted = Boolean.FALSE;
                        newLoyaltyData.save();
                    
                        trx.commit();
                        response.setBaseResponse(1, 0, 1, "Berhasil menyimpan data Loyalty", newLoyaltyData.id);
                        return ok(Json.toJson(response));
                    } catch (Exception e) {
                        logger.error("Error saat menyimpan data Loyalty", e);
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

    public static Result updateLoyaltyPoint(Long id, Long subsCategoryId) {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            try {
                JsonNode json = request().body().asJson();

                LoyaltyPointMerchantRequest request = objectMapper.readValue(json.toString(), LoyaltyPointMerchantRequest.class);
                SubsCategoryMerchant subsCategory = SubsCategoryMerchantRepository.findById(subsCategoryId);
                if(subsCategory == null){
                    response.setBaseResponse(0, 0, 0, "Subs category tidak ditemukan", null);
                    return notFound(Json.toJson(response));
                }
                LoyaltyPointMerchant loyaltyPoint = LoyaltyPointMerchantRepository.findByIdandSubsCategoryId(id, subsCategoryId, ownMerchant.id);
                if(loyaltyPoint != null){
                    String validate = validateData(request);
                    if (validate == null) {
                        Transaction trx = Ebean.beginTransaction();
                        try {
                            loyaltyPoint.setUsageType(request.getUsageType());
                            loyaltyPoint.setLoyaltyUsageValue(request.getLoyaltyUsageValue());
                            loyaltyPoint.setMaxLoyaltyUsageValue(request.getMaxLoyaltyUsageValue());
                            loyaltyPoint.setCashbackType(request.getCashbackType());
                            loyaltyPoint.setCashbackValue(request.getCashbackValue());
                            loyaltyPoint.setMaxCashbackValue(request.getMaxCashbackValue());
                            loyaltyPoint.setSubsCategoryMerchant(subsCategory);
                            loyaltyPoint.setMerchant(ownMerchant);
                            loyaltyPoint.isDeleted = Boolean.FALSE;
                            loyaltyPoint.update();
                        
                            trx.commit();
                            response.setBaseResponse(1, 0, 1, "Berhasil menyimpan data Loyalty", loyaltyPoint.id);
                            return ok(Json.toJson(response));
                        } catch (Exception e) {
                            logger.error("Error saat menyimpan data Loyalty", e);
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
                } else {
                    response.setBaseResponse(0, 0, 0, "Loyalty setting tidak tersedia", null);
                    return notFound(Json.toJson(response));
                }
            } catch (Exception e) {
                logger.error("Error saat parsing json", e);
                e.printStackTrace();
            }
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static String validateData(LoyaltyPointMerchantRequest request) {
        if (request == null)
            return "Bidang tidak boleh nol atau kosong";
        if (request.getUsageType() == null)
            return "Usage Type tidak boleh nol atau kosong";
        if (request.getLoyaltyUsageValue() == null)
            return "Loyalty Usage Value tidak boleh nol atau kosong";
        if (request.getMaxLoyaltyUsageValue() == null)
            return "Max Loyalty Usage Value tidak boleh nol atau kosong";
        if (request.getCashbackType() == null)
            return "Cashback Type tidak boleh nol atau kosong";
        if (request.getCashbackValue() == null)
            return "Cashback Value tidak boleh nol atau kosong";
        if (request.getMaxCashbackValue() == null)
            return "Max Cashback Value tidak boleh nol atau kosong";

        return null;
    }

    public static Result listLoyaltyPoint(int offset, int limit) {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            try {
                Query<LoyaltyPointMerchant> query = LoyaltyPointMerchantRepository.find.where().eq("t0.merchant_id", ownMerchant.id).eq("t0.is_deleted", false).order("t0.id asc");
                List<LoyaltyPointMerchant> listDataLoyalty = LoyaltyPointMerchantRepository.getListLoyaltyPoint(query, offset, limit);
                List<LoyaltyPointMerchant> totalData = LoyaltyPointMerchantRepository.getTotalData(query);
                List<LoyaltyPointMerchantResponse> responsesLoyalty = new ArrayList<>();
                for(LoyaltyPointMerchant loyaltyData : listDataLoyalty){
                    LoyaltyPointMerchantResponse responseLoyalty = new LoyaltyPointMerchantResponse();
                    SubsCategoryMerchant subsCategory = SubsCategoryMerchantRepository.findById(loyaltyData.getSubsCategoryMerchant().id);
                    if(subsCategory == null){
                        response.setBaseResponse(0, 0, 0, "Subs category tidak ditemukan", null);
                        return notFound(Json.toJson(response));
                    }
                    responseLoyalty.setId(loyaltyData.id);
                    responseLoyalty.setUsageType(loyaltyData.getUsageType());
                    responseLoyalty.setLoyaltyUsageValue(loyaltyData.getLoyaltyUsageValue());
                    responseLoyalty.setMaxLoyaltyUsageValue(loyaltyData.getMaxLoyaltyUsageValue());
                    responseLoyalty.setCashbackType(loyaltyData.getCashbackType());
                    responseLoyalty.setCashbackValue(loyaltyData.getCashbackValue());
                    responseLoyalty.setMaxCashbackValue(loyaltyData.getMaxCashbackValue());
                    responseLoyalty.setSubsCategoryId(loyaltyData.getSubsCategoryMerchant().id);
                    responseLoyalty.setSubsCategoryName(subsCategory.getSubscategoryName());
                    responseLoyalty.setMerchantId(loyaltyData.getMerchant().id);
                    responseLoyalty.setIsDeleted(loyaltyData.isDeleted);
                    responsesLoyalty.add(responseLoyalty);
                }
                response.setBaseResponse(totalData.size(), offset, limit, "Berhasil menampilkan data Loyalty", responsesLoyalty);
                return ok(Json.toJson(response));
            } catch (Exception e) {
                logger.error("Error saat memanggil data", e);
                e.printStackTrace();
            }
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result deleteLoyalty(Long id) {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            try {
                JsonNode json = request().body().asJson();

                LoyaltyPointMerchant loyaltyPoint = LoyaltyPointMerchantRepository.find.where().eq("t0.id", id).eq("merchant",ownMerchant).findUnique();
                if(loyaltyPoint != null){
                        Transaction trx = Ebean.beginTransaction();
                        try {
                            loyaltyPoint.isDeleted = Boolean.TRUE;
                            loyaltyPoint.update();
                        
                            trx.commit();
                            response.setBaseResponse(0, 0, 0, "Berhasil menghapus data loyalty", null);
                            return ok(Json.toJson(response));
                        } catch (Exception e) {
                            logger.error("Error saat menghapus data Loyalty", e);
                            e.printStackTrace();
                            trx.rollback();
                        } finally {
                            trx.end();
                        }
                        response.setBaseResponse(0, 0, 0, error, null);
                        return badRequest(Json.toJson(response));
                } else {
                    response.setBaseResponse(0, 0, 0, "Loyalty setting tidak tersedia", null);
                    return notFound(Json.toJson(response));
                }
            } catch (Exception e) {
                logger.error("Error saat parsing json", e);
                e.printStackTrace();
            }
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result useLoyalty(String email, String phoneNumber, String storeCode) {
        if (email != null || phoneNumber != null) {
            try {
                JsonNode json = request().body().asJson();
                Store store = Store.find.where().eq("t0.store_code", storeCode).findUnique();
                LoyaltyMemberResponse lmResponse = new LoyaltyMemberResponse();
                Member memberData = null;
                memberData = Member.find.where().eq("t0.email", email).eq("merchant", store.merchant).eq("t0.is_active", true).eq("t0.is_deleted", false).findUnique();
                if(memberData == null) {
                    memberData = Member.find.where().eq("t0.phone", phoneNumber).eq("merchant", store.merchant).eq("t0.is_active", true).eq("t0.is_deleted", false).findUnique();
                }
                if(memberData != null){
                    lmResponse.setFullName(memberData.fullName);
                    lmResponse.setEmail(memberData.email);
                    lmResponse.setPhone(memberData.phone);
                    lmResponse.setLoyaltyPoint(memberData.loyaltyPoint);
                    response.setBaseResponse(1, 0, 1, "Data loyalty berhasil di tampilkan", lmResponse);
                    return ok(Json.toJson(response));
                } else {
                    response.setBaseResponse(0, 0, 0, "Data tidak ditemukan", null);
                    return notFound(Json.toJson(response));
                }
            } catch (Exception e) {
                logger.error("Error saat parsing json", e);
                e.printStackTrace();
            }
        }
        response.setBaseResponse(0, 0, 0, "Data email / nomor telepon diperlukan", null);
        return unauthorized(Json.toJson(response));
    }

    // public static Result updateStatusPickUpPoint(Long id) {
    //     Merchant ownMerchant = checkMerchantAccessAuthorization();
    //     if (ownMerchant != null) {
    //         try {
    //             JsonNode json = request().body().asJson();

    //             PickUpPointResponse request = objectMapper.readValue(json.toString(), PickUpPointResponse.class);
    //             Transaction trx = Ebean.beginTransaction();
    //             PickUpPointMerchant pickuppoint = PickUpPointRepository.findByIdandMerchantId(id, ownMerchant.id);

    //             if(pickuppoint != null){
    //                 try {
    //                     pickuppoint.setIsActive(request.getIsActive());
    //                     pickuppoint.update();
                        
    //                     trx.commit();
    //                     response.setBaseResponse(1, 0, 1, success + " mengubah status pick up point", pickuppoint);
    //                     return ok(Json.toJson(response));
    //                 } catch (Exception e) {
    //                     logger.error("Error saat mengubah status pick up point", e);
    //                     e.printStackTrace();
    //                     trx.rollback();
    //                 } finally {
    //                     trx.end();
    //                 }
    //                 response.setBaseResponse(0, 0, 0, error, null);
    //                 return badRequest(Json.toJson(response));
    //             } else {
    //                 response.setBaseResponse(0, 0, 0, "Data tidak ditemukan", null);
    //                 return badRequest(Json.toJson(response));
    //             }
    //         } catch (Exception e) {
    //             logger.error("Error saat parsing json", e);
    //             e.printStackTrace();
    //         }
    //     }
    //     response.setBaseResponse(0, 0, 0, unauthorized, null);
    //     return unauthorized(Json.toJson(response));
    // }

    // public static Result getPickupPointForKiosK(Long storeId) {
    //     if (storeId != null) {
    //         try {
    //             // JsonNode json = request().body().asJson();

    //             // PickUpPointResponse request = objectMapper.readValue(json.toString(), PickUpPointResponse.class);
    //             // Transaction trx = Ebean.beginTransaction();
                
    //             //For PUPSetup 
    //             PickUpPointSetup puPointSetup = PickUpPointSetupRepository.findByStoreId(storeId);
    //             PickupPointKiosKResponse responsePickupPoint = new PickupPointKiosKResponse();

    //             if(puPointSetup != null) {

    //                 //For PUPList
    //                 List<PickupPointKiosKResponse.PickupPointMerchant> responseDatas = new ArrayList<>();
                    
    //                 Query<PickUpPointMerchant> queryPuPointList = PickUpPointRepository.find.where().eq("t0.is_deleted", false).eq("t0.store_id", storeId).order("t0.id");
    //                 List<PickUpPointMerchant> pickuppointList = PickUpPointRepository.getListPickUpPoint(queryPuPointList, "", "", 0, 0, storeId);

    //                 responsePickupPoint.setId(puPointSetup.id);
    //                 responsePickupPoint.setImagePUPSetup(puPointSetup.getImagePupointSetup());
    //                 responsePickupPoint.setStoreId(puPointSetup.getStore().id);
    //                 responsePickupPoint.setMerchantId(puPointSetup.getMerchant().id);
    //                 for(PickUpPointMerchant puPointMerchantData : pickuppointList) {
    //                     PickupPointKiosKResponse.PickupPointMerchant responseData = new PickupPointKiosKResponse.PickupPointMerchant();
    //                     responseData.setId(puPointMerchantData.id);
    //                     responseData.setPuPointName(puPointMerchantData.getPupointName());
    //                     responseDatas.add(responseData);
    //                     responsePickupPoint.setPickUpList(responseDatas);
    //                 }
                    
    //                 response.setBaseResponse(1, 0, 1, "Menampilkan data", responsePickupPoint);
    //                 return ok(Json.toJson(response));
    //             } else {
    //                 response.setBaseResponse(0, 0, 0, "Data tidak ditemukan", null);
    //                 return badRequest(Json.toJson(response));
    //             }

    //         } catch (Exception e) {
    //             logger.error("Error saat parsing json", e);
    //             e.printStackTrace();
    //         }
    //     }
    //     response.setBaseResponse(0, 0, 0, "Store code tidak boleh kosong", null);
    //     return unauthorized(Json.toJson(response));
    // }
}