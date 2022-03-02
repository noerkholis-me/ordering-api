package com.hokeba.mapping.request;

import java.util.List;
import java.util.Map;

import javax.persistence.Column;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MapProductMerchant2 {
	public Long id;
    public String name;
    public String sku;
    @JsonProperty("meta_title")
    public String metaTitle;
    @JsonProperty("meta_keyword")
    public String metaKeyword;
    @JsonProperty("meta_description")
    public String metaDescription;
    
    @JsonProperty("brand_id")
    public Long brandId;
    @JsonProperty("category_id")
    public Long categoryId;
    @JsonProperty("sub_category_id")
    public Long subCategoryId;
    @JsonProperty("sub_sub_category_id")
    public Long subSubCategoryId;
    
    public Double price;
    @JsonProperty("strike_through_display")
	public Double strikeThroughDisplay;
	@JsonProperty("price_display")
	public Double priceDisplay;
	
	public Double discount;
	@JsonProperty("discount_type")
	public int discountType;
	@JsonProperty("from_date")
	public String fromDate = "";
	@JsonProperty("to_date")
	public String toDate = "";
	@JsonProperty("from_time")
	public String fromTime = "";
	@JsonProperty("to_time")
	public String toTime = "";
    
    public Double weight;
    public String dimension;
	public Double dimension1;
	public Double dimension2;
	public Double dimension3;
	public Double diameter;
	@JsonProperty("number_of_diamond")
	public Double numberOfDiamond;
	@JsonProperty("diamond_color")
	public String diamondColor;
	@JsonProperty("diamond_clarity")
	public String diamondClarity;
	public String stamp;
	public String certificate;
	public Double kadar;
	@JsonProperty("weight_of_gold")
	public Double weightOfGold;
	@JsonProperty("sum_carat_of_gold")
	public Double sumCaratOfGold;
	@JsonProperty("weight_of_gold_plus_diamond")
	public Double weightOfGoldPlusDiamond;
	
    public Integer warranty;
    @JsonProperty("warranty_period")
    public Integer warrantyPeriod;
    
    public Map<Long, Long> attribute;
    
    public List<String> images;
    
    @JsonProperty("short_description")
    public List<String> shortDescription;
    @JsonProperty("long_description")
    public String longDescription;
    @JsonProperty("whats_in_the_box")
    public String whatsInTheBox;
    @JsonProperty("size_guide")
    public String sizeGuide;
    
}
