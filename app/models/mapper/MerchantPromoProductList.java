package models.mapper;

import com.avaje.ebean.annotation.Sql;

import javax.persistence.Entity;

@Entity
@Sql
public class MerchantPromoProductList {
    public String id;
    public String name;
    public String sku;
    public String status;
    public Double price;
    public Long stock;

    public java.lang.String getId() {
        return id;
    }

    public void setId(java.lang.String id) {
        this.id = id;
    }

    public java.lang.String getName() {
        return name;
    }

    public void setName(java.lang.String name) {
        this.name = name;
    }

    public java.lang.String getSku() {
        return sku;
    }

    public void setSku(java.lang.String sku) {
        this.sku = sku;
    }

    public java.lang.String getStatus() {
        String result = "";
        if(status != null ){
            result = status;
            if(status.equals("P")){
                result = "Review";
            }else if(status.equals("R")){
                result = "Rejected";
            }else if(status.equals("A")){
                result = "Approved";
            }
        }
        return result;
    }

    public void setStatus(java.lang.String status) {
        this.status = status;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Long getStock() {
        return stock;
    }

    public void setStock(Long stock) {
        this.stock = stock;
    }
}