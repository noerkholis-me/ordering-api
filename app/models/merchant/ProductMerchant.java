package models.merchant;

import dtos.product.ProductRequest;
import dtos.product.ProductWithProductStoreRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import models.BaseModel;
import models.BrandMerchant;
import models.CategoryMerchant;
import models.Merchant;
import models.SubCategoryMerchant;
import models.SubsCategoryMerchant;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "product_merchant")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProductMerchant extends BaseModel {
    @Column(name = "product_name")
    private String productName;

    @Column(name = "no_sku")
    private String noSKU;

    @Column(name = "is_active")
    private Boolean isActive;

    @ManyToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "category_merchant_id", referencedColumnName = "id")
    private CategoryMerchant categoryMerchant;

    @ManyToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "sub_category_merchant_id", referencedColumnName = "id")
    private SubCategoryMerchant subCategoryMerchant;

    @ManyToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "subs_category_merchant_id", referencedColumnName = "id")
    private SubsCategoryMerchant subsCategoryMerchant;

    @ManyToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "brand_merchant_id", referencedColumnName = "id")
    private BrandMerchant brandMerchant;

    @ManyToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "merchant_id", referencedColumnName = "id")
    public Merchant merchant;

    public SubsCategoryMerchant subsCategory;

    public ProductMerchant(Merchant merchant, CategoryMerchant categoryMerchant, SubCategoryMerchant subCategoryMerchant, SubsCategoryMerchant subsCategoryMerchant, BrandMerchant brandMerchant, ProductRequest productRequest) {
        this.setNoSKU(productRequest.getNoSKU());
        this.setProductName(productRequest.getProductName());
        this.setIsActive(true);
        this.setCategoryMerchant(categoryMerchant);
        this.setSubCategoryMerchant(subCategoryMerchant);
        this.setSubsCategoryMerchant(subsCategoryMerchant);
        this.setBrandMerchant(brandMerchant);
        this.setMerchant(merchant);
    }

    public ProductMerchant(Merchant merchant, CategoryMerchant categoryMerchant, SubCategoryMerchant subCategoryMerchant, SubsCategoryMerchant subsCategoryMerchant, BrandMerchant brandMerchant, ProductWithProductStoreRequest productRequest) {
        this.setNoSKU(productRequest.getNoSKU());
        this.setProductName(productRequest.getProductName());
        this.setIsActive(true);
        this.setCategoryMerchant(categoryMerchant);
        this.setSubCategoryMerchant(subCategoryMerchant);
        this.setSubsCategoryMerchant(subsCategoryMerchant);
        this.setBrandMerchant(brandMerchant);
        this.setMerchant(merchant);
    }

    public void setProductMerchant(ProductMerchant productMerchant, Merchant merchant, CategoryMerchant categoryMerchant, SubCategoryMerchant subCategoryMerchant, SubsCategoryMerchant subsCategoryMerchant, BrandMerchant brandMerchant, ProductRequest productRequest) {
        productMerchant.setNoSKU(productRequest.getNoSKU());
        productMerchant.setProductName(productRequest.getProductName());
        productMerchant.setIsActive(true);
        productMerchant.setCategoryMerchant(categoryMerchant);
        productMerchant.setSubCategoryMerchant(subCategoryMerchant);
        productMerchant.setSubsCategoryMerchant(subsCategoryMerchant);
        productMerchant.setBrandMerchant(brandMerchant);
        productMerchant.setMerchant(merchant);
    }

    public void setProductMerchant(ProductMerchant productMerchant, Merchant merchant, CategoryMerchant categoryMerchant, SubCategoryMerchant subCategoryMerchant, SubsCategoryMerchant subsCategoryMerchant, BrandMerchant brandMerchant, ProductWithProductStoreRequest productRequest) {
        productMerchant.setNoSKU(productRequest.getNoSKU());
        productMerchant.setProductName(productRequest.getProductName());
        productMerchant.setIsActive(true);
        productMerchant.setCategoryMerchant(categoryMerchant);
        productMerchant.setSubCategoryMerchant(subCategoryMerchant);
        productMerchant.setSubsCategoryMerchant(subsCategoryMerchant);
        productMerchant.setBrandMerchant(brandMerchant);
        productMerchant.setMerchant(merchant);
    }

}
