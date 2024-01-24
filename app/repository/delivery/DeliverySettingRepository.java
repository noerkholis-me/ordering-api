package repository.delivery;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Query;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;
import models.delivery.DeliverySetting;
import play.db.ebean.Model;

import java.util.List;

public class DeliverySettingRepository extends Model {

    public static Model.Finder<Long, DeliverySetting> find = new Model.Finder<Long, DeliverySetting>(Long.class, DeliverySetting.class);

    public static List<DeliverySetting> findAll(Long merchantId, int offset, int limit) {
        String querySql = "SELECT ds.id FROM delivery_setting ds "
                + "WHERE ds.merchant_id = " + merchantId + " AND ds.is_deleted = false "
                + "ORDER BY ds.updated_at DESC";

        RawSql rawSql = RawSqlBuilder.parse(querySql).create();
        Query<DeliverySetting> query = Ebean.find(DeliverySetting.class).setRawSql(rawSql);

        return query.findPagingList(limit).getPage(offset).getList();
    }

    public static DeliverySetting findById(Long id) {
        return find.where()
                .eq("t0.id", id)
                .eq("t0.is_deleted", false)
                .findUnique();
    }

    public static DeliverySetting findByStoreId(Long storeId) {
        return find.where()
                .eq("t0.store_id", storeId)
                .eq("t0.is_deleted", false)
                .findUnique();
    }

}
