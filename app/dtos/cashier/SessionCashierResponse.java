package dtos.cashier;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import utils.BigDecimalSerialize;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class SessionCashierResponse {

    @JsonProperty("is_open")
    private Boolean isOpen;

    @JsonProperty("start_total_amount")
    @JsonSerialize(using = BigDecimalSerialize.class)
    private BigDecimal startTotalAmount;

    @JsonProperty("end_total_amount")
    @JsonSerialize(using = BigDecimalSerialize.class)
    private BigDecimal endTotalAmount;

}
