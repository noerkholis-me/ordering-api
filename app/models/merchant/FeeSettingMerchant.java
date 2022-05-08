package models.merchant;

import lombok.Data;
import lombok.EqualsAndHashCode;
import models.BaseModel;
import models.Merchant;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "fee_setting_merchant")
@Data
@EqualsAndHashCode(callSuper = false)
public class FeeSettingMerchant extends BaseModel {

    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    private Double tax;

    private Double service;

    @Column(name = "platform_fee_type")
    private String platformFeeType;

    @Column(name = "payment_fee_type")
    private String paymentFeeType;

    @Column(name = "platform_fee")
    private BigDecimal platformFee;

    @Column(name = "payment_fee")
    private BigDecimal paymentFee;

    @Column(name = "updated_by")
    private String updatedBy;

    // ===================================================== //
    @ManyToOne(cascade = { CascadeType.ALL })
    @JoinColumn(name = "merchant_id", referencedColumnName = "id")
    private Merchant merchant;

}
