package dtos.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;
import models.*;
import models.merchant.ProductMerchant;
import utils.BigDecimalSerialize;

import java.io.Serializable;
import java.math.BigDecimal;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductResponse implements Serializable {

    @JsonProperty("product_id")
    private Long productId;

    // ======== Name and Category ========== //
    @JsonProperty("no_sku")
    private String noSKU;

    @JsonProperty("product_name")
    private String productName;

    @JsonProperty("category")
    private CategoryResponse category;

    @JsonProperty("sub_category")
    private SubCategoryResponse subCategory;

    @JsonProperty("subs_category")
    private SubsCategoryResponse subsCategory;

    @JsonProperty("brand")
    private BrandResponse brand;

    @JsonProperty("status")
    private Boolean isActive;

    @JsonProperty("merchant_id")
    private Long merchantId;

    // ======== Detail ========== //
    @JsonProperty("product_detail")
    private ProductDetailResponse productDetail;

    // ======== Description ========== //
    @JsonProperty("product_description")
    private ProductDescriptionResponse productDescription;

    private List<ProductResponseStore.ProductStore> productStore;

    public ProductResponse(ProductMerchant productMerchant) {
        this.setProductId(productMerchant.id);
        this.setNoSKU(productMerchant.getNoSKU());
        this.setProductName(productMerchant.getProductName());
        this.setIsActive(productMerchant.getIsActive());
        this.setMerchantId(productMerchant.getMerchant().id);
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    public static class CategoryResponse {
        private Long id;

        @JsonProperty("category_name")
        private String categoryName;

        public CategoryResponse(CategoryMerchant category) {
            this.setId(category.id);
            this.setCategoryName(category.getCategoryName());
        }

    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    public static class SubCategoryResponse {
        private Long id;

        @JsonProperty("sub_category_name")
        private String subCategoryName;

        public SubCategoryResponse(SubCategoryMerchant subCategory) {
            this.setId(subCategory.id);
            this.setSubCategoryName(subCategory.getSubcategoryName());
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    public static class SubsCategoryResponse {
        private Long id;

        @JsonProperty("subs_category_name")
        private String subsCategoryName;

        public SubsCategoryResponse(SubsCategoryMerchant subsCategory) {
            this.setId(subsCategory.id);
            this.setSubsCategoryName(subsCategory.getSubscategoryName());
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @Builder
    public static class BrandResponse {
        private Long id;

        @JsonProperty("brand_name")
        private String brandName;

        public BrandResponse(BrandMerchant brand) {
            this.setId(brand.id);
            this.setBrandName(brand.getBrandName());
        }
    }

}
