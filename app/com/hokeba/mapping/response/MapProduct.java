package com.hokeba.mapping.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import models.Attribute;
import models.Product;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by hendriksaragih on 4/8/17.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class MapProduct {
    public Long id;
    public String name;
    @JsonProperty("sku_seller")
    public String skuSeller;
    public String sku;
    @JsonProperty("slug")
    public String slug;
    public String currency;
    @JsonProperty("meta_title")
    private String metaTitle;
    @JsonProperty("meta_keyword")
    private String metaKeyword;
    @JsonProperty("meta_description")
    private String metaDescription;
    @JsonProperty("discount_active")
    public boolean discountActive;
    @JsonProperty("discount_type")
    private Integer discountType;
    @JsonProperty("discount_type_s")
    private String discountTypeS;
    @JsonProperty("discount_active_from")
    public Date discountActiveFrom;
    @JsonProperty("discount_active_to")
    public Date discountActiveTo;
    @JsonProperty("discount_active_from_s")
    public String discountActiveFromS;
    @JsonProperty("discount_active_to_s")
    public String discountActiveToS;
    @JsonProperty("product_type")
    public int productType;

    @JsonProperty("strike_through_display")
    public Double strikeThroughDisplay;
    @JsonProperty("price")
    public Double price;
    @JsonProperty("price_display")
    public Double priceDisplay;
    
    @JsonProperty("discount")
    public Double discount;
    @JsonProperty("average_rating")
    public float averageRating;
    @JsonProperty("count_rating")
    public float countRating;

    @JsonProperty("image_url")
    public String imageUrl;
    public Long stock;
    
    @JsonProperty("checkout_type")
    public Long checkoutType;

    @JsonProperty("real_price_display")
    public Double realPriceDisplay;

	public MapProductRatting rating;
    public MapBrand brand;
    public MapMerchant seller;
    public List<MapCategoryProduct> categories;
    public List<MapCategoryProduct> categories2;
    public List<MapCategoryProduct> categories1;
    @JsonProperty("product_reviews")
    public List<MapProductReview> productReviews;
    @JsonProperty("product_details")
    public List<MapProductDetail> productDetails;
    @JsonProperty("product_groups")
    public MapProductList[] productGroups;
    public MapSize[] sizes;
    
    public MapProduct() {
    	super();
    }
    
    public MapProduct(Product model) {
    	this.id = model.id;
    	this.name = model.name;
    	this.skuSeller = model.skuSeller;
    	this.sku = model.sku;
    	this.currency = model.getCurrency();
    	this.metaTitle = model.metaTitle;
    	this.metaKeyword = model.metaKeyword;
    	this.metaDescription = model.metaDescription;
    	
    	this.strikeThroughDisplay = model.getStrikeThroughDisplay();
    	this.price = model.price;
    	this.priceDisplay = model.getPriceDisplay();
    	this.discountActive = model.getDiscountActive();
    	this.discount = model.discount;
    	this.discountType = model.discountType;
    	this.discountTypeS = model.getDiscountTypeS();
    	this.discountActiveFrom = model.discountActiveFrom;
    	this.discountActiveTo = model.discountActiveTo;
    	this.discountActiveFromS = model.getDiscountActiveFromS();
    	this.discountActiveToS = model.getDiscountActiveToS();
    	
    	this.productType = model.productType;
    	this.averageRating = model.averageRating;
    	this.countRating = model.countRating == null ? 0 : model.countRating;
    	this.imageUrl = model.getImageUrl();
    	this.stock = model.getTotalStock();
    	this.checkoutType = model.checkoutType;
    	this.realPriceDisplay = model.getRealPriceDisplay();

    	this.rating = model.rating;
    	ObjectMapper mapper = new ObjectMapper();
    	this.brand = mapper.convertValue(model.brand, MapBrand.class);
    	this.seller = model.getSeller();
    	this.categories = mapper.convertValue(model.getCategories(), new TypeReference<List<MapCategoryProduct>>(){});
    	this.categories1 = mapper.convertValue(model.getCategories1(), new TypeReference<List<MapCategoryProduct>>(){});
    	this.categories2 = mapper.convertValue(model.getCategories2(), new TypeReference<List<MapCategoryProduct>>(){});
    	this.productReviews = mapper.convertValue(model.getProductReviews(), new TypeReference<List<MapProductReview>>(){});
    	
    	this.productGroups = new MapProductList[0];
    	this.sizes = new MapSize[0];
    	
    	this.productDetails = new ArrayList<>();
    	MapProductDetail detail = new MapProductDetail();
    	this.productDetails.add(detail);
    	detail.weight = model.getWeight();
    	detail.dimension = model.getDimension();
    	detail.dimensionX = model.getDimensionX();
    	detail.dimensionY = model.getDimensionY();
    	detail.dimensionZ = model.getDimensionZ();
    	detail.diameter = model.getDiameter();
    	detail.numberOfDiamond = model.getNumberOfDiamond();
    	detail.diamondColor = model.getDiamondColor();
    	detail.diamondClarity = model.getDiamondClarity();
    	detail.stamp = model.getStamp();
    	detail.certificate = model.getCertificate();
    	detail.kadar = model.getKadar();
    	detail.sumCaratOfGold = model.getSumCaratOfGold();
    	detail.weightOfGold = model.getWeightOfGold();
    	detail.weightOfGoldPlusDiamond = model.getWeightOfGoldPlusDiamond();
    	
    	detail.whatInTheBox = model.whatInTheBox;
    	
    	detail.warranty = model.getWarranty();
    	detail.warrantyType = model.warrantyType;
    	detail.warrantyPeriod = model.warrantyPeriod;

//    	detail.shortDescription = model.getShortDescriptions();
    	detail.description = model.description;
    	
    	detail.attributes = new LinkedList<>();
    	detail.attributesS = new LinkedList<>();
    	for(Attribute attr : model.attributes){
    		detail.attributes.add(new MapKeyValue(attr.baseAttribute.name, attr.getName(), attr.additional));
    		detail.attributesS.add(new MapAttribute(attr.baseAttribute.id, attr.id, attr.baseAttribute.name, attr.getName()));
		}
    	
    	detail.productImages = model.getProductImages();
    	detail.sizeGuide = model.sizeGuide;
    }
    

}