package com.hokeba.mapping.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by hendriksaragih on 3/2/17.
 */

@JsonIgnoreProperties(ignoreUnknown=true)
public class MapShippingCost {
    private Long id;
    private String name;
    private Double price;
    private String sdate;
    private String edate;
    private String currency;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getSdate() {
        return sdate;
    }

    public void setSdate(String sdate) {
        this.sdate = sdate;
    }

    public String getEdate() {
        return edate;
    }

    public void setEdate(String edate) {
        this.edate = edate;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
