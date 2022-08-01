package dtos.cashier;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import utils.BigDecimalSerialize;

import java.math.BigDecimal;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class AmountCashierResponse {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Jakarta")
    private Date date;
    @JsonProperty("total_amount_opening_pos")
    @JsonSerialize(using = BigDecimalSerialize.class)
    private BigDecimal totalAmountOpeningPos;
    @JsonProperty("total_amount_by_system")
    @JsonSerialize(using = BigDecimalSerialize.class)
    private BigDecimal totalAmountBySystem;

}
