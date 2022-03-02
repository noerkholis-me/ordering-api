package models;

import com.avaje.ebean.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hokeba.util.CommonFunction;
import com.hokeba.util.Constant;
import org.apache.commons.lang3.ArrayUtils;
import play.data.validation.ValidationError;
import play.libs.Json;

import javax.persistence.*;
import java.beans.Transient;
import java.io.IOException;
import java.util.*;


/**
 * Created by hendriksaragih on 2/5/17.
 */
@Entity
public class Banner extends BaseModel{
    private static final long serialVersionUID = 1L;
    public static final boolean ACTIVE = true;
    public static final boolean INACTIVE = false;
    public static final String TYPE_WEB = "Web";
    public static final String TYPE_MOBILE = "Mobile";
    private static final String LOG_TYPE = "ADMIN";
    private static final String LOG_TABLE_NAME = "banner";

    public String name;
    public String caption1;
    public String caption2;
    public String title;
    public String slug;
    public String description;
    public String keyword;
    public boolean status;
    public boolean flashSale;
    public boolean customDiamond;

    public int sequence;
    @JsonProperty("sequence_mobile")
    public int sequenceMobile;

    @JsonProperty("type_id")
    public Integer typeId;
    @JsonProperty("position_id")
    public Integer positionId;
    
    @JsonProperty("open_new_tab")
	public boolean openNewTab;
    @JsonProperty("link_url")
	public String linkUrl;

    @JsonProperty("banner_image_name")
    @Column(name = "banner_image_name", columnDefinition = "TEXT")
    public String bannerImageName;
    @JsonProperty("banner_image_keyword")
    public String bannerImageKeyword;
    @JsonProperty("banner_image_title")
    public String bannerImageTitle;
    @JsonProperty("banner_image_description")
    @Column(name = "banner_image_description", columnDefinition = "TEXT")
    public String bannerImageDescription;

    @JsonProperty("image_url")
    public String imageUrl;
    @JsonIgnore
    public String bannerSize;
    @JsonProperty("banner_size")
    public int[] getBannerSize() throws IOException{
        ObjectMapper om = new ObjectMapper();
        return (bannerSize==null) ? null : om.readValue(bannerSize, int[].class);
    }
    @JsonProperty("image_url_responsive")
    public String imageUrlResponsive;
    @JsonIgnore
    public String bannerResponsiveSize;
    @JsonProperty("banner_responsive_size")
    public int[] getBannerResponsiveSize() throws IOException{
        ObjectMapper om = new ObjectMapper();
        return (bannerResponsiveSize==null) ? null : om.readValue(bannerResponsiveSize, int[].class);
    }
    @JsonProperty("image_url_mobile")
    public String imageUrlMobile;
    @JsonIgnore
    public String bannerMobileSize;
    @JsonProperty("banner_mobile_size")
    public int[] getBannerMobileSize() throws IOException{
        ObjectMapper om = new ObjectMapper();
        return (bannerMobileSize==null) ? null : om.readValue(bannerMobileSize, int[].class);
    }
    @JsonIgnore
    @ManyToMany
    public List<Merchant> merchants;

    @JsonIgnore
    @ManyToMany
    public List<Category> categories;

