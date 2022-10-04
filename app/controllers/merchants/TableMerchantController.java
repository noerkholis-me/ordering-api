package controllers.merchants;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Query;
import com.avaje.ebean.Transaction;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hokeba.api.BaseResponse;
import controllers.BaseController;
import dtos.table.DisplayTableResponse;
import dtos.table.TableMerchantRequest;
import dtos.table.TableMerchantResponse;
import models.Merchant;
import models.Store;
import models.merchant.TableMerchant;
import models.merchant.TableType;
import models.transaction.Order;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import repository.OrderRepository;
import repository.TableMerchantRepository;
import repository.TableTypeRepository;

import java.util.*;
import java.util.function.Function;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.*;

public class TableMerchantController extends BaseController {

    private final static Logger.ALogger logger = Logger.of(TableMerchantController.class);

    private static BaseResponse response = new BaseResponse();

    private static ObjectMapper objectMapper = new ObjectMapper();

    public static Result addTable() {
        Merchant merchant = checkMerchantAccessAuthorization();
        if (merchant != null) {
            JsonNode json = request().body().asJson();
            try {
                TableMerchantRequest tableMerchantRequest = objectMapper.readValue(json.toString(), TableMerchantRequest.class);
                Transaction trx = Ebean.beginTransaction();
                try {
                    Store store = Store.findById(tableMerchantRequest.getStoreId());
                    if (store == null) {
                        response.setBaseResponse(0, 0, 0, "store id does not exists", null);
                        return badRequest(Json.toJson(response));
                    }
                    Optional<TableType> tableType = TableTypeRepository.findById(tableMerchantRequest.getTableTypeId(), merchant);
                    if (!tableType.isPresent()) {
                        response.setBaseResponse(0, 0, 0, "table type id does not exists", null);
                        return badRequest(Json.toJson(response));
                    }
                    if(tableMerchantRequest.getName().length() > 50) {
                        response.setBaseResponse(0, 0, 0, "Jumlah karakter tidak boleh melebihi 50 karakter", null);
                        return badRequest(Json.toJson(response));
                    }
                    TableMerchant tableMerchant = new TableMerchant();
                    tableMerchant.setName(tableMerchantRequest.getName());
                    tableMerchant.setIsActive(tableMerchantRequest.getIsActive());
                    tableMerchant.setIsAvailable(Boolean.TRUE);
                    tableMerchant.setStore(store);
                    tableMerchant.setTableType(tableType.get());
                    tableMerchant.save();

                    trx.commit();

                    response.setBaseResponse(1,offset, 1, success + " Create Table", tableMerchant.id);
                    return ok(Json.toJson(response));
                } catch (Exception e) {
                    logger.error("Error while creating table", e);
                    e.printStackTrace();
                    trx.rollback();
                } finally {
                    trx.end();
                }

            } catch (Exception e) {
                logger.error("Error while creating table", e);
                e.printStackTrace();
            }
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result editTable(Long id) {
        Merchant merchant = checkMerchantAccessAuthorization();
        if (merchant != null) {
            JsonNode json = request().body().asJson();
            try {
                TableMerchantRequest tableMerchantRequest = objectMapper.readValue(json.toString(), TableMerchantRequest.class);
                Transaction trx = Ebean.beginTransaction();
                try {
                    Optional<TableMerchant> tableMerchant = TableMerchantRepository.findById(id);
                    if (!tableMerchant.isPresent()) {
                        response.setBaseResponse(0, 0, 0, "table id does not exists", null);
                        return badRequest(Json.toJson(response));
                    }
                    Store store = Store.findById(tableMerchantRequest.getStoreId());
                    if (store == null) {
                        response.setBaseResponse(0, 0, 0, "store id does not exists", null);
                        return badRequest(Json.toJson(response));
                    }
                    Optional<TableType> tableType = TableTypeRepository.findById(tableMerchantRequest.getTableTypeId(), merchant);
                    if (!tableType.isPresent()) {
                        response.setBaseResponse(0, 0, 0, "table type id does not exists", null);
                        return badRequest(Json.toJson(response));
                    }
                    if(tableMerchantRequest.getName().length() > 50) {
                        response.setBaseResponse(0, 0, 0, "Jumlah karakter tidak boleh melebihi 50 karakter", null);
                        return badRequest(Json.toJson(response));
                    }
                    tableMerchant.get().setName(tableMerchantRequest.getName());
                    tableMerchant.get().setIsActive(tableMerchantRequest.getIsActive());
                    tableMerchant.get().setTableType(tableType.get());
                    tableMerchant.get().setStore(store);
                    tableMerchant.get().update();

                    trx.commit();

                    response.setBaseResponse(1,offset, 1, success + " Update Table", tableMerchant.get().id);
                    return ok(Json.toJson(response));
                } catch (Exception e) {
                    logger.error("Error while updating table", e);
                    e.printStackTrace();
                    trx.rollback();
                } finally {
                    trx.end();
                }

            } catch (Exception e) {
                logger.error("Error while updating table", e);
                e.printStackTrace();
            }
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result getAllTable(Long storeId, String filter, String sort, int offset, int limit) {
        Merchant merchant = checkMerchantAccessAuthorization();
        if (merchant != null) {
            Query<TableMerchant> tableMerchantQuery = null;
            if (storeId == null || storeId == 0) {
                tableMerchantQuery = TableMerchantRepository.findAllTablesQuery();
            } else {
                tableMerchantQuery = TableMerchantRepository.findAllTablesByStoreIdQuery(storeId);
            }
            List<TableMerchant> totalDataPage = TableMerchantRepository.getTotalPage(tableMerchantQuery);
            List<TableMerchant> tableMerchants = TableMerchantRepository.findTablesWithPaging(tableMerchantQuery, sort, filter, offset, limit);
            List<TableMerchantResponse> tableMerchantResponses = new ArrayList<>();
            for (TableMerchant tableMerchant : tableMerchants) {
                TableMerchantResponse tableMerchantResponse = toResponse(tableMerchant);
                tableMerchantResponses.add(tableMerchantResponse);
            }
            response.setBaseResponse(filter == null || filter.equals("") ? totalDataPage.size() : tableMerchantResponses.size(), offset, limit, success + " Showing data tables", tableMerchantResponses);
            return ok(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result getTableById(Long id) {
        Merchant merchant = checkMerchantAccessAuthorization();
        if (merchant != null) {
            try {
                Optional<TableMerchant> tableMerchant = TableMerchantRepository.findById(id);
                if (!tableMerchant.isPresent()) {
                    response.setBaseResponse(0, 0, 0, "table id does not exists", null);
                    return badRequest(Json.toJson(response));
                }
                response.setBaseResponse(1, 0, 1, success + " Showing data table", toResponse(tableMerchant.get()));
                return ok(Json.toJson(response));
            } catch (Exception e) {
                logger.error("Error while getting table", e);
                e.printStackTrace();
            }
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result deleteTable(Long id) {
        Merchant merchant = checkMerchantAccessAuthorization();
        if (merchant != null) {
            try {
                Optional<TableMerchant> tableMerchant = TableMerchantRepository.findById(id);
                if (!tableMerchant.isPresent()) {
                    response.setBaseResponse(0, 0, 0, "table id does not exists", null);
                    return badRequest(Json.toJson(response));
                }
                tableMerchant.get().isDeleted = Boolean.TRUE;
                tableMerchant.get().setIsActive(Boolean.FALSE);
                tableMerchant.get().update();
                response.setBaseResponse(1, 0, 1, success + " Delete data table", tableMerchant.get().id);
                return ok(Json.toJson(response));
            } catch (Exception e) {
                logger.error("Error while deleting table", e);
                e.printStackTrace();
            }
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    private static TableMerchantResponse toResponse(TableMerchant tableMerchant) {
        TableMerchantResponse tableMerchantResponse = new TableMerchantResponse();
        tableMerchantResponse.setId(tableMerchant.id);
        tableMerchantResponse.setName(tableMerchant.getName());
        tableMerchantResponse.setIsActive(tableMerchant.getIsActive());
        tableMerchantResponse.setIsAvailable(tableMerchant.getIsAvailable());

        TableMerchantResponse.StoreRes storeRes = new TableMerchantResponse.StoreRes();
        storeRes.setId(tableMerchant.getStore().id);
        storeRes.setName(tableMerchant.getStore().storeName);

        TableMerchantResponse.TableTypeRes tableTypeRes = new TableMerchantResponse.TableTypeRes();
        tableTypeRes.setId(tableMerchant.getTableType().id);
        tableTypeRes.setName(tableMerchant.getTableType().getName());

        tableMerchantResponse.setStoreResponse(storeRes);
        tableMerchantResponse.setTableTypeResponse(tableTypeRes);
        return tableMerchantResponse;
    }

    public static Result changeAvailabilityTable(Long tableId) {
        Merchant merchant = checkMerchantAccessAuthorization();
        if (merchant == null) {
            response.setBaseResponse(0, 0, 0, unauthorized, null);
            return unauthorized(Json.toJson(response));
        } else {
            JsonNode json = request().body().asJson();
            Boolean isAvailable = json.get("is_available").asBoolean();
            try {
                Optional<TableMerchant> tableMerchant = TableMerchantRepository.findById(tableId);
                if (!tableMerchant.isPresent()) {
                    response.setBaseResponse(0, 0, 0, "meja tidak ditemukan.", null);
                    return badRequest(Json.toJson(response));
                }
                tableMerchant.get().setIsAvailable(isAvailable);
                tableMerchant.get().update();

                response.setBaseResponse(0, 0, 0, "berhasil mengubah ketersediaan meja", tableMerchant.get().id);
                return ok(Json.toJson(response));
            } catch (Exception e) {
                e.printStackTrace();
                response.setBaseResponse(0, 0, 0, error, null);
                return internalServerError(Json.toJson(response));
            }
        }
    }

    public static Result getDisplayTable(Long storeId) {
        int authority = checkAccessAuthorization("all");
        if (authority == 200 || authority == 203) {
            Store store = Store.findById(storeId);
            if (store == null) {
                response.setBaseResponse(0, 0, 0, "toko tidak ditemukan", null);
                return badRequest(Json.toJson(response));
            }
            List<TableMerchant> tableMerchants = TableMerchantRepository.findTableMerchantByStoreId(storeId);
            List<DisplayTableResponse> displayTableResponses = new ArrayList<>();

            for (TableMerchant tableMerchant : tableMerchants) {
                DisplayTableResponse displayTableResponse = new DisplayTableResponse();
                displayTableResponse.setId(tableMerchant.id);
                displayTableResponse.setTableType(tableMerchant.getTableType().getName());
                displayTableResponse.setTableName(tableMerchant.getName());
                displayTableResponse.setMinimumTableCount(tableMerchant.getTableType().getMinimumTableCount());
                displayTableResponse.setMaximumTableCount(tableMerchant.getTableType().getMaximumTableCount());
                displayTableResponse.setIsAvailable(tableMerchant.getIsAvailable());
                displayTableResponses.add(displayTableResponse);
            }

            response.setBaseResponse(displayTableResponses.size(), 0, 0, success, displayTableResponses);
            return ok(Json.toJson(response));
        } else if (authority == 403) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        } else {
            response.setBaseResponse(0, 0, 0, unauthorized, null);
            return unauthorized(Json.toJson(response));
        }
    }

}
