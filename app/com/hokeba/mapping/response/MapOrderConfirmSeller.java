package com.hokeba.mapping.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by hendriksaragih on 5/25/17.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class MapOrderConfirmSeller {
    @JsonProperty("seller_name")
    public String sellerName;
    @JsonProperty("seller_id")
    public Long sellerId;
    @JsonProperty("seller_type")
    public String sellerType;
    private MapOrderShippingCourier courier;
    private MapOrderShippingCourierService service;
    private List<MapOrderSellerProduct> items;
    @JsonProperty("sub_total")
    private Double subTotal;

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

    public MapOrderShippingCourier getCouriers() {
        return courier;
    }

    public void setCouriers(MapOrderShippingCourier courier) {
        this.courier = courier;
    }

    public List<MapOrderSellerProduct> getItems() {
        return items;
    }

    public void setItems(List<MapOrderSellerProduct> items) {
        this.items = items;
    }

    public MapOrderShippingCourier getCourier() {
        return courier;
    }

    public void setCourier(MapOrderShippingCourier courier) {
        this.courier = courier;
    }

    public MapOrderShippingCourierService getService() {
        return service;
    }

    public void setService(MapOrderShippingCourierService service) {
        this.service = service;
    }

    public Double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(Double subTotal) {
        this.subTotal = subTotal;
    }
}