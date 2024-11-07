package repository;

import play.db.ebean.Model;

import models.transaction.OrderDetailStatus;

public class OrderDetailStatusRepository extends Model {
  public static Finder<Long, OrderDetailStatus> find = new Finder<Long, OrderDetailStatus>(Long.class, OrderDetailStatus.class);

  public static void save(OrderDetailStatus orderDetailStatus) {
    orderDetailStatus.save(); // This calls the save method from the Ebean Model
  }

  public static OrderDetailStatus findByCodeAndOrderDetailId(String code, Long order_detail_id) {
    OrderDetailStatus orderDetailStatus = find.where().eq("code", code).eq("order_detail_id", order_detail_id).findUnique();

    return orderDetailStatus;
  }
}
