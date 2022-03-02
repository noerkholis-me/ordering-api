package com.hokeba.mapping.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Created by hendriksaragih on 3/25/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MapBannerFilter {
    private String position_name;
    private List<MapBanner> list;

    public String getPosition_name() {
        return position_name;
    }

    public void setPosition_name(String position_name) {
        this.position_name = position_name;
    }

    public List<MapBanner> getList() {
        return list;
    }

    public void setList(List<MapBanner> list) {
        this.list = list;
    }
}
