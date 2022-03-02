package models;

import com.avaje.ebean.annotation.CreatedTimestamp;
import com.avaje.ebean.annotation.UpdatedTimestamp;
import com.fasterxml.jackson.annotation.JsonFormat;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.Date;


/**
 * Created by nugraha on 7/27/17.
 */
@Entity
@Table(name = "sms_template")
public class SMSTemplate extends Model {
    @Id
    public String id;
    public String name;
    public String subject;
    public String content;

    @Temporal(TemporalType.TIMESTAMP)
    @CreatedTimestamp
    @Column(name = "created_at", updatable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "Asia/Jakarta")
    public Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @UpdatedTimestamp
    @Version
    @Column(name = "updated_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "Asia/Jakarta")
    public Date updatedAt;

    public static Finder<String, SMSTemplate> find = new Finder<String, SMSTemplate>(String.class, SMSTemplate.class);

}