package dtos.voucher;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AvailableStoreByVoucherRes {
    @JsonProperty("voucher_id")
    private Long voucherId;
    @JsonProperty("store_id")
    private List<VoucherStoreResponse> stores;

}
