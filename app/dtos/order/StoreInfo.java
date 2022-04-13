package dtos.order;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StoreInfo {

    @JsonProperty("merchant_id")
    private Long merchantId;
    @JsonProperty("merchant_name")
    private String merchantName;
    @JsonProperty("store_id")
    private Long storeId;
    @JsonProperty("store_name")
    private String storeName;


}
