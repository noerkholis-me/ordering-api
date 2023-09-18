package repository.finance;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Query;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;
import models.BaseModel;
import models.finance.FinanceTransaction;
import play.Logger;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class FinanceTransactionRepository extends BaseModel {

    public final static Logger.ALogger LOGGER = Logger.of(FinanceTransactionRepository.class);

    public static final Finder<Long, FinanceTransaction> find = new Finder<>(Long.class, FinanceTransaction.class);


    public static Integer getTotalPage(Query<FinanceTransaction> reqQuery) {
        Query<FinanceTransaction> query = reqQuery;
        ExpressionList<FinanceTransaction> exp = query.where();
        query = exp.query();
        return query.findPagingList(0).getPage(0).getList().size();
    }

    public static List<FinanceTransaction> getAllTransactions(Long merchantId, Long storeId, String startDate, String endDate, String status, String statusOrder, String sort, int offset, int limit) throws Exception {
        String condition;
        if (storeId != null && storeId != 0L) {
            condition = "WHERE st.id = " + storeId + " ";
        } else {
            condition = "WHERE mc.id = " + merchantId + " ";
        }

        String sorting;
        if (sort.equalsIgnoreCase("ASC")) {
            sorting = "ORDER BY ft.date ASC";
        } else {
            sorting = "ORDER BY ft.date DESC";
        }

        String transactionDate = "";
        if (!startDate.equalsIgnoreCase("") && !endDate.equalsIgnoreCase("")) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            Date start = simpleDateFormat.parse(startDate.concat(" 00:00:00.0"));
            Date end = simpleDateFormat.parse(endDate.concat(" 23:59:00.0"));

            Timestamp startTimestamp = new Timestamp(start.getTime());
            Timestamp endTimestamp = new Timestamp(end.getTime());

            transactionDate = "AND ft.date BETWEEN '" + startTimestamp + "' AND '" + endTimestamp + "' ";
        }

        String transactionStatus = "";
        if (!status.equalsIgnoreCase("")) {
            transactionStatus = "AND ft.status = '" + status + "' ";
        }

        String orderStatus = "";
        if (!statusOrder.equalsIgnoreCase("")) {
            orderStatus = "AND ord.status = '" + statusOrder + "' ";
        }

        String querySql = "SELECT ord.id FROM finance_transaction ft "
                + "JOIN store st ON ft.store_id = st.id "
                + "JOIN merchant mc ON st.merchant_id = mc.id "
                + "JOIN orders ord ON ft.reference_number = ord.order_number "
                + condition
                + orderStatus
                + transactionDate
                + transactionStatus
                + "GROUP BY ft.reference_number, ft.date, ord.id "
                + sorting;

        RawSql rawSql = RawSqlBuilder.parse(querySql).create();
        Query<FinanceTransaction> query = Ebean.find(FinanceTransaction.class).setRawSql(rawSql);

        return query.findPagingList(limit).getPage(offset).getList();
    }

    public static Query<FinanceTransaction> findAllTransactionByMerchantId(Long merchantId) {
        Query<FinanceTransaction> finance =
                Ebean.find(FinanceTransaction.class)
                        .fetch("store")
                        .fetch("store.merchant")
                        .where()
                        .eq("store.merchant.id", merchantId)
                        .query();
        return finance;
    }

    public static Query<FinanceTransaction> findAllTransactionByStoreId(Long storeId) {
        return Ebean.find(FinanceTransaction.class)
                .where().eq("store.id", storeId)
                .query();
    }

    public static List<FinanceTransaction> findAllTransactionByMerchantIdAndOrderClosed(Long merchantId) {
        String querySql = "SELECT ft.id FROM finance_transaction ft "
                + "LEFT JOIN orders ord ON ft.reference_number = ord.order_number "
                + "JOIN store st ON ft.store_id = st.id "
                + "WHERE st.merchant_id = " + merchantId + " AND (ord.status = 'CLOSED' AND ft.status = 'IN' OR ft.status != 'IN') "
                + "ORDER BY ft.date DESC";
        RawSql rawSql = RawSqlBuilder.parse(querySql).create();
        Query<FinanceTransaction> query =  Ebean.find(FinanceTransaction.class).setRawSql(rawSql);

        return query.findPagingList(0).getPage(0).getList();
    }

    public static List<FinanceTransaction> findAllTransactionByStoreIdAndOrderClosed(Long storeId) {
        String querySql = "SELECT ft.id FROM finance_transaction ft "
                + "LEFT JOIN orders ord ON ft.reference_number = ord.order_number "
                + "JOIN store st ON ft.store_id = st.id "
                + "WHERE st.id = " + storeId + " AND (ord.status = 'CLOSED' AND ft.status = 'IN' OR ft.status != 'IN') "
                + "ORDER BY ft.date DESC";
        RawSql rawSql = RawSqlBuilder.parse(querySql).create();
        Query<FinanceTransaction> query =  Ebean.find(FinanceTransaction.class).setRawSql(rawSql);

        return query.findPagingList(0).getPage(0).getList();
    }

    public static Integer getTotalTransaction(Long merchantId, String startDate, String endDate, Long storeId) throws Exception {
        Query<FinanceTransaction> finance = null;
        if (storeId != 0L && storeId != null){
            finance = Ebean.find(FinanceTransaction.class)
                .fetch("store")
                .fetch("store.merchant")
                .where()
                .eq("store.id", storeId)
                .eq("store.merchant.id", merchantId)
                .query();
        } else {
            finance = Ebean.find(FinanceTransaction.class)
                .fetch("store")
                .fetch("store.merchant")
                .where()
                .eq("store.merchant.id", merchantId)
                .query();
        }
        ExpressionList<FinanceTransaction> exp = finance.where();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date start = simpleDateFormat.parse(startDate.concat(" 00:00:00.0"));
        Date end = simpleDateFormat.parse(endDate.concat(" 23:59:00.0"));

        Timestamp startTimestamp = new Timestamp(start.getTime());
        Timestamp endTimestamp = new Timestamp(end.getTime());
        exp.between("t0.date", startTimestamp, endTimestamp);
        return finance.findList().size();
    }

    public static List<FinanceTransaction> findAllTransactionByDate(String startDate, String endDate) throws Exception {
        Query<FinanceTransaction> query = find.query();
        ExpressionList<FinanceTransaction> exp = query.where();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date start = simpleDateFormat.parse(startDate.concat(" 00:00:00.0"));
        Date end = simpleDateFormat.parse(endDate.concat(" 23:59:00.0"));

        Timestamp startTimestamp = new Timestamp(start.getTime());
        Timestamp endTimestamp = new Timestamp(end.getTime());
        exp.between("t0.date", startTimestamp, endTimestamp);
        return query.findList();
    }

    public static List<FinanceTransaction> findAllTransaction(Query<FinanceTransaction> reqQuery, String startDate, String endDate,
                                                                        String sort, int offset, int limit, String status) throws ParseException {
        Query<FinanceTransaction> query = reqQuery;

        if(!sort.equals("")) {
            query = query.orderBy(sort);
        } else {
            query = query.orderBy("t0.date desc");
        }

        ExpressionList<FinanceTransaction> exp = query.where();
        if (!startDate.equalsIgnoreCase("") && !endDate.equalsIgnoreCase("")) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            Date start = simpleDateFormat.parse(startDate.concat(" 00:00:00.0"));
            Date end = simpleDateFormat.parse(endDate.concat(" 23:59:00.0"));

            Timestamp startTimestamp = new Timestamp(start.getTime());
            Timestamp endTimestamp = new Timestamp(end.getTime());
            exp.between("t0.date", startTimestamp, endTimestamp);
        }
        if (!status.equalsIgnoreCase("")) {
            exp = exp.eq("t0.status", status);
        }
        query = exp.query();
        if (limit != 0) {
            query = query.setMaxRows(limit);
        }
        return query.findPagingList(limit).getPage(offset).getList();
    }

    public static List<FinanceTransaction> findListTransaction(Query<FinanceTransaction> reqQuery, String startDate, String endDate, String status) throws ParseException {
        Query<FinanceTransaction> query = reqQuery;

        ExpressionList<FinanceTransaction> exp = query.where();
        if (!startDate.equalsIgnoreCase("") && !endDate.equalsIgnoreCase("")) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            Date start = simpleDateFormat.parse(startDate.concat(" 00:00:00.0"));
            Date end = simpleDateFormat.parse(endDate.concat(" 23:59:00.0"));

            Timestamp startTimestamp = new Timestamp(start.getTime());
            Timestamp endTimestamp = new Timestamp(end.getTime());
            exp.between("t0.date", startTimestamp, endTimestamp);
        }
        if (!status.equalsIgnoreCase("")) {
            exp = exp.eq("t0.status", status);
        }
        query = exp.query();

        return query.findList();
    }

    public static List<FinanceTransaction> filteredActiveBalance(Long merchantId, Long storeId, String startDate, String endDate, String status, String statusOrder) throws Exception {
        String condition;
        if (storeId != null && storeId != 0L) {
            condition = "WHERE st.id = " + storeId + " ";
        } else {
            condition = "WHERE mc.id = " + merchantId + " ";
        }

        String transactionDate = "";
        if (!startDate.equalsIgnoreCase("") && !endDate.equalsIgnoreCase("")) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            Date start = simpleDateFormat.parse(startDate.concat(" 00:00:00.0"));
            Date end = simpleDateFormat.parse(endDate.concat(" 23:59:00.0"));

            Timestamp startTimestamp = new Timestamp(start.getTime());
            Timestamp endTimestamp = new Timestamp(end.getTime());

            transactionDate = "AND ft.date BETWEEN '" + startTimestamp + "' AND '" + endTimestamp + "' ";
        }

        String transactionStatus = "";
        if (!status.equalsIgnoreCase("")) {
            transactionStatus = "AND ft.status = '" + status + "' ";
        }

        String orderStatus = "";
        if (!statusOrder.equalsIgnoreCase("")) {
            orderStatus = "AND ord.status = '" + statusOrder + "' ";
        }

        String querySql = "SELECT ft.id FROM finance_transaction ft "
                + "JOIN store st ON ft.store_id = st.id "
                + "JOIN merchant mc ON st.merchant_id = mc.id "
                + "JOIN orders ord ON ft.reference_number = ord.order_number "
                + condition
                + orderStatus
                + transactionDate
                + transactionStatus
                + "ORDER BY ft.date DESC";

        RawSql rawSql = RawSqlBuilder.parse(querySql).create();
        Query<FinanceTransaction> query = Ebean.find(FinanceTransaction.class).setRawSql(rawSql);

        return query.findPagingList(0).getPage(0).getList();
    }

}
