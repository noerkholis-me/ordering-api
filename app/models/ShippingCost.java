package models;

import com.avaje.ebean.Expr;
import com.avaje.ebean.Page;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.util.List;

@Entity
public class ShippingCost extends BaseModel{
    private static final long serialVersionUID = 1L;

    @ManyToOne
    @JsonProperty("courier_id")
    public Courier courier;

    @ManyToOne
    @JsonProperty("township_from_id")
    public Township townshipFrom;

    @ManyToOne
    @JsonProperty("township_to_id")
    public Township townshipTo;

    @JsonIgnore
    @JoinColumn(name="user_id")
    @ManyToOne
    public UserCms userCms;

    @OneToMany(mappedBy="shippingCost")
    public List<ShippingCostDetail> details;

    @javax.persistence.Transient
    public Long courierId;

    @javax.persistence.Transient
    public Long regionFromId;

    @javax.persistence.Transient
    public Long districtFromId;

    @javax.persistence.Transient
    public Long townshipFromId;

    @javax.persistence.Transient
    public Long regionToId;

    @javax.persistence.Transient
    public Long districtToId;

    @javax.persistence.Transient
    public Long townshipToId;

    @javax.persistence.Transient
    public List<Long> detailId;

    @javax.persistence.Transient
    public List<Long> type;

    @javax.persistence.Transient
    public List<String> description;

    @javax.persistence.Transient
    public List<Double> cost;

    @javax.persistence.Transient
    public List<Integer> estimated;

    @Transient
    public String save;

    public static Finder<Long, ShippingCost> find = new Finder<>(Long.class, ShippingCost.class);

    public static Page<ShippingCost> page(int page, int pageSize, String sortBy, String order, String filter) {
        return
                find.where()
                        .or(Expr.ilike("townshipFrom.name", "%" + filter + "%"), Expr.ilike("townshipTo.name", "%" + filter + "%"))
                        .eq("t0.is_deleted", false)
                        .orderBy(sortBy + " " + order)
                        .findPagingList(pageSize)
                        .setFetchAhead(false)
                        .getPage(page);
    }

    public static Integer RowCount() {
        return find.where().eq("is_deleted", false).findRowCount();
    }

    public static void seed(Courier courier, Township townshipFrom, Township townshipTo, UserCms user){
        ShippingCost model = new ShippingCost();
        model.courier = courier;
        model.townshipFrom = townshipFrom;
        model.townshipTo = townshipTo;
        model.isDeleted = false;
        model.userCms = user;
        model.save();

        ShippingCostDetail.seed(model, courier.services.get(0), "", 700D, 1);
        ShippingCostDetail.seed(model, courier.services.get(1), "", 1200D, 5);
    }

    public static ShippingCost getShippingCost(Courier courier, Township townshipFrom, Township townshipTo){
        if (courier.deliveryType.equals(Courier.DELIVERY_TYPE_PICK_UP_POINT) || courier.name.equalsIgnoreCase("Pay on Delivery")){
            return ShippingCost.find.where().eq("courier", courier).
                    eq("isDeleted", false).setMaxRows(1).findUnique();
        }else{
            ShippingCost model = ShippingCost.find.where().eq("courier", courier).eq("townshipFrom", townshipFrom)
                    .eq("townshipTo", townshipTo).eq("isDeleted", false).setMaxRows(1).findUnique();

            if (model == null){
                model = ShippingCost.find.where().eq("courier", courier).eq("townshipTo", townshipFrom)
                        .eq("townshipFrom", townshipTo).eq("isDeleted", false).setMaxRows(1).findUnique();
            }
            return model;
        }

    }
}