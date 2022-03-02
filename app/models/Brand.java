package models;

import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Page;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hokeba.util.CommonFunction;
import com.hokeba.util.Constant;
import play.data.validation.ValidationError;

import javax.persistence.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//odoo
@Entity
public class Brand extends BaseModel {

	private static final long serialVersionUID = 1L;
	public static final boolean ACTIVE = true;
	public static final boolean INACTIVE = false;

	@Column(unique = true)
	public String name;
	public String title;
	public String description;
	public String keyword;
	public String slug;
	public boolean status;
	@Column(name = "odoo_id")
	public String odooId;

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
	public int sequence;

	@javax.persistence.Transient
	public String save;

	@javax.persistence.Transient
	public String imageLink;

	@javax.persistence.Transient
	public String getStatus() {
		String statusName = "";
		if(status)
			statusName = "Active";
		else statusName = "Inactive";

		return statusName;
	}

	@JsonIgnore
	@JoinColumn(name = "user_id")
	@ManyToOne
	public UserCms userCms;

    @JsonIgnore
    @Column(name = "view_count")
    public int viewCount;

	@JsonIgnore
	public String imageSize;
	@JsonProperty("image_size")
	public int[] getImageSize() throws IOException {
		ObjectMapper om = new ObjectMapper();
		return (imageSize==null) ? null : om.readValue(imageSize, int[].class);
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

    public String getImageUrl(){
        return getImageLink();
    }

	public static Finder<Long, Brand> find = new Finder<>(Long.class, Brand.class);

	public static void seed(String name, String url, Long id, Integer odooId){
        Brand model = new Brand();
		UserCms user = UserCms.find.byId(id);
        model.name = model.title = model.description = model.keyword = model.imageName = model.imageKeyword =
                model.imageTitle = model.imageDescription = name;
        model.slug = CommonFunction.slugGenerate(name);
        model.imageUrl = url;
        model.userCms = user;
//        model.odooId = odooId;
		model.status = true;
        model.save();

        Photo.saveRecord("brd",url, "", "", "", url, user.id, "admin", "Brand", model.id);
    }

	public static String validation(Brand model) {
		Brand uniqueCheck = Brand.find.where().eq("slug", model.slug).setMaxRows(1).findUnique();
		if (model.name.equals("")) {
			return "Name must not empty.";
		}
		if (uniqueCheck != null && model.id == null) {
			return "Brand with similar name already exist";
		}
		if ((model.imageUrl != null) && ((model.imageName == null || model.imageName.equals(""))
				|| (model.imageTitle == null || model.imageTitle.equals(""))
				|| (model.imageKeyword == null || model.imageKeyword.equals(""))
				|| (model.imageDescription == null))) {
			return "Please describe all information for brand's logo";
		}
		return null;
	}

	public List<ValidationError> validate() {
		List<ValidationError> errors = new ArrayList<>();

		if (name == null || name.isEmpty()) {
			errors.add(new ValidationError("name", "Name must not empty."));
		}
		if (title == null || title.isEmpty()) {
			errors.add(new ValidationError("title", "Meta Title must not empty."));
		}
		if (description == null || description.isEmpty()) {
			errors.add(new ValidationError("description", "Meta Description must not empty."));
		}
		if (keyword == null || keyword.isEmpty()) {
			errors.add(new ValidationError("keyword", "Meta Keyword must not empty."));
		}
		if (imageName == null || imageName.isEmpty()) {
			errors.add(new ValidationError("imageName", "Image Name must not empty."));
		}
		if (imageTitle == null || imageTitle.isEmpty()) {
			errors.add(new ValidationError("imageTitle", "Meta Title must not empty."));
		}
		if (imageDescription == null || imageDescription.isEmpty()) {
			errors.add(new ValidationError("imageDescription", "Meta Description must not empty."));
		}
		if (imageKeyword == null || imageKeyword.isEmpty()) {
			errors.add(new ValidationError("imageKeyword", "Meta Keyword must not empty."));
		}

		if(errors.size() > 0)
			return errors;

		return null;
	}

	public static Page<Brand> page(int page, int pageSize, String sortBy, String order, String name, Integer filter) {
		ExpressionList<Brand> qry = Brand.find
				.where()
				.ilike("name", "%" + name + "%")
				.eq("is_deleted", false);

		switch (filter){
			case 1: qry.eq("status", true);
				break;
			case 2: qry.eq("status", false);
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

	public String getImageLink(){
		return imageUrl==null || imageUrl.isEmpty() ? "" : Constant.getInstance().getImageUrl() + imageUrl;
	}

	public static List<Brand> getHomePage() {
		return Brand.find.where()
				.eq("is_deleted", false)
				.eq("status", true)
				.order("sequence asc")
				.findList();
//				.setMaxRows(10).findList();
	}
	
	public static List<Brand> fetchAllBrand() {
		return Brand.find.where()
				.eq("is_deleted", false)
				.order("name asc")
				.findList();
	}

	public static List<Brand> getAllData() {
		return Brand.find.where()
				.eq("is_deleted", false)
				.eq("status", true).findList();
	}

	public void updateStatus(String newStatus) {
//		String oldBannerData = getChangeLogData(this);
//
		if(newStatus.equals("active"))
			status = Brand.ACTIVE;
		else if(newStatus.equals("inactive"))
			status = Brand.INACTIVE;
//
		super.update();
//
//		ChangeLog changeLog;
//		changeLog = new ChangeLog(LOG_TYPE, this.userCms.id, LOG_TABLE_NAME, this.id, "EDIT", oldBannerData, getChangeLogData(this));
//		changeLog.save();

	}
}
