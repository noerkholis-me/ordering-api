package models;

import com.avaje.ebean.annotation.CreatedTimestamp;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hokeba.social.service.FirebaseService;
import com.hokeba.util.CommonFunction;
import com.hokeba.util.StaticText;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by nugraha on 5/24/17.
 */
@Entity
@Table(name = "notification_member")
public class NotificationMember extends BaseModel{
    public static final Integer TYPE_ORDER_RECEIVED = 1;
    public static final Integer TYPE_PAYMENT_CONFIRMED = 2;
    public static final Integer TYPE_SHIPPING = 3;
    public static final Integer TYPE_DELIVERED = 4;
    public static final Integer TYPE_RETUR_QC = 5;
    public static final Integer TYPE_RETUR_REPLACED = 6;
    public static final Integer TYPE_RETUR_REFUND = 7;
    public static final Integer TYPE_RETUR_REJECT = 8;

    @JsonIgnore
    @JoinColumn(name="member_id")
    @ManyToOne
    public Member member;
    public Integer tipe;

    @Temporal(TemporalType.TIMESTAMP)
    @CreatedTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "Asia/Jakarta")
    public Date date;
    public String title;
    public String content;
    @Column(name = "is_read")
    @JsonProperty("is_read")
    public Boolean isRead;

    public static Finder<Long, NotificationMember> find = new Finder<>(Long.class, NotificationMember.class);


    public NotificationMember(Member member, Integer tipe, Date date, String title, String content) {
        this.member = member;
        this.tipe = tipe;
        this.date = date;
        this.title = title;
        this.content = content;
        isRead = false;
    }

    public String getDate(){
        return CommonFunction.getDateTime(date);
    }

    public static void insertNotif(Member member, Integer tipe, String content){
        NotificationMember notif = new NotificationMember(member, tipe, new Date(), getTitle(tipe), content);
        notif.save();

        for (MemberLog log : MemberLog.getListMemberLog(member)){
            try {
                FirebaseService.getInstance().sendNotificationTo(log.deviceId, notif.title, notif.content);
            }catch (Exception ignored){

            }
        }
    }

    public static void insertNotif(SalesOrder so){
        insertNotif(so, TYPE_ORDER_RECEIVED);
    }

    public static void insertNotif(SalesOrder so, int type){
        String content = StaticText.getInstance().getTextNotif(type);
        insertNotif(so.member, type, content.replaceAll("\\{OrderID}", so.orderNumber));
    }

    private static String getTitle(int tipe){
        switch (tipe){
            case 1 : return "Order Received";
            case 2 : return "Payment Confirmed";
            case 3 : return "Shipping";
            case 4 : return "Delivered";
            case 5 : return "Retur QC";
            case 6 :
            case 7 :
                return "Retur Accept";
            case 8 : return "Retur Reject";
        }

        return "";
    }
}
