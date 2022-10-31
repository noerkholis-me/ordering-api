package controllers.merchants;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hokeba.api.BaseResponse;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import controllers.BaseController;
import dtos.merchant.MerchantResponse;
import models.Merchant;
import models.UserMerchant;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import repository.UserMerchantRepository;

import java.util.ArrayList;
import java.util.List;

@Api(value = "/merchants", description = "Merchant")
public class MerchantController extends BaseController {

    private final static Logger.ALogger logger = Logger.of(UserMerchantController.class);

    private static BaseResponse response = new BaseResponse();

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @ApiOperation(value = "Get all merchant list for specific email.", notes = "Returns list of merchant for specific email.\n" + swaggerInfo
            + "", response = Merchant.class, responseContainer = "List", httpMethod = "GET")
    public static Result listMerchantsByEmail(String userType) {
        if (userType.equals("merchant")) {
            Merchant ownMerchant = checkMerchantAccessAuthorization();
            if (ownMerchant != null) {
                List<Merchant> queryMerchant = Merchant.find.where().eq("t0.is_deleted", false).eq("t0.email", ownMerchant.email).order("id").findList();
                List<MerchantResponse> responses = new ArrayList<>();
                if (queryMerchant != null || !queryMerchant.isEmpty()) {
                    for (Merchant data : queryMerchant) {
                        MerchantResponse response = new MerchantResponse();
                        response.setId(data.id);
                        response.setFullName(data.fullName);
                        response.setEmail(data.email);
                        response.setUserType("merchant");
                        responses.add(response);
                    }
                    response.setBaseResponse(queryMerchant.size() , 0, 0, success + " showing data", responses);
                    return ok(Json.toJson(response));
                } else {
                    response.setBaseResponse(0, 0, 0, notFound, null);
                    return notFound(Json.toJson(response));
                }
            } else if (ownMerchant == null) {
                response.setBaseResponse(0, 0, 0, forbidden, null);
                return forbidden(Json.toJson(response));
            }
        } else if (userType.equals("user_merchant")){
            UserMerchant userMerchant = checkUserMerchantAccessAuthorization();
            if (userMerchant != null) {
                List<MerchantResponse> responses = new ArrayList<>();
                List<UserMerchant> queryUserMerchant = UserMerchantRepository.find.where().eq("t0.is_deleted", false).eq("t0.email", userMerchant.getEmail()).order("id").findList();
                System.out.println("isinya : "+queryUserMerchant.size());
                if (queryUserMerchant != null || !queryUserMerchant.isEmpty()) {
                    for (UserMerchant data : queryUserMerchant) {
                        MerchantResponse response = new MerchantResponse();
                        response.setId(data.id);
                        response.setFullName(data.fullName);
                        response.setEmail(data.email);
                        response.setUserType("user_merchant");
                        responses.add(response);
                    }
                    response.setBaseResponse(queryUserMerchant.size() , 0, 0, success + " showing data", responses);
                    return ok(Json.toJson(response));
                } else {
                    response.setBaseResponse(0, 0, 0, notFound, null);
                    return notFound(Json.toJson(response));
                }
            } else if (userMerchant == null) {
                response.setBaseResponse(0, 0, 0, forbidden, null);
                return forbidden(Json.toJson(response));
            }
        }

        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }
}
