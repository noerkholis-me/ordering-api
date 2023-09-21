package controllers.merchants;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Transaction;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hokeba.api.BaseResponse;
import com.hokeba.util.CommonFunction;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import controllers.BaseController;
import dtos.store.ProductStoreResponseForStore;
import dtos.store.StoreRequest;
import dtos.store.StoreResponse;
import dtos.store.StoreResponsePuP;
import models.Merchant;
import models.ProductStore;
import models.ShipperArea;
import models.ShipperCity;
import models.ShipperProvince;
import models.ShipperSuburb;
import models.Store;
import models.merchant.ProductMerchant;
import models.merchant.ProductMerchantDetail;
import models.merchant.TableMerchant;
import models.pupoint.PickUpPointMerchant;
import models.store.StoreAccessDetail;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import repository.ProductMerchantDetailRepository;
import repository.ProductMerchantRepository;
import repository.ProductStoreRepository;
import repository.StoreAccessRepository;
import repository.StoreRepository;
import repository.TableMerchantRepository;
import repository.pickuppoint.PickUpPointRepository;

import java.util.ArrayList;
import java.util.List;

@Api(value = "/merchants/store", description = "Store Management")
public class StoreController extends BaseController {
    private final static Logger.ALogger logger = Logger.of(StoreController.class);
    private static BaseResponse response = new BaseResponse();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @ApiOperation(value = "Create store.", notes = "Save store.\n" + swaggerInfo
            + "", responseContainer = "Add", httpMethod = "POST")
    public static Result createStore () {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            JsonNode json = request().body().asJson();
            try {
                StoreRequest storeRequest = objectMapper.readValue(json.toString(), StoreRequest.class);
                String validation = validateRequest(storeRequest);
                if (validation != null) {
                    response.setBaseResponse(0, 0, 0, validation, null);
                    return badRequest(Json.toJson(response));
                } else {
                    Transaction trx = Ebean.beginTransaction();
                    try {
                        Store store = new Store(storeRequest, ownMerchant);
                        store.save();

                        trx.commit();

                        response.setBaseResponse(1, 0, 1, "Berhasil membuat toko", toResponse(store));
                        return ok(Json.toJson(response));
                    } catch (Exception e) {
                        logger.error("Error while creating store", e);
                        e.printStackTrace();
                        trx.rollback();
                    } finally {
                        trx.end();
                    }
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

    @ApiOperation(value = "Edit store.", notes = "Save store.\n" + swaggerInfo
            + "", responseContainer = "Edit", httpMethod = "PUT")
    public static Result editStore (Long id) {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            JsonNode json = request().body().asJson();
            try {
                StoreRequest storeRequest = objectMapper.readValue(json.toString(), StoreRequest.class);
                String validation = validateRequest(storeRequest);
                if (validation != null) {
                    response.setBaseResponse(0, 0, 0, validation, null);
                    return badRequest(Json.toJson(response));
                }
                Transaction trx = Ebean.beginTransaction();
                try {
                    Store store = Store.findById(id);
                    if (store == null) {
                        response.setBaseResponse(0, 0, 0, " Store is not found.", null);
                        return badRequest(Json.toJson(response));
                    } else {
                        store.setStore(storeRequest, store);
                        store.update();
                    }
                    trx.commit();

                    response.setBaseResponse(1, 0, 1, success + " Store updated successfully", store);
                    return ok(Json.toJson(response));
                } catch (Exception e) {
                    logger.error("Error while updating store", e);
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

    @ApiOperation(value = "Delete store", notes = "Delete of store.\n" + swaggerInfo
            + "", responseContainer = "object", httpMethod = "DELETE")
    public static Result deleteStore (Long id) {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            try {
                Transaction trx = Ebean.beginTransaction();
                try {
                    Store store = Store.findById(id);
                    if (store == null) {
                        response.setBaseResponse(0, 0, 0, " Store is not found.", null);
                        return badRequest(Json.toJson(response));
                    }

                    List<TableMerchant> totalDataTable = TableMerchantRepository.find.where().eq("store_id", store.id).eq("is_deleted", Boolean.FALSE).findPagingList(0).getPage(0).getList();
                    if (totalDataTable.size() != 0) {
                        response.setBaseResponse(0, 0, 0, "Tidak dapat menghapus Store. " +store.storeName+ " memiliki " + totalDataTable.size() + " Table.", null);
                        return badRequest(Json.toJson(response));
                    }

                    List<PickUpPointMerchant> totalDataPickUpPoint = PickUpPointRepository.find.where().eq("store_id", store.id).eq("is_deleted", Boolean.FALSE).findPagingList(0).getPage(0).getList();
                    if (totalDataPickUpPoint.size() != 0) {
                        response.setBaseResponse(0, 0, 0, "Tidak dapat menghapus Store. " +store.storeName+ " memiliki " + totalDataPickUpPoint.size() + " Pickup Point.", null);
                        return badRequest(Json.toJson(response));
                    }

                    List<ProductStore> totalDataProductStore = ProductStoreRepository.find.where().eq("store_id", store.id).eq("is_deleted", Boolean.FALSE).findPagingList(0).getPage(0).getList();
                    if (totalDataProductStore.size() != 0) {
                        response.setBaseResponse(0, 0, 0, "Tidak dapat menghapus Store. " +store.storeName+ " memiliki " + totalDataProductStore.size() + " Product Store.", null);
                        return badRequest(Json.toJson(response));
                    }

                    List<StoreAccessDetail> totalDataStoreAccess = StoreAccessRepository.findDetail.where().eq("store_id", store.id).eq("is_deleted", Boolean.FALSE).findPagingList(0).getPage(0).getList();
                    if (totalDataStoreAccess.size() != 0) {
                        response.setBaseResponse(0, 0, 0, "Tidak dapat menghapus Store. " +store.storeName+ " memiliki " + totalDataStoreAccess.size() + " Store Access.", null);
                        return badRequest(Json.toJson(response));
                    }

                    store.isActive = Boolean.FALSE;
                    store.isDeleted = Boolean.TRUE;

                    store.update();
                    trx.commit();
                    response.setBaseResponse(1, 0, 0, success + " Deleted data store", null);
                    return ok(Json.toJson(response));
                } catch (Exception e) {
                    logger.error("Error while delete store", e);
                    e.printStackTrace();
                    trx.rollback();
                } finally {
                    trx.end();
                }
            } catch (Exception e) {
                Logger.info("Error: " + e.getMessage());
            }
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    @ApiOperation(value = "Get all list of store.", notes = "Returns all list of store.\n" + swaggerInfo
            + "", responseContainer = "List", httpMethod = "GET")
    public static Result getAllStore (String filter, String sort, int offset, int limit) {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            try {
                int totalData = StoreRepository.findAllStore(filter, sort, 0, 0).size();
                List<Store> data = StoreRepository.findAllStore(filter, sort, offset, limit);

                response.setBaseResponse(totalData, offset, limit, "Berhasil menampilkan data toko", listStoreResponses(data));
                return ok(Json.toJson(response));
            } catch (Exception e) {
                Logger.info("Error: " + e.getMessage());
            }
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    @ApiOperation(value = "Get all store list.", notes = "Returns list of store.\n" + swaggerInfo
            + "", responseContainer = "List", httpMethod = "GET")
    public static Result getAllStoreFromMerchant (String filter, String sort, int offset, int limit) {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            try {
                int totalData = StoreRepository.findAllStoreIsActiveByMerchant(ownMerchant.id, filter, sort, 0, 0).size();
                List<Store> data = StoreRepository.findAllStoreIsActiveByMerchant(ownMerchant.id, filter, sort, offset, limit);

                response.setBaseResponse(totalData, offset, limit, "Berhasil menampilkan data toko", listStoreResponses(data));
                return ok(Json.toJson(response));
            } catch (Exception e) {
                e.printStackTrace();
                Logger.info("Error: " + e.getMessage());
            }
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result getAllStoreMerchant() {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            try {
                List<Store> data = StoreRepository.findAllStoreIsActiveByMerchant(ownMerchant.id, "", "", 0, 0);

                response.setBaseResponse(data.size(), 0, 0, "Berhasil menampilkan list store", listStoreResponsesPup(data));
                return ok(Json.toJson(response));
            } catch (Exception e) {
                Logger.info("Error: " + e.getMessage());
            }
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    @ApiOperation(value = "Get store", notes = "Returns of store.\n" + swaggerInfo
            + "", responseContainer = "object", httpMethod = "GET")
    public static Result getStoreById (Long id) {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            try {
                Store store = Store.findById(id);
                if (store == null) {
                    response.setBaseResponse(0, 0, 0, " Store is not found.", null);
                    return badRequest(Json.toJson(response));
                }

                List<ProductMerchant> productMerchantList = ProductMerchantRepository.findProductMerchant(ownMerchant.id, store.id);

                response.setBaseResponse(1, 0, 0, "Berhasil menampilkan data toko", detailStoreResponse(store, productMerchantList));
                return ok(Json.toJson(response));
            } catch (Exception e) {
                Logger.info("Error: " + e.getMessage());
            }
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    @ApiOperation(value = "Get store by store code", notes = "Returns of store.\n" + swaggerInfo
            + "", responseContainer = "object", httpMethod = "GET")
    public static Result getStoreByStoreCode (String storeCode) {
        int authority = checkAccessAuthorization("all");
        if (authority == 200 || authority == 203) {
            try {
                Store store = Store.findByStoreCode(storeCode);
                if (store == null) {
                    response.setBaseResponse(0, 0, 0, " Store is not found.", null);
                    return badRequest(Json.toJson(response));
                }
                StoreResponse storeResponse = toResponse(store);
                response.setBaseResponse(1, 0, 0, success + " Showing data store", storeResponse);
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

    @ApiOperation(value = "Get store by alias", notes = "Returns of store.\n" + swaggerInfo
            + "", responseContainer = "object", httpMethod = "GET")
    public static Result getStoreByAlias(String storeAlias) {
        int authority = checkAccessAuthorization("all");
        if (authority == 200 || authority == 203) {
            try {
                Store store = Store.find.where().eq("storeAlias", storeAlias).eq("isDeleted", false).eq("isActive", true).findUnique();
                if (store == null) {
                    response.setBaseResponse(0, 0, 0, "Store is not found.", null);
                    return badRequest(Json.toJson(response));
                }

                StoreResponse storeResponse = toResponse(store);
                response.setBaseResponse(1, 0, 0, success + " Showing data store", storeResponse);
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

    private static String validateRequest(StoreRequest storeRequest) {
        if (storeRequest == null)
            return "Request is null or empty";
        if (storeRequest.getStoreName() == null || storeRequest.getStoreName().trim().isEmpty())
            return "Name is null or empty";
        if (storeRequest.getStorePhone() == null || storeRequest.getStorePhone().trim().isEmpty())
            return "Phone is null or empty";
        if (storeRequest.getProvinceId() == null)
            return "Province is null or empty";
        if (storeRequest.getCityId() == null)
            return "City is null or empty";
        if (storeRequest.getSuburbId() == null)
            return "Suburb is null or empty";
        if (storeRequest.getAreaId() == null)
            return "Area is null or empty";
        if (storeRequest.getAddress() == null || storeRequest.getAddress().trim().isEmpty())
            return "Address is null or empty";

        // ========== Validate Shipper ========== //
        if (storeRequest.getStoreName().length() > 50) {
            return "Nama toko tidak boleh lebih dari 50 karakter";
        }

        if (storeRequest.getProvinceId() != null) {
            ShipperProvince province = ShipperProvince.findById(storeRequest.getProvinceId());
            if (province == null) {
                return "Provinsi tidak ditemukan";
            }
        }
        if (storeRequest.getCityId() != null) {
            ShipperCity shipperCity = ShipperCity.findById(storeRequest.getCityId());
            if (shipperCity == null) {
                return "Kota tidak ditemukan";
            }
        }
        if (storeRequest.getSuburbId() != null) {
            ShipperSuburb shipperSuburb = ShipperSuburb.findById(storeRequest.getSuburbId());
            if (shipperSuburb == null) {
                return "Kecamatan tidak ditemukan";
            }
        }
        if (storeRequest.getAreaId() != null) {
            ShipperArea shipperArea = ShipperArea.findById(storeRequest.getAreaId());
            if (shipperArea == null) {
                return "Kelurahan tidak ditemukan";
            }
        }
        if (!storeRequest.getStorePhone().matches(CommonFunction.phoneRegex)){
            return "Format nomor telepon tidak valid.";
        }
        return null;
    }

    private static StoreResponse toResponse(Store store) {
        StoreResponse storeResponse = new StoreResponse(store);
        return storeResponse;
    }

    private static List<StoreResponse> listStoreResponses(List<Store> listStore) {
        List<StoreResponse> responses = new ArrayList<>();
        for (Store store : listStore) {
            StoreResponse storeResponse = new StoreResponse(store);
            responses.add(storeResponse);
        }

        return responses;
    }

    private static List<StoreResponsePuP> listStoreResponsesPup(List<Store> listStore) {
        List<StoreResponsePuP> responses = new ArrayList<>();
        for (Store store : listStore) {
            StoreResponsePuP storeResponsePuP = new StoreResponsePuP(store);
            responses.add(storeResponsePuP);
        }

        return responses;
    }

    private static StoreResponse detailStoreResponse(Store store, List<ProductMerchant> productMerchantList) {
        List<ProductStoreResponseForStore> list = new ArrayList<>();
        for (ProductMerchant productMerchant : productMerchantList) {
            ProductMerchantDetail productMerchantDetail = ProductMerchantDetailRepository.findByProduct(productMerchant);
            String linkQrProductMerchant = productMerchantDetail.getProductMerchantQrCode();
            String qrProductMerchantUrl = null;
            if (linkQrProductMerchant != null) {
                String[] parts = linkQrProductMerchant.split("/");
                qrProductMerchantUrl = parts[0]+"/"+parts[1]+"/"+parts[2]+"/"+"home/"+store.storeCode+"/"+store.id+"/"+productMerchant.getMerchant().id+"/"+parts[4]+"/"+parts[5];
            }
            String qrProductMerchantUrlAlias = null;
            if (linkQrProductMerchant != null) {
                String[] parts = linkQrProductMerchant.split("/");
                qrProductMerchantUrlAlias = parts[0]+"/"+parts[1]+"/"+parts[2]+"/"+"home/"+store.getStoreAlias()+"/"+store.id+"/"+productMerchant.getMerchant().id+"/"+parts[4]+"/"+parts[5];
            }
            ProductStoreResponseForStore productStoreResponse = new ProductStoreResponseForStore();
            productStoreResponse.setProductId(productMerchant.id);
            productStoreResponse.setProductName(productMerchant.getProductName());
            productStoreResponse.setProductStoreQrCode(qrProductMerchantUrl);
            productStoreResponse.setProductStoreQrCodeAlias(qrProductMerchantUrlAlias);
            list.add(productStoreResponse);
        }

        StoreResponse storeResponse = new StoreResponse(store);
        storeResponse.setProductStoreResponses(list);

        return storeResponse;
    }

}
