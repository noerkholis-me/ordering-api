package controllers.merchants;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Query;
import com.avaje.ebean.Transaction;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hokeba.api.BaseResponse;
import com.hokeba.util.CommonFunction;
import com.hokeba.util.Constant;
import com.hokeba.util.Helper;
import com.wordnik.swagger.annotations.Api;
import controllers.BaseController;
import dtos.merchant.qrgroup.request.QrGroupRequest;
import dtos.merchant.qrgroup.response.QrGroupResponse;
import dtos.merchant.qrgroup.request.QrGroupStoreRequest;
import dtos.merchant.qrgroup.response.QrGroupStoreResponse;
import dtos.merchant.qrgroup.response.QrGroupResponseList;
import models.Merchant;
import models.QrGroup;
import models.QrGroupStore;
import models.ShipperArea;
import models.ShipperCity;
import models.ShipperProvince;
import models.ShipperSuburb;
import models.Store;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import utils.ShipperHelper;

import java.util.ArrayList;
import java.util.List;

@Api(value = "/merchant/qr-group", description = "Qr Group Management")
public class QrGroupController extends BaseController {

    private final static Logger.ALogger logger = Logger.of(StoreController.class);
    private static BaseResponse response = new BaseResponse();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Result getAllQrGroup(String filter, String sort, int offset, int limit) {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            try {
                Query<QrGroup> queryQrGroup = QrGroup.find.where().eq("t0.merchant_id", ownMerchant.id).eq("t0.is_deleted", false).order("t0.id desc");
                ExpressionList<QrGroup> exp = queryQrGroup.where();
                exp = exp.disjunction();
                exp = exp.or(Expr.ilike("t0.group_name", "%" + filter + "%"), Expr.ilike("t0.group_code", "%" + filter + "%"));
                queryQrGroup = exp.query();
                List<QrGroup> listQrGroup = queryQrGroup.findPagingList(limit).getPage(offset).getList();
                Integer totalQrGroup = queryQrGroup.findList().size();

                List<QrGroupResponseList> listData = new ArrayList<>();
                for (QrGroup qrGroup : listQrGroup) {
                    QrGroupResponseList responses = new QrGroupResponseList();
                    responses.setId(qrGroup.id);
                    responses.setGroupName(qrGroup.groupName);
                    responses.setGroupCode(qrGroup.groupCode);

                    int storeCount = 0;
                    List<QrGroupStore> qrGroupStore = QrGroupStore.find.where().eq("t0.qr_group_id", qrGroup.id).eq("t0.is_deleted", false).findList();
                    if (qrGroupStore != null) {
                        storeCount = qrGroupStore.size();
                    }

                    responses.setStoreCount(storeCount);
                    listData.add(responses);
                }

                response.setBaseResponse(totalQrGroup, offset, limit, "Berhasil menampilkan data list QR group.", listData);
                return ok(Json.toJson(response));
            } catch (Exception e) {
                Logger.info("Error: " + e.getMessage());
            }
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result getQrGroupById(Long id, String filter, String sort, int offset, int limit) {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            try {
                QrGroup getQrGroup = QrGroup.find.where().eq("t0.id", id).eq("t0.merchant_id", ownMerchant.id).eq("t0.is_deleted", false).findUnique();
                if (getQrGroup == null) {
                    response.setBaseResponse(0, 0, 0, "Qr group tidak ditemukan.", null);
                    return badRequest(Json.toJson(response));
                }

                QrGroupResponse qrGroupResponses = new QrGroupResponse();
                if (getQrGroup != null) {
                    qrGroupResponses.setId(getQrGroup.id);
                    qrGroupResponses.setMerchantId(getQrGroup.getMerchant().id);
                    qrGroupResponses.setGroupName(getQrGroup.getGroupName());
                    qrGroupResponses.setGroupLogo(getQrGroup.getGroupLogo());
                    qrGroupResponses.setGroupCode(getQrGroup.getGroupCode());
                    qrGroupResponses.setGroupQrCode(getQrGroup.getGroupQrCode());
                    qrGroupResponses.setAddressType(getQrGroup.getAddressType());
                    qrGroupResponses.setAddress(getQrGroup.getAddress());
                    qrGroupResponses.setPhone(getQrGroup.getPhone());
                    qrGroupResponses.setProvince(getQrGroup.shipperProvince == null ? null : ShipperHelper.toProvinceResponse(getQrGroup.shipperProvince));
                    qrGroupResponses.setCity(getQrGroup.shipperCity == null ? null : ShipperHelper.toCityResponse(getQrGroup.shipperCity));
                    qrGroupResponses.setSuburb(getQrGroup.shipperSuburb == null ? null : ShipperHelper.toSuburbResponse(getQrGroup.shipperSuburb));
                    qrGroupResponses.setArea(getQrGroup.shipperArea == null ? null : ShipperHelper.toAreaResponse(getQrGroup.shipperArea));
                    qrGroupResponses.setUrlGmap(getQrGroup.getUrlGmap());
                    qrGroupResponses.setLongitude(getQrGroup.getLongitude());
                    qrGroupResponses.setLatitude(getQrGroup.getLatitude());
                }

                Query<QrGroupStore> queryQrGroupStore = QrGroupStore.find.where().eq("t0.qr_group_id", id).eq("t0.is_deleted", false).order("t0.id desc");
                List<QrGroupStore> listQrGroupStore = queryQrGroupStore.findPagingList(limit).getPage(offset).getList();
                List<QrGroupStoreResponse> storeResponses = new ArrayList<>();
                Integer storeCount = queryQrGroupStore.findList().size();
                for (QrGroupStore groupStore : listQrGroupStore) {
                    Store getStore = Store.find.byId(groupStore.getStore().id);
                    if (getStore != null) {
                        QrGroupStore store = new QrGroupStore(getStore, getQrGroup);
                        QrGroupStoreResponse storeRes = new QrGroupStoreResponse();
                        storeRes.setId(groupStore.id);
                        storeRes.setStoreId(store.getStoreId());
                        storeRes.setStoreName(store.getStoreName());
                        storeRes.setAddress(store.getStoreAddress());
                        storeRes.setPhone(store.getStorePhone());
                        storeRes.setProvince(store.getShipperProvince());
                        storeRes.setCity(store.getShipperCity());
                        storeRes.setSuburb(store.getShipperSuburb());
                        storeRes.setArea(store.getShipperArea());
                        storeRes.setStoreQrCode(store.getStoreQrCode());
                        storeRes.setMerchantId(store.getMerchantId());
                        storeRes.setStoreLogo(store.getStoreLogo());
                        storeRes.setMerchantType(store.getMerchantType());
                        storeRes.setStoreQueueUrl(Helper.MOBILEQR_URL + store.getStoreCode() + "/queue");
                        storeResponses.add(storeRes);
                    }
                }
                qrGroupResponses.setStore(storeResponses.size() == 0 ? null : storeResponses);

                response.setBaseResponse(storeCount, offset, limit, "Berhasil menampilkan data QR group.", qrGroupResponses);
                return ok(Json.toJson(response));
            } catch (Exception e) {
                Logger.info("Error: " + e.getMessage());
            }
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result createQrGroup() {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            JsonNode json = request().body().asJson();
            try {
                QrGroupRequest request = objectMapper.readValue(json.toString(), QrGroupRequest.class);

                // check validation if address type is true / one address
                if (request.getAddressType()) {
                    String validation = validateRequest(request);
                    if (validation != null) {
                        response.setBaseResponse(0, 0, 0, validation, null);
                        return badRequest(Json.toJson(response));
                    }
                }
                Transaction trx = Ebean.beginTransaction();
                try {
                    QrGroup qrGroup = new QrGroup();
                    qrGroup.setMerchant(ownMerchant);
                    qrGroup.setGroupName(request.getGroupName());
                    qrGroup.setGroupLogo(request.getGroupLogo());
                    qrGroup.setGroupCode(CommonFunction.generateRandomString(8));
                    qrGroup.setGroupQrCode(Constant.getInstance().getFrontEndUrl().concat(qrGroup.groupCode));
                    qrGroup.setAddressType(request.getAddressType());
                    if (request.getAddressType()) {
                        qrGroup.setAddress(request.getAddress());
                        qrGroup.setPhone(request.getPhone());
                        qrGroup.setShipperProvince(ShipperProvince.findById(request.getProvinceId()));
                        qrGroup.setShipperCity(ShipperCity.findById(request.getCityId()));
                        qrGroup.setShipperSuburb(ShipperSuburb.findById(request.getSuburbId()));
                        qrGroup.setShipperArea(ShipperArea.findById(request.getAreaId()));
                        qrGroup.setUrlGmap(request.getUrlGmap());
                        String[] finalLotLang = getLongitudeLatitude(qrGroup.urlGmap);
                        qrGroup.setLongitude(Double.parseDouble(finalLotLang[0]));
                        qrGroup.setLatitude(Double.parseDouble(finalLotLang[1]));
                    }
                    qrGroup.save();

                    trx.commit();

                    response.setBaseResponse(1, 0, 1, "QR grup berhasil dibuat.",
                        toResponse(qrGroup));
                    return ok(Json.toJson(response));
                } catch (Exception e) {
                    logger.error("Error saat membuat QR grup", e);
                    e.printStackTrace();
                    trx.rollback();
                } finally {
                    trx.end();
                }
            } catch (Exception e) {
                Logger.info("Error: " + e.getMessage());
                response.setBaseResponse(0, 0, 0, error, null);
                return internalServerError(Json.toJson(response));
            }
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result editQrGroup(Long id) {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            JsonNode json = request().body().asJson();
            try {
                QrGroupRequest request = objectMapper.readValue(json.toString(), QrGroupRequest.class);
                // check validation if address type is true / one address
                if (request.getAddressType()) {
                    String validation = validateRequest(request);
                    if (validation != null) {
                        response.setBaseResponse(0, 0, 0, validation, null);
                        return badRequest(Json.toJson(response));
                    }
                }
                Transaction trx = Ebean.beginTransaction();
                try {
                    QrGroup getQrGroup = QrGroup.find.where().eq("t0.id", id).eq("t0.merchant_id", ownMerchant.id).eq("t0.is_deleted", false).findUnique();
                    if (getQrGroup == null) {
                        response.setBaseResponse(0, 0, 0, "Qr group tidak ditemukan.", null);
                        return badRequest(Json.toJson(response));
                    }

                    getQrGroup.setMerchant(ownMerchant);
                    getQrGroup.setGroupName(request.getGroupName());
                    getQrGroup.setGroupLogo(request.getGroupLogo());
                    getQrGroup.setGroupQrCode(Constant.getInstance().getFrontEndUrl().concat(getQrGroup.groupCode));
                    getQrGroup.setAddressType(request.getAddressType());
                    if (request.getAddressType()) {
                        getQrGroup.setAddress(request.getAddress());
                        getQrGroup.setPhone(request.getPhone());
                        getQrGroup.setShipperProvince(ShipperProvince.findById(request.getProvinceId()));
                        getQrGroup.setShipperCity(ShipperCity.findById(request.getCityId()));
                        getQrGroup.setShipperSuburb(ShipperSuburb.findById(request.getSuburbId()));
                        getQrGroup.setShipperArea(ShipperArea.findById(request.getAreaId()));
                        getQrGroup.setUrlGmap(request.getUrlGmap());
                        String[] finalLotLang = getLongitudeLatitude(getQrGroup.urlGmap);
                        getQrGroup.setLongitude(Double.parseDouble(finalLotLang[0]));
                        getQrGroup.setLatitude(Double.parseDouble(finalLotLang[1]));
                    }
                    getQrGroup.update();

                    trx.commit();

                    response.setBaseResponse(1, 0, 1, "Berhasil mengubah QR grup.",
                        getQrGroup.id);
                    return ok(Json.toJson(response));
                } catch (Exception e) {
                    logger.error("Error saat mengubah QR grup", e);
                    e.printStackTrace();
                    trx.rollback();
                } finally {
                    trx.end();
                }
            } catch (Exception e) {
                Logger.info("Error: " + e.getMessage());
                response.setBaseResponse(0, 0, 0, error, null);
                return internalServerError(Json.toJson(response));
            }
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result deleteQrGroup(Long id) {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            try {
                QrGroup getQrGroup = QrGroup.find.where().eq("t0.id", id).eq("t0.merchant_id", ownMerchant.id).findUnique();
                if (getQrGroup == null) {
                    response.setBaseResponse(0, 0, 0, "Qr group tidak ditemukan.", null);
                    return badRequest(Json.toJson(response));
                }
                Transaction trx = Ebean.beginTransaction();
                try {
                    List<QrGroupStore> getQrGroupStore = QrGroupStore.find.where().eq("t0.qr_group_id", id).findList();
                    for (QrGroupStore qrGroupStore : getQrGroupStore) {
                        qrGroupStore.isDeleted = true;
                        qrGroupStore.update();
                    }
                    getQrGroup.isDeleted = true;
                    getQrGroup.update();

                    trx.commit();

                    response.setBaseResponse(1, 0, 1, "Berhasil menghapus QR grup", null);
                    return ok(Json.toJson(response));
                } catch (Exception e) {
                    logger.error("Error saat menghapus QR grup", e);
                    e.printStackTrace();
                    trx.rollback();
                } finally {
                    trx.end();
                }
            } catch (Exception e) {
                Logger.info("Error: " + e.getMessage());
                response.setBaseResponse(0, 0, 0, error, null);
                return internalServerError(Json.toJson(response));
            }
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result addStoreToQrGroup() {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            JsonNode json = request().body().asJson();
            try {
                QrGroupStoreRequest qrGroupStoreRequest = objectMapper.readValue(json.toString(), QrGroupStoreRequest.class);
                if (qrGroupStoreRequest.getQrGroupId() == null) {
                    response.setBaseResponse(0, 0, 0, "Id group tidak diboleh kosong.", null);
                    return badRequest(Json.toJson(response));
                }
                QrGroup getQrGroup = QrGroup.find.where().eq("t0.id", qrGroupStoreRequest.getQrGroupId()).eq("t0.is_deleted", false).findUnique();
                if (getQrGroup == null) {
                    response.setBaseResponse(0, 0, 0, "QR group tidak ditemukan.", null);
                    return badRequest(Json.toJson(response));
                }
                Transaction trx = Ebean.beginTransaction();
                try {
                    for (Long requestId : qrGroupStoreRequest.getStoreId()) {
                        Store getStore = Store.find.where().eq("t0.id", requestId).eq("t0.merchant_id", ownMerchant.id).eq("t0.is_deleted", false).findUnique();
                        if (getStore == null) {
                            trx.rollback();
                            response.setBaseResponse(0, 0, 0, "Toko merchant tidak ditemukan.", null);
                            return badRequest(Json.toJson(response));
                        }
                        QrGroupStore getQrGroupStore = QrGroupStore.find.where().eq("t0.store_id", requestId).eq("t0.qr_group_id", getQrGroup.id).eq("t0.is_deleted", false).findUnique();
                        if (getQrGroupStore == null) {
                            QrGroupStore newQrGroupStore = new QrGroupStore();
                            Store store = Store.find.byId(requestId);
                            QrGroup qrGroup = QrGroup.find.byId(getQrGroup.id);
                            newQrGroupStore.setStore(store);
                            newQrGroupStore.setQrGroup(qrGroup);
                            newQrGroupStore.save();
                        }

                    }
                    trx.commit();

                    response.setBaseResponse(1, 0, 1, "Berhasil menambahkan toko ke QR grup",
                        getQrGroup.id);
                    return ok(Json.toJson(response));
                } catch (Exception e) {
                    logger.error("Error saat menambahkan toko ke QR grup", e);
                    e.printStackTrace();
                    trx.rollback();
                } finally {
                    trx.end();
                }
            } catch (Exception e) {
                Logger.info("Error: " + e.getMessage());
                response.setBaseResponse(0, 0, 0, error, null);
                return internalServerError(Json.toJson(response));
            }
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result deleteStoreFromQrGroup(Long id) {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            try {
                QrGroupStore getQrGroupStore = QrGroupStore.find.where().eq("t0.id", id).eq("t0.is_deleted", false).findUnique();
                if (getQrGroupStore == null) {
                    response.setBaseResponse(0, 0, 0, "Toko tidak ditemukan.", null);
                    return badRequest(Json.toJson(response));
                }
                Transaction trx = Ebean.beginTransaction();
                try {
                    getQrGroupStore.isDeleted = true;
                    getQrGroupStore.update();

                    trx.commit();

                    response.setBaseResponse(1, 0, 1, "Berhasil menghapus toko dari QR grup", null);
                    return ok(Json.toJson(response));
                } catch (Exception e) {
                    logger.error("Error saat menghapus toko dari QR grup", e);
                    e.printStackTrace();
                    trx.rollback();
                } finally {
                    trx.end();
                }
            } catch (Exception e) {
                Logger.info("Error: " + e.getMessage());
                response.setBaseResponse(0, 0, 0, error, null);
                return internalServerError(Json.toJson(response));
            }
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static String[] getLongitudeLatitude(String paramGmap) {
        String[] tmpLongLat = paramGmap.split("@");
        String[] finalLotLang = tmpLongLat[1].split("/");
        String[] output = finalLotLang[0].split(",");
        return output;
    }

    private static String validateRequest(QrGroupRequest qrGroupRequest) {
        if (qrGroupRequest == null)
            return "Request is null or empty";
        if (qrGroupRequest.getGroupName() == null || qrGroupRequest.getGroupName().trim().isEmpty())
            return "Name is null or empty";
        if (qrGroupRequest.getAddress() == null || qrGroupRequest.getAddress().trim().isEmpty())
            return "Address is null or empty";
        if (qrGroupRequest.getPhone() == null || qrGroupRequest.getPhone().trim().isEmpty())
            return "Phone is null or empty";
        if (qrGroupRequest.getProvinceId() == null)
            return "Province is null or empty";
        if (qrGroupRequest.getCityId() == null)
            return "City is null or empty";
        if (qrGroupRequest.getSuburbId() == null)
            return "Suburb is null or empty";
        if (qrGroupRequest.getAreaId() == null)
            return "Area is null or empty";
        if (qrGroupRequest.getUrlGmap() == null || qrGroupRequest.getUrlGmap().trim().isEmpty())
            return "Google maps url is null or empty";

        return null;
    }

    private static QrGroupResponse toResponse(QrGroup qrGroup) {
        return QrGroupResponse.builder()
            .id(qrGroup.id)
            .merchantId(qrGroup.merchant.id)
            .groupName(qrGroup.groupName)
            .groupLogo(qrGroup.groupLogo)
            .groupCode(qrGroup.groupCode)
            .groupQrCode(qrGroup.groupQrCode)
            .addressType(qrGroup.addressType)
            .address(qrGroup.address)
            .phone(qrGroup.phone)
            .province(qrGroup.shipperProvince == null ? null : ShipperHelper.toProvinceResponse(qrGroup.shipperProvince))
            .city(qrGroup.shipperCity == null ? null : ShipperHelper.toCityResponse(qrGroup.shipperCity))
            .suburb(qrGroup.shipperSuburb == null ? null : ShipperHelper.toSuburbResponse(qrGroup.shipperSuburb))
            .area(qrGroup.shipperArea == null ? null : ShipperHelper.toAreaResponse(qrGroup.shipperArea))
            .urlGmap(qrGroup.urlGmap)
            .longitude(qrGroup.longitude)
            .latitude(qrGroup.latitude)
            .build();
    }

}
