package controllers.merchants;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Query;
import com.avaje.ebean.Transaction;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hokeba.api.BaseResponse;
import controllers.BaseController;
import dtos.table.TableTypeRequest;
import dtos.table.TableTypeResponse;
import models.Merchant;
import models.merchant.TableType;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import repository.TableTypeRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TableTypeController extends BaseController {

    private final static Logger.ALogger logger = Logger.of(TableTypeController.class);

    private static final Integer MAXIMUM_TABLE_TYPE = 3;

    private static BaseResponse response = new BaseResponse();

    private static ObjectMapper objectMapper = new ObjectMapper();

    public static Result addTableType() {
        Merchant merchant = checkMerchantAccessAuthorization();
        if (merchant != null) {
            JsonNode json = request().body().asJson();
            try {
                TableTypeRequest tableTypeRequest = objectMapper.readValue(json.toString(), TableTypeRequest.class);
                List<TableType> tableTypes = TableTypeRepository.findAllByMerchant(merchant);
                if (tableTypes.size() >= MAXIMUM_TABLE_TYPE) {
                    response.setBaseResponse(0, 0, 0, "maximum table type is only 3", null);
                    return badRequest(Json.toJson(response));
                }
                if (tableTypeRequest.getMinimumTableCount() < 1 || tableTypeRequest.getMaximumTableCount() <= tableTypeRequest.getMinimumTableCount()) {
                    response.setBaseResponse(0, 0, 0, "minimum table count must be greater than 0 and maximum table count must be greater than minimum table count", null);
                    return badRequest(Json.toJson(response));
                }
                Transaction trx = Ebean.beginTransaction();
                try {
                    TableType tableType = new TableType();
                    tableType.setName(tableTypeRequest.getName());
                    tableType.setMinimumTableCount(tableTypeRequest.getMinimumTableCount());
                    tableType.setMaximumTableCount(tableTypeRequest.getMaximumTableCount());
                    tableType.setMerchant(merchant);
                    tableType.save();

                    trx.commit();

                    response.setBaseResponse(1,offset, 1, success + " Create Table Type", tableType.id);
                    return ok(Json.toJson(response));
                } catch (Exception e) {
                    logger.error("Error while creating table type", e);
                    e.printStackTrace();
                    trx.rollback();
                } finally {
                    trx.end();
                }
            } catch (Exception e) {
                logger.error("Error while creating table type", e);
                e.printStackTrace();
            }
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result editTableType(Long id) {
        Merchant merchant = checkMerchantAccessAuthorization();
        if (merchant != null) {
            JsonNode json = request().body().asJson();
            try {
                TableTypeRequest tableTypeRequest = objectMapper.readValue(json.toString(), TableTypeRequest.class);
                Optional<TableType> tableType = TableTypeRepository.findById(id, merchant);
                if (!tableType.isPresent()) {
                    response.setBaseResponse(0, 0, 0, "table type does not exists.", null);
                    return badRequest(Json.toJson(response));
                }
                if (tableTypeRequest.getMinimumTableCount() < 1 || tableTypeRequest.getMaximumTableCount() <= tableTypeRequest.getMinimumTableCount()) {
                    response.setBaseResponse(0, 0, 0, "minimum table count must be greater than 0 and maximum table count must be greater than minimum table count", null);
                    return badRequest(Json.toJson(response));
                }
                Transaction trx = Ebean.beginTransaction();
                try {
                    TableType getTableType = tableType.get();
                    getTableType.setName(tableTypeRequest.getName());
                    getTableType.setMinimumTableCount(tableTypeRequest.getMinimumTableCount());
                    getTableType.setMaximumTableCount(tableTypeRequest.getMaximumTableCount());
                    getTableType.setMerchant(merchant);
                    getTableType.update();

                    trx.commit();

                    response.setBaseResponse(1,offset, 1, success + " Update Table Type", getTableType.id);
                    return ok(Json.toJson(response));
                } catch (Exception e) {
                    logger.error("Error while creating table type", e);
                    e.printStackTrace();
                    trx.rollback();
                } finally {
                    trx.end();
                }

            } catch (Exception e) {
                logger.error("Error while creating table type", e);
                e.printStackTrace();
            }
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result getAllTableType(String filter, String sort, int offset, int limit) {
        Merchant merchant = checkMerchantAccessAuthorization();
        if (merchant != null) {
            try {
                Query<TableType> tableTypeQuery = TableTypeRepository.findByIsActiveAndMerchant(merchant);
                List<TableType> totalDataPage = TableTypeRepository.getTotalDataPage(tableTypeQuery);
                List<TableType> tableTypes = TableTypeRepository.findTableTypeWithPaging(tableTypeQuery, sort, filter, offset, limit);
                List<TableTypeResponse> tableTypeResponses = new ArrayList<>();
                for (TableType tableType : tableTypes) {
                    TableTypeResponse tableTypeResponse = new TableTypeResponse();
                    tableTypeResponse.setId(tableType.id);
                    tableTypeResponse.setName(tableType.getName());
                    tableTypeResponse.setTotalPerson(tableType.getMinimumTableCount() + " - " + tableType.getMaximumTableCount());
                    tableTypeResponses.add(tableTypeResponse);
                }
                response.setBaseResponse(filter == null || filter.equals("") ? totalDataPage.size() : tableTypeResponses.size(), offset, limit, success + " Showing data table types", tableTypeResponses);
                return ok(Json.toJson(response));
            } catch (Exception e) {
                logger.error("Error while getting list table type", e);
                e.printStackTrace();
            }
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result getTableTypeById(Long id) {
        Merchant merchant = checkMerchantAccessAuthorization();
        if (merchant != null) {
            try {
                Optional<TableType> tableType = TableTypeRepository.findById(id, merchant);
                if (!tableType.isPresent()) {
                    response.setBaseResponse(0, 0, 0, "table type does not exists.", null);
                    return badRequest(Json.toJson(response));
                }
                TableTypeResponse tableTypeResponse = new TableTypeResponse();
                tableTypeResponse.setId(tableType.get().id);
                tableTypeResponse.setName(tableType.get().getName());
                tableTypeResponse.setTotalPerson(tableType.get().getMinimumTableCount() + " - " + tableType.get().getMaximumTableCount());
                response.setBaseResponse(1,offset, 1, success + " showing Table Type with id : " + id, tableTypeResponse);
                return ok(Json.toJson(response));
            } catch (Exception e) {
                logger.error("Error while getting table type", e);
                e.printStackTrace();
            }
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result deleteTableType(Long id) {
        Merchant merchant = checkMerchantAccessAuthorization();
        if (merchant != null) {
            try {
                Optional<TableType> tableType = TableTypeRepository.findById(id, merchant);
                if (!tableType.isPresent()) {
                    response.setBaseResponse(0, 0, 0, "table type does not exists.", null);
                    return badRequest(Json.toJson(response));
                }
                tableType.get().isDeleted = Boolean.TRUE;
                tableType.get().update();
                response.setBaseResponse(1,offset, 1, success + " deleting Table Type with id : " + id, tableType.get().id);
                return ok(Json.toJson(response));
            } catch (Exception e) {
                logger.error("Error while deleting table type", e);
                e.printStackTrace();
            }
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }




}
