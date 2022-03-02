package com.hokeba.mapping.response;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by hendriksaragih on 3/2/17.
 */

@JsonIgnoreProperties(ignoreUnknown=true)
public class MapOrderSummary {
    @JsonProperty("new_order")
    private Integer newOrder;
    @JsonProperty("order_processed")
    private Integer orderProcessed;
    @JsonProperty("order_returned")
    private Integer orderReturned;
    @JsonProperty("order_completed")
    private Integer orderCompleted;
    @JsonProperty("date")
    private String date;

    public MapOrderSummary(){

    }

    public MapOrderSummary(int newOrder, int orderProcessed, int orderReturned, int orderCompleted){
        this.newOrder = newOrder;
        this.orderProcessed = orderProcessed;
        this.orderReturned = orderReturned;
        this.orderCompleted = orderCompleted;
    }

    public MapOrderSummary(int newOrder, int orderProcessed, int orderReturned, int orderCompleted, String date){
        this.newOrder = newOrder;
        this.orderProcessed = orderProcessed;
        this.orderReturned = orderReturned;
        this.orderCompleted = orderCompleted;
        this.date = date;
    }

    public Integer getNewOrder() {
        return newOrder;
    }

    public void setNewOrder(Integer newOrder) {
        this.newOrder = newOrder;
    }

    public Integer getOrderProcessed() {
        return orderProcessed;
    }

    public void setOrderProcessed(Integer orderProcessed) {
        this.orderProcessed = orderProcessed;
    }

    public Integer getOrderReturned() {
        return orderReturned;
    }

    public void setOrderReturned(Integer orderReturned) {
        this.orderReturned = orderReturned;
    }

    public Integer getOrderCompleted() {
        return orderCompleted;
    }

    public void setOrderCompleted(Integer orderCompleted) {
        this.orderCompleted = orderCompleted;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
