package com.hokeba.payment.midtrans.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MainTransactionPermata extends MainTransaction {
	@JsonProperty("permata_va")
	public TransactionVirtualAccount permataVa;
}
