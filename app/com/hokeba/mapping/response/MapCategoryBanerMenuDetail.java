package com.hokeba.mapping.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by hendriksaragih on 3/2/17.
 */

@JsonIgnoreProperties(ignoreUnknown=true)
public class MapCategoryBanerMenuDetail {
    private Long id;
    private Integer sequence;
    private String slug;
    @JsonProperty("image_src")
    private String imageLink;
    @JsonProperty("product_detail")
    private Long productDetail;
    @JsonProperty("product_detail_slug")
    private String productDetailSlug;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
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
