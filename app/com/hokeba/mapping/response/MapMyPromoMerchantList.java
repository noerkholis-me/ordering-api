package com.hokeba.mapping.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by hendriksaragih on 5/25/17.
 */

@JsonIgnoreProperties(ignoreUnknown=true)
public class MapMyPromoMerchantList {

    private String id;
    private String name;
    @JsonProperty("banner_image")
    private String bannerImage;
    @JsonProperty("start_date")
    private String startDate;
    @JsonProperty("end_date")
    private String endDate;
    @JsonProperty("number_of_pending")
    private int numberPending;
    @JsonProperty("number_of_approved")
    private int numberApproved;
    @JsonProperty("number_of_rejected")
    private int numberRejected;
    @JsonProperty("status")
    private String status;
    @JsonProperty("request_status")
    private String requestStatus;

    public MapMyPromoMerchantList(){}

    public MapMyPromoMerchantList(String id, String name, String startDate, String endDate, int numberPending, int numberApproved, int numberRejected, String status) {
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
        this.numberPending = numberPending;
        this.numberApproved = numberApproved;
        this.numberRejected = numberRejected;
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

    public int getNumberPending() {
        return numberPending;
    }

    public void setNumberPending(int numberPending) {
        this.numberPending = numberPending;
    }

    public int getNumberApproved() {
        return numberApproved;
    }

    public void setNumberApproved(int numberApproved) {
        this.numberApproved = numberApproved;
    }

    public int getNumberRejected() {
        return numberRejected;
    }

    public void setNumberRejected(int numberRejected) {
        this.numberRejected = numberRejected;
    }

    public String getStatus() {
//        String result = "";
//        switch (status){
//            case Promo.PROMO_STATUS_ACTIVE : result = "Active"; break;
//            case Promo.PROMO_STATUS_OPEN : result = "Open"; break;
//            case Promo.PROMO_STATUS_REVIEW : result = "Review"; break;
//            case Promo.PROMO_STATUS_EXPIRED : result = "Expired"; break;
//        }
//        return result;
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
