package com.hokeba.shipping.rajaongkir.mapping;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ResMapSubDistrict {

	@JsonProperty("province_id")
	public String provinceId;

	@JsonProperty("province")
	public String province;

	@JsonProperty("city_id")
	public String cityId;

	@JsonProperty("city")
	public String city;

	@JsonProperty("subdistrict_id")
	public String subdistrictId;

	@JsonProperty("subdistrict_name")
	public String subdistrictName;

	@JsonProperty("type")
	public String type;
}
