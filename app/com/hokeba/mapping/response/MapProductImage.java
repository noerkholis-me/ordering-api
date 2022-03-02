package com.hokeba.mapping.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by hendriksaragih on 4/8/17.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class MapProductImage {
    @JsonProperty("full_image_url")
    public String fullImageUrl;
    @JsonProperty("medium_image_url")
    private String mediumImageUrl;
    @JsonProperty("thumbnail_image_url")
    private String thumbnailImageUrl;
    @JsonProperty("blur_image_url")
    private String blurImageUrl;

    public String getFullImageUrl() {
        return fullImageUrl;
    }

    public void setFullImageUrl(String fullImageUrl) {
        this.fullImageUrl = fullImageUrl;
    }

    public String getMediumImageUrl() {
        return mediumImageUrl;
    }

    public void setMediumImageUrl(String mediumImageUrl) {
        this.mediumImageUrl = mediumImageUrl;
    }

    public String getThumbnailImageUrl() {
        return thumbnailImageUrl;
    }

    public void setThumbnailImageUrl(String thumbnailImageUrl) {
        this.thumbnailImageUrl = thumbnailImageUrl;
    }

    public String getBlurImageUrl() {
        return blurImageUrl;
    }

    public void setBlurImageUrl(String blurImageUrl) {
        this.blurImageUrl = blurImageUrl;
    }
}