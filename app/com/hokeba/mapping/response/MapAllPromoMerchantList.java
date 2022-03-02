package com.hokeba.mapping.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by hendriksaragih on 5/25/17.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class MapAllPromoMerchantList {

    private String id;
    private String name;

    @JsonProperty("banner_image")
    private String bannerImage;
    @JsonProperty("start_date")
    private String startDate;
    @JsonProperty("end_date")
    private String endDate;
    @JsonProperty("number_of_product")
    private int numberProduct;
    @JsonProperty("number_of_seller")
    private int numberSeller;
    @JsonProperty("status")
    private String status;
    @JsonProperty("request_status")
    private String requestStatus;
    @JsonProperty("is_join")
    private Boolean isJoin;

    public MapAllPromoMerchantList(){}

    public MapAllPromoMerchantList(String id, String name, String bannerImage, String startDate, String endDate, int numberProduct, int numberSeller, String status) {
        this.id = id;
        this.name = name;
        this.bannerImage = bannerImage;
        this.startDate = startDate;
        this.endDate = endDate;
        this.numberProduct = numberProduct;
        this.numberSeller = numberSeller;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBannerImage() {
        return bannerImage;
    }

    public void setBannerImage(String bannerImage) {
        this.bannerImage = bannerImage;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public int getNumberProduct() {
        return numberProduct;
    }

    public void setNumberProduct(int numberProduct) {
        this.numberProduct = numberProduct;
    }

    public int getNumberSeller() {
        return numberSeller;
    }

    public void setNumberSeller(int numberSeller) {
        this.numberSeller = numberSeller;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
