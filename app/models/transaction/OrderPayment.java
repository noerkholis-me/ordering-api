package models.transaction;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import models.BaseModel;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "order_payment")
@Data
public class OrderPayment extends BaseModel {

    @Column(unique = true)
    @JsonProperty("invoice_no")
    private String invoiceNo;

    @Column(name = "status")
    private String status;

    @Column(name = "payment_type")
    private String paymentType;

    @Column(name = "payment_channel")
    private String paymentChannel;

    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    @Column(name = "payment_created_at")
    private Date paymentCreatedAt;




    // ============================================================ //

    @OneToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    private Order order;

    @OneToOne(mappedBy = "orderPayment")
    private XenditPayment xenditPayment;

}
