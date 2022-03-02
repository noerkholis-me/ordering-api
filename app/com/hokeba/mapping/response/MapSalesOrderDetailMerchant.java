package com.hokeba.mapping.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by hendriksaragih on 4/8/17.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class MapSalesOrderDetailMerchant {
    public Long id;
    public String name;
    public String sku;
    public Double price;
    public Integer quantity;
    @JsonProperty("image_url")
    public String imageUrl;
    @JsonProperty("discount_amount")
    public Double discountAmount;
    public String currency;
    @JsonProperty("discount_persen")
    public Double discountPersen;
    @JsonProperty("total")
    public Double total;
    @JsonProperty("fee_price")
    public Double feePrice;
    @JsonProperty("size_id")
    private Long sizeId;
    @JsonProperty("size")
    private String size;
    @JsonProperty("color")
    private String color;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(Double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Double getDiscountPersen() {
        return discountPersen;
    }

    public void setDiscountPersen(Double discountPersen) {
        this.discountPersen = discountPersen;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public Double getFeePrice() {
        return feePrice;
    }

    public void setFeePrice(Double feePrice) {
        this.feePrice = feePrice;
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
}