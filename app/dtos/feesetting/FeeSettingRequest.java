package dtos.feesetting;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FeeSettingRequest {

    private Double tax;
    private Double service;
    @JsonProperty("platform_fee_type")
    private String platformFeeType;
    @JsonProperty("payment_fee_type")
    private String paymentFeeType;
    @JsonProperty("platform_fee")
    private BigDecimal platformFee;
    @JsonProperty("payment_fee")
    private BigDecimal paymentFee;
    @JsonProperty("updated_by")
    private String updatedBy;

}
