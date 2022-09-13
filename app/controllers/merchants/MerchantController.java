package controllers.merchants;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hokeba.api.BaseResponse;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import controllers.BaseController;
import dtos.merchant.MerchantResponse;
import models.Merchant;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;

import java.util.ArrayList;
import java.util.List;

@Api(value = "/merchants", description = "Merchant")
public class MerchantController extends BaseController {

    private final static Logger.ALogger logger = Logger.of(UserMerchantController.class);

    private static BaseResponse response = new BaseResponse();

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @ApiOperation(value = "Get all merchant list for specific email.", notes = "Returns list of merchant for specific email.\n" + swaggerInfo
            + "", response = Merchant.class, responseContainer = "List", httpMethod = "GET")
    public static Result listMerchantsByEmail() {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            List<Merchant> query = Merchant.find.where().eq("t0.is_deleted", false).eq("t0.email", ownMerchant.email).order("id").findList();
            try {
                List<MerchantResponse> responses = new ArrayList<>();
                for (Merchant data : query) {
                    MerchantResponse response = new MerchantResponse();
                    response.setId(data.id);
                    response.setFullName(data.fullName);
                    response.setEmail(data.email);
                    responses.add(response);
                }
                response.setBaseResponse(query.size() , 0, 0, success + " showing data", responses);
                return ok(Json.toJson(response));
            } catch (Exception e) {
                Logger.error("allDetail", e);
            }
        } else if (ownMerchant == null) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }
}
