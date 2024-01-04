package dtos.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import models.merchant.ProductMerchantDetail;
import utils.BigDecimalSerialize;

import java.io.Serializable;
import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ProductDetailResponse implements Serializable {

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

    @JsonProperty("product_image_1")
    private String productImage1;

    @JsonProperty("product_image_2")
    private String productImage2;

    @JsonProperty("product_image_3")
    private String productImage3;

    @JsonProperty("product_image_4")
    private String productImage4;

    @JsonProperty("stock")
    private Long stock;

    public ProductDetailResponse(ProductMerchantDetail productMerchantDetail, models.ProductStore productStore) {
        this.setProductType(productMerchantDetail.getProductType());
        this.setIsCustomizable(productMerchantDetail.getIsCustomizable());
        this.setProductPrice(productStore != null ? productStore.getStorePrice() : productMerchantDetail.getProductPrice());
        this.setDiscountType(productStore != null ? productStore.getDiscountType() : productMerchantDetail.getDiscountType());
        this.setStock(productStore.getStock());
        this.setDiscount(productStore != null ? productStore.getDiscount() : productMerchantDetail.getDiscount());
        this.setProductPriceAfterDiscount(productStore != null ? productStore.getFinalPrice() : productMerchantDetail.getProductPriceAfterDiscount());
        this.setProductImageMain(productMerchantDetail.getProductImageMain());
        this.setProductImage1(productMerchantDetail.getProductImage1());
        this.setProductImage2(productMerchantDetail.getProductImage2());
        this.setProductImage3(productMerchantDetail.getProductImage3());
        this.setProductImage4(productMerchantDetail.getProductImage4());
    }
}
