package com.hokeba.mapping.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by hendriksaragih on 3/26/17.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class MapPromo {
    private String name;
    private String slug;

    @JsonProperty("image_src")
    private String imageSrc;
    @JsonProperty("image_responsive_src")
    private String imageResponsiveSrc;
    @JsonProperty("image_title")
    private String imageTitle;
    @JsonProperty("image_keyword")
    private String imageKeyword;
    @JsonProperty("image_description")
    private String imageDescription;
    @JsonProperty("product_detail")
    private Long productDetail;
    @JsonProperty("product_detail_slug")
    private String productDetailSlug;


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

    public String getImageResponsiveSrc() {
        return imageResponsiveSrc;
    }

    public void setImageResponsiveSrc(String imageResponsiveSrc) {
        this.imageResponsiveSrc = imageResponsiveSrc;
    }

    public Long getProductDetail() {
        return productDetail;
    }

    public void setProductDetail(Long productDetail) {
        this.productDetail = productDetail;
    }

    public String getProductDetailSlug() {
        return productDetailSlug;
    }

    public void setProductDetailSlug(String productDetailSlug) {
        this.productDetailSlug = productDetailSlug;
    }
}
