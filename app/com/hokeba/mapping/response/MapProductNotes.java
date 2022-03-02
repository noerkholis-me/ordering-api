package com.hokeba.mapping.response;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by hendriksaragih on 4/11/17.
 */
public class MapProductNotes {
    @JsonProperty("status")
    private Boolean status;
    @JsonProperty("name")
    private String name;

    public MapProductNotes(){

    }

    public MapProductNotes(Boolean status, String name){
        this.status = status;
        this.name = name;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
