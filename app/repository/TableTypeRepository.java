package repository;

import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Query;
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

    public static List<TableType> getTotalDataPage (Query<TableType> reqQuery) {
        Query<TableType> query = reqQuery;
        ExpressionList<TableType> exp = query.where();
        query = exp.query();
        return query.findPagingList(0).getPage(0).getList();
    }

    public static List<TableType> findTableTypeWithPaging (Query<TableType> reqQuery, String sort, String filter, int offset, int limit) {
        Query<TableType> query = reqQuery;

        if (!"".equals(sort)) {
            query = query.orderBy(sort);
        } else {
            query = query.orderBy("t0.created_at desc");
        }

        ExpressionList<TableType> exp = query.where();
        exp = exp.disjunction();
        exp = exp.ilike("t0.name", "%" + filter + "%");
        query = exp.query();
        if (limit != 0) {
            query = query.setMaxRows(limit);
        }
        if (filter != "" && filter != null) {
            offset = 0;
        }
        return query.findPagingList(limit).getPage(offset).getList();
    }

}
