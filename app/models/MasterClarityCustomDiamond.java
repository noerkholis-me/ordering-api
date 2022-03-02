package models;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hokeba.util.Constant;

import play.db.ebean.Model.Finder;

@Entity
public class MasterClarityCustomDiamond extends BaseModel {
	
	public String name;
	public String description;
	@JsonProperty("image_name")
	public String imageName;
	public String url;
	
	@JsonIgnore
	@JoinColumn(name = "user_id")
	@ManyToOne
	public UserCms userCms;
	
	@Transient
	public String save;
	
	@javax.persistence.Transient
	public String imageLink;
	
	@javax.persistence.Transient
	public int urlX;
	@javax.persistence.Transient
	public int urlY;
	@javax.persistence.Transient
	public int urlW;
	@javax.persistence.Transient
	public int urlH;
	
	public String getImageUrl(){
		return getImageLink();
	}

	public String getImageLink(){
		return url==null || url.isEmpty() ? "" : Constant.getInstance().getImageUrl() + url;
	}
	
	public static Finder<Long, MasterClarityCustomDiamond> find = new Finder<Long, MasterClarityCustomDiamond>(Long.class, MasterClarityCustomDiamond.class);

}
