package models;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.avaje.ebean.Query;

import dtos.voucher.CreateVoucherRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import play.db.ebean.Model.Finder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "voucher_how_to_use")
public class VoucherHowToUse extends BaseModel{

    private static final long serialVersionUID = 1L;

	public static Finder<Long, VoucherHowToUse> find = new Finder<>(Long.class, VoucherHowToUse.class);
	
	private String content;
	@OneToOne(cascade = { CascadeType.ALL })
    @JoinColumn(name = "voucher_id", referencedColumnName = "id")
	private VoucherMerchant voucher;
	
	public VoucherHowToUse (VoucherMerchant voucher, CreateVoucherRequest req) {
		this.voucher = voucher;
		this.content = req.getHowToUse();
	}
	
	public static VoucherHowToUse findByVoucherMerchant(VoucherMerchant voucher) {
		return find.where().eq("isDeleted", Boolean.FALSE).eq("voucher", voucher).findUnique();
	}
	

}
