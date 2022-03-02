package com.hokeba.shipping.rajaongkir.mapping;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ResMapCourier {
	@JsonProperty("code")
	public String code;

	@JsonProperty("name")
	public String name;

	@JsonProperty("costs")
	public List<ResMapCourierService> costs;

}
