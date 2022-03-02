package com.hokeba.mapping.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by hendriksaragih on 7/3/17.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class MapReturnUser {
    @JsonProperty("order_no")
    public String orderNo;
    @JsonProperty("order_date")
    private String orderDate;
    @JsonProperty("returns")
    private List<MapReturnMerchant> returns;


    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public List<MapReturnMerchant> getReturns() {
        return returns;
    }

    public void setReturns(List<MapReturnMerchant> returns) {
        this.returns = returns;
    }
}
