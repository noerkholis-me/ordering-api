package com.hokeba.mapping.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MapTotalPriceCustomDiamond {
	@JsonProperty("estimated_price")
    private Double estimatedPrice;
	
	public Double getEstimatedPrice() {
		return estimatedPrice;
	}
	
	public void setEstimatedPrice(Double estimatedPrice) {
		this.estimatedPrice = estimatedPrice;
	}

}
