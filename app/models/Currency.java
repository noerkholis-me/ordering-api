package models;

import com.avaje.ebean.annotation.CreatedTimestamp;
import com.avaje.ebean.annotation.UpdatedTimestamp;
import com.fasterxml.jackson.annotation.JsonFormat;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by hendriksaragih on 2/4/17.
 */
@Entity
@Table(name = "currency")
public class Currency extends Model {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "code")
    public String code;

    @Column(name = "code_display")
    public String codeDisplay;

    public String name;

    public int sequence;

    @Temporal(TemporalType.TIMESTAMP)
    // @Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
    @CreatedTimestamp
    @Column(name = "created_at", updatable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "Asia/Jakarta")
    public Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    // @Formats.DateTime(pattern = "yyyy-MM-dd HH:mm:ss")
    @UpdatedTimestamp
    @Version
    @Column(name = "updated_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "Asia/Jakarta")
    public Date updatedAt;

    public Currency(String code, String codeDisplay, String name, int sequence) {
        this.code = code;
        this.codeDisplay = codeDisplay;
        this.name = name;
        this.sequence = sequence;
        createdAt = new Date();
        updatedAt = new Date();
    }

    public static Finder<String, Currency> find = new Finder<String, Currency>(String.class, Currency.class);
}