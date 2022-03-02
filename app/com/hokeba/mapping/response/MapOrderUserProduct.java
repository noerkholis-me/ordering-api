package com.hokeba.mapping.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by nugraha on 5/25/17.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class MapOrderUserProduct {
    @JsonProperty("shipment_type")
    public String shipmentType;
    @JsonProperty("shipment_name")
    public String shipmentName;
    public String sdate;
    public String edate;
    @JsonProperty("product_name")
    public String productName;
    @JsonProperty("product_img")
    public String productImg;
    public int qty;
    @JsonProperty("order_status")
    public MapOrderUserStatus orderStatus;
    @JsonProperty("log_status")
    public List<MapOrderLogStatus> logStatus;
    private String size;

    public MapOrderUserProduct(){

    }

    public MapOrderUserProduct(String shipmentType, String shipmentName, String sdate, String edate, String productName, String productImg, int qty, MapOrderUserStatus orderStatus, List<MapOrderLogStatus> logStatus) {
        this.shipmentType = shipmentType;
        this.shipmentName = shipmentName;
        this.sdate = sdate;
        this.edate = edate;
        this.productName = productName;
        this.productImg = productImg;
        this.qty = qty;
        this.orderStatus = orderStatus;
        this.logStatus = logStatus;
    }

    public String getShipmentType() {
        return shipmentType;
    }

    public void setShipmentType(String shipmentType) {
        this.shipmentType = shipmentType;
    }

    public String getShipmentName() {
        return shipmentName;
    }

    public void setShipmentName(String shipmentName) {
        this.shipmentName = shipmentName;
    }

    public String getSdate() {
        return sdate;
    }

    public void setSdate(String sdate) {
        this.sdate = sdate;
    }

    public String getEdate() {
        return edate;
    }

    public void setEdate(String edate) {
        this.edate = edate;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductImg() {
        return productImg;
    }

    public void setProductImg(String productImg) {
        this.productImg = productImg;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public MapOrderUserStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(MapOrderUserStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public List<MapOrderLogStatus> getLogStatus() {
        return logStatus;
    }

    public void setLogStatus(List<MapOrderLogStatus> logStatus) {
        this.logStatus = logStatus;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}