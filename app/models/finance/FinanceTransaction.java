package models.finance;

import lombok.Data;
import lombok.EqualsAndHashCode;
import models.BaseModel;
import models.Store;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "finance_transaction")
@Data
@EqualsAndHashCode(callSuper = false)
public class FinanceTransaction extends BaseModel {

    @Column(name = "event_id")
    private String eventId;

    @Column(name = "reference_number")
    private String referenceNumber;

    @Column(name = "date")
    private Date date;

    @Column(name = "transaction_type")
    private String transactionType;

    @Column(name = "status")
    private String status;

    @Column(name = "amount")
    private BigDecimal amount;

    @ManyToOne(cascade = { CascadeType.ALL })
    @JoinColumn(name = "store_id", referencedColumnName = "id")
    private Store store;

}
