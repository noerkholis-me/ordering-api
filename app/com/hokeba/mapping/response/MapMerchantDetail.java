package com.hokeba.mapping.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by hendriksaragih on 3/2/17.
 */

@JsonIgnoreProperties(ignoreUnknown=true)
public class MapMerchantDetail {
    private Long id;
    private String name;
    private String code;
    private String type;
    public Double rating;
    public String address;
    public String phone;
    @JsonProperty("city_name")
    public String cityName;
    @JsonProperty("postal_code")
    public String postalCode;
    public String province;
    public String email;
    public String logo;
    @JsonProperty("count_rating")
    public int countRating;

    @JsonProperty("rating_stat")
    public MapProductRatting ratingStat;
    @JsonProperty("seller_reviews")
    public List<MapProductReview> sellerReviews;
    @JsonProperty("order_stat")
    public List<MapKeyValue> orderStat;
    @JsonProperty("couriers")
    public List<MapCourier> couriers;
    @JsonProperty("payment_method")
    public List<MapPaymentMethod> paymentMethods;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public int getCountRating() {
        return countRating;
    }

    public void setCountRating(int countRating) {
        this.countRating = countRating;
    }

    public MapProductRatting getRatingStat() {
        return ratingStat;
    }

    public void setRatingStat(MapProductRatting ratingStat) {
        this.ratingStat = ratingStat;
    }

    public List<MapProductReview> getSellerReviews() {
        return sellerReviews;
    }

    public void setSellerReviews(List<MapProductReview> sellerReviews) {
        this.sellerReviews = sellerReviews;
    }

    public List<MapKeyValue> getOrderStat() {
        return orderStat;
    }

    public void setOrderStat(List<MapKeyValue> orderStat) {
        this.orderStat = orderStat;
    }

    public List<MapCourier> getCouriers() {
        return couriers;
    }

    public void setCouriers(List<MapCourier> couriers) {
        this.couriers = couriers;
    }

    public List<MapPaymentMethod> getPaymentMethods() {
        return paymentMethods;
    }

    public void setPaymentMethods(List<MapPaymentMethod> paymentMethods) {
        this.paymentMethods = paymentMethods;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }
}
