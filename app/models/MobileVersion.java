package models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.avaje.ebean.Page;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hokeba.util.CommonFunction;

import play.db.ebean.Model.Finder;

@Entity
@Table(name="mobile_version")
public class MobileVersion extends BaseModel {
	public static final String DEVICETYPE_ANDROID = "ANDROID";
	public static final String DEVICETYPE_IOS = "IOS";
	
	@JsonProperty("mobile_version")
	public Integer mobileVersion;
	
	@JsonProperty("mobile_version_ios")
	public Integer mobileVersionIos;

	@JsonProperty("description")
	public String description;

	@JsonProperty("url_android")
	public String urlAndroid;
	
	@JsonProperty("url_ios")
	public String urlIOS;
	
	@JsonProperty("major_minor_update")
	public boolean majorMinorUpdate;

	@Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING,  pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone="Asia/Jakarta")
    @JsonProperty("release_date")
    public Date releaseDate;
	
	@javax.persistence.Transient
    public String releaseDateTime = "";

    @javax.persistence.Transient
    public String releaseTime = "";

	
	@javax.persistence.Transient
    public String save;
	
	@Transient
    public String getDateFrom() {
        String date = "";
        if(releaseDate != null) date = CommonFunction.getDateFrom(releaseDate, "MM/dd/yyyy HH:mm:ss");
        return date;
    }
	
	@Transient
	public String isMajorMinorUpdate() {
		String name = "";
		if(majorMinorUpdate)
			name = "Major";
		else
			name = "Minor";
		return name;
	}

	public static Finder<Long, MobileVersion> find = new Finder<Long, MobileVersion>(Long.class, MobileVersion.class);

	public static Page<MobileVersion> page(int page, int pageSize, String sortBy, String order, String filter) {
		return find.where().ilike("mobile_version", "%" + filter + "%").eq("is_deleted", false).orderBy(sortBy + " " + order)
				.findPagingList(pageSize).setFetchAhead(false).getPage(page);
	}

	public static Integer RowCount() {
		return find.where().eq("is_deleted", false).findRowCount();
	}
}
