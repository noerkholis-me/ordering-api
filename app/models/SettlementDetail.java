package models;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
public class SettlementDetail extends BaseModel {
//public class SettlementDetail {
	private static final long serialVersionUID = 1L;

	public static Finder<Long, SettlementDetail> find = new Finder<>(Long.class, SettlementDetail.class);
	
	@JsonProperty("account_number")
	public String accountNumber;
	@JsonProperty("account_alias")
	public String accountAlias;
	@JsonProperty("amount")
    public Double amount;
	
	@ManyToOne
	@JoinColumn(name = "merchant_id", referencedColumnName = "id")
	@JsonIgnore
	public Merchant merchant;
	
	@ManyToOne
	@JoinColumn(name = "settlement_id", referencedColumnName = "id")
	@JsonIgnore
	public Settlement settlement;
	
	@JsonGetter("merchant_name")
	public String getMerchantName() {
		return merchant == null ? null : merchant.name; 
	}
}
