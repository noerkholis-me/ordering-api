package models.loyalty;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import models.*;
import java.math.BigDecimal;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LoyaltyPointMerchant extends BaseModel {

    @Column(name = "usage_type")
    private String usageType;

    @Column(name = "loyalty_usage_value")
    private BigDecimal loyaltyUsageValue;

    @Column(name = "max_loyalty_usage_value")
    private BigDecimal maxLoyaltyUsageValue;

    @Column(name = "cashback_type")
    private String cashbackType;

    @Column(name = "cashback_value")
    private BigDecimal cashbackValue;

    @Column(name = "max_cashback_value")
    private BigDecimal maxCashbackValue;

    @ManyToOne(cascade = { CascadeType.ALL })
    @JoinColumn(name = "subs_category_id", referencedColumnName = "id")
    public SubsCategoryMerchant subsCategoryMerchant;

    @ManyToOne(cascade = { CascadeType.ALL })
    @JoinColumn(name = "merchant_id", referencedColumnName = "id")
    public Merchant merchant;
}