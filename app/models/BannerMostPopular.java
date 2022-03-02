package models;

import com.avaje.ebean.Page;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hokeba.util.Constant;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "banner_most_popular")
public class BannerMostPopular extends BaseModel {

	private static final long serialVersionUID = 1L;
	public static final boolean ACTIVE = true;
	public static final boolean INACTIVE = false;
	private static final String LOG_TYPE = "ADMIN";
	private static final String LOG_TABLE_NAME = "banner_most_popular";
	private static final int LINK_TYPE_CATEGORY = 1;
	private static final int LINK_TYPE_BRAND = 2;
	private static final int LINK_TYPE_PRODUCT = 3;

	public static final int[] bannerSize1        = {380,450};
	public static final int[] bannerSize2        = {380,225};
	public static final int[] bannerSize3        = {190,225};

	@ManyToOne
	@JsonProperty("product_id_1")
	public Product product1;

	@JsonProperty("image_url_1")
	public String imageUrl1;

	@ManyToOne
	@JsonProperty("product_id_2")
	public Product product2;

	@JsonProperty("image_url_2")
	public String imageUrl2;

	@ManyToOne
	@JsonProperty("product_id_3")
	public Product product3;

	@JsonProperty("image_url_3")
	public String imageUrl3;

	@ManyToOne
	@JsonProperty("product_id_4")
	public Product product4;

	@JsonProperty("image_url_4")
	public String imageUrl4;

	@ManyToOne
	@JsonProperty("product_id_5")
	public Product product5;

	@JsonProperty("image_url_5")
	public String imageUrl5;

	@ManyToOne
	@JsonProperty("product_id_6")
	public Product product6;

	@JsonProperty("image_url_6")
	public String imageUrl6;

	@ManyToOne
	@JsonProperty("product_id_7")
	public Product product7;

	@JsonProperty("image_url_7")
	public String imageUrl7;


	@JsonIgnore
	@ManyToMany
	public List<Merchant> merchants;

	@JsonIgnore
	@ManyToMany
	public List<Category> categories;

	public boolean status;

	@JsonIgnore
	@JoinColumn(name="user_id")
	@ManyToOne
	public UserCms userCms;

	@Transient
	public String save;

	@javax.persistence.Transient
	public Long subcategoryid;

	@javax.persistence.Transient
	public List<String> merchant_list;

	@javax.persistence.Transient
	public List<String> category_list;

	@javax.persistence.Transient
	public Long productId1;

	@javax.persistence.Transient
	public Long productId2;

	@javax.persistence.Transient
	public Long productId3;

	@javax.persistence.Transient
	public Long productId4;

	@javax.persistence.Transient
	public Long productId5;

	@javax.persistence.Transient
	public Long productId6;

	@javax.persistence.Transient
	public Long productId7;

	@javax.persistence.Transient
	public String imageLink1;

	@javax.persistence.Transient
	public String imageLink2;

	@javax.persistence.Transient
	public String imageLink3;

	@javax.persistence.Transient
	public String imageLink4;

	@javax.persistence.Transient
	public String imageLink5;

	@javax.persistence.Transient
	public String imageLink6;

	@javax.persistence.Transient
	public String imageLink7;

	@javax.persistence.Transient
	public String tmpStatus;

	public static Finder<Long, BannerMostPopular> find = new Finder<>(Long.class, BannerMostPopular.class);

	public String getStatusName(){
		return (status)? "Active":"Inactive";
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

	public static Page<BannerMostPopular> page(int page, int pageSize, String sortBy, String order, String filter) {
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

	public String getImageLink(int number){
		String imageLink = "";
		if(number == 1){
			imageLink = imageUrl1==null || imageUrl1.isEmpty() ? "" : Constant.getInstance().getImageUrl() + imageUrl1;
		}else if(number == 2){
			imageLink = imageUrl2==null || imageUrl2.isEmpty() ? "" : Constant.getInstance().getImageUrl() + imageUrl2;
		}else if(number == 3){
			imageLink = imageUrl3==null || imageUrl3.isEmpty() ? "" : Constant.getInstance().getImageUrl() + imageUrl3;
		}else if(number == 4){
			imageLink = imageUrl4==null || imageUrl4.isEmpty() ? "" : Constant.getInstance().getImageUrl() + imageUrl4;
		}else if(number == 5){
			imageLink = imageUrl5==null || imageUrl5.isEmpty() ? "" : Constant.getInstance().getImageUrl() + imageUrl5;
		}else if(number == 6){
			imageLink = imageUrl6==null || imageUrl6.isEmpty() ? "" : Constant.getInstance().getImageUrl() + imageUrl6;
		}else if(number == 7){
			imageLink = imageUrl7==null || imageUrl7.isEmpty() ? "" : Constant.getInstance().getImageUrl() + imageUrl7;
		}
		return imageLink;
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
