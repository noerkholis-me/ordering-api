package repository;

import models.transaction.OrderPayment;
import play.db.ebean.Model;

import java.util.Optional;

public class OrderPaymentRepository extends Model {

    public static Finder<Long, OrderPayment> find = new Finder<>(Long.class, OrderPayment.class);

    public static Optional<OrderPayment> findByOrderId(Long orderId) {
        return Optional.ofNullable(find.where().eq("order_id", orderId).findUnique());
    }

    public static Optional<OrderPayment> findByOrderIdAndStatus(Long orderId, String status) {
        return Optional.ofNullable(find.where().eq("order_id", orderId).eq("status", status).findUnique());
    }

    public static Optional<OrderPayment> findByOrderIdAndStatusAndPaymentChannel(Long orderId, String status) {
        String query = "t0.payment_channel in (select payment_code from payment_method pm where pm.payment_code = 'debit_credit' or pm.payment_code = 'cash' or pm.payment_code = 'debit' or pm.payment_code = 'credit')";
        return Optional.ofNullable(find.where()
                .eq("order_id", orderId)
                .eq("status", status)
                .raw(query)
                .findUnique());
    }

    public static Optional<OrderPayment> findByOrderIdAndStatusAndPaymentChannelWithOr(Long orderId) {
        String query = "t0.status in ('PAID','PENDING') and t0.payment_channel in (select payment_code from payment_method pm where pm.payment_code = 'debit_credit' or pm.payment_code = 'cash' or pm.payment_code = 'debit' or pm.payment_code = 'credit')";
        return Optional.ofNullable(find.where()
                .eq("order_id", orderId)
                .raw(query)
                .findUnique());
    }

}
