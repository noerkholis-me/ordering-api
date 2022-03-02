package com.hokeba.social.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by hendriksaragih on 6/2/17.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class FacebookPicture {
    private FacebookPictureData data;

    public FacebookPictureData getData() {
        return data;
    }

    public void setData(FacebookPictureData data) {
        this.data = data;
    }

    public String getImage(){
        return data != null ? data.getUrl() : "";
    }
}
