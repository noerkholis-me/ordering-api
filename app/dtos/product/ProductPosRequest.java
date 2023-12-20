package dtos.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProductPosRequest {
    // ======== Name and Category ========== //
    @JsonProperty("no_sku")
    private String noSKU;

    @JsonProperty("product_name")
    private String productName;

    @JsonProperty("category_id")
    private Long categoryId;

    @JsonProperty("sub_category_id")
    private Long subCategoryId;

    @JsonProperty("subs_category_id")
    private Long subsCategoryId;

    @JsonProperty("brand_id")
    private Long brandId;

    // ======== Detail ========== //
    @JsonProperty("product_detail")
    private ProductDetailResponse productDetailRequest;

    // ======== Description ========== //
    @JsonProperty("product_description")
    private ProductDescriptionResponse productDescriptionRequest;

    // ======== Product Store ========== //
    @JsonProperty("product_store")
    private ProductStoreResponse productStoreRequests;
}
