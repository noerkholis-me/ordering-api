package repository;

import java.util.List;
import java.util.Optional;

import models.transaction.Order;
import play.db.ebean.Model;

import models.transaction.OrderDetail;


public class OrderDetailRepository extends Model {
  public static Finder<Long, OrderDetail> find = new Finder<Long, OrderDetail>(Long.class, OrderDetail.class);

  // Added method to find OrderDetail by ID
  public static Optional<OrderDetail> findById(Long id) {
    return Optional.ofNullable(find.byId(id));
  }

  public static List<OrderDetail> findByOrderId(Long orderId) {
    return find.where().eq("order.id", orderId).findList(); // Return a list of OrderDetail
  }
}
