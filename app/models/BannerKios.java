package models;

import com.avaje.ebean.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hokeba.util.CommonFunction;
import com.hokeba.util.Constant;
import play.data.validation.ValidationError;
import play.libs.Json;
import javax.persistence.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Entity
public class BannerKios extends BaseModel{
    private static final long serialVersionUID = 1L;
    public static final boolean ACTIVE = true;
    public static final boolean INACTIVE = false;
    private static final String LOG_TYPE = "ADMIN";
    private static final String LOG_TABLE_NAME = "banner_kios";

    public String bannerName;
    public String imageName;
    public boolean status;
    public int sequence;
    public Integer positionId;
    public String slug;
    
    public String imagePromoPageUrl;

    @JsonIgnore
    public String imagePromoPageSize;
    
    public int[] getImagePromoPageSize() throws IOException{
        ObjectMapper om = new ObjectMapper();
        return (imagePromoPageSize==null) ? null : om.readValue(imagePromoPageSize, int[].class);
    }
    
    public String imageHomePageUrl;
    
    @JsonIgnore
    public String imageHomePageSize;

    public int[] getImageHomePageSize() throws IOException{
        ObjectMapper om = new ObjectMapper();
        return (imageHomePageSize==null) ? null : om.readValue(imageHomePageSize, int[].class);
    }
    
    public String imageMobileUrl;
    
    @JsonIgnore
    public String imageMobileSize;

