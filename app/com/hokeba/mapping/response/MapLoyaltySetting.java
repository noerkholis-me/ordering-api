package com.hokeba.mapping.response;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MapLoyaltySetting {
	@JsonProperty("start_date")
	public String startDate;
	
	@JsonProperty("end_date")
	public String endDate;
	
	@JsonProperty("value")
	public long value;

	@JsonProperty("expired_days")
	public int expiredDays;
	
	public MapLoyaltySetting(Date startDate, Date endDate, long value, int expiredDays) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		this.startDate = df.format(startDate);
		this.endDate = df.format(endDate);
		this.value = value;
		this.expiredDays = expiredDays;
	}
}
