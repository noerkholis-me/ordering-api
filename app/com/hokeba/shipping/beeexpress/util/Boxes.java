package com.hokeba.shipping.beeexpress.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by hendriksaragih on 8/8/17.
 */
public class Boxes {
    private Double length;
    private Double width;
    private Double height;

    public Boxes(Double length, Double width, Double height) {
        this.length = length;
        this.width = width;
        this.height = height;
    }

    public Boxes(Double length, Double width, Double height, boolean withSort) {
        if (!withSort){
            this.length = length;
            this.width = width;
            this.height = height;
        }else{
            List<Double> datas = Arrays.asList(length, width, height);
            datas.sort(Collections.reverseOrder());

            this.length = datas.get(0);
            this.width = datas.get(1);
            this.height = datas.get(2);
        }

    }

    public Boxes(Double length, Double width) {
        this.length = length;
        this.width = width;
    }

    public Double getLength() {
        return length;
    }

    public void setLength(Double length) {
        this.length = length;
    }

    public Double getWidth() {
        return width;
    }

    public void setWidth(Double width) {
        this.width = width;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public String getDimension() {
        return width + " x "+length+" x "+height;
    }

    @Override
    public String toString() {
        return "Boxes{" +
                "length=" + length +
                ", width=" + width +
                ", height=" + height +
                '}';
    }
}
