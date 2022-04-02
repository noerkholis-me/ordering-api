package models.merchant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import models.BaseModel;

import javax.persistence.*;

@Entity
@Table(name = "product_merchant_description")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProductMerchantDescription extends BaseModel {

    @Column(name = "short_description", columnDefinition = "TEXT")
    private String shortDescription;

    @Column(name = "long_description", columnDefinition = "TEXT")
    private String longDescription;

    @OneToOne(cascade = { CascadeType.ALL })
    @JoinColumn(name = "product_merchant_detail_id", referencedColumnName = "id")
    private ProductMerchantDetail productMerchantDetail;

}
