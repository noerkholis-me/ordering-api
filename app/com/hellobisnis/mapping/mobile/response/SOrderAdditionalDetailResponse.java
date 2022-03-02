package com.hellobisnis.mapping.mobile.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SOrderAdditionalDetailResponse {
	public Long id;
	public ProductShortResponse product;
	public Double discount;
	public double price;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ProductShortResponse getProduct() {
		return product;
	}

	public void setProduct(ProductShortResponse product) {
		this.product = product;
	}

	public Double getDiscount() {
		return discount;
	}

	public void setDiscount(Double discount) {
		this.discount = discount;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

}
