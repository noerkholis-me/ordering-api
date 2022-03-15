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
@Table(name = "shipper_city")
public class ShipperCity extends BaseModel{
    public static final boolean ACTIVE = true;
    public static final boolean INACTIVE = false;
    private static final String LOG_TYPE = "ADMIN";
    private static final String LOG_TABLE_NAME = "shipper_cities";

    private static final long serialVersionUID = 1L;

    
    @Column(name = "shipper_city_name")
    public String shipperCityname;

    @ManyToOne
    @JoinColumn(name = "province_id")
    public ShipperProvince shipperProvince;

    @Column(name = "province_name")
    public String provinceName;

    @JsonIgnore
    @JoinColumn(name="user_id")
    @ManyToOne
    public UserCms userCms;

    public ShipperCity() {
        
    }

    public ShipperCity(String shipperCityname) {
        super();
        this.shipperCityname = shipperCityname;
    }

    public static Finder<Long, ShipperCity> find = new Finder<Long, ShipperCity>(Long.class, ShipperCity.class);
}