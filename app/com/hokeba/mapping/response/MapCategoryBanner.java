package com.hokeba.mapping.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Created by hendriksaragih on 3/26/17.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class MapCategoryBanner {
    private String title;
    private String icon;
    private String color;
    private List<MapCategoryBannerDetail> content;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public List<MapCategoryBannerDetail> getContent() {
        return content;
    }

    public void setContent(List<MapCategoryBannerDetail> content) {
        this.content = content;
    }
}
