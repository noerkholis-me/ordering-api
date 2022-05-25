package dtos.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PaymentServiceRequest {

    @JsonProperty("payment_type")
    private String paymentType;
    @JsonProperty("payment_channel")
    private String paymentChannel;
    @JsonProperty("total_amount")
    private BigDecimal totalAmount;
    @JsonProperty("bank_code")
    private String bankCode;

}
