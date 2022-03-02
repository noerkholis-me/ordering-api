package com.hokeba.mapping.request;

import java.util.List;
import java.util.Map;

import javax.persistence.Column;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MapPickUpPoint {
	
		@JsonProperty("merchant_id")
	 	public long merchantId;

	    @JsonProperty("name")
	    public String name;
	    
	    @JsonProperty("address")
	    public String address;

	    @JsonProperty("contact")
	    public String contact;

	    @JsonProperty("duration")
	    public long duration;

	    @JsonProperty("latitude")
	    public double latitude;
	    
	    @JsonProperty("longitude")
	    public double longitude;
}
