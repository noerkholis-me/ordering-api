package repository;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Query;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;
import models.ProductStore;
import models.QrGroup;
import models.QrGroupStore;
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
            + "WHERE qg.group_code = '"+ groupCode +"' AND qg.is_deleted = false "
            + "AND qgs.is_deleted = false "
            + "AND ps.is_deleted = false AND ps.is_active = true "
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

}
