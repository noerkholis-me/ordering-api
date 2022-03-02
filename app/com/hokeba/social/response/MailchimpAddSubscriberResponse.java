package com.hokeba.social.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class MailchimpAddSubscriberResponse {
	@JsonProperty("type")
	private String type;
	
	@JsonProperty("title")
	private String title;
	
	@JsonProperty("status")
	private String status;

	@JsonProperty("detail")
	private String detail;

	@JsonProperty("instance")
	private String instance;
	
	public String GetType() {
		return type;
	}
	public void SetType(String type) {
		this.type = type;
	}
	
	public String GetTitle() {
		return title;
	}
	public void SetTitle(String title) {
		this.title = title;
	}
	
	public String GetStatus() {
		return status;
	}
	public void SetStatus(String status) {
		this.status = status;
	}
	
	public String GetDetail() {
		return detail;
	}
	public void SetDetail(String detail) {
		this.detail = detail;
	}
	
	public String GetInstance() {
		return instance;
	}
	public void SetInstance(String instance) {
		this.instance = instance;
	}
}
