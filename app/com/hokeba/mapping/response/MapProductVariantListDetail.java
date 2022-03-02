package com.hokeba.mapping.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MapProductVariantListDetail {
	@JsonProperty("id")
	public Long id;
	@JsonProperty("total_stock")
	public long totalStock;
	
	@JsonProperty("product_name")
	public String productName;
	@JsonProperty("color_name")
	public String colorName;
	@JsonProperty("size_name")
	public String sizeName;
	
	@JsonProperty("product_id")
	public Long productId;
	@JsonProperty("color_id")
	public Long colorId;
	@JsonProperty("size_id")
	public Long sizeId;
}
