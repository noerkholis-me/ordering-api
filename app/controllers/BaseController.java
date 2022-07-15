package controllers;

import com.hokeba.util.Constant;
import models.*;
import play.mvc.Controller;

/**
 * Created by hendriksaragih on 2/4/17.
 */
public class BaseController extends Controller {

    protected final static String created = "Success create new record";
    protected final static String success = "Success";
    protected final static String error = "We're sorry but something went wrong";
    protected final static String inputParameter = "Please check input parameter";
//    protected final static String loyaltyInvalid = "Please refresh the page to update loyalty points";
    protected final static String loyaltyInvalid = "Please refresh the page to update loyalty points or proceed to recent orders";
    protected final static String insufficientLoyalty = "Not enough loyalty points";
    protected final static String productInvalid = "This product is invalid";
    protected final static String notFound = "The record was not found";
    protected final static String deleted = "The record was deleted";
    protected final static String updated = "The record was updated";
    protected final static String unauthorized = "Unauthorized access";
    protected final static String forbidden = "You don't have permission to access this resource";
    protected final static String notImage = "Please insert image file type";
    protected final static String expiredCode = "The code was expired";
    protected final static String timeOut = "Request Time Out";
    protected final static String API_KEY = "api_key";
    protected final static String TOKEN = "token";

    protected final static String swaggerInfo = "Result data from this api is encapsulated in base response model.\n "
            + "Response status can be known from json path 'meta'.\n"
            + "Response status detail can be known from json path 'message'.\n"
            + "Data from successful response can be obtained from json path 'data'.";
    protected final static String swaggerParamInfo = "Body parameter doesn't need to provide 'created_at','updated_at', and 'is_deleted' data.\n"
            + "Those data were assigned automatically from our system.";

    protected static String sort = "id";
    protected static String filter = "";
    protected static int offset = 0;
    protected static int limit = 0;

    public static boolean checkGuestAccessAuthorization(String apiKey) {
        String keyWeb = Constant.getInstance().getApiKeyWeb();
        String keyIos = Constant.getInstance().getApiKeyIOS();
        String keyAndroid = Constant.getInstance().getApiKeyAndroid();

        return apiKey.equalsIgnoreCase(keyWeb) || apiKey.equalsIgnoreCase(keyIos) || apiKey.equalsIgnoreCase(keyAndroid);
    }

    public static int checkAccessAuthorization(String type) {
        String apiKey = request().headers().containsKey(API_KEY) ? request().headers().get(API_KEY)[0]: null;
        if (apiKey != null) {
            String token = request().headers().containsKey(TOKEN) ? request().headers().get(TOKEN)[0] : null;
            switch (type) {
                case "all":
                    if (token != null) {
                        return MemberLog.isMemberAuthorized(token, apiKey) == null ? 401 : 200;
                    } else {
                        return checkGuestAccessAuthorization(apiKey) ? 200 : 401;
                    }
                case "member":
                    if (token != null) {
                        return MemberLog.isMemberAuthorized(token, apiKey)==null? 401 : 200;
                    }
                case "merchant":
                    if (token != null) {
                        return MerchantLog.isMerchantAuthorized(token, apiKey)==null? 401 : 200;
                    }
                    break;
                case "odoo":
                    if (token != null) {
                        return isOdooAuthorized(token, apiKey) ? 200 : 401;
                    }
                    break;
                case "guest":
                    return checkGuestAccessAuthorization(apiKey) ? 200 : 401;
            }
        }
        return 401;
    }

    public static Boolean isOdooAuthorized(String token, String apiKey) {
        return token.equals(Constant.getInstance().getTokenOdoo()) && apiKey.equals(Constant.getInstance().getApiKeyOdoo());
    }

    public static UserCms getUserCms(){
        return UserCmsLog.getByToken(session("access_token")).user;
    }

    public static Merchant getUserMerchant(){
        String token = request().headers().containsKey(TOKEN) ? request().headers().get(TOKEN)[0] : null;
        return MerchantLog.getByToken(token).merchant;
    }

    public static Member getUser(){
        String token = request().headers().containsKey(TOKEN) ? request().headers().get(TOKEN)[0] : null;
        return MemberLog.getByToken(token).member;
    }

    public static Member checkMemberAccessAuthorization() {
        if (request().headers().get(API_KEY) != null && request().headers().get(TOKEN) != null) {
            String apiKey = request().headers().get(API_KEY)[0];
            String token = request().headers().get(TOKEN)[0];
            MemberLog target = MemberLog.isMemberAuthorized(token, apiKey);
            return target == null ? null : target.member;
        }
        return null;
    }

    public static UserCms checkUserCmsAccessAuthorization(){
        if(request().headers().get(API_KEY)!=null && request().headers().get(TOKEN)!=null){
            String apiKey     = request().headers().get(API_KEY)[0];
            String token      = request().headers().get(TOKEN)[0];
            UserCmsLog target = UserCmsLog.isUserAuthorized(token, apiKey);
            return target == null ? null : target.user;
        }
        return null;
    }

    public static Merchant checkMerchantAccessAuthorization(){
        if(request().headers().get(API_KEY)!=null && request().headers().get(TOKEN)!=null){
            String apiKey     = request().headers().get(API_KEY)[0];
            String token      = request().headers().get(TOKEN)[0];
            MerchantLog target = MerchantLog.isMerchantAuthorized(token, apiKey);
            if (target.merchant != null) {
                return target.merchant;
            } else {
                return target.userMerchant.getRole().getMerchant();
            }
        }
        return null;
    }

    public static UserMerchant checkUserMerchantAccessAuthorization(){
        if(request().headers().get(API_KEY)!=null && request().headers().get(TOKEN)!=null){
            String apiKey     = request().headers().get(API_KEY)[0];
            String token      = request().headers().get(TOKEN)[0];
            MerchantLog target = MerchantLog.isMerchantAuthorized(token, apiKey);
            if (target.userMerchant != null) {
                return target.userMerchant;
            }
        }
        return null;
    }

    // TODO masih lambat (3 query)
//    public static boolean canAccessFeature(UserCms user, String feature){
//        int a = UserCms.find.fetch("role").fetch("role.features").where().eq("id", user.id)
//                .eq("role.features.key", feature).findRowCount();
//        return (a == 1);
//    }

    public static RoleFeature getUserAccessByFeature(String keyFeature){
        UserCms user = getUserCms();
        RoleFeature roleFeature = RoleFeature.getFeaturesByRoleAndFeature(user.role.id, keyFeature);
        return roleFeature;
    }

    public static String getDeviceType() {
        String apiKey = request().headers().containsKey(API_KEY) ? request().headers().get(API_KEY)[0]: null;
        String keyWeb = Constant.getInstance().getApiKeyWeb();
        String keyIos = Constant.getInstance().getApiKeyIOS();
        String keyAndroid = Constant.getInstance().getApiKeyAndroid();
        if(apiKey.equalsIgnoreCase(keyIos))
            return MemberLog.DEV_TYPE_IOS;
        else if(apiKey.equalsIgnoreCase(keyAndroid))
            return MemberLog.DEV_TYPE_ANDROID;
        else return MemberLog.DEV_TYPE_WEB;
    }

}