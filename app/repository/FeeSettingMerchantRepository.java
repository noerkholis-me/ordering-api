package repository;

import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Query;
import models.Merchant;
import models.Store;
import models.UserMerchant;
import models.merchant.FeeSettingMerchant;
import play.db.ebean.Model;

import java.util.List;
import java.util.Optional;

public class FeeSettingMerchantRepository extends Model {

    private static Finder<Long, FeeSettingMerchant> find = new Finder<>(Long.class, FeeSettingMerchant.class);

    public static Optional<FeeSettingMerchant> findById(Long id) {
        return Optional.ofNullable(
                find.where()
                    .eq("id", id)
                    .eq("isDeleted", false)
                    .findUnique());
    }

    public static Optional<FeeSettingMerchant> findByLatestFeeSetting(Long merchantId) {
        return Optional.of(
                find.where()
                        .eq("t0.merchant_id", merchantId)
                        .eq("t0.is_deleted", false)
                        .orderBy("t0.date desc")
                        .findList().get(0)
        );
    }

    public static Optional<FeeSettingMerchant> findByLatestFeeSettingByStore(Long storeId) {
        return Optional.of(
                find.where()
                        .eq("t0.store_id", storeId)
                        .eq("t0.is_deleted", false)
                        .orderBy("t0.date desc")
                        .findList().get(0)
        );
    }

    public static Query<FeeSettingMerchant> findAllByMerchantQuery(Merchant merchant) {
        return find.where().eq("isDeleted", false).eq("merchant", merchant).orderBy("date desc");
    }

    public static Query<FeeSettingMerchant> findAllByStoreQuery(Store store) {
        return find.where().eq("isDeleted", false).eq("store", store).orderBy("date desc");
    }

    public static List<FeeSettingMerchant> getTotalPage(Query<FeeSettingMerchant> requestQuery) {
        Query<FeeSettingMerchant> query = requestQuery;
        ExpressionList<FeeSettingMerchant> exp = query.where();
        query = exp.query();
        return query.findPagingList(0).getPage(0).getList();
    }

    public static List<FeeSettingMerchant> findAllWithPaging(Query<FeeSettingMerchant> reqQuery, String sort, String filter, int offset, int limit) {
        Query<FeeSettingMerchant> query = reqQuery;
        if (!sort.equals("")) {
            query = query.orderBy(sort);
        } else {
            query = query.orderBy("t0.date desc");
        }
        ExpressionList<FeeSettingMerchant> exp = query.where();
        exp = exp.disjunction();
        exp = exp.ilike("t0.updated_by", "%" + filter + "%");
        query = exp.query();
        if (limit != 0) {
            query = query.setMaxRows(limit);
        }
        return query.findPagingList(limit).getPage(offset).getList();
    }

}
