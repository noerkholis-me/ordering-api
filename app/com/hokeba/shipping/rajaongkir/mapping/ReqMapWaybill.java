package com.hokeba.shipping.rajaongkir.mapping;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ReqMapWaybill {

	@JsonProperty("waybill")
	public String waybill;

	@JsonProperty("courier")
	public String courier;
}
