package models;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.avaje.ebean.Page;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hokeba.util.CommonFunction;
import com.hokeba.util.Constant;

import play.data.validation.ValidationError;

@Entity
public class Catalog2 extends BaseModel {
	private static final long serialVersionUID = 1L;
	public static Finder<Long, Catalog2> find = new Finder<Long, Catalog2>(Long.class, Catalog2.class);
	
	public static final int START_LEVEL = 1;
	public static final boolean ACTIVE = true;
	public static final boolean INACTIVE = false;

	public String name;
	public String slug;
	@JsonProperty("link_url")
	public String linkUrl;
	
	@JsonProperty("meta_title")
	public String metaTitle;
	@JsonProperty("meta_description")
	public String metaDescription;
	@JsonProperty("meta_keyword")
	public String metaKeyword;
	
	@JsonProperty("is_active")
	public boolean isActive;
	@Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING,  pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone="Asia/Jakarta")
    @JsonProperty("active_from")
    public Date activeFrom;
	@Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING,  pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone="Asia/Jakarta")
    @JsonProperty("active_to")
    public Date activeTo;
    
	public int sequence;
    @JsonProperty("sequence_mobile")
    public int sequenceMobile;
	
	@JsonIgnore
	@Column(name = "view_count")
	public int viewCount;
	
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
	@JsonProperty("image_url_responsive")
	public String imageUrlResponsive;
	@JsonIgnore
	public String imageSize;
	
	//TODO relation attribute
	@JsonIgnore
	@OneToMany(mappedBy = "catalog")
	@Column(insertable = false, updatable = false)
	public List<CatalogItem> catalogList = new ArrayList<CatalogItem>();
	
	@JsonIgnore
    @ManyToOne
    @JoinColumn(name="lizpedia_id")
    public LizPedia lizpedia;
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "user_id")
	public UserCms userCms;

	
	//TODO transient data for form
	@Transient
	public String save;

	@Transient
	public Long lizpediaId;
	
	@Transient
	public String imageLink;

	@Transient
	public String imageLinkResponsive;
	
	@Transient
	public int imageUrlX;
	
	@Transient
	public int imageUrlY;
	
	@Transient
	public int imageUrlW;
	
	@Transient
	public int imageUrlH;

	@Transient
	public int imageUrlResponsiveX;
	
	@Transient
	public int imageUrlResponsiveY;
	
	@Transient
	public int imageUrlResponsiveW;
	
	@Transient
	public int imageUrlResponsiveH;

	//TODO getter
	@JsonGetter("image_size")
	public int[] getImageSize() throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper om = new ObjectMapper();
		return (imageSize == null) ? null : om.readValue(imageSize, int[].class);
	}

	@JsonGetter("image_url")
	public String getImageUrl() {
		return imageUrl == null || imageUrl.isEmpty() ? "" : Constant.getInstance().getImageUrl() + imageUrl;
	}

	@JsonGetter("image_url_responsive")
	public String getImageUrlResponsive() {
		return imageUrlResponsive == null || imageUrlResponsive.isEmpty() ? ""
				: Constant.getInstance().getImageUrl() + imageUrlResponsive;
	}

	@JsonGetter("is_active")
	public String getIsActive() {
		String statusName = "";
		if (isActive)
			statusName = "Active";
		else
			statusName = "Inactive";
		return statusName;
	}
	
	@JsonGetter("date_from")
    public String getDateFrom() {
        String date = "";
        if(activeFrom != null) date = CommonFunction.getDateFrom(activeFrom, "MM/dd/yyyy HH:mm:ss");

        return date;
    }

	@JsonGetter("date_to")
    public String getDateTo() {
        String date = "";
        if(activeTo != null) date = CommonFunction.getDateFrom(activeTo, "MM/dd/yyyy HH:mm:ss");

        return date;
    }

	//TODO function
	public static Page<Catalog2> page(int page, int pageSize, String sortBy, String order, String filter) {
		return find.where().ilike("name", "%" + filter + "%").eq("t0.is_deleted", false).orderBy(sortBy + " " +order)
				.findPagingList(pageSize).setFetchAhead(false).getPage(page);
	}

	public static int findRowCount() {
		return find.where().eq("t0.is_deleted", false).findRowCount();
	}

	public void updateStatus(String newStatus) {
		if (newStatus.equals("1"))
			isActive = Catalog2.ACTIVE;
		else if (newStatus.equals("2"))
			isActive = Catalog2.INACTIVE;
		super.update();
	}


	public static String validation(Catalog2 model) {
		Catalog2 uniqueCheck = Catalog2.find.where().eq("slug", model.slug).findUnique();
		if (model.name.equals("")) {
			return "Name must not empty.";
		}
		if (uniqueCheck != null && model.id == null) {
			return "Catalog with similar name already exist";
		}
		return null;
	}

	public List<ValidationError> validate() {
		List<ValidationError> errors = new ArrayList<>();
		if (name == null || name.isEmpty()) {
			errors.add(new ValidationError("name", "Name must not empty."));
		}
		if (metaTitle == null || metaTitle.isEmpty()) {
			errors.add(new ValidationError("title", "Meta Title must not empty."));
		}
		if (metaDescription == null || metaDescription.isEmpty()) {
			errors.add(new ValidationError("description", "Meta Description must not empty."));
		}
		if (metaKeyword == null || metaKeyword.isEmpty()) {
			errors.add(new ValidationError("keyword", "Meta Keyword must not empty."));
		}
		if (errors.size() > 0) {
			return errors;
		} else {
			return null;
		}
	}

}
