package models;

import com.avaje.ebean.*;
import com.avaje.ebean.Query;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hokeba.api.*;
import com.hokeba.mapping.request.MapProductMerchant;
import com.hokeba.mapping.request.MapProductMerchant2;
import com.hokeba.mapping.response.*;
import com.hokeba.shipping.beeexpress.util.Boxes;
import com.hokeba.util.CommonFunction;
import com.hokeba.util.Constant;

import play.Logger;
import play.libs.Json;

import javax.persistence.*;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@SuppressWarnings({"rawtypes", "unchecked"})
@Entity
@Table(name = "product")
public class Product extends BaseModel {
	private static final long serialVersionUID = 1L;
	
	public static final boolean ACTIVE = true;
	public static final boolean INACTIVE = false;
	public static final String PENDING = "P";
	public static final String AUTHORIZED = "A";
	public static final String REJECTED = "R";

	@Column(unique = true)
	public String sku;
	@JsonProperty("sku_seller")
	public String skuSeller;
	public String name;
	public String slug;
	@JsonProperty("product_type")
	public int productType;
	@JsonProperty("is_new")
	public boolean isNew;
	@JsonProperty("status")
	public boolean status;
	@JsonProperty("meta_title")
	public String metaTitle;
	@JsonProperty("meta_description")
	public String metaDescription;
	@JsonProperty("meta_keyword")
	public String metaKeyword;

	@JsonProperty("buy_price")
	public Double buyPrice;
	public Double price;
	@ManyToOne(cascade = { CascadeType.ALL })
	@JoinColumn(name = "currency_cd", referencedColumnName = "code")
	public Currency currency;

	@JsonIgnore
	@Column(name = "view_count")
	public int viewCount;

	public Integer retur;
	public boolean stock;
	@JsonProperty("item_count")
	public Long itemCount;
	@JsonProperty("item_count_odoo")
	public Long itemCountOdoo;

	@JsonProperty("strike_through_display")
	public Double strikeThroughDisplay;
	@JsonProperty("price_display")
	public Double priceDisplay;
	public Double discount;
	@Column(name = "discount_type", columnDefinition = "integer default 0")
	@JsonProperty("discount_type")
	public int discountType;

	@JsonProperty("thumbnail_url")
	public String thumbnailUrl;
	@JsonProperty("image_url")
	public String imageUrl;

	public int position;
	@Column(name = "is_show", columnDefinition = "boolean default true")
	public Boolean isShow;
	
	//TODO
	@Column(columnDefinition = "text")
	@JsonProperty("short_description")
	public String shortDescriptions;
	@Column(columnDefinition = "text")
	public String description;
	public Double weight;
	public Double dimension1;
	public Double dimension2;
	public Double dimension3;
	public Double diameter;
	@JsonProperty("number_of_diamond")
	public Double numberOfDiamond;
	@JsonProperty("diamond_color")
	public String diamondColor;
	@JsonProperty("diamond_clarity")
	public String diamondClarity;
	public String stamp;
	public String certificate;
	public Double kadar;
	@JsonProperty("weight_of_gold")
	public Double weightOfGold;
	@JsonProperty("sum_carat_of_gold")
	public Double sumCaratOfGold;
	@JsonProperty("weight_of_gold_plus_diamond")
	public Double weightOfGoldPlusDiamond;

	@Column(name = "warranty_type", columnDefinition = "integer default 0")
	@JsonProperty("warranty_type")
	public int warrantyType;
	@JsonProperty("warranty_period")
	@Column(name = "warranty_period", columnDefinition = "integer default 0")
	public int warrantyPeriod;
	@JsonProperty("sold_fulfilled_by")
	public String soldFulfilledBy;
	@Column(columnDefinition = "text")
	@JsonProperty("what_in_the_box")
	public String whatInTheBox;
	
	//TODO images
	@JsonIgnore
	@Column(name = "full_image_urls", columnDefinition = "TEXT")
	public String fullImageUrls;
	@JsonIgnore
	@Column(name = "medium_image_urls", columnDefinition = "TEXT")
	public String mediumImageUrls;
	@JsonIgnore
	@Column(name = "thumbnail_image_urls", columnDefinition = "TEXT")
	public String thumbnailImageUrls;
	@JsonIgnore
	@Column(name = "blur_image_urls", columnDefinition = "TEXT")
	public String blurImageUrls;
	@JsonIgnore
	@Column(name = "threesixty_image_urls", columnDefinition = "TEXT")
	public String threesixtyImageUrls;
	
	@Column(name = "size_guide", columnDefinition = "text")
	public String sizeGuide;
	
	@JsonProperty("checkout_type")
	public Long checkoutType;
	
	public boolean customizable;

	@ManyToMany
	@JsonIgnore
	public Set <BaseAttribute> baseAttributes;

	@ManyToMany
	@JsonIgnore
	public Set <Attribute> attributes;

	@ManyToMany
	public Set <Size> sizes;

	@Column(name = "odoo_id")
	public String odooId;
	
//	@Transient
//	@JsonProperty("base_attributes")
//	public String[] getBaseAttributes(){
//		String[] arrayAtt = new String[this.baseAttributes.size()];
//		int count = 0;
//		for (BaseAttribute model : this.baseAttributes) {
//			arrayAtt[count] = model.name;
//			count++;
//		}
//		return arrayAtt;
//	}

	@ManyToOne(cascade = { CascadeType.ALL })
	@JoinColumn(name = "merchant_id", referencedColumnName = "id")
	@JsonIgnore
	public Merchant merchant;

	@ManyToOne(cascade = { CascadeType.ALL })
	@JoinColumn(name = "vendor_id", referencedColumnName = "id")
	@JsonIgnore
	public Vendor vendor;

	@ManyToOne(cascade = { CascadeType.ALL })
	@JoinColumn(name = "brand_id", referencedColumnName = "id")
	public Brand brand;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "category_id", referencedColumnName = "id")
	public Category category;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "parent_category_id", referencedColumnName = "id")
	public Category parentCategory;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "grand_parent_category_id", referencedColumnName = "id")
	public Category grandParentCategory;

	@OneToMany(mappedBy = "mainProduct")
	public List<ProductDetailVariance> productDetail;

	@ManyToMany (mappedBy = "products")
	public List<Tag> tags;
	
	@JsonProperty("average_rating")
	public float averageRating;

	@JsonProperty("count_rating")
	public Integer countRating;

	@JsonProperty("num_of_order")
	public Integer numOfOrder;
	
	@OneToMany(mappedBy = "product")			
	public List<ProductReview> productReviews;

	@JsonProperty("discount_active_from")
	public Date discountActiveFrom;

	@JsonProperty("discount_active_to")
	public Date discountActiveTo;

	@JsonIgnore
	@JoinColumn(name="product_group_id")
	@ManyToOne
	public ProductGroup productGroup;

	@JsonIgnore
	@JoinColumn(name="product_variant_group_id")
	@ManyToOne
	public ProductVariantGroup productVariantGroup;

	@JsonIgnore
	@JoinColumn(name="user_id")
	@ManyToOne
	public UserCms userCms;

	@JsonProperty("first_po_status")
	public int firstPoStatus;

	@Transient
	public String merchantId;

	@JsonProperty("approved_status")
	public String approvedStatus;

	@JsonProperty("approved_note")
    @Column(name = "approved_note", length = 2000)
	public String approvedNote;

	@JsonProperty("approved_information")
    @Column(name = "approved_information", length = 1000)
	public String approvedInformation;

	@Column(name = "approved_by")
	@JsonIgnore
	@ManyToOne
	public UserCms approvedBy;

	@JsonIgnore
	@OneToMany(mappedBy = "product")			
	public List<ProductPrice> productPrices;

	//TODO unknown transient stuff
	
	@Transient
	public Long brandId;

	@Transient
	public Long categoryId;

	@Transient
	public Long subCategoryId;

	@Transient
	public Long subSubCategoryId;

	@Transient
	public String currencyCode;

	@Transient
	public String fromDate = "";

	@Transient
	public String toDate = "";

	@Transient
	public String fromTime = "";

	@Transient
	public String toTime = "";

	@Transient
	public String productDescription;

    @Transient
    public List<String> listShortDescriptions;

	@Transient
	public List<Long> listBaseAttribute;

	@Transient
	public List<Long> listAttribute;

	@Transient
	public String newProduct;

	@Transient
	public Long idtmp;

	@Transient
	public String save;

	@Transient
	public String idImagetmp;

	@Transient
	public Boxes boxesTmp;

	@Transient
	@JsonProperty("product_code")
	public String getProductCode(){
		return sku;
	}

	@Transient
	@JsonProperty("created_date")
	public String getCreatedDate(){
		return CommonFunction.getDateTime(createdAt);
	}
	@Transient
	@JsonProperty("discount_active_from_s")
	public String getDiscountActiveFromS(){
		return CommonFunction.getDate2(discountActiveFrom);
	}
	@Transient
	@JsonProperty("discount_active_to_s")
	public String getDiscountActiveToS(){
		return CommonFunction.getDate2(discountActiveTo);
	}
	@Transient
	@JsonProperty("discount_type_s")
	public String getDiscountTypeS(){
		return discountType == 0 ? "" : String.valueOf(discountType);
	}

	@Transient
	@JsonProperty("promo_price")
	public Double getPromoPrice(){
		return getPriceDisplay();
	}
	
