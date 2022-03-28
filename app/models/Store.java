package models;

import com.avaje.ebean.*;
import com.avaje.ebean.Query;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import play.libs.Json;
import javax.persistence.*;
import javax.validation.constraints.Size;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.HashMap;
import play.data.validation.ValidationError;

/**
 * Created by Yuniar Kurniawan on 21 Juli 2021.
 */
@Entity
@Table(name = "store")
public class Store extends BaseModel{
    public static final boolean ACTIVE = true;
    public static final boolean INACTIVE = false;
    private static final String LOG_TYPE = "ADMIN";
    private static final String LOG_TABLE_NAME = "store";

    private static final long serialVersionUID = 1L;

    @Column(name = "store_code")
    public String storeCode;

    @Column(name = "store_name")
    public String storeName;

    @Column(name = "store_address")
    public String storeAddress;

    @Column(name = "store_phone")
    public String storePhone;

    @JsonIgnore
    @JoinColumn(name="user_id")
    @ManyToOne
    public UserCms userCms;

    @JsonIgnore
    @JoinColumn(name="province_id")
    @ManyToOne
    public ShipperProvince shipperProvince;

    @JsonIgnore
    @JoinColumn(name="shipper_city_id")
    @ManyToOne
    public ShipperCity shipperCity;    

    @JsonIgnore
    @JoinColumn(name="suburb_id")
    @ManyToOne
    public ShipperSuburb shipperSuburb;

    @JsonIgnore
    @JoinColumn(name="area_id")
    @ManyToOne
    public ShipperArea shipperArea;    

    @Column(name="store_gmap")
    @Getter @Setter
    public String storeGmap;

    @Column(name="store_long")
    public Double storeLongitude;

    @Column(name="store_lat")
    public Double storeLatitude;

    @ManyToOne(cascade = { CascadeType.ALL })
    @JoinColumn(name = "merchant_id", referencedColumnName = "id")
    @JsonIgnore
    @Getter
    @Setter
    public Merchant merchant;

    @Column(name = "store_qr_code")
    @Getter @Setter
    public String storeQrCode;

    @Column(name = "is_active")
    public Boolean isActive;

    public static Finder<Long, Store> find = new Finder<Long, Store>(Long.class, Store.class);

    @javax.persistence.Transient
    public String save;

    @javax.persistence.Transient
    public Long province_id;

    @javax.persistence.Transient
    public Long city_id;

    @javax.persistence.Transient
    public Long suburb_id;

    @javax.persistence.Transient
    public Long area_id;

    public Store() {

    }

    public Store(String storeCode, String storeName, String storeAddress, String storePhone) {
        super();
        this.storeCode = storeCode;
        this.storeName = storeName;
        this.storeAddress = storeAddress;
        this.storePhone = storePhone;
        //this.isActive = true;

    }

    // public Store(String storeCode, String storeName, String storeAddress, 
    //     ShipperProvince shipperProvince, ShipperCity shipperCity, String storePhone) {
    //     super();
    //     this.storeCode = storeCode;
    //     this.storeName = storeName;
    //     this.storeAddress = storeAddress;
    //     this.shipperProvince = shipperProvince;
    //     this.shipperCity = shipperCity;
    //     this.storePhone = storePhone;
    //     //this.isActive = true;

    // }

    public static Store findById(Long id) {
        return find.where().eq("id", id).findUnique();
    }

    public static Store findByStoreCode(String storeCode) {
        return find.where().eq("storeCode", storeCode).findUnique();
    }

    public static Integer RowCount() {
        return find.where()
                        .eq("is_deleted", false)
                        .findRowCount();
    }

    public static Page<Store> page(int page, int pageSize, String sortBy, String order, String filter) {
        ExpressionList<Store> qry = Store.find
                .where()
                .ilike("storeName", "%" + filter + "%")
                .eq("is_deleted", false)
                .eq("is_active", true);

        return
                qry.orderBy(sortBy + " " + order)
                    .findPagingList(pageSize)
                    .setFetchAhead(false)
                    .getPage(page);
    }

    public static Page<Store> getPage(String filter, String sort, int offset, int limit, Merchant merchant) {
        return find.where()
                .eq("merchant", merchant)
                .ilike("storeName", "%" + filter + "%")
                .orderBy(sort)
                .findPagingList(limit)
                .setFetchAhead(false)
                .getPage(offset);
    }

    public String getChangeLogData(Store data){
        HashMap<String, String> map = new HashMap<>();
        map.put("store_code",(data.storeCode == null)? "":data.storeCode);
        
        return Json.toJson(map).toString();
    }

//    @Override
//    public void save() {
//        super.save();
//        ChangeLog changeLog = new ChangeLog(LOG_TYPE, this.userCms.id, LOG_TABLE_NAME, this.id, "ADD", null, getChangeLogData(this));
//        changeLog.save();
//    }
}