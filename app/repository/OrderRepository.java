package repository;

import com.avaje.ebean.Expr;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Query;
import models.transaction.*;
import play.db.ebean.Model;

import java.util.List;
import java.util.Optional;

public class OrderRepository extends Model {

    public static Finder<Long, Order> find = new Finder<Long, Order>(Long.class, Order.class);
    public static Finder<Long, OrderDetail> findDetail = new Finder<Long, OrderDetail>(Long.class, OrderDetail.class);
    public static Finder<Long, OrderPayment> findOrderPayment = new Finder<Long, OrderPayment>(Long.class, OrderPayment.class);

    public static Optional<Order> findById(Long id) {
        return Optional.ofNullable(find.where().eq("id", id).findUnique());
    }

    public static Optional<Order> findByOrderNumber(String orderNumber) {
        return Optional.ofNullable(find.where().eq("orderNumber", orderNumber).findUnique());
    }

    public static List<Order> findOrderDataByUser(Long userId) {
        Query<Order> query = find.where().eq("user_id", userId).order("t0.created_at desc");
        query = query.orderBy("t0.created_at desc");
        
        ExpressionList<Order> exp = query.where();
        // exp = exp.disjunction();
        // exp = exp.ilike("t0.updated_by", "%" + filter + "%");
        query = exp.query();
        return query.findPagingList(0).getPage(0).getList();
    }

    public static List<Order> findOrderByStatus(Query<Order> reqQuery, int offset, int limit) {
        Query<Order> query = reqQuery;
        query = query.orderBy("t0.created_at desc");
        
        ExpressionList<Order> exp = query.where();
        // exp = exp.disjunction();
        // exp = exp.ilike("t0.updated_by", "%" + filter + "%");
        query = exp.query();
        if (limit != 0) {
            query = query.setMaxRows(limit);
        }
        return query.findPagingList(limit).getPage(offset).getList();
    }

    public static List<OrderDetail> findDataOrderDetail(Long orderId, String productType) {
        String queryRaw = "t0.product_store_id in (select product_merchant_id from product_merchant_detail where product_type = '"+productType+"')";
        Query<OrderDetail> query = findDetail.where().raw(queryRaw).eq("t0.order_id", orderId).order("t0.created_at desc");
        // query = query.orderBy("t0.created_at desc");
        
        ExpressionList<OrderDetail> exp = query.where();
        query = exp.query();
        return query.findPagingList(0).getPage(0).getList();
    }

    public static List<OrderDetail> findDataOrderProductAdditional(Long orderId, Long productId, String productType) {
        
        String queryRaw = "t0.product_id in (select pmd.product_merchant_id from product_merchant_detail pmd where pmd.product_type = '"+productType+"' AND pmd.product_merchant_id in (select pao.product_assign_id from product_add_on_merchant pao where pao.product_id = "+productId+" ORDER BY pao.id asc))";
        Query<OrderDetail> query = findDetail.where().raw(queryRaw).eq("t0.order_id", orderId).order("t0.created_at desc");
        // query = query.orderBy("t0.created_at desc");
        
        ExpressionList<OrderDetail> exp = query.where();
        query = exp.query();
        return query.findPagingList(0).getPage(0).getList();
    }

    public static OrderPayment findDataOrderPayment(Long orderId) {
        return findOrderPayment.where().eq("t0.order_id", orderId).findUnique();
    }

    public static Order checkStatusMerchant(String orderNumber) {
        return find.where().eq("order_number", orderNumber).findUnique();
    }

}
