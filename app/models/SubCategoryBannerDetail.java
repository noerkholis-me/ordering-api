package models;

import com.avaje.ebean.Page;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hokeba.util.CommonFunction;
import com.hokeba.util.Constant;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "sub_category_banner_detail")
public class SubCategoryBannerDetail extends BaseModel {

	private static final long serialVersionUID = 1L;
	public static final boolean ACTIVE = true;
	public static final boolean INACTIVE = false;
	private static final String LOG_TYPE = "ADMIN";
	private static final String LOG_TABLE_NAME = "sub_category_banner_detail";

	public int sequence;

	public String name;
	public String caption;
	public String title;
	public String slug;
	public String description;
	public String keyword;

	@ManyToOne
	@JsonProperty("category_banner_id")
	public SubCategoryBanner subCategoryBanner;

	@ManyToOne
	@JsonProperty("brand_id")
	public Brand brand;

	@JsonProperty("image_url")
	public String imageUrl;

	@JsonIgnore
	@ManyToMany
	public List<Product> products;

	@JsonIgnore
	@JoinColumn(name="user_id")
	@ManyToOne
	public UserCms userCms;

	@Transient
	public String save;

	@Transient
	public Long brandId;

	@Transient
	public String imageLink;

	@javax.persistence.Transient
	public Long categoryId;
	@javax.persistence.Transient
	public Long parentId;

	@javax.persistence.Transient
	public String categoryName;

	@javax.persistence.Transient
	@JsonProperty("image_src")
	public String getImageSrc(){
		return getImageLink();
	}
	@javax.persistence.Transient
	@JsonProperty("image_title")
	public String getImageTitle(){
		return name;
	}
	@javax.persistence.Transient
	@JsonProperty("image_keyword")
	public String getImageKeyword(){
		return name;
	}
	@javax.persistence.Transient
	@JsonProperty("image_description")
	public String getImageDescription(){
		return name;
	}
	@javax.persistence.Transient
    public List<String> product_list;
	@javax.persistence.Transient
    public String product_string;

	@javax.persistence.Transient
	@JsonProperty("product_detail")
	public Long getProductDetail() {
		return (products != null && products.size() == 1) ? products.get(0).id : null;
	}

	@javax.persistence.Transient
	@JsonProperty("product_detail_slug")
	public String getProductDetailSlug() {
		return (products != null && products.size() == 1) ? products.get(0).slug : null;
	}

	public String getSlug(){
		return "product-banner/"+slug;
	}

	public static Finder<Long, SubCategoryBannerDetail> find = new Finder<>(Long.class, SubCategoryBannerDetail.class);

//	public static String validation(BannerMostPopular model) {
//		if (product1 == null) {
//			return "Name must not empty.";
//		}
//		return null;
//	}
//
//	public List<ValidationError> validate() {
//		List<ValidationError> errors = new ArrayList<>();
//
//		if (name == null || name.isEmpty()) {
//			errors.add(new ValidationError("name", "Name must not empty."));
//		}
//		if(errors.size() > 0)
//			return errors;
//
//		return null;
//	}

	public static Page<SubCategoryBannerDetail> page(int page, int pageSize, String sortBy, String order, String filter) {
		return
				find.where()
						.eq("is_deleted", false)
						.orderBy(sortBy + " " + order)
						.findPagingList(pageSize)
						.setFetchAhead(false)
						.getPage(page);
	}

	public static int findRowCount() {
		return
				find.where()
						.eq("is_deleted", false)
						.findRowCount();
	}

	public String getImageLink(){
		String imageLink = "";
		imageLink = imageUrl==null || imageUrl.isEmpty() ? "" : Constant.getInstance().getImageUrl() + imageUrl;

		return imageLink;
	}

	public static List<SubCategoryBannerDetail> getAllChildCategory(Object id) {
		return SubCategoryBannerDetail.find.where().eq("category_banner_id", id).eq("is_deleted", false).order("sequence asc").findList();
	}

	public static void seed(Integer i, SubCategoryBanner scb, UserCms user, List<Product> products){
		SubCategoryBannerDetail detail = new SubCategoryBannerDetail();
		detail.subCategoryBanner = scb;
		detail.name = detail.caption = detail.title = detail.keyword = detail.description = "Banner "+i;
		detail.slug = CommonFunction.slugGenerate(detail.subCategoryBanner.category.id+"-"+detail.name);
		detail.imageUrl = "default/subcategory_banner"+i+".png";
		detail.userCms = user;
		detail.isDeleted = false;
		detail.sequence = i;
		detail.createdAt = new Date();
		detail.updatedAt = new Date();

		detail.save();

		detail.products = products;
		detail.update();
	}

	public static BannerList getDetails(String slug){
		SubCategoryBannerDetail banner = SubCategoryBannerDetail.find.where()
				.eq("is_deleted", false)
				.eq("slug", slug)
				.setMaxRows(1).findUnique();

		return banner != null ? new BannerList(banner) : null;
	}

//	public String getChangeLogData(BannerMostPopular data){
//		HashMap<String, String> map = new HashMap<>();
//		map.put("name",(data.name == null)? "":data.name);
//		map.put("status",(data.status == true)? "Active":"Inactive");
//
//		return Json.toJson(map).toString();
//	}
//
//	@Override
//	public void save() {
//		super.save();
//		ChangeLog changeLog = new ChangeLog(LOG_TYPE, this.userCms.id, LOG_TABLE_NAME, this.id, "ADD", null, getChangeLogData(this));
//		changeLog.save();
//	}
//
//	@Override
//	public void update() {
//		BannerMostPopular oldCategory = BannerMostPopular.find.byId(id);
//		super.update();
//
//		ChangeLog changeLog;
//		if(isDeleted == true){
//			changeLog = new ChangeLog(LOG_TYPE, this.userCms.id, LOG_TABLE_NAME, this.id, "DELETE", getChangeLogData(oldCategory), null);
//		}else{
//			changeLog = new ChangeLog(LOG_TYPE, this.userCms.id, LOG_TABLE_NAME, this.id, "EDIT", getChangeLogData(oldCategory), getChangeLogData(this));
//		}
//		changeLog.save();
//
//	}
//
//	public void updateStatus(String newStatus) {
//		String oldCategoryData = getChangeLogData(this);
//
//		if(newStatus.equals("active"))
//			status = BannerMostPopular.ACTIVE;
//		else if(newStatus.equals("inactive"))
//			status = BannerMostPopular.INACTIVE;
//
//		super.update();
//
//		ChangeLog changeLog;
//		changeLog = new ChangeLog(LOG_TYPE, this.userCms.id, LOG_TABLE_NAME, this.id, "EDIT", oldCategoryData, getChangeLogData(this));
//		changeLog.save();
//
//	}
}
