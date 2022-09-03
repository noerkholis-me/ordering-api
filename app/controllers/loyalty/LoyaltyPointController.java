package controllers.loyalty;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Transaction;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hokeba.api.BaseResponse;
import com.hokeba.util.Helper;
import controllers.BaseController;
import dtos.loyalty.*;
import models.loyalty.*;
import models.*;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import repository.loyalty.*;
import repository.*;

import java.math.BigDecimal;
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
                Query<LoyaltyPointMerchant> query = LoyaltyPointMerchantRepository.find.where().eq("merchant", ownMerchant).eq("t0.is_deleted", false).order("t0.id asc");
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
                Store store = Store.find.where().eq("t0.store_code", storeCode).findUnique();
                if(store == null){
                    response.setBaseResponse(0, 0, 0, "Store code tidak boleh kosong", null);
                    return badRequest(Json.toJson(response));
                }
                LoyaltyMemberResponse lmResponse = new LoyaltyMemberResponse();
                Member memberData = null;
                if(email != null && !email.isEmpty()){
                    if(!Helper.isValidEmailAddress(email)){
                        response.setBaseResponse(0, 0, 0, "Email tidak valid", null);
                        return badRequest(Json.toJson(response));
                    }
                    memberData = Member.find.where().eq("t0.email", email).eq("merchant", store.getMerchant()).eq("t0.is_active", true).eq("t0.is_deleted", false).setMaxRows(1).findUnique();
                }

                if(memberData == null && phoneNumber != null && !phoneNumber.isEmpty()){
                    if(!Helper.isValidPhoneNumber(phoneNumber)){
                        response.setBaseResponse(0, 0, 0, "Nomor telepon tidak valid", null);
                        return badRequest(Json.toJson(response));
                    }
                    memberData = Member.find.where().eq("t0.phone", phoneNumber).eq("merchant", store.getMerchant()).eq("t0.is_active", true).eq("t0.is_deleted", false).findUnique();
                }
                
                if(memberData != null){
                    LoyaltyPointHistory lpHistory = LoyaltyPointHistoryRepository.find.where().eq("member", memberData).order("t0.id desc").setMaxRows(1).findUnique();
                    lmResponse.setFullName(memberData.fullName != null && !memberData.fullName.isEmpty() ? memberData.fullName : "GENERAL CUSTOMER");
                    lmResponse.setEmail(memberData.email);
                    lmResponse.setPhone(memberData.phone);
                    lmResponse.setLoyaltyPoint(memberData.loyaltyPoint);
                    lmResponse.setExpiredDate(lpHistory.getExpiredDate());
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

    public static Result checkMember() {
        MerchantLog merchantLog = checkUserAccessAuthorization();
        if (merchantLog != null && (merchantLog.userMerchant != null || merchantLog.merchant != null)) {
            try {
                JsonNode json = request().body().asJson();
                LoyaltyCheckMemberRequest request = objectMapper.readValue(json.toString(), LoyaltyCheckMemberRequest.class);
                Merchant merchant = merchantLog.userMerchant.getRole().getMerchant() != null ? merchantLog.userMerchant.getRole().getMerchant() : merchantLog.merchant;
                LoyaltyCheckMemberResponse lmResponse = new LoyaltyCheckMemberResponse();
                Member memberData = null;
                if(request.getEmail() != null && !request.getEmail().isEmpty()){
                    if(!Helper.isValidEmailAddress(request.getEmail())){
                        response.setBaseResponse(0, 0, 0, "Email tidak valid", null);
                        return badRequest(Json.toJson(response));
                    }
                    memberData = Member.find.where().eq("t0.email", request.getEmail()).eq("merchant", merchant).eq("t0.is_active", true).eq("t0.is_deleted", false).setMaxRows(1).findUnique();
                    if(memberData == null){
                        if(request.getFullName().isEmpty()){
                            response.setBaseResponse(0, 0, 0, "Nama pemesan tidak boleh kosong", null);
                            return badRequest(Json.toJson(response));
                        }
                        Member newMember = new Member();
                        newMember.phone = "";
                        newMember.firstName = Helper.getFirstName(request.getFullName());
                        newMember.lastName = Helper.getLastName(request.getFullName());
                        newMember.fullName = request.getFullName();
                        newMember.email = request.getEmail();
                        newMember.lastPurchase = null;
                        newMember.setMerchant(merchant);
                        newMember.isActive = true;
                        newMember.loyaltyPoint = new BigDecimal(0);
                        newMember.save();
                        memberData = newMember;
                    }
                }

                if(memberData == null && request.getPhoneNumber() != null && !request.getPhoneNumber().isEmpty()){
                    if(!Helper.isValidPhoneNumber(request.getPhoneNumber())){
                        response.setBaseResponse(0, 0, 0, "Nomor telepon tidak valid", null);
                        return badRequest(Json.toJson(response));
                    }
                    memberData = Member.find.where().eq("t0.phone", request.getPhoneNumber()).eq("merchant", merchant).eq("t0.is_active", true).eq("t0.is_deleted", false).findUnique();
                    if(memberData == null){
                        if(request.getFullName().isEmpty()){
                            response.setBaseResponse(0, 0, 0, "Nama pemesan tidak boleh kosong", null);
                            return badRequest(Json.toJson(response));
                        }
                        Member newMember = new Member();
                        newMember.phone = request.getPhoneNumber();
                        newMember.firstName = Helper.getFirstName(request.getFullName());
                        newMember.lastName = Helper.getLastName(request.getFullName());
                        newMember.fullName = request.getFullName();
                        newMember.email = "";
                        newMember.lastPurchase = null;
                        newMember.setMerchant(merchant);
                        newMember.isActive = true;
                        newMember.loyaltyPoint = new BigDecimal(0);
                        newMember.save();
                        memberData = newMember;
                    }
                }

                if(memberData != null){
                    lmResponse.setMemberId(memberData.id);
                    lmResponse.setFirstName(memberData.firstName);
                    lmResponse.setLastName(memberData.lastName);
                    lmResponse.setFullName(memberData.fullName);
                    lmResponse.setEmail(memberData.email);
                    lmResponse.setPhone(memberData.phone);
                    lmResponse.setIsHaveLoyaltyPoint(memberData.loyaltyPoint.compareTo(new BigDecimal(0)) > 0);
                    lmResponse.setLoyaltyPoint(memberData.loyaltyPoint.intValue());
                    response.setBaseResponse(1, 0, 1, memberData.loyaltyPoint != BigDecimal.ZERO ? "Loyalty Point Member: Rp. " + lmResponse.getLoyaltyPoint() : "Tidak ada point", memberData.loyaltyPoint != BigDecimal.ZERO ? lmResponse : null);
                    return ok(Json.toJson(response));
                } else if(request.getPhoneNumber().isEmpty() || request.getEmail().isEmpty()) {
                    response.setBaseResponse(0, 0, 0, "Nomor telepon / email diperlukan", null);
                } else {
                    response.setBaseResponse(0, 0, 0, "Data tidak ditemukan", null);
                    return notFound(Json.toJson(response));
                }
            } catch (Exception e) {
                logger.error("Error saat parsing json", e);
                e.printStackTrace();
            }
        } else {
            response.setBaseResponse(0, 0, 0, "User tidak memiliki akses", null);
        }
        return unauthorized(Json.toJson(response));
    }
    
    public static Result historyLoyaltyMember(String email, String phoneNumber, String storeCode, int offset, int limit, String type) {
        if (email != null || phoneNumber != null) {
            try {
                Store store = Store.find.where().eq("t0.store_code", storeCode).findUnique();
                List<LoyaltyPointHistoryResponse> lmhResponseList = new ArrayList<>();
                Member memberData = null;
                if(email != null && !email.isEmpty()){
                    if(!Helper.isValidEmailAddress(email)){
                        response.setBaseResponse(0, 0, 0, "Email tidak valid", null);
                        return badRequest(Json.toJson(response));
                    }
                    memberData = Member.find.where().eq("t0.email", email).eq("merchant", store.getMerchant()).eq("t0.is_active", true).eq("t0.is_deleted", false).setMaxRows(1).findUnique();
                }

                if(memberData == null && phoneNumber != null && !phoneNumber.isEmpty()){
                    if(!Helper.isValidPhoneNumber(phoneNumber)){
                        response.setBaseResponse(0, 0, 0, "Nomor telepon tidak valid", null);
                        return badRequest(Json.toJson(response));
                    }
                    memberData = Member.find.where().eq("t0.phone", phoneNumber).eq("merchant", store.getMerchant()).eq("t0.is_active", true).eq("t0.is_deleted", false).findUnique();
                }
                
                if(memberData != null){
                    Query<LoyaltyPointHistory> query = null;
                    if (type != null && !type.isEmpty()) {
                        if (type.equalsIgnoreCase("Pendapatan")) {
                            query = LoyaltyPointHistoryRepository.find.where().ne("t0.added", BigDecimal.ZERO).eq("member", memberData).order("t0.id desc");
                        } else if (type.equalsIgnoreCase("Pengeluaran")) {
                            query = LoyaltyPointHistoryRepository.find.where().ne("t0.used", BigDecimal.ZERO).eq("member", memberData).order("t0.id desc");
                        }
                    }
                    int totalData = LoyaltyPointHistoryRepository.getTotalData(query).size();
                    List<LoyaltyPointHistory> lpHistoryList = LoyaltyPointHistoryRepository.getListLoyaltyPointHistory(query, offset, limit);
                    if (lpHistoryList.size() > 0) {
                        for (LoyaltyPointHistory lpHistory : lpHistoryList) {
                            LoyaltyPointHistoryResponse lmhResponse = new LoyaltyPointHistoryResponse();
                            lmhResponse.setFullName(lpHistory.getMember().fullName != null && !lpHistory.getMember().fullName.isEmpty() ? lpHistory.getMember().fullName : "GENERAL CUSTOMER");
                            lmhResponse.setEmail(lpHistory.getMember().email);
                            lmhResponse.setPhone(lpHistory.getMember().phone);
                            lmhResponse.setOrderNumber(lpHistory.getOrder().getOrderNumber());
                            lmhResponse.setPoint(lpHistory.getPoint());
                            lmhResponse.setAdded(lpHistory.getAdded());
                            lmhResponse.setUsed(lpHistory.getUsed());
                            lmhResponse.setExpiredDate(lpHistory.getExpiredDate());
                            lmhResponseList.add(lmhResponse);
                        }
                        response.setBaseResponse(totalData, offset, limit, "History point berhasil di tampilkan", lmhResponseList);
                        return ok(Json.toJson(response));
                    }
                    response.setBaseResponse(0, 0, 0, "History point tidak tersedia", null);
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
}