package models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import com.avaje.ebean.Page;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hokeba.util.Constant;

import play.data.validation.ValidationError;
import play.db.ebean.Model.Finder;
import play.libs.Json;

@Entity
public class LizPedia extends BaseModel{
	private static final long serialVersionUID = 1L;
    private static final String LOG_TYPE = "ADMIN";
    private static final String LOG_TABLE_NAME = "lizpedia";

    @Column(columnDefinition = "TEXT")
    public String content;
    
    public String title;
    
    public String description;
    
    public String keyword;
    
    public String slug;
    
    public String name;

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
    
    @javax.persistence.Transient
    public String save;

    @JsonIgnore
    @JoinColumn(name = "user_id")
    @ManyToOne
    public UserCms userCms;

    @Transient
    @JsonProperty("user_creator")
    public String getUserCreator() {
        return userCms.email;
    }
    
    @JsonGetter("meta_title")
    public String getMetaTitle() {
    	return this.title;
    }
    
    @JsonGetter("meta_keyword")
    public String getMetaKeyword() {
    	return this.keyword;
    }
    
    @JsonGetter("meta_description")
    public String getMetaDescription() {
    	return this.description;
    }
    
    @JsonGetter("image_url")
    public String getImageUrl(){
        return getImageLink();
    }
    
    public String getImageLink(){
		return imageUrl==null || imageUrl.isEmpty() ? "" : Constant.getInstance().getImageUrl() + imageUrl;
	}

    public static Finder<Long, LizPedia> find = new Finder<>(Long.class, LizPedia.class);

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
        if (content == null || content.isEmpty()) {
            errors.add(new ValidationError("content", "Content must not empty."));
        }
        if(errors.size() > 0)
            return errors;
        return null;
    }

    public static Page<LizPedia> page(int page, int pageSize, String sortBy, String order, String filter) {
        return
                find.where()
                        .ilike("name", "%" + filter + "%")
                        .eq("is_deleted", false)
                        .orderBy(sortBy + " " + order)
                        .findPagingList(pageSize)
                        .setFetchAhead(false)
                        .getPage(page);
    }

    public static Integer RowCount() {
        return find.where().eq("is_deleted", false).findRowCount();
    }

    public String getChangeLogData(LizPedia data){
        HashMap<String, String> map = new HashMap<>();
        map.put("content",(data.content == null)? "":data.content);
        map.put("title",(data.title == null)? "":data.title);
        map.put("description",(data.description == null)? "":data.description);
        map.put("keyword",(data.keyword == null)? "":data.keyword);
        map.put("slug",(data.slug == null)? "":data.slug);
        map.put("name",(data.name == null)? "":data.name);
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
    	LizPedia oldLizPedia = LizPedia.find.byId(id);
        super.update();

        ChangeLog changeLog;
        if(isDeleted == true){
            changeLog = new ChangeLog(LOG_TYPE, this.userCms.id, LOG_TABLE_NAME, this.id, "DELETE", getChangeLogData(oldLizPedia), null);
        }else{
            changeLog = new ChangeLog(LOG_TYPE, this.userCms.id, LOG_TABLE_NAME, this.id, "EDIT", getChangeLogData(oldLizPedia), getChangeLogData(this));
        }
        changeLog.save();

    }

}
