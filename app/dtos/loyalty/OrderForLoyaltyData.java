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
public class OrderForLoyaltyData  {

    @JsonProperty("subs_category_id")
    private Long subsCategoryId;

    @JsonProperty("subs_category_name")
    private String subsCategoryName;

    @JsonProperty("product_name")
    private String productName;

    // set to final price from product store
    @JsonSerialize(using = BigDecimalSerialize.class)
    @JsonProperty("product_price")
    private BigDecimal productPrice;

    @JsonSerialize(using = BigDecimalSerialize.class)
    @JsonProperty("sub_total")
    private BigDecimal subTotal;

}
