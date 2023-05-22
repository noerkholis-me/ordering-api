package controllers.merchants;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Query;
import com.avaje.ebean.Transaction;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hokeba.api.BaseResponse;
import com.hokeba.util.CommonFunction;
import com.hokeba.util.Constant;
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
import repository.*;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import repository.pickuppoint.PickUpPointRepository;
import utils.ShipperHelper;
import com.hokeba.util.Helper;
import java.math.BigDecimal;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
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
                        Store store = new Store();
                        constructEntity(ownMerchant, storeRequest, store, Boolean.FALSE);

                        String storeAlias = slugGenerate(storeRequest.getStoreName());
                        Store alias = Store.find.where().raw("store_alias ~ '^" + storeAlias + "-[0-9]*$' ORDER BY store_alias DESC limit 1").findUnique();

                        if (alias != null) {
                            String[] tmpAlias = alias.storeAlias.split("-");
                            String aliasName = tmpAlias[0];
                            int currentindex = Integer.parseInt(tmpAlias[1]);
                            int nextIndex = currentindex + 1;

                            store.storeAlias = aliasName + "-" + nextIndex;
                        } else {
                            store.storeAlias = storeAlias + "-1";
                        }

                        store.save();
                        trx.commit();

                        response.setBaseResponse(1, 0, 1, success + " Store created successfully", toResponse(store));
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
                } else {
                    Store store = Store.findById(id);
                    if (store == null) {
                        response.setBaseResponse(0, 0, 0, " Store is not found.", null);
                        return badRequest(Json.toJson(response));
                    }
                    Transaction trx = Ebean.beginTransaction();
                    try {
                        store.storeName = storeRequest.getStoreName();
                        store.storePhone = storeRequest.getStorePhone();
                        store.storeAddress = storeRequest.getAddress();
                        store.isActive = Boolean.TRUE;
                        store.setStatusOpenStore(storeRequest.getStatusOpenStore());
                        store.setOpenAt(storeRequest.getOpenAt());
                        store.setClosedAt(storeRequest.getClosedAt());
                        store.shipperProvince = ShipperProvince.findById(storeRequest.getProvinceId());
                        store.shipperCity = ShipperCity.findById(storeRequest.getCityId());
                        store.shipperSuburb = ShipperSuburb.findById(storeRequest.getSuburbId());
                        store.shipperArea = ShipperArea.findById(storeRequest.getAreaId());
                        store.setStoreGmap(storeRequest.getGoogleMapsUrl());
                        String [] finalLotLang = getLongitudeLatitude(store.storeGmap);
                        store.storeLatitude = Double.parseDouble(finalLotLang[0]);
                        store.storeLongitude = Double.parseDouble(finalLotLang[1]);
                        store.setStoreQrCode(Constant.getInstance().getFrontEndUrl().concat(store.storeCode));
                        store.storeLogo = storeRequest.getStoreLogo();
                        System.out.println(store.storeQrCode);

                        String storeAlias = slugGenerate(storeRequest.getStoreName());
                        Store alias = Store.find.where().raw("store_alias ~ '^" + storeAlias + "-[0-9]*$' ORDER BY store_alias DESC limit 1").findUnique();

                        // if alias exist, check store alias
                        if (alias != null) {
                            String[] tmpAlias = alias.storeAlias.split("-");
                            String aliasName = tmpAlias[0];
                            int currentindex = Integer.parseInt(tmpAlias[1]);
                            int nextIndex = currentindex + 1;

                            // if store alias == existing alias, check again
                            if (storeAlias.equalsIgnoreCase(aliasName)) {
                                Store getStore = Store.find.byId(id);
                                if (getStore.storeAlias == null || getStore.storeAlias.trim().isEmpty()) {
                                    store.storeAlias = storeAlias + "-" + nextIndex;
                                } else {
                                    String[] tmpCurrentAlias = getStore.storeAlias.split("-");
                                    String currentAliasName = tmpCurrentAlias[0];
                                    // if store alias != current store alias update store alias (e.g mystore != mynewstore)
                                    if (!storeAlias.equalsIgnoreCase(currentAliasName)) {
                                        store.storeAlias = storeAlias + "-" + nextIndex;
                                    }
                                }
                            }
                        } else {
                            // create new store alias
                            store.storeAlias = storeAlias + "-1";
                        }

                        store.update();
                        trx.commit();

                        response.setBaseResponse(1, 0, 1, success + " Store updated successfully", null);
                        return ok(Json.toJson(response));
                    } catch (Exception e) {
                        logger.error("Error while updating store", e);
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

    @ApiOperation(value = "Get all store list.", notes = "Returns list of store.\n" + swaggerInfo
            + "", responseContainer = "List", httpMethod = "GET")
    public static Result getAllStore (String filter, String sort, int offset, int limit) {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            try {
                Query<Store> query = Store.findStoreIsActiveAndMerchant(ownMerchant);
                List<Store> totalData = Store.getTotalDataPage(query);
                List<Store> storeList = Store.findStoreWithPaging(query, sort, filter, offset, limit);
                List<StoreResponse> storeResponses = toResponses(storeList);
                response.setBaseResponse(filter == null || filter.equals("") ? totalData.size() : storeList.size(), offset, limit, success + " Showing data stores", storeResponses);
                return ok(Json.toJson(response));
            } catch (Exception e) {
                e.printStackTrace();
                Logger.info("Error: " + e.getMessage());
            }
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    @ApiOperation(value = "Get all list of store.", notes = "Returns all list of store.\n" + swaggerInfo
        + "", responseContainer = "List", httpMethod = "GET")
    public static Result getAllStoreFromAllMerchant (String filter, String sort, int offset, int limit) {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            try {
                Query<Store> query = Store.findAllStoreFromAllMerchant();
                List<Store> totalData = Store.getTotalDataPage(query);
                List<Store> storeList = Store.findStoreWithPaging(query, sort, filter, offset, limit);
                List<StoreResponse> storeResponses = toResponses(storeList);
                response.setBaseResponse(filter == null || filter.equals("") ? totalData.size() : storeList.size(), offset, limit, success + " Showing data stores", storeResponses);
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

                response.setBaseResponse(1, 0, 0, success + " Showing data store", toResponse(store, productMerchantList));
                return ok(Json.toJson(response));
            } catch (Exception e) {
                Logger.info("Error: " + e.getMessage());
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

    private static Boolean IsStoreClosed(Store store) {
        Boolean storeIsClosed = false;

        if(store.getStatusOpenStore() == null) {
            storeIsClosed = true;
            String featureCreationDateStr = "2023-05-17";
            LocalDate featureOnOfStoreCreationDate = LocalDate.parse(featureCreationDateStr);
            if(store.merchant.createdAt.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isBefore(featureOnOfStoreCreationDate)) {
                storeIsClosed = false;
            }
        } else {
            if(!store.getStatusOpenStore()) {
                storeIsClosed = true;
            } else {
                if(store.getOpenAt() == null && store.getClosedAt() == null) {
                    storeIsClosed = false;
                } else {
                    LocalTime currentTime = LocalTime.now();
                    LocalTime openTime = LocalTime.parse(store.getOpenAt());
                    LocalTime closeTime = LocalTime.parse(store.getClosedAt());
                    if(currentTime.isAfter(openTime) && currentTime.isBefore(closeTime) ) {
                        storeIsClosed = false;
                    } else {
                        storeIsClosed = true;
                    }
                }
            }
        }

        return storeIsClosed;
    }

    private static StoreResponse toResponse(Store store) {

        Boolean storeIsClosed = IsStoreClosed(store);

        return StoreResponse.builder()
                .id(store.id)
                .storeCode(store.storeCode)
                .storeName(store.storeName)
                .storeAlias(store.storeAlias)
                .storePhone(store.storePhone)
                .address(store.storeAddress)
                .province(ShipperHelper.toProvinceResponse(store.shipperProvince))
                .city(ShipperHelper.toCityResponse(store.shipperCity))
                .suburb(ShipperHelper.toSuburbResponse(store.shipperSuburb))
                .area(ShipperHelper.toAreaResponse(store.shipperArea))
                .googleMapsUrl(store.getStoreGmap())
                .latitude(store.storeLatitude)
                .longitude(store.storeLongitude)
                .storeQrCode(store.getStoreQrCode())
                .merchantId(store.merchant.id)
                .storeLogo(store.storeLogo)
                .merchantType(store.merchant.merchantType)
                .storeQueueUrl(Helper.MOBILEQR_URL + store.storeCode + "/queue")
                .statusOpenStore(!storeIsClosed)
                .openAt(store.getOpenAt())
                .closedAt(store.getClosedAt())
                .build();
    }

    private static StoreResponse toResponse(Store store, List<ProductMerchant> productMerchantList) {
        List<ProductStoreResponseForStore> list = new ArrayList<>();
        for (ProductMerchant productMerchant : productMerchantList) {
            ProductMerchantDetail productMerchantDetail = ProductMerchantDetailRepository.findByProduct(productMerchant);
            String linkQrProductMerchant = productMerchantDetail.getProductMerchantQrCode();
            String qrProductMerchantUrl = null;
            if (linkQrProductMerchant != null) {
                String[] parts = linkQrProductMerchant.split("/");
                qrProductMerchantUrl = parts[0]+"/"+parts[1]+"/"+parts[2]+"/"+"home/"+store.storeCode+"/"+store.id+"/"+productMerchant.getMerchant().id+"/"+parts[4]+"/"+parts[5];
            }
            ProductStoreResponseForStore productStoreResponse = new ProductStoreResponseForStore();
            productStoreResponse.setProductId(productMerchant.id);
            productStoreResponse.setProductName(productMerchant.getProductName());
            productStoreResponse.setProductStoreQrCode(qrProductMerchantUrl);
            list.add(productStoreResponse);
        }
        return StoreResponse.builder()
                .id(store.id)
                .storeCode(store.storeCode)
                .storeName(store.storeName)
                .storeAlias(store.storeAlias)
                .storePhone(store.storePhone)
                .address(store.storeAddress)
                .province(ShipperHelper.toProvinceResponse(store.shipperProvince))
                .city(ShipperHelper.toCityResponse(store.shipperCity))
                .suburb(ShipperHelper.toSuburbResponse(store.shipperSuburb))
                .area(ShipperHelper.toAreaResponse(store.shipperArea))
                .googleMapsUrl(store.getStoreGmap())
                .latitude(store.storeLatitude)
                .longitude(store.storeLongitude)
                .storeQrCode(store.getStoreQrCode())
                .merchantId(store.merchant.id)
                .storeLogo(store.storeLogo)
                .merchantType(store.merchant.merchantType)
                .storeQueueUrl(Helper.MOBILEQR_URL + store.storeCode + "/queue")
                .productStoreResponses(list)
                .statusOpenStore(store.statusOpenStore)
                .openAt(store.openAt)
                .closedAt(store.closedAt)
                .build();
    }

    private static List<StoreResponse> toResponses(List<Store> stores) {
        List<StoreResponse> storeResponses = new ArrayList<>();
        stores.forEach(store -> storeResponses.add(toResponse(store)));
        return storeResponses;
    }

    private static void constructEntity(Merchant ownMerchant, StoreRequest storeRequest, Store store, Boolean isEdit) {
        store.setMerchant(ownMerchant);
        store.storeName = storeRequest.getStoreName();
        store.storePhone = storeRequest.getStorePhone();
        store.storeAddress = storeRequest.getAddress();
        store.isActive = Boolean.TRUE;
        store.statusOpenStore = Boolean.TRUE;
        store.shipperProvince = ShipperProvince.findById(storeRequest.getProvinceId());
        store.shipperCity = ShipperCity.findById(storeRequest.getCityId());
        store.shipperSuburb = ShipperSuburb.findById(storeRequest.getSuburbId());
        store.shipperArea = ShipperArea.findById(storeRequest.getAreaId());
        store.storeGmap = storeRequest.getGoogleMapsUrl();
        if(!isEdit){ 
            store.activeBalance = BigDecimal.ZERO;
        }
        String [] finalLotLang = getLongitudeLatitude(store.storeGmap);
        store.storeLatitude = Double.parseDouble(finalLotLang[0]);
        store.storeLongitude = Double.parseDouble(finalLotLang[1]);
        if (isEdit == Boolean.FALSE) {
            store.storeCode = CommonFunction.generateRandomString(8);
        }
        store.storeQrCode = Constant.getInstance().getFrontEndUrl().concat(store.storeCode);
        store.storeLogo = storeRequest.getStoreLogo();
    }

    public static String [] getLongitudeLatitude(String paramGmap){
        //String tmpString = "https://www.google.com/maps/place/Toko+Ne/@-6.9326603,107.6011616,515m/data=!3m1!1e3!4m13!1m7!3m6!1s0x2e68e899de51f023:0x40cea56365748dcf!2sAstanaanyar,+Bandung+City,+West+Java!3b1!8m2!3d-6.9299008!4d107.5993373!3m4!1s0x2e68e89eebc34b29:0x2e8c9826fb62b77e!8m2!3d-6.9327152!4d107.6020338";
        String [] tmpLongLat = paramGmap.split("@");
        String [] finalLotLang = tmpLongLat[1].split("/");
        String [] output = finalLotLang[0].split(",");
        return output;
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

    public static Result getAllStoreMerchant() {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            try {
                Query<Store> query = Store.findStoreIsActiveAndMerchant(ownMerchant);
                List<Store> storeList = Store.findAllStore(query);
                List<StoreResponsePuP> storeResponses = toResponsesPuP(storeList);
                response.setBaseResponse(storeList.size(), 0, 0, "Berhasil menampilkan list store", storeResponses);
                return ok(Json.toJson(response));
            } catch (Exception e) {
                Logger.info("Error: " + e.getMessage());
            }
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    private static StoreResponsePuP toResponsePuP(Store store) {
        return StoreResponsePuP.builder()
                .id(store.id)
                .storeCode(store.storeCode)
                .storeName(store.storeName)
                .build();
    }

    private static List<StoreResponsePuP> toResponsesPuP(List<Store> stores) {
        List<StoreResponsePuP> storeResponses = new ArrayList<>();
        stores.forEach(store -> storeResponses.add(toResponsePuP(store)));
        return storeResponses;
    }

    public static String slugGenerate(String input) {
        String slug = Normalizer.normalize(input.toLowerCase(), Normalizer.Form.NFD)
            .replaceAll("\\p{InCombiningDiacriticalMarks}+", "").replaceAll("[^\\p{Alnum}]+", "");
        if (slug.length() != 0 && slug.charAt(slug.length() - 1) == '-') {
            return slug.substring(0, slug.length() - 1);
        } else {
            return slug;
        }
    }

}
