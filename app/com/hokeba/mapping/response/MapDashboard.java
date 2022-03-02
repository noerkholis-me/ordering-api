package com.hokeba.mapping.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by hendriksaragih on 3/2/17.
 */

@JsonIgnoreProperties(ignoreUnknown=true)
public class MapDashboard {
    @JsonProperty("order_summary")
    private MapOrderSummary orderSummary;
    private MapProductSummary products;
    private MapRecommendation recommendations;
    @JsonProperty("sales_data")
    private List<MapSellerData> sellerData;
    @JsonProperty("order_summary_details")
    private List<MapOrderSummary> orderSummaryDetails;



    public MapOrderSummary getOrderSummary() {
        return orderSummary;
    }

    public void setOrderSummary(MapOrderSummary orderSummary) {
        this.orderSummary = orderSummary;
    }

    public MapProductSummary getProducts() {
        return products;
    }

    public void setProducts(MapProductSummary products) {
        this.products = products;
    }

    public MapRecommendation getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(MapRecommendation recommendations) {
        this.recommendations = recommendations;
    }

    public List<MapSellerData> getSellerData() {
        return sellerData;
    }

    public void setSellerData(List<MapSellerData> sellerData) {
        this.sellerData = sellerData;
    }

    public List<MapOrderSummary> getOrderSummaryDetails() {
        return orderSummaryDetails;
    }

    public void setOrderSummaryDetails(List<MapOrderSummary> orderSummaryDetails) {
        this.orderSummaryDetails = orderSummaryDetails;
    }
}
