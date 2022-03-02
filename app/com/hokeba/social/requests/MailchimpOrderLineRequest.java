package com.hokeba.social.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MailchimpOrderLineRequest {
	@JsonProperty("id")
	public String id;
	
	@JsonProperty("product_id")
	public String productId;
	
	@JsonProperty("product_variant_id")
	public String productVariantId;

	@JsonProperty("quantity")
	public int quantity;

	@JsonProperty("price")
	public double price;

	@JsonProperty("discount")
	public double discount;
	
	public MailchimpOrderLineRequest() {
		this.id = "";
		this.productId = "";
		this.productVariantId = "";
		this.quantity = 0;
		this.price = 0;
		this.discount = 0;
	}
	
	//minimum required fields
	public MailchimpOrderLineRequest(String id, String productId, String productVariantId, int quantity, double price) {
		this.id = id;
		this.productId = productId;
		this.productVariantId = productVariantId;
		this.quantity = quantity;
		this.price = price;
		this.discount = 0;
	}
	
	public MailchimpOrderLineRequest(String id, String productId, String productVariantId, int quantity, double price, double discount) {
		this.id = id;
		this.productId = productId;
		this.productVariantId = productVariantId;
		this.quantity = quantity;
		this.price = price;
		this.discount = discount;
	}
}
