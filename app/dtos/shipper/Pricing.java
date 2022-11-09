package dtos.shipper;

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
public class Pricing {
    @JsonProperty("logistic")
    private Logistic logistic;

    @JsonProperty("rate")
    private Rates rate;

    @JsonProperty("weight")
    private Double weight;

    @JsonProperty("volume")
    private Double volume;

    @JsonProperty("volumeWeight")
    private Double volumeWeight;

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("finalWeight")
    private Double finalWeight;

    @JsonProperty("final_price")
    private Double finalPrice;

    @JsonProperty("insurance_fee")
    private Double insuranceFee;

    @JsonProperty("must_use_insurance")
    private Boolean mustUseInsurance;

    @JsonProperty("liability_value")
    private Double liabilityValue;

    @JsonProperty("discount")
    private Double discount;

    @JsonProperty("discount_value")
    private Double discountValue;

    @JsonProperty("discounted_price")
    private Double discountedPrice;

    @JsonProperty("min_day")
    private Integer minDay;

    @JsonProperty("max_day")
    private Integer maxDay;

    @JsonProperty("unit_price")
    private Double unitPrice;

    @JsonProperty("total_price")
    private Double totalPrice;

    @JsonProperty("insurance_applied")
    private Boolean insuranceApplied;

    @JsonProperty("base_price")
    private Double basePrice;

    @JsonProperty("surcharge_fee")
    private Double surchargeFee;
}
