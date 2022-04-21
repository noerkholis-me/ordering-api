package repository;

import models.transaction.Order;
import models.transaction.OrderPayment;
import play.db.ebean.Model;

import java.util.Optional;

public class OrderPaymentRepository extends Model {

    public static Finder<Long, OrderPayment> find = new Finder<>(Long.class, OrderPayment.class);

    public static Optional<OrderPayment> findByOrderId(Order order) {
        return Optional.ofNullable(find.where().eq("order_id", order).findUnique());
    }

}
