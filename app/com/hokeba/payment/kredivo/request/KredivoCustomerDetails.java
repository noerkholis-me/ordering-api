package com.hokeba.payment.kredivo.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import models.Member;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class KredivoCustomerDetails {
	@JsonProperty("first_name")
	public String firstName;
	@JsonProperty("last_name")
	public String lastName;
	@JsonProperty("email")
	public String email;
	@JsonProperty("phone")
	public String phone;
	
	public KredivoCustomerDetails() {
		super();
	}
	
	public KredivoCustomerDetails(Member model) {
		this.firstName = model.fullName;
		this.lastName = null;
		this.email = model.email == null ? "-" : model.email;
		this.phone = model.phone == null ? "-" : model.phone;
	}
	
}
