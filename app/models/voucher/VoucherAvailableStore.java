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

import java.util.List;

@Entity
@Table(name = "voucher_available_store")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VoucherAvailableStore extends BaseModel{

    private static final long serialVersionUID = 1L;
    public static Finder<Long, VoucherAvailableStore> find = new Finder<>(Long.class, VoucherAvailableStore.class);
	
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

    public static List<VoucherAvailableStore> findAllByStore (Store store) {
        return VoucherAvailableStore.find.where().eq("is_deleted", false).eq("storeId", store).findList();
    }

    public static List<VoucherAvailableStore> findAllByVoucherId (VoucherMerchant voucher) {
        return VoucherAvailableStore.find.where().eq("is_deleted", false).eq("voucherId", voucher).findList();
    }

	
}
