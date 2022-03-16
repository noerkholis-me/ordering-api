package com.hokeba.mapping.response.kiosk;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MapTaxService{
    private Date date;
    private Double tax, service;


    @JsonFormat(shape = JsonFormat.Shape.STRING,  pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone="Asia/Jakarta")

    public Date getDate(){
        return date;
    }

    public Double getTax(){
        return tax;
    }

    public Double getService(){
        return service;
    }
}