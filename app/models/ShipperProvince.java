package models;

import com.avaje.ebean.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import play.libs.Json;
import javax.persistence.*;
import javax.validation.constraints.Size;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.HashMap;

/**
 * Created by Yuniar Kurniawan on 10 Agustus 2021.
 */
@Entity
@Table(name = "shipper_province")
public class ShipperProvince extends BaseModel{
    public static final boolean ACTIVE = true;
    public static final boolean INACTIVE = false;
    private static final String LOG_TYPE = "ADMIN";
    private static final String LOG_TABLE_NAME = "shipper_province";

    private static final long serialVersionUID = 1L;

    //@OneToMany(mappedBy="shipperProvince")
    //public List<Store> stores;

    @JsonIgnore
    @JoinColumn(name="user_id")
    @ManyToOne
    public UserCms userCms;

    @Column(name = "shipper_province_name")
    public String shipperProvincename;

    public ShipperProvince() {
        
    }

    public ShipperProvince(Integer shipperProvinceId, String shipperProvincename) {
        super();
        this.shipperProvincename = shipperProvincename;
    }

    public static Finder<Long, ShipperProvince> find = new Finder<Long, ShipperProvince>(Long.class, ShipperProvince.class);
}