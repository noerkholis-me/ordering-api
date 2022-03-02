package com.hokeba.mapping.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by hendriksaragih on 7/3/17.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class MapReturStatusDetail {
    private boolean status;
    private String date;

    public MapReturStatusDetail(){

    }

    public MapReturStatusDetail(boolean status, String date) {
        this.status = status;
        this.date = date;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
