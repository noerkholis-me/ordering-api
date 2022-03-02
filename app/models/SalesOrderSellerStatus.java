package models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hokeba.util.CommonFunction;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by nugraha on 5/25/17.
 */
@Entity
public class SalesOrderSellerStatus extends BaseModel {
    private static final long serialVersionUID = 1L;

    public static Finder<Long, SalesOrderSellerStatus> find = new Finder<>(Long.class,
            SalesOrderSellerStatus.class);

    @JsonIgnore
    @ManyToOne
    public SalesOrderSeller salesOrderSeller;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "Asia/Jakarta")
    public Date date;

    @JsonProperty("product_name")
    public String description;
    public Integer type;

    public SalesOrderSellerStatus(SalesOrderSeller salesOrderSeller, Date date, String description) {
        this.salesOrderSeller = salesOrderSeller;
        this.date = date;
        this.description = description;
    }

    public SalesOrderSellerStatus(SalesOrderSeller salesOrderSeller, Date date, Integer type, String description) {
        this.salesOrderSeller = salesOrderSeller;
        this.date = date;
        this.description = description;
        this.type = type;
    }

    public SalesOrderSeller getSalesOrderSeller() {
        return salesOrderSeller;
    }

    public void setSalesOrderSeller(SalesOrderSeller salesOrderSeller) {
        this.salesOrderSeller = salesOrderSeller;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDateFormat() {
        return CommonFunction.getDateTime(date);
    }

    public static List<SalesOrderSellerStatus> getListBySalesOrderSeller(Long sosId){
        return find.where()
                .eq("isDeleted", false)
                .eq("salesOrderSeller.id", sosId)
                .order("date desc")
                .findList();
    }
}
