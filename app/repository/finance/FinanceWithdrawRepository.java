package repository.finance;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Query;
import models.BaseModel;
import models.finance.FinanceTransaction;
import models.finance.FinanceWithdraw;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class FinanceWithdrawRepository extends BaseModel {

    private static final Finder<Long, FinanceWithdraw> find = new Finder<>(Long.class, FinanceWithdraw.class);

    public static Integer getTotalPage(Query<FinanceWithdraw> reqQuery) {
        Query<FinanceWithdraw> query = reqQuery;
        ExpressionList<FinanceWithdraw> exp = query.where();
        query = exp.query();
        return query.findPagingList(0).getPage(0).getList().size();
    }

    public static Query<FinanceWithdraw> finaAllWithdrawByMerchantId(Long merchantId) {
        return Ebean.find(FinanceWithdraw.class)
                .fetch("store")
                .fetch("store.merchant")
                .where()
                .eq("store.merchant.id", merchantId)
                .query();
    }

    public static Query<FinanceWithdraw> finaAllWithdrawByStoreId(Long storeId) {
        return Ebean.find(FinanceWithdraw.class)
                .where().eq("store.id", storeId)
                .query();
    }

    public static Integer getTotalWithdraw(Long merchantId, String startDate, String endDate) throws Exception {
        Query<FinanceWithdraw>  query = Ebean.find(FinanceWithdraw.class)
                .fetch("store")
                .fetch("store.merchant")
                .where()
                .eq("store.merchant.id", merchantId)
                .query();

        ExpressionList<FinanceWithdraw> exp = query.where();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date start = simpleDateFormat.parse(startDate.concat(" 00:00:00.0"));
        Date end = simpleDateFormat.parse(endDate.concat(" 23:59:00.0"));

        Timestamp startTimestamp = new Timestamp(start.getTime());
        Timestamp endTimestamp = new Timestamp(end.getTime());
        exp.between("t0.date", startTimestamp, endTimestamp);
        return query.findList().size();
    }

    public static List<FinanceWithdraw> findAllWithdraw(Query<FinanceWithdraw> reqQuery, String startDate, String endDate,
                                                        String sort, int offset, int limit, String status) throws ParseException {
        Query<FinanceWithdraw> query = reqQuery;

        if(!sort.equals("")) {
            query = query.orderBy(sort);
        } else {
            query = query.orderBy("t0.date desc");
        }

        ExpressionList<FinanceWithdraw> exp = query.where();
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


}
