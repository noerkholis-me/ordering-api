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
public class ProductKiosKResponse implements Serializable {

    @JsonProperty("product_id")
    private Long productId;

    @JsonProperty("no_sku")
    private String noSKU;

    @JsonProperty("product_name")
    private String productName;

    @JsonProperty("status")
    private Boolean isActive;

    @JsonProperty("merchant_id")
    private Long merchantId;

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
    
    @JsonProperty("short_description")
    private String shortDescription;
    
    @JsonProperty("long_description")
    private String longDescription;

    @JsonProperty("brand")
    private ProductResponse.BrandResponse brand;

}
