package models.merchant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import models.BaseModel;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "product_merchant_detail")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProductMerchantDetail extends BaseModel {

    @Column(name = "product_type")
    private String productType;
    @Column(name = "is_customizable")
    private Boolean isCustomizable;
    @Column(name = "product_price")
    private BigDecimal productPrice;
    @Column(name = "discount_type")
    private String discountType;
    @Column(name = "discount")
    private Double discount;
    @Column(name = "product_price_after_discount")
    private BigDecimal productPriceAfterDiscount;
    @Column(name = "product_image_main")
    private String productImageMain;
    @Column(name = "product_image_1")
    private String productImage1;
    @Column(name = "product_image_2")
    private String productImage2;
    @Column(name = "product_image_3")
    private String productImage3;
    @Column(name = "product_image_4")
    private String productImage4;

    @OneToOne(cascade = { CascadeType.ALL })
    @JoinColumn(name = "product_merchant_id", referencedColumnName = "id")
    private ProductMerchant productMerchant;


}
