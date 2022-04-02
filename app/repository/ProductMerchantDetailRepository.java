package repository;

import models.merchant.ProductMerchant;
import models.merchant.ProductMerchantDetail;
import play.db.ebean.Model;

public class ProductMerchantDetailRepository extends Model {

    public static Finder<Long, ProductMerchantDetail> find = new Finder<>(Long.class, ProductMerchantDetail.class);

    public static ProductMerchantDetail findByProduct(ProductMerchant productMerchant) {
        return find.where().eq("productMerchant", productMerchant).findUnique();
    }

}
