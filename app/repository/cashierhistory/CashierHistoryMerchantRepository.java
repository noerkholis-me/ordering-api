package repository.cashierhistory;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Query;
import models.merchant.CashierHistoryMerchant;
import models.transaction.Order;
import play.db.ebean.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CashierHistoryMerchantRepository extends Model {

    private static final Finder<Long, CashierHistoryMerchant> find = new Finder<>(Long.class, CashierHistoryMerchant.class);

    public static Optional<CashierHistoryMerchant> findByUserActiveCashier(Long userMerchantId, Long storeId) {
        return Optional.ofNullable(
            find.where()
                    .eq("isActive", true)
                    .eq("userMerchant.id", userMerchantId)
                    .eq("store.id", storeId)
                    .findUnique()
        );
    }

    public static Query<CashierHistoryMerchant> findAllCashierHistoryByStoreId(Long storeId) {
        return Ebean.find(CashierHistoryMerchant.class)
                .where().eq("store.id", storeId)
                .query();
    }

    public static Query<CashierHistoryMerchant> findAllCashierHistoryByMerchantId(Long merchantId) {
        return Ebean.find(CashierHistoryMerchant.class)
                .fetch("store")
                .fetch("store.merchant")
                .where()
                .eq("store.merchant.id", merchantId)
                .query();
    }

    public static Integer getTotalData(Query<CashierHistoryMerchant> reqQuery) {
        Query<CashierHistoryMerchant> query = reqQuery;
        ExpressionList<CashierHistoryMerchant> exp = query.where();
        query = exp.query();
        return query.findPagingList(0).getPage(0).getList().size();
    }

    public static List<CashierHistoryMerchant> findAllCashierHistory(Query<CashierHistoryMerchant> reqQuery, int offset, int limit) {
        Query<CashierHistoryMerchant> query = reqQuery;
        query.orderBy("t0.created_at desc");

        ExpressionList<CashierHistoryMerchant> exp = query.where();

        query = exp.query();
        if (limit != 0) {
            query = query.setMaxRows(limit);
        }

        return query.findPagingList(limit).getPage(offset).getList();
    }

}
