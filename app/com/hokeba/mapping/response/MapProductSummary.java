package com.hokeba.mapping.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by hendriksaragih on 3/2/17.
 */

@JsonIgnoreProperties(ignoreUnknown=true)
public class MapProductSummary {
    @JsonProperty("in_stock")
    private Integer inStock;
    @JsonProperty("out_of_stock")
    private Integer outOfStock;
    private Integer inactive;

    public MapProductSummary(){

    }

    public MapProductSummary(int inStock, int outOfStock, int inactive){
        this.inStock = inStock;
        this.outOfStock = outOfStock;
        this.inactive = inactive;
    }

    public Integer getInStock() {
        return inStock;
    }

    public void setInStock(Integer inStock) {
        this.inStock = inStock;
    }

    public Integer getOutOfStock() {
        return outOfStock;
    }

    public void setOutOfStock(Integer outOfStock) {
        this.outOfStock = outOfStock;
    }

    public Integer getInactive() {
        return inactive;
    }

    public void setInactive(Integer inactive) {
        this.inactive = inactive;
    }
}
