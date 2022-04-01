package repository;

import models.merchant.ProductMerchant;
import play.db.ebean.Model;

public class ProductMerchantRepository extends Model {

    public static Finder<Long, ProductMerchant> find = new Finder<>(Long.class, ProductMerchant.class);

    public static ProductMerchant findById(Long id) {
        return find.where().eq("id", id).eq("isActive", Boolean.TRUE).findUnique();
    }

}
