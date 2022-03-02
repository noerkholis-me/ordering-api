package models;

import com.avaje.ebean.Expr;
import com.avaje.ebean.Page;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class ShippingCity extends BaseModel{
    private static final long serialVersionUID = 1L;

    @ManyToOne
    @JsonProperty("region_id")
    public Region region;

    @ManyToOne
    @JsonProperty("district_id")
    public District district;

    @ManyToOne
    @JsonProperty("township_id")
    public Township township;

    @ManyToOne
    @JsonProperty("village_id")
    public Village village;

    @JsonIgnore
    @JoinColumn(name="user_id")
    @ManyToOne
    public UserCms userCms;


    @javax.persistence.Transient
    public Long regionId;

    @javax.persistence.Transient
    public Long districtId;

    @javax.persistence.Transient
    public Long townshipId;

    @javax.persistence.Transient
    public Long villageId;

    @javax.persistence.Transient
    public String save;

    public static Finder<Long, ShippingCity> find = new Finder<>(Long.class, ShippingCity.class);

    public static Page<ShippingCity> page(int page, int pageSize, String sortBy, String order, String filter) {
        return
                find.where()
                        .or(Expr.or(Expr.ilike("region.name", "%" + filter + "%"), Expr.ilike("district.name", "%" + filter + "%")),Expr.or(Expr.ilike("township.name", "%" + filter + "%"), Expr.ilike("village.name", "%" + filter + "%")))
//                        .or(Expr.ilike("region.name", "%" + filter + "%"), Expr.ilike("district.name", "%" + filter + "%"))
                        .eq("t0.is_deleted", false)
                        .orderBy(sortBy + " " + order)
                        .findPagingList(pageSize)
                        .setFetchAhead(false)
                        .getPage(page);
    }

    public static Integer RowCount() {
        return find.where().eq("t0.is_deleted", false).findRowCount();
    }

    public static void seed(Region region, District district, Township township, Village village, UserCms user){
        ShippingCity model = new ShippingCity();
        model.region = region;
        model.district = district;
        model.township = township;
        model.village = village;
        model.isDeleted = false;
        model.userCms = user;
        model.save();
    }
}