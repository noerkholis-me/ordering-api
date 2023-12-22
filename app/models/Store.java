package models;

import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Page;
import com.avaje.ebean.Query;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hokeba.util.CommonFunction;
import com.hokeba.util.Constant;
import dtos.store.StoreRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import play.libs.Json;

import javax.persistence.*;
import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Yuniar Kurniawan on 21 Juli 2021.
 */
@NoArgsConstructor
@Entity
@Table(name = "store")
@Getter
@Setter
public class Store extends BaseModel {
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
    @JoinColumn(name = "user_id")
    @ManyToOne
    public UserCms userCms;

    @JsonIgnore
    @JoinColumn(name = "province_id")
    @ManyToOne
    public ShipperProvince shipperProvince;

    @JsonIgnore
    @JoinColumn(name = "shipper_city_id")
    @ManyToOne
    public ShipperCity shipperCity;

    @JsonIgnore
    @JoinColumn(name = "suburb_id")
    @ManyToOne
    public ShipperSuburb shipperSuburb;

    @JsonIgnore
    @JoinColumn(name = "area_id")
    @ManyToOne
    public ShipperArea shipperArea;

    @Column(name = "store_gmap")
    public String storeGmap;

    @Column(name = "store_long")
    public Double storeLongitude;

    @Column(name = "store_lat")
    public Double storeLatitude;

    @ManyToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "merchant_id", referencedColumnName = "id")
    @JsonIgnore
    public Merchant merchant;

    @Column(name = "store_qr_code")
    public String storeQrCode;

    @Column(name = "store_qr_code_static")
    public String storeQrCodeStatic;

    @Column(name = "is_active")
    public Boolean isActive;

    @Column(name = "active_balance")
    public BigDecimal activeBalance;

