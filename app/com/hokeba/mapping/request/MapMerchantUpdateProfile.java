package com.hokeba.mapping.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by hendriksaragih on 7/12/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MapMerchantUpdateProfile {
    private String password;
    @JsonProperty("confirm_password")
    private String confirmPassword;
    @JsonProperty("full_name")
    private String fullName;
    private String name;
    private String gender;
    private String domain;
    private String phone;
    private String address;
    @JsonProperty("postal_code")
    private String postalCode;
    private String url;
    @JsonProperty("birth_date")
    private String birthDate;
    
    @JsonProperty("account_number")
    private String accountNumber;
    @JsonProperty("account_alias")
    private String accountAlias;
    
    @JsonProperty("region_id")
    private Long regionId;
    @JsonProperty("district_id")
    private Long districtId;
    @JsonProperty("township_id")
    private Long townshipId;
    @JsonProperty("village_id")
    private Long villageId;
    @JsonProperty("pickup_point")
    private Long pickupPoint;
    private List<MapShippingId> shippings;
    @JsonProperty("photo")
    private String photo;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender == null ? "" : gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDomain() {
        return domain == null ? "" : domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getPhone() {
        return phone != null ? phone : "";
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBirthDate() {
        return birthDate != null ? birthDate : "";
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public Long getRegionId() {
        return regionId;
    }

    public void setRegionId(Long regionId) {
        this.regionId = regionId;
    }

    public Long getDistrictId() {
        return districtId;
    }

    public void setDistrictId(Long districtId) {
        this.districtId = districtId;
    }

    public Long getTownshipId() {
        return townshipId;
    }

    public void setTownshipId(Long townshipId) {
        this.townshipId = townshipId;
    }

    public Long getVillageId() {
        return villageId;
    }

    public void setVillageId(Long villageId) {
        this.villageId = villageId;
    }

    public List<MapShippingId> getShippings() {
        return shippings;
    }

    public void setShippings(List<MapShippingId> shippings) {
        this.shippings = shippings;
    }

    public Long getPickupPoint() {
        return pickupPoint;
    }

    public void setPickupPoint(Long pickupPoint) {
        this.pickupPoint = pickupPoint;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

	public String getAddress() {
		return address == null ? "" : address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPostalCode() {
		return postalCode == null ? "" : postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getUrl() {
		return url == null ? "" : url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getAccountAlias() {
		return accountAlias;
	}

	public void setAccountAlias(String accountAlias) {
		this.accountAlias = accountAlias;
	}
    
    
}
