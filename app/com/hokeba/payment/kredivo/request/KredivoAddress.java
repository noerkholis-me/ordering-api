package com.hokeba.payment.kredivo.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import models.Address;
import models.Merchant;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class KredivoAddress {
	public static final String DEFAULT_COUNTRY_CODE = "IDN";
	
	@JsonProperty("first_name")
	public String firstName;
	@JsonProperty("last_name")
	public String lastName;
	@JsonProperty("address")
	public String address;
	@JsonProperty("city")
	public String city;
	@JsonProperty("postal_code")
	public String postalCode;
	@JsonProperty("phone")
	public String phone;
	@JsonProperty("country_code")
	public String countryCode;
	
	public KredivoAddress() {
		super();
	}
	
	public KredivoAddress(Merchant merchant) {
		this.firstName = merchant.name;
		this.lastName = null;
		this.address = merchant.address == null ? "-" : merchant.address;
		this.city = merchant.district == null ? "-" : merchant.district.name;
		this.postalCode = merchant.postalCode == null ? "-" : merchant.postalCode;
		this.phone = merchant.phone == null ? "-" : merchant.phone;
		this.countryCode = DEFAULT_COUNTRY_CODE;
	}
	
	public KredivoAddress(Address address) {
		this.firstName = address.name;
		this.lastName = null;
		this.address = address.address == null ? "-" : address.address;
		this.city = address.district == null ? "-" : address.district.name;
		this.postalCode = address.postalCode == null ? "-" : address.postalCode;
		this.phone = address.phone == null ? "-" : address.phone;
		this.countryCode = DEFAULT_COUNTRY_CODE;
	}
}
