package models.merchant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import models.BaseModel;
import models.Store;
import models.UserMerchant;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "cashier_history_merchant")
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class CashierHistoryMerchant extends BaseModel {

    @Column(name = "session_code")
    private String sessionCode;

    @Column(name = "start_total_amount")
    private BigDecimal startTotalAmount;

    @Column(name = "end_total_amount")
    private BigDecimal endTotalAmount;

    @Column(name = "start_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startTime;

    @Column(name = "end_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endTime;

    @Column(name = "is_active")
    private Boolean isActive;

    @ManyToOne(cascade = { CascadeType.ALL })
    @JoinColumn(name = "user_merchant_id", referencedColumnName = "id")
    public UserMerchant userMerchant;

    @ManyToOne(cascade = { CascadeType.ALL })
    @JoinColumn(name = "store_id", referencedColumnName = "id")
    public Store store;

    private static final Finder<Long, CashierHistoryMerchant> find = new Finder<>(Long.class, CashierHistoryMerchant.class);

    public static String generateSessionCode(Long userMerchantId, Long storeId){
        CashierHistoryMerchant cashier = find.where()
                .eq("isActive", true)
                .eq("userMerchant.id", userMerchantId)
                .eq("store.id", storeId)
                .findUnique();

        String seqNum = "";
        if(cashier == null){
            seqNum = "00001";
        }else{
            seqNum = cashier.sessionCode.substring(cashier.sessionCode.length() - 5);
            int seq = Integer.parseInt(seqNum)+1;
            seqNum = "00000" + String.valueOf(seq);
            seqNum = seqNum.substring(seqNum.length() - 5);
        }
        String code = "POS";
        code += String.valueOf(storeId) + seqNum;
        return code;
    }


}
