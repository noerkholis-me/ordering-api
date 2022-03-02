package models;

import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Page;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Created by nugraha on 6/10/17.
 */
@Entity
public class CourierPointLocation extends BaseModel {
    private static final long serialVersionUID = 1L;

    public static Finder<Long, CourierPointLocation> find = new Finder<>(Long.class,
            CourierPointLocation.class);

    @Column(name = "point_name")
    public String name;

    @Column(name = "point_address")
    public String address;
    @Column(name = "agent_id")
    public Integer agentId;
    public Double longitude;
    public Double latitude;

    @JsonIgnore
    @ManyToOne
    public Township township;

    @JsonIgnore
    @ManyToOne
    public Courier courier;


    public static Page<CourierPointLocation> page(Long id, int page, int pageSize, String sortBy, String order, String name) {
        ExpressionList<CourierPointLocation> qry = CourierPointLocation.find
                .where()
                .eq("courier.id", id)
                .ilike("township.name", "%" + name + "%")
                .eq("t0.is_deleted", false);

        return
                qry.orderBy(sortBy + " " + order)
                        .findPagingList(pageSize)
                        .setFetchAhead(false)
                        .getPage(page);
    }

    public static int findRowCount(Long id) {
        return
                find.where()
                        .eq("courier.id", id)
                        .eq("t0.is_deleted", false)
                        .findRowCount();
    }
}
