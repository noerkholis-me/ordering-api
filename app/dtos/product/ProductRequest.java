package dtos.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
    @JsonProperty("product_detail")
    private ProductDetailResponse productDetailRequest;

    // ======== Description ========== //
    @JsonProperty("product_description")
    private ProductDescriptionResponse productDescriptionRequest;



}
