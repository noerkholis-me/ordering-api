package com.hokeba.mapping.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by nugraha on 5/25/17.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class MapOrderUser {
    @JsonProperty("order_number")
    private String orderNumber;
    @JsonProperty("total")
    private Double total;
    @JsonProperty("status_text")
    private String statusText;
    @JsonProperty("order_date_string")
    private String orderDateString;
    @JsonProperty("checkout_type")
    private Long checkoutType;
}