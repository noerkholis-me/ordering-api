package com.hokeba.mapping.response.kiosk;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class MapBannerKios {
    public String bannerName;
    public String imageName;
    public int sequence;
    public Integer positionId;
    public String slug;
    
    public String imagePromoPageUrl;
    public String imageHomePageUrl;

    @JsonFormat(shape = JsonFormat.Shape.STRING,  pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone="Asia/Jakarta")
    private Date activeTo;

	public String getBannerName() {
		return bannerName;
	}

	public void setBannerName(String bannerName) {
		this.bannerName = bannerName;
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public Integer getPositionId() {
		return positionId;
	}

	public void setPositionId(Integer positionId) {
		this.positionId = positionId;
	}

	public String getSlug() {
		return slug;
	}

	public void setSlug(String slug) {
		this.slug = slug;
	}

	public String getImageHomePageUrl() {
		return imageHomePageUrl;
	}

	public void setImageHomePageUrl(String imageHomePageUrl) {
		this.imageHomePageUrl = imageHomePageUrl;
	}

	public Date getActiveTo() {
		return activeTo;
	}

	public void setActiveTo(Date activeTo) {
		this.activeTo = activeTo;
	}
    
}
