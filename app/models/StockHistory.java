package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hokeba.util.Constant;

import dtos.product.ProductStoreResponse;
import models.merchant.ProductMerchant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
public class StockHistory extends BaseModel {


    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "product_store_id", referencedColumnName = "id")
    public ProductStore productStore;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "merchant_id", referencedColumnName = "id")
    public Merchant merchant;

    @Column(name = "notes")
    public String notes;

    @Column(name = "stock")
    public Integer stock;

    @Column(name = "stock_changes")
    public Integer stockChanges;

    public StockHistory(Merchant merchant, Store store, ProductMerchant productMerchant, ProductStore productStore, Boolean isActive) {
        this.setProductStore(productStore);
        this.setMerchant(merchant);
        this.setNotes("Admin");
        this.setStock(productStore.getStock().intValue());
        this.setStockChanges(0);
    }

}
