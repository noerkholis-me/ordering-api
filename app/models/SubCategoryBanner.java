package models;

import com.avaje.ebean.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import play.data.validation.ValidationError;
import play.libs.Json;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Entity
@Table(name = "sub_category_banner")
public class SubCategoryBanner extends BaseModel {

	private static final long serialVersionUID = 1L;
	public static final boolean ACTIVE = true;
	public static final boolean INACTIVE = false;
	private static final String LOG_TYPE = "ADMIN";
	private static final String LOG_TABLE_NAME = "sub_category_banner";

	public boolean status;

	@ManyToOne
	@JsonProperty("category_id")
	public Category category;

	@javax.persistence.Transient
	public Long categoryId;

	@javax.persistence.Transient
	public String categoryName;

	@JsonIgnore
	@JoinColumn(name="user_id")
	@ManyToOne
	public UserCms userCms;

	@javax.persistence.Transient
	@JsonProperty("content")
	public List<SubCategoryBannerDetail> details = new ArrayList<>();

	@javax.persistence.Transient
	@JsonProperty("title")
	public String getTitle(){
		return category.name;
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

	

	public Long getParentCategoryId(Long id){
		SqlQuery sqlQuery = Ebean.createSqlQuery(
                "select parent_id from category where id in(select parent_id from category where id="+id+")");
        SqlRow result = sqlQuery.findUnique();
        //String res = "";
        /*try {
            res = result.getString("content");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            res = "";
        }*/
		return result.getLong("parent_id");
	}

	public static Finder<Long, SubCategoryBanner> find = new Finder<>(Long.class, SubCategoryBanner.class);

	public static String validation(SubCategoryBanner model) {
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



	public static Page<SubCategoryBanner> page(int page, int pageSize, String sortBy, String order, String name, Integer filter) {
		ExpressionList<SubCategoryBanner> qry = SubCategoryBanner.find
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

	public static List<SubCategoryBanner> getHomePage() {
		return SubCategoryBanner.find.where()
				.eq("is_deleted", false)
				.setMaxRows(10).findList();
	}

	public String getChangeLogData(SubCategoryBanner data){
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
		SubCategoryBanner oldCategory = SubCategoryBanner.find.byId(id);
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
			status = SubCategoryBanner.ACTIVE;
		else if(newStatus.equals("inactive"))
			status = SubCategoryBanner.INACTIVE;

		super.update();

		ChangeLog changeLog;
		changeLog = new ChangeLog(LOG_TYPE, this.userCms.id, LOG_TABLE_NAME, this.id, "EDIT", oldCategoryData, getChangeLogData(this));
		changeLog.save();

	}
}
