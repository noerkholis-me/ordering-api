package models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hokeba.util.CommonFunction;
import lombok.*;
import models.merchant.*;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Date;

import utils.BigDecimalSerialize;
import java.math.BigDecimal;

@Entity
public class ProductStore extends BaseModel {
    private static final long serialVersionUID = 1L;

    @Getter @Setter
    @JsonProperty("discount_type")
    public String discountType;

    @Getter @Setter
    @JsonProperty("discount")
    public Double discount;

    @Getter @Setter
    @JsonProperty("store_price")
    public BigDecimal storePrice;

    @Getter @Setter
    @JsonProperty("final_price")
    public BigDecimal finalPrice;

    @Setter @Getter
    @JsonProperty("is_active")
    @Column(name = "is_active")
    public boolean isActive;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="merchant_id", referencedColumnName = "id")
    @Getter @Setter
    public Merchant merchant;
    
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="product_id", referencedColumnName = "id")
    @Getter @Setter
    public ProductMerchant productMerchant;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="store_id", referencedColumnName = "id")
    @Getter @Setter
    public Store store;
}