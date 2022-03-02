package com.hokeba.mapping.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MapProductListWithoutImage {
	@JsonProperty("name")
	public String name;

	@JsonProperty("id")
	public String id;

	@JsonProperty("product_code")
	public String productCode;

	@JsonProperty("slug")
	public String slug;

	@JsonProperty("brand_name")
	public String brandName;

	@JsonProperty("strike_through_display")
	public Double strikeThroughDisplay;

	@JsonProperty("price")
	public Double price;

	@JsonProperty("discount")
	public int discount;

	@JsonProperty("discount_type")
	private Integer discountType;

	@JsonProperty("average_rating")
	public float averageRating;

	@JsonProperty("count_rating")
	public float countRating;

	public String currency;

	@JsonProperty("price_display")
	public Double priceDisplay;

	@JsonProperty("seller")
	public MapMerchant seller;

	@JsonProperty("num_of_order")
	public Integer numOfOrder;

	@JsonProperty("image_url")
	public String imageUrl;

}