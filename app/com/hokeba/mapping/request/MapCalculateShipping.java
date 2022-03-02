package com.hokeba.mapping.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by hendriksaragih on 4/11/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MapCalculateShipping {
    @JsonProperty("merchant_id")
    private Long merchantId;
    @JsonProperty("vendor_id")
    private Long vendorId;
    @JsonProperty("shipping_id")
    private Long shippingId;
    @JsonProperty("pickup_point")
    private Long pickupPoint;

    List<MapOrderDetail> items;

    public Long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }

    public Long getVendorId() {
        return vendorId;
    }

    public void setVendorId(Long vendorId) {
        this.vendorId = vendorId;
    }

    public List<MapOrderDetail> getItems() {
        return items;
    }

    public void setItems(List<MapOrderDetail> items) {
        this.items = items;
    }

    public Long getShippingId() {
        return shippingId;
    }

    public void setShippingId(Long shippingId) {
        this.shippingId = shippingId;
    }

    public Long getPickupPoint() {
        return pickupPoint;
    }

    public void setPickupPoint(Long pickupPoint) {
        this.pickupPoint = pickupPoint;
    }
}
