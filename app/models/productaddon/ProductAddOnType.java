package models.productaddon;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import models.*;
import models.merchant.*;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProductAddOnType extends BaseModel {

    @Column(name = "product_type")
    private String productType;

    @Column(name = "is_active")
    private Boolean isActive;

    @ManyToOne(cascade = { CascadeType.ALL })
    @JoinColumn(name = "merchant_id", referencedColumnName = "id")
    public Merchant merchant;

}
