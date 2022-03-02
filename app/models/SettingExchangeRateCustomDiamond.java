package models;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "setting_exchange_rate_custom_diamond")
public class SettingExchangeRateCustomDiamond extends BaseModel {
	
	@Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING,  pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone="Asia/Jakarta")
    public Date date;
	
	@JsonProperty("idr_rate")
	public Double idrRate;
	
	@JsonIgnore
	@JoinColumn(name = "user_id")
	@ManyToOne
	public UserCms userCms;
	
	public static Finder<Long, SettingExchangeRateCustomDiamond> find = new Finder<Long, SettingExchangeRateCustomDiamond>(Long.class, SettingExchangeRateCustomDiamond.class);

}
