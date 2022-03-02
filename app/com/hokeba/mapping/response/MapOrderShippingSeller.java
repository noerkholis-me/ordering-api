package com.hokeba.mapping.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by nugraha on 5/25/17.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class MapOrderShippingSeller {
    @JsonProperty("seller_name")
    public String sellerName;
    @JsonProperty("seller_id")
    public Long sellerId;
    @JsonProperty("seller_type")
    public String sellerType;

    private List<MapOrderShippingCourier> couriers;
    private List<MapOrderSellerProduct> items;

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    public String getSellerType() {
        return sellerType;
    }

    public void setSellerType(String sellerType) {
        this.sellerType = sellerType;
    }

    public List<MapOrderShippingCourier> getCouriers() {
        return couriers;
    }

    public void setCouriers(List<MapOrderShippingCourier> couriers) {
        this.couriers = couriers;
    }

    public List<MapOrderSellerProduct> getItems() {
        return items;
    }

    public void setItems(List<MapOrderSellerProduct> items) {
        this.items = items;
    }
}