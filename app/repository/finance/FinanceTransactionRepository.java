package repository.finance;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.FetchConfig;
import com.avaje.ebean.Query;
import controllers.finance.FinanceTransactionController;
import models.BaseModel;
import models.Merchant;
import models.Store;
import models.finance.FinanceTransaction;
import play.Logger;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class FinanceTransactionRepository extends BaseModel {

    private final static Logger.ALogger LOGGER = Logger.of(FinanceTransactionRepository.class);

    private static final Finder<Long, FinanceTransaction> find = new Finder<>(Long.class, FinanceTransaction.class);


    public static Integer getTotalPage(Query<FinanceTransaction> reqQuery) {
        Query<FinanceTransaction> query = reqQuery;
        ExpressionList<FinanceTransaction> exp = query.where();
        query = exp.query();
        return query.findPagingList(0).getPage(0).getList().size();
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

    public static Integer getTotalTransaction(Long merchantId, String startDate, String endDate) throws Exception {
        Query<FinanceTransaction> finance =
                Ebean.find(FinanceTransaction.class)
                        .fetch("store")
                        .fetch("store.merchant")
                        .where()
                        .eq("store.merchant.id", merchantId)
                        .query();
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

}
