package com.hokeba.payment.kredivo.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import models.Merchant;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class KredivoSeller {
	@JsonProperty("id")
	public String id;
	@JsonProperty("name")
	public String name;
	@JsonProperty("email")
	public String email;
	@JsonProperty("url")
	public String url;
	@JsonProperty("legal_id")
	public String legalId;
	@JsonProperty("address")
	public KredivoAddress address;
	
	public KredivoSeller() {
		super();
	}
	
	public KredivoSeller(Merchant model) {
		this.id = model.merchantCode;
		this.name = model.name;
		this.email = model.email;
		this.url = model.merchantUrlPage;
		this.address = new KredivoAddress(model);
	}
	
}
