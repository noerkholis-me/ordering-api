package com.hokeba.mapping.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import models.MasterSizeInCaratCustomDiamond;

@JsonIgnoreProperties(ignoreUnknown=true)
public class MapMasterSizeInCarat {
	private Long id;
	private String name;
	private String description;
	@JsonProperty("image_name")
	private String imageName;
	private String url;
	
	public MapMasterSizeInCarat() {
		super();
	}
	
	public MapMasterSizeInCarat(MasterSizeInCaratCustomDiamond model) {
		this.id = model.id;
		this.name = model.name;
		this.description = model.description;
		this.imageName = model.imageName;
		this.url = model.getImageUrl();
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
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getImageName() {
		return imageName;
	}
	
	public void setImageName(String imageName) {
		this.imageName = imageName;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}

}
