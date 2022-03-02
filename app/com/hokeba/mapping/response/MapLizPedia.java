package com.hokeba.mapping.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MapLizPedia {
	private String content;
	
	private String name;
	
    private String slug;
    
    @JsonProperty("meta_title")
    private String metaTitle;
    
    @JsonProperty("meta_keyword")
    private String metaKeyword;
    
    @JsonProperty("meta_description")
    private String metaDescription;
    
    @JsonProperty("image_url")
    private String imageUrl;


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
    
    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getMetaTitle() {
        return metaTitle;
    }

    public void setMetaTitle(String metaTitle) {
        this.metaTitle = metaTitle;
    }

    public String getMetaKeyword() {
        return metaKeyword;
    }
    
    public void setMetaKeyword(String metaKeyword) {
        this.metaKeyword = metaKeyword;
    }

    public String getMetaDescription() {
    	return metaDescription;
    }
    
    public void setMetaDescription(String metaDescription) {
    	this.metaDescription = metaDescription;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

}
