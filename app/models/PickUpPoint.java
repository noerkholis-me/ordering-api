package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import com.hokeba.mapping.request.MapPickUpPoint;

@Entity
public class PickUpPoint extends BaseModel {
	
	public static Finder<Long, PickUpPoint> find = new Finder<Long, PickUpPoint>(Long.class, PickUpPoint.class);

	@JsonIgnore
    @ManyToOne
    @JoinColumn(name="merchant_id")
    public Merchant merchant;

    @JsonProperty("name")
    public String name;
    
    @JsonProperty("address")
    public String address;

    @JsonProperty("contact")
    public String contact;

    @JsonProperty("duration")
    public Long duration;

    @JsonProperty("duration")
    public Double latitude;
    
    @JsonProperty("duration")
    public Double longitude;

	public PickUpPoint(Merchant merchant, String name, String address, String contact, Long duration,
			Double latitude,
			Double longitude) {
		super();
		this.merchant = merchant;
		this.name = name;
		this.address = address;
		this.contact = contact;
		this.duration = duration;
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	public PickUpPoint(Merchant merchant, String name, String address, String contact, Long duration) {
		super();
		this.merchant = merchant;
		this.name = name;
		this.address = address;
		this.contact = contact;
		this.duration = duration;
	}

	public PickUpPoint(MapPickUpPoint map) {
		super();
		this.merchant = merchant.find.byId(map.merchantId);
		this.name = map.name;
		this.address = map.address;
		this.contact = map.contact;
		this.duration = map.duration;
		this.latitude = map.latitude;
		this.longitude = map.longitude;
	}
	
	public void editPickUp(MapPickUpPoint map) {
		this.name = map.name;
		this.address = map.address;
		this.contact = map.contact;
		this.duration = map.duration;
		this.latitude = map.latitude;
		this.longitude = map.longitude;
	}
	
	public Merchant getMerchant() {
		return merchant;
	}

	public void setMerchant(Merchant merchant) {
		this.merchant = merchant;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public Long getDuration() {
		return duration;
	}

	public void setDuration(Long duration) {
		this.duration = duration;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
    
    
}
