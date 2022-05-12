package models.internal;

import lombok.Data;
import lombok.EqualsAndHashCode;
import models.BaseModel;
import models.UserCms;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;

@Entity
@Table(name = "fee_setting")
@Data
@EqualsAndHashCode(callSuper = false)
public class FeeSetting extends BaseModel {

    @Column(name = "platform_fee")
    private BigDecimal platformFee;

    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    @ManyToOne(cascade = { CascadeType.ALL })
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserCms userCms;

    public static Finder<Long, FeeSetting> find = new Finder<>(Long.class, FeeSetting.class);

    public static Optional<FeeSetting> findByLastUpdated() {
        return Optional.ofNullable(find.orderBy("date desc").findList().get(0));
    }

}
