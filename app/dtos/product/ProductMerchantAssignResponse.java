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
public class ProductMerchantAssignResponse implements Serializable {

    // private Long id;

    @JsonProperty("product_id")
    private Long productId;
    
    @JsonProperty("product_name")
    private String productName;

    @JsonProperty("merchant_id")
    private Long merchantId;

    private List<ProductAddOn> productAddOn;

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    @Builder
    public static class ProductAddOn {

        @JsonProperty("product_assign_id")
        private Long productAssignId;

        @JsonProperty("product_name")
        private String productName;

        @JsonProperty("product_type")
        private String productType;
    }  

}
