package dtos.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;
import utils.BigDecimalSerialize;

import java.io.Serializable;
import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Builder
public class ProductDetailResponse implements Serializable {

    @JsonProperty("product_type")
    private String productType;
    @JsonProperty("is_customizable")
    private Boolean isCustomizable;
    @JsonProperty("product_price")
    @JsonSerialize(using = BigDecimalSerialize.class)
    private BigDecimal productPrice;
    @JsonProperty("discount_type")
    private String discountType;
    @JsonProperty("discount")
    private Double discount;
    @JsonProperty("product_price_after_discount")
    @JsonSerialize(using = BigDecimalSerialize.class)
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
