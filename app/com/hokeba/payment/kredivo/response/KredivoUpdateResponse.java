package com.hokeba.payment.kredivo.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class KredivoUpdateResponse {
	@JsonProperty("status")
	public String status;
	@JsonProperty("legal_name")
	public String legalName;
	@JsonProperty("fraud_status")
	public String fraudStatus;
	@JsonProperty("order_id")
	public String orderId;
	@JsonProperty("transaction_time")
	public String transactionTime;
	@JsonProperty("amount")
	public String amount;
	@JsonProperty("payment_type")
	public String paymentType;
	@JsonProperty("transaction_status")
	public String transactionStatus;
	@JsonProperty("message")
	public String message;
	@JsonProperty("transaction_id")
	public String transactionId;
	
}
