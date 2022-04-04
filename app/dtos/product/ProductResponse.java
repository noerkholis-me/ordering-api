package dtos.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class ProductResponse implements Serializable {

    @JsonProperty("product_id")
    private Long productId;

    // ======== Name and Category ========== //
    @JsonProperty("product_name")
    private String productName;
    @JsonProperty("category")
    private CategoryResponse category;
    @JsonProperty("sub_category")
    private SubCategoryResponse subCategory;
    @JsonProperty("brand")
    private BrandResponse brand;

    @JsonProperty("status")
    private Boolean isActive;

    // ======== Detail ========== //
    @JsonProperty("product_detail")
    private ProductDetailResponse productDetail;

    // ======== Description ========== //
    @JsonProperty("product_description")
    private ProductDescriptionResponse productDescription;

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter @Setter
    @Builder
    public static class CategoryResponse {
        private Long id;
        @JsonProperty("category_name")
        private String categoryName;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter @Setter
    @Builder
    public static class SubCategoryResponse {
        private Long id;
        @JsonProperty("sub_category_name")
        private String subCategoryName;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter @Setter
    @Builder
    public static class BrandResponse {
        private Long id;
        @JsonProperty("brand_name")
        private String brandName;
    }

}
