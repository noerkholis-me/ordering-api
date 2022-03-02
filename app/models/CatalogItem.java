package models;

import java.io.IOException;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hokeba.util.Constant;

@Entity
public class CatalogItem extends BaseModel {
	private static final long serialVersionUID = 1L;
	public static Finder<Long, CatalogItem> find = new Finder<Long, CatalogItem>(Long.class, CatalogItem.class);
	
	@JsonProperty("is_active")
	public boolean isActive;
	
	@Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING,  pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone="Asia/Jakarta")
    @JsonProperty("active_from")
    public Date activeFrom;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING,  pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone="Asia/Jakarta")
    @JsonProperty("active_to")
    public Date activeTo;
    
    public int sequence;
    @JsonProperty("sequence_mobile")
    public int sequenceMobile;
	
	@JsonProperty("image_name")
	@Column(name = "image_name", columnDefinition = "TEXT")
	public String imageName;
	@JsonProperty("image_keyword")
	public String imageKeyword;
	@JsonProperty("image_title")
	public String imageTitle;
	@JsonProperty("image_description")
	@Column(name = "image_description", columnDefinition = "TEXT")
	public String imageDescription;
	
	@JsonProperty("image_url")
	public String imageUrl;
	@JsonProperty("image_url_responsive")
	public String imageUrlResponsive;
	@JsonIgnore
	public String imageSize;
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="catalog_id")
	public Catalog2 catalog;
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="product_id")
	public Product product;
	
	
	
	//TODO transient for form
	@Transient
	public String save;

	@Transient
	public Long catalogId;
	
	@Transient
	public String imageLink;

	@Transient
	public String imageLinkResponsive;
	
	@Transient
	public int imageUrlX;
	
	@Transient
	public int imageUrlY;
	
	@Transient
	public int imageUrlW;
	
	@Transient
	public int imageUrlH;

	@Transient
	public int imageUrlResponsiveX;
	
	@Transient
	public int imageUrlResponsiveY;
	
	@Transient
	public int imageUrlResponsiveW;
	
	@Transient
	public int imageUrlResponsiveH;
	
	//TODO getter
	@JsonGetter("is_active")
	public String getIsActive() {
		String statusName = "";
		if (isActive)
			statusName = "Active";
		else
			statusName = "Inactive";
		return statusName;
	}
	
	@JsonGetter("image_size")
	public int[] getImageSize() throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper om = new ObjectMapper();
		return (imageSize == null) ? null : om.readValue(imageSize, int[].class);
	}
	
	@JsonGetter("image_url")
	public String getImageUrl() {
		return imageUrl == null || imageUrl.isEmpty() ? "" : Constant.getInstance().getImageUrl() + imageUrl;
	}

	@JsonGetter("image_url_responsive")
	public String getImageUrlResponsive() {
		return imageUrlResponsive == null || imageUrlResponsive.isEmpty() ? ""
				: Constant.getInstance().getImageUrl() + imageUrlResponsive;
	}
	
	
	public void updateStatus(String newStatus) {
		if (newStatus.equals("1"))
			isActive = Catalog2.ACTIVE;
		else if (newStatus.equals("2"))
			isActive = Catalog2.INACTIVE;
		super.update();
	}
}
