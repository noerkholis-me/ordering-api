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
 * Created by Yuniar Kurniawan on 13 Agustus 2021.
 */
@Entity
@Table(name = "shipper_suburb")
public class ShipperSuburb extends BaseModel{
    public static final boolean ACTIVE = true;
    public static final boolean INACTIVE = false;
    private static final String LOG_TYPE = "ADMIN";
    private static final String LOG_TABLE_NAME = "shipper_suburb";

    private static final long serialVersionUID = 1L;

    
    @Column(name = "name")
    public String name;

    @Column(name = "alias")
    public String alias;

    @ManyToOne
    @JoinColumn(name = "city_id")
    public ShipperCity shipperCity;

    @JsonIgnore
    @JoinColumn(name="user_id")
    @ManyToOne
    public UserCms userCms;

    public ShipperSuburb() {
        
    }

    public ShipperSuburb(String name,String alias) {
        super();
        this.name = name;
        this.alias = alias;
    }

    public static Finder<Long, ShipperSuburb> find = new Finder<Long, ShipperSuburb>(Long.class, ShipperSuburb.class);
}