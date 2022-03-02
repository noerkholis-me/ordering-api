package models;

import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Page;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Created by hendriksaragih on 4/26/17.
 */
@Entity
public class CourierService extends BaseModel {
    private static final long serialVersionUID = 1L;

    public static Finder<Long, CourierService> find = new Finder<>(Long.class,
            CourierService.class);

    public String service;

    @JsonIgnore
//    @ManyToOne(cascade = { CascadeType.ALL })
    @ManyToOne
    public Courier courier;



    public static Page<CourierService> page(Long id, int page, int pageSize, String sortBy, String order, String name) {
        ExpressionList<CourierService> qry = CourierService.find
                .where()
                .eq("courier.id", id)
                .ilike("service", "%" + name + "%")
                .eq("is_deleted", false);

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
                        .eq("is_deleted", false)
                        .findRowCount();
    }
}
