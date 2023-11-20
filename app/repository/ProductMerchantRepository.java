package repository;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Query;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;
import models.CategoryMerchant;
import models.Merchant;
import models.Product;
import models.Store;
import models.merchant.*;
import play.db.ebean.Model;

import java.io.IOException;
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
		exp = exp.ilike("t0.no_sku", "%" + filter + "%");

        query = exp.query();
        if (limit != 0) {
            query = query.setMaxRows(limit);
        }
        if (filter != "" && filter != null) {
            offset = 0;
        }
        return query.findPagingList(limit).getPage(offset).getList();
    }

    public static List<ProductMerchant> findAllProduct(Long merchantId, Boolean isActive, String sort, String filter, int offset, int limit) {
        String sorting;
        if (!"".equals(sort)) {
            sorting = sort;
        } else {
            sorting = "DESC";
        }

        String querySql = "SELECT pm.id FROM product_merchant pm "
            + "WHERE pm.merchant_id = " + merchantId + " AND pm.is_deleted = false AND pm.is_active = '" + isActive + "' "
            + "ORDER BY pm.id " + sorting;

        RawSql rawSql = RawSqlBuilder.parse(querySql).create();
        Query<ProductMerchant> query = Ebean.find(ProductMerchant.class).setRawSql(rawSql);

        ExpressionList<ProductMerchant> exp = query.where();
        exp = exp.disjunction();
        exp = exp.ilike("pm.product_name", "%" + filter + "%");
        exp = exp.ilike("pm.no_sku", "%" + filter + "%");
        exp = exp.endJunction();
        query = exp.query();

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

    public static List<ProductMerchant> findProductMerchant(Long merchantId, Long storeId) {
        String querySql = "SELECT pm.id FROM product_merchant pm "
            + "JOIN product_merchant_detail pmd ON pm.id = pmd.product_merchant_id "
            + "JOIN product_store ps ON pm.id = ps.product_id "
            + "WHERE pm.merchant_id = " + merchantId + " AND pm.is_deleted = false AND pm.is_active = true "
            + "AND pmd.is_deleted = false "
            + "AND ps.store_id = " + storeId + " AND ps.is_deleted = false AND ps.is_active = true "
            + "ORDER BY pm.id ASC";

        RawSql rawSql = RawSqlBuilder.parse(querySql).create();

        return Ebean.find(ProductMerchant.class).setRawSql(rawSql).findList();
    }

    public static List<ProductMerchant> findAllProductMerchant(Long merchantId, Long storeId) {
        String querySql = "SELECT pm.id FROM product_merchant pm "
            + "JOIN product_merchant_detail pmd ON pm.id = pmd.product_merchant_id "
            + "JOIN product_store ps ON pm.id = ps.product_id "
            + "WHERE pm.merchant_id = " + merchantId + " AND pm.is_deleted = false AND pm.is_active = true "
            + "AND pmd.is_deleted = false "
            + "AND ps.store_id = " + storeId + " AND ps.is_deleted = false AND ps.is_active = true "
            + "ORDER BY pm.id ASC";

        RawSql rawSql = RawSqlBuilder.parse(querySql).create();

        return Ebean.find(ProductMerchant.class).setRawSql(rawSql).findList();
    }

    public static List<ProductMerchant> getTotalDataApp(Query<ProductMerchant> reqQuery, String sort, String filter, int offset, int limit)
            throws IOException {
        Query<ProductMerchant> query = reqQuery;

        if (!"".equals(sort)) {
            query = query.orderBy(sort);
        } else {
            query = query.orderBy("t0.updated_at desc");
        }

        ExpressionList<ProductMerchant> exp = query.where();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");


        exp = exp.disjunction();
        exp = exp.ilike("t0.product_name", "%" + filter + "%");
        // exp = exp.endjunction();

        query = exp.query();

        int total = query.findList().size();

        if (limit != 0) {
            query = query.setMaxRows(limit);
        }
        if (filter != "" && filter != null) {
            offset = 0;
        }

        List<ProductMerchant> resData = query.findPagingList(limit).getPage(offset).getList();

        return resData;
    }

}
