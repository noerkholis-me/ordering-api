package models.finance;

import lombok.Data;
import lombok.EqualsAndHashCode;
import models.BaseModel;
import models.Store;

import javax.persistence.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
@Table(name = "finance_withdraw")
@Data
@EqualsAndHashCode(callSuper = false)
public class FinanceWithdraw extends BaseModel {

    public static final String WAITING_CONFIRMATION = "Waiting Confirmation";
    public static final String APPROVED = "Approved";

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

    @Column(name = "account_number")
    private String accountNumber;

    @ManyToOne(cascade = { CascadeType.ALL })
    @JoinColumn(name = "store_id", referencedColumnName = "id")
    private Store store;

    private static final Finder<Long, FinanceWithdraw> find = new Finder<>(Long.class, FinanceWithdraw.class);

    public static String generateRequestNumber(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMDD");
        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-MM");
        FinanceWithdraw withdraw = FinanceWithdraw.find.where("t0.created_at > '"+simpleDateFormat2.format(new Date())+"-01 00:00:00'")
                .order("t0.created_at desc").setMaxRows(1).findUnique();
        String seqNum = "";
        if(withdraw == null){
            seqNum = "00001";
        }else{
            seqNum = withdraw.requestNumber.substring(withdraw.requestNumber.length() - 5);
            int seq = Integer.parseInt(seqNum)+1;
            seqNum = "00000" + String.valueOf(seq);
            seqNum = seqNum.substring(seqNum.length() - 5);
        }
        String code = "WD";
        code += simpleDateFormat.format(new Date()) + seqNum;
        return code;
    }

}
