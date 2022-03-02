package com.hokeba.mapping.response;

import java.util.List;
import java.util.Map;

import javax.persistence.Column;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import models.PickUpPoint;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MapPickUpPointRes {

	@JsonProperty("id")
	public long id;

	@JsonProperty("merchant_id")
	public long merchantId;

	@JsonProperty("name")
	public String name;

	@JsonProperty("address")
	public String address;

	@JsonProperty("contact")
	public String contact;

	@JsonProperty("duration")
	public long duration;

	@JsonProperty("latitude")
	public double latitude;

	@JsonProperty("longitude")
	public double longitude;

	public MapPickUpPointRes(PickUpPoint points) {
		// TODO Auto-generated constructor stub
		
		if(points.merchant!=null) {
			this.merchantId = points.merchant.id;
		}
		this.id = points.id;
		this.name = points.name;
		this.address = points.address;
		this.contact = points.contact;
		this.duration = points.duration;
		this.latitude = points.latitude;
		this.longitude = points.longitude;
	}
}
