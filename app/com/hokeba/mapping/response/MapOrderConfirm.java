package com.hokeba.mapping.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by nugraha on 5/25/17.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class MapOrderConfirm {
    @JsonProperty("order_number")
    public String orderNo;
    @JsonProperty("expired")
    public String expired;
    @JsonProperty("start")
    public String start;
    @JsonProperty("tranfer_amount")
    public Double tranferAmount;
    public Double shipping;
    public MapBank bank;
    @JsonProperty("shipment_address")
    public MapAddress shipmentAddress;
    @JsonProperty("billing_address")
    public MapAddress billingAddress;
    @JsonProperty("sellers")
    public List<MapOrderSeller> sellers;

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Double getTranferAmount() {
        return tranferAmount;
    }

    public void setTranferAmount(Double tranferAmount) {
        this.tranferAmount = tranferAmount;
    }

    public String getExpired() {
        return expired;
    }

    public void setExpired(String expired) {
        this.expired = expired;
    }

    public Double getShipping() {
        return shipping;
    }

    public void setShipping(Double shipping) {
        this.shipping = shipping;
    }

    public MapBank getBank() {
        return bank;
    }

    public void setBank(MapBank bank) {
        this.bank = bank;
    }

    public MapAddress getShipmentAddress() {
        return shipmentAddress;
    }

    public void setShipmentAddress(MapAddress shipmentAddress) {
        this.shipmentAddress = shipmentAddress;
    }

    public MapAddress getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(MapAddress billingAddress) {
        this.billingAddress = billingAddress;
    }

    public List<MapOrderSeller> getSellers() {
        return sellers;
    }

    public void setSellers(List<MapOrderSeller> sellers) {
        this.sellers = sellers;
    }
}