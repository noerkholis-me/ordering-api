package repository;

import com.avaje.ebean.Expr;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Query;
import models.Merchant;
import models.Store;
import models.merchant.ProductMerchant;
import play.db.ebean.Model;

import java.util.List;

public class ProductMerchantRepository extends Model {

    public static Finder<Long, ProductMerchant> find = new Finder<>(Long.class, ProductMerchant.class);

    public static ProductMerchant findById(Long id, Merchant merchant) {
        return find.where().eq("id", id).eq("isActive", Boolean.TRUE).eq("merchant", merchant).findUnique();
    }

    public static Query<ProductMerchant> findProductIsActiveAndMerchant(Merchant merchant, Boolean isActive) {
        return find.where().eq("isDeleted", false).eq("isActive", isActive).eq("merchant", merchant).order("id");
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
        query = exp.query();
        if (limit != 0) {
            query = query.setMaxRows(limit);
        }
        return query.findPagingList(limit).getPage(offset).getList();
    }




}
