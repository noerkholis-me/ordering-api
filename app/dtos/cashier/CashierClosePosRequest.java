package dtos.cashier;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CashierClosePosRequest implements Serializable {
    @JsonProperty("store_id")
    private Long storeId;
    @JsonProperty("end_total_amount_cash")
    private BigDecimal closeTotalAmountCash;
    @JsonProperty("notes")
    private String notes;
}
