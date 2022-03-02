package com.hokeba.mapping.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Created by hendriksaragih on 5/1/17.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class MapVariantGroup {
    private Long id;
    private String name;
    private List<MapVariantGroupList> lists;

    public MapVariantGroup(){

    }

    public MapVariantGroup(Long id, String name, List<MapVariantGroupList> lists) {
        this.id = id;
        this.name = name;
        this.lists = lists;
    }

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

    public List<MapVariantGroupList> getLists() {
        return lists;
    }

    public void setLists(List<MapVariantGroupList> lists) {
        this.lists = lists;
    }
}
