package com.hokeba.mapping.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by nugraha on 7/3/17.
 */

@JsonIgnoreProperties(ignoreUnknown=true)
public class MapVoucher {
    @JsonProperty("code")
    private String code;
    @JsonProperty("start_date")
    private String startDate;
    @JsonProperty("end_date")
    private String endDate;
    @JsonProperty("status")
    private String status;

    public MapVoucher(){

    }

    public java.lang.String getCode() {
        return code;
    }

    public void setCode(java.lang.String code) {
        this.code = code;
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
        return status;
    }

    public void setStatus(java.lang.String voucherStatus) {
        this.status = voucherStatus;
    }
}
