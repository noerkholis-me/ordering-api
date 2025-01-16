package service;

import java.util.Optional;

import com.avaje.ebean.Ebean;

import models.Store;

public class StoreService {
    public static Optional<Store> findById(Long id) {
        return Optional.ofNullable(
            Ebean.find(Store.class)
                .where()
                .eq("id", id)
                .findUnique()
        );
    }
}
