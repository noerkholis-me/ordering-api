package com.hokeba.payment.midtrans.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TransactionItemDetail {
	@JsonProperty("id")
	public String id;
	@JsonProperty("price")
	public long price;
	@JsonProperty("quantity")
	public int quantity;
	@JsonProperty("name")
	public String name;
	
	public TransactionItemDetail() {
		super();
	}
	
	public TransactionItemDetail(String id, String name, long price, int quantity) {
		this.id = id;
		this.name = name == null ? "" : name.length() <= 50 ? name : name.substring(0, 50);
		this.price = price;
		this.quantity = quantity;
	}
	
	public long fetchTotalPrice() {
		return price*((long)quantity);
	}
}
