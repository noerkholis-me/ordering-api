package models.merchant;

import com.hokeba.util.Constant;
import dtos.product.ProductRequest;
import dtos.product.ProductWithProductStoreRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import models.BaseModel;
import models.Store;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
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

    @OneToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "product_merchant_id", referencedColumnName = "id")
    private ProductMerchant productMerchant;

    @Column(name = "product_merchant_qr_code")
    public String productMerchantQrCode;

    @Transient
    public Long total_penjualan;

    public static Finder<Long, ProductMerchantDetail> find = new Finder<Long, ProductMerchantDetail>(Long.class, ProductMerchantDetail.class);

    public ProductMerchantDetail(ProductMerchant productMerchant, ProductRequest productRequest) {
        this.setProductType(productRequest.getProductDetailRequest().getProductType());
        this.setIsCustomizable(productRequest.getProductDetailRequest().getIsCustomizable());
        this.setProductPrice(productRequest.getProductDetailRequest().getProductPrice());
        this.setDiscountType(productRequest.getProductDetailRequest().getDiscountType());
        this.setDiscount(productRequest.getProductDetailRequest().getDiscount() != null ? productRequest.getProductDetailRequest().getDiscount() : 0D);
        this.setProductPriceAfterDiscount(productRequest.getProductDetailRequest().getProductPriceAfterDiscount() != null ? productRequest.getProductDetailRequest().getProductPriceAfterDiscount() : productRequest.getProductDetailRequest().getProductPrice());
        this.setProductImageMain(productRequest.getProductDetailRequest().getProductImageMain());
        this.setProductImage1(productRequest.getProductDetailRequest().getProductImage1());
        this.setProductImage2(productRequest.getProductDetailRequest().getProductImage2());
        this.setProductImage3(productRequest.getProductDetailRequest().getProductImage3());
        this.setProductImage4(productRequest.getProductDetailRequest().getProductImage4());
        this.setProductMerchant(productMerchant);
        this.setProductMerchantQrCode(Constant.getInstance().getFrontEndUrl().concat("product/" + productMerchant.id + "/detail"));
    }

    public ProductMerchantDetail(ProductMerchant productMerchant, ProductWithProductStoreRequest productRequest) {
        this.setProductType(productRequest.getProductDetailRequest().getProductType());
        this.setIsCustomizable(productRequest.getProductDetailRequest().getIsCustomizable());
        this.setProductPrice(productRequest.getProductDetailRequest().getProductPrice());
        this.setDiscountType(productRequest.getProductDetailRequest().getDiscountType());
        this.setDiscount(productRequest.getProductDetailRequest().getDiscount() != null ? productRequest.getProductDetailRequest().getDiscount() : 0D);
        this.setProductPriceAfterDiscount(productRequest.getProductDetailRequest().getProductPriceAfterDiscount() != null ? productRequest.getProductDetailRequest().getProductPriceAfterDiscount() : productRequest.getProductDetailRequest().getProductPrice());
        this.setProductImageMain(productRequest.getProductDetailRequest().getProductImageMain());
        this.setProductImage1(productRequest.getProductDetailRequest().getProductImage1());
        this.setProductImage2(productRequest.getProductDetailRequest().getProductImage2());
        this.setProductImage3(productRequest.getProductDetailRequest().getProductImage3());
        this.setProductImage4(productRequest.getProductDetailRequest().getProductImage4());
        this.setProductMerchant(productMerchant);
        this.setProductMerchantQrCode(Constant.getInstance().getFrontEndUrl().concat("product/" + productMerchant.id + "/detail"));
    }

    public void setProductMerchantDetail(ProductMerchantDetail productMerchantDetail, ProductMerchant productMerchant, ProductRequest productRequest) {
        productMerchantDetail.setProductType(productRequest.getProductDetailRequest().getProductType());
        productMerchantDetail.setIsCustomizable(productRequest.getProductDetailRequest().getIsCustomizable());
        productMerchantDetail.setProductPrice(productRequest.getProductDetailRequest().getProductPrice());
        productMerchantDetail.setDiscountType(productRequest.getProductDetailRequest().getDiscountType());
        productMerchantDetail.setDiscount(productRequest.getProductDetailRequest().getDiscount() != null ? productRequest.getProductDetailRequest().getDiscount() : 0D);
        productMerchantDetail.setProductPriceAfterDiscount(productRequest.getProductDetailRequest().getProductPriceAfterDiscount() != null ? productRequest.getProductDetailRequest().getProductPriceAfterDiscount() : productRequest.getProductDetailRequest().getProductPrice());
        productMerchantDetail.setProductImageMain(productRequest.getProductDetailRequest().getProductImageMain());
        productMerchantDetail.setProductImage1(productRequest.getProductDetailRequest().getProductImage1());
        productMerchantDetail.setProductImage2(productRequest.getProductDetailRequest().getProductImage2());
        productMerchantDetail.setProductImage3(productRequest.getProductDetailRequest().getProductImage3());
        productMerchantDetail.setProductImage4(productRequest.getProductDetailRequest().getProductImage4());
        productMerchantDetail.setProductMerchant(productMerchant);
        productMerchantDetail.setProductMerchantQrCode(Constant.getInstance().getFrontEndUrl().concat("product/" + productMerchant.id + "/detail"));
    }

    public void setProductMerchantDetail(ProductMerchantDetail productMerchantDetail, ProductMerchant productMerchant, ProductWithProductStoreRequest productRequest) {
        productMerchantDetail.setProductType(productRequest.getProductDetailRequest().getProductType());
        productMerchantDetail.setIsCustomizable(productRequest.getProductDetailRequest().getIsCustomizable());
        productMerchantDetail.setProductPrice(productRequest.getProductDetailRequest().getProductPrice());
        productMerchantDetail.setDiscountType(productRequest.getProductDetailRequest().getDiscountType());
        productMerchantDetail.setDiscount(productRequest.getProductDetailRequest().getDiscount() != null ? productRequest.getProductDetailRequest().getDiscount() : 0D);
        productMerchantDetail.setProductPriceAfterDiscount(productRequest.getProductDetailRequest().getProductPriceAfterDiscount() != null ? productRequest.getProductDetailRequest().getProductPriceAfterDiscount() : productRequest.getProductDetailRequest().getProductPrice());
        productMerchantDetail.setProductImageMain(productRequest.getProductDetailRequest().getProductImageMain());
        productMerchantDetail.setProductImage1(productRequest.getProductDetailRequest().getProductImage1());
        productMerchantDetail.setProductImage2(productRequest.getProductDetailRequest().getProductImage2());
        productMerchantDetail.setProductImage3(productRequest.getProductDetailRequest().getProductImage3());
        productMerchantDetail.setProductImage4(productRequest.getProductDetailRequest().getProductImage4());
        productMerchantDetail.setProductMerchant(productMerchant);
        productMerchantDetail.setProductMerchantQrCode(Constant.getInstance().getFrontEndUrl().concat("product/" + productMerchant.id + "/detail"));
    }

}
