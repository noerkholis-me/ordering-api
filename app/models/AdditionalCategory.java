package models;

import com.avaje.ebean.Page;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hokeba.util.Constant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.beans.Transient;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nugraha on 6/9/17.
 */
@Entity
public class AdditionalCategory extends BaseModel{
    private static final long serialVersionUID = 1L;
    public static final boolean ACTIVE = true;
    public static final boolean INACTIVE = false;
    private static final String LOG_TYPE = "ADMIN";
    private static final String LOG_TABLE_NAME = "additional_category";

    public boolean status;

    @ManyToOne
    @JsonProperty("master_id")
    public AdditionalCategoryMaster master;

    @JsonIgnore
    @ManyToOne
    public Product product;

    @JsonProperty("additional_image_name")
    @Column(name = "additional_image_name", columnDefinition = "TEXT")
    public String additionalImageName;
    @JsonProperty("additional_image_keyword")
    public String additionalImageKeyword;
    @JsonProperty("additional_image_title")
    public String additionalImageTitle;
    @JsonProperty("additional_image_description")
    @Column(name = "additional_image_description", columnDefinition = "TEXT")
    public String additionalImageDescription;

    @JsonProperty("image_url")
    public String imageUrl;

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

    @javax.persistence.Transient
    public List<String> product_list;

    @Transient
    @JsonProperty("user_creator")
    public String getUserCreator(){
        return userCms.email;
    }

    @javax.persistence.Transient
    public String tmp_log_data;

    public String getImage() {
        return Images.getImage(imageUrl);
    }

    public static Finder<Long, AdditionalCategory> find = new Finder<Long, AdditionalCategory>(Long.class, AdditionalCategory.class);

    public static Page<AdditionalCategory> page(Long id, int page, int pageSize, String sortBy, String order, String filter) {
        return
                find.where()
                        .ilike("product.name", "%" + filter + "%")
                        .eq("t0.is_deleted", false)
                        .eq("master.id", id)
                        .orderBy(sortBy + " " + order)
                        .findPagingList(pageSize)
                        .setFetchAhead(false)
                        .getPage(page);
    }

    public static BannerList getDetails(String slug){
        List<AdditionalCategory> datas = AdditionalCategory.find
                .where()
                .eq("t0.is_deleted", false)
                .eq("status", true)
                .eq("master.slug", slug)
                .findList();
        List<Product> results = new ArrayList<>();
        for (AdditionalCategory data : datas){
            results.add(data.product);
        }

        return new BannerList(results);
    }

    public static int findRowCount(Long id) {
        return
                find.where()
                        .eq("t0.is_deleted", false)
                        .eq("master.id", id)
                .findRowCount();
    }

    public String getImageLink(){
        return imageUrl==null || imageUrl.isEmpty() ? "" : Constant.getInstance().getImageUrl() + imageUrl;
    }
    /*
    @Override
    public void save() {
        super.save();
        ChangeLog changeLog = new ChangeLog(LOG_TYPE, this.userCms.id, LOG_TABLE_NAME, this.id, "ADD", null, getChangeLogData(this));
        changeLog.save();
    }

    @Override
    public void update() {
        AdditionnalCategory oldPromo = AdditionnalCategory.find.byId(id);
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
            status = newStatus;

        super.update();

        ChangeLog changeLog;
        changeLog = new ChangeLog(LOG_TYPE, this.userCms.id, LOG_TABLE_NAME, this.id, "EDIT", oldPromoData, getChangeLogData(this));
        changeLog.save();

    }*/

}