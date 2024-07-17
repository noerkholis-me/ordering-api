package repository;

import models.Merchant;

import java.util.Optional;

import com.avaje.ebean.Ebean;

public class MerchantRepository {
    public static Optional<Merchant> findById(Long id) {
        return Optional.ofNullable(
            Ebean.find(Merchant.class)
                .where()
                .eq("id", id)
                .findUnique()
        );
    }

    public static Optional<Merchant> findByName(String name) {
        return Optional.ofNullable(
            Ebean.find(Merchant.class)
                .where()
                .eq("name", name)
                .eq("isDeleted", false)
                .findUnique()
        );
    }

    public static Optional<Merchant> findByEmail(String email) {
        return Optional.ofNullable(
            Ebean.find(Merchant.class)
            .where()
            .eq("email", email)
            .eq("isDeleted", false)
            .findUnique()
        );
    }
}
