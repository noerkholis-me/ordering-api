package com.hokeba.social.requests;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MailchimpProductVariantRequest {
	@JsonProperty("id")
	public String id;
	
	@JsonIgnore
	public String productId;

	@JsonProperty("title")
	public String title;
	
	@JsonProperty("url")
	public String url;
	
	@JsonProperty("sku")
	public String sku;
	
	@JsonProperty("price")
	public double price;
	
	@JsonProperty("inventory_quantity")
	public long inventoryQuantity;
	
	@JsonProperty("image_url")
	public String imageUrl;
	
	public MailchimpProductVariantRequest() {
		this.id = "";
		this.title = "";
		this.url = "";
		this.sku = "";
		this.price = 0;
		this.inventoryQuantity = 0;
		this.imageUrl = "";
	}
	
	//minimum required fields
	public MailchimpProductVariantRequest(String id, String title, String productId) {
		this.id = id;
		this.title = title;
		this.url = "";
		this.sku = "";
		this.price = 0;
		this.inventoryQuantity = 0;
		this.imageUrl = "";
		this.productId = productId;
	}
	
	public MailchimpProductVariantRequest(String id, String title, String url, String sku, double price, long inventoryQuantity, String imageUrl, String productId) {
		this.id = id;
		this.title = title;
		this.url = url;
		this.sku = sku;
		this.price = price;
		this.inventoryQuantity = inventoryQuantity;
		this.imageUrl = imageUrl;
		this.productId = productId;
	}
}
