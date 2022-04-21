package dtos.brand;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;
import utils.BigDecimalSerialize;

import java.io.Serializable;
import java.math.BigDecimal;
import dtos.product.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class BrandDetailResponse {

    @JsonProperty("id")
    public Long id;

    @JsonProperty("brand_name")
    public String brandName;

    @JsonProperty("image_web")
    public String imageWeb;

    @JsonProperty("image_mobile")
    public String imageMobile;

    @JsonProperty("is_deleted")
    public Boolean isDeleted;

    @JsonProperty("is_active")
    public Boolean isActive;

    @JsonProperty("merchant_id")
    private Long merchantId;

    @JsonProperty("icon_web")
    public String iconWeb;

    @JsonProperty("icon_mobile")
    public String iconMobile;

    @JsonProperty("brand_type")
    public String brandType;

    @JsonProperty("brand_description")
    public String brandDescription;

    @JsonProperty("category")
    private List<SubsCategoryMerchant> category;

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    @Builder
    public static class SubsCategoryMerchant {

        @JsonProperty("id")
        public Long id;

        @JsonProperty("subscategory_name")
        public String subscategoryName;

        @JsonProperty("image_web")
        public String imageWeb;

        @JsonProperty("image_mobile")
        public String imageMobile;

        @JsonProperty("is_deleted")
        public Boolean isDeleted;

        @JsonProperty("is_active")
        public Boolean isActive;

        @JsonProperty("products")
        private List<ProductMerchant> product;

        @AllArgsConstructor
        @NoArgsConstructor
        @Getter
        @Setter
        @Builder
        public static class ProductMerchant {
            @JsonProperty("product_id")
            private Long productId;

            @JsonProperty("product_name")
            private String productName;

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
            
            @JsonProperty("product_description")
            private ProductDescriptionResponse productDescription;

            @AllArgsConstructor
            @NoArgsConstructor
            @Getter
            @Setter
            @Builder
            public static class ProductDescriptionResponse {
                @JsonProperty("short_description")
                private String shortDescription;

                @JsonProperty("long_description")
                private String longDescription;
            }
        }
    }

}
