package com.hokeba.mapping.response;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by hendriksaragih on 4/11/17.
 */
public class MapProductRatting {
    @JsonProperty("bintang_1")
    private Integer bintang1;
    @JsonProperty("bintang_2")
    private Integer bintang2;
    @JsonProperty("bintang_3")
    private Integer bintang3;
    @JsonProperty("bintang_4")
    private Integer bintang4;
    @JsonProperty("bintang_5")
    private Integer bintang5;

    private Integer count;
    private Float average;

    public Integer getBintang1() {
        return bintang1;
    }

    public void setBintang1(Integer bintang1) {
        this.bintang1 = bintang1;
    }

    public Integer getBintang2() {
        return bintang2;
    }

    public void setBintang2(Integer bintang2) {
        this.bintang2 = bintang2;
    }

    public Integer getBintang3() {
        return bintang3;
    }

    public void setBintang3(Integer bintang3) {
        this.bintang3 = bintang3;
    }

    public Integer getBintang4() {
        return bintang4;
    }

    public void setBintang4(Integer bintang4) {
        this.bintang4 = bintang4;
    }

    public Integer getBintang5() {
        return bintang5;
    }

    public void setBintang5(Integer bintang5) {
        this.bintang5 = bintang5;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Float getAverage() {
        return average;
    }

    public void setAverage(Float average) {
        this.average = average;
    }
}
