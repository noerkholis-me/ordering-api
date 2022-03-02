package com.hokeba.payment.midtrans.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MainTransactionBca extends MainTransaction {
	@JsonProperty("bca_va")
	public TransactionVirtualAccount bcaVa;
}
