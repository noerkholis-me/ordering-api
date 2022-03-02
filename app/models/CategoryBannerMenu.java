package models;

import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Page;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hokeba.util.Constant;
import play.data.validation.ValidationError;
import play.libs.Json;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Entity
@Table(name = "category_banner_menu")
public class CategoryBannerMenu extends BaseModel {

	private static final long serialVersionUID = 1L;
	public static final boolean ACTIVE = true;
	public static final boolean INACTIVE = false;
	private static final String LOG_TYPE = "ADMIN";
	private static final String LOG_TABLE_NAME = "category_banner_menu";

	public boolean status;

	@ManyToOne
	@JsonProperty("category_id")
	public Category category;
	public String color;
	public int sequence;

	@JsonProperty("image_name")
	@Column(name = "image_name", columnDefinition = "TEXT")
	public String imageName;
	@JsonProperty("image_keyword")
	public String imageKeyword;
	@JsonProperty("image_title")
	public String imageTitle;
	@JsonProperty("image_description")
	@Column(name = "image_description", columnDefinition = "TEXT")
	public String imageDescription;
	@JsonProperty("image_url")
	public String imageUrl;

	@Transient
	public Long categoryId;

	@Transient
	public String categoryName;

	@JsonIgnore
	@JoinColumn(name="user_id")
	@ManyToOne
	public UserCms userCms;

	@Transient
	@JsonProperty("content")
	public List<CategoryBannerMenuDetail> details = new ArrayList<>();

	@Transient
	@JsonProperty("title")
	public String getTitle(){
		return category.name;
	}

	@Transient
	@JsonProperty("icon")
	public String getIcon(){
		return getImageLink();
	}


	@java.beans.Transient
	public String getStatus() {
		String statusName = "";
		if(status)
			statusName = "Active";
		else statusName = "Inactive";

		return statusName;
	}

	@Transient
	public String save;

	public String getImageLink(){
		String imageLink = "";
		imageLink = imageUrl==null || imageUrl.isEmpty() ? "" : Constant.getInstance().getImageUrl() + imageUrl;

		return imageLink;
	}

	public static Finder<Long, CategoryBannerMenu> find = new Finder<>(Long.class, CategoryBannerMenu.class);

	public static String validation(CategoryBannerMenu model) {
//		if (model.name.equals("")) {
//			return "Name must not empty.";
//		}
		return null;
	}

	public List<ValidationError> validate() {
		List<ValidationError> errors = new ArrayList<>();

		if(errors.size() > 0)
			return errors;

		return null;
	}



	public static Page<CategoryBannerMenu> page(int page, int pageSize, String sortBy, String order, String name, Integer filter) {
		ExpressionList<CategoryBannerMenu> qry = CategoryBannerMenu.find
				.where()
				.ilike("category.name", "%" + name + "%")
				.eq("t0.is_deleted", false);

		switch (filter){
			case 3: qry.eq("status", true);
				break;
			case 4: qry.eq("status", false);
				break;
		}

		return
				qry.orderBy(sortBy + " " + order)
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

	public static List<CategoryBannerMenu> getHomePage() {
		return CategoryBannerMenu.find.where()
				.eq("is_deleted", false)
				.setMaxRows(10).findList();
	}

	public String getChangeLogData(CategoryBannerMenu data){
		HashMap<String, String> map = new HashMap<>();
		map.put("status",(data.status == true)? "Active":"Inactive");

		return Json.toJson(map).toString();
	}

	@Override
	public void save() {
		super.save();
		ChangeLog changeLog = new ChangeLog(LOG_TYPE, this.userCms.id, LOG_TABLE_NAME, this.id, "ADD", null, getChangeLogData(this));
		changeLog.save();
	}

	@Override
	public void update() {
		CategoryBannerMenu oldCategory = CategoryBannerMenu.find.byId(id);
		super.update();

		ChangeLog changeLog;
		if(isDeleted == true){
			changeLog = new ChangeLog(LOG_TYPE, this.userCms.id, LOG_TABLE_NAME, this.id, "DELETE", getChangeLogData(oldCategory), null);
		}else{
			changeLog = new ChangeLog(LOG_TYPE, this.userCms.id, LOG_TABLE_NAME, this.id, "EDIT", getChangeLogData(oldCategory), getChangeLogData(this));
		}
		changeLog.save();

	}

	public void updateStatus(String newStatus) {
		String oldCategoryData = getChangeLogData(this);

		if(newStatus.equals("active"))
			status = CategoryBannerMenu.ACTIVE;
		else if(newStatus.equals("inactive"))
			status = CategoryBannerMenu.INACTIVE;

		super.update();

		ChangeLog changeLog;
		changeLog = new ChangeLog(LOG_TYPE, this.userCms.id, LOG_TABLE_NAME, this.id, "EDIT", oldCategoryData, getChangeLogData(this));
		changeLog.save();

	}
}
