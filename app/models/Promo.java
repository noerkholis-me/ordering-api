package models;

import com.avaje.ebean.*;
import com.avaje.ebean.Query;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hokeba.api.BaseResponse;
import com.hokeba.mapping.request.MapMerchantPromoRequest;
import com.hokeba.mapping.request.MapMerchantPromoRequestProduct;
import com.hokeba.mapping.response.MapAllPromoMerchantList;
import com.hokeba.mapping.response.MapMyPromoMerchantList;
import com.hokeba.mapping.response.MapPromoProductRequestStatus;
import com.hokeba.util.CommonFunction;
import com.hokeba.util.Constant;
import models.mapper.MerchantPromoProductList;
import models.mapper.PromoProductList;
import play.Logger;
import play.libs.Json;

import javax.persistence.*;
import java.beans.Transient;
import java.io.IOException;
import java.util.*;

/**
 * Created by hendriksaragih on 2/5/17.
 */
@Entity
public class Promo extends BaseModel{
    private static final long serialVersionUID = 1L;
    public static final boolean ACTIVE = true;
    public static final boolean INACTIVE = false;
    private static final String LOG_TYPE = "ADMIN";
    private static final String LOG_TABLE_NAME = "promo";

//    public static final String PROMO_STATUS_OPEN = "O";
//    public static final String PROMO_STATUS_ACTIVE = "A";
//    public static final String PROMO_STATUS_REVIEW = "R";
//    public static final String PROMO_STATUS_EXPIRED = "E";

    public String name;
    public String caption1;
    public String caption2;
    public String title;
    public String slug;
    public String description;
    public String keyword;
    public boolean status;
    public int sequence;
    @JsonProperty("all_seller")
    public boolean allSeller;

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

    @JsonProperty("link_url")
    public String linkUrl;

    @JsonProperty("promo_image_name")
    @Column(name = "promo_image_name", columnDefinition = "TEXT")
    public String promoImageName;
    @JsonProperty("promo_image_keyword")
    public String promoImageKeyword;
    @JsonProperty("promo_image_title")
    public String promoImageTitle;
    @JsonProperty("promo_image_description")
    @Column(name = "promo_image_description", columnDefinition = "TEXT")
    public String promoImageDescription;

    @JsonProperty("image_url")
    public String imageUrl;
    @JsonIgnore
    public String promoSize;
    @JsonProperty("promo_size")
    public int[] getPromoSize() throws IOException{
        ObjectMapper om = new ObjectMapper();
        return (promoSize==null) ? null : om.readValue(promoSize, int[].class);
    }
    @JsonProperty("image_url_responsive")
    public String imageUrlResponsive;
    @JsonIgnore
    public String promoResponsiveSize;
    @JsonProperty("promo_responsive_size")

    public int[] getPromoResponsiveSize() throws IOException{
        ObjectMapper om = new ObjectMapper();
        return (promoResponsiveSize==null) ? null : om.readValue(promoResponsiveSize, int[].class);
    }

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING,  pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone="Asia/Jakarta")
    @JsonProperty("active_from")
    public Date activeFrom;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING,  pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone="Asia/Jakarta")
    @JsonProperty("active_to")
    public Date activeTo;
    
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING,  pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone="Asia/Jakarta")
    @JsonProperty("notification_time")
    public Date notificationTime;

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
    public String fromDate = "";

    @javax.persistence.Transient
    public String toDate = "";

    @javax.persistence.Transient
    public String fromTime = "";

    @javax.persistence.Transient
    public String toTime = "";

