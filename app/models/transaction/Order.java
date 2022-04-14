package models.transaction;

import com.avaje.ebean.annotation.CreatedTimestamp;
import lombok.Data;
import lombok.EqualsAndHashCode;
import models.BaseModel;
import models.Member;
import models.Merchant;
import models.Store;

import javax.persistence.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@EqualsAndHashCode(callSuper = false)
public class Order extends BaseModel {

    @Temporal(TemporalType.TIMESTAMP)
    @CreatedTimestamp
    @Column(name = "order_date")
    private Date orderDate;

    @Column(unique = true, name = "order_number")
    private String orderNumber;

    @Column(name = "order_type")
    private String orderType;

    @Column(name = "sub_total")
    private BigDecimal subTotal;

    @Column(name = "total_price")
    private BigDecimal totalPrice;

    @ManyToOne(cascade = { CascadeType.ALL })
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private Member member;

    private String status;

    @Column(name = "approved_by")
    private String approvedBy;

    @Temporal(TemporalType.TIMESTAMP)
    @CreatedTimestamp
    @Column(name = "approved_date")
    public Date approvedDate;

    // ================================================================ //

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderDetail> orderDetails;

    @OneToOne(mappedBy = "order")
    private OrderPayment orderPayment;

    // ================================================================ //

    public static Finder<Long, Order> find = new Finder<>(Long.class, Order.class);

    public static String getOrderNumber(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMM");
        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-MM");
        Order order = Order.find.where("t0.created_at > '"+simpleDateFormat2.format(new Date())+"-01 00:00:00'")
                .order("t0.created_at desc").setMaxRows(1).findUnique();
        String seqNum = "";
        if(order == null){
            seqNum = "00001";
        }else{
            seqNum = order.orderNumber.substring(order.orderNumber.length() - 5);
            int seq = Integer.parseInt(seqNum)+1;
            seqNum = "00000" + String.valueOf(seq);
            seqNum = seqNum.substring(seqNum.length() - 5);
        }
        String code = "SBX";
        code += simpleDateFormat.format(new Date()) + seqNum;
        return code;
    }


}
