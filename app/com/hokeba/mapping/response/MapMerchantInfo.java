package com.hokeba.mapping.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by hendriksaragih on 3/2/17.
 */

@JsonIgnoreProperties(ignoreUnknown=true)
public class MapMerchantInfo {
    @JsonProperty("unpaid_customer")
    private Double unpaidCustomer;
    @JsonProperty("paid_hokeba")
    private Double paidHokeba;
    @JsonProperty("unpaid_hokeba")
    private Double unpaidHokeba;
    private List<MapMerchantPayment> lists;

    public MapMerchantInfo(){

    }

    public MapMerchantInfo(Double unpaidCustomer, Double paidHokeba, Double unpaidHokeba, List<MapMerchantPayment> lists) {
        this.unpaidCustomer = unpaidCustomer;
        this.paidHokeba = paidHokeba;
        this.unpaidHokeba = unpaidHokeba;
        this.lists = lists;
    }

    public Double getUnpaidCustomer() {
        return unpaidCustomer;
    }

    public void setUnpaidCustomer(Double unpaidCustomer) {
        this.unpaidCustomer = unpaidCustomer;
    }

    public Double getPaidHokeba() {
        return paidHokeba;
    }

    public void setPaidHokeba(Double paidHokeba) {
        this.paidHokeba = paidHokeba;
    }

    public Double getUnpaidHokeba() {
        return unpaidHokeba;
    }

    public void setUnpaidHokeba(Double unpaidHokeba) {
        this.unpaidHokeba = unpaidHokeba;
    }

    public List<MapMerchantPayment> getLists() {
        return lists;
    }

    public void setLists(List<MapMerchantPayment> lists) {
        this.lists = lists;
    }
}
