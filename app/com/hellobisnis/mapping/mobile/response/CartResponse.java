package com.hellobisnis.mapping.mobile.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CartResponse {

	public static final String STATUS_TAKEN_OUT = "TO";
	public static final String STATUS_CHECKOUT = "CH";

	private Long id;
	private MemberShortResponse member;
	private ProductShortResponse product;
	private Integer quantity;
	private Double price;
	private Double totalPrice;
	private Double discount;
	private String status;
	private String note;

	private List<CartAdditionalDetailResponse> additionalDetails;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public MemberShortResponse getMember() {
		return member;
	}

	public void setMember(MemberShortResponse member) {
		this.member = member;
	}

	public ProductShortResponse getProduct() {
		return product;
	}

	public void setProduct(ProductShortResponse product) {
		this.product = product;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
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

	public Double getDiscount() {
		return discount;
	}

	public void setDiscount(Double discount) {
		this.discount = discount;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<CartAdditionalDetailResponse> getAdditionalDetails() {
		return additionalDetails;
	}

	public void setAdditionalDetails(List<CartAdditionalDetailResponse> additionalDetails) {
		this.additionalDetails = additionalDetails;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
	
}
