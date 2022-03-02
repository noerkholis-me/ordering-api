package com.hokeba.payment.kredivo.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class KredivoTransactionDetail {
	@JsonProperty("order_id")
	public String orderId;
	@JsonProperty("amount")
	public Double amount;
	@JsonProperty("items")
	public List<KredivoTransactionItem> items; 
	
}
