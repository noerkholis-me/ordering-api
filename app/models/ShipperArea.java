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
@Table(name = "shipper_area")
public class ShipperArea extends BaseModel{
    public static final boolean ACTIVE = true;
    public static final boolean INACTIVE = false;
    private static final String LOG_TYPE = "ADMIN";
    private static final String LOG_TABLE_NAME = "shipper_area";

    private static final long serialVersionUID = 1L;

    
    @Column(name = "name")
    public String name;

    @Column(name = "alias")
    public String alias;

    @Column(name = "post_code")
    public String postCode;    

    @ManyToOne
    @JoinColumn(name = "suburb_id")
    public ShipperSuburb shipperSuburb;

    @JsonIgnore
    @JoinColumn(name="user_id")
    @ManyToOne
    public UserCms userCms;


    public ShipperArea() {
        
    }

    public ShipperArea(String name, String alias, String postCode) {
        super();
        this.name = name;
        this.alias = alias;
        this.postCode = postCode;
    }

    public static Finder<Long, ShipperArea> find = new Finder<Long, ShipperArea>(Long.class, ShipperArea.class);

    public static List<ShipperArea> findAllBySuburb(ShipperSuburb shipperSuburb) {
        return find.where().eq("shipperSuburb", shipperSuburb).findList();
    }

    public static List<ShipperArea> findAllBySuburbAndName(ShipperSuburb shipperSuburb, String name) {
        return find.where().eq("shipperSuburb", shipperSuburb).ilike("name", "%" + name + "%").findList();
    }

}