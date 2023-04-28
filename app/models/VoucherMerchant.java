package models;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.avaje.ebean.Expr;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Query;
import com.fasterxml.jackson.annotation.JsonIgnore;

import dtos.voucher.CreateVoucherRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Entity
@Table(name = "voucher_merchant_new")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VoucherMerchant extends BaseModel{

    private static final long serialVersionUID = 1L;
    public static final String PERCENT = "PERCENT";
    public static final String NOMINAL = "NOMINAL";
    private static final String VOUCHER_TYPE_FREE_DELIVERY = "FREE DELIVERY";
    private static final String VOUCHER_TYPE_DISCOUNT = "DISCOUNT";
	
	public static Finder<Long, VoucherMerchant> find = new Finder<>(Long.class, VoucherMerchant.class);
	
	@Column(name = "available")
	private boolean isAvailable;
	@Column(name = "value")
	private BigDecimal value;
	@Column(name = "value_text")
	private String valueText;
	@Column(name = "purchase_price")
	private BigDecimal purchasePrice;
	@Column(name = "expiry_days")
	private int expiryDay;
	@Column(name = "tittle")
	private String name;
	@Column(name = "code")
	private String code;
	@Column(name = "description")
	private String description;
	@Column(name = "voucher_type")
	private String voucherType;
	@ManyToOne(cascade = { CascadeType.ALL })
    @JoinColumn(name = "merchant_id", referencedColumnName = "id")
    @JsonIgnore
    @Getter
    @Setter
	private Merchant merchant;
	
	public VoucherMerchant (CreateVoucherRequest request, Merchant merchant) {
		this.isAvailable = Boolean.TRUE;
		this.value = new BigDecimal(request.getValue());
		this.valueText = request.getValueText().equalsIgnoreCase(PERCENT) ? PERCENT : NOMINAL;
		this.voucherType = request.getVoucherType().equalsIgnoreCase(VOUCHER_TYPE_FREE_DELIVERY) ? VOUCHER_TYPE_FREE_DELIVERY : VOUCHER_TYPE_DISCOUNT;
		this.purchasePrice = new BigDecimal(request.getPurchasePrice());
		this.expiryDay = request.getExpiryDay();
		this.name = request.getName();
		this.code = request.getCode();
		this.description = request.getDescription();
		this.merchant = merchant;
	}
	
	public static VoucherMerchant findById(Long id) {
        return find.where().eq("id", id).eq("isDeleted", false).eq("available", true).findUnique();
    }
	
	public static Query<VoucherMerchant> findAllVoucerFromAllMerchant() {
        return find.where().eq("isDeleted", false).order("id");
    }
	
	public static Query<VoucherMerchant> findAllVoucherMerchantAvailableAndMerchant(Merchant merchant) {
		return find.where().eq("isDeleted", Boolean.FALSE).eq("merchant", merchant).order("id");
	}
	
	public static List<VoucherMerchant> getTotalDataPage (Query<VoucherMerchant> reqQuery) {
        Query<VoucherMerchant> query = reqQuery;
        ExpressionList<VoucherMerchant> exp = query.where();
        query = exp.query();
        return query.findPagingList(0).getPage(0).getList();
    }
	
	public static List<VoucherMerchant> findVoucherMerchantWithPaging(Query<VoucherMerchant> reqQuery, 
			String sort, String filter, int offset, int limit) {
        Query<VoucherMerchant> query = reqQuery;

        if (!"".equals(sort)) {
            query = query.orderBy(sort);
        } else {
            query = query.orderBy("t0.created_at desc");
        }

        ExpressionList<VoucherMerchant> exp = query.where();
        exp = exp.disjunction();
        exp = exp.or(Expr.ilike("name", "%" + filter + "%"), Expr.ilike("code", "%" + filter + "%"));
        query = exp.query();
        if (limit != 0) {
            query = query.setMaxRows(limit);
        }
        return query.findPagingList(limit).getPage(offset).getList();
    }
}
