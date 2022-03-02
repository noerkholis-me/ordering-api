package com.hokeba.social.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by hendriksaragih on 6/2/17.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class FacebookPictureData {
    private Boolean is_silhouette;
    private String url;

    public Boolean getIs_silhouette() {
        return is_silhouette;
    }

    public void setIs_silhouette(Boolean is_silhouette) {
        this.is_silhouette = is_silhouette;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
