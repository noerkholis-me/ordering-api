package com.hokeba.social.requests;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MailchimpOrderRequest {
	@JsonProperty("id")
	public String id;
	
	@JsonProperty("customer")
	public MailchimpCustomerRequest customer;

	@JsonProperty("currency_code")
	public String currencyCode;

	@JsonProperty("order_total")
	public double orderTotal;
	
	@JsonProperty("lines")
	public List<MailchimpOrderLineRequest> lines;

	@JsonProperty("discount_total")
	public double discountTotal;

	@JsonProperty("shipping_total")
	public double shippingTotal;
	
	public MailchimpOrderRequest() {
		this.id = "";
		this.customer = new MailchimpCustomerRequest();
		this.currencyCode = "IDR";
		this.orderTotal = 0;
		this.lines = new ArrayList<MailchimpOrderLineRequest>();
	}
	
	public MailchimpOrderRequest(String id, MailchimpCustomerRequest customer, double orderTotal, List<MailchimpOrderLineRequest> lines) {
		this.id = id;
		this.customer = customer;
		this.currencyCode = "IDR";
		this.orderTotal = orderTotal;
		this.lines = lines;
	}
	
	public MailchimpOrderRequest(String id, MailchimpCustomerRequest customer, double orderTotal, List<MailchimpOrderLineRequest> lines, double discountTotal, double shippingTotal) {
		this.id = id;
		this.customer = customer;
		this.currencyCode = "IDR";
		this.orderTotal = orderTotal;
		this.lines = lines;
		this.discountTotal = discountTotal;
		this.shippingTotal = shippingTotal;
	}
}
