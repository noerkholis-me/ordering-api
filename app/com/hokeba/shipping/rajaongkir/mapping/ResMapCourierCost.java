package com.hokeba.shipping.rajaongkir.mapping;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ResMapCourierCost {
	@JsonProperty("value")
	public String value;

	@JsonProperty("etd")
	public String etd;
	
	@JsonProperty("note")
	public String note;
	
}
