package com.hokeba.mapping.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

/**
 * Created by hendriksaragih on 3/24/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MapArticle {
    private String title;
    private String slug;
    @JsonProperty("image_name")
    private String imageName;
    @JsonProperty("image_title")
    private String imageTitle;
    @JsonProperty("image_alternate")
    private String imageAlternate;
    @JsonProperty("image_description")
    private String imageDescription;
    @JsonProperty("image_url")
    private String imageUrl;
    @JsonProperty("image_home_url")
    private String imageHomeUrl;
    @JsonProperty("image_list_url")
    private String imageListUrl;
    @JsonProperty("short_description")
    private String shortDescription;
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "Asia/Jakarta")
    public Date createdAt;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getImageTitle() {
        return imageTitle;
    }

    public void setImageTitle(String imageTitle) {
        this.imageTitle = imageTitle;
    }

    public String getImageAlternate() {
        return imageAlternate;
    }

    public void setImageAlternate(String imageAlternate) {
        this.imageAlternate = imageAlternate;
    }

    public String getImageDescription() {
        return imageDescription;
    }

    public void setImageDescription(String imageDescription) {
        this.imageDescription = imageDescription;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

	public String getImageHomeUrl() {
		return imageHomeUrl;
	}

	public void setImageHomeUrl(String imageHomeUrl) {
		this.imageHomeUrl = imageHomeUrl;
	}

	public String getImageListUrl() {
		return imageListUrl;
	}

	public void setImageListUrl(String imageListUrl) {
		this.imageListUrl = imageListUrl;
	}
    
    
}
