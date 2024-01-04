package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hokeba.util.Constant;
import dtos.product.ProductPosRequest;
import dtos.product.ProductStoreResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import models.merchant.ProductMerchant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;

@NoArgsConstructor
@Entity
@Getter
@Setter
public class ProductStore extends BaseModel {
    private static final long serialVersionUID = 1L;

    @JsonProperty("discount_type")
    public String discountType;

    @JsonProperty("discount")
    public Double discount;

    @JsonProperty("store_price")
    public BigDecimal storePrice;

    @JsonProperty("final_price")
    public BigDecimal finalPrice;

    @JsonProperty("stock")
    public Long stock;

    @JsonProperty("is_active")
    @Column(name = "is_active")
    public boolean isActive;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "merchant_id", referencedColumnName = "id")
    public Merchant merchant;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    public ProductMerchant productMerchant;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "store_id", referencedColumnName = "id")
    public Store store;

    @Column(name = "product_store_qr_code")
    public String productStoreQrCode;

    public ProductStore(Merchant merchant, Store store, ProductMerchant productMerchant, ProductStoreResponse productStoreRequest, Boolean isActive) {
        this.setStore(store);
        this.setProductMerchant(productMerchant);
        this.setMerchant(merchant);
        this.setActive(isActive != null || productStoreRequest.getIsActive());
        this.setStorePrice(productStoreRequest.getStorePrice());
        this.setStock(productStoreRequest.getStock());
        this.setProductStoreQrCode(Constant.getInstance().getFrontEndUrl().concat(store.storeCode + "/" + store.id + "/" + merchant.id + "/product/" + productMerchant.id + "/detail"));
        if (productStoreRequest.getDiscountType() != null) {
            this.setDiscountType(productStoreRequest.getDiscountType());
        }
        if (productStoreRequest.getDiscount() != null) {
            this.setDiscount(productStoreRequest.getDiscount());
        }
        if (productStoreRequest.getFinalPrice() != null) {
            this.setFinalPrice(productStoreRequest.getFinalPrice());
        }
    }

    public ProductStore(Merchant merchant, Store store, ProductMerchant productMerchant, ProductPosRequest productRequest, Boolean isActive) {
        this.setStore(store);
        this.setProductMerchant(productMerchant);
        this.setMerchant(merchant);
        this.setActive(isActive);
        this.setStorePrice(productRequest.getProductStoreRequests().getStorePrice());
        this.setProductStoreQrCode(Constant.getInstance().getFrontEndUrl().concat(store.storeCode + "/" + store.id + "/" + merchant.id + "/product/" + productMerchant.id + "/detail"));
        if (productRequest.getProductStoreRequests().getDiscountType() != null) {
            this.setDiscountType(productRequest.getProductStoreRequests().getDiscountType());
        }
        if (productRequest.getProductStoreRequests().getDiscount() != null) {
            this.setDiscount(productRequest.getProductStoreRequests().getDiscount());
        }
        if (productRequest.getProductStoreRequests().getFinalPrice() != null) {
            this.setFinalPrice(productRequest.getProductStoreRequests().getFinalPrice());
        }
    }

    public void setProductStore(ProductStore productStore, Merchant merchant, Store store, ProductMerchant productMerchant, ProductStoreResponse productStoreRequest) {
        productStore.setStore(store);
        productStore.setProductMerchant(productMerchant);
        productStore.setMerchant(merchant);
        productStore.setActive(productStoreRequest.getIsActive());
        productStore.setStock(productStoreRequest.getStock());
        productStore.setStorePrice(productStoreRequest.getStorePrice());
        productStore.setProductStoreQrCode(Constant.getInstance().getFrontEndUrl().concat(store.storeCode + "/" + store.id + "/" + merchant.id + "/product/" + productMerchant.id + "/detail"));
        if (productStoreRequest.getDiscountType() != null) {
            productStore.setDiscountType(productStoreRequest.getDiscountType());
        }
        if (productStoreRequest.getDiscount() != null) {
            productStore.setDiscount(productStoreRequest.getDiscount());
        }
        if (productStoreRequest.getFinalPrice() != null) {
            productStore.setFinalPrice(productStoreRequest.getFinalPrice());
        }
    }

    public void setProductStore(ProductStore productStore, Merchant merchant, Store store, ProductMerchant productMerchant, ProductPosRequest productRequest) {
        productStore.setStore(store);
        productStore.setProductMerchant(productMerchant);
        productStore.setMerchant(merchant);
        productStore.setActive(productRequest.getProductStoreRequests().getIsActive());
        productStore.setStorePrice(productRequest.getProductStoreRequests().getStorePrice());
        productStore.setProductStoreQrCode(Constant.getInstance().getFrontEndUrl().concat(store.storeCode + "/" + store.id + "/" + merchant.id + "/product/" + productMerchant.id + "/detail"));
        if (productRequest.getProductStoreRequests().getDiscountType() != null) {
            productStore.setDiscountType(productRequest.getProductStoreRequests().getDiscountType());
        }
        if (productRequest.getProductStoreRequests().getDiscount() != null) {
            productStore.setDiscount(productRequest.getProductStoreRequests().getDiscount());
        }
        if (productRequest.getProductStoreRequests().getFinalPrice() != null) {
            productStore.setFinalPrice(productRequest.getProductStoreRequests().getFinalPrice());
        }
    }
}