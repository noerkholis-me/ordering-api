package models.mapper;

import com.avaje.ebean.annotation.Sql;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Entity;

@Entity
@Sql
public class TopSalesReportMerchant {
    public Long id;
    public String sku;
    @JsonProperty("product_name")
    public String productName;
    public String category;
    public String brand;
    public Long qty;
    public Long sold;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public Long getQty() {
        return qty;
    }

    public void setQty(Long qty) {
        this.qty = qty;
    }

    public Long getSold() {
        return sold;
    }

    public void setSold(Long sold) {
        this.sold = sold;
    }

}