//    @javax.persistence.Transient
//    public Long categoryPromoId;

    @javax.persistence.Transient
    public Long subcategoryid;

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

    @javax.persistence.Transient
    public List<Product> productListSlug;

    @Transient
    @JsonProperty("user_creator")
    public String getUserCreator(){
        return userCms.email;
    }

    @javax.persistence.Transient
    @JsonProperty("image_responsive_src")
    public String getImageResponsiveSrc(){
        return getImageResponsiveLink();
    }
    @javax.persistence.Transient
    @JsonProperty("image_src")
    public String getImageSrc(){
        return getImageLink();
    }
    @javax.persistence.Transient
    @JsonProperty("image_title")
    public String getImageTitle(){
        return title;
    }
    @javax.persistence.Transient
    @JsonProperty("image_keyword")
    public String getImageKeyword(){
        return keyword;
    }
    @javax.persistence.Transient
    @JsonProperty("image_description")
    public String getImageDescription(){
        return description;
    }

    @Transient
    @JsonProperty("product_detail")
    public Long getProductDetail() {
//        return (products != null && products.size() == 1) ? products.get(0).id : null;
        setProductSlug();
        return (productListSlug != null && productListSlug.size() == 1) ? productListSlug.get(0).id : null;
    }
    @Transient
    @JsonProperty("product_detail_slug")
    public String getProductDetailSlug() {
        setProductSlug();
//        return (products != null && products.size() == 1) ? products.get(0).slug : null;
        return (productListSlug != null && productListSlug.size() == 1) ? productListSlug.get(0).slug : null;
    }

    public String getSlug(){
        return "promo/"+slug;
    }

    @Transient
    public String getStatus() {
        String statusName = "";
        if(status)
            statusName = "Active";
        else statusName = "Inactive";

        return statusName;
    }

