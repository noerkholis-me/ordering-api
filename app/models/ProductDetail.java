package models;

import com.avaje.ebean.*;
import com.avaje.ebean.Query;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hokeba.api.*;
import com.hokeba.mapping.response.MapAttribute;
import com.hokeba.mapping.response.MapKeyValue;
import com.hokeba.mapping.response.MapProductImage;
import com.hokeba.util.Constant;
import play.Logger;
import play.libs.Json;

import javax.persistence.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductDetail extends BaseModel {
	@JsonProperty("product_name")
	public String productName;
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
	@JsonProperty("size_guide")
	public String sizeGuide;

	@javax.persistence.Transient
	@JsonProperty("dimension_x")
	public Double getDimensionX(){
		return dimension1;
	}
	@javax.persistence.Transient
	@JsonProperty("dimension_y")
	public Double getDimensionY(){
		return dimension2;
	}
	@javax.persistence.Transient
	@JsonProperty("dimension_z")
	public Double getDimensionZ(){
		return dimension3;
	}

	@javax.persistence.Transient
	@JsonProperty("warranty")
	public String getWarranty(){
		String warranty = "";
		if (warrantyType != 0){
			warranty += warrantyPeriod + " Month ";
		}

		return warranty + getWarrantyType(warrantyType);
	}

	@javax.persistence.Transient
	@JsonProperty("dimension")
	public String getDimension(){
		return dimension1 + " x "+dimension2+" x "+dimension3;
	}

	public List<String> getShortDescriptions(){
		if(shortDescriptions != null && !shortDescriptions.equals("")){
			return Arrays.asList(shortDescriptions.split("##"));
		}else return new ArrayList<String>();
	}

	@javax.persistence.Transient
	@JsonProperty("attributes")
	public List<MapKeyValue> attributes;

	@javax.persistence.Transient
	@JsonProperty("attributes_s")
	public List<MapAttribute> attributesMerchant;


	@javax.persistence.Transient
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

	public MapKeyValue setKV(String name, String value, String additional){
		MapKeyValue data = new MapKeyValue();
		data.setName(name);
		data.setValue(value);
		data.setAdditional(additional);
		return data;
	}

	public void setAttribute(){
		setAttribute(false);
	}

	public void setAttribute(Boolean fromMerchant){
		attributes = new LinkedList<>();
		if (!fromMerchant){
			attributes.add(setKV("SKU", mainProduct.sku, null));
			for(Attribute attr : mainProduct.attributes){
				attributes.add(setKV(attr.baseAttribute.name, attr.getName(), attr.additional));
			}
			attributes.add(setKV("Warranty period", warrantyPeriod+" Month", null));
			attributes.add(setKV("Warranty type", getWarrantyType(warrantyType), null));
		}else{
			attributesMerchant = new LinkedList<>();
			for(Attribute attr : mainProduct.attributes){
				attributes.add(setKV(attr.baseAttribute.name, attr.getName(), attr.additional));
				attributesMerchant.add(new MapAttribute(attr.baseAttribute.id, attr.id, attr.baseAttribute.name, attr.getName()));
			}
		}
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

	public Double getDimension1() {
		return dimension1 == null ? 0D : dimension1;
	}

	public Double getDimension2() {
		return dimension2  == null ? 0D : dimension2;
	}

	public Double getDimension3() {
		return dimension3  == null ? 0D : dimension3;
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

	// @Column(name = "json_attribute", columnDefinition = "TEXT")
	// @JsonIgnore
	// public String jsonAttribute;
	//
	// @Transient
	// @JsonProperty("attribute_data")
	// public Object getAttributeData() {
	// try {
	// ObjectMapper om = new ObjectMapper();
	// return om.readValue(jsonAttribute, Object[].class);
	// } catch (Exception e) {
	// // e.printStackTrace();
	// return new Object[0];
	// }
	// }

	@ManyToOne
	@JoinColumn(name = "product_id", referencedColumnName = "id")
	@JsonIgnore
	public Product mainProduct;

	@Transient
	@JsonProperty("main_product_id")
	public Long getMainProductId() {
		if (mainProduct != null)
			return mainProduct.id;
		return null;
	}

	@OneToMany(mappedBy = "product")
	@JsonIgnore
	public List<WishList> wishlist;

	// private List<ItemSummaryDetailVo> offerItems;//buat liat bisa dianter via
	// apa aja


	public Double getWeight() {
		return weight == null ? 0D : weight;
	}

	public Double getDiameter() {
		return diameter == null ? 0D : diameter;
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
		return kadar == null ? 0D : kadar;
	}
	
	public Double getWeightOfGold() {
		return weightOfGold == null ? 0D : weightOfGold;
	}
	
	public Double getSumCaratOfGold() {
		return sumCaratOfGold == null ? 0D : sumCaratOfGold;
	}
	
	public Double getWeightOfGoldPlusDiamond() {
		return weightOfGoldPlusDiamond == null ? 0D : weightOfGoldPlusDiamond;
	}
	
	public Double getNumberOfDiamond() {
		return numberOfDiamond == null ? 0D : numberOfDiamond;
	}
	
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

	public static Finder<Long, ProductDetail> find = new Finder<Long, ProductDetail>(Long.class, ProductDetail.class);

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

	public ProductDetail() {

	}

	public ProductDetail(Product main, Double listPrice, Double offerPrice, boolean stock,
			boolean limitedStock, boolean stockCounter, Long stockCount, boolean published) {
		this.mainProduct = main;
		this.productName = main.name;
		this.stock = stock;
		this.limitedStock = limitedStock;
		this.stockCounter = stockCounter;
		this.totalStock = stockCount;
		this.reservedStock = stockCount;

		this.published = published;
		this.save();
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

	@SuppressWarnings({ "unchecked", "deprecation" })
	public static <T> BaseResponse<T> getData(Query<T> query, String sort, String filter, int offset, int limit)
			throws IOException {

		// sort
		// ex : &sort=[{"property":"first_name","direction":"asc"}]
		if (!sort.equals("")) {
			ApiSort[] sorts = new ObjectMapper().readValue(sort, ApiSort[].class);
			for (ApiSort apiSort : sorts) {
				query = query.orderBy(apiSort.getProperty() + " " + apiSort.getDirection());
			}
		}

		// filter
		ExpressionList<T> exp = query.where();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		if (filter != "") {
			ApiFilters filters = new ObjectMapper().readValue(filter, ApiFilters.class);
			System.out.println(Json.toJson(filters));
			if (filters.getLogic() == null) {
				exp = exp.conjunction();
			} else if (filters.getLogic().equals("and")) {
				exp = exp.conjunction();
			} else {
				exp = exp.disjunction();
			}

			ApiFilter[] apiFilters = filters.getFilters();
			for (int i = 0; i < apiFilters.length; i++) {
				ApiFilter apiFilter = apiFilters[i];
				ApiResponse.getInstance().setFilter(exp, formatter, apiFilter);
			}
			exp = exp.endJunction();
		}

		// assign
		List<Object> list1 = query.findIds();
		List<BaseAttributeFilter> resFilter = ProductDetail.fetchAttributeData(list1);

		query = exp.query();
		int total = query.findList().size();

		if (limit != 0) {
			query = query.setMaxRows(limit);
		}

		// paging
		List<T> resData = query.findPagingList(limit).getPage(offset).getList();
		Logger.debug(query.getGeneratedSql());

		ObjectNode result = Json.newObject();
		result.put("filter", Json.toJson(resFilter));
		result.put("result", Json.toJson(resData));

		// output
		BaseResponse<T> response = new BaseResponse<T>();
		response.setData(result);
		response.setMeta(total, offset, limit);
		response.setMessage("Success");

		return response;
	}

	public void setShortDescriptions(List<String> list){
		shortDescriptions = String.join("##", list);
	}
}
