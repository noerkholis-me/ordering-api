package com.hokeba.mapping.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by hendriksaragih on 3/2/17.
 */

@JsonIgnoreProperties(ignoreUnknown=true)
public class MapMerchantPayment {
    @JsonProperty("order_no")
    private String orderNo;
    private String date;
    private String status;
    private Double amount;
    private Double retur;
    private Double shipping;
    @JsonProperty("sell_price")
    private Double sellPrice;
    @JsonProperty("real_price")
    private Double realPrice;
    private Double commision;
    @JsonProperty("receive_payment")
    private Double receivePayment;

    public MapMerchantPayment(){

    }

    public MapMerchantPayment(String orderNo, String date, String status, Double amount, Double retur, Double shipping, Double sellPrice, Double realPrice, Double commision, Double receivePayment) {
        this.orderNo = orderNo;
        this.date = date;
        this.status = status;
        this.amount = amount;
        this.retur = retur;
        this.shipping = shipping;
        this.sellPrice = sellPrice;
        this.realPrice = realPrice;
        this.commision = commision;
        this.receivePayment = receivePayment;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getRetur() {
        return retur;
    }

    public void setRetur(Double retur) {
        this.retur = retur;
    }

    public Double getShipping() {
        return shipping;
    }

    public void setShipping(Double shipping) {
        this.shipping = shipping;
    }

    public Double getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(Double sellPrice) {
        this.sellPrice = sellPrice;
    }

    public Double getRealPrice() {
        return realPrice;
    }

    public void setRealPrice(Double realPrice) {
        this.realPrice = realPrice;
    }

    public Double getCommision() {
        return commision;
    }

    public void setCommision(Double commision) {
        this.commision = commision;
    }

    public Double getReceivePayment() {
        return receivePayment;
    }

    public void setReceivePayment(Double receivePayment) {
        this.receivePayment = receivePayment;
    }
}
