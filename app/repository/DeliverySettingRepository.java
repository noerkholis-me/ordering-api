package repository;

import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Query;
import dtos.delivery.DeliverySettingRequest;
import models.DeliverySettings;
import models.StockHistory;
import models.merchant.ProductMerchant;
import models.store.StoreRatings;
import models.Merchant;
import models.Store;
import play.db.ebean.Model;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

public class DeliverySettingRepository extends Model {

    public static Finder<Long, DeliverySettings> find = new Finder<>(Long.class, DeliverySettings.class);

    public static List<DeliverySettings> findAll(Long merchantId, String sort, int offset, int limit) {

        Query<DeliverySettings> query = find.query();

        query.where().eq("merchant_id", merchantId);

        if (!"".equals(sort)) {
            query = query.orderBy(sort);
        } else {
            query = query.orderBy("t0.updated_at desc");
        }

        ExpressionList<DeliverySettings> exp = query.where();

        query = exp.query();

        int total = query.findList().size();

        if (limit != 0) {
            query = query.setMaxRows(limit);
        }

        List<DeliverySettings> resData = query.findPagingList(limit).getPage(offset).getList();

        return resData;
    }

    public  static int countAll(Long merchantId)  {

        int count = find.where().eq("merchant_id", merchantId).findRowCount();

        return count;

    }
    public static DeliverySettings findBystoreId(Long storeId) {
      DeliverySettings deliverySettings = find.where().eq("store_id", storeId).findUnique();
      return deliverySettings;
    }

    public static DeliverySettings addDeliverySettings(Store store, Merchant merchant, DeliverySettingRequest deliverySettingRequest) {
        System.out.println("INSERT NEW DATA");
                DeliverySettings newDeliverySettings = new DeliverySettings();
                newDeliverySettings.setStore(store);
                newDeliverySettings.setMerchant(merchant);
                newDeliverySettings.setMaxRangeFlatPrice(deliverySettingRequest.getMaxRangeFlatPrice());
                newDeliverySettings.setEnableFlatPrice(deliverySettingRequest.getEnableFlatPrice());
                newDeliverySettings.setKmPriceValue(deliverySettingRequest.getKmPriceValue());
                newDeliverySettings.setMaxRangeDelivery(deliverySettingRequest.getMaxRangeDelivery());
                newDeliverySettings.setFlatPriceValue(deliverySettingRequest.getFlatPriceValue());
                newDeliverySettings.setDeliverFee(deliverySettingRequest.getDeliverFee());
                newDeliverySettings.setCalculateMethod(deliverySettingRequest.getCalculateMethod());

                newDeliverySettings.save();
                return newDeliverySettings;
    }
    public static DeliverySettings updateDeliverySettings(DeliverySettings deliverySettings, Store store, Merchant merchant, DeliverySettingRequest deliverySettingRequest) {
      System.out.println("UPDATE NEW DATA");
                deliverySettings.setStore(store);
                deliverySettings.setMerchant(merchant);
                deliverySettings.setMaxRangeFlatPrice(deliverySettingRequest.getMaxRangeFlatPrice());
                deliverySettings.setEnableFlatPrice(deliverySettingRequest.getEnableFlatPrice());
                deliverySettings.setKmPriceValue(deliverySettingRequest.getKmPriceValue());
                deliverySettings.setMaxRangeDelivery(deliverySettingRequest.getMaxRangeDelivery());
                deliverySettings.setCalculateMethod(deliverySettingRequest.getCalculateMethod());
                deliverySettings.setDeliverFee(deliverySettingRequest.getDeliverFee());
                deliverySettings.setFlatPriceValue(deliverySettingRequest.getFlatPriceValue());

                deliverySettings.update();
                return deliverySettings;
    }
  }