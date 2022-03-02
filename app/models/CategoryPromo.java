package models;

import com.avaje.ebean.Page;
import com.fasterxml.jackson.annotation.JsonIgnore;
import play.data.validation.ValidationError;
import play.libs.Json;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Entity
@Table(name = "category_promo")
public class CategoryPromo extends BaseModel {

	private static final long serialVersionUID = 1L;
	public static final boolean ACTIVE = true;
	public static final boolean INACTIVE = false;
	private static final String LOG_TYPE = "ADMIN";
	private static final String LOG_TABLE_NAME = "category_promo";

	@Column(unique = true)
	public String name;

	public boolean status;

	@JsonIgnore
	@JoinColumn(name="user_id")
	@ManyToOne
	public UserCms userCms;

	@Transient
	public String save;

	public static Finder<Long, CategoryPromo> find = new Finder<>(Long.class, CategoryPromo.class);

	public static String validation(CategoryPromo model) {
		if (model.name.equals("")) {
			return "Name must not empty.";
		}
		return null;
	}

	public List<ValidationError> validate() {
		List<ValidationError> errors = new ArrayList<>();

		if (name == null || name.isEmpty()) {
			errors.add(new ValidationError("name", "Name must not empty."));
		}
		if(errors.size() > 0)
			return errors;

		return null;
	}

	public static Page<CategoryPromo> page(int page, int pageSize, String sortBy, String order, String filter) {
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
						.eq("is_deleted", false)
						.findRowCount();
	}

	public static List<CategoryPromo> getHomePage() {
		return CategoryPromo.find.where()
				.eq("is_deleted", false)
				.setMaxRows(10).findList();
	}

	public String getChangeLogData(CategoryPromo data){
		HashMap<String, String> map = new HashMap<>();
		map.put("name",(data.name == null)? "":data.name);
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
		CategoryPromo oldCategory = CategoryPromo.find.byId(id);
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
			status = CategoryPromo.ACTIVE;
		else if(newStatus.equals("inactive"))
			status = CategoryPromo.INACTIVE;

		super.update();

		ChangeLog changeLog;
		changeLog = new ChangeLog(LOG_TYPE, this.userCms.id, LOG_TABLE_NAME, this.id, "EDIT", oldCategoryData, getChangeLogData(this));
		changeLog.save();

	}
}
