package com.hokeba.mapping.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by nugraha on 7/2/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MapMerchantPromoRequest {
    @JsonProperty("promo_id")
    private Integer promoId;
    @JsonProperty("products")
    private List<MapMerchantPromoRequestProduct> products;
    private String type;

    public MapMerchantPromoRequest() {
    }

    public java.lang.Integer getPromoId() {
        return promoId;
    }

    public void setPromoId(java.lang.Integer promoId) {
        this.promoId = promoId;
    }

    public List<MapMerchantPromoRequestProduct> getProducts() {
        return products;
    }

    public void setProducts(List<MapMerchantPromoRequestProduct> products) {
        this.products = products;
    }

    public String getType() {
        return type == null ? "add" : type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
