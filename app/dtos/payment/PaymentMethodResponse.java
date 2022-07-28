package dtos.payment;

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
public class PaymentMethodResponse {

    private Long id;
    @JsonProperty("payment_code")
    private String paymentCode;
    @JsonProperty("payment_name")
    private String paymentName;
    @JsonSerialize(using = BigDecimalSerialize.class)
    @JsonProperty("payment_fee_price")
    private BigDecimal paymentFeePrice;
    @JsonProperty("payment_fee_percentage")
    private Double paymentFeePercentage;
    @JsonProperty("type_payment")
    private String typePayment;
    @JsonProperty("is_available")
    private Boolean isAvailable;
    @JsonProperty("is_active")
    private Boolean isActive;

}
