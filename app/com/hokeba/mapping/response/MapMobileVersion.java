package com.hokeba.mapping.response;

import java.util.Date;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class MapMobileVersion {
	@JsonProperty("mobile_version")
	private Integer mobileVersion;

	@JsonProperty("mobile_version_ios")
	private Integer mobileVersionIos;

	@JsonProperty("description")
	private String description;

	@JsonProperty("url_android")
	private String urlAndroid;
	
	@JsonProperty("url_ios")
	private String urlIOS;
	
	@JsonProperty("major_minor_update")
	private boolean majorMinorUpdate;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING,  pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone="Asia/Jakarta")
	@JsonProperty("release_date")
    private Date releaseDate;
	
	public boolean isMajorMinorUpdate() {
		return majorMinorUpdate;
	}

	public void setMajorMinorUpdate(boolean majorMinorUpdate) {
		this.majorMinorUpdate = majorMinorUpdate;
	}

	public Date getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(Date releaseDate) {
		this.releaseDate = releaseDate;
	}

	public Integer getMobileVersion() {
		return mobileVersion;
	}

	public Integer getMobileVersionIos() {
		return mobileVersionIos;
	}

	public void setMobileVersion(Integer mobileVersion) {
		this.mobileVersion = mobileVersion;
	}

	public void setMobileVersionIos(Integer mobileVersionIos) {
		this.mobileVersionIos = mobileVersionIos;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUrlAndroid() {
		return urlAndroid;
	}

	public void setUrlAndroid(String urlAndroid) {
		this.urlAndroid = urlAndroid;
	}

	public String getUrlIOS() {
		return urlIOS;
	}

	public void setUrlIOS(String urlIOS) {
		this.urlIOS = urlIOS;
	}
	
	
}
