package com.hokeba.mapping.response;

import java.util.Date;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by hendriksaragih on 3/2/17.
 */

@JsonIgnoreProperties(ignoreUnknown=true)
public class MapBanner {
    private String name;
    private String slug;
    private Integer sequence;
    private boolean flashSale;

    @JsonProperty("open_new_tab")
    private boolean openNewTab;
    @JsonProperty("link_url")
    private String linkUrl;
    @JsonProperty("image_url")
    private String imageLink;
    @JsonProperty("image_url_responsive")
    public String imageUrlResponsive;
    @JsonProperty("image_url_mobile")
    private String imageUrlMobile;

    @JsonProperty("caption1")
    private String caption1;
    @JsonProperty("caption2")
    private String caption2;
    
    @JsonProperty("meta_title")
    private String metaTitle;
    @JsonProperty("meta_keyword")
    private String metaKeyword;
    @JsonProperty("meta_description")
    private String metaDescription;
    @JsonProperty("product_detail")
    private Long productDetail;
    @JsonProperty("product_detail_slug")
    private String productDetailSlug;

	@Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING,  pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone="Asia/Jakarta")
    @JsonProperty("active_to")
    private Date activeTo;
    
    public boolean getFlashSale() {
		return flashSale;
	}

	public void setFlashSale(boolean flashSale) {
		this.flashSale = flashSale;
	}

	public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }


    public boolean isOpenNewTab() {
        return openNewTab;
    }

    public void setOpenNewTab(boolean openNewTab) {
        this.openNewTab = openNewTab;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public String getImageUrlResponsive() {
        return imageUrlResponsive;
    }

    public void setImageUrlResponsive(String imageUrlResponsive) {
        this.imageUrlResponsive = imageUrlResponsive;
    }

    public String getImageUrlMobile() {
		return imageUrlMobile;
	}

	public void setImageUrlMobile(String imageUrlMobile) {
		this.imageUrlMobile = imageUrlMobile;
	}

	public String getMetaTitle() {
        return metaTitle;
    }

    public void setMetaTitle(String metaTitle) {
        this.metaTitle = metaTitle;
    }

    public String getMetaKeyword() {
        return metaKeyword;
    }

    public void setMetaKeyword(String metaKeyword) {
        this.metaKeyword = metaKeyword;
    }

    public String getMetaDescription() {
        return metaDescription;
    }

    public void setMetaDescription(String metaDescription) {
        this.metaDescription = metaDescription;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public Long getProductDetail() {
        return productDetail;
    }

    public void setProductDetail(Long productDetail) {
        this.productDetail = productDetail;
    }

    public String getProductDetailSlug() {
        return productDetailSlug;
    }

    public void setProductDetailSlug(String productDetailSlug) {
        this.productDetailSlug = productDetailSlug;
    }

	public String getCaption1() {
		return caption1;
	}

	public void setCaption1(String caption1) {
		this.caption1 = caption1;
	}

	public String getCaption2() {
		return caption2;
	}

	public void setCaption2(String caption2) {
		this.caption2 = caption2;
	}
    
    public Date getActiveTo() {
		return activeTo;
	}

	public void setActiveTo(Date activeTo) {
		this.activeTo = activeTo;
	}
    
}
