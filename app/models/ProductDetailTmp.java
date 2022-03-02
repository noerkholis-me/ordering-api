package models;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlQuery;
import com.avaje.ebean.SqlRow;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hokeba.mapping.response.MapKeyValue;
import com.hokeba.mapping.response.MapProductImage;
import com.hokeba.util.Constant;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.*;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "product_detail_tmp")
public class ProductDetailTmp extends Model {

	@Id
	@JsonProperty("id_tmp")
	public String idTmp;
	public Long id;

	@JsonProperty("product_name")
	public String productName;
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
	public int warrantyType;
	@Column(name = "warranty_period", columnDefinition = "integer default 0")
	public int warrantyPeriod;
	@JsonProperty("sold_fulfilled_by")
	public String soldFulfilledBy;
	@JsonProperty("what_in_the_box")
	public String whatInTheBox;

	@JsonProperty("total_stock")
	public long totalStock;
	@JsonProperty("free_stock")
	public long freeStock;
	@JsonProperty("reserved_stock")
	public long reservedStock;

	public boolean stock;
	@JsonProperty("limited_stock")
	public boolean limitedStock;
	@JsonProperty("stock_counter")
	public boolean stockCounter;

	public boolean published;

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
	
	@Transient
	@JsonProperty("dimension_x")
	public Double getDimensionX(){
		return dimension1;
	}
	@Transient
	@JsonProperty("dimension_y")
	public Double getDimensionY(){
		return dimension2;
	}
	@Transient
	@JsonProperty("dimension_z")
	public Double getDimensionZ(){
		return dimension3;
	}

	@Transient
	@JsonProperty("warranty")
	public String getWarranty(){
		return warrantyPeriod + " Month "+getWarrantyType(warrantyType);
	}
	@Transient
	@JsonProperty("dimension")
	public String getDimension(){
		return dimension1 + " x "+dimension2+" x "+dimension3;
	}

	public List<String> getShortDescriptions(){
		if(shortDescriptions != null && !shortDescriptions.equals("")){
			return Arrays.asList(shortDescriptions.split("##"));
		}else return new ArrayList<String>();
	}

	@Transient
	@JsonProperty("attributes")
	public List<MapKeyValue> attributes;


