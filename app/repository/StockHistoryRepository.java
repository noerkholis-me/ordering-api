package repository;

import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Query;
import models.StockHistory;
import models.merchant.ProductMerchant;
import play.db.ebean.Model;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

public class StockHistoryRepository extends Model {

    public static Finder<Long, StockHistory> find = new Finder<>(Long.class, StockHistory.class);

    public static List<StockHistory> findAll(Long merchantId, String sort, int offset, int limit, Long product_store_id) {

        Query<StockHistory> query = find.query();

        query.where().eq("merchant_id", merchantId);
        query.where().eq("product_store_id", product_store_id);

        if (!"".equals(sort)) {
            query = query.orderBy(sort);
        } else {
            query = query.orderBy("t0.updated_at desc");
        }

        ExpressionList<StockHistory> exp = query.where();

        query = exp.query();

        int total = query.findList().size();

        if (limit != 0) {
            query = query.setMaxRows(limit);
        }

        List<StockHistory> resData = query.findPagingList(limit).getPage(offset).getList();

        return resData;
    }

    public  static int countAll(Long merchantId, Long product_store_id)  {

        int count = find.where().eq("merchant_id", merchantId).eq("product_store_id", product_store_id).findRowCount();

        return count;

    }
}
