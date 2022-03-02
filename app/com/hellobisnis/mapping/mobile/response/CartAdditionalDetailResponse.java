package com.hellobisnis.mapping.mobile.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CartAdditionalDetailResponse {

	private Long id;
	private ProductShortResponse product;
	private Double price;
	private Double discount;

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

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Double getDiscount() {
		return discount;
	}

	public void setDiscount(Double discount) {
		this.discount = discount;
	}

}
