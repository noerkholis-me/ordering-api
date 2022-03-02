package models.mapper;

import com.avaje.ebean.annotation.Sql;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hokeba.util.Constant;
import models.Merchant;
import models.Vendor;

import javax.persistence.Entity;

@Entity
@Sql
public class PromoProductList {
    public Long id;
    public String name;
    public String sku;
    public String image;
    public String seller;
    public Long merchant;
    public Long vendor;
    public Double price;
    public Long stock;

    @JsonProperty("seller")
    public String getSeller() {
        if (merchant != null){
            Merchant merchantModel = Merchant.find.byId(merchant);
            return merchantModel.name;
        }else{
            Vendor vendorModel = Vendor.find.byId(vendor);
            return vendorModel.name;
        }
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getImage() {
        return image==null || image.isEmpty() ? "" :
                (image.startsWith("http") ? image : Constant.getInstance().getImageUrl() + image);
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Long getMerchant() {
        return merchant;
    }

    public void setMerchant(Long merchant) {
        this.merchant = merchant;
    }

    public Long getVendor() {
        return vendor;
    }

    public void setVendor(Long vendor) {
        this.vendor = vendor;
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