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
        String querySql = "product_merchant_id in (select id from product_merchant where id = "+productId+" and is_active = "+true+" and is_deleted = "+false+")";
        return find.where().raw(querySql).eq("product_merchant_id", productId).eq("is_deleted", false).eq("product_type", "MAIN").findUnique();
    }

    public static List<ProductMerchantDetail> forProductRecommendation(Query<ProductMerchantDetail> reqQuery) {
        Query<ProductMerchantDetail> query = reqQuery;

        ExpressionList<ProductMerchantDetail> exp = query.where();
        return query.findPagingList(0).getPage(0).getList();
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

}
