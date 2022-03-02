package com.hokeba.mapping.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by hendriksaragih on 5/1/17.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class MapProductColor {
    private String image;
    @JsonProperty("product_id")
    private Long productId;
    @JsonProperty("product_slug")
    private String productSlug;
    @JsonProperty("color")
    private String color;

    public MapProductColor(){

    }

    public MapProductColor(String image, Long productId, String productSlug, String color) {
        this.image = image;
        this.productId = productId;
        this.productSlug = productSlug;
        this.color = color;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getProductSlug() {
        return productSlug;
    }

    public void setProductSlug(String productSlug) {
        this.productSlug = productSlug;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
