package com.hokeba.payment.kredivo.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class KredivoRequestUpdate {
	@JsonProperty("transaction_id")
	public String transactionId;
	@JsonProperty("signature_key")
	public String signatureKey;
	
}
