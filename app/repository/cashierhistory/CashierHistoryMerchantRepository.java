package repository.cashierhistory;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Query;
import models.merchant.CashierHistoryMerchant;
import play.db.ebean.Model;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class CashierHistoryMerchantRepository extends Model {

    private static final Finder<Long, CashierHistoryMerchant> find = new Finder<>(Long.class, CashierHistoryMerchant.class);

    public static Optional<CashierHistoryMerchant> findByUserActiveCashier(Long userMerchantId, Long storeId) {
        return Optional.ofNullable(
            find.where()
                    .eq("isActive", true)
                    .eq("userMerchant.id", userMerchantId)
                    .eq("store.id", storeId)
                    .findUnique()
        );
    }

    public static Optional<CashierHistoryMerchant> findByUserActiveCashierAndOpen(Long userMerchantId) {
        return Optional.ofNullable(
                find.where()
                        .eq("isActive", true)
                        .eq("userMerchant.id", userMerchantId)
                        .isNull("endTime")
                        .setMaxRows(1)
                        .findUnique()
        );
    }

    public static Query<CashierHistoryMerchant> findAllCashierHistoryByStoreId(Long storeId) {
        return Ebean.find(CashierHistoryMerchant.class)
                .where().eq("store.id", storeId)
                .query();
    }

    public static Query<CashierHistoryMerchant> findAllCashierHistoryByMerchantId(Long merchantId) {
        return Ebean.find(CashierHistoryMerchant.class)
                .fetch("store")
                .fetch("store.merchant")
                .where()
                .eq("store.merchant.id", merchantId)
                .query();
    }

    public static Integer getTotalData(Query<CashierHistoryMerchant> reqQuery) {
        Query<CashierHistoryMerchant> query = reqQuery;
        ExpressionList<CashierHistoryMerchant> exp = query.where();
        query = exp.query();
        return query.findPagingList(0).getPage(0).getList().size();
    }

    public static List<CashierHistoryMerchant> findAllCashierHistory(Query<CashierHistoryMerchant> reqQuery, int offset, int limit) {
        Query<CashierHistoryMerchant> query = reqQuery;
        query.orderBy("t0.created_at desc");

        ExpressionList<CashierHistoryMerchant> exp = query.where();

        query = exp.query();
        if (limit != 0) {
            query = query.setMaxRows(limit);
        }

        return query.findPagingList(limit).getPage(offset).getList();
    }

    public static Optional<CashierHistoryMerchant> findLastSessionCashier(Long userMerchantId, Long storeId) {
        return Optional.ofNullable(
                find.where()
                        .eq("isActive", true)
                        .eq("userMerchant.id", userMerchantId)
                        .eq("store.id", storeId)
                        .orderBy("t0.updated_at desc")
                        .findList().get(0)
        );
    }
    public static Optional<CashierHistoryMerchant> findBySessionCode(Long userMerchantId, Long storeId, String sessionCode) {
        return Optional.ofNullable(
                find.where()
                        .eq("isActive", true)
                        .eq("userMerchant.id", userMerchantId)
                        .eq("store.id", storeId)
                        .eq("sessionCode", sessionCode)
                        .findList().get(0)
        );
    }

    public static Query<CashierHistoryMerchant> findAllCashierReportByDate(String startDate, String endDate) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date start = simpleDateFormat.parse(startDate.concat(" 00:00:00.0"));
        Date end = simpleDateFormat.parse(endDate.concat(" 23:59:00.0"));
        Timestamp startTimestamp = new Timestamp(start.getTime());
        Timestamp endTimestamp = new Timestamp(end.getTime());
        return Ebean.find(CashierHistoryMerchant.class)
                .where()
                .between("start_time", startTimestamp, endTimestamp)
                .query();
    }
    public static Query<CashierHistoryMerchant> findAllCashierReportByUserMerchant(Long storeId, Long userMerchantId) {
        return Ebean.find(CashierHistoryMerchant.class)
                .where().eq("store.id", storeId)
                .eq("userMerchant.id", userMerchantId)
                .query();
    }
    public static List<CashierHistoryMerchant> findAllCashierReport(Query<CashierHistoryMerchant> reqQuery, int offset, int limit) {
        Query<CashierHistoryMerchant> query = reqQuery;
        query.orderBy("t0.created_at desc");

        ExpressionList<CashierHistoryMerchant> exp = query.where();

        query = exp.query();
        if (limit != 0) {
            query = query.setMaxRows(limit);
        }

        return query.findPagingList(limit).getPage(offset).getList();
    }

}
