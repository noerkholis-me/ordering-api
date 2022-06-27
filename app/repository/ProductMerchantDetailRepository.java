package repository;

import models.merchant.ProductMerchant;
import models.merchant.ProductMerchantDetail;
import play.db.ebean.Model;
import com.avaje.ebean.Expr;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Query;

import java.util.List;

public class ProductMerchantDetailRepository extends Model {

    public static Finder<Long, ProductMerchantDetail> find = new Finder<>(Long.class, ProductMerchantDetail.class);

    public static ProductMerchantDetail findByProduct(ProductMerchant productMerchant) {
        return find.where().eq("productMerchant", productMerchant).findUnique();
    }

    public static ProductMerchantDetail findDetailProduct(Long productId, Long merchantId) {
        String querySql = "t0.product_merchant_id in (select pm.id from product_merchant pm where pm.id = "+productId+" and pm.merchant_id = "+merchantId+" and pm.is_active = "+true+" and pm.is_deleted = "+false+")";
        return find.where().raw(querySql).eq("t0.is_deleted", false).eq("t0.product_type", "MAIN").findUnique();
    }

    public static ProductMerchantDetail findDetailAdditionalProduct(Long productId, Long merchantId) {
        String querySql = "t0.product_merchant_id in (select pm.id from product_merchant pm where pm.id = "+productId+" and pm.merchant_id = "+merchantId+" and pm.is_active = "+true+" and pm.is_deleted = "+false+")";
        return find.where().raw(querySql).eq("t0.is_deleted", false).eq("t0.product_type", "ADDITIONAL").findUnique();
    }

    public static ProductMerchantDetail getTypeData(Long productId) {
        return find.where().eq("t0.is_deleted", false).eq("t0.product_merchant_id", productId).findUnique();
    }

    public static List<ProductMerchantDetail> findDataAdditionalForMerchant(Query<ProductMerchantDetail> reqQuery, Long merchantId) {
        Query<ProductMerchantDetail> query = reqQuery;

        ExpressionList<ProductMerchantDetail> exp = query.where();
        return query.findPagingList(0).getPage(0).getList();
    }

    public static List<ProductMerchantDetail> forProductRecommendation(Query<ProductMerchantDetail> reqQuery) {
        Query<ProductMerchantDetail> query = reqQuery;

        ExpressionList<ProductMerchantDetail> exp = query.where();
        return query.findPagingList(0).getPage(0).getList();
    }

    public static List<ProductMerchantDetail> findDetailData(Query<ProductMerchantDetail> reqQuery, String sort, String filter, int offset, int limit) {
        Query<ProductMerchantDetail> query = reqQuery;

        if (!"".equals(sort)) {
            query = query.orderBy(sort);
        } else {
            query = query.orderBy("t0.created_at desc");
        }

        ExpressionList<ProductMerchantDetail> exp = query.where();
        // exp = exp.disjunction();
        // exp = exp.ilike("pm.product_name", "%" + filter + "%");
        query = exp.query();
        if (limit != 0) {
            query = query.setMaxRows(limit);
        }
        return query.findPagingList(limit).getPage(offset).getList();
    }

    public static List<ProductMerchantDetail> findProductAdditional(Query<ProductMerchantDetail> reqQuery) {
        Query<ProductMerchantDetail> query = reqQuery;

        ExpressionList<ProductMerchantDetail> exp = query.where();
        return query.findPagingList(0).getPage(0).getList();
    }

    public static List<ProductMerchantDetail> getTotalDataPage (Query<ProductMerchantDetail> reqQuery) {
        Query<ProductMerchantDetail> query = reqQuery;
        ExpressionList<ProductMerchantDetail> exp = query.where();
        query = exp.query();
        return query.findPagingList(0).getPage(0).getList();
    }

    public static List<ProductMerchantDetail> getAllDataKiosK (Query<ProductMerchantDetail> reqQuery) {
        Query<ProductMerchantDetail> query = reqQuery;
        ExpressionList<ProductMerchantDetail> exp = query.where();
        query = exp.query();
        return query.findPagingList(0).getPage(0).getList();
    }

}
