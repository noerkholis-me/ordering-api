package com.hokeba.mapping.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by hendriksaragih on 4/11/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MapSoReturn {
    @JsonProperty("invoice_id")
    private Integer invoiceId;
    @JsonProperty("description")
    private String description;
    @JsonProperty("type")
    private String type;
    //odoo
    @JsonProperty("odoo_id")
    private Integer odooId;

    private List<MapSoReturnDetail> items;

    public Integer getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(Integer invoiceId) {
        this.invoiceId = invoiceId;
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

    public List<MapSoReturnDetail> getItems() {
        return items;
    }

    public void setItems(List<MapSoReturnDetail> items) {
        this.items = items;
    }

    public Integer getOdooId() {
        return odooId;
    }

    public void setOdooId(Integer odooId) {
        this.odooId = odooId;
    }
}
