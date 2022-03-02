package com.hokeba.mapping.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by hendriksaragih on 3/2/17.
 */

@JsonIgnoreProperties(ignoreUnknown=true)
public class MapKeyValue {
    private String name;
    private String value;
    private String additional;

    public MapKeyValue(){

    }

    public MapKeyValue(String name, String value){
        this.name = name;
        this.value = value;
    }

    public MapKeyValue(String name, String value, String additional){
        this.name = name;
        this.value = value;
        this.additional = additional;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getAdditional() {
        return additional;
    }

    public void setAdditional(String additional) {
        this.additional = additional;
    }
}
