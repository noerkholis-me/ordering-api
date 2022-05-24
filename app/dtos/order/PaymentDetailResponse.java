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
public class PaymentDetailResponse {

    @JsonProperty("payment_type")
    private String paymentType;
    @JsonProperty("payment_channel")
    private String paymentChannel;
    @JsonProperty("bank_code")
    private String bankCode;
    @JsonProperty("tax_percentage")
    private Double taxPercentage;
    @JsonProperty("service_percentage")
    private Double servicePercentage;
    @JsonProperty("tax_price")
    private BigDecimal taxPrice;
    @JsonProperty("service_price")
    private BigDecimal servicePrice;
    @JsonProperty("payment_fee_type")
    private String paymentFeeType;
    @JsonProperty("payment_fee_customer")
    private BigDecimal paymentFeeCustomer;
    @JsonProperty("payment_fee_owner")
    private BigDecimal paymentFeeOwner;
    @JsonProperty("total_amount")
    private BigDecimal totalAmount;



}
