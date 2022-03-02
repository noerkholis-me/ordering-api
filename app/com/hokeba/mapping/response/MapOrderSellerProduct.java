package com.hokeba.mapping.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hokeba.mapping.request.MapVoucherCode;

import java.util.List;

/**
 * Created by nugraha on 5/25/17.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class MapOrderSellerProduct {
    @JsonProperty("product_name")
    public String productName;
    @JsonProperty("product_id")
    public Long productId;
    @JsonProperty("product_variance_id")
    public Long productVarianceId;
    public double price;
    public int quantity;
    public String sku;
    @JsonProperty("discount_persen")
    public Double discountPersen;
    @JsonProperty("price_display")
    public Double priceDisplay;
    @JsonProperty("discount_amount")
    public Double discountAmount;
    @JsonProperty("image_url")
    public String imageUrl;
    @JsonProperty("voucher_amount")
    public Double voucherAmount;
    @JsonProperty("vouchers")
    public List<MapVoucherCode> vouchers;
    @JsonProperty("sizes")
    public String sizes;
    @JsonProperty("bee_boxes")
    public String beeBoxes;
    @JsonProperty("size_id")
    private Long sizeId;
    @JsonProperty("size")
    private String size;
    @JsonProperty("color")
    private String color;
    
    @JsonProperty("loyalty_eligible_use")
    public Long loyaltyEligibleUse;
    @JsonProperty("loyalty_eligible_earn")
    public Long loyaltyEligibleEarn;

	public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Double getDiscountPersen() {
        return discountPersen;
    }

    public void setDiscountPersen(Double discountPersen) {
        this.discountPersen = discountPersen;
    }

    public Double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(Double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Double getVoucherAmount() {
        return voucherAmount;
    }

    public void setVoucherAmount(Double voucherAmount) {
        this.voucherAmount = voucherAmount;
    }

    public List<MapVoucherCode> getVouchers() {
        return vouchers;
    }

    public void setVouchers(List<MapVoucherCode> vouchers) {
        this.vouchers = vouchers;
    }

    public Double getPriceDisplay() {
        return priceDisplay;
    }

    public void setPriceDisplay(Double priceDisplay) {
        this.priceDisplay = priceDisplay;
    }

    public String getSizes() {
        return sizes;
    }

    public void setSizes(String sizes) {
        this.sizes = sizes;
    }

    public String getBeeBoxes() {
        return beeBoxes;
    }

    public void setBeeBoxes(String beeBoxes) {
        this.beeBoxes = beeBoxes;
    }

    public Long getSizeId() {
        return sizeId;
    }

    public void setSizeId(Long sizeId) {
        this.sizeId = sizeId;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Long getLoyaltyEligibleUse() {
		return loyaltyEligibleUse;
	}

	public void setLoyaltyEligibleUse(Long loyaltyEligibleUse) {
		this.loyaltyEligibleUse = loyaltyEligibleUse;
	}

	public Long getLoyaltyEligibleEarn() {
		return loyaltyEligibleEarn;
	}

	public void setLoyaltyEligibleEarn(Long loyaltyEligibleEarn) {
		this.loyaltyEligibleEarn = loyaltyEligibleEarn;
	}
}