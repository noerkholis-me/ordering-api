package com.hokeba.mapping.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by hendriksaragih on 5/1/17.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class MapVariantGroupList {
    private String name;
    @JsonProperty("product_id")
    private Long productId;
    @JsonProperty("product_sku")
    private String productSku;

    public MapVariantGroupList(){

    }

    public MapVariantGroupList(String name, Long productId, String productSku) {
        this.name = name;
        this.productId = productId;
        this.productSku = productSku;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductSku() {
        return productSku;
    }

    public void setProductSku(String productSku) {
        this.productSku = productSku;
    }
}
