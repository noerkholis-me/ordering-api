package models.voucher;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;
import com.avaje.ebean.SqlRow;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import models.BaseModel;
import models.Member;
import models.Store;

import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "voucher_available_store")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VoucherAvailableStore extends BaseModel{

    private static final long serialVersionUID = 1L;
    public static Finder<Long, VoucherAvailableStore> find = new Finder<>(Long.class, VoucherAvailableStore.class);
	
    @ManyToOne(targetEntity = VoucherMerchant.class)
    @JoinColumn(name = "voucher_id", referencedColumnName = "id")
    @Getter
    @Setter
    private VoucherMerchant voucherId;
    
    @ManyToOne(targetEntity = Store.class)
    @JoinColumn(name = "store_id", referencedColumnName = "id")
    @Getter
    @Setter
    private Store storeId;

    public static List<VoucherAvailableStore> findAllByStore (Store store) {
        return VoucherAvailableStore.find.where().eq("isDeleted", false).eq("storeId", store).findList();
    }

    public static List<VoucherAvailableStore> findAllByVoucherId (VoucherMerchant voucher) {
        return VoucherAvailableStore.find.where().eq("isDeleted", false).eq("voucherId", voucher).findList();
    }

    public static VoucherAvailableStore findByStoreAndMerchant (VoucherMerchant voucherMerchant, Store store) {
        return VoucherAvailableStore.find.where().eq("voucherId", voucherMerchant)
                .eq("storeId", store).findUnique();
    }

    public static List<VoucherAvailableStore> findAllStoreNotInList (List<Store> stores, VoucherMerchant voucher) {
        return VoucherAvailableStore.find.where().eq("isDeleted", false).eq("voucherId", voucher).not(Expr.in("storeId", stores)).findList();
    }

	
}
