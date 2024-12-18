package models;
import com.hokeba.util.Helper;
import models.BaseModel;
import javax.persistence.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;
import models.transaction.Order;

@Entity
@Table(name = "loyalty_point_history")
public class LoyaltyHistory extends BaseModel {
    @Column(name = "member_id")
    public Integer memberId;

    @OneToOne(cascade = { CascadeType.ALL })
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    @Column(name = "order_id")
    public Order order;

    @Column(name = "point")
    public Integer point;

    @Column(name = "added")
    public Integer addedPoint;

    @Column(name = "used")
    public Integer usedPoint;
    
    @Column(name = "merchant_id")
    public Integer merchantId;

    @Column(name = "expired")
    public Date expired;
}
