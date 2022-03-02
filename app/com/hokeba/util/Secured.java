package com.hokeba.util;

import play.mvc.Security;
import play.mvc.Http.Context;
import com.hokeba.api.BaseResponse;
import models.Member;
import play.libs.Json;
import play.mvc.Result;

public class Secured extends Security.Authenticator {
	
	private static Member member = null;
	
	@Override
	public String getUsername(Context ctx) {
		Object apiKey = ctx.request().getHeader("X-API-KEY");
		Object token = ctx.request().getHeader("X-API-TOKEN");
		
		if (apiKey == null)
			return null;
		
		if (!apiKey.toString().equals(Constant.API_KEY))
			return null;
		
		if (token == null)
			return null;

		member = Member.find.where().eq("token", token.toString()).setMaxRows(1).findUnique();

		if (member == null) {
			if (!token.toString().equals(Constant.KIOSK_TOKEN)) {
				return null;
			} else {
				return token.toString();
			}
		}

		return member.token;
	}

    @SuppressWarnings("rawtypes")
	@Override
	public Result onUnauthorized(Context context) {
		BaseResponse response = new BaseResponse();
        response.setBaseResponse(0, 0, 0, "Unauthorized access", null);
        return unauthorized(Json.toJson(response));
	}
	
	public static Member getMember() {
		return member;
	}

}
