package controllers.delivery;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Transaction;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hokeba.api.BaseResponse;
import controllers.BaseController;
import dtos.delivery.DeliverySettingRequest;
import dtos.delivery.DeliverySettingResponse;
import models.Merchant;
import models.Store;
import models.delivery.DeliverySetting;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import repository.StoreRepository;
import repository.delivery.DeliverySettingRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class DeliverySettingController extends BaseController {

    private static final Logger.ALogger logger = Logger.of(DeliverySettingController.class);
    private static BaseResponse response = new BaseResponse();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Result getAllDeliverySetting(int offset, int limit) {
        try {
            Merchant merchant = checkMerchantAccessAuthorization();
            if (merchant != null) {
                int totalData = DeliverySettingRepository.findAll(merchant.id, 0, 0).size();
                List<DeliverySetting> data = DeliverySettingRepository.findAll(merchant.id, offset, limit);

                response.setBaseResponse(totalData, offset, limit, "Menampilkan list data pengaturan pengiriman.", listResponse(data));
                return ok(Json.toJson(response));
            } else {
                response.setBaseResponse(0, 0, 0, unauthorized, null);
                return unauthorized(Json.toJson(response));
            }
        } catch (Exception e) {
            logger.error("Error when get all : " + e.getMessage());
            response.setBaseResponse(0, 0, 0, e.getMessage(), null);
            return badRequest(Json.toJson(response));
        }
    }

    public static Result getById(Long id) {
        try {
            Merchant merchant = checkMerchantAccessAuthorization();
            if (merchant != null) {
                DeliverySetting deliverySetting = DeliverySettingRepository.findById(id);
                if (deliverySetting == null) {
                    response.setBaseResponse(0, 0, 0, "Pengaturan pengiriman tidak ditemukan atau belum dibuat.", null);
                    return badRequest(Json.toJson(response));
                }

                response.setBaseResponse(1, 0, 0, "Menampilkan data pengaturan pengiriman.", detailResponse(deliverySetting));
                return ok(Json.toJson(response));
            } else {
                response.setBaseResponse(0, 0, 0, unauthorized, null);
                return unauthorized(Json.toJson(response));
            }
        } catch (Exception e) {
            logger.error("Error when get by id: " + e.getMessage());
            response.setBaseResponse(0, 0, 0, e.getMessage(), null);
            return badRequest(Json.toJson(response));
        }
    }

    public static Result getByStoreId(Long id) {
        try {
            Merchant merchant = checkMerchantAccessAuthorization();
            if (merchant != null) {
                DeliverySetting deliverySetting = DeliverySettingRepository.findByStoreId(id);
                if (deliverySetting == null) {
                    response.setBaseResponse(0, 0, 0, "Pengaturan pengiriman tidak ditemukan atau belum dibuat.", null);
                    return badRequest(Json.toJson(response));
                }

                response.setBaseResponse(1, 0, 0, "Menampilkan data pengaturan pengiriman.", detailResponse(deliverySetting));
                return ok(Json.toJson(response));
            } else {
                response.setBaseResponse(0, 0, 0, unauthorized, null);
                return unauthorized(Json.toJson(response));
            }
        } catch (Exception e) {
            logger.error("Error when get by store id : " + e.getMessage());
            response.setBaseResponse(0, 0, 0, e.getMessage(), null);
            return badRequest(Json.toJson(response));
        }
    }

    public static Result getByStoreIdNoAuth(Long id) {
        try {
            int authority = checkAccessAuthorization("all");
            if (authority == 200 || authority == 203) {
                DeliverySetting deliverySetting = DeliverySettingRepository.findByStoreId(id);
                if (deliverySetting == null) {
                    response.setBaseResponse(0, 0, 0, "Pengaturan pengiriman tidak ditemukan atau belum dibuat.", null);
                    return badRequest(Json.toJson(response));
                }

                response.setBaseResponse(1, 0, 0, "Menampilkan data pengaturan pengiriman.", detailResponse(deliverySetting));
                return ok(Json.toJson(response));
            } else {
                response.setBaseResponse(0, 0, 0, forbidden, null);
                return unauthorized(Json.toJson(response));
            }
        } catch (Exception e) {
            logger.error("Error when get by store id : " + e.getMessage());
            response.setBaseResponse(0, 0, 0, e.getMessage(), null);
            return badRequest(Json.toJson(response));
        }
    }

    public static Result createDeliverySetting() {
        try {
            Merchant merchant = checkMerchantAccessAuthorization();
            if (merchant != null) {
                JsonNode json = request().body().asJson();
                DeliverySettingRequest request = objectMapper.readValue(json.toString(), DeliverySettingRequest.class);
                if (request.getIsShipper() != null && !request.getIsShipper()) {
                    String validate = validate(request);
                    if (validate != null) {
                        response.setBaseResponse(0, 0, 0, validate, null);
                        return badRequest(Json.toJson(response));
                    }
                }
                Store store = StoreRepository.findByStoreId(request.getStoreId());
                if (store == null) {
                    response.setBaseResponse(0, 0, 0, "Toko tidak ditemukan.", null);
                    return badRequest(Json.toJson(response));
                }
                Transaction trx = Ebean.beginTransaction();
                try {
                    // TODO: update data when input store value is exist
                    DeliverySetting deliverySetting = DeliverySettingRepository.findByStoreId(store.id);
                    if (deliverySetting == null) {
                        deliverySetting = new DeliverySetting(request, merchant, store);
                        deliverySetting.save();
                    } else {
                        deliverySetting.updateDeliverySetting(request, deliverySetting, merchant, store);
                        deliverySetting.update();
                    }
                    trx.commit();

                    response.setBaseResponse(1, 0, 0, "Berhasil menyimpan data pengaturan pengiriman", detailResponse(deliverySetting));
                    return ok(Json.toJson(response));
                } catch (Exception e) {
                    trx.rollback();
                    logger.error("Error when create :", e);
                    response.setBaseResponse(0, 0, 0, e.getMessage(), null);
                    return badRequest(Json.toJson(response));
                } finally {
                    trx.end();
                }
            } else {
                response.setBaseResponse(0, 0, 0, unauthorized, null);
                return unauthorized(Json.toJson(response));
            }
        } catch (Exception e) {
            logger.error("Error when create : " + e.getMessage());
            response.setBaseResponse(0, 0, 0, e.getMessage(), null);
            return badRequest(Json.toJson(response));
        }
    }

    public static Result updateDeliverySetting(Long id) {
        try {
            Merchant merchant = checkMerchantAccessAuthorization();
            if (merchant != null) {
                JsonNode json = request().body().asJson();
                DeliverySettingRequest request = objectMapper.readValue(json.toString(), DeliverySettingRequest.class);
                if (request.getIsShipper() != null && !request.getIsShipper()) {
                    String validate = validate(request);
                    if (validate != null) {
                        response.setBaseResponse(0, 0, 0, validate, null);
                        return badRequest(Json.toJson(response));
                    }
                }
                DeliverySetting deliverySetting = DeliverySettingRepository.findById(id);
                if (deliverySetting == null) {
                    response.setBaseResponse(0, 0, 0, "Pengaturan pengiriman tidak ditemukan.", null);
                    return badRequest(Json.toJson(response));
                }
                Store store = StoreRepository.findByStoreId(request.getStoreId());
                if (store == null) {
                    response.setBaseResponse(0, 0, 0, "Toko tidak ditemukan.", null);
                    return badRequest(Json.toJson(response));
                }
                Transaction trx = Ebean.beginTransaction();
                try {
                    deliverySetting.updateDeliverySetting(request, deliverySetting, merchant, store);
                    deliverySetting.update();
                    trx.commit();

                    response.setBaseResponse(1, 0, 0, "Berhasil menyimpan data pengaturan pengiriman", detailResponse(deliverySetting));
                    return ok(Json.toJson(response));
                } catch (Exception e) {
                    trx.rollback();
                    logger.error("Error when updating :", e);
                    response.setBaseResponse(0, 0, 0, e.getMessage(), null);
                    return badRequest(Json.toJson(response));
                } finally {
                    trx.end();
                }
            } else {
                response.setBaseResponse(0, 0, 0, unauthorized, null);
                return unauthorized(Json.toJson(response));
            }
        } catch (Exception e) {
            logger.error("Error when create : " + e.getMessage());
            response.setBaseResponse(0, 0, 0, e.getMessage(), null);
            return unauthorized(Json.toJson(response));
        }
    }

    public static Result deleteDeliverySetting(Long id) {
        try {
            Merchant merchant = checkMerchantAccessAuthorization();
            if (merchant != null) {
                DeliverySetting deliverySetting = DeliverySettingRepository.findById(id);
                if (deliverySetting == null) {
                    response.setBaseResponse(0, 0, 0, "Pengaturan pengiriman tidak ditemukan.", null);
                    return badRequest(Json.toJson(response));
                }
                Transaction trx = Ebean.beginTransaction();
                try {
                    deliverySetting.isDeleted = true;
                    deliverySetting.update();
                    trx.commit();

                    response.setBaseResponse(0, 0, 0, "Berhasil menghapus data pengaturan pengiriman", null);
                    return ok(Json.toJson(response));
                } catch (Exception e) {
                    trx.rollback();
                    logger.error("Error while deleting :", e);
                    response.setBaseResponse(0, 0, 0, e.getMessage(), null);
                    return badRequest(Json.toJson(response));
                } finally {
                    trx.end();
                }
            } else {
                response.setBaseResponse(0, 0, 0, unauthorized, null);
                return unauthorized(Json.toJson(response));
            }
        } catch (Exception e) {
            logger.error("Error : " + e.getMessage());
            response.setBaseResponse(0, 0, 0, e.getMessage(), null);
            return unauthorized(Json.toJson(response));
        }
    }


    // ==========
    public static DeliverySettingResponse detailResponse(DeliverySetting data) {
        return new DeliverySettingResponse(data);
    }

    public static List<DeliverySettingResponse> listResponse(List<DeliverySetting> data) {
        List<DeliverySettingResponse> responses = new ArrayList<>();
        for (DeliverySetting deliverySetting : data) {
            DeliverySettingResponse response = new DeliverySettingResponse(deliverySetting);
            responses.add(response);
        }

        return responses;
    }

    public static String validate(DeliverySettingRequest request) {
        String message = null;
        if (request.getIsActiveBasePrice()) {
            if (request.getDeliveryMethod() == null || request.getDeliveryMethod().isEmpty())
                message = "Metode perhitungan tidak boleh atau kurang dari nol";
            if (request.getNormalPrice().compareTo(BigDecimal.ZERO) == 0 || request.getNormalPrice().compareTo(BigDecimal.ZERO) < 0)
                message = "Tarif normal tidak boleh atau kurang dari nol";
            if (request.getNormalPriceMaxRange() == 0 || request.getNormalPriceMaxRange() < 0)
                message = "Jangkauan maksimal tarif normal tidak boleh atau kurang dari nol";
            if (request.getBasicPrice().compareTo(BigDecimal.ZERO) == 0 || request.getBasicPrice().compareTo(BigDecimal.ZERO) < 0)
                message = "Tarif dasar tidak boleh atau kurang dari nol";
            if (request.getBasicPriceMaxRange() == 0 || request.getBasicPriceMaxRange() < 0)
                message = "Jangkauan maksimal tarif dasar tidak boleh atau kurang dari nol";
        } else {
            if (request.getDeliveryMethod() == null || request.getDeliveryMethod().isEmpty())
                message = "Metode perhitungan tidak boleh atau kurang dari nol";
            if (request.getNormalPrice().compareTo(BigDecimal.ZERO) == 0 || request.getNormalPrice().compareTo(BigDecimal.ZERO) < 0)
                message = "Tarif normal tidak boleh atau kurang dari nol";
            if (request.getNormalPriceMaxRange() == 0 || request.getNormalPriceMaxRange() < 0)
                message = "Jangkauan maksimal tarif normal tidak boleh atau kurang dari nol";
        }

        return message;
    }

}