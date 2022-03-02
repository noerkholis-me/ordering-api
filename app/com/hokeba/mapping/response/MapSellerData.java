package com.hokeba.mapping.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hokeba.util.Helper;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Created by hendriksaragih on 3/2/17.
 */

@JsonIgnoreProperties(ignoreUnknown=true)
public class MapSellerData {
    private String date;
    private Integer amount;

    public MapSellerData(){

    }

    public MapSellerData(String date, Integer amount){
        this.date = date;
        this.amount = amount;
    }

    public static List<MapSellerData> dummy(){
        List<MapSellerData> data = new LinkedList<>();
        String edate = Helper.nowFormat();
        String sdate = Helper.addDate(edate, -7);
        while (!Objects.equals(sdate, edate)){
            data.add(new MapSellerData(sdate, (int)(Math.random() * 100)));
            sdate = Helper.addDate(sdate, 1);
        }
        return data;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }
}
