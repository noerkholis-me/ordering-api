package com.hokeba.payment.midtrans.request;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hokeba.util.CommonFunction;

public class TransactionExpiry {
	private static final String defaultUnit = "minutes";
	
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss Z", timezone = "Asia/Jakarta")
	@JsonProperty("start_time")
	public Date start_time;
	@JsonProperty("unit")
	public String unit;
	@JsonProperty("duration")
	public int duration;
	
	public TransactionExpiry() {
		super();
	}
	
	public TransactionExpiry(Date orderDate, Date expiredDate, TimeUnit timeUnit) {
		long dateDiff = CommonFunction.getDateDifference(expiredDate, orderDate, timeUnit);
		this.start_time = orderDate;
		this.duration = new Long(dateDiff).intValue();
		this.unit = timeUnit.name().toLowerCase();
	}
}
