package models;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Page;
import com.avaje.ebean.SqlQuery;
import com.avaje.ebean.SqlRow;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hokeba.util.Constant;

import play.data.validation.ValidationError;
import play.libs.Json;

import javax.persistence.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by hendriksaragih on 2/4/17.
 */
@Entity
@Table(name = "loyalty")
public class Loyalty extends BaseModel {
    private static final long serialVersionUID = 1L;
    private static final String LOG_TYPE = "ADMIN";
    private static final String LOG_TABLE_NAME = "loyalty";

    @Column(columnDefinition = "TEXT")
    public String content;
    
    public String name;
    
    public String slug;
    
    @JsonProperty("loyalty_image_name")
    @Column(name = "loyalty_image_name", columnDefinition = "TEXT")
    public String loyaltyImageName;
    
    @JsonProperty("loyalty_image_keyword")
    public String loyaltyImageKeyword;
    
    @JsonProperty("loyalty_image_title")
    public String loyaltyImageTitle;
    
    @JsonProperty("loyalty_image_description")
    @Column(name = "loyalty_image_description", columnDefinition = "TEXT")
    public String loyaltyImageDescription;
    
    @JsonProperty("image_url")
    public String imageUrl;
    
    @JsonProperty("image_url_responsive")
    public String imageUrlResponsive;
    
    @JsonProperty("image_url_mobile")
    public String imageUrlMobile;
    @JsonIgnore
    
    @javax.persistence.Transient
    public String save;
    
    @javax.persistence.Transient
    public String imageLink;

    @javax.persistence.Transient
    public String imageResponsiveLink;

    @javax.persistence.Transient
    public String imageMobileLink;
    
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

    @javax.persistence.Transient
    public int imageUrlMobileX;
    
    @javax.persistence.Transient
    public int imageUrlMobileY;
    
    @javax.persistence.Transient
    public int imageUrlMobileW;
    
    @javax.persistence.Transient
    public int imageUrlMobileH;

    @javax.persistence.Transient
    @JsonProperty("meta_name")
    public String getMetaName(){
        return name;
    }
    
    @javax.persistence.Transient
    @JsonProperty("meta_content")
    public String getMetaContent(){
        return content;
    }

    public String getImageUrl(){
        return getImageLink();
    }

    public String getImageUrlResponsive(){
        return getImageResponsiveLink();
    }
    
    public String getImage() {
        return Images.getImage(imageUrl);
    }
    
    public String getImageLink(){
        return imageUrl==null || imageUrl.isEmpty() ? "" : Constant.getInstance().getImageUrl() + imageUrl;
    }

    public String getImageResponsiveLink(){
        return imageUrlResponsive==null || imageUrlResponsive.isEmpty() ? "" : Constant.getInstance().getImageUrl() + imageUrlResponsive;
    }

    public String getImageMobileLink(){
        return imageUrlMobile==null || imageUrlMobile.isEmpty() ? "" : Constant.getInstance().getImageUrl() + imageUrlMobile;
    }
    
    @JsonIgnore
    @JoinColumn(name = "user_id")
    @ManyToOne
    public UserCms userCms;

    @Transient
    @JsonProperty("user_creator")
    public String getUserCreator() {
        return userCms.email;
    }

    public static Finder<Long, Loyalty> find = new Finder<>(Long.class, Loyalty.class);

    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<>();

        if (name == null || name.isEmpty()) {
            errors.add(new ValidationError("name", "Name must not empty."));
        }
        if (content == null || content.isEmpty()) {
            errors.add(new ValidationError("content", "Content must not empty."));
        }
        if (loyaltyImageName == null || loyaltyImageName.isEmpty()) {
            errors.add(new ValidationError("loyaltyImageName", "Image Name must not empty."));
        }
        if (loyaltyImageTitle == null || loyaltyImageTitle.isEmpty()) {
            errors.add(new ValidationError("loyaltyImageTitle", "Meta Title must not empty."));
        }
        if (loyaltyImageDescription == null || loyaltyImageDescription.isEmpty()) {
            errors.add(new ValidationError("loyaltyImageDescription", "Meta Description must not empty."));
        }
        if (loyaltyImageKeyword == null || loyaltyImageKeyword.isEmpty()) {
            errors.add(new ValidationError("loyaltyImageKeyword", "Meta Keyword must not empty."));
        }
        if(errors.size() > 0)
            return errors;

        return null;
    }

    public String getChangeLogData(Loyalty data){
        HashMap<String, String> map = new HashMap<>();
        map.put("content",(data.content == null)? "":data.content);
        map.put("name",(data.name == null)? "":data.name);
        map.put("slug",(data.slug == null)? "":data.slug);
        map.put("loyalty_image_name",(data.loyaltyImageName == null)? "":data.loyaltyImageName);
        map.put("loyalty_image_title",(data.loyaltyImageTitle == null)? "":data.loyaltyImageTitle);
        map.put("loyalty_image_keyword",(data.loyaltyImageKeyword == null)? "":data.loyaltyImageKeyword);
        map.put("loyalty_image_description",(data.loyaltyImageDescription == null)? "":data.loyaltyImageDescription);
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
        Loyalty oldStaticPage = Loyalty.find.byId(id);
        super.update();

        ChangeLog changeLog;
        if(isDeleted == true){
            changeLog = new ChangeLog(LOG_TYPE, this.userCms.id, LOG_TABLE_NAME, this.id, "DELETE", getChangeLogData(oldStaticPage), null);
        }else{
            changeLog = new ChangeLog(LOG_TYPE, this.userCms.id, LOG_TABLE_NAME, this.id, "EDIT", getChangeLogData(oldStaticPage), getChangeLogData(this));
        }
        changeLog.save();

    }

	public Loyalty(String content, String name, String slug, UserCms userCms) {
		super();
		this.content = content;
		this.name = name;
		this.slug = slug;
		this.userCms = userCms;
	}
    
    

}