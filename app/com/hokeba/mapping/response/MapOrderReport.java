package com.hokeba.mapping.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by hendriksaragih on 3/2/17.
 */

@JsonIgnoreProperties(ignoreUnknown=true)
public class MapOrderReport {
    @JsonProperty("order_number")
    private String orderNo;
    @JsonProperty("seller_name")
    private String sellerName;
    @JsonProperty("total")
    private Double total;
    @JsonProperty("status")
    private String status;
    @JsonProperty("customer_name")
    private String customerName;
    @JsonProperty("payment_type")
    private String paymentType;
    private Double shipping;
    @JsonProperty("sell_price")
    private Double sellPrice;
    @JsonProperty("real_price")
    private Double realPrice;
    private Double commision;
    @JsonProperty("receive_payment")
    private Double receivePayment;
    @JsonProperty("buyer_name")
    private String buyerName;
    @JsonProperty("buyer_address")
    private String buyerAddress;
    @JsonProperty("date")
    private String date;

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
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

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public String getBuyerAddress() {
        return buyerAddress;
    }

    public void setBuyerAddress(String buyerAddress) {
        this.buyerAddress = buyerAddress;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
