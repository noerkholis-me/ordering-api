package repository.ratings;

import java.util.List;

import javax.management.Query;

import models.Member;
import models.ProductRatings;
import models.Store;
import models.merchant.TableMerchant;
import models.transaction.Order;
import play.db.ebean.Model;
import com.avaje.ebean.*;

public class ProductRatingRepository {
    public static Model.Finder<Long, models.ProductRatings> find = new Model.Finder<>(Long.class,
            models.ProductRatings.class);

    public static models.ProductRatings findByStoreAndMember(Store store, Member member) {
        return find.where()
                .eq("store", store)
                .eq("member", member)
                .eq("t0.is_deleted", Boolean.FALSE)
                .findUnique();
    }

    public static models.ProductRatings findByProductMerchantIdAndStoreAndMember(Long productId, Store store,
            Member member) {
        return find.where()
                .eq("t0.product_merchant_id", productId)
                .eq("store", store)
                .eq("member", member)
                .eq("t0.is_deleted", Boolean.FALSE)
                .findUnique();
    }

    public static List<ProductRatings> findByProductRating(String order_number) {
        return Ebean.find(ProductRatings.class)
                .where()
                // .eq("store.id", storeId)
                .eq("order_number", order_number)
                .query().findList();
    }

    public static List<ProductRatings> findByProductRatingByProductId(Long storeId, Long productId) {
        return Ebean.find(ProductRatings.class)
                .where()
                .eq("store.id", storeId)
                .eq("product_merchant_id", productId)
                .query().findList();
    }

    public static int getTotalRating(Long storeId, Long productId){
        String sql = "SELECT SUM(rate) AS TOTAL FROM product_ratings" +
                " WHERE store_id = "+storeId+" AND product_merchant_id = "+productId+"";

        com.avaje.ebean.SqlQuery query = Ebean.createSqlQuery(sql);
        return query.findUnique().getInteger("TOTAL");
    }
}
