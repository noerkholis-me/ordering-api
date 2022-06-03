package repository;

import com.avaje.ebean.Expr;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Query;
import models.Merchant;
import models.productaddon.*;
import play.db.ebean.Model;

import java.util.List;

public class ProductAddOnRepository extends Model {

    public static Finder<Long, ProductAddOn> find = new Finder<>(Long.class, ProductAddOn.class);

    public static ProductAddOn findByIdAndMerchant(Long id, Long merchantId) {
        return find.where().eq("id", id).eq("merchant_id", merchantId).eq("is_active", Boolean.TRUE).findUnique();
    }

    public static ProductAddOn findByProductAssignIdAndProductId(Long productAssignId, Long productId) {
        return find.where()
                .eq("t0.product_assign_id", productAssignId)
                .eq("t0.product_id", productId)
                .findUnique();
    }

    public static List<ProductAddOn> getTotalDataPage (Query<ProductAddOn> reqQuery) {
        Query<ProductAddOn> query = reqQuery;
        ExpressionList<ProductAddOn> exp = query.where();
        query = exp.query();
        return query.findPagingList(0).getPage(0).getList();
    }

    public static List<ProductAddOn> findProductWithPaging(Query<ProductAddOn> reqQuery, String sort, String filter, int offset, int limit) {
        Query<ProductAddOn> query = reqQuery;

        if (!"".equals(sort)) {
            query = query.orderBy(sort);
        } else {
            query = query.orderBy("t0.created_at desc");
        }

        ExpressionList<ProductAddOn> exp = query.where();
        // exp = exp.disjunction();
        // exp = exp.ilike("t0.product_name", "%" + filter + "%");
        query = exp.query();
        if (limit != 0) {
            query = query.setMaxRows(limit);
        }
        return query.findPagingList(limit).getPage(offset).getList();
    }

    public static List<ProductAddOn> getDataForAddOn(Query<ProductAddOn> reqQuery) {
        Query<ProductAddOn> query = reqQuery;

        ExpressionList<ProductAddOn> exp = query.where();
        // exp = exp.disjunction();
        // exp = exp.ilike("t0.product_name", "%" + filter + "%");
        query = exp.query();
        return query.findPagingList(0).getPage(0).getList();
    }

}
