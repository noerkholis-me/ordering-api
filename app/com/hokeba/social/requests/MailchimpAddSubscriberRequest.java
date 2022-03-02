package com.hokeba.social.requests;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MailchimpAddSubscriberRequest {
    public static final String SUBSCRIBED = "subscribed";
    public static final String UNSUBSCRIBED = "unsubscribed";
    public static final String CLEANED = "cleaned";
    public static final String PENDING = "pending";
    public static final String TRANSACTIONAL = "transactional";
    public static final String ARCHIVED = "archived";
    
	@JsonProperty("email_address")
	private String email_address;
	
	@JsonProperty("status")
	private String status;

	@JsonProperty("merge_fields")
	private MailchimpMergeFields merge_fields;
	
	public MailchimpAddSubscriberRequest(String email, String status, String fname, String lname, String bday) {
		email_address = email;
		this.status = status;
		merge_fields = new MailchimpMergeFields(fname, lname, bday);
	}
	
	public MailchimpAddSubscriberRequest() {
		merge_fields = new MailchimpMergeFields("", "", "");
	}
	
	public String getEmailAddress() {
		return email_address;
	}
	public void setEmailAddress(String email_address) {
		this.email_address = email_address;
	}
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getFname() {
		return merge_fields.getFname();
	}
	public void setFname(String fname) {
		merge_fields.setFname(fname);
	}
	
	public String getLname() {
		return merge_fields.getLname();
	}
	public void setLname(String lname) {
		merge_fields.setLname(lname);
	}
	
	public String getBday() {
		return merge_fields.getBday();
	}
	public void setBday(String bday) {
		merge_fields.setBday(bday);
	}
}
