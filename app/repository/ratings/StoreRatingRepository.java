package repository.ratings;

import com.avaje.ebean.*;
import models.Member;
import models.Store;
import models.store.StoreRatings;
import models.store.StoreTaggings;
import play.db.ebean.Model;

import java.util.List;

public class StoreRatingRepository {
    public static Model.Finder<Long, StoreRatings> find = new Model.Finder<>(Long.class, StoreRatings.class);

    public static StoreRatings findByStoreAndMember(Store store, Member member) {
        return find.where()
                .eq("store", store)
                .eq("member", member)
                .eq("t0.is_deleted", Boolean.FALSE)
                .findUnique();
    }

    public static float getRatings(Store store) {

        try {

            String querySql = "SELECT AVG(rate) AS RATING FROM store_ratings WHERE store_id = " + store.id;

            SqlRow result = Ebean.createSqlQuery(querySql).findUnique();
            if (result == null) {
                return  0;
            }

            float rating = result.getFloat("RATING");

            return rating;

        } catch (Exception e) {
            return 0;
        }
    }
}
