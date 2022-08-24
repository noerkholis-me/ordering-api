package models.transaction;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import models.BaseModel;

import javax.persistence.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
@Table(name = "order_payment")
@Data
@EqualsAndHashCode(callSuper = false)
public class OrderPayment extends BaseModel {

    public static final String PENDING = "PENDING";
    public static final String PAID = "PAID";
    public static final String UNPAID = "UNPAID";
    public static final String CANCELLED = "CANCELLED";

    @Column(unique = true, name = "invoice_no")
    private String invoiceNo;

    @Column(name = "status")
    private String status;

    @Column(name = "payment_type")
    private String paymentType;

    @Column(name = "payment_channel")
    private String paymentChannel;

    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    @Column(name = "payment_date")
    private Date paymentDate;

    @Column(name = "tax_percentage")
    private Double taxPercentage;

    @Column(name = "service_percentage")
    private Double servicePercentage;

    @Column(name = "tax_price")
    private BigDecimal taxPrice;

    @Column(name = "service_price")
    private BigDecimal servicePrice;

    @Column(name = "payment_fee_type")
    private String paymentFeeType;

    @Column(name = "payment_fee_customer")
    private BigDecimal paymentFeeCustomer;

    @Column(name = "payment_fee_owner")
    private BigDecimal paymentFeeOwner;

    @Column(name = "mail_status_code")
    private String mailStatusCode;

    @Column(name = "mail_status")
    private String mailStatus;

    @Column(name = "mail_message")
    private String mailMessage;


    // ============================================================ //

    @OneToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    private Order order;

    @OneToOne(mappedBy = "orderPayment")
    private PaymentDetail paymentDetail;

    public static Finder<Long, OrderPayment> find = new Finder<>(Long.class, OrderPayment.class);

    public static String generateInvoiceCode(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMM");
        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-MM");
        OrderPayment so = OrderPayment.find.where("t0.created_at > '"+simpleDateFormat2.format(new Date())+"-01 00:00:00' and invoice_no is not null")
                .order("t0.created_at desc").setMaxRows(1).findUnique();
        String seqNum = "";
        if(so == null){
            seqNum = "00001";
        }else{
            seqNum = so.invoiceNo.substring(so.invoiceNo.length() - 5);
            int seq = Integer.parseInt(seqNum)+1;
            seqNum = "00000" + String.valueOf(seq);
            seqNum = seqNum.substring(seqNum.length() - 5);
        }
        String code = "INV";
        code += simpleDateFormat.format(new Date()) + seqNum;
        return code;
    }

}
