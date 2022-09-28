package models.transaction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import models.BaseModel;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "shipper_order_status")
public class ShipperOrderStatus extends BaseModel {

    private static final long serialVersionUID = 1L;


    @JsonIgnore
    @ManyToOne
    public Order order;

    public String status;
    public String notes;
    public Long order_id;


    public static Model.Finder<Long, ShipperOrderStatus> find = new Model.Finder<Long, ShipperOrderStatus>(Long.class, ShipperOrderStatus.class);
}
