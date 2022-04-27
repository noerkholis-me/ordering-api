package models.internal;

import lombok.Data;
import lombok.EqualsAndHashCode;
import models.BaseModel;
import models.UserCms;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "fee_setting")
@Data
@EqualsAndHashCode(callSuper = false)
public class FeeSetting extends BaseModel {

    private BigDecimal platformFee;

    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    @ManyToOne(cascade = { CascadeType.ALL })
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserCms userCms;

}
