package dtos.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import models.ProductStore;
import models.Store;
import utils.BigDecimalSerialize;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductResponseStore implements Serializable {

    @JsonProperty("product_id")
    private Long productId;

    @JsonProperty("product_name")
    private String productName;

    @JsonProperty("status")
    private Boolean isActive;

    @JsonProperty("merchant_id")
    private Long merchantId;

    @JsonProperty("product_detail")
    private ProductDetailResponse productDetail;

    private List<ProductStore> productStore;

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
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


}
