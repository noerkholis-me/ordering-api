package dtos.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import utils.BigDecimalSerialize;

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
    @JsonSerialize(using = BigDecimalSerialize.class)
    private BigDecimal taxPrice;
    @JsonProperty("delivery_fee")
    @JsonSerialize(using = BigDecimalSerialize.class)
    private BigDecimal deliveryFee;
    @JsonProperty("service_price")
    @JsonSerialize(using = BigDecimalSerialize.class)
    private BigDecimal servicePrice;
    @JsonProperty("payment_fee_type")
    private String paymentFeeType;
    @JsonProperty("payment_fee_customer")
    @JsonSerialize(using = BigDecimalSerialize.class)
    private BigDecimal paymentFeeCustomer;
    @JsonProperty("platform_fee")
    @JsonSerialize(using = BigDecimalSerialize.class)
    private BigDecimal platformFee;
    @JsonProperty("payment_fee_owner")
    @JsonSerialize(using = BigDecimalSerialize.class)
    private BigDecimal paymentFeeOwner;
    @JsonProperty("total_amount")
    @JsonSerialize(using = BigDecimalSerialize.class)
    private BigDecimal totalAmount;
}
