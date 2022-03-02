package com.hokeba.social.requests;

import java.util.ArrayList;
import java.util.List;
import models.Bag;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MailchimpCartLineRequest {
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

	public MailchimpCartLineRequest() {
		this.id = "";
		this.productId = "";
		this.productVariantId = "";
		this.quantity = 0;
		this.price = 0;
	}

	public MailchimpCartLineRequest(String id, String productId, String productVariantId, int quantity, double price) {
		this.id = id;
		this.productId = productId;
		this.productVariantId = productVariantId;
		this.quantity = quantity;
		this.price = price;
	}
	
	public MailchimpCartLineRequest(Bag bagItem) {
		this.id = bagItem.id.toString();
		this.productId = bagItem.productVariance.mainProduct.id.toString();
		this.productVariantId = bagItem.productVariance.id.toString();
		this.quantity = bagItem.quantity.intValue();
		this.price = bagItem.productVariance.mainProduct.getPriceDisplay().intValue();
	}
}
