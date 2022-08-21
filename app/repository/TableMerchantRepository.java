package repository;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Query;
import models.merchant.TableMerchant;
import models.transaction.Order;
import play.db.ebean.Model;

import java.util.List;
import java.util.Optional;

public class TableMerchantRepository extends Model {

    private static Finder<Long, TableMerchant> find = new Finder<>(Long.class, TableMerchant.class);

    public static Optional<TableMerchant> findById(Long id) {
        return Optional.ofNullable(
                find.where()
                        .eq("id", id)
                        .eq("isDeleted", false)
                        .findUnique());
    }

    public static List<TableMerchant> findAllTables() {
        return find.where()
                .eq("isDeleted", false)
                .findList();
    }

    public static Query<TableMerchant> findAllTablesQuery() {
        return find.where()
                .eq("isDeleted", false)
                .order("id");
    }

    public static Query<TableMerchant> findAllTablesByStoreIdQuery(Long storeId) {
        return find.where()
                .eq("isDeleted", false)
                .eq("store_id", storeId)
                .order("id");
    }

    public static List<TableMerchant> getTotalPage(Query<TableMerchant> reqQuery) {
        Query<TableMerchant> query = reqQuery;
        ExpressionList<TableMerchant> exp = query.where();
        query = exp.query();
        return query.findPagingList(0).getPage(0).getList();
    }

    public static List<TableMerchant> findTablesWithPaging(Query<TableMerchant> reqQuery, String sort, String filter, int offset, int limit){
        Query<TableMerchant> query = reqQuery;
        if (!"".equals(sort)) {
            query = query.orderBy(sort);
        } else {
            query = query.orderBy("t0.created_at desc");
        }
        ExpressionList<TableMerchant> exp = query.where();
        exp = exp.disjunction();
        exp = exp.ilike("t0.name", "%" + filter + "%");
        query = exp.query();
        if (limit != 0) {
            query = query.setMaxRows(limit);
        }
        return query.findPagingList(limit).getPage(offset).getList();
    }

    public static List<TableMerchant> findTableMerchantByStoreId(Long storeId) {
        return Ebean.find(TableMerchant.class)
                .fetch("store")
                .where()
                .eq("store.id", storeId)
                .eq("isActive", true)
                .query().findList();
    }

}
