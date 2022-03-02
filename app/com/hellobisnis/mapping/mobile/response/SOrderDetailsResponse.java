package com.hellobisnis.mapping.mobile.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@JsonIgnoreProperties(ignoreUnknown = true)
public class SOrderDetailsResponse {
	public Long id;
	public ProductShortResponse product;
	public Double discount;
	public Double price;
	public Double totalPrice;
	public Integer quantity;
	public String note;
	
	List<SOrderAdditionalDetailResponse> additionals;

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

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Double getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(Double totalPrice) {
		this.totalPrice = totalPrice;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public List<SOrderAdditionalDetailResponse> getAdditionals() {
		return additionals;
	}

	public void setAdditionals(List<SOrderAdditionalDetailResponse> additionals) {
		this.additionals = additionals;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
	

}
