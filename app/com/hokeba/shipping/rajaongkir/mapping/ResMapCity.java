package com.hokeba.shipping.rajaongkir.mapping;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ResMapCity {

	@JsonProperty("province_id")
	public String provinceId;

	@JsonProperty("province")
	public String province;

	@JsonProperty("city_id")
	public String cityId;

	@JsonProperty("city")
	public String city;
	
	@JsonProperty("city_name")
	public String cityName;
	
	@JsonProperty("subdistrict_id")
	public String subdistrictId;

	@JsonProperty("subdistrict_name")
	public String subdistrictName;
	
	@JsonProperty("type")
	public String type;

	@JsonProperty("postal_code")
	public String postalCode;
}
