package repository.finance;

import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Query;
import models.BaseModel;
import models.finance.FinanceTransaction;
import models.finance.FinanceWithdraw;

import java.util.List;

public class FinanceWithdrawRepository extends BaseModel {

    private static final Finder<Long, FinanceWithdraw> find = new Finder<>(Long.class, FinanceWithdraw.class);

    public static List<FinanceWithdraw> getTotalPage(Long storeId) {
        Query<FinanceWithdraw> query = null;
        if (storeId != 0) {
            query = find.where().eq("isDeleted", false).eq("t0.store_id", storeId).query();
        } else {
            query = find.where().eq("isDeleted", false).query();
        }

        ExpressionList<FinanceWithdraw> exp = query.where();
        query = exp.query();
        return query.findPagingList(0).getPage(0).getList();
    }

    public static List<FinanceWithdraw> findAllWithdraw(String startDate, String endDate, Long storeId,
                                                        String sort, int offset, int limit, String status) {
        Query<FinanceWithdraw> query = null;
        if (storeId != null) {
            query = find.where().eq("isDeleted", false).eq("t0.store_id", storeId).query();
        }
        if (storeId != 0 && !status.equals("")) {
            query = find.where().eq("isDeleted", false).eq("t0.store_id", storeId).eq("t0.status", status).query();
        }
        if (!status.equals("")) {
            query = find.where().eq("isDeleted", false).eq("t0.status", status).query();
        }
        if (storeId == 0 || status.equals("")) {
            query = find.where().eq("isDeleted", false).query();
        }

        if(!sort.equals("")) {
            query = query.orderBy(sort);
        } else {
            query = query.orderBy("t0.date desc");
        }

        ExpressionList<FinanceWithdraw> exp = query.where();
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