/*	@Transient
	@JsonProperty("fee_price")
	public Double getFeePrice(){
		return getPriceDisplay() - getBuyPrice();
	}*/
	
	@Transient
	@JsonProperty("is_active")
	public Boolean getIsActive(){
		return status;
	}
	@Transient
	@JsonProperty("stock")
	public Long getStock(){
		return itemCount == null ? 0L : itemCount;
	}
	
	@Transient
	@JsonProperty("total_stock")
	public Long getTotalStock() {
		String sql = "SELECT SUM(total_stock) AS TOTAL " +
				"FROM product_detail_variance " +
				"WHERE is_deleted = false AND product_id = :productId ";

		SqlQuery query = Ebean.createSqlQuery(sql);
		query.setParameter("productId", this.id);
		Long result = query.findUnique().getLong("TOTAL");
		return result == null ? 0L : result;
	}
	
	@Transient
	@JsonGetter("discount_active")
	public boolean getDiscountActive() {
		return this.price != null && !this.price.equals(this.priceDisplay);
	}
	
	public boolean checkDiscountActive() {
		boolean discountActive = true;
		if (discountActiveFrom != null && discountActiveTo != null) {
			Date currentDate = new Date();
			if (!(currentDate.after(discountActiveFrom) && currentDate.before(discountActiveTo))) {
				discountActive = false;
			}
		}
		return discountActive;
	}

	@Transient
	@JsonProperty("retur_qty")
	public Integer getReturQty(){
		return retur == null ? 0 : retur;
	}

	@Transient
	@JsonProperty("product_reviews")
	public List<ProductReview> getProductReviews(){
		return ProductReview.getReview(id);
	}
	@JsonProperty("categories")
	public List<Category> getCategories(){
		return Arrays.asList(category);
	}
	@JsonProperty("categories2")
	public List<Category> getCategories2(){
		return Arrays.asList(parentCategory);
	}
	@JsonProperty("categories1")
	public List<Category> getCategories1(){
		return Arrays.asList(grandParentCategory);
	}
	@JsonProperty("product_details")
	public List<ProductDetailVariance> getProductDetails(){
		return productDetail;
	}
	
	//TODO image getter
	public String getFullUrlImage(String image){
		return image==null || image.isEmpty() ? "" : Constant.getInstance().getImageUrl() + image;
	}
	
	@Transient
	@JsonProperty("full_image_url")
	public String[] getImage1() {
		try {
			ObjectMapper om = new ObjectMapper();
			return om.readValue(fullImageUrls, String[].class);
		} catch (Exception e) {
			return new String[0];
		}
	}

	@Transient
	@JsonProperty("medium_image_url")
	public String[] getImage2() {
		try {
			ObjectMapper om = new ObjectMapper();
			return om.readValue(mediumImageUrls, String[].class);
		} catch (Exception e) {
			return new String[0];
		}
	}

	@Transient
	@JsonProperty("thumbnail_image_url")
	public String[] getImage3() {
		try {
			ObjectMapper om = new ObjectMapper();
			return om.readValue(thumbnailImageUrls, String[].class);
		} catch (Exception e) {
			return new String[0];
		}
	}

	@Transient
	@JsonProperty("blur_image_url")
	public String[] getImage4() {
		try {
			ObjectMapper om = new ObjectMapper();
			return om.readValue(blurImageUrls, String[].class);
		} catch (Exception e) {
			return new String[0];
		}
	}
	
	@Transient
	@JsonProperty("product_images")
	public List<MapProductImage> getProductImages(){
		List<MapProductImage> images = new ArrayList<>();
//		ProductDetailVariance pd = productDetail.get(0);
		String[] image1 = getImage1();
		String[] image2 = getImage2();
		String[] image3 = getImage3();
		String[] image4 = getImage4();

		int i = 0;
		for(String image:image1){
			MapProductImage img = new MapProductImage();
			img.setFullImageUrl(getFullUrlImage(image));
			img.setMediumImageUrl(getFullUrlImage(image2[i]));
			img.setThumbnailImageUrl(getFullUrlImage(image3[i]));
			img.setBlurImageUrl("");
			images.add(img);
			i++;
		}
		return images;
	}

	//TODO etc
	@JsonProperty("currency")
	public String getCurrency(){
//		return currency.code;
		
		/*Hellobisnis*/
		return null;
	}

	@Transient
	@JsonProperty("reason")
	public String getReason(){
		return approvedNote == null? "" : approvedNote;
	}

	@Transient
	@JsonProperty("note")
	public List<MapProductNotes> getNote(){
		List<MapProductNotes> note = new ArrayList<>();

		if (approvedStatus.equals(REJECTED)){
			if (approvedInformation != null && !approvedInformation.isEmpty()){
				JsonNode json = Json.parse(approvedInformation);
				try {
					note = Arrays.asList(new ObjectMapper().readValue(json.toString(), MapProductNotes[].class));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}

		return note;
	}

	@Transient
	@JsonProperty("product_variants")
	public List<MapVariantGroup> productVariants;

	@Transient
	@JsonProperty("product_colors")
	public List<MapProductColor> productColors;

	public List<MapProductNotes> getNote2(){
		List<MapProductNotes> note = new ArrayList<>();

/*		if (!approvedStatus.equals(PENDING)){
			Map<String, String> notes = new HashMap<>();

			List<Param> params = Param.find.where().eq("param", "product-marketplace-reject-info")
					.findList();

			for (Param p : params){
				notes.put(p.code, p.value);
			}

			List<String> lists = new ArrayList<>();
			if (approvedInformation != null){
				String[] infos = approvedInformation.split(";");
				for (int i=0; i < infos.length; i++){
					if (notes.containsKey(infos[i])){
						note.add(new MapProductNotes(true, notes.get(infos[i])));
						lists.add(notes.get(infos[i]));
					}
				}
			}

			for (Map.Entry<String, String> entry : notes.entrySet()) {
				if (!lists.contains(entry.getKey())){
					note.add(new MapProductNotes(false, entry.getValue()));
				}
			}
		}
*/
		return note;
	}

	@Transient
	@JsonProperty("rating")
	public MapProductRatting rating;

	@Transient
	public ProductType getInstanceProductType(){
		return ProductType.getProductTypeById(productType);

	}
	
	@JsonGetter("brand_name")
	public String getBrandName() {
		return brand == null ? "" : brand.name;
	}

	public Double getWeight(){
		return this.weight == null ? 0D : this.weight;
	}

	public Double getDiameter() {
		return this.diameter == null ? 0D : this.diameter;
	}

	public Double getNumberOfDiamond() {
		return this.numberOfDiamond == null ? 0D : this.numberOfDiamond;
	}

	public String getDiamondColor() {
		return diamondColor;
	}

	public String getDiamondClarity() {
		return diamondClarity;
	}

	public String getStamp() {
		return stamp;
	}

	public String getCertificate() {
		return certificate;
	}

	public Double getKadar() {
		return this.kadar == null ? 0D : this.kadar;
	}

	public Double getWeightOfGold() {
		return this.weightOfGold == null ? 0D : this.weightOfGold;
	}

	public Double getSumCaratOfGold() {
		return this.sumCaratOfGold == null ? 0D : this.sumCaratOfGold;
	}

	public Double getWeightOfGoldPlusDiamond() {
		return this.weightOfGoldPlusDiamond == null ? 0D : this.weightOfGoldPlusDiamond;
	}

	

	public void setBoxes(Boxes boxes){
		boxesTmp = boxes;
	}

	public Boxes getBoxesTmp(){
		return boxesTmp;
	}

	//TODO Dimensions
	@Transient
	@JsonGetter("dimension")
	public String getDimension(){
		return getDimensionX()+" x "+getDimensionY()+" x "+getDimensionZ();
	}
	@Transient
	@JsonGetter("dimension_x")
	public Double getDimensionX(){
		return dimension1 == null ? 0D : dimension1;
	}
	@Transient
	@JsonGetter("dimension_y")
	public Double getDimensionY(){
		return dimension2 == null ? 0D : dimension2;
	}
	@Transient
	@JsonGetter("dimension_z")
	public Double getDimensionZ(){
		return dimension3 == null ? 0D : dimension3;
	}
	public Double getVolumes(){
		return getDimensionX() * getDimensionY() * getDimensionZ();
	}
	public Boxes getBoxes(){
		return new Boxes(getDimensionX(), getDimensionY(), getDimensionZ(), true);
	}
	
/*	public List<String> getShortDescriptions(){
		if(shortDescriptions != null && !shortDescriptions.equals("")) {
			return Arrays.asList(shortDescriptions.split("##"));
		} else {
			return new ArrayList<String>();
		}
	}
*/	
	@Transient
	@JsonProperty("warranty")
	public String getWarranty(){
		String warranty = "";
		if (warrantyType != 0){
			warranty += warrantyPeriod + " Month ";
		}

		return warranty + fetchWarrantyType(warrantyType);
	}
	
	public static Map<Integer, String> fetchListWarrantyType(){
		Map<Integer, String> result = new LinkedHashMap<>();
		result.put(0, "No Warranty");
		result.put(1, "Seller Warranty");
		result.put(2, "Distributor Warranty");
		return result;
	}

	public String fetchWarrantyType(int id){
		Map<Integer, String> map = fetchListWarrantyType();
		String result = "";
		if(map.containsKey(id)){
			result = map.get(id).toString();
		}
		return result;
	}

	public void setRating(){
		rating = new MapProductRatting();
		rating.setAverage(ProductReview.getAverage(id));
		rating.setBintang1(ProductReview.getJumlah(id, 1));
		rating.setBintang2(ProductReview.getJumlah(id, 2));
		rating.setBintang3(ProductReview.getJumlah(id, 3));
		rating.setBintang4(ProductReview.getJumlah(id, 4));
		rating.setBintang5(ProductReview.getJumlah(id, 5));
		rating.setCount(ProductReview.getJumlah(id));
	}

	public void setVariant(){
		productVariants = ProductVariantGroup.getListRelatedAttribute(productVariantGroup, id);
	}

	public void setColors(){
		productColors = ProductVariantGroup.getListRelatedAttributeColor(productVariantGroup, this);
	}

	public Attribute findCollorProduct(){
		Attribute attr = null;
		for(Attribute attribute: attributes){
			if (attribute.baseAttribute.id.equals(1L)){
				attr = attribute;
				break;
			}
		}

		return attr;
	}

	@JsonProperty("seller")
	public MapMerchant getSeller(){
//		MapMerchant seller = new MapMerchant();
//		if (merchant != null){
//			seller.setCode(merchant.merchantCode);
//			seller.setName(merchant.name);
//			seller.setId(merchant.id);
//			seller.setCountRating(merchant.countRating);
//			seller.setRating(merchant.rating);
//			seller.setType("MERCHANT");
//		}else{
//			seller.setCode(vendor.code);
//			seller.setName(vendor.name);
//			seller.setId(vendor.id);
//			seller.setCountRating(vendor.countRating);
//			seller.setRating(vendor.rating);
//			seller.setType("VENDOR");
//		}
//		return seller;
		
		/*Hellobisnis*/
		return null;
	}


	public String getThumbnailUrl(){
		String image = thumbnailUrl==null || thumbnailUrl.isEmpty() ? "" : Constant.getInstance().getImageUrl() + thumbnailUrl;
		if (productDetail.size() > 0){
			String[] links = getImage3();
			for (String link : links) {
				if (link != null && !link.isEmpty()) {
					image = Constant.getInstance().getImageUrl() + link;
					break;
				}
			}
		}
		return image;
	}

	public String getImageUrl(){
		return imageUrl==null || imageUrl.isEmpty() ? "" : Constant.getInstance().getImageUrl() + imageUrl;
	}

	public String getProductColor(){
		String color = "";
		for (Attribute attr : attributes){
			if (attr.baseAttribute.id == 1L){
				color = attr.value;
				break;
			}
		}
		return color;
	}


	@java.beans.Transient
	public String getApproveStatus() {
		String statusName = "";
		switch (approvedStatus){
			case "P" : statusName = "Pending"; break;
			case "A" : statusName = "Approved"; break;
			case "R" : statusName = "Rejected"; break;
		}

		return statusName;
	}

	public static Finder<Long, Product> find = new Finder<Long, Product>(Long.class, Product.class);

//	public static String validate(Product model) {
//		String res = null;
//		Product uniqueCheck = Product.find.where().eq("product_code", model.productCode).findUnique();
//		if (model.name == null || model.name.equals("")) {
//			res = "Name field must not empty.";
//		} else if (model.productCode == null || model.productCode.equals("")) {
//			res = "Product code must not empty.";
//		} else if (uniqueCheck != null && !uniqueCheck.id.equals(model.id)) {
//			res = "Product code already exist.";
//		} else if (model.brand == null) {
//			res = "Brand doesn't exist.";
//		} else if (model.category == null) {
//			res = "Category doesn't exist.";
//		} else if (model.merchant == null) {
//			res = "Merchant doesn't exist.";
//		} else if (model.baseAttributes.size()==0){
//			res = "Please assign at least 1 valid base attribute";
//		}
//		return res;
//	}

	public Product(){
		super();
	}
	
	public Product(String sku, String name, int productType, boolean isNew,
			boolean status, Long brandId, Long categoryId, Long merchantId, Long itemCount, String currency, Double price, Double buyPrice,
			String[] attList) {
		this.sku = sku;
		this.name = name;
		this.metaTitle = name;
		this.slug = CommonFunction.slugGenerate(name);
		this.metaDescription = name;
		this.metaKeyword = name;
		this.productType = productType;
//		this.shortDescription = description.length() > 20 ? description.substring(0, 18) + "..." : description;
		this.isNew = isNew;
		this.status = status;
		this.itemCountOdoo = this.itemCount = itemCount;
		this.strikeThroughDisplay = price;
		this.priceDisplay = price;
		this.currency = Currency.find.byId(currency);
		this.price=price;
		this.buyPrice=buyPrice;
		this.brand = Brand.find.byId(brandId);
		if(productType == 3){
			this.merchant = Merchant.find.byId(merchantId);
		}else{
			this.vendor = Vendor.find.byId(merchantId);
			if (productType == 1){
				this.merchant = Merchant.find.byId(-1L);
			}
		}

		this.category = Category.find.byId(categoryId);
		this.parentCategory = Category.find.byId(this.category.parentCategory.id);
		this.grandParentCategory = Category.find.byId(this.parentCategory.parentCategory.id);
		this.attributes = Attribute.find.where().in("value", new ArrayList<String>(Arrays.asList(attList))).findSet();
		Set<BaseAttribute> listBaseAttribute = new HashSet<>();
		for(Attribute at : this.attributes){
			listBaseAttribute.add(at.baseAttribute);
		}
		this.baseAttributes = listBaseAttribute;
		numOfOrder = 0;
		isShow = true;
	}

	public Product(MapProductMerchant data){
		this.name = data.getName();
		this.metaTitle = data.getMetaTitle();
		this.metaKeyword = data.getMetaKeyword();
		this.metaDescription = data.getMetaDescription();
        this.grandParentCategory = Category.find.byId(data.getCategoryId());
        this.parentCategory = Category.find.byId(data.getSubCategoryId());
        this.category = Category.find.byId(data.getSubSubCategoryId());
        this.brand = Brand.find.byId(data.getBrandId());
        this.price = data.getPrice();
		this.priceDisplay = price;
        this.discountType = data.getDiscountType();
        this.discount = data.getDiscount();
		if (!data.getDiscountValidFrom().isEmpty()){
			this.discountActiveFrom = CommonFunction.getDateFrom(data.getDiscountValidFrom(), "MM/dd/yyyy");
		}
		if (!data.getDiscountValidTo().isEmpty()){
			this.discountActiveTo = CommonFunction.getDateFrom(data.getDiscountValidTo(), "MM/dd/yyyy");
		}
		this.skuSeller = data.getSku();
        this.itemCountOdoo = this.itemCount = data.getStock();
        this.approvedStatus = PENDING;
        this.currency = Currency.find.byId(data.getCurrency());
        this.firstPoStatus = 1;
        this.productType = 3;
		numOfOrder = 0;

		if (data.getId() != null){
            this.id = data.getId();
        }else{
			this.status = false;
			this.sku = generateSKU();
		}
		isShow = true;

//        weight, dimession, warranty, warranty_period, short_description, long_description, whats_in_the_box
	}
	
	public Product(MapProductMerchant2 data){ //TODO create product model from merchant request
		this.name = data.name;
		this.metaTitle = data.metaTitle;
		this.metaKeyword = data.metaKeyword;
		this.metaDescription = data.metaDescription;
        this.grandParentCategory = Category.find.byId(data.categoryId);
        this.parentCategory = Category.find.byId(data.subCategoryId);
        this.category = Category.find.byId(data.subSubCategoryId);
        this.brand = Brand.find.byId(data.brandId);
        this.currency = Currency.find.findUnique();
        
        this.price = data.price;
        this.strikeThroughDisplay = data.price;
		if(data.discount == null) data.discount = 0.0;
    	this.discount = data.discount;
    	this.discountType = data.discountType;
    	
        if(data.discount == 0 ){
        	this.discountType = 0;
        	this.priceDisplay = data.price;
        	this.discountActiveFrom = null;
        	this.discountActiveTo = null;
//            this.odooId = 0;
        }else{
            try {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
                this.discountActiveFrom = simpleDateFormat.parse(data.fromDate + " " + data.fromTime);
                this.discountActiveTo = simpleDateFormat.parse(data.toDate + " " + data.toTime);
            }catch (ParseException e){
            	this.discountActiveFrom = null;
            	this.discountActiveTo = null;
            }
            
            if (this.checkDiscountActive()) {
                if(this.discountType == 1){
                	this.priceDisplay = data.price - data.discount;
                }else{
                	this.priceDisplay = data.price - Math.floor(data.price * (data.discount/100));
                }
//                this.odooId = 0;
            } else {
            	this.priceDisplay = data.price;
//            	this.odooId = 1;
            }
        }
		
//        String[] dimen = data.dimension.split("x");
//        this.dimension1= Double.valueOf(dimen[0]);
//        this.dimension2= Double.valueOf(dimen[1]);
//        this.dimension3= Double.valueOf(dimen[2]);
        this.dimension1 = data.dimension1;
        this.dimension2 = data.dimension2;
        this.dimension3 = data.dimension3;
        this.weight = data.weight;
        this.diameter = data.diameter;
        
        this.numberOfDiamond = data.numberOfDiamond;
        this.diamondColor = data.diamondColor;
        this.diamondClarity = data.diamondClarity;
        this.stamp = data.stamp;
        this.certificate = data.certificate;
        this.kadar = data.kadar;
        this.weightOfGold = data.weightOfGold;
        this.sumCaratOfGold = data.sumCaratOfGold;
        this.weightOfGoldPlusDiamond = data.weightOfGoldPlusDiamond;

        this.shortDescriptions = String.join("##", data.shortDescription);
        this.description = data.longDescription;
        this.whatInTheBox = data.whatsInTheBox;
        this.sizeGuide = data.sizeGuide;
        
        this.warrantyType = data.warranty;
        if(this.warrantyType != 0){
        	this.warrantyPeriod = data.warrantyPeriod;
        }
        
        this.firstPoStatus = 1;
        this.productType = 3;
		numOfOrder = 0;

		if (data.id != null){
            this.id = data.id;
        }else{
//			this.status = false;
			this.sku = generateSKU();
		}
		isShow = true;
//		this.approvedStatus = PENDING;
		this.status = true;
		this.approvedStatus = AUTHORIZED;

	}
	
	public void updateDataJson(MapProductMerchant2 data) {
		this.name = data.name;
		this.metaTitle = data.metaTitle;
		this.metaKeyword = data.metaKeyword;
		this.metaDescription = data.metaDescription;
        this.grandParentCategory = Category.find.byId(data.categoryId);
        this.parentCategory = Category.find.byId(data.subCategoryId);
        this.category = Category.find.byId(data.subSubCategoryId);
        this.brand = Brand.find.byId(data.brandId);
        this.currency = Currency.find.findUnique();
        
        this.price = data.price;
        this.strikeThroughDisplay = data.price;
		if(data.discount == null) data.discount = 0.0;
    	this.discount = data.discount;
    	this.discountType = data.discountType;
    	
        if(data.discount == 0 ){
        	this.discountType = 0;
        	this.priceDisplay = data.price;
        	this.discountActiveFrom = null;
        	this.discountActiveTo = null;
//            this.odooId = 0;
        }else{
            try {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
                this.discountActiveFrom = simpleDateFormat.parse(data.fromDate + " " + data.fromTime);
                this.discountActiveTo = simpleDateFormat.parse(data.toDate + " " + data.toTime);
            }catch (ParseException e){
            	this.discountActiveFrom = null;
            	this.discountActiveTo = null;
            }
            
            if (this.checkDiscountActive()) {
                if(this.discountType == 1){
                	this.priceDisplay = data.price - data.discount;
                }else{
                	this.priceDisplay = data.price - Math.floor(data.price * (data.discount/100));
                }
//                this.odooId = 0;
            } else {
            	this.priceDisplay = data.price;
//            	this.odooId = 1;
            }
        }
		
        this.dimension1 = data.dimension1;
        this.dimension2 = data.dimension2;
        this.dimension3 = data.dimension3;
        this.weight = data.weight;
        this.diameter = data.diameter;
        
        this.numberOfDiamond = data.numberOfDiamond;
        this.diamondColor = data.diamondColor;
        this.diamondClarity = data.diamondClarity;
        this.stamp = data.stamp;
        this.certificate = data.certificate;
        this.kadar = data.kadar;
        this.weightOfGold = data.weightOfGold;
        this.sumCaratOfGold = data.sumCaratOfGold;
        this.weightOfGoldPlusDiamond = data.weightOfGoldPlusDiamond;

        this.shortDescriptions = String.join("##", data.shortDescription);
        this.description = data.longDescription;
        this.whatInTheBox = data.whatsInTheBox;
        this.sizeGuide = data.sizeGuide;
        
        this.warrantyType = data.warranty;
        if(this.warrantyType != 0){
        	this.warrantyPeriod = data.warrantyPeriod;
        }
        
        this.firstPoStatus = 1;
        this.productType = 3;
		numOfOrder = 0;

//		if (data.id != null){
//            this.id = data.id;
//        }else{
//			this.status = false;
//			this.sku = generateSKU();
//		}
		isShow = true;
		
	}

	public static void updateAverageRating(Long id){
		Product dt = Product.find.byId(id);
        dt.averageRating = ProductReview.getAverage(id);
        dt.countRating = ProductReview.getJumlah(id);
        dt.update();
	}


	public static Map<Integer, String> getListDiscountType(){
		Map<Integer, String> result = new LinkedHashMap<>();
		result.put(1, "Nominal");
		result.put(2, "Percent");
		return result;
	}

	public String getDiscountType(int id){
		Map<Integer, String> map = getListDiscountType();
		String result = "";
		if(map.containsKey(id)){
			result = map.get(id).toString();
		}
		return result;
	}

	public Double getStrikeThroughDisplay(){
		return price;
	}

	public Double getPriceDisplay(){
		//check override price
		Date currentDate = new Date();
		for (ProductPrice productPrice : productPrices) {
			if (productPrice.isActive && currentDate.after(productPrice.startDate) && currentDate.before(productPrice.endDate)) {
				switch (productPrice.overrideType) {
					case ProductPrice.OVERRIDE_SALEPRICE : return productPrice.salePrice < 10000? 10000 : productPrice.salePrice;
					case ProductPrice.OVERRIDE_DISCPERCENTAGE : return priceDisplay - Math.floor(productPrice.discountPercentage/100*priceDisplay);
					case ProductPrice.OVERRIDE_DISCNOMINAL : return (priceDisplay - productPrice.discountNominal) < 10000d ? 10000d : (priceDisplay - productPrice.discountNominal);
				}
				
			}
		}
		//check discount active
		if (this.getDiscountActive()) {
			switch (discountType){
				case 1 : return (price - discount);
				case 2 : return price - Math.floor(discount/100*price);
			}
		}
		return price;
	}

	@JsonGetter("real_price_display")
	public Double getRealPriceDisplay(){
		//check discount active
		if (this.getDiscountActive()) {
			switch (discountType){
				case 1 : return (price - discount);
				case 2 : return price - Math.floor(discount/100*price);
			}
		}
		return price;
	}

	public Double getBuyPrice(){
		return buyPrice == null ? 0D : buyPrice;
	}

	public Double getPriceDiscountAmount(){
		return discountType == 1 ? discount : 0D;
	}

	public Double getPriceDiscountPersen(){
		return discountType == 2 ? discount : 0D;
	}
	
	public String fetchDiscountString() {
		switch (discountType){
			case 1 : return "" + discount;
			case 2 : return discount + "%";
		}
		return "";
	}

	public ProductDetailVariance getProductDetail(){
		return ProductDetailVariance.find.where().eq("mainProduct.id",id).setMaxRows(1).findUnique();
	}

	public static Page<Product> page(int page, int pageSize, String sortBy, String order, String filter) {
		return
				find.where()
						.ilike("name", "%" + filter + "%")
						.eq("is_deleted", false)
						.orderBy(sortBy + " " + order)
						.findPagingList(pageSize)
						.setFetchAhead(false)
						.getPage(page);
	}

	public static Page<Product> page2(int page, int pageSize, String sortBy, String order, Long categoryId) {
		return
				find.where()
				.eq("category_id", categoryId)
				.eq("is_deleted", false)
				.ne("productType", ProductType.productTypeAdditional.getId())
				.orderBy(sortBy + " " + order)
				.findPagingList(pageSize)
				.setFetchAhead(false)
				.getPage(page);
	}

	public static Page<Product> productByType(Long type) {
		return
				find.where()
				.eq("productType", type)
				.eq("is_deleted", false)
				.findPagingList(100)
				.setFetchAhead(false)
				.getPage(0);
	}

	
	public static int findRowCount() {
		return
				find.where()
						.eq("is_deleted", false)
						.ne("productType", 3)
						.findRowCount();
	}

	public static int findRowCountMarketplace() {
		return
				find.where()
						.eq("is_deleted", false)
						.eq("productType", 3)
						.findRowCount();
	}

	public static List<Product> getRelatedGroups(Long id, ProductGroup productGroup){
		List<Product> result = new ArrayList<>();
		if (productGroup != null){
			result = Product.find.where()
					.eq("productGroup", productGroup)
					.ne("id", id)
					.eq("is_deleted", false)
					.gt("item_count", 0)
					.setMaxRows(10)
					.findList();
		}

		return result;
	}

	public static void seed(String code, int type, String name, Long brandId, Long categoryId, Long merchantId,
							String urlImage, ProductGroup productGroup, Integer odooId,
							Set<BaseAttribute> ba, Set<Attribute> a){

		Product prod1 = new Product(code, name, type,
				true, true, brandId, categoryId, merchantId,
				100L, "MMK", 100000D, 70000D, new String[] { "BLACK", "Android" });
		prod1.imageUrl = urlImage;
		prod1.thumbnailUrl = urlImage;
		prod1.firstPoStatus = 1;
		prod1.productGroup = productGroup;
		prod1.approvedStatus = AUTHORIZED;
		prod1.approvedBy = UserCms.find.byId(new Long(1));
//		prod1.odooId = odooId;
		prod1.baseAttributes = ba;
		prod1.attributes = a;

		if (type == 3){
			prod1.buyPrice = prod1.price - (3D/100 * prod1.price);
		}

		prod1.save();
		ProductDetail p1d1 = new ProductDetail();
		p1d1.mainProduct = prod1;
		p1d1.description = "long description";
		p1d1.weight = 200.0;
		p1d1.dimension1 = 70.0;
		p1d1.dimension2 = 40.0;
		p1d1.dimension3 = 10.0;
		p1d1.warrantyType = 0;
		p1d1.warrantyPeriod = 0;
		p1d1.whatInTheBox = name;
		p1d1.totalStock = 20;

		p1d1.fullImageUrls = Json.toJson(
				new String[] { urlImage })
				.toString();
		p1d1.mediumImageUrls = Json.toJson(
				new String[] { urlImage })
				.toString();
		p1d1.thumbnailImageUrls = Json.toJson(
				new String[] { urlImage })
				.toString();
		p1d1.threesixtyImageUrls = Json.toJson(
				new String[] { urlImage })
				.toString();
		p1d1.save();

	}

	public String generateSKU(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHH");
		int random = new Random().nextInt((2000 - 1000) + 1) + 1000;
		String newSku = "";
		newSku += this.grandParentCategory.name.substring(0,1);
		newSku += this.parentCategory.name.substring(0,1);
		newSku += this.category.name.substring(0,1);
		newSku += this.category.id;
		newSku += simpleDateFormat.format(new Date());
		newSku += String.valueOf(random);
        return newSku;
    }

	public void updateStatus(String newStatus) {
//		String oldBannerData = getChangeLogData(this);

		if(newStatus.equals("active"))
			status = Product.ACTIVE;
		else if(newStatus.equals("inactive"))
			status = Product.INACTIVE;

		super.update();

//		ChangeLog changeLog;
//		changeLog = new ChangeLog(LOG_TYPE, this.userCms.id, LOG_TABLE_NAME, this.id, "EDIT", oldBannerData, getChangeLogData(this));
//		changeLog.save();

	}

	public static Query<Product> getQueryProductList(){
		String sql = "SELECT id, sku, name, price, currency_cd, image_url, average_rating, count_rating, category_id, brand_id, merchant_id, parent_category_id, grand_parent_category_id, discount, view_count " +
				"FROM product as a " +
				"WHERE first_po_status = 1 " +
				"AND approved_status = 'A' " +
				"AND is_deleted = FALSE " +
				"AND status = TRUE " +
				"AND is_show = TRUE " +
				"AND item_count > 0 ";
		RawSql rawSql = RawSqlBuilder.parse(sql)
				.columnMapping("currency_cd", "currency.code")
				.columnMapping("category_id", "category.id")
				.columnMapping("parent_category_id", "parentCategory.id")
				.columnMapping("grand_parent_category_id", "grandParentCategory.id")
				.columnMapping("brand_id", "brand.id")
				.columnMapping("merchant_id", "merchant.id")
				.create();
		Query<Product> query = Ebean.find(Product.class);
		query.setRawSql(rawSql);

		return query;
	}

	public static Query<Product> getQueryProductList2(){
		String sql = "SELECT id, sku, name, price, currency_cd, image_url, average_rating, count_rating, category_id, brand_id, merchant_id, parent_category_id, grand_parent_category_id,discount,view_count FROM ( " +
				"SELECT id, sku, name, price, currency_cd, image_url, average_rating, count_rating, category_id, brand_id, merchant_id, parent_category_id, grand_parent_category_id,a.discount,a.view_count " +
				"FROM product as a " +
				"WHERE product_group_id IS NULL " +
				"AND first_po_status = 1 " +
				"AND approved_status = 'A' " +
				"AND is_deleted = FALSE " +
				"AND status = TRUE " +
				"AND item_count > 0 " +
				"UNION ALL " +
				"SELECT b.id, sku, c.name, price, currency_cd, image_url, average_rating, count_rating, category_id, brand_id, merchant_id, parent_category_id, grand_parent_category_id,b.discount,b.view_count " +
				"FROM product_group as c " +
				"JOIN product as b ON c.lowest_price_product = b.id "+
				"WHERE b.first_po_status = 1 " +
				"AND b.approved_status = 'A' " +
				"AND b.is_deleted = FALSE " +
				"AND b.status = TRUE " +
				"AND b.item_count > 0 " +
				") as list";
		RawSql rawSql = RawSqlBuilder.parse(sql)
				.columnMapping("currency_cd", "currency.code")
				.columnMapping("category_id", "category.id")
				.columnMapping("parent_category_id", "parentCategory.id")
				.columnMapping("grand_parent_category_id", "grandParentCategory.id")
				.columnMapping("brand_id", "brand.id")
				.columnMapping("merchant_id", "merchant.id")
				.create();
		Query<Product> query = Ebean.find(Product.class);
		query.setRawSql(rawSql);

		return query;
	}

	@SuppressWarnings({ "unchecked", "deprecation" })
	public static <T> BaseResponse<T> getData(Long categoryId, Long brandId, Long merchantId, Query<T> reqQuery, String sort, String filter, int offset, int limit)
			throws IOException {
		Query<T> query = reqQuery;

		// sort
		// ex : &sort=[{"property":"first_name","direction":"asc"}]
		if (!"".equals(sort)) {
			ApiSort[] sorts = new ObjectMapper().readValue(sort, ApiSort[].class);
			for (ApiSort apiSort : sorts) {
				query = query.orderBy(apiSort.getProperty() + " " + apiSort.getDirection());
			}
		}

		// filter
		ExpressionList<T> exp = query.where();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		ObjectNode result = Json.newObject();
		if (!"".equals(filter)) {
			ApiFilters filters = new ObjectMapper().readValue(filter, ApiFilters.class);
			if (filters.getLogic() == null) {
				exp = exp.conjunction();
			} else if ("and".equals(filters.getLogic())) {
				exp = exp.conjunction();
			} else {
				exp = exp.disjunction();
			}

			ApiFilter[] apiFilters = filters.getFilters();
			for (int i = 0; i < apiFilters.length; i++) {
				ApiFilter apiFilter = apiFilters[i];
				if (apiFilter.getProperty().equals("slug")){
					extractQueryBanner(apiFilter.getValues()[0].getValue().toString(), exp);
					continue;
				}
				ApiResponse.getInstance().setFilter(exp, formatter, apiFilter);
			}
			exp = exp.endJunction();
		}

		List<MapVariant> resFilter = new ArrayList<>();
		if (categoryId != 0L){
			Category data = Category.find.where().eq("id", categoryId).findUnique();
			if (data != null){
				data.viewCount = data.viewCount + 1;
				data.update();
			}

			List<SubCategoryBannerDetail> banners = SubCategoryBannerDetail.find
					.fetch("subCategoryBanner")
					.where()
					.eq("subCategoryBanner.category.id", categoryId)
					.eq("subCategoryBanner.status", true)
					.eq("subCategoryBanner.isDeleted", false)
					.orderBy("sequence ASC").findList();
			result.put("banner", Json.toJson(new ObjectMapper().convertValue(banners, MapCategoryBanerMenuDetail[].class)));
			List<Brand> brands = Brand.getHomePage();
			result.put("brand_banner", Json.toJson(new ObjectMapper().convertValue(brands, MapBrand[].class)));
			result.put("brand", Json.toJson(null));
			resFilter = Arrays.asList(Product.fetchAttributeData(categoryId));
			ApiResponse.getInstance().setFilter(exp, formatter, new ApiFilter("category.id", "equals", new ApiFilterValue[]{new ApiFilterValue(categoryId)}));
		}
		if (brandId != 0L){
			Brand data = Brand.find.where().eq("id", brandId).findUnique();
			if (data != null){
				data.viewCount = data.viewCount + 1;
				data.update();
			}
			result.put("brand", Json.toJson(new ObjectMapper().convertValue(data, MapBrand.class)));
			result.put("banner", Json.toJson(new ArrayList<>()));
			ApiResponse.getInstance().setFilter(exp, formatter, new ApiFilter("brand.id", "equals", new ApiFilterValue[]{new ApiFilterValue(brandId)}));
		}
		// assign
//		List<Object> list1 = query.findIds();

		query = exp.query();
		int total = query.findList().size();

		if (limit != 0) {
			query = query.setMaxRows(limit);
		}

		List<T> resData = query.findPagingList(limit).getPage(offset).getList();
		result.put("filter", Json.toJson(resFilter));
		result.put("result", Json.toJson(new ObjectMapper().convertValue(resData, MapProductList[].class)));

		// output
		BaseResponse<T> response = new BaseResponse<>();
		response.setData(result);
		response.setMeta(total, offset, limit);
		response.setMessage("Success");

		return response;
	}

	public static MapVariant[] fetchAttributeData(Long categoryId){
		Category category = Category.find.byId(categoryId);
		return new ObjectMapper().convertValue(category.listBaseAttribute, MapVariant[].class);
	}

	public static BannerList getBannerList(String slug){
		String[] split = slug.split("/");
		BannerList list = null;
		switch (split[0]){
			case "banner":
				list = Banner.getDetails(split[1]);
				break;
			case "category-banner":
				list = CategoryBannerDetail.getDetails(split[1]);
				break;
			case "menu-banner":
				list = CategoryBannerMenuDetail.getDetails(split[1]);
				break;
			case "most-popular":
				list = MostPopularBanner.getDetails(split[1]);
				break;
			case "promo":
				list = Promo.getDetails(split[1]);
				break;
			case "product-banner":
				list = SubCategoryBannerDetail.getDetails(split[1]);
				break;
			case "additional-category":
				list = AdditionalCategory.getDetails(split[1]);
				break;
			case "promo-all":
				list = Promo.getDetailAll();
				break;
		}

		return list;
	}

	private static void extractQueryBanner(String slug, ExpressionList exp){
        BannerList list = getBannerList(slug);
        if (list != null){
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            if (list.getProducts() != null){
                List<ApiFilterValue> filter = new ArrayList<>();
                for(Product dt : list.getProducts()){
                    filter.add(new ApiFilterValue(dt.id));
                }
                if (filter.size() > 0){
                    ApiResponse.getInstance().setFilter(exp, formatter, new ApiFilter("id", "in", filter.toArray(new ApiFilterValue[0])));
                }
            }
            if (list.getMerchants() != null){
                List<ApiFilterValue> filter = new ArrayList<>();
                for(Merchant dt : list.getMerchants()){
                    filter.add(new ApiFilterValue(dt.id));
                }
                if (filter.size() > 0){
                    ApiResponse.getInstance().setFilter(exp, formatter, new ApiFilter("merchant.id", "in", filter.toArray(new ApiFilterValue[0])));
                }
            }
            if (list.getCategories1() != null){
                List<ApiFilterValue> filter = new ArrayList<>();
                for(Category dt : list.getCategories1()){
                    filter.add(new ApiFilterValue(dt.id));
                }
                if (filter.size() > 0){
                    ApiResponse.getInstance().setFilter(exp, formatter, new ApiFilter("grand_parent_category_id", "in", filter.toArray(new ApiFilterValue[0])));
                }
            }
            if (list.getCategories2() != null){
                List<ApiFilterValue> filter = new ArrayList<>();
                for(Category dt : list.getCategories2()){
                    filter.add(new ApiFilterValue(dt.id));
                }
                if (filter.size() > 0){
                    ApiResponse.getInstance().setFilter(exp, formatter, new ApiFilter("parent_category_id", "in", filter.toArray(new ApiFilterValue[0])));
                }
            }
            if (list.getCategories3() != null){
                List<ApiFilterValue> filter = new ArrayList<>();
                for(Category dt : list.getCategories3()){
                    filter.add(new ApiFilterValue(dt.id));
                }
                if (filter.size() > 0){
                    ApiResponse.getInstance().setFilter(exp, formatter, new ApiFilter("category_id", "in", filter.toArray(new ApiFilterValue[0])));
                }
            }
            if (list.getBrands() != null){
                List<ApiFilterValue> filter = new ArrayList<>();
                for(Brand dt : list.getBrands()){
                    filter.add(new ApiFilterValue(dt.id));
                }
                if (filter.size() > 0){
                    ApiResponse.getInstance().setFilter(exp, formatter, new ApiFilter("brand.id", "in", filter.toArray(new ApiFilterValue[0])));
                }
            }
        }
    }

	public static <T> BaseResponse<T> getDataMerchant(Query<T> reqQuery, String type, String sort, String filter, int offset, int limit)
			throws IOException {
		Query<T> query = reqQuery;

		if (!"".equals(sort)) {
            query = query.orderBy(sort);
		} else {
			query = query.orderBy("t0.updated_at desc");
		}

		ExpressionList<T> exp = query.where();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");


        exp = exp.conjunction();
		exp = exp.or(Expr.ilike("t0.name", filter + "%"),Expr.ilike("t0.sku", filter + "%"));
        // ApiResponse.getInstance().setFilter(exp, formatter,"(" + new ApiFilter("name", "like", new ApiFilterValue[]{new ApiFilterValue(filter)}) + " or " +new ApiFilter("sku", "like", new ApiFilterValue[]{new ApiFilterValue(filter)}) + ")");
        // ApiResponse.getInstance().setFilter(exp, formatter, new ApiFilter("name", "like", new ApiFilterValue[]{new ApiFilterValue(filter)}));
        switch (type){
            case "in_stock" :
            	ApiResponse.getInstance().setFilter(exp, formatter, new ApiFilter("productDetail.isDeleted", "equals", new ApiFilterValue[]{new ApiFilterValue(false)}));
                ApiResponse.getInstance().setFilter(exp, formatter, new ApiFilter("productDetail.totalStock", "greater_than", new ApiFilterValue[]{new ApiFilterValue(0)}));
                ApiResponse.getInstance().setFilter(exp, formatter, new ApiFilter("t0.approved_status", "not_equals", new ApiFilterValue[]{new ApiFilterValue(REJECTED)}));
                break;
            case "out_stock" :
            	ApiResponse.getInstance().setFilter(exp, formatter, new ApiFilter("productDetail.isDeleted", "equals", new ApiFilterValue[]{new ApiFilterValue(false)}));
                ApiResponse.getInstance().setFilter(exp, formatter, new ApiFilter("productDetail.totalStock", "less_than_or_equals", new ApiFilterValue[]{new ApiFilterValue(0)}));
                break;
            case "inactive" :
                ApiResponse.getInstance().setFilter(exp, formatter, new ApiFilter("t0.status", "equals", new ApiFilterValue[]{new ApiFilterValue(INACTIVE)}));
				ApiResponse.getInstance().setFilter(exp, formatter, new ApiFilter("t0.approved_status", "equals", new ApiFilterValue[]{new ApiFilterValue(AUTHORIZED)}));
                break;
            case "approval_pending" :
                ApiResponse.getInstance().setFilter(exp, formatter, new ApiFilter("t0.approved_status", "equals", new ApiFilterValue[]{new ApiFilterValue(PENDING)}));
                break;
            case "rejected" :
                ApiResponse.getInstance().setFilter(exp, formatter, new ApiFilter("t0.approved_status", "equals", new ApiFilterValue[]{new ApiFilterValue(REJECTED)}));
                break;
            case "missing_image2" :
                ApiResponse.getInstance().setFilter(exp, formatter, new ApiFilter("t0.approved_status", "equals", new ApiFilterValue[]{new ApiFilterValue(REJECTED)}));
                break;
            case "best_selling" :
                ApiResponse.getInstance().setFilter(exp, formatter, new ApiFilter("t0.num_of_order", "greater_than", new ApiFilterValue[]{new ApiFilterValue(0)}));
                break;
        }

        exp = exp.endJunction();

		query = exp.query();
        if (type.equals("best_selling")){
            query = query.orderBy("t0.view_count DESC");
        }

		int total = query.findList().size();

		if (limit != 0) {
			query = query.setMaxRows(limit);
		}

		List<T> resData = query.findPagingList(limit).getPage(offset).getList();

		BaseResponse<T> response = new BaseResponse<>();
		response.setData(new ObjectMapper().convertValue(resData, MapProductMerchantList[].class));
		response.setMeta(total, offset, limit);
		response.setMessage("Success");

		return response;
	}


	public static Integer getProductInStockMerchant(Long merchant){
		return Product.find.where()
				.eq("t0.merchant_id", merchant)
				.eq("t0.is_deleted", false)
				.eq("productDetail.isDeleted", false)
				.gt("productDetail.totalStock", 0)
				.findRowCount();
	}

	public static Integer getProductOutStockMerchant(Long merchant){
		return Product.find.where()
				.eq("t0.merchant_id", merchant)
				.eq("t0.is_deleted", false)
				.eq("productDetail.isDeleted", false)
				.le("productDetail.totalStock", 0)
				.findRowCount();
	}

	public static Integer getProductInActiveMerchant(Long merchant){
		return Product.find.where()
				.eq("merchant_id", merchant)
				.eq("is_deleted", false)
				.eq("status", INACTIVE)
				.findRowCount();
	}

	public static MapProductSummary getProductSummaryMerchant(Long merchant){
		Integer inStock = getProductInStockMerchant(merchant);
		Integer outStock = getProductOutStockMerchant(merchant);
		Integer inActive = getProductInActiveMerchant(merchant);

		return new MapProductSummary(inStock, outStock, inActive);
	}

	public static void incrementViewCount(Long id, Integer viewCount){
		Product product = Product.find.byId(id);
		product.viewCount = viewCount;
		product.update();
	}

	public static List<MapProductRecommendation> getBestSellingProduct(Long merchant){
		List<Product> products = Product.find.where()
				.eq("merchant_id", merchant)
				.eq("status", true)
				.gt("numOfOrder", 0)
				.orderBy("numOfOrder DESC").setMaxRows(3).findList();

		List<MapProductRecommendation> data = new LinkedList<>();
		for (Product product : products){
			data.add(new MapProductRecommendation(product.id, product.name, product.getImageUrl()));
		}

		return data;
	}

	public static List<MapProductRecommendation> getTopTrending(Long merchant){
		List<Product> products = Product.find.where()
				.eq("merchant_id", merchant)
				.eq("status", true)
				.orderBy("view_count DESC").setMaxRows(3).findList();

		List<MapProductRecommendation> data = new LinkedList<>();
		for (Product product : products){
			data.add(new MapProductRecommendation(product.id, product.name, product.getImageUrl()));
		}

		return data;
	}

	public static List<MapProductRecommendation> getRecentAddition(Long merchant){
		List<Product> products = Product.find.where()
				.eq("merchant_id", merchant)
				.eq("status", true)
				.orderBy("created_at DESC").setMaxRows(3).findList();

		List<MapProductRecommendation> data = new LinkedList<>();
		for (Product product : products){
			data.add(new MapProductRecommendation(product.id, product.name, product.getImageUrl()));
		}

		return data;
	}

	public static int getNumberOfSold(Long merchant){
		String sql = "SELECT SUM(num_of_order) AS TOTAL " +
				"FROM product " +
				"WHERE merchant_id = "+merchant;

		com.avaje.ebean.SqlQuery query = Ebean.createSqlQuery(sql);
		return query.findUnique().getInteger("TOTAL");
	}

	public static MapRecommendation getRecommendation(Long merchant){
		return new MapRecommendation(getBestSellingProduct(merchant), getTopTrending(merchant), getRecentAddition(merchant));
	}

	@Override
	public void save() {
		super.save();
		// indexing.Product product = new indexing.Product(this);
		// product.index();
	}

	@Override
	public void update() {
		super.update();
		indexing.Product product = new indexing.Product(this);
		product.index();
	}

	@JsonIgnore
	public double getEligiblePointUsed() {
		CategoryLoyalty categoryLoyalty = category.categoryLoyalty.get(0);
		double priceDisplay = getPriceDisplay();
		//nominal
		if (categoryLoyalty.loyaltyUsageType == CategoryLoyalty.NOMINAL) {
			if (priceDisplay > categoryLoyalty.loyaltyUsageValue)
				return categoryLoyalty.loyaltyUsageValue;
			else
				return priceDisplay;
		}
		//percentage
		else {
			if (categoryLoyalty.loyaltyUsageValue * priceDisplay / 100 > categoryLoyalty.maxLoyaltyUsageValue && categoryLoyalty.maxLoyaltyUsageValue != 0){
				return categoryLoyalty.maxLoyaltyUsageValue;
			}
			else {
				if((categoryLoyalty.loyaltyUsageValue * priceDisplay / 100)-10000 <= 0) {
					return 0;
				}
				return (categoryLoyalty.loyaltyUsageValue * priceDisplay / 100)-10000;
			}
		}
	}
	

	@JsonIgnore
	public double getEligiblePointEarned() {
		CategoryLoyalty categoryLoyalty = category.categoryLoyalty.get(0);
		double priceDisplay = getPriceDisplay();
		if (categoryLoyalty.cashbackType == CategoryLoyalty.NOMINAL)
        {
        	return categoryLoyalty.cashbackValue;
        }
        else 
        {
        	if (categoryLoyalty.cashbackValue * priceDisplay /100 > categoryLoyalty.maxCashbackValue && categoryLoyalty.maxCashbackValue != 0)
        	{
        		return categoryLoyalty.maxCashbackValue;
        	}
        	else
        	{
        		return categoryLoyalty.cashbackValue * priceDisplay /100;
        	}
        }
	}

//	@JsonProperty("eligible_point_used")
//	public double getEligiblePointUsed() {
//		CategoryLoyalty categoryLoyalty = category.categoryLoyalty.get(0);
//		if (categoryLoyalty.loyaltyUsageType == CategoryLoyalty.NOMINAL) {
//			if (buyPrice > categoryLoyalty.loyaltyUsageValue)
//				return categoryLoyalty.loyaltyUsageValue;
//			else
//				return buyPrice;
//		}
//		else {
//			if (categoryLoyalty.loyaltyUsageValue * buyPrice / 100 > categoryLoyalty.maxLoyaltyUsageValue && category.getMaxLoyaltyUsageValue() != 0)
//				return categoryLoyalty.maxLoyaltyUsageValue;
//			else
//				return categoryLoyalty.loyaltyUsageValue * buyPrice / 100;
//		}
//	}
	
//	@JsonProperty("eligible_point_earn")
	@JsonIgnore
	public double getEligiblePointEarn(double payPrice) {
		CategoryLoyalty categoryLoyalty = category.categoryLoyalty.get(0);

		
		if (categoryLoyalty.cashbackType == CategoryLoyalty.NOMINAL)
        {
			if(payPrice > categoryLoyalty.cashbackValue) {
				return categoryLoyalty.cashbackValue;
			}
			else {
				return payPrice;
			}
        }
        else
        {

        	//System.out.println("cashback value : "+ category.getCashbackValue());
        	

        	if (category.getCashbackValue() * payPrice /100 > category.getMaxCashbackValue() && category.getMaxCashbackValue() != 0)
        	{
        		return category.getMaxCashbackValue();
        	}
        	else
        	{
        		return category.getCashbackValue() * payPrice /100;
        	}
        }
	}

	
	@JsonIgnore
	public Long getCheckoutType() {
		return checkoutType;
	}
	
	public void setCheckoutType(Long checkoutType) {
		this.checkoutType = checkoutType;
	}
		
	public double getEligiblePointEarnReferral(double payPrice) {
		CategoryLoyalty categoryLoyalty = category.categoryLoyalty.get(0);
		
		int cashbackTypeReferral = categoryLoyalty.cashbackTypeReferral;
		
		if (categoryLoyalty.cashbackTypeReferral == CategoryLoyalty.NOMINAL)
        {
			if(payPrice > categoryLoyalty.cashbackValueReferral) {
				return categoryLoyalty.cashbackValueReferral;
			}
			else {
				return payPrice;
			}
			
        }
        else
        {
        	//System.out.println("cashback referral value : "+ category.getCashbackValueReferral());
        	
        	if (category.getCashbackValueReferral() * payPrice /100 > category.getMaxCashbackValueReferral() && category.getMaxCashbackValueReferral() != 0)
        	{
        		return category.getMaxCashbackValueReferral();
        	}
        	else
        	{
        		return category.getCashbackValueReferral() * payPrice /100;
        	}
        }
	}
	
}
