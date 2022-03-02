package com.hokeba.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * Created by hendriksaragih on 3/25/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class HomePage {

    private Object banner;
    private Object brand;
    private Object partner;
    private Object promo;
    @JsonProperty("most_popular")
    private Object mostPopular;
    @JsonProperty("category_promo")
    private Object categoryProm;
    @JsonProperty("additional_category")
    private Object additionalCategory;
    
    public Object getAdditionalCategory() {
		return additionalCategory;
	}

	public void setAdditionalCategory(Object additionalCategory) {
		this.additionalCategory = additionalCategory;
	}

	public Object getPartner() {
		return partner;
	}

	public void setPartner(Object partner) {
		this.partner = partner;
	}

	public Object getBanner() {
        return banner;
    }

    public void setBanner(Object banner) {
        this.banner = banner;
    }

    public Object getBrand() {
        return brand;
    }

    public void setBrand(Object brand) {
        this.brand = brand;
    }

    public Object getPromo() {
        return promo;
    }

    public void setPromo(Object promo) {
        this.promo = promo;
    }

    public Object getMostPopular() {
        return mostPopular;
    }

    public void setMostPopular(Object mostPopular) {
        this.mostPopular = mostPopular;
    }

    public Object getCategoryProm() {
        return categoryProm;
    }

    public void setCategoryProm(Object categoryProm) {
        this.categoryProm = categoryProm;
    }
}