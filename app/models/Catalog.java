package models;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import com.avaje.ebean.Page;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hokeba.util.Constant;

import play.data.validation.ValidationError;
import play.db.ebean.Model.Finder;

@Entity
public class Catalog extends BaseModel {
	public static final int START_LEVEL = 1;
	public static final boolean ACTIVE = true;
	public static final boolean INACTIVE = false;
	
	public String code;
	
	@JsonProperty("root_category_code")
	public String rootCatalogCode; // root code catalog
	
	@JsonProperty("is_active")
	public boolean isActive;

	public String name;
	
	public String title;
	
	public String description;
	
	public String keyword;
	
	public String alias;
	
	public Integer level;
	
	public String slug;
	
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

	@javax.persistence.Transient
	public String save;

	@javax.persistence.Transient
	public Long parent;

	@javax.persistence.Transient
	public String parentName;

	@javax.persistence.Transient
	public String imageLink;

	@javax.persistence.Transient
	public String imageLinkResponsive;

	@JsonIgnore
	@JoinColumn(name = "user_id")
	@ManyToOne
	public UserCms userCms;

	@Transient
	@JsonProperty("has_child")
	public boolean getHasChild() {
		if (subCatalog.size() == 0)
			return false;
		return true;
	}

	@Transient
	@JsonProperty("parent_catalog_id")
	public Long getParentCatalogId() {
		if (parentCatalog != null)
			return parentCatalog.id;
		return new Long(0);
	}

	@JsonIgnore
	@Column(name = "view_count")
	public int viewCount;

	@Transient
	@JsonProperty("sub_catalog")
	public List<Catalog> childCatalog = new ArrayList<>();

	@ManyToOne(cascade = { CascadeType.ALL })
	@JoinColumn(name = "parent_id")
	@JsonIgnore
	public Catalog parentCatalog;

	@OneToMany(mappedBy = "parentCatalog")
	@Column(insertable = false, updatable = false)
	@JsonIgnore
	public Set<Catalog> subCatalog = new HashSet<Catalog>();

	@JsonIgnore
	public String imageSize;

	@JsonProperty("image_size")
	public int[] getImageSize() throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper om = new ObjectMapper();
		return (imageSize == null) ? null : om.readValue(imageSize, int[].class);
	}

	@ManyToMany // (mappedBy = "parentCatalog")
	// @Column(insertable = false, updatable = false)
	@JsonIgnore
	public List<BaseAttribute> listBaseAttribute;

	@Transient
	public List<String> base_attribute_list;

	@javax.persistence.Transient
	public int imageUrlX;
	
	@javax.persistence.Transient
	public int imageUrlY;
	
	@javax.persistence.Transient
	public int imageUrlW;
	
	@javax.persistence.Transient
	public int imageUrlH;

	@javax.persistence.Transient
	public int imageUrlResponsiveX;
	
	@javax.persistence.Transient
	public int imageUrlResponsiveY;
	
	@javax.persistence.Transient
	public int imageUrlResponsiveW;
	
	@javax.persistence.Transient
	public int imageUrlResponsiveH;

	@Transient
	@JsonProperty("icon")
	public String getIcon() {
		return getImageLinkResponsive();
	}

	@Transient
	@JsonProperty("top_brands")
	public List<Brand> topBrands = new ArrayList<>();

	public String getImageUrl() {
		return getImageLink();
	}

	public String getImageUrlResponsive() {
		return getImageLinkResponsive();
	}

	@Transient
	public String getIsActive() {
		String statusName = "";
		if (isActive)
			statusName = "Active";
		else
			statusName = "Inactive";
		return statusName;
	}

	public static Finder<Long, Catalog> find = new Finder<Long, Catalog>(Long.class, Catalog.class);

	public static String validation(Catalog model) {
		Catalog uniqueCheck = Catalog.find.where().eq("slug", model.slug).findUnique();
		Catalog uniqueCheck2 = Catalog.find.where().eq("code", model.code).findUnique();
		if (model.name.equals("")) {
			return "Name must not empty.";
		}
		if (uniqueCheck != null && model.id == null) {
			return "Catalog with similar name already exist";
		}
		if (model.code.equals("")) {
			return "Code must not empty.";
		}
		if (uniqueCheck2 != null && !uniqueCheck2.id.equals(model.id)) {
			return "category with similar code already exist";
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
		if (errors.size() > 0)
			return errors;
		return null;
	}

	public static Page<Catalog> page(int page, int pageSize, String sortBy, String order, String filter) {
		return find.where().ilike("name", "%" + filter + "%").eq("is_deleted", false).orderBy(sortBy + " " +order)
				.findPagingList(pageSize).setFetchAhead(false).getPage(page);
	}

	public static int findRowCount() {
		return find.where().eq("is_deleted", false).findRowCount();
	}

	public static Integer RowCount() {
		return find.where().eq("is_deleted", false).findRowCount();
	}

	public String getImageLink() {
		return imageUrl == null || imageUrl.isEmpty() ? "" : Constant.getInstance().getImageUrl() + imageUrl;
	}

	public String getImageLinkResponsive() {
		return imageUrlResponsive == null || imageUrlResponsive.isEmpty() ? ""
				: Constant.getInstance().getImageUrl() + imageUrlResponsive;
	}

	public void updateStatus(String newStatus) {
		if (newStatus.equals("1"))
			isActive = Category.ACTIVE;
		else if (newStatus.equals("2"))
			isActive = Category.INACTIVE;
		super.update();
	}

}
