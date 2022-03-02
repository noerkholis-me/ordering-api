package com.hokeba.mapping.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by hendriksaragih on 3/2/17.
 */

@JsonIgnoreProperties(ignoreUnknown=true)
public class MapMasterCategory {

    private Long id;
    private String name;

    @JsonProperty("has_child")
    private boolean hasChild;

    @JsonProperty("sub_category")
    private List<MapMasterCategory> childCategory;

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

    public boolean isHasChild() {
        return hasChild;
    }

    public void setHasChild(boolean hasChild) {
        this.hasChild = hasChild;
    }

    public List<MapMasterCategory> getChildCategory() {
        return childCategory;
    }

    public void setChildCategory(List<MapMasterCategory> childCategory) {
        this.childCategory = childCategory;
    }
}
