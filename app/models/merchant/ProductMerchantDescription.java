package models.merchant;

import dtos.product.ProductRequest;
import dtos.product.ProductWithProductStoreRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import models.BaseModel;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

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

    @OneToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "product_merchant_detail_id", referencedColumnName = "id")
    private ProductMerchantDetail productMerchantDetail;

    public ProductMerchantDescription(ProductMerchantDetail productMerchantDetail, ProductRequest productRequest) {
        this.setShortDescription(productRequest.getProductDescriptionRequest().getShortDescription());
        this.setLongDescription(productRequest.getProductDescriptionRequest().getLongDescription());
        this.setProductMerchantDetail(productMerchantDetail);
    }

    public ProductMerchantDescription(ProductMerchantDetail productMerchantDetail, ProductWithProductStoreRequest productRequest) {
        this.setShortDescription(productRequest.getProductDescriptionRequest().getShortDescription());
        this.setLongDescription(productRequest.getProductDescriptionRequest().getLongDescription());
        this.setProductMerchantDetail(productMerchantDetail);
    }

    public void setProductMerchantDescription(ProductMerchantDescription productMerchantDescription, ProductMerchantDetail productMerchantDetail, ProductRequest productRequest) {
        productMerchantDescription.setShortDescription(productRequest.getProductDescriptionRequest().getShortDescription());
        productMerchantDescription.setLongDescription(productRequest.getProductDescriptionRequest().getLongDescription());
        productMerchantDescription.setProductMerchantDetail(productMerchantDetail);
    }

    public void setProductMerchantDescription(ProductMerchantDescription productMerchantDescription, ProductMerchantDetail productMerchantDetail, ProductWithProductStoreRequest productRequest) {
        productMerchantDescription.setShortDescription(productRequest.getProductDescriptionRequest().getShortDescription());
        productMerchantDescription.setLongDescription(productRequest.getProductDescriptionRequest().getLongDescription());
        productMerchantDescription.setProductMerchantDetail(productMerchantDetail);
    }

}
