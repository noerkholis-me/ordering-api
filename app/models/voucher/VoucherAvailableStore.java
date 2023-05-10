package models.voucher;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import models.BaseModel;
import models.Member;
import models.Store;

@Entity
@Table(name = "voucher_available_store")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VoucherAvailableStore{
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
    @ManyToOne
    @JoinColumn(name = "voucher_id", referencedColumnName = "id")
    @Getter
    @Setter
    private VoucherMerchant voucherId;
    
    @ManyToOne
    @JoinColumn(name = "store_id", referencedColumnName = "id")
    @Getter
    @Setter
    private Store storeId;
	
}
