package com.hokeba.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dtos.FeatureAndPermissionSession;

import java.util.HashMap;
import java.util.List;

/**
 * Created by hendriksaragih on 3/1/17.
 */
public class UserSession {
    private String access_token;
    private String expired_date;
    private String user_type;
    private Object profile_data;
    @JsonIgnore
    private HashMap<String, Boolean> features;
    private List<FeatureAndPermissionSession> featureAndPermissions;

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getExpired_date() {
        return expired_date;
    }

    public void setExpired_date(String expired_date) {
        this.expired_date = expired_date;
    }

    public String getUser_type() {
        return user_type;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }

    public UserSession() {
    }

    public UserSession(String access_token, String expired_date, String user_type, Object profile_data, List<FeatureAndPermissionSession> featureAndPermissions) {
        this.access_token = access_token;
        this.expired_date = expired_date;
        this.user_type = user_type;
        this.profile_data = profile_data;
        this.featureAndPermissions = featureAndPermissions;
    }

    public UserSession(String access_token, String expired_date, String user_type) {
        super();
        this.access_token = access_token;
        this.expired_date = expired_date;
        this.user_type = user_type;
        this.setProfile_data(null);
        this.setFeatures(null);
    }

    public UserSession(String access_token, String expired_date, String user_type, HashMap<String, Boolean> features) {
        super();
        this.access_token = access_token;
        this.expired_date = expired_date;
        this.user_type = user_type;
        this.setProfile_data(null);
        this.features = features;
    }

    public HashMap<String, Boolean> getFeatures() {
        return features;
    }

    public void setFeatures(HashMap<String, Boolean> features) {
        this.features = features;
    }

    public Object getProfile_data() {
        return profile_data;
    }

    public void setProfile_data(Object profile_data) {
        this.profile_data = profile_data;
    }

    public List<FeatureAndPermissionSession> getFeatureAndPermissions() {
        return featureAndPermissions;
    }

    public void setFeatureAndPermissions(List<FeatureAndPermissionSession> featureAndPermissions) {
        this.featureAndPermissions = featureAndPermissions;
    }
}
