package com.hokeba.payment.midtrans.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionGopay {
	@JsonProperty("enable_callback")
	public boolean enableCallback;
	@JsonProperty("callback_url")
	public String callbackUrl;
	
	public TransactionGopay() {
		super();
	}
	
	public TransactionGopay(boolean enableCallback, String callbackUrl) {
		this.enableCallback = enableCallback;
		this.callbackUrl = callbackUrl;
	}
	
}
