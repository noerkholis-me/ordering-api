package com.hokeba.mapping.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import models.DiamondType;
import models.UserCms;

@JsonIgnoreProperties(ignoreUnknown=true)
public class MapDiamondType {
	
	private Long id;
	private String name;
	@JsonProperty("user_id")
	private UserCms userCms;
	
	public MapDiamondType() {
		super();
	}
	
	public MapDiamondType(DiamondType model) {
		this.id = model.id;
		this.name = model.name;
		this.userCms = model.userCms;
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
	
	public UserCms getUserCms() {
		return userCms;
	}
	
	public void setUserCms(UserCms userCms) {
		this.userCms = userCms;
	}

}
