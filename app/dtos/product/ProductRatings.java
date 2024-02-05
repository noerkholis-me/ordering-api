package dtos.product;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import models.BaseModel;
import models.Store;
import models.merchant.ProductMerchant;

import javax.persistence.*;

@Table(name = "product_ratings")
@Getter
@Setter
@Entity
public class ProductRatings extends BaseModel {
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "store_id", referencedColumnName = "id")
    public Store store;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "product_merchant_id", referencedColumnName = "id")
    public ProductMerchant productMerchant;

    @Column(columnDefinition = "text")
    private String feedback;

    private float rate;
}
