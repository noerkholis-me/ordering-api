package repository;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Query;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;
import models.BrandMerchant;
import models.CategoryMerchant;
import models.ProductStore;
import models.QrGroup;
import models.QrGroupStore;
import models.merchant.ProductMerchantDetail;
import play.db.ebean.Model;

import java.util.List;

public class QrGroupRepository extends Model {

    public static Model.Finder<Long, QrGroup> find = new Model.Finder<Long, QrGroup>(Long.class, QrGroup.class);

    public static QrGroup findByIdAndMerchant(Long id, Long merchantId) {
        return find.where().eq("t0.id", id).eq("t0.merchant_id", merchantId).eq("t0.is_deleted", false).findUnique();
    }

    public static QrGroup findByCode(String groupCode) {
        return find.where().eq("group_code", groupCode).eq("is_deleted", false).findUnique();
    }

    public static List<QrGroup> findAllQrGroup(Long merchantId, String filter, int offset, int limit) {
        Query<QrGroup> query = find.where()
            .eq("t0.merchant_id", merchantId)
            .eq("t0.is_deleted", false)
            .order("t0.id desc");

        ExpressionList<QrGroup> exp = query.where();
        exp = exp.disjunction();
        exp = exp.or(Expr.ilike("t0.group_name", "%" + filter + "%"), Expr.ilike("t0.group_code", "%" + filter + "%"));
        query = exp.query();

        return query.findPagingList(limit).getPage(offset).getList();
    }

    public static List<QrGroupStore> findQrGroupStoreByGroupId(Long qrGroupId, String filter, int offset, int limit) {
        Query<QrGroupStore> query = QrGroupStore.find.where()
            .eq("t0.qr_group_id", qrGroupId)
            .eq("t0.is_deleted", false)
            .order("t0.id desc");

        ExpressionList<QrGroupStore> exp = query.where();
        exp = exp.disjunction();
        exp = exp.ilike("store.storeName", "%" + filter + "%");
        query = exp.query();

        return query.findPagingList(limit).getPage(offset).getList();
    }

    public static List<QrGroupStore> findListStoreFromGroup(Long qrGroupId, String filter, String sort_name, String sort_store, int offset, int limit) {
        String sortByName = null;
        if (sort_name.equalsIgnoreCase("A-Z")) {
            sortByName = "st.store_name asc";
        } else if (sort_name.equalsIgnoreCase("Z-A")) {
            sortByName = "st.store_name desc";
        }

        String sortByStore = null;
        if (sort_store.equalsIgnoreCase("OLD")) {
            sortByStore = "st.id asc";
        } else if (sort_store.equalsIgnoreCase("NEW")) {
            sortByStore = "st.id desc";
        }

        String orderBy = null;
        if (!sort_name.trim().isEmpty() && !sort_store.trim().isEmpty()) {
            orderBy = "ORDER BY " + sortByName + ", " + sortByStore + " ";
        } else if (!sort_name.trim().isEmpty()) {
            orderBy = "ORDER BY " + sortByName + " ";
        } else if (!sort_store.trim().isEmpty()) {
            orderBy = "ORDER BY " + sortByStore + " ";
        } else {
            orderBy = "ORDER BY RANDOM ()";
        }

        String querySql = "SELECT qgs.id FROM qr_group_store qgs "
            + "JOIN store st ON qgs.store_id = st.id "
            + "WHERE qgs.qr_group_id = " + qrGroupId + " AND qgs.is_deleted = false "
            + orderBy;

        RawSql rawSql = RawSqlBuilder.parse(querySql).create();
        Query<QrGroupStore> query = Ebean.find(QrGroupStore.class).setRawSql(rawSql);

        ExpressionList<QrGroupStore> exp = query.where();
        exp = exp.disjunction();
        exp = exp.ilike("st.store_name", "%" + filter + "%");
        query = exp.query();

        return query.findPagingList(limit).getPage(offset).getList();
    }

