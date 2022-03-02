package models;

import com.avaje.ebean.Page;
import com.avaje.ebean.annotation.CreatedTimestamp;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hokeba.mapping.response.MapMerchant;
import com.hokeba.mapping.response.MapProductRatting;
import com.hokeba.util.Constant;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "product_tmp")
public class ProductTmp extends Model {

	public static final boolean ACTIVE = true;
	public static final boolean INACTIVE = false;
	public static final String PENDING = "P";
	public static final String AUTHORIZED = "A";
	public static final String REJECTED = "R";

	@Id
	@JsonProperty("id_tmp")
	public String idTmp;

	public Long id;

	public String sku;
	public String name;
	@JsonProperty("product_type")
	public int productType;
	@JsonProperty("is_new")
	public boolean isNew;
	@JsonProperty("status")
	public boolean status;
	public String title;
	public String slug;
	public String description ;
	public String keyword;

	@JsonProperty("buy_price")
	public Double buyPrice;
	public Double price;
	@ManyToOne(cascade = { CascadeType.ALL })
	@JoinColumn(name = "currency_cd", referencedColumnName = "code")
	public Currency currency;

	@JsonIgnore
	@Column(name = "view_count")
	public int viewCount;

	public boolean stock;
	@JsonProperty("item_count")
	public Long itemCount;

	@JsonProperty("strike_through_display")
	public Double strikeThroughDisplay;

	@JsonProperty("price_display")
	public Double priceDisplay;

	public Double discount;

	@Column(name = "discount_type", columnDefinition = "integer default 0")
	public int discountType;

	@JsonProperty("thumbnail_url")
	public String thumbnailUrl;
	@JsonProperty("image_url")
	public String imageUrl;

	@ManyToMany
	@JsonIgnore
	public Set <BaseAttribute> baseAttributes;

	@ManyToMany
	@JsonIgnore
	public Set <Attribute> attributes;

	@ManyToMany
	@JsonIgnore
	public Set <Size> sizes;

	//odoo
	@Column(name = "odoo_id")
	public String odooId;

	@Transient
	@JsonProperty("base_attributes")
	public String[] getBaseAttributes(){
		String[] arrayAtt = new String[this.baseAttributes.size()];
		int count = 0;
		for (BaseAttribute model : this.baseAttributes) {
			arrayAtt[count] = model.name;
			count++;
		}
		return arrayAtt;
	}


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

	@OneToOne//(mappedBy = "mainProduct")
	@JoinColumn(name = "detail_id", referencedColumnName = "id_tmp")
	public ProductDetailTmp productDetail;

//	@ManyToMany (mappedBy = "products")
//	public List<Tag> tags;

	@JsonProperty("average_rating")
	public float averageRating;

	@JsonProperty("count_rating")
	public int countRating;

	@JsonProperty("discount_active_from")
	public Date discountActiveFrom;

	@JsonProperty("discount_active_to")
	public Date discountActiveTo;

	@JsonIgnore
	@JoinColumn(name="product_group_id")
	@ManyToOne
	public ProductGroup productGroup;

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
	public String approvedNote;

	@Column(name = "approved_by")
	@JsonIgnore
	@ManyToOne
	public UserCms approvedBy;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "updated_at")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "Asia/Jakarta")
	public Date approvedAt;

	@Temporal(TemporalType.TIMESTAMP)
	@CreatedTimestamp
	@Column(name = "created_at", updatable = false)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "Asia/Jakarta")
	public Date createdAt;

	@Transient
	public Long brandId;

	@Transient
	public Long categoryId;

	@Transient
	public Long subCategoryId;

	@Transient
	public Long subSubCategoryId;

	@Transient
	public Double weight;

	@Transient
	public Double dimension1;

	@Transient
	public Double dimension2;

	@Transient
	public Double dimension3;
	
	@Transient
	public Double diameter;
	
	@Transient
	public Double numberOfDiamond;
	
	@Transient
	public String diamondColor;
	
	@Transient
	public String diamondClarity;
	
	@Transient
	public String stamp;
	
	@Transient
	public String certificate;
	
	@Transient
	public Double kadar;
	
	@Transient
	public Double weightOfGold;
	
	@Transient
	public Double sumCaratOfGold;
	
	@Transient
	public Double weightOfGoldPlusDiamond;

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
	public String whatInTheBox = "";

	@Transient
	public String productDescription;

    @Transient
    public int warrantyType;

    @Transient
    public int warrantyPeriod;

    @Transient
    public List<String> listShortDescriptions;

	@Transient
	public List<Long> listBaseAttribute;

	@Transient
	public List<Long> listAttribute;

	@Transient
	public String newProduct;

