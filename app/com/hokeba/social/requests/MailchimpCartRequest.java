package com.hokeba.social.requests;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MailchimpCartRequest {
	@JsonProperty("id")
	public String id;
	
	@JsonProperty("customer")
	public MailchimpCustomerRequest customer;
	
	@JsonProperty("currency_code")
	public String currencyCode;
	
	@JsonProperty("order_total")
	public double orderTotal;
	
	@JsonProperty("lines")
	public List<MailchimpCartLineRequest> lines;
	
	public MailchimpCartRequest() {
		this.id = "";
		this.customer = new MailchimpCustomerRequest();
		this.currencyCode = "IDR";
		this.orderTotal = 0;
		this.lines = new ArrayList<MailchimpCartLineRequest>();
	}

	public MailchimpCartRequest(String id, MailchimpCustomerRequest customer, double orderTotal,
			List<MailchimpCartLineRequest> lines) {
		this.id = id;
		this.customer = customer;
		this.currencyCode = "IDR";
		this.orderTotal = orderTotal;
		this.lines = lines;
	}
}
