package controllers.merchants;

import com.avaje.ebean.Ebean;
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
import dtos.brand.BrandMerchantResponse;
import dtos.category.CategoryMerchantResponse;
import dtos.merchant.qrgroup.request.QrGroupRequest;
import dtos.merchant.qrgroup.request.QrGroupStoreRequest;
import dtos.merchant.qrgroup.response.QrGroupResponse;
import dtos.merchant.qrgroup.response.QrGroupResponseList;
import dtos.merchant.qrgroup.response.QrGroupStoreResponse;
import dtos.product.ProductDetailResponse;
import dtos.product.ProductSpecificStoreResponse;
import models.BrandMerchant;
import models.CategoryMerchant;
import models.Merchant;
import models.ProductStore;
import models.QrGroup;
import models.QrGroupStore;
import models.ShipperArea;
import models.ShipperCity;
import models.ShipperProvince;
import models.ShipperSuburb;
import models.Store;
import models.SubCategoryMerchant;
import models.SubsCategoryMerchant;
import models.merchant.ProductMerchantDetail;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import repository.CategoryMerchantRepository;
import repository.ProductMerchantDetailRepository;
import repository.QrGroupRepository;
import repository.SubCategoryMerchantRepository;
import repository.SubsCategoryMerchantRepository;
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
                List<QrGroup> listQrGroup = QrGroupRepository.findAllQrGroup(ownMerchant.id, filter, offset ,limit);
                int totalQrGroup = QrGroupRepository.findAllQrGroup(ownMerchant.id, filter, 0 ,0).size();

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
                QrGroup getQrGroup = QrGroupRepository.findByIdAndMerchant(id, ownMerchant.id);
                if (getQrGroup == null) {
                    response.setBaseResponse(0, 0, 0, "Qr group tidak ditemukan.", null);
                    return badRequest(Json.toJson(response));
                }

                List<QrGroupStore> listQrGroupStore = QrGroupRepository.findQrGroupStoreByGroupId(getQrGroup.id, filter, offset, limit);
                int storeCount = QrGroupRepository.findQrGroupStoreByGroupId(getQrGroup.id, filter, 0, 0).size();

                response.setBaseResponse(storeCount, offset, limit, "Berhasil menampilkan data QR group.",
                    toQrGroupResponse(getQrGroup, listQrGroupStore));
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
                    QrGroup getQrGroup = QrGroupRepository.findByIdAndMerchant(id, ownMerchant.id);
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
                    } else {
                        getQrGroup.setAddress(null);
                        getQrGroup.setPhone(null);
                        getQrGroup.setShipperProvince(null);
                        getQrGroup.setShipperCity(null);
                        getQrGroup.setShipperSuburb(null);
                        getQrGroup.setShipperArea(null);
                        getQrGroup.setUrlGmap(null);
                        getQrGroup.setLongitude(null);
                        getQrGroup.setLatitude(null);
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
                QrGroup getQrGroup = QrGroupRepository.findByIdAndMerchant(id, ownMerchant.id);
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
                QrGroup getQrGroup = QrGroupRepository.findByIdAndMerchant(qrGroupStoreRequest.getQrGroupId(), ownMerchant.id);
                if (getQrGroup == null) {
                    response.setBaseResponse(0, 0, 0, "QR group tidak ditemukan.", null);
                    return badRequest(Json.toJson(response));
                }
                Transaction trx = Ebean.beginTransaction();
                try {
                    for (Long requestId : qrGroupStoreRequest.getStoreId()) {
                        Store getStore = null;
                        if (ownMerchant.globalStoreQrGroup) {
                            getStore = Store.find.where().eq("t0.id", requestId).eq("t0.is_deleted", false).findUnique();
                        } else {
                            getStore = Store.find.where().eq("t0.id", requestId).eq("t0.merchant_id", ownMerchant.id).eq("t0.is_deleted", false).findUnique();
                        }
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

    public static Result getQrGroupByGroupCode(String groupCode, String filter, String sort, int offset, int limit) {
        int authority = checkAccessAuthorization("all");
        if (authority == 200 || authority == 203) {
            try {
                QrGroup getQrGroup = QrGroupRepository.findByCode(groupCode);
                if (getQrGroup == null) {
                    response.setBaseResponse(0, 0, 0, "QR group tidak ditemukan.", null);
                    return badRequest(Json.toJson(response));
                }

                List<QrGroupStore> listQrGroupStore = QrGroupRepository.findQrGroupStoreByGroupId(getQrGroup.id, filter, offset, limit);
                int storeCount = QrGroupRepository.findQrGroupStoreByGroupId(getQrGroup.id, filter, 0, 0).size();

                response.setBaseResponse(storeCount, offset, limit, "Berhasil menampilkan data QR group.",
                    toQrGroupResponse(getQrGroup, listQrGroupStore));
                return ok(Json.toJson(response));
            } catch (Exception e) {
                Logger.info("Error: " + e.getMessage());
            }
        } else if (authority == 403) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result getListStoreFromGroup(String groupCode, String filter, String sortName, String sortStore, int offset, int limit) {
        int authority = checkAccessAuthorization("all");
        if (authority == 200 || authority == 203) {
            try {
                QrGroup getQrGroup = QrGroupRepository.findByCode(groupCode);
                if (getQrGroup == null) {
                    response.setBaseResponse(0, 0, 0, "QR group tidak ditemukan.", null);
                    return badRequest(Json.toJson(response));
                }

                List<QrGroupStore> listQrGroupStore = QrGroupRepository.findListStoreFromGroup(getQrGroup.id, filter, sortName, sortStore,  offset, limit);
                int storeCount = QrGroupRepository.findListStoreFromGroup(getQrGroup.id, filter, sortName, sortStore, 0, 0).size();

                response.setBaseResponse(storeCount, offset, limit, "Berhasil menampilkan data QR group.",
                    toListStoreResponse(listQrGroupStore));
                return ok(Json.toJson(response));
            } catch (Exception e) {
                Logger.info("Error: " + e.getMessage());
            }
        } else if (authority == 403) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result getListProductFromGroup(String groupCode, String filter, String sort, int offset, int limit) {
        int authority = checkAccessAuthorization("all");
        if (authority == 200 || authority == 203) {
            try {
                QrGroup getQrGroup = QrGroupRepository.findByCode(groupCode);
                if (getQrGroup == null) {
                    response.setBaseResponse(0, 0, 0, "QR group tidak ditemukan.", null);
                    return badRequest(Json.toJson(response));
                }

                List<ProductStore> productStore = QrGroupRepository.findListProductFromGroupCode(groupCode, filter, offset, limit);
                int totalProduct = QrGroupRepository.findListProductFromGroupCode(groupCode, filter, 0, 0).size();

                response.setBaseResponse(totalProduct, offset, limit, "Berhasil menampilkan daftar produk QR group.",
                    toListProductResponse(productStore));
                return ok(Json.toJson(response));
            } catch (Exception e) {
                Logger.info("Error: " + e.getMessage());
            }
        } else if (authority == 403) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result getAllProductFromGroup(String groupCode, Long brandId, Long categoryId, String keyword, int offset, int limit) {
        int authority = checkAccessAuthorization("all");
        if (authority == 200 || authority == 203) {
            try {
                QrGroup getQrGroup = QrGroupRepository.findByCode(groupCode);
                if (getQrGroup == null) {
                    response.setBaseResponse(0, 0, 0, "QR group tidak ditemukan.", null);
                    return badRequest(Json.toJson(response));
                }

                List<ProductStore> productStore = QrGroupRepository.findAllProductFromGroup(groupCode, brandId, categoryId, keyword, offset, limit);
                int totalProduct = QrGroupRepository.findAllProductFromGroup(groupCode, brandId, categoryId, keyword, 0, 0).size();

                response.setBaseResponse(totalProduct, offset, limit, "Berhasil menampilkan daftar produk QR group.",
                    toListProductResponse(productStore));
                return ok(Json.toJson(response));
            } catch (Exception e) {
                Logger.info("Error: " + e.getMessage());
            }
        } else if (authority == 403) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result getListCategoryFromGroup(String groupCode) {
        int authority = checkAccessAuthorization("all");
        if (authority == 200 || authority == 203) {
            try {
                QrGroup getQrGroup = QrGroupRepository.findByCode(groupCode);
                if (getQrGroup == null) {
                    response.setBaseResponse(0, 0, 0, "QR group tidak ditemukan.", null);
                    return badRequest(Json.toJson(response));
                }

                List<CategoryMerchant> data = QrGroupRepository.findListCategoryFromGroup(groupCode, "", 0, 0);
                int totalData = QrGroupRepository.findListCategoryFromGroup(groupCode, "", 0, 0).size();

                List<CategoryMerchantResponse> responses = new ArrayList<>();
                for (CategoryMerchant category : data) {
                    int totalProductCategory = QrGroupRepository.getTotalProductCategory(groupCode, category.id);
                    CategoryMerchantResponse categoryRes = new CategoryMerchantResponse(category);
                    categoryRes.setTotalProduct(totalProductCategory);

                    List<SubCategoryMerchant> subCategoryMerchants = SubCategoryMerchantRepository.findAllByMerchantAndCategory(category.getMerchant().id, category.id);

                    List<CategoryMerchantResponse.SubCategoryMerchantResponse> subResponses = new ArrayList<>();
                    for(SubCategoryMerchant subCategory : subCategoryMerchants) {
                        int totalProductSubCategory = QrGroupRepository.getTotalProductSubCategory(groupCode, subCategory.id);
                        CategoryMerchantResponse.SubCategoryMerchantResponse subRes = new CategoryMerchantResponse.SubCategoryMerchantResponse(subCategory);
                        subRes.setTotalProduct(totalProductSubCategory);

                        List<SubsCategoryMerchant> subsCategoryMerchants = SubsCategoryMerchantRepository.findAllByMerchantAndSubCategory(subCategory.getMerchant().id, subCategory.id);

                        List<CategoryMerchantResponse.SubCategoryMerchantResponse.SubsCategoryMerchantResponse> subsResponses = new ArrayList<>();
                        for(SubsCategoryMerchant subsCategory : subsCategoryMerchants) {
                            int totalProductSubsCategory = QrGroupRepository.getTotalProductSubsCategory(groupCode, subsCategory.id);
                            CategoryMerchantResponse.SubCategoryMerchantResponse.SubsCategoryMerchantResponse subsRes = new CategoryMerchantResponse.SubCategoryMerchantResponse.SubsCategoryMerchantResponse(subsCategory);
                            subsRes.setTotalProduct(totalProductSubsCategory);

                            subsResponses.add(subsRes);
                            subRes.setSubsCategory(subsRes != null ? subsResponses : null);
                        }

                        subResponses.add(subRes);
                        categoryRes.setSubCategory(subRes != null ? subResponses : null);
                    }

                    responses.add(categoryRes);
                }

                response.setBaseResponse(totalData, offset, limit, "Berhasil menampilkan category.", responses);
                return ok(Json.toJson(response));
            } catch (Exception e) {
                Logger.info("Error: " + e.getMessage());
            }
        } else if (authority == 403) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result getListProductByCategory(String groupCode, Long categoryId, Long subCategoryId, Long subsCategoryId, String keyword, String filter, String sort, int offset, int limit) {
        int authority = checkAccessAuthorization("all");
        if (authority == 200 || authority == 203) {
            try {
                QrGroup getQrGroup = QrGroupRepository.findByCode(groupCode);
                if (getQrGroup == null) {
                    response.setBaseResponse(0, 0, 0, "QR group tidak ditemukan.", null);
                    return badRequest(Json.toJson(response));
                }

                List<ProductStore> productStore = QrGroupRepository.findListProductByCategory(groupCode, categoryId, subCategoryId, subsCategoryId, keyword, filter, sort, offset, limit);
                int totalProduct = QrGroupRepository.findListProductByCategory(groupCode, categoryId, subCategoryId, subsCategoryId, keyword, filter, sort, 0, 0).size();

                response.setBaseResponse(totalProduct, offset, limit, "Berhasil menampilkan daftar produk QR group.",
                    toListProductResponse(productStore));
                return ok(Json.toJson(response));
            } catch (Exception e) {
                Logger.info("Error: " + e.getMessage());
            }
        } else if (authority == 403) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result getListBrandFromGroup(String groupCode, String filter, int offset, int limit) {
        int authority = checkAccessAuthorization("all");
        if (authority == 200 || authority == 203) {
            try {
                QrGroup getQrGroup = QrGroupRepository.findByCode(groupCode);
                if (getQrGroup == null) {
                    response.setBaseResponse(0, 0, 0, "QR group tidak ditemukan.", null);
                    return badRequest(Json.toJson(response));
                }

                List<BrandMerchant> brandMerchant = QrGroupRepository.findListBrandFromGroup(groupCode, filter, offset, limit);
                int totalBrand = QrGroupRepository.findListBrandFromGroup(groupCode, filter, 0, 0).size();

                List<BrandMerchantResponse> responses = new ArrayList<>();
                for (BrandMerchant data : brandMerchant) {
                    Integer totalProductBrand = QrGroupRepository.getTotalProductBrandFromGroup(groupCode, data.id);
                    BrandMerchantResponse response = new BrandMerchantResponse();
                    response.setId(data.id);
                    response.setBrandName(data.getBrandName());
                    response.setBrandType(data.getBrandType());
                    response.setBrandDescription(data.getBrandDescription());
                    response.setImageWeb(data.getImageWeb());
                    response.setImageMobile(data.getImageMobile());
                    response.setIconWeb(data.getIconWeb());
                    response.setIconMobile(data.getIconMobile());
                    response.setIsDeleted(data.isDeleted);
                    response.setIsActive(data.isActive());
                    response.setMerchantId(data.getMerchant().id);
                    response.setTotalProduct(totalProductBrand);
                    responses.add(response);
                }

                response.setBaseResponse(totalBrand, offset, limit, "Berhasil menampilkan daftar brand QR group.",
                    responses);
                return ok(Json.toJson(response));
            } catch (Exception e) {
                Logger.info("Error: " + e.getMessage());
            }
        } else if (authority == 403) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
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

        if (qrGroupRequest.getProvinceId() != null) {
            ShipperProvince province = ShipperProvince.findById(qrGroupRequest.getProvinceId());
            if (province == null) {
                return "Provinsi tidak ditemukan";
            }
        }
        if (qrGroupRequest.getCityId() != null) {
            ShipperCity shipperCity = ShipperCity.findById(qrGroupRequest.getCityId());
            if (shipperCity == null) {
                return "Kota tidak ditemukan";
            }
        }
        if (qrGroupRequest.getSuburbId() != null) {
            ShipperSuburb shipperSuburb = ShipperSuburb.findById(qrGroupRequest.getSuburbId());
            if (shipperSuburb == null) {
                return "Kecamatan tidak ditemukan";
            }
        }
        if (qrGroupRequest.getAreaId() != null) {
            ShipperArea shipperArea = ShipperArea.findById(qrGroupRequest.getAreaId());
            if (shipperArea == null) {
                return "Kelurahan tidak ditemukan";
            }
        }
        if (!qrGroupRequest.getPhone().matches(CommonFunction.phoneRegex)){
            return "Format nomor telepon tidak valid.";
        }

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

    private static QrGroupResponse toQrGroupResponse(QrGroup qrGroup, List<QrGroupStore> listQrGroupStore) {
        QrGroupResponse qrGroupResponses = new QrGroupResponse();
        if (qrGroup != null) {
            qrGroupResponses.setId(qrGroup.id);
            qrGroupResponses.setMerchantId(qrGroup.getMerchant().id);
            qrGroupResponses.setGroupName(qrGroup.getGroupName());
            qrGroupResponses.setGroupLogo(qrGroup.getGroupLogo());
            qrGroupResponses.setGroupCode(qrGroup.getGroupCode());
            qrGroupResponses.setGroupQrCode(qrGroup.getGroupQrCode());
            qrGroupResponses.setAddressType(qrGroup.getAddressType());
            qrGroupResponses.setAddress(qrGroup.getAddress());
            qrGroupResponses.setPhone(qrGroup.getPhone());
            qrGroupResponses.setProvince(qrGroup.shipperProvince == null ? null : ShipperHelper.toProvinceResponse(qrGroup.shipperProvince));
            qrGroupResponses.setCity(qrGroup.shipperCity == null ? null : ShipperHelper.toCityResponse(qrGroup.shipperCity));
            qrGroupResponses.setSuburb(qrGroup.shipperSuburb == null ? null : ShipperHelper.toSuburbResponse(qrGroup.shipperSuburb));
            qrGroupResponses.setArea(qrGroup.shipperArea == null ? null : ShipperHelper.toAreaResponse(qrGroup.shipperArea));
            qrGroupResponses.setUrlGmap(qrGroup.getUrlGmap());
            qrGroupResponses.setLongitude(qrGroup.getLongitude());
            qrGroupResponses.setLatitude(qrGroup.getLatitude());
        }

        List<QrGroupStoreResponse> storeResponses = new ArrayList<>();
        for (QrGroupStore groupStore : listQrGroupStore) {
            Store getStore = Store.find.byId(groupStore.getStore().id);
            if (getStore != null) {
                QrGroupStore store = new QrGroupStore(getStore, qrGroup);
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

        return qrGroupResponses;
    }

    private static List<QrGroupStoreResponse> toListStoreResponse(List<QrGroupStore> listQrGroupStore) {
        List<QrGroupStoreResponse> storeResponses = new ArrayList<>();
        for (QrGroupStore groupStore : listQrGroupStore) {
            Store getStore = Store.find.byId(groupStore.getStore().id);
            if (getStore != null) {
                QrGroupStore store = new QrGroupStore(getStore, null);
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

        return storeResponses;
    }

    private static List<ProductSpecificStoreResponse> toListProductResponse(List<ProductStore> listProductStore) {
        List<ProductSpecificStoreResponse> listProductResponses = new ArrayList<>();
        for(ProductStore productStore : listProductStore) {
            ProductMerchantDetail productMerchantDetail = ProductMerchantDetailRepository.findByProduct(productStore.getProductMerchant());

            ProductSpecificStoreResponse response = new ProductSpecificStoreResponse(productStore.getProductMerchant());

            ProductDetailResponse productDetailResponse = new ProductDetailResponse(productMerchantDetail, productStore);
            response.setProductDetail(productDetailResponse);

            ProductSpecificStoreResponse.Brand brand = new ProductSpecificStoreResponse.Brand(productMerchantDetail.getProductMerchant().getBrandMerchant());
            response.setBrand(brand);

            ProductSpecificStoreResponse.Category category = new ProductSpecificStoreResponse.Category(productMerchantDetail.getProductMerchant().getCategoryMerchant());
            response.setCategory(category);

            ProductSpecificStoreResponse.SubCategory subCategory = new ProductSpecificStoreResponse.SubCategory(productMerchantDetail.getProductMerchant().getSubCategoryMerchant());
            response.setSubCategory(subCategory);

            ProductSpecificStoreResponse.SubsCategory subsCategory = new ProductSpecificStoreResponse.SubsCategory(productMerchantDetail.getProductMerchant().getSubsCategoryMerchant());
            response.setSubsCategory(subsCategory);

            ProductSpecificStoreResponse.ProductStore pStore = new ProductSpecificStoreResponse.ProductStore(productStore);
            response.setProductStore(pStore);

            listProductResponses.add(response);
        }

        return listProductResponses;
    }

}
