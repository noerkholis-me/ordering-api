package controllers.users;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hokeba.api.BaseResponse;
import com.hokeba.mapping.response.MapMerchantDetail;
import com.wordnik.swagger.annotations.Api;
import controllers.BaseController;
import models.Merchant;
import models.Vendor;
import play.libs.Json;
import play.mvc.Result;

/**
 * Created by hendriksaragih on 4/8/17.
 */
@Api(value = "/users/seller", description = "Seller")
public class SellerController extends BaseController {
    @SuppressWarnings("rawtypes")
    private static BaseResponse response = new BaseResponse();

    public static Result index() {
        return ok();
    }

    public static Result detail(String type, Long id) {
        int authority = checkAccessAuthorization("all");
        if (authority == 200) {
            if (type.equalsIgnoreCase("merchant")){
                Merchant merchant = Merchant.find.where().eq("is_deleted", false).eq("id", id).findUnique();
                if (merchant != null){
//                    merchant.setCouriers();
                    merchant.setRatingStat();
                    merchant.setPaymentMethods();
                    merchant.setOrderStat();
                    response.setBaseResponse(1, offset, 1, success, new ObjectMapper().convertValue(merchant, MapMerchantDetail.class));
                    return ok(Json.toJson(response));
                }
            }else{
                Vendor vendor = Vendor.find.where().eq("is_deleted", false).eq("id", id).findUnique();
                if (vendor != null){
                    vendor.setCouriers();
                    vendor.setRatingStat();
                    vendor.setPaymentMethods();
                    vendor.setOrderStat();
                    response.setBaseResponse(1, offset, 1, success, new ObjectMapper().convertValue(vendor, MapMerchantDetail.class));
                    return ok(Json.toJson(response));
                }
            }
            response.setBaseResponse(0, 0, 0, notFound, null);
            return notFound(Json.toJson(response));

        } else if (authority == 403) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

}
