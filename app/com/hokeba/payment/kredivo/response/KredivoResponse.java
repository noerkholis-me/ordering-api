package com.hokeba.payment.kredivo.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class KredivoResponse {
	
	@JsonProperty("status")
	public String status;
	@JsonProperty("message")
	public String message;
	@JsonProperty("redirect_url")
	public String redirectUrl;
	@JsonProperty("error")
	public KredivoResponseError error;
	
	@JsonProperty("payments")
	public List<KredivoResponsePayment> payments;
	
}
