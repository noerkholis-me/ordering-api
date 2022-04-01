package repository;

import models.merchant.ProductMerchantDescription;
import models.merchant.ProductMerchantDetail;
import play.db.ebean.Model;

public class ProductMerchantDescriptionRepository extends Model {

    public static Finder<Long, ProductMerchantDescription> find = new Finder<>(Long.class, ProductMerchantDescription.class);

    public static ProductMerchantDescription findByProductMerchantDetail(ProductMerchantDetail productMerchantDetail) {
        return find.where().eq("productMerchantDetail", productMerchantDetail).findUnique();
    }

}
