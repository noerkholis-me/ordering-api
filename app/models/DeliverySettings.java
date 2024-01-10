package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hokeba.util.Constant;

import dtos.product.ProductStoreResponse;
import models.merchant.ProductMerchant;

import javax.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "delivery_settings")
@Getter
@Setter
@Entity
public class DeliverySettings extends BaseModel {
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "store_id", referencedColumnName = "id")
    public Store Store;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "merchant_id", referencedColumnName = "id")
    public Merchant merchant;

    @Column(name = "max_range_delivery")
    public Integer maxRangeDelivery;

    @Column(name = "km_price_value")
    public Integer kmPriceValue;

    @Column(name = "enable_flat_price")
    public Boolean enableFlatPrice;

    @Column(name = "max_range_flat_price")
    public Integer maxRangeFlatPrice;

    @Column(name = "flat_price_value")
    public Integer flatPriceValue;

    @Column(name = "deliver_fee")
    public Integer deliverFee;

    @Column(name = "calculate_method")
    public String calculateMethod;
}
