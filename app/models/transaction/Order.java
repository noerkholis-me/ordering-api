package models.transaction;

import com.avaje.ebean.annotation.CreatedTimestamp;
import com.hokeba.util.CommonFunction;
import lombok.Data;
import lombok.EqualsAndHashCode;
import models.*;
import models.internal.PaymentMethod;
import models.internal.PaymentMethodConfig;
import models.merchant.TableMerchant;
import models.pupoint.PickUpPointMerchant;

import javax.persistence.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Data
@EqualsAndHashCode(callSuper = false)
public class Order extends BaseModel {

    public static final String NEW_ORDER = "NEW_ORDER";
    public static final String CANCELLED = "CANCELLED";
    public static final String PENDING = "PENDING";
    public static final String COMPLETE = "COMPLETE";

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

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "member_name")
    private String memberName;

    private String status;

    @Column(name = "approved_by")
    private String approvedBy;

    @Temporal(TemporalType.TIMESTAMP)
    @CreatedTimestamp
    @Column(name = "approved_date")
    public Date approvedDate;

    @Column(name = "order_queue")
    private Integer orderQueue;

    // ================================================================ //

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderDetail> orderDetails;

    @OneToOne(mappedBy = "order")
    private OrderPayment orderPayment;

    @ManyToOne(cascade = { CascadeType.ALL })
    @JoinColumn(name = "store_id", referencedColumnName = "id")
    private Store store;

    @ManyToOne(cascade = { CascadeType.ALL })
    @JoinColumn(name = "pickup_point_id", referencedColumnName = "id")
    private PickUpPointMerchant pickUpPointMerchant;

    @Column(name = "pickup_point_name")
    private String pickupPointName;

    @ManyToOne(cascade = { CascadeType.ALL })
    @JoinColumn(name = "table_id", referencedColumnName = "id")
    private TableMerchant tableMerchant;

    @javax.persistence.Transient
    public Long table_id;

    @javax.persistence.Transient
    public Long pickup_point_id;

    @Column(name = "table_name")
    private String tableName;
    
    @Column(name = "total_loyalty_usage")
    private BigDecimal totalLoyaltyUsage;

    @Column(name = "device_type")
    private String deviceType;

    @ManyToOne(cascade = { CascadeType.ALL })
    @JoinColumn(name = "user_merchant_id", referencedColumnName = "id")
    private UserMerchant userMerchant;

    @Column(name = "shipper_order_id")
    private String shipperOrderId;

    // ================================================================ //

    public static Finder<Long, Order> find = new Finder<>(Long.class, Order.class);

    public static String generateOrderNumber(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMDD");
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

    public String getTanggal(){
        return CommonFunction.getDate(this.orderDate);
    }

    public String getStatusOrder() {
        return this.status;
    }

    public String getNoInvoice() {
        return this.orderPayment.getInvoiceNo();
    }

    public String getNoTransaksi() {
        return this.orderNumber;
    }

    public String getTotalBayar() {
        return String.valueOf(this.totalPrice);
    }

    public String getJenisTransaksi() {
        String paymentType = this.orderPayment.getPaymentType();
        PaymentMethod paymentMethod = PaymentMethod.find.where().eq("paymentCode", paymentType).findUnique();
        return paymentMethod.getPaymentName();
    }

    public List<OrderDetail> getProductDetail() {
        return this.orderDetails;
    }


}
