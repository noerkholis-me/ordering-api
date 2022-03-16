package repository;

import models.UserMerchant;
import play.db.ebean.Model;

public class UserMerchantRepository extends Model {

    public static Finder<Long, UserMerchant> find = new Finder<>(Long.class, UserMerchant.class);

    public static UserMerchant findByEmailAndMerchantId(String email, Long merchantId) {
        return find.where()
            .eq("email", email)
            .eq("merchantId", merchantId)
            .eq("isActive", Boolean.TRUE)
            .findUnique();
    }

}
