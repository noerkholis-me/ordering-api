package com.hokeba.mapping.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by hendriksaragih on 3/2/17.
 */

@JsonIgnoreProperties(ignoreUnknown=true)
public class MapMember {
    private Long id;
    @JsonProperty("full_name")
    public String fullName;
    public String username;
    public String email;
    public String phone;
    public String gender;
    @JsonProperty("birth_day")
    public String birthDay;
    @JsonProperty("news_letter")
    public Boolean newsLetter;
    @JsonProperty("billing_address")
    public MapAddress billingAddress;
    @JsonProperty("shipping_address")
    public MapAddress shippingAddress;
    public List<MapOrderUser> orders;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Boolean getNewsLetter() {
        return newsLetter;
    }

    public void setNewsLetter(Boolean newsLetter) {
        this.newsLetter = newsLetter;
    }

    public MapAddress getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(MapAddress billingAddress) {
        this.billingAddress = billingAddress;
    }

    public MapAddress getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(MapAddress shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(String birthDay) {
        this.birthDay = birthDay;
    }

    public List<MapOrderUser> getOrders() {
        return orders;
    }

    public void setOrders(List<MapOrderUser> orders) {
        this.orders = orders;
    }

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
}
