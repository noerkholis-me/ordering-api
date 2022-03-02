package com.hokeba.shipping.rajaongkir.mapping;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ResMapWaybillManifest {

	@JsonProperty("manifest_code")
	public String manifestCode;

	@JsonProperty("manifest_description")
	public String manifestDescription;

	@JsonProperty("manifest_date")
	public String manifestDate;

	@JsonProperty("manifest_time")
	public String manifestTime;

	@JsonProperty("city_name")
	public String cityName;
}