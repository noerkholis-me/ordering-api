package com.hokeba.payment.kredivo.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class KredivoTransactionItem {
	@JsonProperty("id")
	public String id;
	@JsonProperty("name")
	public String name;
	@JsonProperty("price")
	public Double price;
	@JsonProperty("quantity")
	public Integer quantity;
	
	@JsonProperty("url")
	public String url;
	@JsonProperty("image_url")
	public String imageUrl;
	
	@JsonProperty("type")
	public String type; //category
	@JsonProperty("parent_type")
	public String parentType; //SELLER OR ITEM
	@JsonProperty("parent_id")
	public String parentId; //ID SELLER OR ID PARENT ITEM
	
	public KredivoTransactionItem() {
		super();
	}
	
	public KredivoTransactionItem(String id, String name, Double price, Integer quantity,
			String url, String imageUrl, String category, String parentType, String parentId) {
		this.id = id;
		this.name = name;
		this.price = price;
		this.quantity = quantity;
		this.url = url;
		this.imageUrl = imageUrl;
		this.type = category;
		this.parentType = parentType;
		this.parentId = parentId;
	}
	
}
