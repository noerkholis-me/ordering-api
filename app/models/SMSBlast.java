package models;

import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Page;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hokeba.social.service.FirebaseService;
import com.hokeba.util.CommonFunction;
import org.elasticsearch.monitor.os.OsInfo;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by nugraha on 7/27/17.
 */
@Entity
@Table(name = "sms_blast")
public class SMSBlast extends BaseModel {
    private static final long serialVersionUID = 1L;
    public static final String FILTER_RECIPIENT_ALL = "A";
    public static final String FILTER_RECIPIENT_CUSTOM = "C";

    public static Finder<Long, SMSBlast> find = new Finder<>(Long.class, SMSBlast.class);

    public String title;
    public String recipient;
    @JsonProperty("filter_recipient")
    public String filterRecipient;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "Asia/Jakarta")
    public Date date;
    public String content;

    @JsonIgnore
    @ManyToMany
    public List<Member> members;

    @JsonIgnore
    @JoinColumn(name="user_id")
    @ManyToOne
    public UserCms userCms;

    @JsonIgnore
    @JoinColumn(name="template_id")
    @ManyToOne
    public SMSTemplate smsTemplate;

    @Column(name = "is_sent")
    public boolean isSent;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "sent_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "Asia/Jakarta")
    public Date sentAt;

    @javax.persistence.Transient
    public String save;

    @javax.persistence.Transient
    public List<String> member_list;

    @javax.persistence.Transient
    public String sendDate = "";

    @javax.persistence.Transient
    public String sendTime = "";

    @javax.persistence.Transient
    public String templateId = "";

    public static Page<SMSBlast> page(int page, int pageSize, String sortBy, String order, String name) {
        ExpressionList<SMSBlast> qry = SMSBlast.find
                .where()
                .ilike("title", "%" + name + "%")
                .eq("t0.is_deleted", false);

        return
                qry.orderBy(sortBy + " " + order)
                        .findPagingList(pageSize)
                        .setFetchAhead(false)
                        .getPage(page);

    }

    public static int findRowCount() {
        return
                find.where()
                        .eq("t0.is_deleted", false)
                        .findRowCount();
    }

    public String getFilterRecipientView(){
        String result = "";
        switch (filterRecipient){
            case FILTER_RECIPIENT_ALL : result = "All";break;
            case FILTER_RECIPIENT_CUSTOM : result = "Custom";break;
        }
        return result;
    }

    public String getTitle(){
        return title;
    }

    public String getDateView(){
        return CommonFunction.getDateTime2(date);
    }

    public String getRecipient() {
        return recipient;
    }

    public String getFilterRecipient() {
        return filterRecipient;
    }

    public Date getDate() {
        return date;
    }

    public String getContent() {
        return content;
    }

    public List<Member> getMembers() {
        return members;
    }

    public UserCms getUserCms() {
        return userCms;
    }

    public SMSTemplate getSmsTemplate() {
        return smsTemplate;
    }

    public List<String> getMember_list() {
        return member_list;
    }

    public String getSendDate() {
        return sendDate;
    }

    public String getSendTime() {
        return sendTime;
    }

    public String getTemplateId() {
        return templateId;
    }

    public boolean isSent() {
        return isSent;
    }

    public void setSent(boolean sent) {
        isSent = sent;
    }

    public Date getSentAt() {
        return sentAt;
    }

    public void setSentAt(Date sentAt) {
        this.sentAt = sentAt;
    }

    public static void insertNotif(Member member, String subject, String content){
        NotificationMember notif = new NotificationMember(member, 0, new Date(), subject, content);
        notif.save();

        for (MemberLog log : MemberLog.getListMemberLog(member)){
            try {
                FirebaseService.getInstance().sendNotificationTo(log.deviceId, notif.title, notif.content);
            }catch (Exception ignored){

            }
        }
    }

    public void sendNotif(){
        String message = "";
        if(smsTemplate != null){
            message = smsTemplate.content;
        }else message = this.content;

        if(FILTER_RECIPIENT_ALL.equals(filterRecipient)){
            List<Member> lists = Member.find.where().eq("isActive", Member.ACTIVE).findList();
            for(Member member : lists){
                insertNotif(member, title, message);
            }
        }else{
            for(Member member : members){
                insertNotif(member, title, message);
            }
        }
    }
}
