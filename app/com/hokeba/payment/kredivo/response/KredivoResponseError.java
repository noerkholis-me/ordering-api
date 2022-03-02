package com.hokeba.payment.kredivo.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class KredivoResponseError {
	@JsonProperty("message")
	public String message;
	@JsonProperty("kind")
	public String kind;
}
