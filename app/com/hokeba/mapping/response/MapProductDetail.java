package com.hokeba.mapping.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by hendriksaragih on 4/8/17.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class MapProductDetail {

    public Double weight;
    @JsonProperty("dimension_x")
    public Double dimensionX;
    @JsonProperty("dimension_y")
    public Double dimensionY;
    @JsonProperty("dimension_z")
    public Double dimensionZ;
    
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
    
    @JsonProperty("what_in_the_box")
    public String whatInTheBox;

    public String warranty;
    public String description;
    public String dimension;
    public List<MapKeyValue> attributes;
    @JsonProperty("attributes_s")
    public List<MapAttribute> attributesS;
    @JsonProperty("short_description")
    public List<String> shortDescription;
    @JsonProperty("product_images")
    public List<MapProductImage> productImages;

    @JsonProperty("warranty_type")
    public int warrantyType;
    @JsonProperty("warranty_period")
    public int warrantyPeriod;
    @JsonProperty("size_guide")
    public String sizeGuide;

}