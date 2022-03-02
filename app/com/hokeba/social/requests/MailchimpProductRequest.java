package com.hokeba.social.requests;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MailchimpProductRequest {
	@JsonProperty("id")
	public String id;

	@JsonProperty("title")
	public String title;

	@JsonProperty("url")
	public String url;

	@JsonProperty("description")
	public String description;

	@JsonProperty("type")
	public String type;

	@JsonProperty("vendor")
	public String vendor;

	@JsonProperty("image_url")
	public String imageUrl;

	@JsonProperty("variants")
	public List<MailchimpProductVariantRequest> variants;
	
	public MailchimpProductRequest() {
		this.id = "";
		this.title = "";
		this.url = "";
		this.description = "";
		this.type = "";
		this.vendor = "";
		this.imageUrl = "";
		this.variants = new ArrayList<MailchimpProductVariantRequest>();
	}

	//minimum required fields
	public MailchimpProductRequest(String id, String title, List<MailchimpProductVariantRequest> variants) {
		this.id = id;
		this.title = title;
		this.url = "";
		this.description = "";
		this.type = "";
		this.vendor = "";
		this.imageUrl = "";
		this.variants = variants;
	}
	
	public MailchimpProductRequest(String id, String title, String url, String description, String type, String vendor, String imageUrl, List<MailchimpProductVariantRequest> variants) {
		this.id = id;
		this.title = title;
		this.url = url;
		this.description = description;
		this.type = type;
		this.vendor = vendor;
		this.imageUrl = imageUrl;
		this.variants = variants;
	}
}
