package com.hokeba.mapping.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


/**
 * Created by hendriksaragih on 3/2/17.
 */

@JsonIgnoreProperties(ignoreUnknown=true)
public class MapMasterProduct {
    private MapMasterBrand[] brands;
    private MapMasterCategory[] categories;
    private MapAttributeAll[] attributes;
    private MapSize[] sizes;
    private MapProduct product;

    public MapMasterBrand[] getBrands() {
        return brands;
    }

    public void setBrands(MapMasterBrand[] brands) {
        this.brands = brands;
    }

    public MapMasterCategory[] getCategories() {
        return categories;
    }

    public void setCategories(MapMasterCategory[] categories) {
        this.categories = categories;
    }

    public MapAttributeAll[] getAttributes() {
        return attributes;
    }

    public void setAttributes(MapAttributeAll[] attributes) {
        this.attributes = attributes;
    }

    public MapProduct getProduct() {
        return product;
    }

    public void setProduct(MapProduct product) {
        this.product = product;
    }

    public MapSize[] getSizes() {
        return sizes;
    }

    public void setSizes(MapSize[] sizes) {
        this.sizes = sizes;
    }
}


