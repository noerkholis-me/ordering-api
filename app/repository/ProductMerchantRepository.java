package repository;

import com.avaje.ebean.Expr;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Query;
import models.Merchant;
import models.Product;
import models.Store;
import models.merchant.*;
import play.db.ebean.Model;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ProductMerchantRepository extends Model {

    public static Finder<Long, ProductMerchant> find = new Finder<>(Long.class, ProductMerchant.class);

    public static ProductMerchant findById(Long id) {
        return find.where().eq("id", id).eq("isActive", Boolean.TRUE).findUnique();
    }

    public static ProductMerchant findById(Long id, Merchant merchant) {
        return find.where().eq("id", id).eq("merchant", merchant).findUnique();
    }

    public static ProductMerchant findByIdProductRecommend(Long id, Long merchantId) {
        return find.where().eq("id", id).eq("merchant_id", merchantId).findUnique();
    }

    public static Query<ProductMerchant> findProductIsActiveAndMerchant(Merchant merchant, Boolean isActive) {
        return find.where().eq("isDeleted", false).eq("isActive", isActive).eq("merchant", merchant).order("id");
    }

    public static Integer getTotalProduct(Merchant merchant, String startDate, String endDate) throws Exception {
        Query<ProductMerchant> productMerchantQuery = find.where()
                .eq("isActive", true)
                .eq("merchant", merchant)
                .query();

        ExpressionList<ProductMerchant> exp = productMerchantQuery.where();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date start = simpleDateFormat.parse(startDate.concat(" 00:00:00.0"));
        Date end = simpleDateFormat.parse(endDate.concat(" 23:59:00.0"));

        Timestamp startTimestamp = new Timestamp(start.getTime());
        Timestamp endTimestamp = new Timestamp(end.getTime());
        exp.between("t0.created_at", startTimestamp, endTimestamp);
        return productMerchantQuery.findList().size();
    }

    public static List<ProductMerchant> getTotalDataPage (Query<ProductMerchant> reqQuery) {
        Query<ProductMerchant> query = reqQuery;
        ExpressionList<ProductMerchant> exp = query.where();
        query = exp.query();
        return query.findPagingList(0).getPage(0).getList();
    }

    public static List<ProductMerchant> findProductWithPaging(Query<ProductMerchant> reqQuery, String sort, String filter, int offset, int limit) {
        Query<ProductMerchant> query = reqQuery;

        if (!"".equals(sort)) {
            query = query.orderBy(sort);
        } else {
            query = query.orderBy("t0.created_at desc");
        }

        ExpressionList<ProductMerchant> exp = query.where();
        exp = exp.disjunction();
        exp = exp.ilike("t0.product_name", "%" + filter + "%");
        query = exp.query();
        if (limit != 0) {
            query = query.setMaxRows(limit);
        }
        return query.findPagingList(limit).getPage(offset).getList();
    }

    public static List<ProductMerchant> getProductRecommendation(Query<ProductMerchant> reqQuery) {
        Query<ProductMerchant> query = reqQuery;

        ExpressionList<ProductMerchant> exp = query.where();
        return query.findPagingList(0).getPage(0).getList();
    }

    public static List<ProductMerchant> getDataProductStore(Query<ProductMerchant> reqQuery) {
        Query<ProductMerchant> query = reqQuery;

        query = query.orderBy("t0.created_at desc");

        ExpressionList<ProductMerchant> exp = query.where();
        return query.findPagingList(0).getPage(0).getList();
    }

}
