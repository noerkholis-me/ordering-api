package com.hokeba.mapping.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import models.Address;

/**
 * Created by hendriksaragih on 3/2/17.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class MapAddress {
	private Long id;
	public String name;
	public Integer type;
	public String phone;
	public String address;
	
	public String districts;
	@JsonProperty("district_id")
	public Long districtId;
	
	public String regions;
	@JsonProperty("region_id")
	public Long regionId;
	
	public String city;
	@JsonProperty("city_id")
	public Long cityId;
	
//	public String villages;
//	@JsonProperty("vilage_id")
//	public Long vilageId;
	@JsonProperty("postal_code")
	public String postalCode;
	@JsonProperty("is_primary")
	public Boolean isPrimary;

	public MapAddress() {

	}

	public MapAddress(Address address) {
		setId(address.id);
		setName(address.name);
		setType(address.type);
		setPhone(address.phone);
		setAddress(address.address);

		setDistricts(address.getDistricts());
		setRegions(address.getRegions());
//		setVillages(address.getVilages());
		setCity(address.getCity());

		setDistrictId(address.getDistrictId());
		setRegionId(address.getRegionId());
//		setVilageId(address.getVilageId());
		setCityId(address.getCityId());
		setPostalCode(address.postalCode);
		setPrimary(address.isPrimary);
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

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getDistricts() {
		return districts;
	}

	public void setDistricts(String districts) {
		this.districts = districts;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public Boolean getPrimary() {
		return isPrimary;
	}

	public void setPrimary(Boolean primary) {
		isPrimary = primary;
	}

	public Long getCityId() {
		return cityId;
	}

	public void setCityId(Long cityId) {
		this.cityId = cityId;
	}

	public Long getDistrictId() {
		return districtId;
	}

	public void setDistrictId(Long districtId) {
		this.districtId = districtId;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getRegions() {
		return regions;
	}

	public void setRegions(String regions) {
		this.regions = regions;
	}

//	public String getVillages() {
//		return villages;
//	}
//
//	public void setVillages(String villages) {
//		this.villages = villages;
//	}

	public Long getRegionId() {
		return regionId;
	}

	public void setRegionId(Long regionId) {
		this.regionId = regionId;
	}

//	public Long getVilageId() {
//		return vilageId;
//	}
//
//	public void setVilageId(Long vilageId) {
//		this.vilageId = vilageId;
//	}

	public Boolean getIsPrimary() {
		return isPrimary;
	}

	public void setIsPrimary(Boolean isPrimary) {
		this.isPrimary = isPrimary;
	}

}
