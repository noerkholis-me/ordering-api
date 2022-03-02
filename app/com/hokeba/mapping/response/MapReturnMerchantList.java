package com.hokeba.mapping.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by hendriksaragih on 7/3/17.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class MapReturnMerchantList {
    @JsonProperty("order_no")
    public String orderNo;
    @JsonProperty("product_name")
    private String productName;
    private String sku;
    private String status;
    @JsonProperty("order_date")
    private String orderDate;
    @JsonProperty("order_item_id")
    private String orderItemId;
    @JsonProperty("quantity")
    private Integer quantity;
    @JsonProperty("price")
    private Double price;
    @JsonProperty("total")
    private Double total;
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

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
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

    public String getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(String orderItemId) {
        this.orderItemId = orderItemId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
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
}
