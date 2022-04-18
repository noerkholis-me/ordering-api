package dtos.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;
import models.*;
import utils.BigDecimalSerialize;

import java.io.Serializable;
import java.math.BigDecimal;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class ProductResponseStore implements Serializable {

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
    private List<ProductStore> productStore;

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter @Setter
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
    

}
