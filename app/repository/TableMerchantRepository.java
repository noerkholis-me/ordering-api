package repository;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Query;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;
import models.merchant.TableMerchant;
import models.transaction.Order;
import play.db.ebean.Model;

import java.util.List;
import java.util.Optional;

public class TableMerchantRepository extends Model {

    public static Finder<Long, TableMerchant> find = new Finder<>(Long.class, TableMerchant.class);

    public static Optional<TableMerchant> findById(Long id) {
        return Optional.ofNullable(
                find.where()
                        .eq("id", id)
                        .eq("isDeleted", false)
                        .findUnique());
    }

    public static Optional<TableMerchant> findByIdAndAvailable(Long id) {
        return Optional.ofNullable(
                find.where()
                        .eq("id", id)
                        .eq("isActive", true)
                        .eq("isDeleted", false)
                        .eq("isAvailable", true)
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

    public static List<TableMerchant> findListTables(Long storeId, String sort, String filter, int offset, int limit){
        String sorting;
        if (!"".equals(sort)) {
            sorting = sort;
        } else {
            sorting = "DESC";
        }

        String querySql;
        if (storeId == null || storeId == 0) {
            querySql = "SELECT tm.id FROM table_merchant tm "
                + "WHERE tm.is_deleted = false "
                + "ORDER BY tm.id " + sorting;
        } else {
            querySql = "SELECT tm.id FROM table_merchant tm "
                + "WHERE tm.store_id = " + storeId + " AND tm.is_deleted = false "
                + "ORDER BY tm.id " + sorting;
        }

        RawSql rawSql = RawSqlBuilder.parse(querySql).create();
        Query<TableMerchant> query = Ebean.find(TableMerchant.class).setRawSql(rawSql);

        ExpressionList<TableMerchant> exp = query.where();
        exp = exp.disjunction();
        exp = exp.ilike("tm.name", "%" + filter + "%");
        exp = exp.endJunction();
        query = exp.query();

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
