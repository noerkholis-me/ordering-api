package com.hokeba.shipping.beeexpress.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by hendriksaragih on 7/28/17.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class PriceResponse {
    private String originaltown;
    private Integer originalpoint;
    private String destinationtown;
    private Integer destinationpoint;
    private Integer packagetype;
    private Double price;

    public String getOriginaltown() {
        return originaltown;
    }

    public void setOriginaltown(String originaltown) {
        this.originaltown = originaltown;
    }

    public Integer getOriginalpoint() {
        return originalpoint;
    }

    public void setOriginalpoint(Integer originalpoint) {
        this.originalpoint = originalpoint;
    }

    public String getDestinationtown() {
        return destinationtown;
    }

    public void setDestinationtown(String destinationtown) {
        this.destinationtown = destinationtown;
    }

    public Integer getDestinationpoint() {
        return destinationpoint;
    }

    public void setDestinationpoint(Integer destinationpoint) {
        this.destinationpoint = destinationpoint;
    }

    public Integer getPackagetype() {
        return packagetype;
    }

    public void setPackagetype(Integer packagetype) {
        this.packagetype = packagetype;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "PriceResponse{" +
                "originaltown='" + originaltown + '\'' +
                ", originalpoint=" + originalpoint +
                ", destinationtown='" + destinationtown + '\'' +
                ", destinationpoint=" + destinationpoint +
                ", packagetype=" + packagetype +
                ", price=" + price +
                '}';
    }
}
