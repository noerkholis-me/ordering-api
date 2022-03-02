package com.hokeba.mapping.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by hendriksaragih on 4/8/17.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class MapProductMerchantList {
    public String id;
    public String name;
    public String sku;
    @JsonProperty("created_date")
    public String createdDate;
    public Double price;
    @JsonProperty("promo_price")
    public Double promoPrice;
    @JsonProperty("fee_price")
    public Double feePrice;
    @JsonProperty("is_active")
    public Boolean isActive;
    public Long stock;
    @JsonProperty("retur_qty")
    public Integer returQty;
    public String reason;
//    public List<MapProductNotes> note;

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getPromoPrice() {
        return promoPrice;
    }

    public void setPromoPrice(Double promoPrice) {
        this.promoPrice = promoPrice;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Long getStock() {
        return stock;
    }

    public void setStock(Long stock) {
        this.stock = stock;
    }

//    public List<MapProductNotes> getNote() {
//        return note;
//    }
//
//    public void setNote(List<MapProductNotes> note) {
//        this.note = note;
//    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Integer getReturQty() {
        return returQty;
    }

    public void setReturQty(Integer returQty) {
        this.returQty = returQty;
    }
}