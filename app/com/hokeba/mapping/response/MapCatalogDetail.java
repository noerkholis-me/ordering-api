package com.hokeba.mapping.response;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import models.Catalog2;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MapCatalogDetail {
	private Long id;
	private String name;
	private String slug;
	@JsonProperty("image_url")
	private String imageUrl;
	private List<MapCatalogDetailProduct> product;
	
	public MapCatalogDetail() {
		super();
	}
	
	public MapCatalogDetail(Catalog2 model) {
		this.id = model.id;
		this.name = model.name;
		this.slug = model.slug;
		this.imageUrl = model.getImageUrl();
		this.product = new ArrayList<>();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public List<MapCatalogDetailProduct> getProduct() {
		return product;
	}

	public void setProduct(List<MapCatalogDetailProduct> product) {
		this.product = product;
	}

}
