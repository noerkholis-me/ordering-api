package models;

import com.avaje.ebean.Page;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hokeba.util.Constant;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "category_banner_detail")
public class CategoryBannerDetail extends BaseModel {

	private static final long serialVersionUID = 1L;
	public static final boolean ACTIVE = true;
	public static final boolean INACTIVE = false;
	private static final String LOG_TYPE = "ADMIN";
	private static final String LOG_TABLE_NAME = "category_banner_detail";

	public int sequence;

	public String name;
	public String caption;
	public String title;
	public String slug;
	public String description;
	public String keyword;

	@ManyToOne
	@JsonProperty("category_banner_id")
	public CategoryBanner categoryBanner;

	@ManyToOne
	@JsonProperty("category_id")
	public Category category;

	@ManyToOne
	@JsonProperty("sub_category_id")
	public Category subCategory;

	@ManyToOne
	@JsonProperty("brand_id")
	public Brand brand;

	@ManyToOne
	@JsonProperty("product_id")
	public Product product;

	@JsonProperty("image_url")
	public String imageUrl;

	@JsonIgnore
	@JoinColumn(name="user_id")
	@ManyToOne
	public UserCms userCms;

	@Transient
	public String save;

	@Transient
	public Long productId;

	@Transient
	public Long categoryId;

	@Transient
	public Long subCategoryId;

	@Transient
	public Long brandId;

	@Transient
	public String imageLink;

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

	public static Finder<Long, CategoryBannerDetail> find = new Finder<>(Long.class, CategoryBannerDetail.class);

	@javax.persistence.Transient
	@JsonProperty("product_detail")
	public Long getProductDetail() {
		return product != null ? product.id : null;
	}

	@javax.persistence.Transient
	@JsonProperty("product_detail_slug")
	public String getProductDetailSlug() {
		return product != null ? product.slug : null;
	}

	public String getSlug(){
		return "category-banner/"+slug;
	}

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

	public static Page<CategoryBannerDetail> page(int page, int pageSize, String sortBy, String order, String filter) {
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

	public static List<CategoryBannerDetail> getAllChildCategory(Long id) {
		return CategoryBannerDetail.find.where().eq("category_banner_id", id).eq("is_deleted", false).order("sequence asc").findList();
	}

	public static BannerList getDetails(String slug){
		CategoryBannerDetail banner = CategoryBannerDetail.find.where()
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
