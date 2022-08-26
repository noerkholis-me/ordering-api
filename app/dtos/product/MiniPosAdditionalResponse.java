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
public class MiniPosAdditionalResponse implements Serializable {
        @JsonProperty("product_type")
        private String productType;

        private List<ProductAddOn> productAddOn;
        @AllArgsConstructor
        @NoArgsConstructor
        @Getter
        @Setter
        @Builder
        public static class ProductAddOn {
            @JsonProperty("product_id")
            private Long productId;
            @JsonProperty("product_assign_id")
            private Long productAssignId;
            @JsonProperty("no_sku")
            private String noSKU;
            @JsonProperty("product_name")
            private String productName;
            @JsonProperty("product_price")
            private BigDecimal productPrice;
            @JsonProperty("discount_type")
            private String discountType;
            @JsonProperty("discount")
            private Double discount;
            @JsonProperty("product_price_after_discount")
            private BigDecimal productPriceAfterDiscount;
            @JsonProperty("product_image_main")
            private String productImageMain;
            @JsonProperty("product_image_1")
            private String productImage1;
            @JsonProperty("product_image_2")
            private String productImage2;
            @JsonProperty("product_image_3")
            private String productImage3;
            @JsonProperty("product_image_4")
            private String productImage4;
        }
}
