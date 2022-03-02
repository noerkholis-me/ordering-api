package com.hokeba.mapping.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by nugraha on 6/29/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MapShippingId {
    @JsonProperty("shipping_id")
    private Long shippingId;

    public Long getShippingId() {
        return shippingId;
    }

    public void setShippingId(Long shippingId) {
        this.shippingId = shippingId;
    }
}
