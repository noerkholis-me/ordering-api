package com.hokeba.mapping.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import models.CatalogItem;
import models.Product;

public class MapCatalogDetailProduct {
	private Long id;
	@JsonProperty("category_level_3_name")
	private String categoryLevel3Name;
	private String name;
	private String slug;
	@JsonProperty("short_description")
	private String shortDescription;
	@JsonProperty("image_url")
	private String imageUrl;
	@JsonProperty("banner_image_url")
	private String bannerImageUrl;

	@JsonProperty("strike_through_display")
	private Double strikeThroughDisplay;
	@JsonProperty("price")
	private Double price;
	@JsonProperty("price_display")
	private Double priceDisplay;
	@JsonProperty("discount_string")
	private String discountString;
	
	public MapCatalogDetailProduct() {
		super();
	}
	
	public MapCatalogDetailProduct(CatalogItem model) {
		Product target = model.product;
		this.id = target.id;
		this.categoryLevel3Name = target.category.name;
		this.name = target.name;
		this.slug = target.slug;
		this.shortDescription = target.shortDescriptions;
		this.imageUrl = target.getImageUrl();
		this.bannerImageUrl = model.getImageUrl();
		this.strikeThroughDisplay = target.getStrikeThroughDisplay();
		this.price = target.price;
		this.priceDisplay = target.getPriceDisplay();
		this.discountString = target.fetchDiscountString();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCategoryLevel3Name() {
		return categoryLevel3Name;
	}

	public void setCategoryLevel3Name(String categoryLevel3Name) {
		this.categoryLevel3Name = categoryLevel3Name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSlug() {
		return slug;
	}

	public void setSlug(String slug) {
		this.slug = slug;
	}

	public String getShortDescription() {
		return shortDescription;
	}

	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getBannerImageUrl() {
		return bannerImageUrl;
	}

	public void setBannerImageUrl(String bannerImageUrl) {
		this.bannerImageUrl = bannerImageUrl;
	}

	public Double getStrikeThroughDisplay() {
		return strikeThroughDisplay;
	}

	public void setStrikeThroughDisplay(Double strikeThroughDisplay) {
		this.strikeThroughDisplay = strikeThroughDisplay;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Double getPriceDisplay() {
		return priceDisplay;
	}

	public void setPriceDisplay(Double priceDisplay) {
		this.priceDisplay = priceDisplay;
	}

	public String getDiscountString() {
		return discountString;
	}

	public void setDiscountString(String discountString) {
		this.discountString = discountString;
	}

}
