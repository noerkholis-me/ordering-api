package repository.finance;

import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Query;
import models.BaseModel;
import models.finance.FinanceTransaction;

import java.util.List;

public class FinanceTransactionRepository extends BaseModel {

    private static final Finder<Long, FinanceTransaction> find = new Finder<>(Long.class, FinanceTransaction.class);


    public static List<FinanceTransaction> getTotalPage(Long storeId) {
        Query<FinanceTransaction> query = null;
        if (storeId != 0) {
            query = find.where().eq("isDeleted", false).eq("t0.store_id", storeId).query();
        } else {
            query = find.where().eq("isDeleted", false).query();
        }
        ExpressionList<FinanceTransaction> exp = query.where();
        query = exp.query();
        return query.findPagingList(0).getPage(0).getList();
    }

    public static List<FinanceTransaction> findAllTransaction(String startDate, String endDate, Long storeId,
                                                                        String sort, int offset, int limit, String status) {
        Query<FinanceTransaction> query = null;
        if (storeId != 0) {
            query = find.where().eq("isDeleted", false).eq("t0.store_id", storeId).eq("status", status).query();
        } else {
            query = find.where().eq("isDeleted", false).eq("status", status).query();
        }

        if(!sort.equals("")) {
            query = query.orderBy(sort);
        } else {
            query = query.orderBy("t0.date desc");
        }

        ExpressionList<FinanceTransaction> exp = query.where();
//        exp = exp.disjunction();
//        if (storeId != null || storeId != 0) {
//            exp.eq("t0.store_id", storeId);
//        }
        if (!startDate.equalsIgnoreCase("") && endDate.equalsIgnoreCase("")) {
            exp = exp.raw("between t0.date = " + startDate.concat(" 00:00:00") + " and " + endDate.concat(" 23:59:00"));
        }
        query = exp.query();
        if (limit != 0) {
            query = query.setMaxRows(limit);
        }
        return query.findPagingList(limit).getPage(offset).getList();
    }

}
