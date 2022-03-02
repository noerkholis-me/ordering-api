package com.hokeba.mapping.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Created by hendriksaragih on 3/2/17.
 */

@JsonIgnoreProperties(ignoreUnknown=true)
public class MapMerchantShipping {
    private Long id;
    private String name;
    private List<MapShippingCost> costs;

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

    public List<MapShippingCost> getCosts() {
        return costs;
    }

    public void setCosts(List<MapShippingCost> costs) {
        this.costs = costs;
    }
}
