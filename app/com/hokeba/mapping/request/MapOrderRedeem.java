package com.hokeba.mapping.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hokeba.payment.kredivo.KredivoService;
import com.hokeba.payment.midtrans.MidtransService;

import java.util.List;

/**
 * Created by hendriksaragih on 4/11/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MapOrderRedeem {
    @JsonProperty("shipping_address")
    private Long shippingAddress;
    @JsonProperty("pickup_point")
    private Long pickupPoint;
    @JsonProperty("billing_address")
    private Long billingAddress;
    
    List<MapOrderSeller> sellers;

    @JsonProperty("loyalty")
    private Long loyalty;
    
    public Long getLoyalty() {
        return loyalty;
    }
    
    public void setLoyalty(Long loyalty) {
        this.loyalty = loyalty;
    }

	public Long getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(Long shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public Long getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(Long billingAddress) {
        this.billingAddress = billingAddress;
    }

	public List<MapOrderSeller> getSellers() {
        return sellers;
    }

    public void setSellers(List<MapOrderSeller> sellers) {
        this.sellers = sellers;
    }

    public Long getPickupPoint() {
        return pickupPoint;
    }

    public void setPickupPoint(Long pickupPoint) {
        this.pickupPoint = pickupPoint;
    }
}
