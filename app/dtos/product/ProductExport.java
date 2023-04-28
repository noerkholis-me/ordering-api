package dtos.product;


import java.math.BigDecimal;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import models.ProductStore;
import models.merchant.ProductMerchant;
import models.merchant.ProductMerchantDescription;
import models.merchant.ProductMerchantDetail;

@Getter
@Setter
@NoArgsConstructor
public class ProductExport {
	private Long productId;
	private String SkuNumber;
	private String productName;
	private String categoryProduct;
	private String subCategoryProduct;
	private String subsCategoryProduct;
	private String productBrand;
	private String productType;
	private String isCustomizable;
	private BigDecimal price;
	private String discountType;
	private double discount;
	private String imageMain;
	private String image1;
	private String image2;
	private String image3;
	private String image4;
	private String shortDesc;
	private String longDesc;

	private String productStoreId;
	private String storeName;
	private int priceStore;
	private String typeDiscountStore;
	private double discountStore;
	
	public static ProductExport getInstance(ProductMerchant data, ProductStore storeData, ProductMerchantDetail detail
			, ProductMerchantDescription description) {
		ProductExport res = new ProductExport();
		res.setProductId(data.id);
		res.setSkuNumber(data.getNoSKU());
		res.setProductName(data.getProductName());
		res.setCategoryProduct(data.getCategoryMerchant().getCategoryName());
		res.setSubCategoryProduct(data.getSubCategoryMerchant().getSubcategoryName());
		res.setSubsCategoryProduct(data.getSubsCategoryMerchant().getSubscategoryName());
		res.setProductBrand(data.getBrandMerchant().getBrandName());
		res.setProductType(detail.getProductType());
		res.setIsCustomizable(String.valueOf(detail.getIsCustomizable()));
		res.setPrice(detail.getProductPrice());
		res.setDiscountType(detail.getDiscountType());
		res.setDiscount(detail.getDiscount());
		res.setImageMain(detail.getProductImageMain());
		res.setImage1(detail.getProductImage1());
		res.setImage2(detail.getProductImage2());
		res.setImage3(detail.getProductImage3());
		res.setImage4(detail.getProductImage4());
		res.setShortDesc(description.getShortDescription());
		res.setLongDesc(description.getLongDescription());
		res.setProductStoreId(String.valueOf(storeData.id));
		res.setStoreName(storeData.getStore().storeName);
		res.setPriceStore(storeData.getStorePrice().intValue());
		res.setTypeDiscountStore(storeData.getDiscountType());
		res.setDiscountStore(storeData.getDiscount().intValue());
		
		return res;
	}
	
	public static ProductExport getInstance(ProductMerchant data, ProductMerchantDetail detail
			, ProductMerchantDescription description) throws Exception {
		System.out.println(data.getProductName() + " " + data.id);
		System.out.println(detail.getProductPrice().intValue());
		ProductExport res = new ProductExport();
		res.setProductId(data.id);
		res.setSkuNumber(data.getNoSKU());
		res.setProductName(data.getProductName());
		res.setCategoryProduct(data.getCategoryMerchant().getCategoryName());
		res.setSubCategoryProduct(data.getSubCategoryMerchant().getSubcategoryName());
		res.setSubsCategoryProduct(data.getSubsCategoryMerchant().getSubscategoryName());
		res.setProductBrand(data.getBrandMerchant().getBrandName());
		res.setProductType(detail.getProductType());
		res.setIsCustomizable(String.valueOf(detail.getIsCustomizable()));
		res.setPrice(detail.getProductPrice());
		res.setDiscountType(detail.getDiscountType());
		res.setDiscount(detail.getDiscount());
		res.setImageMain(detail.getProductImageMain());
		res.setImage1(detail.getProductImage1());
		res.setImage2(detail.getProductImage2());
		res.setImage3(detail.getProductImage3());
		res.setImage4(detail.getProductImage4());
		res.setShortDesc(description.getShortDescription());
		res.setLongDesc(description.getLongDescription());
		res.setProductStoreId("");
		res.setStoreName("");
		res.setPriceStore(0);
		res.setTypeDiscountStore("");
		res.setDiscountStore(0);
		
		return res;
	}
	
}
