package com.hokeba.mapping.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MapOrderSellerVoucherDetail {
	@JsonProperty("voucher_name")
	public String voucherName;
	@JsonProperty("value")
	public Double value;
	
	public MapOrderSellerVoucherDetail() {
		super();
	}
	
	public MapOrderSellerVoucherDetail(String voucherName, Double value) {
		this.voucherName = voucherName;
		this.value = value;
	}
	
}
