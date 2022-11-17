package dtos.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import utils.BigDecimalSerialize;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class ProductSpecificStoreResponse implements Serializable {

    @JsonProperty("product_id")
    private Long productId;

    // ======== Name and Category ========== //
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

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    @Builder
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
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    @Builder
    public static class Brand {

        @JsonProperty("brand_id")
        private Long brandId;

        @JsonProperty("brand_name")
        public String brandName;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    @Builder
    public static class Category {
        @JsonProperty("category_id")
        private Long categoryId;

        @JsonProperty("category_name")
        public String categoryName;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    @Builder
    public static class SubCategory {
        @JsonProperty("sub_category_id")
        private Long subCategoryId;

        @JsonProperty("sub_category_name")
        public String subCategoryName;

    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    @Builder
    public static class SubsCategory {
        @JsonProperty("subs_category_id")
        private Long subsCategoryId;

        @JsonProperty("subs_category_name")
        public String subsCategoryName;
    }
}
