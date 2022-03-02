package com.hokeba.mapping.response.kiosk;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class MapBannerForMobile {
    private String bannerName;
    private String imageName;
    private String imageMobileUrl;
    private String linkUrl;

    @JsonFormat(shape = JsonFormat.Shape.STRING,  pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone="Asia/Jakarta")
    private Date activeTo;

	public String getBannerName() {
		return bannerName;
	}

	public void setBannerName(String bannerName) {
		this.bannerName = bannerName;
	}

	public Date getActiveTo() {
		return activeTo;
	}

	public void setActiveTo(Date activeTo) {
		this.activeTo = activeTo;
	}

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	public String getImageMobileUrl() {
		return imageMobileUrl;
	}

	public void setImageMobileUrl(String imageMobileUrl) {
		this.imageMobileUrl = imageMobileUrl;
	}

	public String getLinkUrl() {
		return linkUrl;
	}

	public void setLinkUrl(String linkUrl) {
		this.linkUrl = linkUrl;
	}
	
}
