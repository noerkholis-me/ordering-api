package com.hokeba.payment.midtrans.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionBcaVaNumber {
	@JsonProperty("va_number")
	public String va_number;
	@JsonProperty("bank")
	public String bank;
}
