package com.hokeba.mapping.response;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class MapGoogleCatalog {

	public String sku;

	@JsonProperty("name")
	public String name;

	@JsonIgnore
	public Double promo_price;
	@JsonGetter("price")
	public String getPriceDisplay() {
		return "IDR " + promo_price.intValue();
	}
	
	@JsonGetter("condition")
	public String getCondition() {
		return "new";
	}
	
	@JsonProperty("total_stock")
	public Long totalStock;

	@JsonGetter("availability")
	public String getAvailability() {
		return (totalStock > 0) ? "in stock" : "out of stock";
	}

	@JsonProperty("slug")
	public String slug;
	@JsonGetter("link")
	public String getLink() {
		return "https://whizliz.com/product/" + slug;
	}

	@JsonProperty("image_url")
	public String imageUrl;
	
	@JsonProperty("brand_name")
	public String brandName;
}
