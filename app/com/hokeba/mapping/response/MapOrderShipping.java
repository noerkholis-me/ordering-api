package com.hokeba.mapping.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by nugraha on 5/25/17.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class MapOrderShipping {
    @JsonProperty("shipping_address")
    public MapAddress shippingAddress;
    @JsonProperty("billing_address")
    public MapAddress billingAddress;
    private MapNameCode[] regions;
    private MapNameCode[] township;

    private List<MapOrderShippingSeller> sellers;

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

    public List<MapOrderShippingSeller> getSellers() {
        return sellers;
    }

    public void setSellers(List<MapOrderShippingSeller> sellers) {
        this.sellers = sellers;
    }

    public MapNameCode[] getRegions() {
        return regions;
    }

    public void setRegions(MapNameCode[] regions) {
        this.regions = regions;
    }

    public MapNameCode[] getTownship() {
        return township;
    }

    public void setTownship(MapNameCode[] township) {
        this.township = township;
    }
}