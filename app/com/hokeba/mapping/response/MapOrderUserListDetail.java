package com.hokeba.mapping.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by nugraha on 5/25/17.
 */

@JsonIgnoreProperties(ignoreUnknown=true)
public class MapOrderUserListDetail {
    @JsonProperty("product_name")
    private String productName ;
    @JsonProperty("product_img")
    private String productImg;
    @JsonProperty("qty")
    private int qty;

    public MapOrderUserListDetail(){

    }

    public MapOrderUserListDetail(String productName, String productImg, int qty) {
        this.productName = productName;
        this.productImg = productImg;
        this.qty = qty;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductImg() {
        return productImg;
    }

    public void setProductImg(String productImg) {
        this.productImg = productImg;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }
}
