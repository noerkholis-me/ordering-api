package models.transaction;

import lombok.Data;
import lombok.EqualsAndHashCode;
import models.BaseModel;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "payment_detail")
@Data
@EqualsAndHashCode(callSuper = false)
public class PaymentDetail extends BaseModel {

    @Column(name = "order_number")
    private String orderNumber;

    // id from external or third party payment
    @Column(name = "reference_id")
    private String referenceId;

    @Column(name = "status")
    private String status;

    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    @Column(name = "payment_channel")
    private String paymentChannel;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "creation_time")
    private Date creationTime;

    @Column(name = "qr_code", columnDefinition = "TEXT")
    private String qrCode;

    @Column(name = "account_number")
    private String accountNumber;


    // ============================================================= //
    @OneToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "order_payment_id", referencedColumnName = "id")
    private OrderPayment orderPayment;

}
