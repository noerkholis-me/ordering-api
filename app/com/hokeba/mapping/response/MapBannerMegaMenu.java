package com.hokeba.mapping.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonIgnoreProperties(ignoreUnknown=true)
public class MapBannerMegaMenu {

    public Long id;
    @JsonProperty("url")
    public String url;
    @JsonProperty("image_url")
    public String imageUrl;
    
    public String getUrl() {
    	return url;
    }
    
    public String getImageUrl() {
    	return imageUrl;
    }
}
