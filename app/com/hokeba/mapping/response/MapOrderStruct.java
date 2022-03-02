package com.hokeba.mapping.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by nugraha on 5/25/17.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class MapOrderStruct {
    @JsonProperty("order_number")
    public String orderNo;
    public MapBank bank;
    @JsonProperty("payment_method")
    private String paymentMethod;
    @JsonProperty("shipment_address")
    public MapAddress shipmentAddress;
    @JsonProperty("billing_address")
    public MapAddress billingAddress;
    @JsonProperty("details")
    public List<MapOrderDetails> details;

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
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

    public List<MapOrderDetails> getDetails() {
        return details;
    }

    public void setDetails(List<MapOrderDetails> details) {
        this.details = details;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}