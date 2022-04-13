package models.transaction;

import lombok.Data;
import models.BaseModel;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "xendit_payment")
@Data
public class XenditPayment extends BaseModel {

    @Column(name = "order_number")
    private String orderNumber;

    @Column(name = "reference_id")
    private String referenceId;

    @Column(name = "status")
    private String status;

    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "creation_time")
    private Date creationTime;

    @Column(name = "qr_code")
    private String qrCode;


    // ============================================================= //
    @OneToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "order_payment_id", referencedColumnName = "id")
    private OrderPayment orderPayment;

}
