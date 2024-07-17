package repository;

import com.avaje.ebean.Ebean;

import models.Role;

public class RoleRepository {
    public static Role getById(Long id) {
        return Ebean.find(Role.class)
            .where()
            .eq("id", id)
            .findUnique();
    }

    public static Role getByKey(String key) {
        return Ebean.find(Role.class)
            .where()
            .eq("key", key)
            .findUnique();
    }
}
