package com.hokeba.mapping.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import models.DiamondType;
import models.MasterDiamondInventory;
import models.MasterDiamondPrice;
import models.UserCms;

@JsonIgnoreProperties(ignoreUnknown=true)
public class MapMasterDiamondInventory {
	
	private Long id;
	@JsonProperty("size_in_carat")
	private Float sizeInCarat;
	private String clarity;
	private String color;
	@JsonProperty("quantity_in_stock")
	public Long quantityInStock;
	
	
	@JsonProperty("user_id")
	public UserCms userCms;
	
	@JsonProperty("master_diamond_price")
	public MasterDiamondPrice masterDiamondPrice;
	
	@JsonProperty("diamond_type")
	public DiamondType diamondType;
	
//	public MapMasterDiamondInventory() {
//		super();
//	}
	
//	public MapMasterDiamondInventory(MasterDiamondInventory model) {
//		this.id = model.id;
//		this.sizeInCarat = model.sizeInCarat;
//		this.clarity = model.clarity;
//		this.color = model.color;
//		this.quantityInStock = model.quantityInStock;
//		this.userCms = model.userCms;
//		this.masterDiamondPrice = model.masterDiamondPrice;
//		this.diamondType = model.diamondType;
//	}
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public Float getSizeInCarat() {
		return sizeInCarat;
	}
	
	public void setSizeInCarat(Float sizeInCarat) {
		this.sizeInCarat = sizeInCarat;
	}
	
	public String getClarity() {
		return clarity;
	}
	
	public void setClarity(String clarity) {
		this.clarity = clarity;
	}
	
	public String getColor() {
		return color;
	}
	
	public void setColor(String color) {
		this.color = color;
	}
	
	public Long getQuantityInStock() {
		return quantityInStock;
	}
	
	public void setQuantityInStock(Long quantityInStock) {
		this.quantityInStock = quantityInStock;
	}
	
	public MasterDiamondPrice getMasterDiamondPrice() {
		return masterDiamondPrice;
	}
	
	public void setMasterDiamondPrice(MasterDiamondPrice masterDiamondPrice) {
		this.masterDiamondPrice = masterDiamondPrice;
	}
	
	public DiamondType getDiamondType() {
		return diamondType;
	}
	
	public void setDiamondType(DiamondType diamondType) {
		this.diamondType = diamondType;
	}
	
	public UserCms getUserCms() {
		return userCms;
	}
	
	public void setUserCms(UserCms userCms) {
		this.userCms = userCms;
	}

}
