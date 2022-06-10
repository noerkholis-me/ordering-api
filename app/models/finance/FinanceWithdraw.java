package models.finance;

import lombok.Data;
import lombok.EqualsAndHashCode;
import models.BaseModel;
import models.Store;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "finance_withdraw")
@Data
@EqualsAndHashCode(callSuper = false)
public class FinanceWithdraw extends BaseModel {

    @Column(name = "event_id")
    private String eventId;

    @Column(name = "request_number")
    private String requestNumber;

    @Column(name = "date")
    private Date date;

    @Column(name = "status")
    private String status;

    @Column(name = "amount")
    private BigDecimal amount;

    @ManyToOne(cascade = { CascadeType.ALL })
    @JoinColumn(name = "store_id", referencedColumnName = "id")
    private Store store;

}
