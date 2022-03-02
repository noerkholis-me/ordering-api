package com.hokeba.payment.kredivo.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class KredivoCallbackResponse {
	
	@JsonProperty("status")
	public String status;
	@JsonProperty("message")
	public String message;
	@JsonProperty("signature_key")
	public String signatureKey;
	
	@JsonProperty("order_id")
	public String orderId;
	@JsonProperty("amount")
	public String amount;
	
	@JsonProperty("transaction_id")
	public String transactionId;
	@JsonProperty("transaction_status")
	public String transactionStatus;
	@JsonProperty("transaction_time")
	public Long transactionTime;
	@JsonProperty("payment_type")
	public String paymentType;
	
	@JsonProperty("shipping_address")
	public KredivoCallbackShippingAddressResponse shippingAddress;
	
	public String fetchSignatureKeyRaw() {
		return this.orderId + ";" 
				+ this.transactionId + ";"
				+ this.transactionStatus + ";"
				+ this.amount;
	}
	
}
