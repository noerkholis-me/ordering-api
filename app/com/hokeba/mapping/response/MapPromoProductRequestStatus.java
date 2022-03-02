package com.hokeba.mapping.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by hendriksaragih on 5/25/17.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class MapPromoProductRequestStatus {

    private String id;
    @JsonProperty("promo_name")
    private String promoName;
    @JsonProperty("product_name")
    private String productName;
    @JsonProperty("banner_image")
    private String bannerImage;
    @JsonProperty("product_image")
    private String productImage;
    @JsonProperty("start_date")
    private String startDate;
    @JsonProperty("end_date")
    private String endDate;
    @JsonProperty("status")
    private String status;

    public MapPromoProductRequestStatus(){}

    public java.lang.String getId() {
        return id;
    }

    public void setId(java.lang.String id) {
        this.id = id;
    }

    public java.lang.String getPromoName() {
        return promoName;
    }

    public void setPromoName(java.lang.String promoName) {
        this.promoName = promoName;
    }

    public java.lang.String getProductName() {
        return productName;
    }

    public void setProductName(java.lang.String productName) {
        this.productName = productName;
    }

    public java.lang.String getBannerImage() {
        return bannerImage;
    }

    public void setBannerImage(java.lang.String bannerImage) {
        this.bannerImage = bannerImage;
    }

    public java.lang.String getStartDate() {
        return startDate;
    }

    public void setStartDate(java.lang.String startDate) {
        this.startDate = startDate;
    }

    public java.lang.String getEndDate() {
        return endDate;
    }

    public void setEndDate(java.lang.String endDate) {
        this.endDate = endDate;
    }

    public java.lang.String getStatus() {
        return (status!= null && status.equals("Pending"))? "Review":status ;
    }

    public void setStatus(java.lang.String status) {
        this.status = status;
    }
}
