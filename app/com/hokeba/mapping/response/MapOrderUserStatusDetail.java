package com.hokeba.mapping.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by nugraha on 6/21/17.
 */

@JsonIgnoreProperties(ignoreUnknown=true)
public class MapOrderUserStatusDetail {
    private boolean status;
    @JsonProperty("last_detail_status")
    private String lastDetailStatus;
    @JsonProperty("last_status_date")
    private String lastStatusDate;
    @JsonProperty("tracking_number")
    private String trackingNumber;

    public MapOrderUserStatusDetail(){

    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getLastDetailStatus() {
        return lastDetailStatus==null? "":lastDetailStatus;
    }

    public void setLastDetailStatus(String lastDetailStatus) {
        this.lastDetailStatus = lastDetailStatus;
    }

    public String getLastStatusDate() {
        return lastStatusDate==null? "":lastStatusDate;
    }

    public void setLastStatusDate(String lastStatusDate) {
        this.lastStatusDate = lastStatusDate;
    }

    public String getTrackingNumber() {
        return trackingNumber==null? "":trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }
}
