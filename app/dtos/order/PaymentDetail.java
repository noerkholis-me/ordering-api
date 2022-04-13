package dtos.order;

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
public class PaymentDetail {
    @JsonProperty("payment_type")
    private String paymentType;
    @JsonProperty("payment_channel")
    private String paymentChannel;
    @JsonProperty("platform_fee")
    private BigDecimal platformFee;
    @JsonProperty("payment_fee")
    private BigDecimal paymentFee;
    @JsonProperty("tax_fee")
    private BigDecimal taxFee;
    @JsonProperty("service_fee")
    private BigDecimal serviceFee;
    @JsonProperty("total_amount")
    private BigDecimal totalAmount;
}