//    @Transient
//    public String getStatusName() {
//        String statusName = "";
//        switch (status){
//            case PROMO_STATUS_OPEN : statusName = "Open";break;
//            case PROMO_STATUS_ACTIVE : statusName = "Active";break;
//            case PROMO_STATUS_REVIEW : statusName = "Review";break;
//            case PROMO_STATUS_EXPIRED : statusName = "Expired";break;
//        }
//
//        return statusName;
//    }

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

    @javax.persistence.Transient
    public String tmp_log_data;

    public String getImage() {
        return Images.getImage(imageUrl);
    }

    public static Finder<Long, Promo> find = new Finder<Long, Promo>(Long.class, Promo.class);

    public static Set<Promo> getMerchantPromo(){
        Set<Promo> result = Promo.find.fetch("merchants").where().not(Expr.eq("merchants.id", null)).findSet();
        return result;
    }

    public static Set<Promo> getBrandPromo(){
        Set<Promo> result = Promo.find.fetch("brands").where().not(Expr.eq("brands.id", null)).findSet();
        return result;
    }

    public static Set<Promo> getProductPromo(){
        Set<Promo> result = Promo.find.fetch("products").where().not(Expr.eq("products.id", null)).findSet();
        return result;
    }

    public static Set<Category> getProductCategory(){
        Set<Category> result = Category.find.fetch("categories").where().not(Expr.eq("categories.id", null)).findSet();
        return result;
    }

    public static Page<Promo> page(int page, int pageSize, String sortBy, String order, String filter) {
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

    public String getImageLink(){
        return imageUrl==null || imageUrl.isEmpty() ? "" : Constant.getInstance().getImageUrl() + imageUrl;
    }

    public String getImageResponsiveLink(){
        return imageUrlResponsive==null || imageUrlResponsive.isEmpty() ? "" : Constant.getInstance().getImageUrl() + imageUrlResponsive;
    }

    public String getChangeLogData(Promo data){
        HashMap<String, String> map = new HashMap<>();
        map.put("title",(data.title == null)? "":data.title);
        map.put("description",(data.description == null)? "":data.description);
        map.put("keyword",(data.keyword == null)? "":data.keyword);
        map.put("caption1",(data.caption1 == null)? "":data.caption1);
        map.put("caption2",(data.caption2 == null)? "":data.caption2);
        map.put("name",(data.name == null)? "":data.name);
        map.put("status",getStatus());
        map.put("link_url",(data.linkUrl == null)? "":data.linkUrl);
        map.put("promo_image_name",(data.promoImageName == null)? "":data.promoImageName);
        map.put("promo_image_title",(data.promoImageTitle == null)? "":data.promoImageTitle);
        map.put("promo_image_keyword",(data.promoImageKeyword == null)? "":data.promoImageKeyword);
        map.put("promo_image_description",(data.promoImageDescription == null)? "":data.promoImageDescription);
        map.put("active_from", data.getDateFrom());
        map.put("active_to",data.getDateTo());

        if(data.merchants != null){
            List<String> merchants = new ArrayList<>();
            for(Merchant merchant : data.merchants){
                merchants.add(merchant.name);
            }
            map.put("merchant",String.join(", ", merchants));
        } else map.put("merchant","");

        if(data.brands != null){
            List<String> brands = new ArrayList<>();
            for(Brand brand : data.brands){
                brands.add(brand.name);
            }
            map.put("brand",String.join(", ", brands));
        } else map.put("brand","");

        if(data.categories != null){
            List<String> categories = new ArrayList<>();
            for(Category category : data.categories){
                categories.add(category.name);
            }
            map.put("category",String.join(", ", categories));
        } else map.put("category","");

        if(data.products != null){
            List<String> products = new ArrayList<>();
            for(Product product : data.products){
                products.add(product.name);
            }
            map.put("product",String.join(", ", products));
        } else map.put("product","");

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
        Promo oldPromo = Promo.find.byId(id);
        super.update();

        ChangeLog changeLog;
        if(isDeleted == true){
            changeLog = new ChangeLog(LOG_TYPE, this.userCms.id, LOG_TABLE_NAME, this.id, "DELETE", getChangeLogData(oldPromo), null);
        }else{
            changeLog = new ChangeLog(LOG_TYPE, this.userCms.id, LOG_TABLE_NAME, this.id, "EDIT", tmp_log_data, getChangeLogData(this));
        }
        changeLog.save();

    }

    public void updateStatus(String newStatus) {
        String oldPromoData = getChangeLogData(this);

        if(newStatus.equals("active"))
            status = ACTIVE;
        else if(newStatus.equals("inactive"))
            status = INACTIVE;

        super.update();

        ChangeLog changeLog;
        changeLog = new ChangeLog(LOG_TYPE, this.userCms.id, LOG_TABLE_NAME, this.id, "EDIT", oldPromoData, getChangeLogData(this));
        changeLog.save();

    }

    public static void seed(Integer seq, String name, String url, List<Merchant> merchants, List<Brand> brands,
                            List<Category> categories, List<Product> products,
                            Date start, Date end, UserCms user){
        Promo model = new Promo();
        model.name = model.caption1 = model.caption2 = model.title = model.description = model.keyword =
                model.promoImageName = model.promoImageKeyword =
                model.promoImageTitle = model.promoImageDescription = name;
        model.sequence = seq;
        model.status = ACTIVE;
        model.slug = CommonFunction.slugGenerate(name);
        model.imageUrl = model.imageUrlResponsive = url;
        model.userCms = user;
        model.merchants = merchants;
        model.brands = brands;
        model.categories = categories;
        model.products = products;
        model.activeFrom = start;
        model.activeTo = end;
        model.save();

        Photo.saveRecord("prm",url, "", "", "", url, user.id, "admin", "Promo", model.id);
        Photo.saveRecord("prm-res",url, "", "", "", url, user.id, "admin", "PromoRes", model.id);
    }

    public static List<Promo> getHomePage() {
        Date now = new Date();
        return Promo.find.where()
                .eq("is_deleted", false)
                .le("active_from", now)
                .ge("active_to", now)
                .eq("status", ACTIVE)
                .order("sequence asc").findList();
    }

    public static BannerList getDetails(String slug){
        Date now = new Date();
        Promo banner = Promo.find.where()
                .eq("status", ACTIVE)
                .eq("is_deleted", false)
                .eq("slug", slug)
                .le("active_from", now)
                .ge("active_to", now)
                .setMaxRows(1).findUnique();
        BannerList tmp = new BannerList(banner);
        if(tmp != null){
            List<Product> products = getPromoProductList(banner);
            tmp.setProducts(products);
        }
        return tmp;
    }

    public static BannerList getDetailAll(){
        Date now = new Date();
        List<Promo> banner = Promo.find.where()
                .eq("status", ACTIVE)
                .eq("is_deleted", false)
                .le("active_from", now)
                .ge("active_to", now)
                .findList();
        BannerList tmp = new BannerList();
        List<Product> product = new ArrayList<>();
        for (Promo p : banner){
            List<Product> products = getPromoProductList(p);
            products.removeAll(product);
            product.addAll(products);
        }
        tmp.setProducts(product);
        return tmp;
    }

    /******************** FOR API MERCHANTS *************************/
    @javax.persistence.Transient
    @JsonProperty("banner_image")
    public String getBannerImage(){
        return getImageLink();
    }
    @javax.persistence.Transient
    @JsonProperty("start_date")
    public String getStartDate(){
        return CommonFunction.getDateTime(activeFrom);
    }
    @javax.persistence.Transient
    @JsonProperty("end_date")
    public String getEndDate(){
        return CommonFunction.getDateTime(activeTo);
    }
    @javax.persistence.Transient
    @JsonProperty("number_of_product")
    public int getNumberOfProduct(){

        if(products.size() > 0){
            return products.size() + MerchantPromoRequestProduct.findRowCountActive(id);
        }else {
            String sql = "SELECT count(*) as total FROM product p ";
            List<String> where = new ArrayList<>();

            if (brands.size() > 0) {
                sql += "LEFT JOIN promo_brand b on p.brand_id=b.brand_id ";
                where.add(" b.promo_id=" + id + "  ");
            }
            if (categories.size() > 0) {
                sql += "LEFT JOIN promo_category c on p.category_id=c.category_id ";
                where.add(" c.promo_id=" + id + " ");
            }
            if (merchants.size() > 0) {
                sql += "LEFT JOIN promo_merchant m on p.merchant_id=m.merchant_id ";
                where.add(" m.promo_id=" + id + " ");
            }

            if (where.size() > 0) {
                sql += " WHERE ";
                for (int i = 0; i < where.size(); i++) {
                    if (i > 0) {
                        sql += " AND ";
                    }
                    sql += where.get(i);
                }
            }

            com.avaje.ebean.SqlQuery query = Ebean.createSqlQuery(sql);
            return query.findUnique().getInteger("total") + MerchantPromoRequestProduct.findRowCountActive(id);
        }
    }


    @javax.persistence.Transient
    @JsonProperty("number_of_seller")
    public int getNumberOfSeller(){
//        if (allSeller){
//            return Merchant.findRowCountActive();
//        }else{
//            return merchants.size() + MerchantPromoRequest.findRowCountPromo(id);
//        }
        if (allSeller){
            return Merchant.findRowCountActive();
        }else{
            return Promo.getNumberOfSeller(id);
        }
    }

    @javax.persistence.Transient
    @JsonProperty("number_of_pending")
    private int numberPending;
    @javax.persistence.Transient
    @JsonProperty("number_of_approved")
    private int numberApproved;
    @javax.persistence.Transient
    @JsonProperty("number_of_rejected")
    private int numberRejected;

    @javax.persistence.Transient
    public String requestStatus;

    @javax.persistence.Transient
    @JsonProperty("request_status")
    public String getRequestStatus(){
        return requestStatus;
    }

    @javax.persistence.Transient
    public Boolean isJoin;

    @javax.persistence.Transient
    @JsonProperty("is_join")
    public Boolean getIsJoin(){
        return isJoin;
    }

    public static int getNumberOfRequestStatus(Long promoId, Long merchantId, String status){
        String sql = "SELECT COUNT(*) AS TOTAL FROM merchant_promo_request M " +
                "LEFT JOIN merchant_promo_request_product D ON M.id=D.request_id " +
                "WHERE M.promo_id = "+promoId+" AND M.merchant_id="+merchantId+" AND D.status='"+status+"'";

        com.avaje.ebean.SqlQuery query = Ebean.createSqlQuery(sql);
        return query.findUnique().getInteger("TOTAL");
    }

    public static int getNumberOfSeller(Long promoId){
        String sql = "SELECT COUNT(*) AS TOTAL FROM (" +
                "SELECT M.merchant_id " +
                "FROM merchant_promo_request M " +
                "WHERE M.promo_id = "+promoId+" " +
                "UNION " +
                "SELECT merchant_id " +
                "FROM promo_merchant pm " +
                "WHERE promo_id = "+promoId+" ) tbl";

        com.avaje.ebean.SqlQuery query = Ebean.createSqlQuery(sql);
        return query.findUnique().getInteger("TOTAL");
    }

    public static int getNumberOfApprovedProduct(Long promoId, Long merchantId){
        String sql = "SELECT COUNT(*) AS TOTAL FROM promo_product a " +
                "LEFT JOIN product b ON a.product_id=b.id " +
                "WHERE a.promo_id = "+promoId+" AND b.merchant_id="+merchantId+"";

        com.avaje.ebean.SqlQuery query = Ebean.createSqlQuery(sql);
        return query.findUnique().getInteger("TOTAL");
    }



    public static <T> BaseResponse<T> getDataAllMerchant(Merchant merchant, String type, String sort, String filter, int offset, int limit)
            throws IOException {

        String sql = "select id, name, image_url, active_from, active_to, request_status, is_join from ( " +
                "select p.id, p.name, p.image_url, p.image_url_responsive, p.active_from, p.active_to, p.status, " +
                "CASE WHEN active_to < now() THEN 'expired' " +
                "            WHEN active_to >= now() and count(rp.id) > 0 THEN 'review' " +
                "            WHEN active_to >= now() THEN 'open' " +
                "       END as request_status, " +
                "CASE WHEN p.all_seller = true THEN true " +
                "       WHEN r.id IS NOT NULL THEN true " +
                "       WHEN pm.promo_id IS NOT NULL THEN true " +
                "       ELSE false " +
                "END as is_join " +
                "from promo p " +
                "left join merchant_promo_request r on p.id=r.promo_id and r.merchant_id = "+ merchant.id +" " +
                "left join merchant_promo_request_product rp on rp.request_id=r.id and rp.status='P' " +
                "left join promo_merchant pm on pm.promo_id=p.id and pm.merchant_id = "+ merchant.id +" " +
                "where p.status = true " +
                "and p.is_deleted = false " +
                "group by p.id, p.name, r.id, pm.promo_id " +
                ") as tbl  ";

        switch (type){
            case "open" :
                sql += " where request_status='open' "; break;
            case "review" :
                sql += " where request_status='review' "; break;
            case "expired" :
                sql += " where request_status='expired' "; break;
        }
        sql += "ORDER BY id DESC";

        RawSql rawSql = RawSqlBuilder.parse(sql)
                .columnMapping("id", "id")
                .columnMapping("name", "name")
                .columnMapping("image_url", "imageUrl")
                .columnMapping("active_from", "activeFrom")
                .columnMapping("active_to", "activeTo")
                .columnMapping("request_status", "requestStatus")
                .columnMapping("is_join", "isJoin")
                .create();
        com.avaje.ebean.Query<Promo> query = Ebean.find(Promo.class);
        query.setRawSql(rawSql);
        List<Promo> resData = query.findList();

        int total = resData.size();

        BaseResponse<T> response = new BaseResponse<>();
        response.setData(new ObjectMapper().convertValue(resData, MapAllPromoMerchantList[].class));
        response.setMeta(total, offset, limit);
        response.setMessage("Success");

        return response;
    }

    private void setProductSlug(){
        if (productListSlug == null){
            String sql = "select prod.id, prod.sku " +
                    "FROM product prod " +
                    "INNER JOIN ( " +
                    "SELECT pp.product_id as id " +
                    "from promo_product pp " +
                    "WHERE pp.promo_id= "+id+" " +
                    "UNION " +
                    "SELECT rp.product_id as id " +
                    "FROM merchant_promo_request_product rp " +
                    "LEFT JOIN merchant_promo_request r on r.id=rp.request_id " +
                    "WHERE r.promo_id = "+id+" AND rp.status='A') tbl ON tbl.id = prod.id " +
                    "WHERE " +
                    "prod.is_deleted = false LIMIT 2";
            RawSql rawSql = RawSqlBuilder.parse(sql).create();
            com.avaje.ebean.Query<Product> query = Ebean.find(Product.class).setRawSql(rawSql);

            productListSlug = query.findList();
        }
    }

    public static List<Product> getAllProduct(Long promoId){
        String sql = "select prod.id " +
                "FROM product prod " +
                "INNER JOIN ( " +
                "SELECT pp.product_id as id " +
                "from promo_product pp " +
                "WHERE pp.promo_id= "+promoId+" " +
                "UNION " +
                "SELECT rp.product_id as id " +
                "FROM merchant_promo_request_product rp " +
                "LEFT JOIN merchant_promo_request r on r.id=rp.request_id " +
                "WHERE r.promo_id = "+promoId+" AND rp.status='A') tbl ON tbl.id = prod.id " +
                "WHERE " +
                "prod.is_deleted = false";
        RawSql rawSql = RawSqlBuilder.parse(sql).create();
        com.avaje.ebean.Query<Product> query = Ebean.find(Product.class).setRawSql(rawSql);

        return query.findList();
    }


    public static <T> BaseResponse<T> getDataMerchant(Merchant merchant, String type, String sort, String filter, int offset, int limit)
            throws IOException {

        String sql = "select id, name, image_url, active_from, active_to, request_status from ( " +
                "select p.id, p.name, p.image_url, p.image_url_responsive, p.active_from, p.active_to, p.status, " +
                "CASE WHEN active_to < now() THEN 'expired' " +
                "            WHEN active_to >= now() and count(rp.id) > 0 THEN 'review' " +
                "            WHEN active_to >= now() THEN 'open' " +
                "       END as request_status " +
                "from promo p " +
                "left join merchant_promo_request r on p.id=r.promo_id and r.merchant_id = "+ merchant.id +" " +
                "left join merchant_promo_request_product rp on rp.request_id=r.id and rp.status='P' " +
                "left join promo_product pp on pp.promo_id=p.id " +
                "left join product prod on pp.product_id=prod.id " +
                "where p.status = true " +
                "and p.is_deleted = false " +
                "and (r.merchant_id = "+ merchant.id +" or prod.merchant_id = "+ merchant.id +" or p.all_seller = true) " +
                "group by p.id, p.name " +
                ") as tbl  ";

        switch (type){
            case "open" :
                sql += " where request_status='open' "; break;
            case "review" :
                sql += " where request_status='review' "; break;
            case "expired" :
                sql += " where request_status='expired' "; break;
        }

        sql += "ORDER BY id DESC";

        RawSql rawSql = RawSqlBuilder.parse(sql)
                .columnMapping("id", "id")
                .columnMapping("name", "name")
                .columnMapping("image_url", "imageUrl")
                .columnMapping("active_from", "activeFrom")
                .columnMapping("active_to", "activeTo")
                .columnMapping("request_status", "requestStatus")
                .create();
        com.avaje.ebean.Query<Promo> query = Ebean.find(Promo.class);
        query.setRawSql(rawSql);
        List<Promo> resData = query.setMaxRows(limit).findList();
        for(int i=0; i<resData.size(); i++){
            resData.get(i).numberPending = getNumberOfRequestStatus(resData.get(i).id, merchant.id, MerchantPromoRequestProduct.STATUS_PENDING);
            resData.get(i).numberApproved = getNumberOfRequestStatus(resData.get(i).id, merchant.id, MerchantPromoRequestProduct.STATUS_APPROVED) +
                    getNumberOfApprovedProduct(resData.get(i).id, merchant.id);
            resData.get(i).numberRejected = getNumberOfRequestStatus(resData.get(i).id, merchant.id, MerchantPromoRequestProduct.STATUS_REJECTED);
        }
        int total = resData.size();

        BaseResponse<T> response = new BaseResponse<>();
        response.setData(new ObjectMapper().convertValue(resData, MapMyPromoMerchantList[].class));
        response.setMeta(total, offset, limit);
        response.setMessage("Success");

        return response;
    }

    public static <T> BaseResponse<T> getDataRequestProduct(Merchant merchant, String status, String sort, String filter, int offset, int limit)
            throws IOException {

        List<MerchantPromoRequestProduct> resData = MerchantPromoRequestProduct.find.where()
                .eq("product.merchant.id",merchant.id)
                .eq("status", status).findList();
        BaseResponse<T> response = new BaseResponse<>();
        int total = resData.size();
        response.setData(new ObjectMapper().convertValue(resData, MapPromoProductRequestStatus[].class));
        response.setMeta(total, offset, limit);
        response.setMessage("Success");

        return response;
    }

    public static <T> BaseResponse<T> getProductList(Long merchantId, Long promoId, String type, String sort, String filter, int offset, int limit)
            throws IOException {
        String sql = "select prod.id, prod.name, prod.sku, tbl.status, prod.price, prod.item_count " +
                "FROM product prod " +
                "LEFT JOIN ( " +
                "SELECT pp.product_id as id, 'A' as status " +
                "from promo_product pp " +
                "WHERE pp.promo_id= "+promoId+" " +
                "UNION " +
                "SELECT rp.product_id as id, rp.status as status " +
                "FROM merchant_promo_request_product rp " +
                "LEFT JOIN merchant_promo_request r on r.id=rp.request_id " +
                "WHERE r.promo_id = "+promoId+" ) tbl ON tbl.id = prod.id " +
                "WHERE " +
                "prod.is_deleted = false " +
                "AND prod.merchant_id = "+merchantId+" " +
                "AND prod.first_po_status = 1 " +
                "AND prod.approved_status = 'A' " +
                "AND prod.id NOT IN (select product_id from ( " +
                "select pp2.product_id from promo_product pp2  " +
                "left join promo p2 on p2.id = pp2.promo_id  " +
                "where p2.id <> "+promoId+" AND p2.status = true AND p2.active_to >= now() and p2.active_from <= now()  " +
                "union  " +
                "select rp2.product_id from merchant_promo_request_product rp2  " +
                "left join merchant_promo_request r2 on r2.id = rp2.request_id  " +
                "left join promo p22 on p22.id = r2.promo_id " +
                "where p22.id <> "+promoId+" AND p22.status = true AND p22.active_to >= now() and p22.active_from <= now()) tbl2) ";

        switch (type){
            case "reject" :
                sql += " and tbl.status='R' "; break;
            case "approve" :
                sql += " and tbl.status='A' "; break;
            case "review" :
                sql += " and tbl.status='P' "; break;
        }

        sql += "group by prod.id, prod.name, prod.sku, tbl.status ORDER BY tbl.status ASC";

        RawSql rawSql = RawSqlBuilder.parse(sql)
                .columnMapping("prod.id", "id")
                .columnMapping("prod.name", "name")
                .columnMapping("prod.sku", "sku")
                .columnMapping("tbl.status", "status")
                .columnMapping("prod.price", "price")
                .columnMapping("prod.item_count", "stock")
                .create();
        com.avaje.ebean.Query<MerchantPromoProductList> query = Ebean.find(MerchantPromoProductList.class);
        query.setRawSql(rawSql);
        List<MerchantPromoProductList> resData = query.findList();
        int total = resData.size();

        BaseResponse<T> response = new BaseResponse<>();
        response.setData(new ObjectMapper().convertValue(resData, MerchantPromoProductList[].class));
        response.setMeta(total, offset, limit);
        response.setMessage("Success");

        return response;
    }

    public static <T> BaseResponse<T> getAllProductList(Long promoId, String sort, String filter, int offset, int limit)
            throws IOException {
        String sql = "select prod.id, prod.name, prod.sku, prod.price, prod.item_count, prod.thumbnail_url, merchant_id, vendor_id " +
                "FROM product prod " +
                "INNER JOIN ( " +
                "SELECT pp.product_id as id " +
                "from promo_product pp " +
                "WHERE pp.promo_id= "+promoId+" " +
                "UNION " +
                "SELECT rp.product_id as id " +
                "FROM merchant_promo_request_product rp " +
                "LEFT JOIN merchant_promo_request r on r.id=rp.request_id " +
                "WHERE r.promo_id = "+promoId+" AND rp.status = 'A') tbl ON tbl.id = prod.id " +
                "WHERE " +
                "prod.is_deleted = false " +
                "AND prod.first_po_status = 1 " +
                "AND prod.approved_status = 'A' " +
                "ORDER BY prod.name ASC";

        RawSql rawSql = RawSqlBuilder.parse(sql)
                .columnMapping("prod.id", "id")
                .columnMapping("prod.name", "name")
                .columnMapping("prod.sku", "sku")
                .columnMapping("merchant_id", "merchant")
                .columnMapping("vendor_id", "vendor")
                .columnMapping("prod.thumbnail_url", "image")
                .columnMapping("prod.price", "price")
                .columnMapping("prod.item_count", "stock")
                .create();
        com.avaje.ebean.Query<PromoProductList> query = Ebean.find(PromoProductList.class);
        query.setRawSql(rawSql);
        List<PromoProductList> resData = query.findList();
        int total = resData.size();

        BaseResponse<T> response = new BaseResponse<>();
        response.setData(new ObjectMapper().convertValue(resData, PromoProductList[].class));
        response.setMeta(total, offset, limit);
        response.setMessage("Success");

        return response;
    }

    public static String applyProduct(Merchant merchant, MapMerchantPromoRequest map){
        Promo promo = Promo.find.byId(Long.parseLong(String.valueOf(map.getPromoId())));
        if (promo != null){
            if (map.getType().equals("delete")){
                for(MapMerchantPromoRequestProduct product : map.getProducts()){
                    MerchantPromoRequestProduct mprp = MerchantPromoRequestProduct.findProduct(promo.id, Long.valueOf(product.getProductId()));
                    if (mprp != null){
                        mprp.delete();
                    }
                }
            }else{
                for(MapMerchantPromoRequestProduct product : map.getProducts()){
                    if(checkValidProduct(Long.parseLong(String.valueOf(product.getProductId())))){
                        List<MerchantPromoRequest> request = MerchantPromoRequest.find.where()
                                .eq("promo.id", promo.id)
                                .eq("merchant.id", merchant.id)
                                .findList();
                        MerchantPromoRequest newReq = new MerchantPromoRequest();
                        if(request.size() == 0){
                            newReq = new MerchantPromoRequest();
                            newReq.promo = promo;
                            newReq.merchant = merchant;
                            newReq.save();
                        }else {
                            newReq = request.get(0);
                        }
                        Product prod = Product.find.byId(Long.parseLong(String.valueOf(product.getProductId())));
                        MerchantPromoRequestProduct newReqProduct = new MerchantPromoRequestProduct();
                        newReqProduct.request = newReq;
                        newReqProduct.product = prod;
                        newReqProduct.status = MerchantPromoRequestProduct.STATUS_PENDING;
                        newReqProduct.save();
                    }
                }
            }
        }

        return "";
    }

    private static boolean checkValidProduct(Long productId){
        String sql = "select product_id from ( " +
                "select pp.product_id from promo_product pp " +
                "left join promo p on p.id = pp.promo_id " +
                "where product_id = "+productId+" " +
                "and p.status = true AND p.active_to >= now() and p.active_from <= now() " +
                "union " +
                "select rp.product_id from merchant_promo_request_product rp " +
                "left join merchant_promo_request r on r.id = rp.request_id " +
                "left join promo p2 on p2.id = r.promo_id " +
                "where product_id = "+productId+" " +
                "and p2.status = true AND p2.active_to >= now() and p2.active_from <= now() " +
                ") tbl";
        RawSql rawSql = RawSqlBuilder.parse(sql)
                .columnMapping("product_id", "id")
                .create();
        com.avaje.ebean.Query<Product> query = Ebean.find(Product.class);
        query.setRawSql(rawSql);
        List<Product> resData = query.findList();

        return resData.size() == 0;

    }

    public static List<Product> getPromoProductList(Promo promo) {

        String sql = "";
        if(promo.products.size() > 0){
            sql = "select id,name " +
                    "from ( " +
                    "select p.id, p.name " +
                    "from product p " +
                    "LEFT JOIN category cat ON cat.id = p.category_id " +
                    "inner join promo_product pp on p.id=pp.product_id " +
                    "left join merchant m on m.id=p.merchant_id " +
                    "where pp.promo_id="+promo.id+
                    "union ALL " +
                    "select p.id, p.name " +
                    "from product p " +
                    "LEFT JOIN category cat ON cat.id = p.category_id " +
                    "inner join merchant_promo_request_product rp on p.id=rp.product_id " +
                    "INNER JOIN merchant_promo_request r on r.id=rp.request_id " +
                    "left join merchant m on m.id=p.merchant_id " +
                    "where r.promo_id=" + promo.id + " AND rp.status='A' " +
                    ")tbl " ;

        }else{
            sql = "select id,name " +
                    "from ( " +
                    "select p.id, p.name from product p " +
                    "LEFT JOIN category cat ON cat.id = p.category_id " +
                    "left join merchant m on m.id=p.merchant_id " ;
            String where = "";
            if(promo.merchants.size() > 0){
                where += "where p.merchant_id in (select merchant_id from promo_merchant where promo_id="+promo.id+") ";
            }
            if(promo.categories.size() > 0){
                if(where.equals(""))
                    where += " where ";
                else where += " and ";
                where += "p.category_id in (select category_id from promo_category where promo_id="+promo.id+") ";
            }
            if(promo.brands.size() > 0){
                if(where.equals(""))
                    where += " where ";
                else where += " and ";
                where += "p.brand_id in (select brand_id from promo_brand where promo_id="+promo.id+") ";
            }
            sql += " "+where+" ";
            sql += "union ALL " +
                    "select p.id, p.name " +
                    "from product p " +
                    "LEFT JOIN category cat ON cat.id = p.category_id " +
                    "inner join merchant_promo_request_product rp on p.id=rp.product_id " +
                    "INNER JOIN merchant_promo_request r on r.id=rp.request_id " +
                    "left join merchant m on m.id=p.merchant_id " +
                    "where r.promo_id=" + promo.id + " AND rp.status='A' " +
                    ")tbl ";
        }

        RawSql rawSql = RawSqlBuilder.parse(sql)
                .columnMapping("id", "id")
                .columnMapping("name", "name")
                .create();

        Query<Product> query = Ebean.find(Product.class);
        query.setRawSql(rawSql);

        List<Product> datas = query.findList();
        return datas;

    }
}