package utils;

import com.hokeba.api.BaseResponse;
import com.hokeba.util.Constant;
import models.MerchantLog;
import play.libs.Json;
import play.mvc.Http.Context;
import play.mvc.Result;
import play.mvc.Security;

public class SecurityConfig extends Security.Authenticator {

    private static final String keyWeb = Constant.getInstance().getApiKeyWeb();
    private static final String keyIos = Constant.getInstance().getApiKeyIOS();
    private static final String keyAndroid = Constant.getInstance().getApiKeyAndroid();

    @Override
    public String getUsername(Context ctx) {

        MerchantLog target = null;
        Object apiKey = ctx.request().getHeader("api_key");
        Object token = ctx.request().getHeader("token");

        if (apiKey == null) {
            System.out.println("api key null");
            return null;
        }

        if (token == null) {
            System.out.println("token null");
            return null;
        }

        if (token != null) {
            System.out.println("api key not null and token not null");
            target = MerchantLog.isMerchantAuthorized(token.toString(), apiKey.toString());
            System.out.println("check to merchant log");
            if (target.memberType.equalsIgnoreCase("merchant") || target.memberType.equalsIgnoreCase("user_merchant")) {
                System.out.println("member type check and return token");
                return target.token;
            } else {
                System.out.println("else not equal member type");
                return null;
            }
        }
        System.out.println("final");
        return target.token;
    }


    @SuppressWarnings("rawtypes")
    @Override
    public Result onUnauthorized(Context ctx) {
        BaseResponse response = new BaseResponse();
        response.setBaseResponse(0, 0, 0, "Unauthorized access", null);
        return unauthorized(Json.toJson(response));
    }

}
