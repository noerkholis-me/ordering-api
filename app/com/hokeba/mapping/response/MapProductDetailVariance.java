package com.hokeba.mapping.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import models.MasterColor;
import models.ProductDetailVariance;
import models.Size;

public class MapProductDetailVariance {

	@JsonProperty("id")
	public Long id;

	@JsonProperty("total_stock")
	public Long totalStock;

	@JsonProperty("color")
	public MasterColor color;

	@JsonProperty("size")
	public Size size;

	public MapProductDetailVariance(ProductDetailVariance pdv) {
		super();
		this.id = pdv.id;
		this.totalStock = pdv.totalStock;
		this.color = pdv.color;
		this.size = pdv.size;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getTotalStock() {
		return totalStock;
	}

	public void setTotalStock(Long totalStock) {
		this.totalStock = totalStock;
	}

	public MasterColor getColor() {
		return color;
	}

	public void setColor(MasterColor color) {
		this.color = color;
	}

	public Size getSize() {
		return size;
	}

	public void setSize(Size size) {
		this.size = size;
	}

}
