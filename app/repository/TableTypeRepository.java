package repository;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Query;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;
import models.Merchant;
import models.merchant.TableType;
import play.db.ebean.Model;

import java.util.List;
import java.util.Optional;

public class TableTypeRepository extends Model {

    private static Finder<Long, TableType> find = new Finder<>(Long.class, TableType.class);

    public static Optional<TableType> findById(Long id, Merchant merchant) {
        return Optional.ofNullable(find.where()
                .eq("id", id)
                .eq("merchant", merchant)
                .eq("isDeleted", false)
                .findUnique());
    }

    public static List<TableType> findAllByMerchant (Merchant merchant) {
        return find.where().eq("merchant", merchant).eq("isDeleted", false).order("id").findList();
    }

    public static Query<TableType> findByIsActiveAndMerchant (Merchant merchant) {
        return find.where().eq("isDeleted", false).eq("merchant", merchant).order("id");
    }

    public static List<TableType> findListTableType (Long merchantId, String sort, String filter, int offset, int limit) {
        String sorting;
        if (!"".equals(sort)) {
            sorting = sort;
        } else {
            sorting = "DESC";
        }

        String querySql = "SELECT tp.id FROM table_type tp "
            + "WHERE tp.merchant_id = " + merchantId + " AND tp.is_deleted = false "
            + "ORDER BY tp.id " + sorting;

        RawSql rawSql = RawSqlBuilder.parse(querySql).create();
        Query<TableType> query = Ebean.find(TableType.class).setRawSql(rawSql);

        ExpressionList<TableType> exp = query.where();
        exp = exp.disjunction();
        exp = exp.ilike("tp.name", "%" + filter + "%");
        exp = exp.endJunction();
        query = exp.query();

        return query.findPagingList(limit).getPage(offset).getList();
    }

}
