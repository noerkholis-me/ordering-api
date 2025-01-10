package controllers.merchants;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Query;
import com.avaje.ebean.Transaction;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hokeba.api.BaseResponse;
import com.hokeba.util.Constant;
import com.hokeba.util.Helper;
import controllers.BaseController;
import dtos.cashier.*;
import models.Member;
import models.Merchant;
import models.Store;
import models.UserMerchant;
import models.appsettings.AppSettings;
import models.merchant.CashierHistoryMerchant;
import models.store.StoreAccess;
import models.store.StoreAccessDetail;
import models.transaction.Order;
import models.transaction.OrderPayment;
import models.transaction.OrderStatus;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import repository.*;
import repository.cashierhistory.CashierHistoryMerchantRepository;
import service.DownloadCashierReport;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

public class CashierHistoryController extends BaseController {

    private static final Logger.ALogger LOGGER = Logger.of(CashierHistoryController.class);
    private static final BaseResponse response = new BaseResponse();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * controller for open POS
     */
    public static Result openPos() {
        Merchant merchant = checkMerchantAccessAuthorization();
        if (merchant != null) {
            JsonNode json = request().body().asJson();
            try {
                CashierOpenPosRequest cashierOpenPosRequest = objectMapper.readValue(json.toString(), CashierOpenPosRequest.class);
                String message = validateRequest(cashierOpenPosRequest);
                if (message != null) {
                    response.setBaseResponse(0, 0, 0, inputParameter + " " + message, null);
                    return badRequest(Json.toJson(response));
                }
                Transaction trx = Ebean.beginTransaction();
                try {

                    UserMerchant userMerchant = UserMerchantRepository.findAccountById(cashierOpenPosRequest.getUserMerchantId());
                    if (userMerchant == null) {
                        response.setBaseResponse(0, 0, 0, inputParameter + " user merchant tidak ditemukan", null);
                        return badRequest(Json.toJson(response));
                    }

                    Store store = Store.findById(cashierOpenPosRequest.getStoreId());
                    if (store == null) {
                        response.setBaseResponse(0, 0, 0, inputParameter + " store tidak ditemukan", null);
                        return badRequest(Json.toJson(response));
                    }


                    CashierHistoryMerchant cashier = new CashierHistoryMerchant();
                    cashier.setSessionCode(CashierHistoryMerchant.generateSessionCode(cashierOpenPosRequest.getUserMerchantId(), cashierOpenPosRequest.getStoreId()));
                    cashier.setIsActive(Boolean.TRUE);
                    cashier.setStartTime(new Date());
                    cashier.setUserMerchant(userMerchant);
                    cashier.setStartTotalAmount(cashierOpenPosRequest.getStartTotalAmount());
                    cashier.setStore(store);

                    cashier.save();

                    trx.commit();

                    response.setBaseResponse(1, 0, 0, success, cashier.id);
                    return ok(Json.toJson(response));
                } catch (Exception e) {
                    LOGGER.error("Error while creating session cashier", e);
                    e.printStackTrace();
                    trx.rollback();
                } finally {
                    trx.end();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                response.setBaseResponse(0, 0, 0, error, null);
                return internalServerError(Json.toJson(response));
            }
        } else {
            response.setBaseResponse(0, 0, 0, unauthorized, null);
            return unauthorized(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, error, null);
        return internalServerError(Json.toJson(response));
    }

    public static Result getAllCashierHistory(int limit, int offset, Long storeId) {
        Merchant merchant = checkMerchantAccessAuthorization();
        if (merchant != null) {
            try {

                System.out.println("merchant id >>> " + merchant);

                Query<CashierHistoryMerchant> query = null;
                query = CashierHistoryMerchantRepository.findAllCashierHistoryByMerchantId(merchant.id);
                if (storeId != null && storeId != 0L) {
                    Store store = Store.findById(storeId);
                    if (store == null) {
                        response.setBaseResponse(0, 0, 0, "store tidak ditemukan", null);
                        return badRequest(Json.toJson(response));
                    }
                    query = CashierHistoryMerchantRepository.findAllCashierHistoryByStoreId(storeId);
                } else {
                    query = CashierHistoryMerchantRepository.find.where().eq("store.merchant", merchant).eq("end_time", null).eq("end_total_amount", null).eq("end_total_amount_cash", null).order("created_at desc");
                }
                List<CashierHistoryMerchant> cashierHistoryMerchants = CashierHistoryMerchantRepository.findAllCashierHistory(query, offset, limit);
                Integer totalData = CashierHistoryMerchantRepository.getTotalData(query);
                List<CashierHistoryResponse> responses = new ArrayList<>();
                for (CashierHistoryMerchant historyMerchant : cashierHistoryMerchants) {
                    CashierHistoryResponse response = new CashierHistoryResponse();
                    response.setCashierName(historyMerchant.getUserMerchant().getFullName());
                    response.setSessionCode(historyMerchant.getSessionCode());
                    response.setStartTime(historyMerchant.getStartTime());
                    response.setEndTime(historyMerchant.getEndTime());
                    response.setStoreName(historyMerchant.getStore().storeName);
                    responses.add(response);
                }
                response.setBaseResponse(totalData, offset, limit, success + " menampilkan data history cashier", responses);
                return ok(Json.toJson(response));
            } catch (Exception ex) {
                ex.printStackTrace();
                response.setBaseResponse(0, 0, 0, error, null);
                return internalServerError(Json.toJson(response));
            }
        } else {
            response.setBaseResponse(0, 0, 0, unauthorized, null);
            return unauthorized(Json.toJson(response));
        }
    }

    private static String validateRequest(CashierOpenPosRequest cashierOpenPosRequest) {
        if (cashierOpenPosRequest.getStartTotalAmount() == null)
            return "start total amount tidak boleh kosong";
        if (cashierOpenPosRequest.getStartTotalAmount().compareTo(new BigDecimal(1000)) < 0)
            return "start total amount tidak boleh kurang dari 1000";
        if (cashierOpenPosRequest.getStartTotalAmount().compareTo(new BigDecimal(100000000)) > 0)
            return "start total amount tidak boleh lebih dari 100000000";

        return null;
    }

    /**
     * controller for close POS
     */
    public static Result closePos() {
        UserMerchant currentUserMerchant = checkUserMerchantAccessAuthorization();
        if (currentUserMerchant != null) {
            JsonNode json = request().body().asJson();
            try {
                CashierClosePosRequest cashierClosePosRequest = objectMapper.readValue(json.toString(), CashierClosePosRequest.class);
                Transaction trx = Ebean.beginTransaction();
                try {
                    String message = validateClosePosRequest(cashierClosePosRequest);
                    if (message != null) {
                        response.setBaseResponse(0, 0, 0, inputParameter + " " + message, null);
                        return badRequest(Json.toJson(response));
                    }
                    UserMerchant userMerchant = UserMerchantRepository.findAccountById(currentUserMerchant.id);
                    if (userMerchant == null) {
                        response.setBaseResponse(0, 0, 0, inputParameter + " user merchant tidak ditemukan", null);
                        return badRequest(Json.toJson(response));
                    }

                    Store store = Store.findById(cashierClosePosRequest.getStoreId());
                    if (store == null) {
                        response.setBaseResponse(0, 0, 0, inputParameter + " store tidak ditemukan", null);
                        return badRequest(Json.toJson(response));
                    }

                    Optional<CashierHistoryMerchant> lastSessionCashier = CashierHistoryMerchantRepository.findByUserActiveCashierAndStoreIdOpen(userMerchant.id, store.id);
                    if(!lastSessionCashier.isPresent()){
                        response.setBaseResponse(0, 0, 0, "Kode sesi tidak ditemukan", null);
                        return badRequest(Json.toJson(response));
                    }
                    CashierHistoryMerchant cashierHistoryMerchant = lastSessionCashier.get();
                    cashierHistoryMerchant.setEndTime(new Date());

                    BigDecimal endTotalAmount = BigDecimal.ZERO;
                    Query<Order> orderQuery = OrderRepository.findAllOrderByUserMerchantIdAndStoreId(userMerchant.id, store.id);
                    List<Order> orders = OrderRepository.findOrdersByRangeToday(orderQuery, cashierHistoryMerchant.getStartTime(), new Date());
                    BigDecimal endTotalFromOrder = BigDecimal.ZERO;
                    String orderPendingMessage = "";
                    for (Order order : orders) {
                        if (order.getStatus().equalsIgnoreCase(OrderStatus.PENDING.getStatus())) {
                            orderPendingMessage += "Sesi tidak bisa berakhir karena masih ada order yang belum bayar";
                            break;
                        } else {
                            Optional<OrderPayment> orderPayment = OrderPaymentRepository.findByOrderIdAndStatusAndPaymentChannel(order.id, OrderPayment.PAID);
                            if (orderPayment.isPresent()) {
                                endTotalFromOrder = endTotalFromOrder.add(orderPayment.get().getTotalAmount());
                            }
                            continue;
                        }
                    }

                    if (!orderPendingMessage.equalsIgnoreCase("")) {
                        response.setBaseResponse(0, 0, 0, orderPendingMessage, null);
                        return badRequest(Json.toJson(response));
                    }

                    endTotalAmount = endTotalAmount.add(endTotalFromOrder);

                    // BigDecimal totalAmountClosingBySystem = BigDecimal.ZERO;
                    // Query<Order> orderQuery = OrderRepository.findAllOrderByUserMerchantIdAndStoreId(userMerchant.id, storeId);
                    // List<Order> orders = OrderRepository.findOrdersByRangeToday(orderQuery, cashierHistoryMerchant.get().getStartTime(), new Date());
                    // for (Order order : orders) {
                    //     Optional<OrderPayment> orderPayment = OrderPaymentRepository.findByOrderIdAndStatus(order.id, OrderPayment.PAID);
                    //     if (orderPayment.isPresent()) {
                    //         totalAmountClosingBySystem = totalAmountClosingBySystem.add(cashierHistoryMerchant.get().getStartTotalAmount()).add(orderPayment.get().getTotalAmount());
                    //     }
                    //     continue;
                    // }

                    System.out.print("End total amount: ");
                    System.out.println(Json.toJson(endTotalAmount));
                    System.out.print("Total Cash system: ");
                    System.out.println(Json.toJson(cashierClosePosRequest.getCloseTotalAmountCash()));
                    System.out.println("======================");
                    System.out.println(endTotalAmount.compareTo(cashierClosePosRequest.getCloseTotalAmountCash()));
                    System.out.println(endTotalAmount.compareTo(cashierClosePosRequest.getCloseTotalAmountCash()) > 0 &&
                            (cashierClosePosRequest.getNotes() == null || cashierClosePosRequest.getNotes().isEmpty()));

                    if(endTotalAmount.compareTo(cashierClosePosRequest.getCloseTotalAmountCash()) > 0 &&
                            (cashierClosePosRequest.getNotes() == null || cashierClosePosRequest.getNotes().isEmpty())){
                        response.setBaseResponse(0, 0, 0,
                                "Terdapat selisih antara penutupan kasir sistem dengan closing yang anda masukkan, Silahkan masukkan Catatan!", null);
                        return badRequest(Json.toJson(response));
                    }
                    cashierHistoryMerchant.setEndTotalAmount(endTotalAmount);
                    cashierHistoryMerchant.setEndTotalAmountCash(cashierClosePosRequest.getCloseTotalAmountCash());
                    cashierHistoryMerchant.setNotes(cashierClosePosRequest.getNotes());
                    cashierHistoryMerchant.setIsActive(Boolean.FALSE);
                    cashierHistoryMerchant.update();
                    trx.commit();

                    response.setBaseResponse(1, 0, 0, "Closing Berhasil", cashierHistoryMerchant.id);

                    return ok(Json.toJson(response));
                } catch (Exception e) {
                    LOGGER.error("Error while updating session cashier", e);
                    e.printStackTrace();
                    trx.rollback();
                    response.setBaseResponse(0, 0, 0, e.getMessage(), null);
                    return internalServerError(Json.toJson(response));
                } finally {
                    trx.end();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                response.setBaseResponse(0, 0, 0, ex.getMessage(), null);
                return internalServerError(Json.toJson(response));
            }
        } else {
            response.setBaseResponse(0, 0, 0, unauthorized, null);
            return unauthorized(Json.toJson(response));
        }
    }
    private static String validateClosePosRequest(CashierClosePosRequest cashierClosePosRequest) {
        if (cashierClosePosRequest.getCloseTotalAmountCash() == null)
            return "Nominal Closing (by Kasir) tidak boleh kosong";
        if (cashierClosePosRequest.getCloseTotalAmountCash().compareTo(new BigDecimal(1000)) < 0)
            return "Nominal Closing (by Kasir) tidak boleh kurang dari 1000";

        return null;
    }
    public static Result closePOSResult(Long storeId) {
        UserMerchant ownUser = checkUserMerchantAccessAuthorization();
        if (ownUser != null) {
            try {
                System.out.println("user merchant id >>> " + ownUser.id);
                Optional<CashierHistoryMerchant> cashierHistoryMerchant = Optional.empty();
                Store store = null;
                if (storeId != null && storeId != 0L) {
                    store = Store.findById(storeId);
                    if (store == null) {
                        response.setBaseResponse(0, 0, 0, "store tidak ditemukan", null);
                        return badRequest(Json.toJson(response));
                    }
                    cashierHistoryMerchant = CashierHistoryMerchantRepository.findLastSessionCashier(ownUser.id, storeId);
                }
                if (!cashierHistoryMerchant.isPresent()) {
                    response.setBaseResponse(0, 0, 0, "session cashier tidak ditemukan", null);
                    return badRequest(Json.toJson(response));
                }
                BigDecimal closingSystem = new BigDecimal(
                        cashierHistoryMerchant.get().getEndTotalAmount() != null ? cashierHistoryMerchant.get().getEndTotalAmount().toString() : "0");
                BigDecimal closingCashier = new BigDecimal(cashierHistoryMerchant.get().getEndTotalAmountCash() != null ?
                        cashierHistoryMerchant.get().getEndTotalAmountCash().toString() : "0");
                CashierReportResponse cashierReportResponse = new CashierReportResponse();
                cashierReportResponse.setId(cashierHistoryMerchant.get().id);
                cashierReportResponse.setCashierName(cashierHistoryMerchant.get().getUserMerchant().getFullName());
                if(cashierHistoryMerchant.get().getStore() != null && !cashierHistoryMerchant.get().getStore().storeName.isEmpty()){
                    cashierReportResponse.setStoreName(cashierHistoryMerchant.get().getStore().storeName);
                } else if(cashierHistoryMerchant.get().getStore() != null && cashierHistoryMerchant.get().getStore().id != null){
                    store = Store.findById(cashierHistoryMerchant.get().getStore().id);
                    cashierReportResponse.setStoreName(store.storeName);
                } else {
                    cashierReportResponse.setStoreName(store.storeName);
                }
                cashierReportResponse.setStartTime(cashierHistoryMerchant.get().getStartTime());
                cashierReportResponse.setEndTime(cashierHistoryMerchant.get().getEndTime());
                cashierReportResponse.setSessionCode(cashierHistoryMerchant.get().getSessionCode());
                cashierReportResponse.setInitialCash(Helper.convertCurrencyIDR(cashierHistoryMerchant.get().getStartTotalAmount()));
                cashierReportResponse.setClosingCashSystem(Helper.convertCurrencyIDR(closingSystem));
                cashierReportResponse.setClosingCashCashier(Helper.convertCurrencyIDR(closingCashier));
                cashierReportResponse.setMarginCash(Helper.convertCurrencyIDR(closingSystem.subtract(closingCashier)));
                cashierReportResponse.setNotes(cashierHistoryMerchant.get().getNotes());

                response.setBaseResponse(1, offset, limit, success + " menampilkan data penutupan kasir", cashierReportResponse);
                return ok(Json.toJson(response));
            } catch (Exception ex) {
                ex.printStackTrace();
                response.setBaseResponse(0, 0, 0, ex.getMessage(), null);
                return internalServerError(Json.toJson(response));
            }
        } else {
            response.setBaseResponse(0, 0, 0, unauthorized, null);
            return unauthorized(Json.toJson(response));
        }
    }
    public static Result printClosePOSResult(Long storeId, String sessionCode) {
        UserMerchant ownUser = checkUserMerchantAccessAuthorization();
        if (ownUser != null) {
            try {
                System.out.println("user merchant id >>> " + ownUser.id);
                Optional<CashierHistoryMerchant> cashierHistoryMerchant = Optional.empty();
                Store store = null;
                if (storeId != null && storeId != 0L) {
                    store = Store.findById(storeId);
                    if (store == null) {
                        response.setBaseResponse(0, 0, 0, "store tidak ditemukan", null);
                        return badRequest(Json.toJson(response));
                    }
                    cashierHistoryMerchant = CashierHistoryMerchantRepository.findLastSessionCashier(ownUser.id, storeId);
                } else if(sessionCode != null && !sessionCode.isEmpty()) {
                    cashierHistoryMerchant = CashierHistoryMerchantRepository.findBySessionCode(sessionCode);
                }
                if (!cashierHistoryMerchant.isPresent()) {
                    response.setBaseResponse(0, 0, 0, "session cashier tidak ditemukan", null);
                    return badRequest(Json.toJson(response));
                }
                CashierClosePrintResponse cashierClosePrintResponse = new CashierClosePrintResponse();

                AppSettings appSettings = AppSettingRepository.findByMerchantId(cashierHistoryMerchant.get().store.getMerchant().id);
                if (appSettings == null) {
                    String sandboxImage = Constant.getInstance().getImageUrl()
                            .concat("/assets/images/logo-sandbox.png");
                    cashierClosePrintResponse.setImageStoreUrl(sandboxImage);
                }else {
                    cashierClosePrintResponse.setImageStoreUrl(appSettings.getAppLogo());
                }
                cashierClosePrintResponse.setStoreName(cashierHistoryMerchant.get().store.storeName);
                cashierClosePrintResponse.setStoreAddress(cashierHistoryMerchant.get().store.storeAddress);
                cashierClosePrintResponse.setStorePhoneNumber(cashierHistoryMerchant.get().store.storePhone);

                BigDecimal closingSystem = new BigDecimal(
                        cashierHistoryMerchant.get().getEndTotalAmount() != null ? cashierHistoryMerchant.get().getEndTotalAmount().toString() : "0");
                BigDecimal closingCashier = new BigDecimal(cashierHistoryMerchant.get().getEndTotalAmountCash() != null ?
                        cashierHistoryMerchant.get().getEndTotalAmountCash().toString() : "0");
                cashierClosePrintResponse.setId(cashierHistoryMerchant.get().id);
                cashierClosePrintResponse.setCashierName(cashierHistoryMerchant.get().getUserMerchant().getFullName());
                if(cashierHistoryMerchant.get().getStore() != null && !cashierHistoryMerchant.get().getStore().storeName.isEmpty()){
                    cashierClosePrintResponse.setStoreName(cashierHistoryMerchant.get().getStore().storeName);
                } else if(cashierHistoryMerchant.get().getStore() != null && cashierHistoryMerchant.get().getStore().id != null){
                    store = Store.findById(cashierHistoryMerchant.get().getStore().id);
                    cashierClosePrintResponse.setStoreName(store.storeName);
                } else if(store != null){
                    cashierClosePrintResponse.setStoreName(store.storeName);
                }
                cashierClosePrintResponse.setStartTime(cashierHistoryMerchant.get().getStartTime());
                cashierClosePrintResponse.setEndTime(cashierHistoryMerchant.get().getEndTime());
                cashierClosePrintResponse.setSessionCode(cashierHistoryMerchant.get().getSessionCode());
                cashierClosePrintResponse.setInitialCash(Helper.convertCurrencyIDR(cashierHistoryMerchant.get().getStartTotalAmount()));
                cashierClosePrintResponse.setClosingCashSystem(Helper.convertCurrencyIDR(closingSystem));
                cashierClosePrintResponse.setClosingCashCashier(Helper.convertCurrencyIDR(closingCashier));
                cashierClosePrintResponse.setMarginCash(Helper.convertCurrencyIDR(closingSystem.subtract(closingCashier)));
                cashierClosePrintResponse.setNotes(cashierHistoryMerchant.get().getNotes());

                response.setBaseResponse(1, offset, limit, success + " menampilkan data penutupan kasir", cashierClosePrintResponse);
                return ok(Json.toJson(response));
            } catch (Exception ex) {
                ex.printStackTrace();
                response.setBaseResponse(0, 0, 0, ex.getMessage(), null);
                return internalServerError(Json.toJson(response));
            }
        } else {
            response.setBaseResponse(0, 0, 0, unauthorized, null);
            return unauthorized(Json.toJson(response));
        }
    }
    public static Result closePOSReport(int offset, int limit, Long storeId, String sessionCode, String startDate, String endDate) {
        UserMerchant ownUser = checkUserMerchantAccessAuthorization();
        if (ownUser != null) {
            try {
                System.out.println("user merchant id >>> " + ownUser.id);
                Query<CashierHistoryMerchant> query = CashierHistoryMerchantRepository.findAllCashierReportByUserMerchantId(ownUser.id);
                List<CashierHistoryMerchant> cashierHistoryMerchant = new ArrayList<>();
                Store store = null;
                if (storeId != null && storeId != 0L) {
                    store = Store.findById(storeId);
                    if (store == null) {
                        response.setBaseResponse(0, 0, 0, "store tidak ditemukan", null);
                        return badRequest(Json.toJson(response));
                    }
                    query = CashierHistoryMerchantRepository.findAllCashierReportByUserMerchant(query, storeId, ownUser.id);
                }
                if(startDate != null && !startDate.isEmpty() && endDate != null && !endDate.isEmpty()) {
                    query = CashierHistoryMerchantRepository.findAllCashierReportByDate(query, startDate, endDate);
                }
                if(sessionCode != null && !sessionCode.isEmpty()) {
                    query =  query.where().ilike("sessionCode", "%" + sessionCode + "%").query();
                }
                if (query != null) {
                    cashierHistoryMerchant = CashierHistoryMerchantRepository.findAllCashierReport(query, offset, limit);
                }
                List<CashierReportResponse> cashierReportResponseList = new ArrayList<>();
                for (CashierHistoryMerchant cashierHistoryMerchant1 : cashierHistoryMerchant) {
                    CashierReportResponse cashierReportResponse = new CashierReportResponse();
                    BigDecimal closingSystem = new BigDecimal(
                            cashierHistoryMerchant1.getEndTotalAmount() != null ? cashierHistoryMerchant1.getEndTotalAmount().toString() : "0");
                    BigDecimal closingCashier = new BigDecimal(cashierHistoryMerchant1.getEndTotalAmountCash() != null ?
                            cashierHistoryMerchant1.getEndTotalAmountCash().toString() : "0");
                    cashierReportResponse.setId(cashierHistoryMerchant1.id);
                    cashierReportResponse.setCashierName(cashierHistoryMerchant1.getUserMerchant().getFullName());
                    if(cashierHistoryMerchant1.getStore() != null && !cashierHistoryMerchant1.getStore().storeName.isEmpty()){
                        cashierReportResponse.setStoreName(cashierHistoryMerchant1.getStore().storeName);
                    } else if(cashierHistoryMerchant1.getStore() != null && cashierHistoryMerchant1.getStore().id != null){
                        store = Store.findById(cashierHistoryMerchant1.getStore().id);
                        cashierReportResponse.setStoreName(store.storeName);
                    } else if(store != null){
                        cashierReportResponse.setStoreName(store.storeName);
                    }
                    cashierReportResponse.setStartTime(cashierHistoryMerchant1.getStartTime());
                    cashierReportResponse.setEndTime(cashierHistoryMerchant1.getEndTime());
                    cashierReportResponse.setSessionCode(cashierHistoryMerchant1.getSessionCode());
                    cashierReportResponse.setInitialCash(Helper.convertCurrencyIDR(cashierHistoryMerchant1.getStartTotalAmount()));
                    cashierReportResponse.setClosingCashSystem(Helper.convertCurrencyIDR(closingSystem));
                    cashierReportResponse.setClosingCashCashier(Helper.convertCurrencyIDR(closingCashier));
                    cashierReportResponse.setMarginCash(Helper.convertCurrencyIDR(closingSystem.subtract(closingCashier)));
                    cashierReportResponse.setNotes(cashierHistoryMerchant1.getNotes());
                    cashierReportResponseList.add(cashierReportResponse);
                }
                response.setBaseResponse(cashierReportResponseList.size(), offset, limit, success + " menampilkan data penutupan kasir", cashierReportResponseList);
                return ok(Json.toJson(response));
            } catch (Exception ex) {
                ex.printStackTrace();
                response.setBaseResponse(0, 0, 0, ex.getMessage(), null);
                return internalServerError(Json.toJson(response));
            }
        } else {
            response.setBaseResponse(0, 0, 0, unauthorized, null);
            return unauthorized(Json.toJson(response));
        }
    }

    public static Result downloadClosePOSReport(int offset, int limit, Long storeId, String sessionCode, String startDate, String endDate) {
        Merchant ownUser = checkMerchantAccessAuthorization();
        if (ownUser != null) {
            try {
                System.out.println("merchant id >>> " + ownUser);
                Query<CashierHistoryMerchant> query = CashierHistoryMerchantRepository.find.where().eq("store.merchant", ownUser).query();
                List<CashierHistoryMerchant> cashierHistoryMerchant = new ArrayList<>();
                Store store = null;
                if (storeId != null && storeId != 0L) {
                    store = Store.findById(storeId);
                    if (store == null) {
                        response.setBaseResponse(0, 0, 0, "store tidak ditemukan", null);
                        return badRequest(Json.toJson(response));
                    }
                    query = CashierHistoryMerchantRepository.findAllCashierReportByMerchantAndStore(query, storeId, ownUser);
                }
                if(startDate != null && !startDate.isEmpty() && endDate != null && !endDate.isEmpty()) {
                    query = CashierHistoryMerchantRepository.findAllCashierReportByDate(query, startDate, endDate);
                }
                if(sessionCode != null && !sessionCode.isEmpty()) {
                    query =  query.where().ilike("sessionCode", "%" + sessionCode + "%").query();
                }
                if (query != null) {
                    cashierHistoryMerchant = CashierHistoryMerchantRepository.findAllCashierReport(query, offset, limit);
                }
                List<CashierReportResponse> cashierReportResponseList = new ArrayList<>();
                for (CashierHistoryMerchant cashierHistoryMerchant1 : cashierHistoryMerchant) {
                    CashierReportResponse cashierReportResponse = new CashierReportResponse();
                    BigDecimal closingSystem = new BigDecimal(
                            cashierHistoryMerchant1.getEndTotalAmount() != null ? cashierHistoryMerchant1.getEndTotalAmount().toString() : "0");
                    BigDecimal closingCashier = new BigDecimal(cashierHistoryMerchant1.getEndTotalAmountCash() != null ?
                            cashierHistoryMerchant1.getEndTotalAmountCash().toString() : "0");
                    cashierReportResponse.setId(cashierHistoryMerchant1.id);
                    cashierReportResponse.setCashierName(cashierHistoryMerchant1.getUserMerchant().getFullName());
                    if(cashierHistoryMerchant1.getStore() != null && !cashierHistoryMerchant1.getStore().storeName.isEmpty()){
                        cashierReportResponse.setStoreName(cashierHistoryMerchant1.getStore().storeName);
                    } else if(cashierHistoryMerchant1.getStore() != null && cashierHistoryMerchant1.getStore().id != null){
                        store = Store.findById(cashierHistoryMerchant1.getStore().id);
                        cashierReportResponse.setStoreName(store.storeName);
                    } else if(store != null){
                        cashierReportResponse.setStoreName(store.storeName);
                    }
                    cashierReportResponse.setStartTime(cashierHistoryMerchant1.getStartTime());
                    cashierReportResponse.setEndTime(cashierHistoryMerchant1.getEndTime());
                    cashierReportResponse.setSessionCode(cashierHistoryMerchant1.getSessionCode());
                    cashierReportResponse.setInitialCash(Helper.convertCurrencyIDR(cashierHistoryMerchant1.getStartTotalAmount()));
                    cashierReportResponse.setClosingCashSystem(Helper.convertCurrencyIDR(closingSystem));
                    cashierReportResponse.setClosingCashCashier(Helper.convertCurrencyIDR(closingCashier));
                    cashierReportResponse.setMarginCash(Helper.convertCurrencyIDR(closingSystem.subtract(closingCashier)));
                    cashierReportResponse.setNotes(cashierHistoryMerchant1.getNotes());
                    cashierReportResponseList.add(cashierReportResponse);
                }
                if(cashierReportResponseList.isEmpty()){
                    response.setBaseResponse(0, offset, limit, success + " menampilkan data penutupan kasir", cashierReportResponseList);
                    return ok(Json.toJson(response));
                }
                File downloadCashierReport = DownloadCashierReport.downloadCashierClosingReport(cashierReportResponseList);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyyyy");
                String filename = "Closing_"+simpleDateFormat.format(new Date()).toString()+".xlsx";
                assert downloadCashierReport != null;
                response().setContentType("application/vnd.ms-excel");
                response().setHeader("Content-disposition", "attachment; filename=" + filename);
                return ok(downloadCashierReport);
            } catch (Exception ex) {
                ex.printStackTrace();
                response.setBaseResponse(0, 0, 0, error, null);
                return internalServerError(Json.toJson(response));
            }
        } else {
            response.setBaseResponse(0, 0, 0, unauthorized, null);
            return unauthorized(Json.toJson(response));
        }
    }

    public static Result getSessionCashier() {
        UserMerchant userMerchant = checkUserMerchantAccessAuthorization();
        if (userMerchant == null) {
            response.setBaseResponse(0, 0, 0, unauthorized, null);
            return unauthorized(Json.toJson(response));
        } else {
            try {

                Optional<CashierHistoryMerchant> cashierHistoryMerchant = CashierHistoryMerchantRepository.findByUserActiveCashierAndOpen(userMerchant.id);
                if (!cashierHistoryMerchant.isPresent()) {
                    Map<String, Boolean> responseMap = new HashMap<>();
                    responseMap.put("is_open", Boolean.FALSE);
                    response.setBaseResponse(0, 0, 0, "cashier history tidak ditemukan", responseMap);
                    return ok(Json.toJson(response));
                }

                SessionCashierResponse sessionCashierResponse = new SessionCashierResponse();
                CashierHistoryMerchant cHM = CashierHistoryMerchant.find.where().eq("userMerchant", userMerchant).eq("end_time", null).eq("t0.end_total_amount", null).setMaxRows(1).findUnique();
                if (cHM != null) {
                    // StoreAccessDetail storeAccessDetail = StoreAccessDetail.find.where().eq("storeAccess", storeAccess).eq("t0.store_id", storeId).setMaxRows(1).findUnique();
                    sessionCashierResponse.setStoreId(cHM.getStore().id);
                }


                sessionCashierResponse.setIsOpen(Boolean.TRUE);
                sessionCashierResponse.setStartTotalAmount(cashierHistoryMerchant.get().getStartTotalAmount());
                sessionCashierResponse.setEndTotalAmount(cashierHistoryMerchant.get().getEndTotalAmount());

                response.setBaseResponse(1, 0, 0, success, sessionCashierResponse);
                return ok(Json.toJson(response));
            } catch (Exception e) {
                e.printStackTrace();
                response.setBaseResponse(0, 0, 0, error, null);
                return internalServerError(Json.toJson(response));
            }
        }
    }

    public static Result getAmountCashier(Long storeId) {
        UserMerchant userMerchant = checkUserMerchantAccessAuthorization();
        if (userMerchant == null) {
            response.setBaseResponse(0, 0, 0, unauthorized, null);
            return unauthorized(Json.toJson(response));
        } else {
            try {

                AmountCashierResponse amountCashierResponse = new AmountCashierResponse();

                Store store = Store.findById(storeId);
                if (store == null) {
                    amountCashierResponse.setDate(new Date());
                    amountCashierResponse.setTotalAmountOpeningPos(BigDecimal.ZERO);
                    amountCashierResponse.setTotalAmountBySystem(BigDecimal.ZERO);
                    response.setBaseResponse(0, 0, 0, "store tidak ditemukan", amountCashierResponse);
                    return ok(Json.toJson(response));
                }

                Optional<CashierHistoryMerchant> cashierHistoryMerchant = CashierHistoryMerchantRepository.findByUserActiveCashierAndStoreIdOpen(userMerchant.id, storeId);
                if (!cashierHistoryMerchant.isPresent()) {
                    amountCashierResponse.setDate(new Date());
                    amountCashierResponse.setTotalAmountOpeningPos(BigDecimal.ZERO);
                    amountCashierResponse.setTotalAmountBySystem(BigDecimal.ZERO);
                    response.setBaseResponse(0, 0, 0, "cashier history tidak ditemukan", amountCashierResponse);
                    return ok(Json.toJson(response));
                }

                BigDecimal totalAmountClosingBySystem = BigDecimal.ZERO;
                Query<Order> orderQuery = OrderRepository.findAllOrderByUserMerchantIdAndStoreId(userMerchant.id, storeId);
                List<Order> orders = OrderRepository.findOrdersByRangeToday(orderQuery, cashierHistoryMerchant.get().getStartTime(), new Date());
                BigDecimal totalAmountFromOrder = BigDecimal.ZERO;
                for (Order order : orders) {
                    Optional<OrderPayment> orderPayment = OrderPaymentRepository.findByOrderIdAndStatus(order.id, OrderPayment.PAID);
                    if (orderPayment.isPresent()) {
                        totalAmountFromOrder = totalAmountFromOrder.add(orderPayment.get().getTotalAmount());
                    }
                    continue;
                }

                totalAmountClosingBySystem = cashierHistoryMerchant.get().getStartTotalAmount().add(totalAmountFromOrder);

                amountCashierResponse.setDate(cashierHistoryMerchant.get().getStartTime());
                amountCashierResponse.setTotalAmountOpeningPos(cashierHistoryMerchant.get().getStartTotalAmount());
                amountCashierResponse.setTotalAmountBySystem(totalAmountClosingBySystem);

                response.setBaseResponse(1, 0, 0, success, amountCashierResponse);
                return ok(Json.toJson(response));
            } catch (Exception ex) {
                ex.printStackTrace();
                response.setBaseResponse(0, 0, 0, error, null);
                return internalServerError(Json.toJson(response));
            }
        }
    }

    public static Result closePOSReportMerchant(int offset, int limit, Long storeId, String sessionCode, String startDate, String endDate) {
        Merchant ownUser = checkMerchantAccessAuthorization();
        if (ownUser != null) {
            try {
                if (startDate.compareTo(endDate) > 0) {
                        response.setBaseResponse(0, 0, 0, "tanggal awal tidak boleh melebihi tanggal akhir", null);
                        return badRequest(Json.toJson(response));
                }
                Query<CashierHistoryMerchant> query = CashierHistoryMerchantRepository.findAllCashierReportByMerchant(ownUser);
                List<CashierHistoryMerchant> cashierHistoryMerchant = new ArrayList<>();
                Store store = null;
                if (storeId != null && storeId != 0L) {
                    store = Store.findById(storeId);
                    if (store == null) {
                        response.setBaseResponse(0, 0, 0, "store tidak ditemukan", null);
                        return badRequest(Json.toJson(response));
                    }
                    query = CashierHistoryMerchantRepository.findAllCashierReportByMerchant(query, storeId, ownUser);
                }
                if(startDate != null && !startDate.isEmpty() && endDate != null && !endDate.isEmpty()) {
                    query = CashierHistoryMerchantRepository.findAllCashierReportByDate(query, startDate, endDate);
                }
                if(sessionCode != null && !sessionCode.isEmpty()) {
                    query =  query.where().ilike("sessionCode", "%" + sessionCode + "%").query();
                }
                if (query != null) {
                    cashierHistoryMerchant = CashierHistoryMerchantRepository.findAllCashierReport(query, offset, limit);
                }
                Integer totalData = CashierHistoryMerchantRepository.find.where().eq("store.merchant", ownUser).order("t0.id desc").findList().size();
                List<CashierReportResponse> cashierReportResponseList = new ArrayList<>();
                for (CashierHistoryMerchant cashierHistoryMerchant1 : cashierHistoryMerchant) {
                    CashierReportResponse cashierReportResponse = new CashierReportResponse();
                    BigDecimal closingSystem = new BigDecimal(
                            cashierHistoryMerchant1.getEndTotalAmount() != null ? cashierHistoryMerchant1.getEndTotalAmount().toString() : "0");
                    BigDecimal closingCashier = new BigDecimal(cashierHistoryMerchant1.getEndTotalAmountCash() != null ?
                            cashierHistoryMerchant1.getEndTotalAmountCash().toString() : "0");
                    cashierReportResponse.setId(cashierHistoryMerchant1.id);
                    cashierReportResponse.setCashierName(cashierHistoryMerchant1.getUserMerchant().getFullName());
                    if(cashierHistoryMerchant1.getStore() != null && cashierHistoryMerchant1.getStore().storeName != null){
                        cashierReportResponse.setStoreName(cashierHistoryMerchant1.getStore().storeName);
                    } else if(cashierHistoryMerchant1.getStore() != null && cashierHistoryMerchant1.getStore().id != null){
                        store = Store.findById(cashierHistoryMerchant1.getStore().id);
                        cashierReportResponse.setStoreName(store.storeName);
                    } else if(store != null){
                        cashierReportResponse.setStoreName(store.storeName);
                    }
                    cashierReportResponse.setStartTime(cashierHistoryMerchant1.getStartTime());
                    cashierReportResponse.setEndTime(cashierHistoryMerchant1.getEndTime());
                    cashierReportResponse.setSessionCode(cashierHistoryMerchant1.getSessionCode());
                    cashierReportResponse.setInitialCash(Helper.convertCurrencyIDR(cashierHistoryMerchant1.getStartTotalAmount()));
                    cashierReportResponse.setClosingCashSystem(Helper.convertCurrencyIDR(closingSystem));
                    cashierReportResponse.setClosingCashCashier(Helper.convertCurrencyIDR(closingCashier));
                    cashierReportResponse.setMarginCash(Helper.convertCurrencyIDR(closingSystem.subtract(closingCashier)));
                    cashierReportResponse.setNotes(cashierHistoryMerchant1.getNotes());
                    cashierReportResponseList.add(cashierReportResponse);
                }
                response.setBaseResponse(totalData, offset, limit, success + " menampilkan data penutupan kasir", cashierReportResponseList);
                return ok(Json.toJson(response));
            } catch (Exception ex) {
                ex.printStackTrace();
                response.setBaseResponse(0, 0, 0, ex.getMessage(), null);
                return internalServerError(Json.toJson(response));
            }
        } else {
            response.setBaseResponse(0, 0, 0, unauthorized, null);
            return unauthorized(Json.toJson(response));
        }
    }


}
