package repository;

import models.transaction.Order;
import play.db.ebean.Model;

import java.util.Optional;

public class OrderRepository extends Model {

    public static Finder<Long, Order> find = new Finder<Long, Order>(Long.class, Order.class);

    public static Optional<Order> findById(Long id) {
        return Optional.ofNullable(find.where().eq("id", id).findUnique());
    }

    public static Optional<Order> findByOrderNumber(String orderNumber) {
        return Optional.ofNullable(find.where().eq("orderNumber", orderNumber).findUnique());
    }

}
