package dtos.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class ProductRequest {

    // ======== Name and Category ========== //
    @JsonProperty("product_name")
    private String productName;
    @JsonProperty("category_id")
    private Long categoryId;
    @JsonProperty("sub_category_id")
    private Long subCategoryId;
    @JsonProperty("brand_id")
    private Long brandId;

    // ======== Detail ========== //
    @JsonProperty("product_type")
    private String productType;
    @JsonProperty("is_customizable")
    private Boolean isCustomizable;
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

    // ======== Description ========== //
    @JsonProperty("short_description")
    private String shortDescription;
    @JsonProperty("long_description")
    private String longDescription;



}
