package com.hokeba.mapping.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import models.Courier;

import java.util.List;

/**
 * Created by nugraha on 5/25/17.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class MapOrderShippingCourier {
    @JsonProperty("courier_id")
    private Long courierId;
    @JsonProperty("courier_name")
    private String courierName;
    @JsonProperty("courier_image")
    private String courierImage;
    @JsonProperty("courier_type")
    private String courierType;
    private List<MapOrderShippingCourierService> services;

    public MapOrderShippingCourier(){

    }
    public MapOrderShippingCourier(Courier c){
        courierId = c.id;
        courierName = c.name;
        courierImage = c.getImageLink();
        courierType = c.getDeliveryType();
    }

    public Long getCourierId() {
        return courierId;
    }

    public void setCourierId(Long courierId) {
        this.courierId = courierId;
    }

    public String getCourierName() {
        return courierName;
    }

    public void setCourierName(String courierName) {
        this.courierName = courierName;
    }

    public String getCourierImage() {
        return courierImage;
    }

    public void setCourierImage(String courierImage) {
        this.courierImage = courierImage;
    }

    public List<MapOrderShippingCourierService> getServices() {
        return services;
    }

    public void setServices(List<MapOrderShippingCourierService> services) {
        this.services = services;
    }

    public String getCourierType() {
        return courierType;
    }

    public void setCourierType(String courierType) {
        this.courierType = courierType;
    }
}