	@Transient
	@JsonProperty("product_images")
	public List<MapProductImage> getProductImages(){
		List<MapProductImage> images = new ArrayList<>();
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

	public MapKeyValue setKV(String name, String value){
		MapKeyValue data = new MapKeyValue();
		data.setName(name);
		data.setValue(value);
		return data;
	}
	public void setAttribute(){
		attributes = new LinkedList<>();
		attributes.add(setKV("SKU", mainProduct.sku));
        for(Attribute attr : mainProduct.attributes){
            attributes.add(setKV(attr.baseAttribute.name, attr.getName()));
        }
		attributes.add(setKV("Warranty period", warrantyPeriod+" Month"));
		attributes.add(setKV("Warranty type", getWarrantyType(warrantyType)));

	}

	public String getFullUrlImage(String image){
		return image==null || image.isEmpty() ? "" : Constant.getInstance().getImageUrl() + image;
	}

	public String[] getImage3Link(){
		String[] links = getImage3();

		for(int i = 0; i < links.length; i++){
			if(links[i] == null || links[i].isEmpty() ){
				links[i] = "";
			}else{
				links[i] = Constant.getInstance().getImageUrl() + links[i];
			}

		}

		return links;
	}

	public HashMap<String,String> getImage3And1Link(){
		HashMap<String, String> result = new HashMap<>();
		String[] links5 = getImage3();
		String[] links1 = getImage1();

		for(int i = 0; i < links5.length; i++){
			if(links5[i] != null && !links5[i].isEmpty() && links1[i] != null && !links1[i].isEmpty()){
				result.put(Constant.getInstance().getImageUrl() + links5[i], Constant.getInstance().getImageUrl() + links1[i]);
			}

		}

		return result;
	}

	public String[] getImage5Link(){
		String[] links = getImage5();

		for(int i = 0; i < links.length; i++){
			if(links[i] == null || links[i].isEmpty() ){
				links[i] = "";
			}else{
				links[i] = Constant.getInstance().getImageUrl() + links[i];
			}

		}

		return links;
	}

	@Transient
	@JsonProperty("full_image_url")
	public String[] getImage1() {
		try {
			ObjectMapper om = new ObjectMapper();
			return om.readValue(fullImageUrls, String[].class);
		} catch (Exception e) {
			// e.printStackTrace();
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
			// e.printStackTrace();
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
			// e.printStackTrace();
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
			// e.printStackTrace();
			return new String[0];
		}
	}

	@Transient
	@JsonProperty("threesixty_image_url")
	public String[] getImage5() {
		try {
			ObjectMapper om = new ObjectMapper();
			return om.readValue(threesixtyImageUrls, String[].class);
		} catch (Exception e) {
			// e.printStackTrace();
			return new String[0];
		}
	}

	@OneToOne//(mappedBy = "productDetail")
	@JoinColumn(name = "product_id", referencedColumnName = "id_tmp")
	public ProductTmp mainProduct;

	@Transient
	@JsonProperty("main_product_id")
	public Long getMainProductId() {
		if (mainProduct != null)
			return mainProduct.id;
		return null;
	}

	// private List<ItemSummaryDetailVo> offerItems;//buat liat bisa dianter via
	// apa aja



	public static Map<Integer, String> getListWarrantyType(){
		Map<Integer, String> result = new LinkedHashMap<>();
		result.put(0, "No Warranty");
		result.put(1, "Seller Warranty");
		result.put(2, "Distributor Warranty");
		return result;
	}

	public String getWarrantyType(int id){
		Map<Integer, String> map = getListWarrantyType();
		String result = "";
		if(map.containsKey(id)){
			result = map.get(id).toString();
		}
		return result;
	}

	public static Finder<Long, ProductDetailTmp> find = new Finder<Long, ProductDetailTmp>(Long.class, ProductDetailTmp.class);

//	public static String validate(ProductDetail model) {
//		String res = null;
//		ProductDetail uniqueCheck = ProductDetail.find.where().eq("sku", model.sku).findUnique();
//		if (model.sku == null || model.sku.equals("")) {
//			res = "Sku must not empty.";
//		} else if (uniqueCheck != null && !uniqueCheck.id.equals(model.id)){
//			res = "Sku already exist.";
//		}
//		return res;
//	}

	public ProductDetailTmp() {

	}

	public ProductDetailTmp(ProductDetail detail) {
        this.idTmp = UUID.randomUUID().toString();
        this.id = detail.id;
        this.productName = detail.productName;
        this.shortDescriptions = detail.shortDescriptions;
        this.description = detail.description;
        this.weight = detail.weight;
        this.dimension1 = detail.dimension1;
        this.dimension2 = detail.dimension2;
        this.dimension3 = detail.dimension3;
        this.diameter = detail.diameter;
        this.numberOfDiamond = detail.numberOfDiamond;
        this.diamondColor = detail.diamondColor;
        this.diamondClarity = detail.diamondClarity;
        this.stamp = detail.stamp;
    	this.certificate = detail.certificate;
    	this.kadar = detail.kadar;
    	this.weightOfGold = detail.weightOfGold;
    	this.sumCaratOfGold = detail.sumCaratOfGold;
    	this.weightOfGoldPlusDiamond = detail.weightOfGoldPlusDiamond;
        this.warrantyType = detail.warrantyType;
        this.warrantyPeriod = detail.warrantyPeriod;
        this.soldFulfilledBy = detail.soldFulfilledBy;
        this.whatInTheBox = detail.whatInTheBox;
        this.totalStock = detail.totalStock;
        this.freeStock = detail.freeStock;
        this.reservedStock = detail.reservedStock;
        this.stock = detail.stock;
        this.limitedStock = detail.limitedStock;
        this.stockCounter = detail.stockCounter;
        this.published = detail.published;
        this.fullImageUrls = detail.fullImageUrls;
        this.mediumImageUrls = detail.mediumImageUrls;
        this.thumbnailImageUrls = detail.thumbnailImageUrls;
        this.blurImageUrls = detail.blurImageUrls;
        this.threesixtyImageUrls = detail.threesixtyImageUrls;
        this.sizeGuide = detail.sizeGuide;
	}

	public static List<BaseAttributeFilter> fetchAttributeData(List<Object> detailFrom) {
		Map<String, List<AttributeFilter>> resultSet = new HashMap<String, List<AttributeFilter>>();
		Set<Long> idList = new HashSet<Long>();
		for (Object detail : detailFrom) {
			idList.add((Long) detail);
		}
		List<SqlRow> attResult = new ArrayList<SqlRow>();
		if(!idList.isEmpty()){
			SqlQuery attQuery = Ebean.createSqlQuery("select " + "base_attribute.name as base_name, "
					+ "attribute.name as att_name, " + "attribute.image_url as image_url, " + "attribute.value as value "
					+ "from base_attribute " + "inner join attribute "
					+ "on attribute.base_attribute_id = base_attribute.id " + "inner join product_detail_attribute "
					+ "on product_detail_attribute.attribute_id = attribute.id " + "inner join product_detail "
					+ "on product_detail.id = product_detail_attribute.product_detail_id " + "where "
					+ "product_detail.id in (:idList) " + "group by attribute.id, base_attribute.id "
					+ "order by base_attribute.name, attribute.name");
			attQuery.setParameter("idList", idList);
			attResult = attQuery.findList();
		}
		for (SqlRow attRow : attResult) {
			String baseName = attRow.getString("base_name");
			if (!resultSet.containsKey(baseName)) {
				resultSet.put(baseName, new ArrayList<AttributeFilter>());
			}
			resultSet.get(baseName).add(new AttributeFilter(attRow.getString("att_name"), attRow.getString("image_url"),
					attRow.getString("value")));
		}
		List<BaseAttributeFilter> result = new ArrayList<BaseAttributeFilter>();
		for (String key : resultSet.keySet()) {
			result.add(new BaseAttributeFilter(key, resultSet.get(key)));
		}
		return result;
	}

	public void setShortDescriptions(List<String> list){
		shortDescriptions = String.join("##", list);
	}
}
