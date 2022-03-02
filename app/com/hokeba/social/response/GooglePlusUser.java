package com.hokeba.social.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by hendriksaragih on 3/19/17.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class GooglePlusUser {
    private String kind;
    private String etag;
    private String nickname;
    private String gender;
    private GooglePlusEmail[] emails;
    private String objectType;
    private String id;
    private String displayName;
    private GooglePlusName name;
    private String url;
    private GooglePlusImage image;
    private boolean isPlusUser;
    private String language;
    private int circledByCount;
    private boolean verified;

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public GooglePlusEmail[] getEmails() {
        return emails;
    }

    public void setEmails(GooglePlusEmail[] emails) {
        this.emails = emails;
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public GooglePlusName getName() {
        return name;
    }

    public void setName(GooglePlusName name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public GooglePlusImage getImage() {
        return image;
    }

    public void setImage(GooglePlusImage image) {
        this.image = image;
    }

    public boolean isPlusUser() {
        return isPlusUser;
    }

    public void setPlusUser(boolean isPlusUser) {
        this.isPlusUser = isPlusUser;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public int getCircledByCount() {
        return circledByCount;
    }

    public void setCircledByCount(int circledByCount) {
        this.circledByCount = circledByCount;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }
}