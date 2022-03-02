package models;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Page;
import com.avaje.ebean.SqlQuery;
import com.avaje.ebean.SqlRow;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hokeba.util.CommonFunction;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class HighlightBanner extends BaseModel {
    public static final int START_LEVEL = 1;
    public static final boolean ACTIVE = true;
    public static final boolean INACTIVE = false;

    @JsonProperty("is_active")
    public boolean isActive;

	public String name;
    @Column(nullable = false, unique = true)
    public String slug;
    public String title;
    public String description;
    public String keyword;
	public Integer level;
    public Integer sequence;

    @ManyToOne(cascade = { CascadeType.ALL })
    @JoinColumn(name = "parent_id")
    @JsonIgnore
    public HighlightBanner parentBanner;

    @OneToMany(mappedBy = "parentBanner")
    @Column(insertable = false, updatable = false)
    @JsonIgnore
    public Set<HighlightBanner> subBanner = new HashSet<HighlightBanner>();

    @JsonIgnore
    @ManyToMany
    public List<Merchant> merchants;

    @JsonIgnore
    @ManyToMany
    public List<Brand> brands;

    @JsonIgnore
    @ManyToMany
    public List<Category> categories;

    @JsonIgnore
    @ManyToMany
    public List<Product> products;

    @JsonIgnore
    @JoinColumn(name = "user_id")
    @ManyToOne
    public UserCms userCms;

    @Transient
    public String save;

    @Transient
    public Long parent;

    @Transient
    public String parentName;

    @Transient
    public String imageLink;

    @javax.persistence.Transient
    public List<String> merchant_list;

    @javax.persistence.Transient
    public List<String> brand_list;

    @javax.persistence.Transient
    public List<String> category_list;

    @javax.persistence.Transient
    public List<String> subcategory_list;

    @javax.persistence.Transient
    public List<String> product_list;

	@Transient
	@JsonProperty("has_child")
	public boolean getHasChild() {
        return subBanner.size() != 0;
    }

	@Transient
	@JsonProperty("parent_banner_id")
	public Long getParentBannerId() {
		if (parentBanner != null)
			return parentBanner.id;
		return new Long(0);
	}

	@Transient
	@JsonProperty("sub_banner")
	public List<HighlightBanner> childBanner = new ArrayList<>();

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

    @Transient
    @JsonProperty("top_brands")
    public List<Brand> topBrands = new ArrayList<>();

    @Transient
    public String getIsActive() {
        String statusName = "";
        if(isActive)
            statusName = "Active";
        else statusName = "Inactive";

        return statusName;
    }

	public static Finder<Long, HighlightBanner> find = new Finder<Long, HighlightBanner>(Long.class, HighlightBanner.class);


    public static Page<HighlightBanner> page(int page, int pageSize, String sortBy, String order, String filter) {
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

	public static Integer RowCount() {
		return find.where().eq("is_deleted", false).findRowCount();
	}

    public static int getNextSequence(Long parentId) {
        SqlQuery sqlQuery;
        if (parentId == null) {
            sqlQuery = Ebean.createSqlQuery(
                    "select max(sequence) as max from category where is_active = true and parent_id is null");
        } else {
            sqlQuery = Ebean.createSqlQuery(
                    "select max(sequence) as max from category where is_active = true and parent_id = :parentId");
            sqlQuery.setParameter("parentId", parentId);
        }
        SqlRow result = sqlQuery.findUnique();
        int resSequence = (result.getInteger("max") == null ? 0 : result.getInteger("max")) + 1;
        return resSequence;
    }

	public static HighlightBanner seed(String name, Long parent, int sequence, List<Merchant> merchants, List<Category> categories, List<Product> products) {
		HighlightBanner model = new HighlightBanner();
		UserCms user = UserCms.find.byId(1L);
		model.name = model.title = model.description = model.keyword =  name;
        model.slug = CommonFunction.slugGenerate(name);
		model.userCms = user;
        model.isActive = true;
        if (parent == 0L){
            model.parentBanner = null;
            model.level = 1;
            model.sequence = sequence;
        }else{
            HighlightBanner dataParent = HighlightBanner.find.byId(parent);
            model.parentBanner = dataParent;
            model.level = dataParent.level + 1;
            model.sequence = sequence;
        }

        model.categories = categories;
        model.merchants = merchants;
        model.products = products;
        model.save();

        return model;
	}
}
