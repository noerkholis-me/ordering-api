package com.hokeba.social.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

import models.Member;

public class MailchimpCustomerRequest {
	@JsonProperty("id")
	public String id;
	
	@JsonProperty("email_address")
	public String emailAddress;

	@JsonProperty("opt_in_status")
	public boolean optInStatus;
	
	@JsonProperty("first_name")
	public String firstname;
	
	@JsonProperty("last_name")
	public String lastname;
	
	public MailchimpCustomerRequest() {
		id = "";
		emailAddress="";
		optInStatus = true;
		firstname = "";
		lastname = "";
	}
	
	public MailchimpCustomerRequest(String id,String emailAddress, boolean optInStatus, String fullname) {
		this.id = id;
		this.emailAddress= emailAddress;
		this.optInStatus = true;
		this.firstname = fullname;
		this.lastname = "";
	}
	
	public MailchimpCustomerRequest(Member member) {
		this.id = member.id.toString();
		this.emailAddress= member.email;
		this.optInStatus = true;
		this.firstname = member.fullName;
		this.lastname = "";
	}
}
