package com.hokeba.mapping.request;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import models.LoyaltyPoint;

@JsonIgnoreProperties(ignoreUnknown=true)
public class MapLoyaltyPoint {
	@JsonProperty("point")
	private Long point;

	@JsonProperty("used")
	private Long used;

	@JsonProperty("note")
	private String note;

//	@JsonProperty("is_expired")
	@JsonIgnore
	private boolean isExpired;

	@JsonProperty("expired_date")
	private String expiredDate;

	@JsonProperty("created_at")
	private String createdAt;
//
//	public MapLoyaltyPoint(LoyaltyPoint loyaltyPoint) {
//		DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
//		Date date = new Date();
//		String expDateString = "";
//		
//		if(loyaltyPoint.expiredDate != null) {
//			expDateString = dateFormat.format(loyaltyPoint.expiredDate);
//		}
//		String createDateString = dateFormat.format(loyaltyPoint.createdAt);
//		
//		setPoint(loyaltyPoint.point);
//		setUsed(loyaltyPoint.used);
//		setNote(loyaltyPoint.note);
//		
//		if(expDateString.equals("")) {
//			this.setExpired(false);
//		}
//		else {
//			if(date.after(loyaltyPoint.expiredDate)) {
//				this.setExpired(true);
//			}
//			else {
//				this.setExpired(false);
//			}
//		}
//		setExpiredDate(expDateString);
//		setCreatedAt(createDateString);
//	}
	

    public MapLoyaltyPoint(){

    }

	public boolean isExpired() {
		return isExpired;
	}

	public void setExpired(boolean isExpired) {
		this.isExpired = isExpired;
	}

	public String getNote() {
		return note;
	}

	public Long getPoint() {
		return point;
	}

	public Long getUsed() {
		return used;
	}

	public String getExpiredDate() {
		return expiredDate;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setPoint(Long point) {
		this.point = point;
	}

	public void setUsed(Long used) {
		this.used = used;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public void setExpiredDate(String expiredDate) {
		this.expiredDate = expiredDate;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

}
