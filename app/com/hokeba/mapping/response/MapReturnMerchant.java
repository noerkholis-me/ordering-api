package com.hokeba.mapping.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by hendriksaragih on 7/3/17.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class MapReturnMerchant {
    @JsonProperty("return_id")
    public Long returnId;
    @JsonProperty("order_no")
    public String orderNo;
    private String status;
    @JsonProperty("order_date")
    private String orderDate;
    @JsonProperty("return_date")
    private String returnDate;
    @JsonProperty("return_no")
    private String returnNo;
    @JsonProperty("return_type")
    private String returnType;
    @JsonProperty("return_customer")
    private String returnCustomer;
    @JsonProperty("return_description")
    private String returnDescription;
    @JsonProperty("status_shipping")
    private MapReturStatus statusShipping;
    @JsonProperty("tracking_number")
    public String trackingNumber;
    @JsonProperty("return_merchant")
    public String returnMerchant;
    @JsonProperty("items")
    public List<MapReturnMerchantDetail> items;

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(String returnDate) {
        this.returnDate = returnDate;
    }

    public String getReturnNo() {
        return returnNo;
    }

    public void setReturnNo(String returnNo) {
        this.returnNo = returnNo;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public String getReturnCustomer() {
        return returnCustomer;
    }

    public void setReturnCustomer(String returnCustomer) {
        this.returnCustomer = returnCustomer;
    }

    public String getReturnDescription() {
        return returnDescription;
    }

    public void setReturnDescription(String returnDescription) {
        this.returnDescription = returnDescription;
    }

    public MapReturStatus getStatusShipping() {
        return statusShipping;
    }

    public void setStatusShipping(MapReturStatus statusShipping) {
        this.statusShipping = statusShipping;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public List<MapReturnMerchantDetail> getItems() {
        return items;
    }

    public void setItems(List<MapReturnMerchantDetail> items) {
        this.items = items;
    }

    public Long getReturnId() {
        return returnId;
    }

    public void setReturnId(Long returnId) {
        this.returnId = returnId;
    }

    public String getReturnMerchant() {
        return returnMerchant;
    }

    public void setReturnMerchant(String returnMerchant) {
        this.returnMerchant = returnMerchant;
    }
}
