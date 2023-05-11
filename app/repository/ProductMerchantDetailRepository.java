package repository;

import models.ProductStore;
import models.merchant.ProductMerchant;
import models.merchant.ProductMerchantDetail;
import play.db.ebean.Model;
import com.avaje.ebean.*;

import java.util.List;

public class ProductMerchantDetailRepository extends Model {

    public static Finder<Long, ProductMerchantDetail> find = new Finder<>(Long.class, ProductMerchantDetail.class);

    public static ProductMerchantDetail findByProduct(ProductMerchant productMerchant) {
        return find.where().eq("productMerchant", productMerchant).findUnique();
    }
    
    public static ProductMerchantDetail findMainProduct(ProductMerchant productMerchant) {
        return find.where().eq("t0.is_deleted", false).eq("t0.product_merchant_id", productMerchant.id).eq("t0.product_type", "MAIN").findUnique();
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

    public static List<ProductMerchantDetail> getDataByPagination(Query<ProductMerchantDetail> reqQuery, int offset, int limit) {
        Query<ProductMerchantDetail> query = reqQuery;
        ExpressionList<ProductMerchantDetail> exp = query.where();
        query = exp.query();
        return query.findPagingList(limit).getPage(offset).getList();
    }

    public static List<ProductMerchantDetail> forProductBestSeller(Long merchantID) {
        String sql = "select pmd.id, pmd.product_type, pmd.is_customizable, pmd.product_price, pmd.discount_type, pmd.discount, pmd.product_price_after_discount, pmd.product_image_main, pmd.product_image_1, pmd.product_image_2, pmd.product_image_3, pmd.product_image_4, (select count(od.product_id) as totals from order_detail as od where od.product_id = pmd.product_merchant_id group by od.product_id order by totals desc ) as total_penjualan from product_merchant_detail as pmd where pmd.product_type = 'MAIN' and pmd.product_merchant_id in (select pm.id from product_merchant as pm where pm.is_deleted = false and pm.is_active = true and pm.merchant_id = "+merchantID+")";

        RawSql rawSql = RawSqlBuilder.parse(sql).create();
        // Query<TopSalesReportMerchant> query = Ebean.find(TopSalesReportMerchant.class);
        Query<ProductMerchantDetail> query = Ebean.find(ProductMerchantDetail.class);;
        query.setRawSql(rawSql);

        // return query;

        ExpressionList<ProductMerchantDetail> exp = query.where();
        return query.findPagingList(10).getPage(0).getList();
    }

    public static List<ProductMerchantDetail> findListProductStore(Long merchantId, Long storeId, String keyword, String filter, String sort, int offset, int limit) {
        String querySql;

        List<ProductStore> productStore = ProductStoreRepository.find.where().eq("t0.store_id", storeId).eq("t0.is_active", true).eq("t0.is_deleted", false).findList();
        if (productStore.isEmpty()) {
            querySql = "SELECT pmd.id FROM product_merchant_detail pmd "
                + "JOIN product_merchant pm ON pmd.product_merchant_id = pm.id "
                + "JOIN brand_merchant bm ON pm.brand_merchant_id = bm.id "
                + "WHERE pmd.product_type = 'MAIN' AND pmd.is_deleted = false "
                + "AND pm.merchant_id = " + merchantId + " AND pm.is_active = true AND pm.is_deleted = false "
                + "AND bm.is_active = true "
                + "ORDER BY pm.id DESC";
        } else {
            querySql = "SELECT pmd.id FROM product_merchant_detail pmd "
                + "JOIN product_merchant pm ON pmd.product_merchant_id = pm.id "
                + "LEFT JOIN product_store ps ON pm.id = ps.product_id "
                + "JOIN brand_merchant bm ON pm.brand_merchant_id = bm.id "
                + "WHERE pmd.product_type = 'MAIN' AND pmd.is_deleted = false "
                + "AND pm.merchant_id = " + merchantId + " AND pm.is_active = true AND pm.is_deleted = false "
                + "AND ps.store_id = " + storeId + " AND ps.is_active = true AND ps.is_deleted = false "
                + "AND bm.is_active = true "
                + "ORDER BY pm.id DESC";
        }

        RawSql rawSql = RawSqlBuilder.parse(querySql).create();
        Query<ProductMerchantDetail> query = Ebean.find(ProductMerchantDetail.class).setRawSql(rawSql);

        ExpressionList<ProductMerchantDetail> exp = query.where();
        exp = exp.disjunction();
        exp = exp.ilike("pm.product_name", "%" + keyword + "%");
        exp = exp.endJunction();
        query = exp.query();

        return query.findPagingList(limit).getPage(offset).getList();
    }

    public static List<ProductMerchantDetail> findListProductKiosk(Long brandId, Long merchantId, Long storeId, Long categoryId, String keyword, int offset, int limit) {
        String category = "";
        if (categoryId > 0) {
            category = "AND pm.subs_category_merchant_id = " + categoryId + " ";
        }

        String product;
        if (!brandId.equals(0L)) {
            product = "AND pm.merchant_id = " + merchantId + " AND pm.is_deleted = false AND pm.is_active = true "
                + "AND bm.is_active = true AND pm.brand_merchant_id = " + brandId + " " + category + " ";
        } else {
            product = "AND pm.merchant_id = " + merchantId + " AND pm.is_deleted = false AND pm.is_active = true ";
        }

        String querySql = "SELECT pmd.id FROM product_merchant_detail pmd "
            + "JOIN product_merchant pm ON pmd.product_merchant_id = pm.id "
            + "JOIN brand_merchant bm ON pm.brand_merchant_id = bm.id "
            + "WHERE pmd.product_type = 'MAIN' AND pmd.is_deleted = false "
            + product
            + "ORDER BY pm.id ASC";

        RawSql rawSql = RawSqlBuilder.parse(querySql).create();
        Query<ProductMerchantDetail> query = Ebean.find(ProductMerchantDetail.class).setRawSql(rawSql);

        ExpressionList<ProductMerchantDetail> exp = query.where();
        exp = exp.disjunction();
        exp = exp.ilike("pm.product_name", "%" + keyword + "%");
        exp = exp.endJunction();
        query = exp.query();

        return query.findPagingList(limit).getPage(offset).getList();
    }

}
