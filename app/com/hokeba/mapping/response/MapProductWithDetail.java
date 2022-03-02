package com.hokeba.mapping.response;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;

import models.Product;
import models.ProductDetailVariance;
import models.ProductReview;

public class MapProductWithDetail {

	@JsonProperty("id")
	private Long id;

	@JsonProperty("name")
	private String name;

	@JsonProperty("created_date")
	private String createdDate;
	
	@JsonProperty("price")
	private Double price;

	@JsonProperty("price_display")
	private Double priceDisplay;
	
	@JsonProperty("discount_active")
    private boolean discountActive;
	@JsonProperty("discount")
	private Double discount;
    @JsonProperty("discount_type")
    private Integer discountType;

	@JsonProperty("dimension")
	private String dimension;

	@JsonProperty("kadar")
	private Double kadar;

	@JsonProperty("weight_of_gold")
	private Double weightOfGold;

	@JsonProperty("weight_of_gold_plus_diamond")
	private Double weightOfGoldPlusDiamond;

	@JsonProperty("weight")
	private Double weight;

	@JsonProperty("diameter")
	private Double diameter;

	@JsonProperty("number_of_diamond")
	private Double numberOfDiamond;

	@JsonProperty("diamond_color")
	private String diamondColor;

	@JsonProperty("diamond_clarity")
	private String diamondClarity;
	
	@JsonProperty("sum_carat_of_gold")
	private Double sumCaratOfGold;

	@JsonProperty("stamp")
	private String stamp;

	@JsonProperty("certificate")
	private String certificate;

	@JsonProperty("category1_slug")
	private String category1Slug;

	@JsonProperty("category2_slug")
	private String category2Slug;

	@JsonProperty("category3_slug")
	private String category3Slug;

	@JsonProperty("category3_name")
	private String category3Name;

	@JsonProperty("category3_id")
	private Long category3Id;

	@JsonProperty("brand_name")
	private String brandName;

	@JsonProperty("seller_name")
	private String sellerName;
	
	@JsonProperty("seller")
	private MapMerchantProductOwner seller;

	@JsonProperty("rating")
	private MapProductRatting productRating;

	@JsonProperty("product_images")
	private List<MapProductImage> productImages;

	@JsonProperty("product_detail_variance")
	private List<MapProductDetailVariance> productDetailVariance;

	// reviews
	@JsonProperty("product_reviews")
	private List<MapProductReview> productReviews;
	
	@Column(columnDefinition = "text")
	@JsonProperty("description")
	private String description;
	
    @JsonProperty("checkout_type")
    public Long checkoutType;
	
    @JsonProperty("real_price_display")
    public Double realPriceDisplay;

	public MapProductWithDetail(Product product) {
		super();
		this.id = product.id;
		this.name = product.name;
		this.createdDate = product.getCreatedDate();
		this.price = product.price;
		this.priceDisplay = product.getPriceDisplay();
		this.discountActive = product.getDiscountActive();
		this.discount = product.discount;
		this.discountType = product.discountType;
		this.dimension = product.getDimension();
		this.kadar = product.kadar;
		this.weightOfGold = product.weightOfGold;
		this.weightOfGoldPlusDiamond = product.weightOfGoldPlusDiamond;
		this.weight = product.weight;
		this.diameter = product.diameter;
		this.numberOfDiamond = product.numberOfDiamond;
		this.diamondColor = product.diamondColor;
		this.diamondClarity = product.diamondClarity;
		this.sumCaratOfGold = product.sumCaratOfGold;
		this.stamp = product.stamp;
		this.certificate = product.certificate;
		this.category1Slug = product.grandParentCategory.slug;
		this.category2Slug = product.parentCategory.slug;
		this.category3Slug = product.category.slug;
		this.category3Name = product.category.name;
		this.category3Id = product.category.id;
		this.brandName = product.getBrandName();
		this.sellerName = product.getSeller().getName();
		this.seller = product.merchant == null ? new MapMerchantProductOwner(product.vendor) 
				: new MapMerchantProductOwner(product.merchant);
		this.productRating = setRating();
		this.productImages = product.getProductImages();
		this.productDetailVariance = setProductDetailVariance(product.productDetail);
		this.productReviews = setProductReview(product.getProductReviews());
		
		this.description = product.description;
		this.checkoutType = product.checkoutType;
		this.realPriceDisplay = product.getRealPriceDisplay();
	}

	public List<MapProductReview> setProductReview(List<ProductReview> reviews) {
		List<MapProductReview> listReview = new ArrayList<MapProductReview>();
		reviews.forEach((data) -> {
			listReview.add(new MapProductReview(data));
		});
		return listReview;
	}

	public List<MapProductDetailVariance> setProductDetailVariance(List<ProductDetailVariance> pdv) {
		List<MapProductDetailVariance> mpdv = new ArrayList<MapProductDetailVariance>();
		pdv.forEach((data) -> {
			if (!data.isDeleted) {
				mpdv.add(new MapProductDetailVariance(data));
			}
		});
		return mpdv;
	}

