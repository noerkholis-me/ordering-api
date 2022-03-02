package com.hokeba.social.service;

import com.hokeba.http.HTTPRequest3;
import com.hokeba.http.Param;
import com.hokeba.http.response.global.ServiceResponse;

/**
 * Created by hendriksaragih on 3/19/17.
 */
public class GooglePlusService extends HTTPRequest3 {
    private static final String googlePlusUrl = "https://www.googleapis.com/oauth2/v3/userinfo";
    private static GooglePlusService instance;

    public static GooglePlusService getInstance() {
        if (instance == null) {
            instance = new GooglePlusService();
        }
        return instance;
    }

    public static String getGooglePlusAuth(String token){
        return "Bearer "+token;
    }

    public ServiceResponse getGooglePlusUserData(String token){
        return get(googlePlusUrl, new Param("Authorization", getGooglePlusAuth(token)));
    }
}
