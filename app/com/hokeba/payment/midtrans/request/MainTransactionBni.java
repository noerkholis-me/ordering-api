package com.hokeba.payment.midtrans.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MainTransactionBni extends MainTransaction {
	@JsonProperty("bni_va")
	public TransactionVirtualAccount bniVa;
}
