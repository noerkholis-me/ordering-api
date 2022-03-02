package controllers.web;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hokeba.api.BaseResponse;
import com.hokeba.mapping.response.MapBanner;
import com.hokeba.mapping.response.MapBannerMegaMenu;
import com.wordnik.swagger.annotations.Api;

import controllers.BaseController;
import models.Banner;
import models.BannerMegaMenu;
//import models.Merchant;
//import models.SalesOrderSeller;
import play.libs.Json;
import play.mvc.Result;

@Api(value = "/megamenubanner")
public class BannerMegaMenuController extends BaseController {
    @SuppressWarnings("rawtypes")
    private static BaseResponse response = new BaseResponse();
    
	public static Result detail(Long id) {
		int authority = checkAccessAuthorization("all");
		if (authority == 200 || authority == 203) {
			BannerMegaMenu data = BannerMegaMenu.find.where().eq("id", id).eq("is_deleted", false).setMaxRows(1).findUnique();
            if (data != null){
                ObjectMapper mapper = new ObjectMapper();
                MapBannerMegaMenu result = mapper.convertValue(data, MapBannerMegaMenu.class);
                response.setBaseResponse(1, offset, 1, success, result);
                return ok(Json.toJson(response));
            }
		}
		else if (authority == 403) {
		    response.setBaseResponse(0, 0, 0, forbidden, null);
		    return forbidden(Json.toJson(response));
		}
//
		response.setBaseResponse(0, 0, 0, unauthorized, null);
		return unauthorized(Json.toJson(response));
    	}
	}



//result.setVoucherInfo(data.fetchVoucherInfo());
//Merchant actor = checkMerchantAccessAuthorization();
//if (actor != null) {
	
//  response.setBaseResponse(0, 0, 0, notFound, null);
//  return forbidden(Json.toJson(response));

//response.setBaseResponse(0, 0, 0, unauthorized, null);
//return unauthorized(Json.toJson(response));
//int authority = checkAccessAuthorization("all");
//if (authority == 200 || authority == 203) {
//    List<Banner> banners = Banner.getAllBanner(getDeviceType());
//
//    response.setBaseResponse(banners.size(), offset, banners.size(), success, new ObjectMapper().convertValue(banners, MapBanner[].class));
//    return ok(Json.toJson(response));
//} else if (authority == 403) {
//    response.setBaseResponse(0, 0, 0, forbidden, null);
//    return forbidden(Json.toJson(response));
//}
//response.setBaseResponse(0, 0, 0, unauthorized, null);
//return unauthorized(Json.toJson(response));