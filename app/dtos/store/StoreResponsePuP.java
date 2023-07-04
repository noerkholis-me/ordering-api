package dtos.store;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import models.Store;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class StoreResponsePuP {

    private Long id;

    @JsonProperty("store_code")
    private String storeCode;

    @JsonProperty("store_name")
    private String storeName;

    public StoreResponsePuP(Store store) {
        this.setId(store.id);
        this.setStoreCode(store.getStoreCode());
        this.setStoreName(store.getStoreName());
    }
}
