package repository.store;

import models.Store;
import models.store.StoreTaggings;
import play.db.ebean.Model;

import java.util.List;

public class StoreTaggingsRepository {
    public static Model.Finder<Long, StoreTaggings> find = new Model.Finder<>(Long.class, StoreTaggings.class);

    public static List<StoreTaggings> findByStore(Store store) {
        return find.where()
                .eq("store", store)
                .eq("t0.is_deleted", Boolean.FALSE)
                .findList();
    }
}
