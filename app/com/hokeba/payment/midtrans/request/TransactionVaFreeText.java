package com.hokeba.payment.midtrans.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TransactionVaFreeText {
	@JsonProperty("inquiry")
	public TransactionVaFreeTextInfo[] inquiry;
	@JsonProperty("payment")
	public TransactionVaFreeTextInfo[] payment;
}
