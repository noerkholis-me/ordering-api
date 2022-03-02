package com.hokeba.mapping.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by hendriksaragih on 3/2/17.
 */

@JsonIgnoreProperties(ignoreUnknown=true)
public class MapFooterAll {

    @JsonProperty("left")
    private List<MapFooter> left;

    @JsonProperty("middle")
    private List<MapFooter> middle;

    @JsonProperty("right")
    private List<MapFooter> right;

    public List<MapFooter> getLeft() {
        return left;
    }

    public void setLeft(List<MapFooter> left) {
        this.left = left;
    }

    public List<MapFooter> getMiddle() {
        return middle;
    }

    public void setMiddle(List<MapFooter> middle) {
        this.middle = middle;
    }

    public List<MapFooter> getRight() {
        return right;
    }

    public void setRight(List<MapFooter> right) {
        this.right = right;
    }
}
