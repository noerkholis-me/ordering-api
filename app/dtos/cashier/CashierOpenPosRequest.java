package dtos.cashier;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CashierOpenPosRequest {

    @JsonProperty("store_id")
    private Long storeId;
    @JsonProperty("user_merchant_id")
    private Long userMerchantId;
    @JsonProperty("start_total_amount")
    private BigDecimal startTotalAmount;

}
