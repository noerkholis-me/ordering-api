package com.hokeba.payment.midtrans.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import models.Address;
import models.Member;

public class TransactionCustomerDetail {
	@JsonProperty("first_name")
	public String first_name;
	@JsonProperty("last_name")
	public String last_name;
	@JsonProperty("email")
	public String email;
	@JsonProperty("phone")
	public String phone;
	@JsonProperty("billing_address")
	public TransactionAddress billing_address;
	@JsonProperty("shipping_address")
	public TransactionAddress shipping_address;
	
	public TransactionCustomerDetail() {
		super();
	}
	
	public TransactionCustomerDetail(Member member, Address shippingAddress, Address billingAddress) {
		this.first_name = member.fullName;
//		this.last_name = "";
		this.email = member.email;
		this.phone = member.phone;
		this.shipping_address = new TransactionAddress(shippingAddress);
		this.billing_address = new TransactionAddress(billingAddress);
	}
}