    public int[] getImageMobileSize() throws IOException{
        ObjectMapper om = new ObjectMapper();
        return (imageMobileSize==null) ? null : om.readValue(imageMobileSize, int[].class);
    }

    
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING,  pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone="Asia/Jakarta")
    public Date activeFrom;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING,  pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone="Asia/Jakarta")
    public Date activeTo;

    @JsonIgnore
    @JoinColumn(name="user_id")
    @ManyToOne
    public UserCms userCms;
    
	public String linkUrl;

    @Transient
    public String save;

    @Transient
    public String imagePromoPageLink;

    @Transient
    public String imageHomePageLink;

    @Transient
    public String imageMobileLink;
    
    @Transient
    public String fromDate = "";

    @Transient
    public String toDate = "";

    @Transient
    public String fromTime = "";

    @Transient
    public String toTime = "";
    
    @Transient
    public int imagePromoPageUrlX;
    @Transient
    public int imagePromoPageUrlY;
    @Transient
    public int imagePromoPageUrlW;
    @Transient
    public int imagePromoPageUrlH;

    @Transient
    public int imageHomePageUrlX;
    @Transient
    public int imageHomePageUrlY;
    @Transient
    public int imageHomePageUrlW;
    @Transient
    public int imageHomePageUrlH;

    @Transient
    public int imageMobileUrlX;
    @Transient
    public int imageMobileUrlY;
    @Transient
    public int imageMobileUrlW;
    @Transient
    public int imageMobileUrlH;

    @Transient
    @JsonProperty("user_creator")
    public String getUserCreator(){
        return userCms.email;
    }

    public String getImagePromoPageUrl(){
        return getImagePromoPageLink();
    }

    public String getImageHomePageUrl(){
        return getImageHomePageLink();
    }

    public String getImageMobileUrl(){
    	return getImageMobileLink();
    }
    
    @Transient
    public String getStatus() {
        String statusName = "";
        if(status)
            statusName = "Active";
        else statusName = "Inactive";

        return statusName;
    }
    
    @Transient
    public String getPosition() {
        switch (positionId){
            case 1:
            case 4:
                return "Main Banner";
            case 2:
            case 5:
                return "Middle Banner";
            case 3:
            case 6:
                return "Footer Banner";
        }
        return "";
    }

    @Transient
    public String getDateFrom() {
        String date = "";
        if(activeFrom != null) date = CommonFunction.getDateFrom(activeFrom, "MM/dd/yyyy HH:mm:ss");

        return date;
    }

    @Transient
    public String getDateTo() {
        String date = "";
        if(activeTo != null) date = CommonFunction.getDateFrom(activeTo, "MM/dd/yyyy HH:mm:ss");

        return date;
    }

    public static Finder<Long, BannerKios> find = new Finder<Long, BannerKios>(Long.class, BannerKios.class);

    public static String validation(BannerKios bannerKios){
        if(bannerKios.bannerName==null || bannerKios.bannerName.equals("")){
            return "Please insert banner name";
        } else if((bannerKios.imageName==null || bannerKios.imageName.equals(""))){
            return "Please describe all information for banner's image";
        } else if(bannerKios.activeFrom==null||bannerKios.activeTo==null||bannerKios.activeFrom.after(bannerKios.activeTo)){
            return "Please input valid active date range";
        }
        return null;
    }

    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<>();

        if (bannerName == null || bannerName.isEmpty()) {
            errors.add(new ValidationError("name", "Name must not empty."));
        }
        if (imageName == null || imageName.isEmpty()) {
            errors.add(new ValidationError("bannerImageName", "Image Name must not empty."));
        }

        if(errors.size() > 0)
            return errors;

        return null;
    }

    //Alex, 24-01-2017, Method ini digunakan untuk menginformasikan sequence banner selanjutnya pada suatu posisi
    public static int getNextSequence(int positionId){
        SqlQuery sqlQuery = Ebean.createSqlQuery(
                "select max(sequence) as max from banner_kios where status = true and position_id = :positionId");
        sqlQuery.setParameter("positionId", positionId);
        SqlRow result = sqlQuery.findUnique();
        int resSequence = (result.getInteger("max")==null ? 0 : result.getInteger("max"))+1;
        return resSequence;
    }

    public static Page<BannerKios> page(int page, int pageSize, String sortBy, String order, String name, Integer filter) {
        ExpressionList<BannerKios> qry = BannerKios.find
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

    public String getImagePromoPageLink(){
        return imagePromoPageUrl==null || imagePromoPageUrl.isEmpty() ? "" : Constant.getInstance().getImageUrl() + imagePromoPageUrl;
    }

    public String getImageHomePageLink(){
        return imageHomePageUrl==null || imageHomePageUrl.isEmpty() ? "" : Constant.getInstance().getImageUrl() + imageHomePageUrl;
    }
    
    public String getImageMobileLink(){
    	return imageMobileUrl==null || imageMobileUrl.isEmpty() ? "" : Constant.getInstance().getImageUrl() + imageMobileUrl;
    }

    public String getChangeLogData(BannerKios data){
        HashMap<String, String> map = new HashMap<>();
        map.put("name",(data.bannerName == null)? "":data.bannerName);
        map.put("status",(data.status == true)? "Active":"Inactive");
        map.put("position",data.getPosition());
        map.put("image_name",(data.imageName == null)? "":data.imageName);
        map.put("active_from", data.getDateFrom());
        map.put("active_to",data.getDateTo());
        map.put("link_url",(data.linkUrl == null)? "":data.linkUrl);

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
        BannerKios oldBanner = BannerKios.find.byId(id);
        super.update();

        ChangeLog changeLog;
        if(isDeleted == true){
            changeLog = new ChangeLog(LOG_TYPE, this.userCms.id, LOG_TABLE_NAME, this.id, "DELETE", getChangeLogData(oldBanner), null);
        }else{
            changeLog = new ChangeLog(LOG_TYPE, this.userCms.id, LOG_TABLE_NAME, this.id, "EDIT", getChangeLogData(oldBanner), getChangeLogData(this));
        }
        changeLog.save();

    }

    public void updateStatus(String newStatus) {
        String oldBannerData = getChangeLogData(this);

        if(newStatus.equals("active"))
            status = BannerKios.ACTIVE;
        else if(newStatus.equals("inactive"))
            status = BannerKios.INACTIVE;

        super.update();

        ChangeLog changeLog;
        changeLog = new ChangeLog(LOG_TYPE, this.userCms.id, LOG_TABLE_NAME, this.id, "EDIT", oldBannerData, getChangeLogData(this));
        changeLog.save();

    }
    
    public void updateSequence(int positionId, int seq) {
        if(positionId <= 3)
            sequence = seq;
        super.update();
    }
    
    public static List<BannerKios> getAllBanner() {
        Date now = new Date();
        ExpressionList<BannerKios> qry = BannerKios.find
                .where()
                .le("active_from", now)
                .ge("active_to", now)
                .eq("is_deleted", false)
                .eq("status", true);

        return qry.findList();
    }

}