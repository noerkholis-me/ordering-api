package models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
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
    public int stock;

    @Column(name = "stock_changes")
    public int stockChanges;

}