    public static List<ProductStore> findListProductFromGroupCode(String groupCode, String filter, int offset, int limit) {
        String querySql = "SELECT ps.id FROM qr_group qg "
            + "JOIN qr_group_store qgs ON qgs.qr_group_id = qg.id "
            + "JOIN product_store ps ON ps.store_id = qgs.store_id "
            + "JOIN product_merchant pm ON pm.id = ps.product_id "
            + "JOIN product_merchant_detail pmd ON pmd.product_merchant_id = pm.id "
            + "JOIN brand_merchant bm ON pm.brand_merchant_id = bm.id "
            + "WHERE qg.group_code = '"+ groupCode +"' AND qg.is_deleted = false "
            + "AND qgs.is_deleted = false "
            + "AND ps.is_deleted = false AND ps.is_active = true "
            + "AND pm.is_deleted = false AND pm.is_active = true "
            + "AND pmd.is_deleted = false AND pmd.product_type = 'MAIN' "
            + "AND bm.is_active = true and pm.brand_merchant_id = bm.id "
            + "ORDER BY ps.product_id DESC";

        RawSql rawSql = RawSqlBuilder.parse(querySql).create();
        Query<ProductStore> query = Ebean.find(ProductStore.class).setRawSql(rawSql);

        ExpressionList<ProductStore> exp = query.where();
        exp = exp.disjunction();
        exp = exp.ilike("pm.product_name", "%" + filter + "%");
        exp = exp.endJunction();
        query = exp.query();

        return query.findPagingList(limit).getPage(offset).getList();
    }

    public static List<ProductStore> findAllProductFromGroup(String groupCode, Long brandId, Long categoryId, String keyword, int offset, int limit) {
        String category = null;
        if (categoryId > 0) {
            category = "AND pm.subs_category_merchant_id = " + categoryId + " ";
        }

        String product = null;
        if (!brandId.equals(0L)) {
            product = "AND pm.is_deleted = false AND pm.is_active = true AND bm.is_active = true "
                + "AND pm.brand_merchant_id = " + brandId + " " + category + " ";
        } else {
            product = "AND pm.is_deleted = false AND pm.is_active = true AND bm.is_active = true ";
        }

        String querySql = "SELECT ps.id FROM qr_group qg "
            + "JOIN qr_group_store qgs ON qgs.qr_group_id = qg.id "
            + "JOIN product_store ps ON ps.store_id = qgs.store_id "
            + "JOIN product_merchant pm ON pm.id = ps.product_id "
            + "JOIN product_merchant_detail pmd ON pmd.product_merchant_id = pm.id "
            + "JOIN brand_merchant bm ON pm.brand_merchant_id = bm.id "
            + "WHERE qg.group_code = '" + groupCode + "' AND qg.is_deleted = false "
            + "AND qgs.is_deleted = false "
            + "AND ps.is_deleted = false AND ps.is_active = true "
            + "AND pmd.is_deleted = false AND pmd.product_type = 'MAIN' "
            + product
            + "ORDER BY ps.product_id ASC";

        RawSql rawSql = RawSqlBuilder.parse(querySql).create();
        Query<ProductStore> query = Ebean.find(ProductStore.class).setRawSql(rawSql);

        ExpressionList<ProductStore> exp = query.where();
        exp = exp.disjunction();
        exp = exp.ilike("pm.product_name", "%" + keyword + "%");
        exp = exp.endJunction();
        query = exp.query();

        return query.findPagingList(limit).getPage(offset).getList();
    }

    public static List<CategoryMerchant> findListCategoryFromGroup(String groupCode, String filter, int offset, int limit) {
        String querySql = "SELECT cm.id FROM qr_group qg "
            + "JOIN merchant mc ON qg.merchant_id = mc.id "
            + "JOIN category_merchant cm ON mc.id = cm.merchant_id "
            + "JOIN product_merchant pm ON cm.id = pm.category_merchant_id "
            + "WHERE qg.group_code = '" + groupCode + "' AND qg.is_deleted = false "
            + "AND cm.is_deleted = false AND cm.is_active = true "
            + "AND pm.is_deleted = false AND pm.is_active = true "
            + "GROUP BY cm.id "
            + "ORDER BY cm.id DESC";

        RawSql rawSql = RawSqlBuilder.parse(querySql).create();
        Query<CategoryMerchant> query = Ebean.find(CategoryMerchant.class).setRawSql(rawSql);

        return query.findPagingList(limit).getPage(offset).getList();
    }

    public static String queryTotalCategory(String groupCode, String category) {
        return "SELECT pm.id FROM qr_group qg "
            + "JOIN merchant mc ON qg.merchant_id = mc.id "
            + "JOIN category_merchant cm ON mc.id = cm.merchant_id "
            + "JOIN product_merchant pm ON cm.id = pm.category_merchant_id "
            + "JOIN product_merchant_detail pmd ON pm.id = pmd.product_merchant_id "
            + "WHERE qg.group_code = '" + groupCode + "' AND qg.is_deleted = false "
            + "AND cm.is_deleted = false AND cm.is_active = true "
            + "AND pm.is_deleted = false AND pm.is_active = true AND " + category + " "
            + "AND pmd.is_deleted = false AND pmd.product_type = 'MAIN' "
            + "ORDER BY pm.id DESC";
    }

