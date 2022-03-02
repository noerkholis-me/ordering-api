package com.hokeba.mapping.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by nugraha on 7/2/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MapMerchantPromoRequestProduct {
    @JsonProperty("product_id")
    private Integer productId;

    public MapMerchantPromoRequestProduct() {
    }

    public java.lang.Integer getProductId() {
        return productId;
    }

    public void setProductId(java.lang.Integer productId) {
        this.productId = productId;
    }
}
