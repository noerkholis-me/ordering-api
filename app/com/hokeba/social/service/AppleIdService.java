package com.hokeba.social.service;

import com.hokeba.http.HTTPRequest3;
import com.hokeba.http.Param;
import com.hokeba.http.response.global.ServiceResponse;

import play.libs.F.Promise;
import play.libs.ws.WS;
import play.libs.ws.WSRequestHolder;
import play.libs.ws.WSResponse;
import java.util.Map;

public class AppleIdService extends HTTPRequest3 {
	private static final String appleUrl = "https://appleid.apple.com/auth/token";
	private static AppleIdService instance;
	
	public static AppleIdService getInstance() {
        if (instance == null) {
            instance = new AppleIdService();
        }
        return instance;
    }
	
//	public ServiceResponse getAppleIdUserData(String client_id, String token, String authorizationCode){
//		WSRequestHolder req = WS.url(appleUrl);
//		req.setContentType("application/x-www-form-urlencoded");
//		req.setQueryParameter("client_id", client_id);
//		req.setQueryParameter("client_secret", token);
//		req.setQueryParameter("grant_type", "authorization_code");
//		req.setQueryParameter("code", authorizationCode);
//		return req;

//		return postXFormAppleId(appleUrl, client_id, token, authorizationCode);
//    }

}
