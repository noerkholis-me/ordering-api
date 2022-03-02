package com.hokeba.mapping.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by hendriksaragih on 3/2/17.
 */

@JsonIgnoreProperties(ignoreUnknown=true)
public class MapNotificationMerchant {
    private Long id;
    private Integer tipe;
    private String title;
    private String content;
    public String date;
    @JsonProperty("is_read")
    public Boolean isRead;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getTipe() {
        return tipe;
    }

    public void setTipe(Integer tipe) {
        this.tipe = tipe;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Boolean getRead() {
        return isRead;
    }

    public void setRead(Boolean read) {
        isRead = read;
    }
}
