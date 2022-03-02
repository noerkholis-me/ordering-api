package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import play.db.ebean.Model.Finder;

@Entity
public class Settlement extends BaseModel {
//public class Settlement {
	private static final long serialVersionUID = 1L;

	public static Finder<Long, Settlement> find = new Finder<>(Long.class, Settlement.class);

	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "Asia/Jakarta")
	@JsonProperty("start_date")
	public Date startDate;
	
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "Asia/Jakarta")
	@JsonProperty("end_date")
	public Date endDate;
	
	@JsonProperty("status_read")
	public boolean statusRead;
	
	@JsonProperty("status_print")
	public boolean statusPrint;
	
	@JsonProperty("status_complete")
	public boolean statusComplete;
	
	@JsonProperty("total_merchant")
	public int totalMerchant;
	
	@JsonProperty("total_settlement")
	public Double totalSettlement;
	
	@OneToMany(mappedBy = "settlement")
	@JsonIgnore
	public List<SettlementDetail> details;
	
}
