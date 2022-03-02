package models;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Page;
import com.avaje.ebean.SqlQuery;
import com.avaje.ebean.SqlRow;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hokeba.util.CommonFunction;
import com.hokeba.util.Constant;
import play.data.validation.ValidationError;

import javax.persistence.*;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.*;

@Entity
public class Category extends BaseModel {
	private static final long serialVersionUID = 1L;
	public static final int START_LEVEL = 1;
    public static final boolean ACTIVE = true;
    public static final boolean INACTIVE = false;
	// This is sales category, not master category

	// @JsonProperty("parent_id")
	// public Long parentId; // parent category id

//	@Column(nullable = false, unique = true)
	public String code;
	@JsonProperty("root_category_code")
	public String rootCategoryCode; // root code category

    @JsonProperty("is_active")
    public boolean isActive;

	public String name;
    public String title;
    public String description;
    public String keyword;
	public String alias;
	public Integer level;
	public Integer sequence;
	public String slug;
    @JsonProperty("share_profit")
    public Double shareProfit;
    //odoo
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
    @JsonProperty("image_url_responsive")
    public String imageUrlResponsive;
    @JsonProperty("image_banner_url")
    public String imageBannerUrl;
    @JsonProperty("image_splash_url")
    public String imageSplashUrl;

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
        return subCategory.size() != 0;
    }

	@Transient
	@JsonProperty("parent_category_id")
	public Long getParentCategoryId() {
		if (parentCategory != null)
			return parentCategory.id;
		return new Long(0);
	}

	@JsonIgnore
	@Column(name = "view_count")
	public int viewCount;

	@Transient
	@JsonProperty("sub_category")
	public List<Category> childCategory = new ArrayList<>();

	@ManyToOne(cascade = { CascadeType.ALL })
	@JoinColumn(name = "parent_id")
	@JsonIgnore
	public Category parentCategory;

	@OneToMany(mappedBy = "parentCategory")
	@Column(insertable = false, updatable = false)
	@JsonIgnore
	public Set<Category> subCategory = new HashSet<Category>();

    @JsonIgnore
    public String imageSize;
    @JsonProperty("image_size")
    public int[] getImageSize() throws IOException {
        ObjectMapper om = new ObjectMapper();
        return (imageSize==null) ? null : om.readValue(imageSize, int[].class);
    }

    @ManyToMany//(mappedBy = "parentCategory")
    //@Column(insertable = false, updatable = false)
    @JsonIgnore
    public List<BaseAttribute> listBaseAttribute;

    @Transient
    public List<String> base_attribute_list;

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
    @JsonProperty("icon")
    public String getIcon(){
        return getImageLinkResponsive();
    }
    @Transient
    @JsonProperty("top_brands")
    public List<Brand> topBrands = new ArrayList<>();

    public String getImageUrl(){
        return getImageLink();
    }

    public String getImageUrlResponsive(){
        return getImageLinkResponsive();
    }

    @Transient
    public String getIsActive() {
        String statusName = "";
        if(isActive)
            statusName = "Active";
        else statusName = "Inactive";

        return statusName;
    }

	// @OneToMany(mappedBy = "product_category", cascade = CascadeType.ALL)
	// @JsonManagedReference
	// @JsonIgnore
	// public List<Product> products = new ArrayList<Product>();

	public static Finder<Long, Category> find = new Finder<Long, Category>(Long.class, Category.class);

	public static String validation(Category model) {
		Category uniqueCheck = Category.find.where().eq("slug", model.slug).findUnique();
		Category uniqueCheck2 = Category.find.where().eq("code", model.code).findUnique();
		if (model.name.equals("")) {
			return "Name must not empty.";
		}
		if (uniqueCheck != null && model.id==null)
		{
			return "Category with similar name already exist";
		}
		if (model.code.equals("")) {
			return "Code must not empty.";
		}
		if (uniqueCheck2!=null && !uniqueCheck2.id.equals(model.id)){
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


        if(errors.size() > 0)
            return errors;

        return null;
    }


    public static Page<Category> page(int page, int pageSize, String sortBy, String order, String filter) {
    	
		return
				find.where()
						.ilike("name", StringUtils.join("%", filter, "%"))
						.eq("isDeleted", false)
						.eq("isActive", true)
						.eq("level", 3)
						.ne("parentCategory.id", null)
						.ne("parentCategory.parentCategory.id", null)
						.eq("parentCategory.parentCategory.isDeleted", false)
						.eq("parentCategory.parentCategory.isActive", true)
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

    public String getImageLink(){
        return imageUrl==null || imageUrl.isEmpty() ? "" : Constant.getInstance().getImageUrl() + imageUrl;
    }

    public String getImageLinkResponsive(){
    	return imageUrlResponsive==null || imageUrlResponsive.isEmpty() ? "" : Constant.getInstance().getImageUrl() + imageUrlResponsive;
    }
    
    public String getImageBannerLink(){
    	return imageBannerUrl==null || imageBannerUrl.isEmpty() ? "" : Constant.getInstance().getImageUrl() + imageBannerUrl;
    }
    
    public String getImageSplashLink(){
        return imageSplashUrl==null || imageSplashUrl.isEmpty() ? "" : Constant.getInstance().getImageUrl() + imageSplashUrl;
    }

	public static Category seed(String name, String url, String icon, Long id, Long parent, Integer odooId, List<BaseAttribute> ba) {
        return seed(name, url, icon, id, parent, odooId, ba, 0D);
	}

	public static Category seed(String name, String url, String icon, Long id, Long parent, Integer odooId, List<BaseAttribute> ba, Double share) {
		Category model = new Category();
		UserCms user = UserCms.find.byId(id);
		model.name = model.title = model.description = model.keyword = model.imageName = model.imageKeyword =
				model.imageTitle = model.imageDescription = name;
        model.code = model.slug = CommonFunction.slugGenerate(name);
		model.imageUrl = url;
		model.imageUrlResponsive = icon;
		model.userCms = user;
//		model.odooId = odooId;
        model.isActive = true;
        if (parent == 0L){
            model.parentCategory = null;
            model.rootCategoryCode = model.code;
            model.sequence =Category.getNextSequence(null);
            model.level = 1;
        }else{
            Category dataParent = Category.find.byId(parent);
            model.parentCategory = dataParent;
            model.rootCategoryCode = dataParent.rootCategoryCode;
            model.sequence = Category.getNextSequence(dataParent.id);
            model.level = dataParent.level + 1;
        }
        if (ba != null){
            model.listBaseAttribute = ba;
        }
        model.shareProfit = share;
        model.save();
        if (!url.isEmpty()){
            Photo.saveRecord("cat", url, "", "", "", url, user.id, "admin", "Category", model.id);
        }
        if (!icon.isEmpty()){
            Photo.saveRecord("cat-icon", icon, "", "", "", icon, user.id, "admin", "Category", model.id);
        }

        return model;
	}

	public static Category seed(String name, String url, String icon, Long id, Long parent, Integer odooId) {
        return seed(name, url, icon, id, parent, odooId, null);
	}

    public static List<Category> recGetShownChildCategory(Long id) {
        List<Category> result = Category.getShownChildCategory(id);
        for (Category category : result) {
            category.childCategory = recGetShownChildCategory(category.id);
        }
        return result;
    }

    public static List<Category> getShownChildCategory(Long id) {
        return Category.find.where().eq("parent_id", id).eq("is_active", true).eq("is_deleted", false).order("sequence asc").findList();
    }

    public static List<Category> recGetAllChildCategory(Long id) {
        List<Category> result = Category.getAllChildCategory(id);
        for (Category category : result) {
            category.childCategory = recGetAllChildCategory(category.id);
            if (category.level == 2){
                category.topBrands = Brand.find.where().eq("is_deleted", false).findPagingList(5).getPage(0).getList();
            }
        }
        return result;
    }

    public static List<Category> getAllChildCategory(Long id) {
        return Category.find.where().eq("parent_id", id).eq("is_deleted", false).eq("is_active", true).order("sequence asc").findList();
    }

    public static Map<String, String> getParent(String slug){
    	SqlQuery sqlQuery;
        Map<String, String> retval = new LinkedHashMap<>();
        Category category = Category.find.where().eq("is_deleted", false).eq("is_active", true).eq("slug", slug).findUnique();
        if(category.level==3) {
        	 sqlQuery = Ebean.createSqlQuery(
                    "select a.id l1_id,a.name l1_name,a.slug l1_slug,"
					+ "a.title as l1_meta_title, a.keyword as l1_meta_key, a.description as l1_meta_desc,"
                    + "b.id l2_id,b.name l2_name,b.slug l2_slug,"
					+ "b.title as l2_meta_title, b.keyword as l2_meta_key, b.description as l2_meta_desc,"
                    + "c.id l3_id,c.name l3_name,c.slug l3_slug, "
					+ "c.title as l3_meta_title, c.keyword as l3_meta_key, c.description as l3_meta_desc,"
                    + "c.image_banner_url as l3_banner, c.image_splash_url as l3_splash "
                    + "from "+
                    "  (select id,parent_id,name,slug,title,keyword,description from category where level=1 AND is_deleted = false)a, "+
                    "  (select id,parent_id,name,slug,title,keyword,description from category where level=2 AND is_deleted = false)b, "+
                    "  (select id,parent_id,name,slug,title,keyword,description,image_banner_url,image_splash_url from category where level=3 AND is_deleted = false)c "+
                    "where a.id=b.parent_id "+
                    "and b.id=c.parent_id "+
                    "and c.slug='"+slug+"'");
        } else if (category.level==2) {
        	 sqlQuery = Ebean.createSqlQuery(
        			"select a.id l1_id,a.name l1_name,a.slug l1_slug,"
					+ "a.title as l1_meta_title, a.keyword as l1_meta_key, a.description as l1_meta_desc,"
                    + "b.id l2_id,b.name l2_name,b.slug l2_slug,"
					+ "b.title as l2_meta_title, b.keyword as l2_meta_key, b.description as l2_meta_desc,"
                    + "'' as l3_id,'' as l3_name,'' as l3_slug, "
					+ "'' as l3_meta_title,'' as l3_meta_key,'' as l3_meta_desc,"
                    + "'' as l3_banner,'' as l3_splash "
                    + "from "+
		            "  (select id,parent_id,name,slug,title,keyword,description from category where level=1 AND is_deleted = false)a, "+
                    "  (select id,parent_id,name,slug,title,keyword,description from category where level=2 AND is_deleted = false)b "+
                    "where a.id=b.parent_id "+
                    "and b.slug='"+slug+"'");
        } else {
        	 sqlQuery = Ebean.createSqlQuery(
        			"select a.id l1_id,a.name l1_name,a.slug l1_slug,"
					+ "a.title as l1_meta_title, a.keyword as l1_meta_key, a.description as l1_meta_desc,"
                    + "'' as l2_id,'' as l2_name,'' as l2_slug,"
					+ "'' as l2_meta_title,'' as l2_meta_key,'' as l2_meta_desc,"
                    + "'' as l3_id,'' as l3_name,'' as l3_slug, "
					+ "'' as l3_meta_title,'' as l3_meta_key,'' as l3_meta_desc,"
                    + "'' as l3_banner,'' as l3_splash "
                    + "from "+
                    "  (select id,parent_id,name,slug,title,keyword,description from category where level=1 AND is_deleted = false)a "+
                    "where a.slug='"+slug+"'");
        }
		SqlRow result = sqlQuery.findUnique();		
		String imageBanner =  result.getString("l3_banner");
		String imageSplash =  result.getString("l3_splash");
		
        retval.put("category_1_id",result.getString("l1_id").toString());
        retval.put("category_1_name",result.getString("l1_name"));
        retval.put("category_1_slug",result.getString("l1_slug"));
        retval.put("category_1_meta_title",result.getString("l1_meta_title"));
        retval.put("category_1_meta_keyword",result.getString("l1_meta_key"));
        retval.put("category_1_meta_description",result.getString("l1_meta_desc"));
        
        retval.put("category_2_id",result.getString("l2_id").toString());
        retval.put("category_2_name",result.getString("l2_name"));
        retval.put("category_2_slug",result.getString("l2_slug"));
        retval.put("category_2_meta_title",result.getString("l2_meta_title"));
        retval.put("category_2_meta_keyword",result.getString("l2_meta_key"));
        retval.put("category_2_meta_description",result.getString("l2_meta_desc"));
        
        retval.put("category_3_id",result.getString("l3_id").toString());
        retval.put("category_3_name",result.getString("l3_name"));
        retval.put("category_3_slug",result.getString("l3_slug"));
        retval.put("category_3_meta_title",result.getString("l3_meta_title"));
        retval.put("category_3_meta_keyword",result.getString("l3_meta_key"));
        retval.put("category_3_meta_description",result.getString("l3_meta_desc"));
        retval.put("category_3_banner_url",imageBanner==null || imageBanner.isEmpty() ? "" : (Constant.getInstance().getImageUrl() + imageBanner));
        retval.put("category_3_splash_url",imageSplash==null || imageSplash.isEmpty() ? "" : (Constant.getInstance().getImageUrl() + imageSplash));

        return retval;
    }

    public Double getShareProfit() {
        return shareProfit == null ? 0D : shareProfit;
    }
    
    @OneToMany(mappedBy = "category")
    public List<CategoryLoyalty> categoryLoyalty;
    
    @JsonGetter("loyalty_usage_type")
	public int getLoyaltyUsageType(){
    	return categoryLoyalty == null || categoryLoyalty.size() == 0 ? 1 : categoryLoyalty.get(0).loyaltyUsageType;
    }
	
    @JsonGetter("max_loyalty_usage_value")
	public float getMaxLoyaltyUsageValue() {
    	return categoryLoyalty == null || categoryLoyalty.size() == 0 ? 0 : categoryLoyalty.get(0).maxLoyaltyUsageValue;
    }
	
    @JsonGetter("loyalty_usage_value")
	public float getLoyaltyUsageValue() {
    	return categoryLoyalty == null || categoryLoyalty.size() == 0 ? 0 : categoryLoyalty.get(0).loyaltyUsageValue;
    }
	
    @JsonGetter("cashback_type")
	public int getCashbackType() {
    	return categoryLoyalty == null || categoryLoyalty.size() == 0 ? 1 : categoryLoyalty.get(0).cashbackType;
    }
	
    @JsonGetter("cashback_value")
	public float getCashbackValue() {
    	return categoryLoyalty == null || categoryLoyalty.size() == 0 ? 0 : categoryLoyalty.get(0).cashbackValue;
    }
	
    @JsonGetter("max_cashback_value")
	public float getMaxCashbackValue() {
    	return categoryLoyalty == null || categoryLoyalty.size() == 0 ? 0 : categoryLoyalty.get(0).maxCashbackValue;
    }
    
    @JsonGetter("cashback_type_referral")
	public int getCashbackTypeReferral() {
    	return categoryLoyalty == null || categoryLoyalty.size() == 0 ? 1 : categoryLoyalty.get(0).cashbackTypeReferral;
    }
	
    @JsonGetter("cashback_value_referral")
	public float getCashbackValueReferral() {
    	return categoryLoyalty == null || categoryLoyalty.size() == 0 ? 0 : categoryLoyalty.get(0).cashbackValueReferral;
    }
	
    @JsonGetter("max_cashback_value_referral")
	public float getMaxCashbackValueReferral() {
    	return categoryLoyalty == null || categoryLoyalty.size() == 0 ? 0 : categoryLoyalty.get(0).maxCashbackValueReferral;
    }
}
