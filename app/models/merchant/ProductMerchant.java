package models.merchant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import models.*;

import javax.persistence.*;

@Entity
@Table(name = "product_merchant")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProductMerchant extends BaseModel {

    @Column(name = "product_name")
    private String productName;

    @Column(name = "is_active")
    private Boolean isActive;

    @ManyToOne(cascade = { CascadeType.ALL })
    @JoinColumn(name = "category_merchant_id", referencedColumnName = "id")
    private CategoryMerchant categoryMerchant;

    @ManyToOne(cascade = { CascadeType.ALL })
    @JoinColumn(name = "sub_category_merchant_id", referencedColumnName = "id")
    private SubCategoryMerchant subCategoryMerchant;

    @ManyToOne(cascade = { CascadeType.ALL })
    @JoinColumn(name = "brand_merchant_id", referencedColumnName = "id")
    private BrandMerchant brandMerchant;

    @ManyToOne(cascade = { CascadeType.ALL })
    @JoinColumn(name = "merchant_id", referencedColumnName = "id")
    public Merchant merchant;
    
    @Getter @Setter
    public SubsCategoryMerchant subsCategory;


}