//    @OneToMany(mappedBy = "store")
//    private List<Order> orders;

    @Column(name = "store_logo")
    public String storeLogo;

    @Column(name = "store_alias")
    public String storeAlias;

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

    @Column(name = "status_open_store")
    public Boolean statusOpenStore;

    @Column(name = "open_at")
    public String openAt;

    @Column(name = "closed_at")
    public String closedAt;

    public Store(StoreRequest request, Merchant merchant) {
        this.setMerchant(merchant);
        this.setStoreName(request.getStoreName());
        this.setStoreAlias(slugGenerate(request.getStoreName()));
        this.setStorePhone(request.getStorePhone());
        this.setStoreAddress(request.getAddress());
        this.setStoreLogo(request.getStoreLogo());
        this.setIsActive(true);
        this.setStatusOpenStore(true);
        this.setShipperProvince(ShipperProvince.findById(request.getProvinceId()));
        this.setShipperCity(ShipperCity.findById(request.getCityId()));
        this.setShipperSuburb(ShipperSuburb.findById(request.getSuburbId()));
        this.setShipperArea(ShipperArea.findById(request.getAreaId()));
        this.setActiveBalance(BigDecimal.ZERO);
        this.setStoreCode(CommonFunction.generateRandomString(8));
        this.setStoreQrCode(Constant.getInstance().getFrontEndUrl().concat(this.getStoreCode()));

        this.setStoreGmap(request.getGoogleMapsUrl());
        String[] finalLotLang = getLongitudeLatitude(this.getStoreGmap());
        this.setStoreLatitude(Double.parseDouble(finalLotLang[0]));
        this.setStoreLongitude(Double.parseDouble(finalLotLang[1]));
    }

    public void setStore(StoreRequest request, Store store) {
        store.setStoreName(request.getStoreName());
        store.setStoreAlias(slugGenerate(request.getStoreName()));
        store.setStorePhone(request.getStorePhone());
        store.setStoreAddress(request.getAddress());
        store.setStoreLogo(request.getStoreLogo());
        store.setIsActive(true);
        store.setStatusOpenStore(request.getStatusOpenStore() != null && request.getStatusOpenStore());
        store.setOpenAt("".equals(request.getOpenAt()) ? null : request.getOpenAt());
        store.setClosedAt("".equals(request.getClosedAt()) ? null : request.getClosedAt());
        store.setShipperProvince(ShipperProvince.findById(request.getProvinceId()));
        store.setShipperCity(ShipperCity.findById(request.getCityId()));
        store.setShipperSuburb(ShipperSuburb.findById(request.getSuburbId()));
        store.setShipperArea(ShipperArea.findById(request.getAreaId()));
        store.setStoreQrCode(Constant.getInstance().getFrontEndUrl().concat(store.getStoreCode()));

        store.setStoreGmap(request.getGoogleMapsUrl());
        String[] finalLotLang = getLongitudeLatitude(store.getStoreGmap());
        store.setStoreLatitude(Double.parseDouble(finalLotLang[0]));
        store.setStoreLongitude(Double.parseDouble(finalLotLang[1]));
    }

    public static String[] getLongitudeLatitude(String paramGmap) {
        //String tmpString = "https://www.google.com/maps/place/Toko+Ne/@-6.9326603,107.6011616,515m/data=!3m1!1e3!4m13!1m7!3m6!1s0x2e68e899de51f023:0x40cea56365748dcf!2sAstanaanyar,+Bandung+City,+West+Java!3b1!8m2!3d-6.9299008!4d107.5993373!3m4!1s0x2e68e89eebc34b29:0x2e8c9826fb62b77e!8m2!3d-6.9327152!4d107.6020338";
        String[] tmpLongLat = paramGmap.split("@");
        String[] finalLotLang = tmpLongLat[1].split("/");
        return finalLotLang[0].split(",");
    }

    public static String slugGenerate(String input) {
        String slug = Normalizer.normalize(input.toLowerCase(), Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .replaceAll("[^\\p{Alnum}]+", "");
        if (slug.length() != 0 && slug.charAt(slug.length() - 1) == '-') {
            return slug.substring(0, slug.length() - 1);
        } else {
            return slug;
        }
    }

    public static Store findById(Long id) {
        return find.where().eq("id", id).eq("is_deleted", false).eq("isActive", true).findUnique();
    }

    public static Store findByStoreCode(String storeCode) {
        return find.where().eq("storeCode", storeCode).eq("is_deleted", false).findUnique();
    }

    public static Integer RowCount() {
        return find.where().eq("is_deleted", false).findRowCount();
    }

    public static Page<Store> page(int page, int pageSize, String sortBy, String order, String filter) {
        ExpressionList<Store> qry = Store.find.where()
                .ilike("storeName", "%" + filter + "%")
                .eq("is_deleted", false)
                .eq("is_active", true);

        return qry.orderBy(sortBy + " " + order)
                .findPagingList(pageSize)
                .setFetchAhead(false)
                .getPage(page);
    }

    public static Page<Store> getPage(String filter, String sort, int offset, int limit, Merchant merchant) {
        return find.where()
                .eq("merchant", merchant)
                .eq("is_deleted", false)
                .eq("is_active", true)
                .ilike("storeName", "%" + filter + "%")
                .orderBy(sort)
                .findPagingList(limit)
                .setFetchAhead(false)
                .getPage(offset);
    }

    public static List<Store> findAllStoreIsActiveByMerchant(Merchant merchant) {
        return find.where().eq("isDeleted", false).eq("merchant", merchant).eq("isActive", true).findList();
    }

    public static List<Store> findAllStoreByMerchant(Long merchantId) {
        return find.where()
                .eq("t0.is_deleted", false)
                .eq("t0.merchant_id", merchantId)
                .eq("t0.is_active", true)
                .orderBy().asc("t0.id")
                .findList();
    }

    public static List<Store> getTotalDataPage(Query<Store> reqQuery) {
        Query<Store> query = reqQuery;
        ExpressionList<Store> exp = query.where();
        query = exp.query();
        return query.findPagingList(0).getPage(0).getList();
    }

    public String getChangeLogData(Store data) {
        HashMap<String, String> map = new HashMap<>();
        map.put("store_code", (data.storeCode == null) ? "" : data.storeCode);

        return Json.toJson(map).toString();
    }

}