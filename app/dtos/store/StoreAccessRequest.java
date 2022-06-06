package dtos.store;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import models.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class StoreAccessRequest {

    @JsonProperty("user_merchant_id")
    private Long userMerchantId;
    @JsonProperty("merchant_id")
    private Long merchantId;
    @JsonProperty("store_id")
    private List<Store> storeId;
    @JsonProperty("is_active")
    private Boolean isActive;
    @JsonProperty("is_deleted")
    private Boolean isDeleted;

}
