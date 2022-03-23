package models;

import com.avaje.ebean.annotation.CreatedTimestamp;
import com.avaje.ebean.annotation.UpdatedTimestamp;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wordnik.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import play.db.ebean.Model;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by hendriksaragih on 2/4/17.
 */
@MappedSuperclass
public class BaseModel extends Model {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

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

    //	@JsonIgnore
//	@JsonProperty("is_deleted")
    @Column(name = "is_deleted")
    public boolean isDeleted;

    @ApiModelProperty(hidden = true)
    @Transient
    @JsonIgnore
    private int _ebean_intercept;// used to trick swagger to not viewing ebean
    // default model properties
}
