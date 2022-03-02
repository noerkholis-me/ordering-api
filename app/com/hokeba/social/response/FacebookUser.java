package com.hokeba.social.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by hendriksaragih on 3/19/17.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class FacebookUser {
    private String first_name;
    private String email;
    private String middle_name;
    private String last_name;
    private String name;
    private String id;
    private String gender;
    private FacebookPicture picture;

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getMiddle_name() {
        return middle_name;
    }

    public void setMiddle_name(String middle_name) {
        this.middle_name = middle_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public FacebookPicture getPicture() {
        return picture;
    }

    public void setPicture(FacebookPicture picture) {
        this.picture = picture;
    }

    public String getImage(){
        return picture != null ? picture.getImage() : "";
    }
}