package models;

import com.avaje.ebean.annotation.CreatedTimestamp;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hokeba.social.service.FirebaseService;
import com.hokeba.util.CommonFunction;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by nugraha on 5/24/17.
 */
@Entity
@Table(name = "notification_merchant")
public class NotificationMerchant extends BaseModel{
    public static final Integer TYPE_NEW_ORDER = 1;
    public static final Integer TYPE_OUT_OF_STOCK = 2;
    public static final Integer TYPE_RECEIVED_ORDER = 3;

    @JsonIgnore
    @JoinColumn(name="merchant_id")
    @ManyToOne
    public Merchant merchant;
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

    public static Finder<Long, NotificationMerchant> find = new Finder<>(Long.class, NotificationMerchant.class);


    public NotificationMerchant(Merchant merchant, Integer tipe, Date date, String title, String content) {
        this.merchant = merchant;
        this.tipe = tipe;
        this.date = date;
        this.title = title;
        this.content = content;
        isRead = false;
    }

    public static void insertNotif(Merchant merchant, Integer tipe, String title, String content){
        NotificationMerchant notif = new NotificationMerchant(merchant, tipe, new Date(), title, content);
        notif.save();

        for (MerchantLog log : MerchantLog.getListMerchantLog(merchant)){
            try {
                FirebaseService.getInstance().sendNotificationTo(log.deviceId, notif.title, notif.content);
            }catch (Exception ignored){

            }
        }
    }

    public String getDate(){
        return CommonFunction.getDateTime(date);
    }
}
