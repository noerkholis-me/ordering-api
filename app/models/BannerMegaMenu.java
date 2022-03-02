package models;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Page;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hokeba.util.Constant;

//import controllers.admin.Banner;
import play.db.ebean.Model.Finder;

@Entity
@Table(name = "banner_megamenu")
public class BannerMegaMenu extends BaseModel{
	
	public String title;

    @JsonProperty("url")
	public String url;
    @JsonProperty("image_url")
	public String imageUrl;
    
    @javax.persistence.Transient
    public int imageUrlX;
    @javax.persistence.Transient
    public int imageUrlY;
    @javax.persistence.Transient
    public int imageUrlW;
    @javax.persistence.Transient
    public int imageUrlH;

	public static Finder<Long, BannerMegaMenu> find = new Finder<>(Long.class, BannerMegaMenu.class);
    
    public String getTitle() {
    	return this.title;
    }

    //get image string
    public String getImageUrl(){
        return getImageLink();
    }
    public String getImageLink(){
        return imageUrl==null || imageUrl.isEmpty() ? "" : Constant.getInstance().getImageUrl() + imageUrl;
    }
    
    public String getUrl() {
    	return this.url;
    }
    
    public static int findRowCount() {
    	return
                find.where()
                        .eq("is_deleted", false)
                .findRowCount();
    }
    
    public static Page<BannerMegaMenu> page(int page, int pageSize, String sortBy, String order, String title) {
        ExpressionList<BannerMegaMenu> qry = BannerMegaMenu.find
                .where()
                .ilike("title", "%" + title + "%")
                .eq("is_deleted", false);

//        switch (filter){
//            case 0: qry.eq("type_id", 0);
//                break;
//            case 1: qry.eq("type_id", 2);
//                break;
//            case 2: qry.eq("type_id", 1);
//                break;
//            case 3: qry.eq("status", true);
//                break;
//            case 4: qry.eq("status", false);
//                break;
//        }

        return
                qry.orderBy(sortBy + " " + order)
                        .findPagingList(pageSize)
                        .setFetchAhead(false)
                        .getPage(page);

    }
}
