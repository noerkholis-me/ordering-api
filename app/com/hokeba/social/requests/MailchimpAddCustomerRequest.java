package com.hokeba.social.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MailchimpAddCustomerRequest {
	@JsonProperty("id")
	public String id;
	
	@JsonProperty("email_address")
	public String emailAddress;

	@JsonProperty("opt_in_status")
	public boolean optInStatus;
	
	@JsonProperty("first_name")
	public String name;
	
	public MailchimpAddCustomerRequest() {
		id = "";
		emailAddress="";
		optInStatus = true;
		name = "";
	}
	
	public MailchimpAddCustomerRequest(String id,String emailAddress, boolean optInStatus, String name) {
		this.id = id;
		this.emailAddress= emailAddress;
		this.optInStatus = true;
		this.name = name;
	}
}
