package com.hokeba.mapping.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class MapMostPopularBanner {
    private Integer sequence;
    private String slug;
    @JsonProperty("image_src")
    private String imageSrc;
    @JsonProperty("image_title")
    private String imageTitle;
    @JsonProperty("image_keyword")
    private String imageKeyword;
    @JsonProperty("image_description")
    private String imageDescription;

    @JsonProperty("product_id")
    public MapProductListWithoutImage product;

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getImageSrc() {
        return imageSrc;
    }

    public void setImageSrc(String imageSrc) {
        this.imageSrc = imageSrc;
    }

    public String getImageTitle() {
        return imageTitle;
    }

    public void setImageTitle(String imageTitle) {
        this.imageTitle = imageTitle;
    }

    public String getImageKeyword() {
        return imageKeyword;
    }

    public void setImageKeyword(String imageKeyword) {
        this.imageKeyword = imageKeyword;
    }

    public String getImageDescription() {
        return imageDescription;
    }

    public void setImageDescription(String imageDescription) {
        this.imageDescription = imageDescription;
    }

}
