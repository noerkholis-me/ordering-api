package com.hokeba.payment.midtrans.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TransactionVirtualAccount {
	@JsonProperty("va_number")
	public String vaNumber;
	@JsonProperty("free_text")
	public TransactionVaFreeText freeText;
}
