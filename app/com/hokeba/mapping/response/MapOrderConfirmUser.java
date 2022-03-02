package com.hokeba.mapping.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by hendriksaragih on 5/25/17.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class MapOrderConfirmUser {
    @JsonProperty("shipping_address")
    public MapAddress shippingAddress;
    @JsonProperty("billing_address")
    public MapAddress billingAddress;
    @JsonProperty("pickup_point")
    private MapCourierLocation pickupPoint;
    private Double total;
    private Integer item;
    @JsonProperty("allow_cod")
    private boolean allowCod;
    private List<MapOrderConfirmSeller> sellers;

    public MapAddress getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(MapAddress shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public MapAddress getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(MapAddress billingAddress) {
        this.billingAddress = billingAddress;
    }

    public List<MapOrderConfirmSeller> getSellers() {
        return sellers;
    }

    public void setSellers(List<MapOrderConfirmSeller> sellers) {
        this.sellers = sellers;
    }

    public MapCourierLocation getPickupPoint() {
        return pickupPoint;
    }

    public void setPickupPoint(MapCourierLocation pickupPoint) {
        this.pickupPoint = pickupPoint;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public Integer getItem() {
        return item;
    }

    public void setItem(Integer item) {
        this.item = item;
    }

    public boolean isAllowCod() {
        return allowCod;
    }

    public void setAllowCod(boolean allowCod) {
        this.allowCod = allowCod;
    }
}