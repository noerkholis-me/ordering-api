package com.hokeba.payment.midtrans.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionToken {
	@JsonProperty("token")
	public String token;
	@JsonProperty("redirect_url")
	public String redirectUrl;
}
