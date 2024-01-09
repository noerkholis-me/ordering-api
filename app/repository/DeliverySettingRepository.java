package repository;

import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Query;
import dtos.delivery.DeliverySettingRequest;
import models.DeliverySettings;
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
                deliverySettings.setFlatPriceValue(deliverySettingRequest.getFlatPriceValue());

                deliverySettings.update();
                return deliverySettings;
    }
  }