package com.hokeba.mapping.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by hendriksaragih on 4/11/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MapPoReturn {
    @JsonProperty("po_id")
    private Integer poId;
    //odoo
    @JsonProperty("odoo_id")
    private Integer odooId;
    @JsonProperty("description")
    private String description;
    @JsonProperty("type")
    private String type;

    private List<MapPoReturnDetail> items;

    public Integer getPoId() {
        return poId;
    }

    public void setPoId(Integer poId) {
        this.poId = poId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<MapPoReturnDetail> getItems() {
        return items;
    }

    public void setItems(List<MapPoReturnDetail> items) {
        this.items = items;
    }

    public Integer getOdooId() {
        return odooId;
    }

    public void setOdooId(Integer odooId) {
        this.odooId = odooId;
    }
}
