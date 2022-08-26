package dtos.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;
import models.*;
import utils.BigDecimalSerialize;

import java.io.Serializable;
import java.math.BigDecimal;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class ProductAdditionalMerchantResponse {

    private Long id;
    
    @JsonProperty("no_sku")
    private String noSKU;

    @JsonProperty("product_name")
    private String productName;

    @JsonProperty("merchant_id")
    private Long merchantId;

}