    public static Integer getTotalProductCategory(String groupCode, Long categoryMerchantId) {
        String querySql = queryTotalCategory(groupCode, "pm.category_merchant_id = " + categoryMerchantId + "");

        RawSql rawSql = RawSqlBuilder.parse(querySql).create();
        Query<CategoryMerchant> query = Ebean.find(CategoryMerchant.class).setRawSql(rawSql);

        return query.findPagingList(0).getPage(0).getList().size();
    }

    public static Integer getTotalProductSubCategory(String groupCode, Long subCategoryMerchantId) {
        String querySql = queryTotalCategory(groupCode, "pm.sub_category_merchant_id = " + subCategoryMerchantId + "");

        RawSql rawSql = RawSqlBuilder.parse(querySql).create();
        Query<CategoryMerchant> query = Ebean.find(CategoryMerchant.class).setRawSql(rawSql);

        return query.findPagingList(0).getPage(0).getList().size();
    }

    public static Integer getTotalProductSubsCategory(String groupCode, Long subsCategoryMerchantId) {
        String querySql = queryTotalCategory(groupCode, "pm.subs_category_merchant_id = " + subsCategoryMerchantId + "");

        RawSql rawSql = RawSqlBuilder.parse(querySql).create();
        Query<CategoryMerchant> query = Ebean.find(CategoryMerchant.class).setRawSql(rawSql);

        return query.findPagingList(0).getPage(0).getList().size();
    }

    public static List<BrandMerchant> findListBrandFromGroup(String groupCode, String filter, int offset, int limit) {
        String querySql = "SELECT bm.id FROM qr_group qg "
            + "JOIN qr_group_store qgs ON qgs.qr_group_id = qg.id "
            + "JOIN product_store ps ON ps.store_id = qgs.store_id "
            + "JOIN product_merchant pm ON pm.id = ps.product_id "
            + "JOIN product_merchant_detail pmd ON pmd.product_merchant_id = pm.id "
            + "JOIN brand_merchant bm ON pm.brand_merchant_id = bm.id "
            + "WHERE qg.group_code = '"+ groupCode +"' AND qg.is_deleted = false "
            + "AND qgs.is_deleted = false "
            + "AND ps.is_deleted = false AND ps.is_active = true "
            + "AND pm.is_deleted = false AND pm.is_active = true "
            + "AND pmd.is_deleted = false AND pmd.product_type = 'MAIN' "
            + "AND bm.is_active = true "
            + "GROUP BY bm.id "
            + "ORDER BY bm.brand_name ASC";

        RawSql rawSql = RawSqlBuilder.parse(querySql).create();
        Query<BrandMerchant> query = Ebean.find(BrandMerchant.class).setRawSql(rawSql);

        ExpressionList<BrandMerchant> exp = query.where();
        exp = exp.disjunction();
        exp = exp.ilike("bm.brand_name", "%" + filter + "%");
        exp = exp.endJunction();
        query = exp.query();

        return query.findPagingList(limit).getPage(offset).getList();
    }

    public static Integer getTotalProductBrandFromGroup(String groupCode, Long brandId) {
        String querySql = "SELECT bm.id FROM qr_group qg "
            + "JOIN qr_group_store qgs ON qgs.qr_group_id = qg.id "
            + "JOIN product_store ps ON ps.store_id = qgs.store_id "
            + "JOIN product_merchant pm ON pm.id = ps.product_id "
            + "JOIN product_merchant_detail pmd ON pmd.product_merchant_id = pm.id "
            + "JOIN brand_merchant bm ON pm.brand_merchant_id = bm.id "
            + "WHERE qg.group_code = '"+ groupCode +"' AND qg.is_deleted = false "
            + "AND qgs.is_deleted = false "
            + "AND ps.is_deleted = false AND ps.is_active = true "
            + "AND pm.is_deleted = false AND pm.is_active = true "
            + "AND pmd.is_deleted = false AND pmd.product_type = 'MAIN' "
            + "AND pm.brand_merchant_id = " + brandId + " AND bm.is_active = true "
            + "ORDER BY bm.brand_name ASC";


        RawSql rawSql = RawSqlBuilder.parse(querySql).create();
        Query<BrandMerchant> query = Ebean.find(BrandMerchant.class).setRawSql(rawSql);

        return query.findPagingList(0).getPage(0).getList().size();
    }
}
