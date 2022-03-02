package models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Created by nugraha on 5/29/17.
 */
@Entity
public class PurchaseOrderReturnDetail extends BaseModel {
    private static final long serialVersionUID = 1L;

    public static Finder<Long, PurchaseOrderReturnDetail> find = new Finder<>(Long.class,
            PurchaseOrderReturnDetail.class);

    @JsonIgnore
    @ManyToOne
    public PurchaseOrderReturn purchaseOrderReturn;

    @JsonIgnore
    @ManyToOne
    public Product product;

    public int quantity;


}
