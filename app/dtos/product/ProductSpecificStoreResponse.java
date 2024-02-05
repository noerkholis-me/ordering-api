package dtos.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.NoArgsConstructor;
import models.BrandMerchant;
import models.CategoryMerchant;
import models.SubCategoryMerchant;
import models.SubsCategoryMerchant;
import models.merchant.ProductMerchant;
import utils.BigDecimalSerialize;

import java.io.Serializable;
import java.math.BigDecimal;

@NoArgsConstructor
@Data
public class ProductSpecificStoreResponse implements Serializable {

    @JsonProperty("product_id")
    private Long productId;

    @JsonProperty("product_name")
    private String productName;

    @JsonProperty("status")
    private Boolean isActive;

    @JsonProperty("merchant_id")
    private Long merchantId;

    // ======== Detail ========== //
    @JsonProperty("product_detail")
    private ProductDetailResponse productDetail;

    private ProductSpecificStoreResponse.ProductStore productStore;

    private ProductSpecificStoreResponse.Brand brand;

    private ProductSpecificStoreResponse.Category category;

    private ProductSpecificStoreResponse.SubCategory subCategory;

    private ProductSpecificStoreResponse.SubsCategory subsCategory;


    public ProductSpecificStoreResponse(ProductMerchant productMerchant) {
        this.setProductId(productMerchant.id);
        this.setProductName(productMerchant.getProductName());
        this.setIsActive(productMerchant.getIsActive());
        this.setMerchantId(productMerchant.getMerchant().id);
    }

    @NoArgsConstructor
    @Data
    public static class ProductStore {
        @JsonProperty("id")
        private Long id;

        @JsonProperty("product_id")
        private Long productId;

        @JsonProperty("store_id")
        private Long storeId;

        @JsonProperty("store_name")
        private String storesName;

        @JsonProperty("store_price")
        @JsonSerialize(using = BigDecimalSerialize.class)
        private BigDecimal storePrice;

        @JsonProperty("discount_type")
        private String discountType;

        @JsonProperty("discount")
        private Double discount;

        @JsonProperty("final_price")
        @JsonSerialize(using = BigDecimalSerialize.class)
        private BigDecimal finalPrice;

        @JsonProperty("is_active")
        private Boolean isActive;

        @JsonProperty("is_deleted")
        private Boolean isDeleted;
        private Long stock;


        public ProductStore(models.ProductStore productStore) {
            this.setId(productStore.id);
            this.setStoreId(productStore.getStore().id);
            this.setProductId(productStore.getProductMerchant().id);
            this.setIsActive(productStore.isActive);
            this.setStorePrice(productStore.getStorePrice());
            this.setDiscountType(productStore.getDiscountType());
            this.setDiscount(productStore.getDiscount());
            this.setIsDeleted(productStore.isDeleted);
            this.setFinalPrice(productStore.getFinalPrice());
            this.setStoresName(productStore.getStore().getStoreName());
        }
    }

    @NoArgsConstructor
    @Data
    public static class Brand {

        @JsonProperty("brand_id")
        private Long brandId;

        @JsonProperty("brand_name")
        public String brandName;

        public Brand(BrandMerchant brand) {
            this.setBrandId(brand.id);
            this.setBrandName(brand.getBrandName());
        }
    }

    @NoArgsConstructor
    @Data
    public static class Category {
        @JsonProperty("category_id")
        private Long categoryId;

        @JsonProperty("category_name")
        public String categoryName;

        public Category(CategoryMerchant category) {
            this.setCategoryId(category.id);
            this.setCategoryName(category.getCategoryName());
        }
    }

    @NoArgsConstructor
    @Data
    public static class SubCategory {
        @JsonProperty("sub_category_id")
        private Long subCategoryId;

        @JsonProperty("sub_category_name")
        public String subCategoryName;

        public SubCategory(SubCategoryMerchant subCategory) {
            this.setSubCategoryId(subCategory.id);
            this.setSubCategoryName(subCategory.getSubcategoryName());
        }
    }

    @NoArgsConstructor
    @Data
    public static class SubsCategory {
        @JsonProperty("subs_category_id")
        private Long subsCategoryId;

        @JsonProperty("subs_category_name")
        public String subsCategoryName;

        public SubsCategory(SubsCategoryMerchant subsCategory) {
            this.setSubsCategoryId(subsCategory.id);
            this.setSubsCategoryName(subsCategory.getSubscategoryName());
        }
    }
}
