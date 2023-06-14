package repository;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Query;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;
import models.internal.DeviceType;
import models.transaction.*;
import models.*;
import play.db.ebean.Model;
import play.libs.Json;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class OrderRepository extends Model {

    public static Finder<Long, Order> find = new Finder<Long, Order>(Long.class, Order.class);
    public static Finder<Long, OrderDetail> findDetail = new Finder<Long, OrderDetail>(Long.class, OrderDetail.class);
    public static Finder<Long, OrderDetailAddOn> findDetailAddOn = new Finder<Long, OrderDetailAddOn>(Long.class,
            OrderDetailAddOn.class);
    public static Finder<Long, OrderPayment> findOrderPayment = new Finder<Long, OrderPayment>(Long.class,
            OrderPayment.class);

    public static Optional<Order> findById(Long id) {
        return Optional.ofNullable(find.where().eq("id", id).findUnique());
    }

    public static Optional<Order> findByOrderNumber(String orderNumber) {
        return Optional.ofNullable(find.where().eq("orderNumber", orderNumber).findUnique());
    }

    public static Optional<Order> findByOrderNumberandStatus(String status, String orderNumber) {
        return Optional.ofNullable(find.where().eq("orderNumber", orderNumber).eq("status", status).findUnique());
    }

    public static List<Order> findOrderDataByUser(Long userId) {
        Query<Order> query = find.where().eq("user_id", userId).order("t0.created_at desc");
        query = query.orderBy("t0.created_at desc");

        ExpressionList<Order> exp = query.where();
        // exp = exp.disjunction();
        // exp = exp.ilike("t0.updated_by", "%" + filter + "%");
        query = exp.query();
        return query.findPagingList(0).getPage(0).getList();
    }

    public static List<Order> findOrdersQueue(Query<Order> reqQuery, int offset, int limit) {
        Query<Order> query = reqQuery;
        query = query.orderBy("t0.order_queue asc");

        ExpressionList<Order> exp = query.where();
        query = exp.query();
        exp.raw("t0.status in ('NEW_ORDER', 'PROCESS', 'READY_TO_PICKUP', 'DELIVERY')");
        if (limit != 0) {
            query = query.setMaxRows(limit);
        }
        return query.findPagingList(limit).getPage(offset).getList();
    }

    public static List<Order> findOrdersCustomer(Query<Order> reqQuery, int offset, int limit) {
        Query<Order> query = reqQuery;
        query = query.orderBy("t0.order_date desc");

        ExpressionList<Order> exp = query.where();
        query = exp.query();
        if (limit != 0) {
            query = query.setMaxRows(limit);
        }
        return query.findPagingList(limit).getPage(offset).getList();
    }

    public static List<Order> findOrdersCustomerTotal(Query<Order> reqQuery) {
        Query<Order> query = reqQuery;
        query = query.orderBy("t0.order_date desc");

        ExpressionList<Order> exp = query.where();
        query = exp.query();
        return query.findPagingList(0).getPage(0).getList();
    }

    public static List<Order> findOrdersByToday(Query<Order> reqQuery, Date today) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String todayString = simpleDateFormat.format(today);

        Query<Order> query = reqQuery;
        query = query.orderBy("t0.created_at desc");

        ExpressionList<Order> exp = query.where();
        exp.raw("t0.order_date between '" + todayString.concat(" 00:00:00.000") + "'" + " and " + "'" + todayString.concat(" 23:59:59.000") + "'");


        query = exp.query();
        query = query.setMaxRows(0);

        return query.findPagingList(0).getPage(0).getList();
    }

    public static List<Order> findOrdersByRangeToday(Query<Order> reqQuery, Date startDate, Date endDate) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.sss");
        String startDateString = simpleDateFormat.format(startDate);
        String endDateString = simpleDateFormat.format(endDate);

        Query<Order> query = reqQuery;
        query = query.orderBy("t0.created_at desc");

        ExpressionList<Order> exp = query.where();
        exp = exp.eq("t0.device_type", DeviceType.MINIPOS.getDevice());
        exp.raw("t0.order_date between '" + startDateString + "'" + " and " + "'" + endDateString + "'");

        query = exp.query();
        query = query.setMaxRows(0);

        return query.findPagingList(0).getPage(0).getList();
    }

    public static List<OrderDetail> findDataOrderDetail(Long orderId, String productType) {
        String queryRaw = "t0.product_id in (select pmid.product_merchant_id from product_merchant_detail pmid where pmid.product_type = '"
                + productType + "')";
        Query<OrderDetail> query = findDetail.where().raw(queryRaw).eq("t0.order_id", orderId)
                .order("t0.created_at desc");
        // query = query.orderBy("t0.created_at desc");

        ExpressionList<OrderDetail> exp = query.where();
        query = exp.query();
        return query.findPagingList(0).getPage(0).getList();
    }

    public static List<OrderDetail> findDataOrderProductAdditional(Long orderId, Long productId, String productType) {

        String queryRaw = "t0.product_id in (select pmd.product_merchant_id from product_merchant_detail pmd where pmd.product_type = '"
                + productType
                + "' AND pmd.product_merchant_id in (select pao.product_assign_id from product_add_on_merchant pao where pao.product_id = "
                + productId + " ORDER BY pao.id asc))";
        Query<OrderDetail> query = findDetail.where().raw(queryRaw).eq("t0.order_id", orderId)
                .order("t0.created_at desc");
        // query = query.orderBy("t0.created_at desc");

        ExpressionList<OrderDetail> exp = query.where();
        query = exp.query();
        return query.findPagingList(0).getPage(0).getList();
    }

    public static List<OrderDetailAddOn> findOrderDataProductAddOn(Long orderDetailId) {
        Query<OrderDetailAddOn> query = findDetailAddOn.where().eq("t0.order_detail_id", orderDetailId)
                .order("t0.created_at desc");
        // query = query.orderBy("t0.created_at desc");

        ExpressionList<OrderDetailAddOn> exp = query.where();
        query = exp.query();
        return query.findPagingList(0).getPage(0).getList();
    }

    public static OrderPayment findDataOrderPayment(Long orderId) {
        return findOrderPayment.where().eq("t0.order_id", orderId).findUnique();
    }

    public static Order checkStatusMerchant(String orderNumber) {
        return find.where().eq("order_number", orderNumber).findUnique();
    }

    // ==============================================================================================================================
    // //

    public static Query<Order> findAllOrderByStoreId(Long storeId) {
        return Ebean.find(Order.class)
                .fetch("store")
                .fetch("member")
                .where()
                .eq("store.id", storeId)
                .query();
    }

    public static Query<Order> findAllOrderByStoreIdNow(Long storeId) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String todayString = simpleDateFormat.format(new Date());
        return Ebean.find(Order.class)
                .fetch("store")
                .fetch("member")
                .where()
                .eq("store.id", storeId)
                .raw("t0.order_date between '" + todayString.concat(" 00:00:00.000") + "'" + " and " + "'" + todayString.concat(" 23:59:59.000") + "'")
                .query();
    }

    public static Query<Order> findAllOrderByUserMerchantIdAndStoreId(Long userMerchantId, Long storeId) {
        return Ebean.find(Order.class)
                .fetch("userMerchant")
                .fetch("store")
                .fetch("member")
                .where()
                .eq("userMerchant.id", userMerchantId)
                .eq("store.id", storeId)
                .query();
    }

    public static Query<Order> findAllOrderByMemberIdAndStoreId(Long memberId, Long storeId) {
        return Ebean.find(Order.class)
                .fetch("store")
                .fetch("member")
                .where()
                .eq("member.id", memberId)
                .eq("store.id", storeId)
                .query();
    }

    public static Query<Order> findAllOrderByMerchantId(Merchant merchant) {
        return Ebean.find(Order.class)
                .fetch("store")
                .fetch("store.merchant")
                .where()
                .eq("orderPayment.status", "PAID")
                .eq("store.merchant",
                        merchant)
                .query();
    }

    public static Query<Order> findAllOrderReportWithFilter(Merchant merchant, String startDate, String endDate) {
        if (startDate != null && startDate != "") {
            return Ebean.find(Order.class)
                    .fetch("store")
                    .fetch("store.merchant")
                    .where()
                    .raw("t0.order_date between '" + startDate + "' and '" + endDate + "'")
                    .eq("orderPayment.status", "PAID")
                    .eq("store.merchant",
                            merchant)
                    .query();
        } else {
            return Ebean.find(Order.class)
                    .fetch("store")
                    .fetch("store.merchant")
                    .where()
                    .eq("orderPayment.status", "PAID")
                    .eq("store.merchant",
                            merchant)
                    .query();
        }
    }

    public static Integer getTotalData(Query<Order> reqQuery, String statusOrder) {
        Query<Order> query = reqQuery;
        ExpressionList<Order> exp = query.where();

        if (!statusOrder.equalsIgnoreCase("") && !statusOrder.isEmpty()) {
            exp = exp.eq("t0.status", statusOrder);
        }
        query = exp.query();
        return query.findPagingList(0).getPage(0).getList().size();
    }

    public static List<Order> findAllOrderWithFilter(Query<Order> reqQuery, int offset, int limit, String status) {
        Query<Order> query = reqQuery;
        query.orderBy("t0.order_date desc");

        ExpressionList<Order> exp = query.where();

        if (!status.equalsIgnoreCase("")) {
            exp = exp.eq("t0.status", status);
        }

        query = exp.query();
        if (limit != 0) {
            query = query.setMaxRows(limit);
        }
        return query.findPagingList(limit).getPage(offset).getList();
    }

    public static List<Order> findAllOrderByStatusAndCustomerNameAndOrderNumber(Query<Order> reqQuery, int offset, int limit, String status, String customerName, String orderNumber) {
        Query<Order> query = reqQuery;
        query.orderBy("t0.order_date desc");

        ExpressionList<Order> exp = query.where();

        if (!status.equalsIgnoreCase("")) {
            exp = exp.eq("t0.status", status);
        }

        if (!orderNumber.equalsIgnoreCase("")) {
            exp = exp.ilike("t0.order_number", "%" + orderNumber + "%");
        }

        if (!customerName.equalsIgnoreCase("")) {
            exp = exp.ilike("t1.full_name", "%" + customerName + "%");
        }

        exp = exp.eq("t0.device_type", DeviceType.MINIPOS.getDevice());

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String todayString = simpleDateFormat.format(new Date());

        exp.raw("t0.order_date between '" + todayString.concat(" 00:00:00.000") + "'" + " and " + "'" + todayString.concat(" 23:59:59.000") + "'");

        query = exp.query();
        if (limit != 0) {
            query = query.setMaxRows(limit);
        }
        return query.findPagingList(limit).getPage(offset).getList();
    }

    public static List<OrderDetail> findOrderDetailByOrderId(Long orderId) {
        return findDetail.where().eq("order.id", orderId).findList();
    }

    public static List<Order> findOrderByUserMerchant(UserMerchant userMerchant){
        return find.where().eq("user_merchant", userMerchant).findList();
    }

    public static BigDecimal getTotalClosingCashier(Long userMerchantId, Date startDate, Date endDate, Long storeId) {
        Timestamp startTimestamp = new Timestamp(startDate.getTime());
        Timestamp endTimestamp = new Timestamp(endDate.getTime());
        List<Order> orderList = find.where()
                .raw("t0.store_id = "+storeId
                        +" and t0.user_merchant_id = " + userMerchantId
                        + " and t0.status = 'PAID' and t0.order_date between '" + startTimestamp + "' and '" + endTimestamp + "'")
                .findList();
        BigDecimal total = new BigDecimal(0);
        for(Order order : orderList){
            total = total.add(order.getTotalPrice());
        }
        return total;
    }

    public static Integer getTotalOrder(Query<Order> reqQuery, String statusOrder, Date startDate, Date endDate) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.sss");
        String startDateString = simpleDateFormat.format(startDate);
        String endDateString = simpleDateFormat.format(endDate);

        Query<Order> query = reqQuery;
        ExpressionList<Order> exp = query.where();

        if (!statusOrder.equalsIgnoreCase("") && !statusOrder.isEmpty()) {
            exp = exp.eq("t0.status", statusOrder);
        }

        exp.raw("t0.order_date between '" + startDateString + "'" + " and " + "'" + endDateString + "'");

        query = exp.query();
        return query.findPagingList(0).getPage(0).getList().size();
    }

    public static Integer getTotalOrder(Query<Order> reqQuery) {
        Query<Order> query = reqQuery;
        ExpressionList<Order> exp = query.where();

        query = exp.query();
        return query.findPagingList(0).getPage(0).getList().size();
    }

    public static List<OrderDetail> findOrderDetail(Long orderId, String productType) {
        String querySql;
        if (productType.equalsIgnoreCase("STORE")) {
            querySql = "SELECT od.id FROM order_detail od "
                + "WHERE od.order_id = " + orderId + " "
                + "AND od.product_id in (SELECT ps.product_id FROM product_store ps) ";
        } else if (productType.equalsIgnoreCase("GLOBAL")) {
            querySql = "SELECT od.id FROM order_detail od "
                + "LEFT JOIN product_store ps ON od.product_id = ps.product_id "
                + "WHERE od.order_id = " + orderId + " "
                + "AND ps.product_id IS NULL ";
        } else {
            querySql = "SELECT od.id FROM order_detail od "
                + "WHERE od.order_id = " + orderId + " ";
        }

        RawSql rawSql = RawSqlBuilder.parse(querySql).create();
        Query<OrderDetail> query = Ebean.find(OrderDetail.class).setRawSql(rawSql);
        return query.findList();
    }

    public static String checkProductType(String productType, String whereCondition) {
        String querySql;

        if (productType.equalsIgnoreCase("STORE")) {
            querySql = "SELECT ord.id, ord.order_number FROM orders ord "
                + "JOIN store str ON ord.store_id = str.id "
                + "JOIN order_payment op ON ord.id = op.order_id "
                + "JOIN order_detail od ON ord.id = od.order_id "
                + "LEFT JOIN member mbr ON ord.user_id = mbr.id "
                + whereCondition
                + "AND od.product_id in (SELECT ps.product_id FROM product_store ps) "
                + "GROUP BY ord.id, ord.order_number "
                + "ORDER BY ord.id DESC ";
        } else if (productType.equalsIgnoreCase("GLOBAL")) {
            querySql = "SELECT ord.id, ord.order_number FROM orders ord "
                + "JOIN store str ON ord.store_id = str.id "
                + "JOIN order_payment op ON ord.id = op.order_id "
                + "JOIN order_detail od ON ord.id = od.order_id "
                + "LEFT JOIN member mbr ON ord.user_id = mbr.id "
                + "LEFT JOIN product_store ps ON od.product_id = ps.product_id "
                + whereCondition
                + "AND ps.product_id IS NULL "
                + "GROUP BY ord.id, ord.order_number "
                + "ORDER BY ord.id DESC ";
        } else {
            querySql = "SELECT ord.id, ord.order_number FROM orders ord "
                + "JOIN store str ON ord.store_id = str.id "
                + "JOIN order_payment op ON ord.id = op.order_id "
                + "JOIN order_detail od ON ord.id = od.order_id "
                + "LEFT JOIN member mbr ON ord.user_id = mbr.id "
                + whereCondition
                + "GROUP BY ord.id, ord.order_number "
                + "ORDER BY ord.id DESC ";
        }

        return querySql;
    }

    public static Query<Order> queryGetOrderListWithFilter(Long merchantId, Long storeId, String statusOrder, String filter, String productType) {
        String whereCondition;

        // default query find by merchant id
        if (statusOrder.equalsIgnoreCase("CANCELED")) {
            whereCondition = "WHERE str.merchant_id = " + merchantId + " "
                + "AND (ord.status = '" + statusOrder + "' OR ord.status = 'CANCELLED') ";
        } else if (statusOrder.equalsIgnoreCase("PENDING")) {
            whereCondition = "WHERE (ord.status != 'CANCELLED' OR ord.status != 'CANCELED') "
                + "AND str.merchant_id = " + merchantId + " "
                + "AND op.status = '" + statusOrder + "' ";
        } else {
            whereCondition = "WHERE op.status = 'PAID' "
                + "AND str.merchant_id = " + merchantId + " "
                + "AND ord.status = '" + statusOrder + "' ";
        }

        // check store id --> mandatory
        if (storeId != null && storeId != 0L) {
            if (statusOrder.equalsIgnoreCase("CANCELED")) {
                whereCondition = "WHERE str.id = " + storeId + " "
                    + "AND ord.status = '" + statusOrder + "' ";
            } else if (statusOrder.equalsIgnoreCase("PENDING")) {
                whereCondition = "WHERE (ord.status != 'CANCELLED' OR ord.status != 'CANCELED') "
                    + "AND str.id = " + storeId + " "
                    + "AND op.status = '" + statusOrder + "' ";
            } else {
                whereCondition = "WHERE op.status = 'PAID' "
                    + "AND str.id = " + storeId + " "
                    + "AND ord.status = '" + statusOrder + "' ";
            }
        }

        String querySql = checkProductType(productType, whereCondition);

        RawSql rawSql = RawSqlBuilder.parse(querySql).create();
        Query<Order> query = Ebean.find(Order.class).setRawSql(rawSql);

        ExpressionList<Order> exp = query.where();
        exp = exp.disjunction();
        exp = exp.ilike("order_number", "%" + filter + "%");
        exp = exp.ilike("member_name", "%" + filter + "%");
        exp = exp.ilike("mbr.full_name", "%" + filter + "%");
        exp = exp.ilike("mbr.first_name", "%" + filter + "%");
        exp = exp.ilike("mbr.last_name", "%" + filter + "%");
        exp = exp.endJunction();
        query = exp.query();

        return query;
    }

    public static List<Order> getOrderListWithFilter(Long merchantId, Long storeId, int offset, int limit, String statusOrder, String filter, String productType) {
        Query<Order> query = queryGetOrderListWithFilter(merchantId, storeId, statusOrder, filter, productType);
        if (filter != "" && filter != null) {
            offset = 0;
        }
        return query.findPagingList(limit).getPage(offset).getList();
    }

    public static Integer getTotalOrderListWithFilter(Long merchantId, Long storeId, String statusOrder, String filter, String productType) {
        Query<Order> query = queryGetOrderListWithFilter(merchantId, storeId, statusOrder, filter, productType);
        return query.findList().size();
    }

    public static Query<Order> queryOrderReportMerchant(Long merchantId, Long storeId, String statusOrder, String productType, String startDate, String endDate) {
        String whereCondition;

        if (statusOrder.equalsIgnoreCase("CANCEL") || statusOrder.equalsIgnoreCase("CANCELED")) {
            statusOrder = "CANCELLED";
        }

        // default query find by merchant id
        if(statusOrder != null && !statusOrder.trim().isEmpty()){
            if(startDate != null && !startDate.trim().isEmpty()){
                whereCondition = "WHERE ord.order_date BETWEEN '" + startDate + "' AND '" + endDate + "' "
                    + "AND op.status != 'PENDING' "
                    + "AND str.merchant_id = " + merchantId + " "
                    + "AND ord.status = '" + statusOrder + "' ";
            } else {
                whereCondition = "WHERE op.status != 'PENDING' "
                    + "AND str.merchant_id = " + merchantId + " "
                    + "AND ord.status = '" + statusOrder + "' ";
            }
        } else {
            if(startDate != null && !startDate.trim().isEmpty()){
                whereCondition = "WHERE ord.order_date BETWEEN '" + startDate + "' AND '" + endDate + "' "
                    + "AND op.status != 'PENDING' "
                    + "AND str.merchant_id = " + merchantId + " ";
            } else {
                whereCondition = "WHERE op.status != 'PENDING' "
                    + "AND str.merchant_id = " + merchantId + " ";
            }

        }

        // check store id --> mandatory
        if (storeId != null && storeId != 0L) {
            if(statusOrder != null && !statusOrder.trim().isEmpty()){
                if(startDate != null && !startDate.trim().isEmpty()){
                    whereCondition = "WHERE ord.order_date BETWEEN '" + startDate + "' AND '" + endDate + "' "
                        + "AND op.status != 'PENDING' "
                        + "AND str.id = " + storeId + " "
                        + "AND ord.status = '" + statusOrder + "' ";
                } else {
                    whereCondition = "WHERE op.status != 'PENDING' "
                        + "AND str.id = " + storeId + " "
                        + "AND ord.status = '" + statusOrder + "' ";
                }
            } else {
                if(startDate != null && !startDate.trim().isEmpty()){
                    whereCondition = "WHERE ord.order_date BETWEEN '" + startDate + "' AND '" + endDate + "' "
                        + "AND op.status != 'PENDING' "
                        + "AND str.id = " + storeId + " ";
                } else {
                    whereCondition = "WHERE op.status != 'PENDING' "
                        + "AND str.id = " + storeId + " ";
                }
            }
        }

        String querySql = checkProductType(productType, whereCondition);

        RawSql rawSql = RawSqlBuilder.parse(querySql).create();
        return Ebean.find(Order.class).setRawSql(rawSql);
    }
    public static List<Order> getReportOrderMerchant(Long merchantId, Long storeId, int offset, int limit, String statusOrder, String productType, String startDate, String endDate) {
        Query<Order> query = queryOrderReportMerchant(merchantId, storeId, statusOrder, productType, startDate, endDate);
        return query.findPagingList(limit).getPage(offset).getList();
    }

    public static Integer getTotalOrderReportMerchant(Long merchantId, Long storeId, String statusOrder, String productType, String startDate, String endDate) {
        Query<Order> query = queryOrderReportMerchant(merchantId, storeId, statusOrder, productType, startDate, endDate);
        return query.findList().size();
    }




}
