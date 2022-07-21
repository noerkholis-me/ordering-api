package repository;

import com.avaje.ebean.Expr;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Query;
import models.Merchant;
import models.productaddon.*;
import play.db.ebean.Model;

import java.util.List;

public class ProductAddOnTypeRepository extends Model {

    public static Finder<Long, ProductAddOnType> find = new Finder<>(Long.class, ProductAddOnType.class);

    public static ProductAddOnType findByIdAndMerchant(Long id, Long merchantId) {
        return find.where().eq("id", id).eq("merchant_id", merchantId).eq("is_active", Boolean.TRUE).findUnique();
    }

    public static ProductAddOnType findByNameAndMerchant(String productType, Long merchantId) {
        return find.where().eq("product_type", productType).eq("merchant_id", merchantId).eq("is_active", Boolean.TRUE).findUnique();
    }
    
    public static List<ProductAddOnType> findByMerchantId(Merchant merchant) {
        return find.where().eq("merchant", merchant).findPagingList(0).getPage(0).getList();
    }


    public static List<ProductAddOnType> getTotalDataPage (Query<ProductAddOnType> reqQuery) {
        Query<ProductAddOnType> query = reqQuery;
        ExpressionList<ProductAddOnType> exp = query.where();
        query = exp.query();
        return query.findPagingList(0).getPage(0).getList();
    }

    public static List<ProductAddOnType> findProductTipeWithPaging(Query<ProductAddOnType> reqQuery, String sort, String filter, int offset, int limit) {
        Query<ProductAddOnType> query = reqQuery;

        if (!"".equals(sort)) {
            query = query.orderBy(sort);
        } else {
            query = query.orderBy("t0.created_at desc");
        }

        ExpressionList<ProductAddOnType> exp = query.where();
        // exp = exp.disjunction();
        // exp = exp.ilike("t0.product_name", "%" + filter + "%");
        query = exp.query();
        if (limit != 0) {
            query = query.setMaxRows(limit);
        }
        return query.findPagingList(limit).getPage(offset).getList();
    }

    public static List<ProductAddOnType> getDataForAddOn(Query<ProductAddOnType> reqQuery) {
        Query<ProductAddOnType> query = reqQuery;

        ExpressionList<ProductAddOnType> exp = query.where();
        // exp = exp.disjunction();
        // exp = exp.ilike("t0.product_name", "%" + filter + "%");
        query = exp.query();
        return query.findPagingList(0).getPage(0).getList();
    }

}
