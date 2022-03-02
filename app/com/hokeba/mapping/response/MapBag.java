package com.hokeba.mapping.response;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import models.Member;
import models.Bag;
import models.ProductDetailVariance;

@JsonIgnoreProperties(ignoreUnknown=true)
public class MapBag {

	@JsonProperty("id")
	public Long id;
	
    @JsonProperty("product_detail_variance_id")
    public Long productVariance;

    @JsonProperty("status")
    public String status;
    
	@JsonProperty("quantity")
	public Long quantity;

    @JsonProperty("product")
    public MapProductWithDetail product;
    
    @JsonProperty("product_variance_attribute")
    public MapProductDetailVariance productVarianceAttribute;
	
//    @JsonProperty("product_detail")
//    public MapProductDetail productDetail;
    
    public MapBag(Bag bag){
    	this.id = bag.id;
    	this.status = bag.status;
    	this.quantity = bag.quantity;
    	this.productVariance = bag.productVariance.id;
    	
    	this.product = new MapProductWithDetail(bag.productVariance.mainProduct);
    	
    	ProductDetailVariance pdv = ProductDetailVariance.find.byId(this.productVariance);
    	this.productVarianceAttribute = new MapProductDetailVariance(pdv);
    }
}
