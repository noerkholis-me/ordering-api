package com.hokeba.payment.kredivo.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class KredivoCallbackShippingAddressResponse {
	@JsonProperty("first_name")
	public String firstName;
	@JsonProperty("last_name")
	public String lastName;
	
	@JsonProperty("country_code")
	public String countryCode;
	@JsonProperty("city")
	public String city;
	@JsonProperty("state")
	public String state;
	@JsonProperty("phone")
	public String phone;
	@JsonProperty("postcode")
	public String postcode;
	@JsonProperty("location_details")
	public String locationDetails;
	
	@JsonProperty("creation_date")
	public String creationDate;
	@JsonProperty("transaction")
	public Integer transaction;
	
}
