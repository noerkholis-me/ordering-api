package dtos.finance;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import utils.BigDecimalSerialize;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ActiveBalanceResponse {

    @JsonProperty("active_balance")
    @JsonSerialize(using = BigDecimalSerialize.class)
    private BigDecimal activeBalance;
    @JsonProperty("total_active_balance")
    @JsonSerialize(using = BigDecimalSerialize.class)
    private BigDecimal totalActiveBalance;
    @JsonProperty("filtered_active_balance")
    @JsonSerialize(using = BigDecimalSerialize.class)
    private BigDecimal filteredActiveBalance;


}