    @JsonIgnore
    @ManyToMany
    public List<Product> products;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING,  pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone="Asia/Jakarta")
    @JsonProperty("active_from")
    public Date activeFrom;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING,  pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone="Asia/Jakarta")
    @JsonProperty("active_to")
    public Date activeTo;

    @JsonIgnore
    @JoinColumn(name="user_id")
    @ManyToOne
    public UserCms userCms;

    @javax.persistence.Transient
    public String save;

    @javax.persistence.Transient
    public String imageLink;

    @javax.persistence.Transient
    public String imageResponsiveLink;

    @javax.persistence.Transient
    public String imageMobileLink;

    @javax.persistence.Transient
    public List<String> merchant_list;

    @javax.persistence.Transient
    public List<String> category_list;

    @javax.persistence.Transient
    public List<String> subcategory_list;

    @javax.persistence.Transient
    public List<String> product_list;


    @javax.persistence.Transient
    public String fromDate = "";

    @javax.persistence.Transient
    public String toDate = "";

    @javax.persistence.Transient
    public String fromTime = "";

    @javax.persistence.Transient
    public String toTime = "";

    @javax.persistence.Transient
    @JsonProperty("meta_title")
    public String getMetaTitle(){
        return title;
    }
    @javax.persistence.Transient
    @JsonProperty("meta_keyword")
    public String getMetaKeyword(){
        return keyword;
    }
    @javax.persistence.Transient
    @JsonProperty("meta_description")
    public String getMetaDescription(){
        return description;
    }

    @Transient
    @JsonProperty("user_creator")
    public String getUserCreator(){
        return userCms.email;
    }

    public String getImageUrl(){
        return getImageLink();
    }

    public String getImageUrlResponsive(){
    	return getImageResponsiveLink();
    }
    
    public String getImageUrlMobile(){
        return getImageMobileLink();
    }

    @Transient
    @JsonProperty("product_detail")
    public Long getProductDetail() {
        return (products != null && products.size() == 1) ? products.get(0).id : null;
    }

    @Transient
    @JsonProperty("product_detail_slug")
    public String getProductDetailSlug() {
        return (products != null && products.size() == 1) ? products.get(0).slug : null;
    }

    public String getSlug(){
        return "banner/"+slug;
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
    public String fetchFlashSale() {
        String flashSaleName = "";
        if(flashSale)
            flashSaleName = "flashSale";
        else flashSaleName = "notFlashSale";

        return flashSaleName;
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

    public String getType(){
        String type = "";
        switch (typeId){
            case 0: type = "Web & Mobile"; break;
            case 1: type = "Web"; break;
            case 2: type = "Mobile"; break;
        }
        return type;
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

    public String getImage() {
        return Images.getImage(imageUrl);
    }

    public static Finder<Long, Banner> find = new Finder<Long, Banner>(Long.class, Banner.class);

    public static String validation(Banner model){
        if(model.name==null || model.name.equals("")){
            return "Please insert banner name";
        } else if((model.bannerImageName==null || model.bannerImageName.equals(""))||
                (model.bannerImageTitle==null || model.bannerImageTitle.equals(""))||
                (model.bannerImageKeyword==null || model.bannerImageKeyword.equals(""))||
                (model.bannerImageDescription==null)){
            return "Please describe all information for banner's image";
        } else if(model.activeFrom==null||model.activeTo==null||model.activeFrom.after(model.activeTo)){
            return "Please input valid active date range";
        } else if (!ArrayUtils.contains(new int[]{1, 2}, model.typeId)){
            return "Please select valid banner type";
        } else if ((model.typeId==1&&!ArrayUtils.contains(new int[]{1,2,3}, model.positionId))||
                (model.typeId==2&&!ArrayUtils.contains(new int[]{4,5,6}, model.positionId))){
            return "Please select valid banner position";
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
        if (typeId == null) {
            errors.add(new ValidationError("typeId", "Type must not empty."));
        }
        if (positionId == null || positionId==0) {
            errors.add(new ValidationError("positionId", "Position must not empty."));
        }

        if (bannerImageName == null || bannerImageName.isEmpty()) {
            errors.add(new ValidationError("bannerImageName", "Image Name must not empty."));
        }
        if (bannerImageTitle == null || bannerImageTitle.isEmpty()) {
            errors.add(new ValidationError("bannerImageTitle", "Meta Title must not empty."));
        }
        if (bannerImageDescription == null || bannerImageDescription.isEmpty()) {
            errors.add(new ValidationError("bannerImageDescription", "Meta Description must not empty."));
        }
        if (bannerImageKeyword == null || bannerImageKeyword.isEmpty()) {
            errors.add(new ValidationError("bannerImageKeyword", "Meta Keyword must not empty."));
        }
//        if (imageUrl == null || imageUrl.isEmpty()) {
//            errors.add(new ValidationError("imageUrl", "Image must not empty."));
//        }

        if(errors.size() > 0)
            return errors;

        return null;
    }

    //Alex, 13-01-2017, Method ini digunakan untuk mengembalikan seluruh pilihan tipe banner yang dapat dipilih
    @SuppressWarnings("deprecation")
    public static ObjectNode getBannerOption(){
        BannerType[] types = {BannerType.bannerTypeWeb, BannerType.bannerTypeMobile};
        ObjectNode[] positions = new ObjectNode[types.length];
        ObjectNode[] linkTypes = new ObjectNode[types.length];
        int count = 0;
        for (BannerType type : types) {
            positions[count]= Json.newObject();
            positions[count].put("type", type.getId());
            positions[count].put("values", Json.toJson(BannerPosition.getBannerPositionByType(type.getId())));
            linkTypes[count] = Json.newObject();
            linkTypes[count].put("type", type.getId());
            linkTypes[count].put("values", Json.toJson(BannerLinkType.getBannerLinkTypeByType(type.getId())));
            count++;
        }
        ObjectNode result = Json.newObject();
        result.put("types", Json.toJson(types));
        result.put("positions", Json.toJson(positions));
        result.put("link_types", Json.toJson(linkTypes));
        return result;
    }

    //Alex, 13-01-2017, Method ini digunakan untuk mengembalikan list banner berdasarkan tipe yang dipilih
    //list banner yang dikembalikan sudah dipilah berdasarkan posisinya dan terurut oleh sequence-nya
    public static List<BannerPositionFilter> getBannerByType(int type){
        List<BannerPositionFilter> result = new LinkedList<>();
        BannerPosition[] listPos = BannerPosition.getBannerPositionByType(type);
        List<Integer> listPosId = new LinkedList<>();
        LinkedHashMap<Integer, ArrayList<Banner>> listResult = new LinkedHashMap<>();
        for (BannerPosition position: listPos) {
            listPosId.add(position.getId());
            listResult.put(position.getId(), new ArrayList<>());
        }
        Date now = new Date();
        List<Banner> bannerList = Banner.find.where()
                .eq("status", true)
                .eq("is_deleted", false)
                .ne("type_id", 1)
                .le("active_from", now)
                .ge("active_to", now)
                .orderBy("position_id asc, sequenceMobile asc").findList();

        for (Banner banner : bannerList) {
            if (banner.imageUrlMobile != null && !banner.imageUrlMobile.isEmpty()){
                banner.imageUrl = banner.imageUrlMobile;
            }
            int position = 4;
            if (banner.positionId != null && banner.positionId < 4){
                position = banner.positionId + 3;
            }
            banner.sequence = banner.sequenceMobile;
            listResult.get(position).add(banner);
        }
        int count = 0;
        for (Integer key : listResult.keySet()) {
            result.add(new BannerPositionFilter(listPos[count].getId(), listPos[count].getName(), listResult.get(key)));
            count++;
        }
        return result;
    }

    //Alex, 24-01-2017, Method ini digunakan untuk menginformasikan sequence banner selanjutnya pada suatu posisi
    public static int getNextSequence(int positionId){
        SqlQuery sqlQuery = Ebean.createSqlQuery(
                "select max(sequence) as max from banner where status = true and position_id = :positionId");
        sqlQuery.setParameter("positionId", positionId);
        SqlRow result = sqlQuery.findUnique();
        int resSequence = (result.getInteger("max")==null ? 0 : result.getInteger("max"))+1;
        return resSequence;
    }

    public static Page<Banner> page(int page, int pageSize, String sortBy, String order, String name, Integer filter) {
        ExpressionList<Banner> qry = Banner.find
                .where()
                .ilike("name", "%" + name + "%")
                .eq("is_deleted", false);

        switch (filter){
            case 1: qry.eq("type_id", 2);
                break;
            case 2: qry.eq("type_id", 1);
                break;
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

    public String getImageLink(){
        return imageUrl==null || imageUrl.isEmpty() ? "" : Constant.getInstance().getImageUrl() + imageUrl;
    }

    public String getImageResponsiveLink(){
        return imageUrlResponsive==null || imageUrlResponsive.isEmpty() ? "" : Constant.getInstance().getImageUrl() + imageUrlResponsive;
    }

    public String getImageMobileLink(){
        return imageUrlMobile==null || imageUrlMobile.isEmpty() ? "" : Constant.getInstance().getImageUrl() + imageUrlMobile;
    }
    
    public static List<Banner> getAllBanners(String deviceType) {
        Date now = new Date();
        ExpressionList<Banner> qry = Banner.find
                .where()
                .le("active_from", now)
                .ge("active_to", now)
                .eq("is_deleted", false)
                .eq("status", true);
        if(MemberLog.DEV_TYPE_WEB.equals(deviceType)){
            qry.ne("type_id", 2);
            qry.order("sequence asc");
        }else{
            qry.ne("type_id", 1);
            qry.order("sequenceMobile asc");
        }
        return qry.findList();
    }
    
    public static List<Banner> getAllBannerFlashSale(String deviceType) {
        Date now = new Date();
        ExpressionList<Banner> qry = Banner.find
                .where()
                .le("active_from", now)
                .ge("active_to", now)
                .eq("is_deleted", false)
                .eq("status", true)
                .eq("flash_sale", true);
        if(MemberLog.DEV_TYPE_WEB.equals(deviceType)){
            qry.ne("type_id", 2);
            qry.order("sequence asc");
        }else{
            qry.ne("type_id", 1);
            qry.order("sequenceMobile asc");
        }
        return qry.findList();
    }

    public static List<Banner> getAllBanner(String deviceType) {
        Date now = new Date();
        ExpressionList<Banner> qry = Banner.find
                .where()
                .le("active_from", now)
                .ge("active_to", now)
                .eq("is_deleted", false)
                .eq("flash_sale", false)
                .eq("status", true);
        if(MemberLog.DEV_TYPE_WEB.equals(deviceType)){
            qry.ne("type_id", 2);
            qry.order("sequence asc");
        }else{
            qry.ne("type_id", 1);
            qry.order("sequenceMobile asc");
        }

        return qry.findList();
//        return Banner.find.where()
//                .le("active_from", now)
//                .ge("active_to", now)
//                .eq("is_deleted", false)
//                .eq("status", true)
////                .eq("type_id", 1)
//                .ne("type_id", 2)
//                .order("sequence asc").findList();
    }
    
    public String getChangeLogData(Banner data){
        HashMap<String, String> map = new HashMap<>();
        map.put("title",(data.title == null)? "":data.title);
        map.put("description",(data.description == null)? "":data.description);
        map.put("keyword",(data.keyword == null)? "":data.keyword);
        map.put("caption1",(data.caption1 == null)? "":data.caption1);
        map.put("caption2",(data.caption2 == null)? "":data.caption2);
        map.put("name",(data.name == null)? "":data.name);
        map.put("type",(data.typeId == 1)? "Web":"Mobile");
        map.put("status",(data.status == true)? "Active":"Inactive");
        map.put("flashSale",(data.flashSale == true)? "flashSale":"notFlashSale");
        map.put("position",data.getPosition());
        map.put("banner_image_name",(data.bannerImageName == null)? "":data.bannerImageName);
        map.put("banner_image_title",(data.bannerImageTitle == null)? "":data.bannerImageTitle);
        map.put("banner_image_keyword",(data.bannerImageKeyword == null)? "":data.bannerImageKeyword);
        map.put("banner_image_description",(data.bannerImageDescription == null)? "":data.bannerImageDescription);
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
        Banner oldBanner = Banner.find.byId(id);
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
            status = Banner.ACTIVE;
        else if(newStatus.equals("inactive"))
            status = Banner.INACTIVE;

        super.update();

        ChangeLog changeLog;
        changeLog = new ChangeLog(LOG_TYPE, this.userCms.id, LOG_TABLE_NAME, this.id, "EDIT", oldBannerData, getChangeLogData(this));
        changeLog.save();

    }
    
    public void updateFlashSale(String newFlashSale) {
        String oldBannerData = getChangeLogData(this);

        if(newFlashSale.equals("active"))
            flashSale = Banner.ACTIVE;
        else if(newFlashSale.equals("inactive"))
            flashSale = Banner.INACTIVE;

        super.update();

        ChangeLog changeLog;
        changeLog = new ChangeLog(LOG_TYPE, this.userCms.id, LOG_TABLE_NAME, this.id, "EDIT", oldBannerData, getChangeLogData(this));
        changeLog.save();

    }

    public void updateSequence(int seq) {
        sequence = seq;
        super.update();
    }

    public static void seed(Integer seq, String name, Integer type, String url, List<Merchant> merchants,
                            List<Category> categories, List<Product> products,
                            Date start, Date end, UserCms user){
        Banner model = new Banner();
        model.name = model.caption1 = model.caption2 = model.title = model.description = model.keyword =
                model.bannerImageName = model.bannerImageKeyword =
                        model.bannerImageTitle = model.bannerImageDescription = name;

        model.slug = CommonFunction.slugGenerate(model.name);
        model.status = true;
        model.typeId = type;
        model.positionId = type==1 ? 1 : 4;
        model.sequence = seq;
        model.imageUrl = model.imageUrlResponsive = url;
        model.userCms = user;
        model.merchants = merchants;
        model.categories = categories;
        model.products = products;
        model.activeFrom = start;
        model.activeTo = end;
        model.save();

        Photo.saveRecord("ban",url, "", "", "", url, user.id, "admin", "Banner", model.id);
        Photo.saveRecord("ban-res",url, "", "", "", url, user.id, "admin", "Banner", model.id);
    }

    public static BannerList getDetails(String slug){
        Date now = new Date();
        Banner banner = Banner.find.where()
                .eq("status", true)
                .eq("is_deleted", false)
                .eq("slug", slug)
                .le("active_from", now)
                .ge("active_to", now)
                .setMaxRows(1).findUnique();

        return banner != null ? new BannerList(banner) : null;
    }
}