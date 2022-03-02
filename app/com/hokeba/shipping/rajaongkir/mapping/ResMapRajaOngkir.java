package com.hokeba.shipping.rajaongkir.mapping;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ResMapRajaOngkir {

	@JsonProperty("query")
	private ResMapQuery query;

	@JsonProperty("status")
	private ResMapStatus status;

	@JsonProperty("origin_details")
	private ResMapCity originDetails;

	@JsonProperty("destination_details")
	private ResMapCity destinationDetails;

	@JsonProperty("results")
	private JsonNode results;

	@JsonProperty("result")
	private JsonNode result;

	public JsonNode getResult() {
		return result;
	}

	public void setResult(JsonNode result) {
		this.result = result;
	}

	public ResMapStatus getStatus() {
		return status;
	}

	public void setStatus(ResMapStatus status) {
		this.status = status;
	}

	public JsonNode getResults() {
		return results;
	}

	public void setResults(JsonNode results) {
		this.results = results;
	}

	public ResMapQuery getQuery() {
		return query;
	}

	public void setQuery(ResMapQuery query) {
		this.query = query;
	}

	public ResMapCity getOriginDetails() {
		return originDetails;
	}

	public void setOriginDetails(ResMapCity originDetails) {
		this.originDetails = originDetails;
	}

	public ResMapCity getDestinationDetails() {
		return destinationDetails;
	}

	public void setDestinationDetails(ResMapCity destinationDetails) {
		this.destinationDetails = destinationDetails;
	}

}
