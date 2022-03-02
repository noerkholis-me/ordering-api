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
@Table(name = "currency_exchange_rate")
public class CurrencyExchangeRate extends Model {

    private static final long serialVersionUID = 1L;


    @Temporal(TemporalType.DATE)
    @Column(name = "date", updatable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Jakarta")
    public Date date;

    public String code;

    public Double rate;

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

}