//	public int position;
	@Column(name = "is_show", columnDefinition = "boolean default true")
	public Boolean isShow;

	@Transient
	public String save;

	@Transient
	@JsonProperty("product_code")
	public String getProductCode(){
		return sku;
	}

	@Transient
	@JsonProperty("promo_price")
	public Double getPromoPrice(){
		return price;
	}
	@Transient
	@JsonProperty("is_active")
	public Boolean getIsActive(){
		return status;
	}
	@Transient
	@JsonProperty("stock")
	public Long getStock(){
		return itemCount;
	}

	@Transient
	@JsonProperty("meta_title")
	public String getMetaTitle(){
		return title;
	}
	@Transient
	@JsonProperty("meta_keyword")
	public String getMetaKeyword(){
		return keyword;
	}
	@Transient
	@JsonProperty("meta_description")
	public String getMetaDescription(){
		return description;
	}
	@Transient
	@JsonProperty("categories")
	public List<Category> getCategories(){
		return Arrays.asList(category);
	}

	@Transient
	@JsonProperty("rating")
	public MapProductRatting rating;

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


	@JsonProperty("seller")
	public MapMerchant getSeller(){
		MapMerchant seller = new MapMerchant();
		if (vendor != null){
			seller.setCode(vendor.code);
			seller.setName(vendor.name);
			seller.setId(vendor.id);
			seller.setType("VENDOR");
		}else{
			seller.setCode(merchant.merchantCode);
			seller.setName(merchant.name);
			seller.setId(merchant.id);
			seller.setType("MERCHANT");
		}
		return seller;
	}


	public String getThumbnailUrl(){
		return thumbnailUrl==null || thumbnailUrl.isEmpty() ? "" : Constant.getInstance().getImageUrl() + thumbnailUrl;
	}

	public String getImageUrl(){
		return imageUrl==null || imageUrl.isEmpty() ? "" : Constant.getInstance().getImageUrl() + imageUrl;
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

	public static Finder<String, ProductTmp> find = new Finder<String, ProductTmp>(String.class, ProductTmp.class);

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

	public ProductTmp(){

	}

	public ProductTmp(Product product) {
		this.idTmp = UUID.randomUUID().toString();
		this.id = product.id;
		this.sku = product.sku;
		this.productType = product.productType;
		this.name = product.name;
		this.title = product.metaTitle;
		this.slug = product.slug;
		this.description = product.description;
		this.keyword = product.metaKeyword;
		this.isNew = product.isNew;
		this.status = product.status;
		this.itemCount = product.itemCount;
		this.strikeThroughDisplay = product.strikeThroughDisplay;
		this.currency = product.currency;
		this.price=product.price;
		this.buyPrice=product.buyPrice;
		this.brand = product.brand;
		this.vendor = product.vendor;
		this.merchant = product.merchant;
		this.viewCount = product.viewCount;
		this.stock = product.stock;
		this.itemCount = product.itemCount;
		this.strikeThroughDisplay = product.strikeThroughDisplay;
		this.priceDisplay = product.priceDisplay;
		this.discount = product.discount;
		this.discountType = product.discountType;
		this.thumbnailUrl = product.thumbnailUrl;
		this.imageUrl = product.imageUrl;
		this.averageRating = product.averageRating;
		this.countRating = product.countRating == null ? 0 : product.countRating;
		this.discountActiveFrom = product.discountActiveFrom;
		this.discountActiveTo = product.discountActiveTo;
		this.productGroup = product.productGroup;
		this.firstPoStatus = product.firstPoStatus;
		this.buyPrice = product.buyPrice;

		this.category = product.category;
		this.parentCategory = product.parentCategory;
		this.grandParentCategory = product.grandParentCategory;
		this.attributes = product.attributes;
		this.baseAttributes = product.baseAttributes;
		this.odooId = product.odooId;
		this.isShow = product.isShow;
		this.sizes = product.sizes;
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


	@Transient
	public ProductType getInstanceProductType(){
		return ProductType.getProductTypeById(productType);

	}

//	public ProductDetail getProductDetail(){
//		return ProductDetail.find.where().eq("mainProduct.id",id).findUnique();
//	}

	public static Page<ProductTmp> page(int page, int pageSize, String sortBy, String order, String filter) {
		return
				find.where()
						.ilike("name", "%" + filter + "%")
						.eq("is_deleted", false)
						.orderBy(sortBy + " " + order)
						.findPagingList(pageSize)
						.setFetchAhead(false)
						.getPage(page);
	}

	public static int findRowCount() {
		return
				find.where()
						.eq("approvedStatus", PENDING)
						.findRowCount();
	}

	public static ProductTmp approveProduct(String id, UserCms userCms) {
		ProductTmp productTmp = null;
		if(id != null){
			productTmp = ProductTmp.find.where().eq("idTmp", id).findUnique();

			Product prod = Product.find.byId(productTmp.id);
			prod.name = productTmp.name;
			prod.metaTitle = productTmp.title;
			prod.slug = productTmp.slug;
			prod.description = productTmp.description;
			prod.metaKeyword = productTmp.keyword;
			prod.strikeThroughDisplay = productTmp.strikeThroughDisplay;
			prod.currency = productTmp.currency;
			prod.price = productTmp.price;
			prod.buyPrice = productTmp.buyPrice;
			prod.brand = productTmp.brand;
			prod.vendor = productTmp.vendor;
			prod.merchant = productTmp.merchant;
			prod.itemCountOdoo = prod.itemCount = productTmp.itemCount;
			prod.priceDisplay = productTmp.priceDisplay;
			prod.discount = productTmp.discount;
			prod.discountType = productTmp.discountType;
			prod.thumbnailUrl = productTmp.thumbnailUrl;
			prod.imageUrl = productTmp.imageUrl;
			prod.averageRating = productTmp.averageRating;
			prod.countRating = productTmp.countRating;
			prod.discountActiveFrom = productTmp.discountActiveFrom;
			prod.discountActiveTo = productTmp.discountActiveTo;

			prod.category = productTmp.category;
			prod.parentCategory = productTmp.parentCategory;
			prod.grandParentCategory = productTmp.grandParentCategory;
			prod.attributes.clear();
			prod.attributes.addAll(productTmp.attributes);
			prod.baseAttributes.clear();
			prod.baseAttributes.addAll(productTmp.baseAttributes);
			prod.userCms = productTmp.userCms;
			prod.update(prod.id);

			ProductDetailTmp detailTmp = productTmp.productDetail;
			ProductDetail detail = ProductDetail.find.where().eq("mainProduct", prod).findUnique();
//			detail.mainProduct = prod;
			detail.weight= detailTmp.weight;
			detail.dimension1= detailTmp.dimension1;
			detail.dimension2= detailTmp.dimension2;
			detail.dimension3= detailTmp.dimension3;
			detail.diameter = detailTmp.diameter;
			detail.numberOfDiamond = detailTmp.numberOfDiamond;
			detail.diamondColor = detailTmp.diamondColor;
			detail.diamondClarity = detailTmp.diamondClarity;
			detail.stamp = detailTmp.stamp;
			detail.certificate = detailTmp.certificate;
	    	detail.kadar = detailTmp.kadar;
	    	detail.weightOfGold = detailTmp.weightOfGold;
	    	detail.sumCaratOfGold = detailTmp.sumCaratOfGold;
	    	detail.weightOfGoldPlusDiamond = detailTmp.weightOfGoldPlusDiamond;
			detail.warrantyType= detailTmp.warrantyType;
			detail.warrantyPeriod= detailTmp.warrantyPeriod;
			detail.whatInTheBox = detailTmp.whatInTheBox;
			detail.description = detailTmp.description;
			detail.setShortDescriptions(detailTmp.getShortDescriptions());
			detail.fullImageUrls = detailTmp.fullImageUrls;
			detail.mediumImageUrls = detailTmp.mediumImageUrls;
			detail.thumbnailImageUrls = detailTmp.thumbnailImageUrls;
			detail.threesixtyImageUrls = detailTmp.threesixtyImageUrls;
			detail.update(detail.id);

			productTmp.approvedStatus = Product.AUTHORIZED;
			productTmp.approvedBy = userCms;
			productTmp.approvedAt = new Date();
			productTmp.update();

		}

		return productTmp;
	}
}
