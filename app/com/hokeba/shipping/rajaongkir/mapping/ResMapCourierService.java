package com.hokeba.shipping.rajaongkir.mapping;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ResMapCourierService {
	@JsonProperty("service")
	public String service;

	@JsonProperty("description")
	public String description;

	@JsonProperty("cost")
	public List<ResMapCourierCost> cost;
	
}
