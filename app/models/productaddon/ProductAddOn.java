package models.productaddon;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import models.*;
import models.merchant.*;

import javax.persistence.*;

@Entity
@Table(name = "product_add_on_merchant")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProductAddOn extends BaseModel {

    @Column(name = "product_assign_id")
    private Long productAssignId;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "product_type")
    private String productType;

    @ManyToOne(cascade = { CascadeType.ALL })
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    public ProductMerchant productMerchant;

    @ManyToOne(cascade = { CascadeType.ALL })
    @JoinColumn(name = "merchant_id", referencedColumnName = "id")
    public Merchant merchant;

}
