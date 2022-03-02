package com.hokeba.shipping.beeexpress.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by hendriksaragih on 7/28/17.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class TownResponse {
    private String statediv;

    public String getStatediv() {
        return statediv;
    }

    public void setStatediv(String statediv) {
        this.statediv = statediv;
    }

    @Override
    public String toString() {
        return "TownResponse{" +
                "statediv='" + statediv + '\'' +
                '}';
    }
}
