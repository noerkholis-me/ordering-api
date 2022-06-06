package dtos.store;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import models.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class StoreAccessResponse {

    private Long id;
    @JsonProperty("user_merchant_id")
    private Long userMerchantId;
    @JsonProperty("user_name")
    private String userName;
    @JsonProperty("merchant_id")
    private Long merchantId;
    @JsonProperty("is_active")
    private Boolean isActive;
    @JsonProperty("data_store")
    private List<StoreAccessDetail> storeData;
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    @Builder
    public static class StoreAccessDetail {

        @JsonProperty("store_id")
        private Long id;
        @JsonProperty("store_name")
        private String storeName;
        @JsonProperty("is_active")
        private Boolean isActive;

    }
}
