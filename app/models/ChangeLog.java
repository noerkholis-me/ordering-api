package models;

import com.fasterxml.jackson.annotation.JsonProperty;
import play.db.ebean.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;
import java.util.UUID;

/**
 * Created by hendriksaragih on 2/4/17.
 */
@Entity
@Table(name = "change_log")
public class ChangeLog extends Model {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public String id;
    @JsonProperty("action_date")
    public Date actionDate;
    public String type;
    @JsonProperty("user_id")
    public Long userId;
    @JsonProperty("table_id")
    public String tableName;
    @JsonProperty("item_id")
    public Long itemId;
    public String action;
    @Column(columnDefinition = "TEXT")
    public String before;
    @Column(columnDefinition = "TEXT")
    public String after;

    public ChangeLog(String type, Long userId, String tableName, Long itemId, String action, String before, String after) {
        this.id = UUID.randomUUID().toString();
        this.actionDate = new Date();
        this.type = type;
        this.userId = userId;
        this.tableName = tableName;
        this.itemId = itemId;
        this.action = action;
        this.before = before;
        this.after = after;
    }

    public static Finder<Long, ChangeLog> find = new Finder<Long, ChangeLog>(Long.class, ChangeLog.class);

}