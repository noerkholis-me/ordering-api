package com.hokeba.payment.midtrans.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MainTransactionBri extends MainTransaction {
	@JsonProperty("bri_va")
	public TransactionVirtualAccount briVa;
}
