package com.hokeba.mapping.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Created by hendriksaragih on 3/2/17.
 */

@JsonIgnoreProperties(ignoreUnknown=true)
public class MapAttributeAll {
    private Long id;
    private String name;
    private List<MapAttributeAll> values;

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

    public List<MapAttributeAll> getValues() {
        return values;
    }

    public void setValues(List<MapAttributeAll> values) {
        this.values = values;
    }
}
