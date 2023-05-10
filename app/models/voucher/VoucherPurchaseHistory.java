package models.voucher;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import models.BaseModel;
import models.Member;

@Entity
@Table(name = "voucher_purchase_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VoucherPurchaseHistory extends BaseModel{
    private static final long serialVersionUID = 1L;
    
    @Column(name = "price")
    private BigDecimal price;
    
    @ManyToOne
    @JoinColumn(name = "voucher_id", referencedColumnName = "id")
    @Getter
    @Setter
    private VoucherMerchant voucherId;
    
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @Getter
    @Setter
    private Member userId;
    
}
