package service.shop.order;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;

import models.Store;

public class ShopOrderService {
    public static Boolean IsStoreClosed(Store store) {
        boolean storeIsClosed = false;

        if (store.getStatusOpenStore() == null) {
            storeIsClosed = true;
            String featureCreationDateStr = "2023-05-17";
            LocalDate featureOnOfStoreCreationDate = LocalDate.parse(featureCreationDateStr);

            if (
                store.merchant.createdAt.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                    .isBefore(featureOnOfStoreCreationDate)
            ) {
                storeIsClosed = false;
            }
        } else {
            if (!store.getStatusOpenStore()) {
                storeIsClosed = true;
            } else {
                if (
                    (store.getOpenAt() == null || store.getClosedAt() == null)
                    || ("".equals(store.getOpenAt()) || "".equals(store.getClosedAt()))
                ) {
                    storeIsClosed = false;
                } else {
                    LocalTime currentTime = LocalTime.now();
                    LocalTime openTime = LocalTime.parse(store.getOpenAt());
                    LocalTime closeTime = LocalTime.parse(store.getClosedAt());

                    if (currentTime.isAfter(openTime) && currentTime.isBefore(closeTime) ) {
                        storeIsClosed = false;
                    } else {
                        storeIsClosed = true;
                    }
                }
            }
        }

        return storeIsClosed;
    }
}
