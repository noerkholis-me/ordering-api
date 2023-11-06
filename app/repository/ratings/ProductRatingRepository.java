package repository.ratings;

import models.Member;
import models.Store;
import play.db.ebean.Model;

public class ProductRatingRepository {
    public static Model.Finder<Long, models.ProductRatings> find = new Model.Finder<>(Long.class, models.ProductRatings.class);

    public static models.ProductRatings findByStoreAndMember(Store store, Member member) {
        return find.where()
                .eq("store", store)
                .eq("member", member)
                .eq("t0.is_deleted", Boolean.FALSE)
                .findUnique();
    }
}
