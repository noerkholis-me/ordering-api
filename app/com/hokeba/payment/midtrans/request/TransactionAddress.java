package com.hokeba.payment.midtrans.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import models.Address;

public class TransactionAddress {
	@JsonProperty("first_name")
	public String first_name;
	@JsonProperty("last_name")
	public String last_name;
	@JsonProperty("email")
	public String email;
	@JsonProperty("phone")
	public String phone;
	@JsonProperty("address")
	public String address;
	@JsonProperty("city")
	public String city;
	@JsonProperty("postal_code")
	public String postal_code;
	@JsonProperty("country_code")
	public String country_code;
	
	public TransactionAddress() {
		super();
	}
	
	public TransactionAddress(Address address) {
		this.first_name = address.name;
//		this.last_name = address.lastName;
//		this.email = address.email;
//		this.phone = address.phone1;
		this.address = address.address;
		if (this.address != null && this.address.length() > 200) {
			this.address = this.address.substring(0, 200);
		}
		this.city = address.getCity();
		if (this.city != null && this.city.length() > 100) {
			this.city = this.city.substring(0, 100);
		}
//		this.postal_code = address.getPostalCode();
		this.country_code = "IDN";
	}
}
