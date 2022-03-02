package models;

import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Page;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hokeba.util.Constant;

import play.data.validation.ValidationError;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "additional_category_master")
public class AdditionalCategoryMaster extends BaseModel {

	private static final long serialVersionUID = 1L;
	public static final boolean ACTIVE = true;
	public static final boolean INACTIVE = false;
	private static final String LOG_TYPE = "ADMIN";
	private static final String LOG_TABLE_NAME = "additional_category_master";

	public boolean status;
	public String name;
	public String slug;
	public int sequence;
	public String color;
	
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
	

//	@javax.persistence.Transient
//	public Long categoryId;
//
//	@javax.persistence.Transient
//	public String categoryName;

	@JsonIgnore
	@JoinColumn(name="user_id")
	@ManyToOne
	public UserCms userCms;

//	@javax.persistence.Transient
//	@JsonProperty("title")
//	public String getTitle(){
//		return category.name;
//	}


	@java.beans.Transient
	public String getStatus() {
		String statusName = "";
		if(status)
			statusName = "Show in frontend menu";
		else statusName = "Hide in frontend menu";

		return statusName;
	}
	
	public String getImageUrl(){
        return getImageLink();
    }
    
    public String getImageLink(){
		return imageUrl==null || imageUrl.isEmpty() ? "" : Constant.getInstance().getImageUrl() + imageUrl;
	}

	@Transient
	public String save;

	public static Finder<Long, AdditionalCategoryMaster> find = new Finder<>(Long.class, AdditionalCategoryMaster.class);

	public static String validation(AdditionalCategoryMaster model) {
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


	public String getSlug(){
		return "additional-category/"+slug;
	}

	public static Page<AdditionalCategoryMaster> page(int page, int pageSize, String sortBy, String order, String name, Integer filter) {
		ExpressionList<AdditionalCategoryMaster> qry = AdditionalCategoryMaster.find
				.where()
				.ilike("name", "%" + name + "%")
				.eq("is_deleted", false);

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

	public void updateStatus(String newStatus) {

		if(newStatus.equals("active"))
			status = AdditionalCategoryMaster.ACTIVE;
		else if(newStatus.equals("inactive"))
			status = AdditionalCategoryMaster.INACTIVE;

		super.update();



	}
	
	public static List<AdditionalCategoryMaster> getHomePage() {
		return AdditionalCategoryMaster.find.where()
                .eq("is_deleted", false).eq("status", true).order("sequence asc").findList();
	}
}
