package dtos.voucher;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VoucherStoreResponse {
    @JsonProperty("id")
    private Long storeId;
    @JsonProperty("store_name")
    private String storeName;
}
