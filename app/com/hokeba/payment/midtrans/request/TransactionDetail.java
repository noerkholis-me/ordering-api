package com.hokeba.payment.midtrans.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TransactionDetail {
	@JsonProperty("order_id")
	public String order_id;
	@JsonProperty("gross_amount")
	public long gross_amount;
	
	public TransactionDetail() {
		super();
	}
	
	public TransactionDetail(String orderId, long grossAmount) {
		this.order_id = orderId;
		this.gross_amount = grossAmount;
	}
}
