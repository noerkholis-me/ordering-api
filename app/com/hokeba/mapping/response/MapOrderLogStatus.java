package com.hokeba.mapping.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by nugraha on 5/25/17.
 */

@JsonIgnoreProperties(ignoreUnknown=true)
public class MapOrderLogStatus {
    public String date;
    public String description;

    public MapOrderLogStatus(){

    }

    public MapOrderLogStatus(String date, String description) {
        this.date = date;
        this.description = description;
    }

    public String isDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String isDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
