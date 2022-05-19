package models.internal;

import lombok.Data;
import lombok.EqualsAndHashCode;
import models.BaseModel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "payment_method")
@Data
@EqualsAndHashCode(callSuper = false)
public class PaymentMethod extends BaseModel {

    @Column(name = "payment_code")
    private String paymentCode;
    @Column(name = "payment_name")
    private String paymentName;
    @Column(name = "payment_fee_price")
    private BigDecimal paymentFeePrice;
    @Column(name = "payment_fee_percentage")
    private Double paymentFeePercentage;
    @Column(name = "is_available")
    private Boolean isAvailable;
    @Column(name = "is_active")
    private Boolean isActive;

    public static Finder<Long, PaymentMethod> find = new Finder<>(Long.class, PaymentMethod.class);

    public static List<PaymentMethod> findAllPaymentMethod() {
        return find.where().eq("isAvailable", true).findList();
    }


}
