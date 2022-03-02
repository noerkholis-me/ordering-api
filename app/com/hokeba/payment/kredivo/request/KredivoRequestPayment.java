package com.hokeba.payment.kredivo.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class KredivoRequestPayment {
	@JsonProperty("server_key")
	public String serverKey;
	@JsonProperty("amount")
	public Double amount;
	@JsonProperty("items")
	public List<KredivoTransactionItem> items; 
}
