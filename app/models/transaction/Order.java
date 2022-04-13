package models.transaction;

import com.avaje.ebean.annotation.CreatedTimestamp;
import lombok.Data;
import models.BaseModel;
import models.Member;
import models.UserMerchant;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "order")
@Data
public class Order extends BaseModel {

    @Temporal(TemporalType.TIMESTAMP)
    @CreatedTimestamp
    @Column(name = "order_date")
    private Date orderDate;

    @Column(unique = true, name = "order_number")
    private String orderNumber;

    @Column(unique = true, name = "order_type")
    private String orderType;

    @Column(name = "sub_total")
    private BigDecimal subTotal;

    @Column(name = "total_price")
    private BigDecimal totalPrice;

    @ManyToOne(cascade = { CascadeType.ALL })
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private Member member;

    private String status;

    @ManyToOne(cascade = { CascadeType.ALL })
    @JoinColumn(name = "user_merchant_id", referencedColumnName = "id")
    private UserMerchant userMerchant;

    @Column(name = "approved_by")
    private String approvedBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "approved_date")
    public Date approvedDate;

    // ================================================================ //

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderDetail> orderDetails;

    @OneToOne(mappedBy = "order")
    private OrderPayment orderPayment;


}
