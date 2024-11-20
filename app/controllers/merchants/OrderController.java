package controllers.merchants;

import com.avaje.ebean.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hokeba.api.BaseResponse;
import com.hokeba.mapping.response.*;
import com.hokeba.shipping.rajaongkir.RajaOngkirService;
import com.hokeba.shipping.rajaongkir.mapping.ReqMapWaybill;
import com.hokeba.shipping.rajaongkir.mapping.ResMapWaybill;
import com.hokeba.social.requests.FirebaseNotificationHelper;
import com.hokeba.util.CommonFunction;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import controllers.BaseController;
import models.*;
import models.mapper.ReturnMerchant;
import models.mapper.TopSalesReportMerchant;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by hendriksaragih on 4/8/17.
 */
@Api(value = "/merchants/order", description = "Orders")
public class OrderController extends BaseController {
    @SuppressWarnings("rawtypes")
    private static BaseResponse response = new BaseResponse();

    public static Result index() {
        return ok();
    }

    @ApiOperation(value = "Get all order list.", notes = "Returns list of order.\n" + swaggerInfo
            + "", response = SalesOrderSeller.class, responseContainer = "List", httpMethod = "GET")
    public static Result lists(String type, String filter, String sort, int offset, int limit) {
        Merchant actor = checkMerchantAccessAuthorization();
        if (actor != null) {
            String message;
            Query<SalesOrderSeller> query = SalesOrderSeller.find.where().eq("merchant", actor).eq("t0.is_deleted", false)
            		.ne("salesOrder.status", SalesOrder.ORDER_STATUS_CHECKOUT).orderBy("t0.id ASC");
            BaseResponse<SalesOrderSeller> responseIndex;
            try {
                responseIndex = SalesOrderSeller.getDataMerchant(query, type, sort, filter, offset, limit);
                return ok(Json.toJson(responseIndex));
            } catch (IOException e) {
                message = e.getMessage();
                Logger.error("allDetail", e);
            }
            response.setBaseResponse(0, 0, 0, error, message);
            return internalServerError(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result update(Long id) {
        Merchant actor = checkMerchantAccessAuthorization();
        FirebaseNotificationHelper notificationHelper = new FirebaseNotificationHelper();
        if (actor != null) {
            JsonNode json = request().body().asJson();
            Integer status = json.findPath("status").asInt();
            String trackingNumber = json.has("tracking_number") ? json.findPath("tracking_number").asText("") : "";
            SalesOrderSeller data = SalesOrderSeller.find.where().eq("merchant_id", actor.id).eq("is_deleted", false).eq("id", id).setMaxRows(1).findUnique();
            if (data != null){
                Transaction txn = Ebean.beginTransaction();
                try {
                    if (status == 1 && data.status.equals(SalesOrder.ORDER_STATUS_VERIFY)){
                        data.status = SalesOrder.ORDER_STATUS_PACKING;
                        data.update();

                        NotificationMember.insertNotif(data.salesOrder, NotificationMember.TYPE_SHIPPING);
                        
                        String title = "Pesanan sedang dikemas";
    					String message = "Tunggu sebentar ya, pesananmu sedang dikemas nih.";
    					ObjectNode ob = Json.newObject();
    					ob.put("data", data.status);
    	    			ObjectNode type = ob;
    	    			String topic = "order-"+actor.id;
    	    			String screenMobile = "/RecentrderPage";
                        notificationHelper.sendToTopic(title, message, type, topic,screenMobile);

                        SalesOrderSellerStatus sosStatus = new SalesOrderSellerStatus(data, new Date(), 4, "Orders are being processed in our warehouse.");
                        sosStatus.save();

                        data.salesOrderDetail.forEach(sod -> {
                            sod.status = data.status;
                            sod.update();
                        });
                    }else if (status == 2){
                        if (data.status.equals(SalesOrder.ORDER_STATUS_PACKING)){
                            SalesOrderSellerStatus sosStatus = new SalesOrderSellerStatus(data, new Date(), 5, "The orders was sent via "+data.courierName);
                            sosStatus.save();

                            NotificationMember.insertNotif(data.salesOrder, NotificationMember.TYPE_DELIVERED);
                            
                            String title = "Pesanan dikirim";
        					String message = "Siap-siap terima paket ya.. Pesananmu sudah dikirim!";
        					ObjectNode ob = Json.newObject();
        					ob.put("data", data.status);
        	    			ObjectNode type = ob;
        	    			String topic = "order-"+actor.id;
        	    			String screenMobile = "/RecentrderPage";
                            notificationHelper.sendToTopic(title, message, type, topic, screenMobile);

                            data.salesOrderDetail.forEach(sod -> {
                                sod.status = data.status;
                                sod.update();
                            });

                            data.status = SalesOrder.ORDER_STATUS_ON_DELIVERY;
                            data.sentDate = new Date();
//                            data.deliveredDate = data.shippingCostDetail.getDelivered();
                        }

                        data.trackingNumber = trackingNumber;
                        data.update();
//                    } else if (status == 3 & data.status.equals(SalesOrder.ORDER_STATUS_ON_DELIVERY)){
//                        data.status = SalesOrder.ORDER_STATUS_RECEIVE_BY_CUSTOMER;
//                        SalesOrderSellerStatus sosStatus = new SalesOrderSellerStatus(data, new Date(), 6, "Your orders has arrived at the destination. Thank you for shopping at Whizliz");
//                        sosStatus.save();
                    } else{
                        response.setBaseResponse(0, 0, 0, "Invalid status", null);
                        return badRequest(Json.toJson(response));
                    }

                    txn.commit();
                    response.setBaseResponse(1, 0, 1, success, null);
                    return ok(Json.toJson(response));
                }catch (Exception ex){
                	ex.printStackTrace();
                    txn.rollback();
                }finally {
                    txn.end();
                }
                response.setBaseResponse(0, 0, 0, error, null);
                return internalServerError(Json.toJson(response));
            }
            response.setBaseResponse(0, 0, 0, notFound, null);
            return notFound(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result detail(Long id) {
        Merchant actor = checkMerchantAccessAuthorization();
        if (actor != null) {
            SalesOrderSeller data = SalesOrderSeller.find.where().eq("merchant_id", actor.id).eq("is_deleted", false).eq("id", id).setMaxRows(1).findUnique();
            if (data != null){
                ObjectMapper mapper = new ObjectMapper();
                MapOrderMerchantList result = mapper.convertValue(data, MapOrderMerchantList.class);
                result.setVoucherInfo(data.fetchVoucherInfo());
                response.setBaseResponse(1, offset, 1, success, result);
                return ok(Json.toJson(response));
            }
            response.setBaseResponse(0, 0, 0, notFound, null);
            return forbidden(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result report(String period, String period2, int offset, int limit) {
        Merchant actor = checkMerchantAccessAuthorization();
        if (actor != null) {
            Query<SalesOrderSeller> qry = SalesOrderSeller.find.orderBy("id DESC");
            ExpressionList<SalesOrderSeller> exp = qry.where().conjunction();
            exp.eq("merchant_id", actor.id).eq("is_deleted", false)
                    .not(Expr.in("status", SalesOrder.PAYMENT_CANCEL))
                    .not(Expr.eq("status", SalesOrder.ORDER_STATUS_CHECKOUT));
            if (!period.isEmpty()){
                exp.ge("orderDate", CommonFunction.getDateTimeStartFromDate(period));
            }
            if (!period2.isEmpty()){
                exp.le("orderDate", CommonFunction.getDateTimeEndFromDate(period2));
            }
            exp = exp.endJunction();
            qry = exp.query();
            int total = qry.findList().size();
            if (limit != 0) {
                qry = qry.setMaxRows(limit);
            }
            List<SalesOrderSeller> datas = qry.findPagingList(limit).getPage(offset).getList();
            ObjectMapper mapper = new ObjectMapper();
            response.setBaseResponse(total, offset, limit, success, mapper.convertValue(datas, MapOrderReport[].class));
            return ok(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result payment(String period, String period2, int offset, int limit) {
        Merchant actor = checkMerchantAccessAuthorization();
        if (actor != null) {
            Query<SalesOrderSeller> qry = SalesOrderSeller.find.orderBy("id DESC");
            ExpressionList<SalesOrderSeller> exp = qry.where().conjunction();
            exp.eq("merchant_id", actor.id)
                    .eq("is_deleted", false)
            .not(Expr.in("status", SalesOrder.PAYMENT_CANCEL));
            if (!period.isEmpty()){
                exp.ge("orderDate", CommonFunction.getDateTimeStartFromDate(period));
            }
            if (!period2.isEmpty()){
                exp.le("orderDate", CommonFunction.getDateTimeEndFromDate(period2));
            }
            exp = exp.endJunction();
            qry = exp.query();
            int total = qry.findList().size();
            if (limit != 0) {
                qry = qry.setMaxRows(limit);
            }

            List<MapMerchantPayment> lists = new ArrayList<>();
            qry.findPagingList(limit).getPage(offset).getList().forEach(sos->{
                lists.add(new MapMerchantPayment(sos.orderNumber, sos.getDate(), sos.getPaymentStatus(),
                        sos.paymentSeller, sos.getReturAmount(),
                        sos.shipping, sos.getSellPrice(), sos.getRealPrice(), sos.getCommision(), sos.getReceivePayment()));
            });

            MapMerchantInfo result = new MapMerchantInfo(actor.getUnpaidCustomer(), actor.getPaidHokeba(), actor.getUnpaidHokeba(), lists);
            response.setBaseResponse(total, offset, limit, success, result);
            return ok(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result topSales(Long category, Long brand, String period, String period2, int offset, int limit) {
        Merchant actor = checkMerchantAccessAuthorization();
        if (actor != null) {
            Query<TopSalesReportMerchant> qry = generateSqlQueryTopSales(actor.id, category, brand, period, period2);
            int total = qry.findList().size();
            if (limit != 0) {
                qry = qry.setMaxRows(limit);
            }
            List<TopSalesReportMerchant> datas = qry.orderBy("qty DESC").findPagingList(limit).getPage(offset).getList();
            ObjectMapper mapper = new ObjectMapper();
            response.setBaseResponse(total, offset, limit, success, mapper.convertValue(datas, MapTopSalesReport[].class));
            return ok(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    private static Query<TopSalesReportMerchant> generateSqlQueryTopSales(Long merchant, Long category, Long brand, String period, String period2){
        String sql = "SELECT p.id, p.sku, p.name as productName, pc.name as category, b.name as brand, " +
                "sum(so.quantity) as qty, p.num_of_order as sold " +
                "FROM sales_order_detail so " +
                "LEFT JOIN sales_order_seller sos ON so.sales_order_seller_id = sos.id " +
                "LEFT JOIN product p ON so.product_id=p.id " +
                "LEFT JOIN category pc ON p.category_id=pc.id " +
                "LEFT JOIN brand b ON p.brand_id=b.id " +
                "WHERE sos.merchant_id = " + merchant + " AND sos.status NOT IN ('WC', 'EX', 'CA', 'CC', 'CH') ";
        if (category != 0L){
            sql += "AND pc.id = " + category + " ";
        }
        if (brand != 0L){
            sql += "AND b.id = " + brand + " ";
        }
        if (!period.isEmpty()){
            sql += "AND sos.order_date >= '" + period + " 00:00:00' ";
        }
        if (!period2.isEmpty()){
            sql += "AND sos.order_date <= '" + period2 + " 23:59:59' ";
        }

        sql += "GROUP BY p.id, p.sku, p.name, pc.name, b.name, p.num_of_order, pc.id, b.id ";

        RawSql rawSql = RawSqlBuilder.parse(sql).create();
        Query<TopSalesReportMerchant> query = Ebean.find(TopSalesReportMerchant.class);
        query.setRawSql(rawSql);

        return query;
    }

    @ApiOperation(value = "Get all retur list.", notes = "Returns list of retur.\n" + swaggerInfo
            + "", response = SalesOrderSeller.class, responseContainer = "List", httpMethod = "GET")
    public static Result retur(String type, String filter, String sort, int offset, int limit) {
        Merchant actor = checkMerchantAccessAuthorization();
        if (actor != null) {
            String message;
            Query<ReturnMerchant> query = SalesOrderReturnDetail.queryRetur();
            BaseResponse<ReturnMerchant> responseIndex;
            try {
                responseIndex = SalesOrderReturnDetail.getDataMerchant(query, type, sort, filter, offset, limit);
                return ok(Json.toJson(responseIndex));
            } catch (IOException e) {
                message = e.getMessage();
                Logger.error("allDetail", e);
            }
            response.setBaseResponse(0, 0, 0, error, message);
            return internalServerError(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    @ApiOperation(value = "Get all retur list.", notes = "Returns list of retur.\n" + swaggerInfo
            + "", response = SalesOrderSeller.class, responseContainer = "List", httpMethod = "GET")
    public static Result returV2(String type, String filter, String sort, int offset, int limit) {
        Merchant actor = checkMerchantAccessAuthorization();
        if (actor != null) {
            String message;
            Query<SalesOrderReturn> query = SalesOrderReturn.find.where()
                    .eq("salesOrderSeller.merchant", actor)
                    .eq("t0.is_deleted", false).order("t0.id DESC");
            BaseResponse<SalesOrderReturn> responseIndex;
            try {
                responseIndex = SalesOrderReturn.getDataMerchant(query, type, sort, filter, offset, limit);
                return ok(Json.toJson(responseIndex));
            } catch (IOException e) {
                message = e.getMessage();
                Logger.error("allDetail", e);
            }
            response.setBaseResponse(0, 0, 0, error, message);
            return internalServerError(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    //odoo
    public static Result updateRetur(Long id) {
        Merchant actor = checkMerchantAccessAuthorization();
        if (actor != null) {
            JsonNode json = request().body().asJson();
            String trackingNumber = json.has("tracking_number") ? json.findPath("tracking_number").asText("") : "";
            SalesOrderReturn data = SalesOrderReturn.find.where()
                    .eq("salesOrderSeller.merchant", actor)
                    .eq("t0.is_deleted", false)
                    .eq("t0.type", SalesOrderReturn.TYPE_REPLACED)
                    .eq("t0.id", id).setMaxRows(1).findUnique();
            if (data != null){
                Transaction txn = Ebean.beginTransaction();
                try {
                    if (data.status.equals(SalesOrderReturn.STATUS_APPROVED)){
                        data.status = SalesOrderReturn.STATUS_ONPROGRESS;
                        data.documentNumber = trackingNumber;
                        data.scheduleAt = new Date();
                        data.shippedBy = "[Merchant] "+actor.name;
                        data.update();
                        txn.commit();
                        //odoo
//                        OdooService.getInstance().updateReturOnDelivery(data);
                    }else if (data.status.equals(SalesOrderReturn.STATUS_ONPROGRESS)){
                        data.documentNumber = trackingNumber;
                        data.update();
                        txn.commit();
                    }else{
                        response.setBaseResponse(0, 0, 0, notFound, null);
                        return forbidden(Json.toJson(response));
                    }

                    response.setBaseResponse(1, 0, 1, success, null);
                    return ok(Json.toJson(response));
                }catch (Exception ex){
                    txn.rollback();
                }finally {
                    txn.end();
                }

            }
            response.setBaseResponse(0, 0, 0, notFound, null);
            return forbidden(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result trackOrder() {
        Merchant actor = checkMerchantAccessAuthorization();
        if (actor != null) {
            JsonNode json = request().body().asJson();
            ObjectMapper mapper = new ObjectMapper();
            try {
            	ReqMapWaybill request = mapper.readValue(json.toString(), ReqMapWaybill.class);
            	ResMapWaybill result = RajaOngkirService.getInstance().shipmentTracking(request);
                response.setBaseResponse(1, offset, 1, success, result);
                return ok(Json.toJson(response));
            } catch (IOException e) {
                e.printStackTrace();
            }

            response.setBaseResponse(0, 0, 0, inputParameter, null);
            return badRequest(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }
}
