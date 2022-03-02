package com.hokeba.mapping.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by hendriksaragih on 3/2/17.
 */

@JsonIgnoreProperties(ignoreUnknown=true)
public class MapSize {
    private Long id;
    private String international;
    private int eu;
    private float collar;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInternational() {
        return international;
    }

    public void setInternational(String international) {
        this.international = international;
    }

    public int getEu() {
        return eu;
    }

    public void setEu(int eu) {
        this.eu = eu;
    }

    public float getCollar() {
        return collar;
    }

    public void setCollar(float collar) {
        this.collar = collar;
    }
}
