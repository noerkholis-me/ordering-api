package com.hokeba.mapping.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by nugraha on 5/25/17.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class MapOrderUserDetail {
    public Long id;
    @JsonProperty("order_date")
    public String orderDate;
    @JsonProperty("order_notes")
    public String orderNotes;

    @JsonProperty("list_product")
    public List<MapOrderUserProduct> products;

    public MapOrderUserDetail(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrderNotes() {
		return orderNotes;
	}

	public void setOrderNotes(String orderNotes) {
		this.orderNotes = orderNotes;
	}

	public List<MapOrderUserProduct> getProducts() {
        return products;
    }

    public void setProducts(List<MapOrderUserProduct> products) {
        this.products = products;
    }
}