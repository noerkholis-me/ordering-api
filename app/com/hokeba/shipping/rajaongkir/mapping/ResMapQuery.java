package com.hokeba.shipping.rajaongkir.mapping;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ResMapQuery {
	
	@JsonProperty("city")
	public String city;

	@JsonProperty("province_id")
	public String provinceId;

	@JsonProperty("province")
	public String province;

	public String originType;
	@JsonProperty("origin")
	public String origin;

	public String destinationType;
	@JsonProperty("destination")
	public String destination;

	@JsonProperty("courier")
	public String courier;

	@JsonProperty("weight")
	public Integer weight;

	public ResMapQuery() {
		super();
	}
	
	public ResMapQuery(String origin, String destination) {
		super();
		this.origin = origin;
		this.destination = destination;
	}

	public String transformToFormX() {
		return "origin=" + this.origin + "&destination=" + this.destination + "&courier=" + this.courier + "&weight="
				+ this.weight;
	}
}
