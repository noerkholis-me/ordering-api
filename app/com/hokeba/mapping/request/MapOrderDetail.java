package com.hokeba.mapping.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by hendriksaragih on 4/11/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MapOrderDetail {
    @JsonProperty("bag_id")//additional attribute for database bag
    private Long bagId;
	@JsonProperty("product_id")
	private Long productId;
    @JsonProperty("product_variant_id")
    private Long productVariantId;
    @JsonProperty("quantity")
    private Integer quantity;
    @JsonProperty("size_id")
    private Long sizeId;    

    public Long getBagId() {
		return bagId;
	}

	public void setBagId(Long bagId) {
		this.bagId = bagId;
	}

	public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }
    
    public Long getProductVariantId() {
		return productVariantId;
	}

	public void setProductVariantId(Long productVariantId) {
		this.productVariantId = productVariantId;
	}

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Long getSizeId() {
        return sizeId;
    }

    public void setSizeId(Long sizeId) {
        this.sizeId = sizeId;
    }
}
