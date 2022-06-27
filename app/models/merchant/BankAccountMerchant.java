package models.merchant;

import lombok.Data;
import lombok.EqualsAndHashCode;
import models.BaseModel;
import models.Merchant;

import javax.persistence.*;

@Entity
@Table(name = "bank_account_merchant")
@Data
@EqualsAndHashCode(callSuper = false)
public class BankAccountMerchant extends BaseModel {

    @Column(name = "bank_name")
    private String bankName;

    @Column(name = "account_number")
    private String accountNumber;

    @Column(name = "account_name")
    private String accountName;

    @Column(name = "is_primary")
    private Boolean isPrimary;

    // ============================================================ //

    @ManyToOne(cascade = { CascadeType.ALL })
    @JoinColumn(name = "merchant_id", referencedColumnName = "id")
    public Merchant merchant;

}