	public MapProductRatting setRating() {
		MapProductRatting rating = new MapProductRatting();
		rating.setAverage(ProductReview.getAverage(id));
		rating.setBintang1(ProductReview.getJumlah(id, 1));
		rating.setBintang2(ProductReview.getJumlah(id, 2));
		rating.setBintang3(ProductReview.getJumlah(id, 3));
		rating.setBintang4(ProductReview.getJumlah(id, 4));
		rating.setBintang5(ProductReview.getJumlah(id, 5));
		rating.setCount(ProductReview.getJumlah(id));
		return rating;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Double getPriceDisplay() {
		return priceDisplay;
	}

	public void setPriceDisplay(Double priceDisplay) {
		this.priceDisplay = priceDisplay;
	}

	public String getDimension() {
		return dimension;
	}

	public void setDimension(String dimension) {
		this.dimension = dimension;
	}

	public Double getKadar() {
		return kadar;
	}

	public void setKadar(Double kadar) {
		this.kadar = kadar;
	}

	public Double getWeightOfGold() {
		return weightOfGold;
	}

	public void setWeightOfGold(Double weightOfGold) {
		this.weightOfGold = weightOfGold;
	}

	public Double getWeightOfGoldPlusDiamond() {
		return weightOfGoldPlusDiamond;
	}

	public void setWeightOfGoldPlusDiamond(Double weightOfGoldPlusDiamond) {
		this.weightOfGoldPlusDiamond = weightOfGoldPlusDiamond;
	}

	public Double getWeight() {
		return weight;
	}

	public void setWeight(Double weight) {
		this.weight = weight;
	}

	public Double getDiameter() {
		return diameter;
	}

	public void setDiameter(Double diameter) {
		this.diameter = diameter;
	}

	public Double getNumberOfDiamond() {
		return numberOfDiamond;
	}

	public void setNumberOfDiamond(Double numberOfDiamond) {
		this.numberOfDiamond = numberOfDiamond;
	}

	public String getDiamondColor() {
		return diamondColor;
	}

	public void setDiamondColor(String diamondColor) {
		this.diamondColor = diamondColor;
	}

	public String getDiamondClarity() {
		return diamondClarity;
	}

	public void setDiamondClarity(String diamondClarity) {
		this.diamondClarity = diamondClarity;
	}

	public String getStamp() {
		return stamp;
	}

	public void setStamp(String stamp) {
		this.stamp = stamp;
	}

	public String getCertificate() {
		return certificate;
	}

	public void setCertificate(String certificate) {
		this.certificate = certificate;
	}

	public String getCategory1Slug() {
		return category1Slug;
	}

	public void setCategory1Slug(String category1Slug) {
		this.category1Slug = category1Slug;
	}

	public String getCategory2Slug() {
		return category2Slug;
	}

	public void setCategory2Slug(String category2Slug) {
		this.category2Slug = category2Slug;
	}

	public String getCategory3Slug() {
		return category3Slug;
	}

	public void setCategory3Slug(String category3Slug) {
		this.category3Slug = category3Slug;
	}

	public String getCategory3Name() {
		return category3Name;
	}

	public void setCategory3Name(String category3Name) {
		this.category3Name = category3Name;
	}

	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	public String getSellerName() {
		return sellerName;
	}

	public void setSellerName(String sellerName) {
		this.sellerName = sellerName;
	}

	public Long getCategory3Id() {
		return category3Id;
	}

	public void setCategory3Id(Long category3Id) {
		this.category3Id = category3Id;
	}

	public MapMerchantProductOwner getSeller() {
		return seller;
	}

	public void setSeller(MapMerchantProductOwner seller) {
		this.seller = seller;
	}

	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	public Double getSumCaratOfGold() {
		return sumCaratOfGold;
	}

	public void setSumCaratOfGold(Double sumCaratOfGold) {
		this.sumCaratOfGold = sumCaratOfGold;
	}

	public boolean isDiscountActive() {
		return discountActive;
	}

	public void setDiscountActive(boolean discountActive) {
		this.discountActive = discountActive;
	}

	public Double getDiscount() {
		return discount;
	}

	public void setDiscount(Double discount) {
		this.discount = discount;
	}

	public Integer getDiscountType() {
		return discountType;
	}

	public void setDiscountType(Integer discountType) {
		this.discountType = discountType;
	}

	@JsonGetter("total_stock")
	public Long getTotalStock() {
		Long totalStock = 0L;
		if (this.productDetailVariance != null) {
			for (MapProductDetailVariance mapProductDetailVariance : productDetailVariance) {
				totalStock += (mapProductDetailVariance.totalStock == null ? 0L : mapProductDetailVariance.totalStock);
			}
		}
		return totalStock;
	}

	public Long getCheckoutType() {
		return checkoutType;
	}

	public void setCheckoutType(Long checkoutType) {
		this.checkoutType = checkoutType;
	}
	
}
