package com.hokeba.social.service;

import com.hokeba.http.HTTPRequest3;
import com.hokeba.http.Param;
import com.hokeba.http.response.global.ServiceResponse;

/**
 * Created by hendriksaragih on 3/19/17.
 */
public class FacebookService extends HTTPRequest3 {
    private static final String facebookUrl   = "https://graph.facebook.com/v2.8/me";
    private static final String facebookFieldParam = "?fields=email,first_name,middle_name,last_name,name,id,gender,picture";
    private static FacebookService instance;

    public static FacebookService getInstance() {
        if (instance == null) {
            instance = new FacebookService();
        }
        return instance;
    }

    public static String getFacebookAuth(String token){
        return "OAuth "+token;
    }

    public ServiceResponse getFacebookUserData(String token){
        return get(facebookUrl+facebookFieldParam, new Param("Authorization", getFacebookAuth(token)));
    }
}
