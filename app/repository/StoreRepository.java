package repository;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Query;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;
import models.StockHistory;
import models.Store;
import play.db.ebean.Model;

import java.util.List;

public class StoreRepository {
    public static Model.Finder<Long, Store> find = new Model.Finder<Long, Store>(Long.class, Store.class);

    public static Store findByName(String storeName) {
        String querySql = "SELECT s.id FROM store s "
                + "WHERE LOWER(s.store_name) = '" + storeName.toLowerCase() + "' "
                + "AND s.is_deleted = false AND s.is_active = true ";

        RawSql rawSql = RawSqlBuilder.parse(querySql).create();
        Query<Store> query = Ebean.find(Store.class).setRawSql(rawSql);

        return query.findUnique();
    }

    public static List<Store> findAll(String sort, int offset, int limit) {
        Query<Store> query = find.query();

        if (!"".equals(sort)) {
            query = query.orderBy(sort);
        } else {
            query = query.orderBy("t0.updated_at desc");
        }

        ExpressionList<Store> exp = query.where();

        query = exp.query();

        int total = query.findList().size();

        if (limit != 0) {
            query = query.setMaxRows(limit);
        }

        List<Store> resData = query.findPagingList(limit).getPage(offset).getList();

        return resData;
    }

    public static List<Store> findAllStore(String filter, String sort, int offset, int limit) {
        String sorting;
        if (!"".equals(sort)) {
            sorting = sort;
        } else {
            sorting = "DESC";
        }

        String querySql = "SELECT s.id FROM store s "
                + "WHERE s.is_deleted = false "
                + "ORDER BY s.id " + sorting;

        RawSql rawSql = RawSqlBuilder.parse(querySql).create();
        Query<Store> query = Ebean.find(Store.class).setRawSql(rawSql);

        ExpressionList<Store> exp = query.where();
        exp = exp.disjunction();
        exp = exp.ilike("s.store_name", "%" + filter + "%");
        exp = exp.ilike("s.store_code", "%" + filter + "%");
        exp = exp.endJunction();
        query = exp.query();

        return query.findPagingList(limit).getPage(offset).getList();
    }

    public static List<Store> findAllStoreIsActiveByMerchant(Long merchantId, String filter, String sort, int offset, int limit) {
        String sorting;
        if (!"".equals(sort)) {
            sorting = sort;
        } else {
            sorting = "DESC";
        }

        String querySql = "SELECT s.id FROM store s "
                + "WHERE s.merchant_id = " + merchantId + " AND s.is_deleted = false AND s.is_active = true "
                + "ORDER BY s.id " + sorting;

        RawSql rawSql = RawSqlBuilder.parse(querySql).create();
        Query<Store> query = Ebean.find(Store.class).setRawSql(rawSql);

        ExpressionList<Store> exp = query.where();
        exp = exp.disjunction();
        exp = exp.ilike("s.store_name", "%" + filter + "%");
        exp = exp.ilike("s.store_code", "%" + filter + "%");
        exp = exp.endJunction();
        query = exp.query();

        return query.findPagingList(limit).getPage(offset).getList();
    }

}
