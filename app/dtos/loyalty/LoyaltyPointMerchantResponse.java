package dtos.loyalty;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.annotation.JsonIgnore;
import utils.BigDecimalSerialize;


import java.math.BigDecimal;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LoyaltyPointMerchantResponse  {

    private Long id;

    @JsonProperty("usage_type")
    private String usageType;

    @JsonSerialize(using = BigDecimalSerialize.class)
    @JsonProperty("loyalty_usage_value")
    private BigDecimal loyaltyUsageValue;

    @JsonSerialize(using = BigDecimalSerialize.class)
    @JsonProperty("max_loyalty_usage_value")
    private BigDecimal maxLoyaltyUsageValue;

    @JsonProperty("cashback_type")
    private String cashbackType;

    @JsonSerialize(using = BigDecimalSerialize.class)
    @JsonProperty("cashback_value")
    private BigDecimal cashbackValue;

    @JsonSerialize(using = BigDecimalSerialize.class)
    @JsonProperty("max_cashback_value")
    private BigDecimal maxCashbackValue;

    @JsonProperty("is_deleted")
    private Boolean isDeleted;

    @JsonProperty("subs_category_id")
    private Long subsCategoryId;

    @JsonProperty("subs_category_name")
    private String subsCategoryName;

    @JsonProperty("merchant_id")
    private Long merchantId;

}
