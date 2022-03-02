package models;

import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Page;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hokeba.util.CommonFunction;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by hendriksaragih on 7/25/17.
 */
@Entity
public class SalesOrderReturnGroup extends BaseModel {
    private static final long serialVersionUID = 1L;
    public static Finder<Long, SalesOrderReturnGroup> find = new Finder<>(Long.class,
            SalesOrderReturnGroup.class);

    @JsonIgnore
    @ManyToOne
    public SalesOrder salesOrder;

    @Column(name = "return_number", unique = true, length = 30)
    public String returnNumber;

    @OneToMany(mappedBy = "salesOrderReturnGroup")
    @JsonProperty("returns")
    public List<SalesOrderReturn> salesOrderReturns;

    @JsonIgnore
    @ManyToOne
    public Member member;

    @javax.persistence.Transient
    @JsonProperty("order_no")
    public String getOrderNo(){
        return salesOrder.orderNumber;
    }
    @javax.persistence.Transient
    @JsonProperty("order_date")
    public String getOrderDate(){
        return salesOrder.getOrderDateString();
    }

    public String getDateFormated(){
        return CommonFunction.getDate(createdAt);
    }


    public static String generateReturnCode(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMM");
        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-MM");
        SalesOrderReturnGroup so = SalesOrderReturnGroup.find.where("created_at > '"+simpleDateFormat2.format(new Date())+"-01 00:00:00'")
                .order("created_at desc").setMaxRows(1).findUnique();
        String seqNum = "";
        if(so == null){
            seqNum = "0001";
        }else{
            seqNum = so.returnNumber.substring(so.returnNumber.length() - 4);
            int seq = Integer.parseInt(seqNum)+1;
            seqNum = "0000" + String.valueOf(seq);
            seqNum = seqNum.substring(seqNum.length() - 4);
        }
        String code = "RTSG";
        code += simpleDateFormat.format(new Date()) + seqNum;
        return code;
    }

    public static Page<SalesOrderReturnGroup> page(int page, int pageSize, String sortBy, String order, String name) {
        ExpressionList<SalesOrderReturnGroup> qry = SalesOrderReturnGroup.find
                .where()
                .ilike("returnNumber", "%" + name + "%")
                .eq("is_deleted", false);

        return
                qry.orderBy(sortBy + " " + order)
                        .findPagingList(pageSize)
                        .setFetchAhead(false)
                        .getPage(page);

    }

    public static int findRowCount() {
        return
                find.where()
                        .eq("is_deleted", false)
                        .findRowCount();
    }

}