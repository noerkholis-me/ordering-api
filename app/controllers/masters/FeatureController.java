package controllers.masters;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Transaction;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hokeba.api.BaseResponse;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import controllers.BaseController;
import dtos.feature.FeatureRequest;
import dtos.feature.FeatureResponse;
import models.Feature;
import models.Merchant;
import models.RoleFeature;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;

import java.util.ArrayList;
import java.util.List;

@Api(value = "/master/feature", description = "All Feature")
public class FeatureController extends BaseController {

    private final static Logger.ALogger logger = Logger.of(FeatureController.class);

    private static BaseResponse response = new BaseResponse();

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @ApiOperation(value = "Get all feature list.", notes = "Returns list of feature.\n" + swaggerInfo
            + "", response = Feature.class, responseContainer = "List", httpMethod = "GET")
    public static Result getAllFeatureMerchant(boolean isMerchant) {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            try {
                List<Feature> featureList;
                if (isMerchant) {
                    featureList = Feature.getAllFeatures(isMerchant);
                } else {
                    featureList = Feature.getAllFeatures();
                }
                List<FeatureResponse> featureResponses = new ArrayList<>();
                for (Feature feature : featureList) {
                    FeatureResponse featureResponse = new FeatureResponse();
                    featureResponse.setId(feature.id);
                    featureResponse.setKey(feature.key);
                    featureResponse.setSection(feature.section);
                    featureResponse.setDescription(feature.description);
                    featureResponse.setName(feature.name);
                    featureResponse.setActive(feature.isActive);
                    featureResponse.setMerchant(feature.isMerchant);
                    featureResponses.add(featureResponse);
                }
                response.setBaseResponse(featureResponses.size(), 0, 0, success, featureResponses);
                return ok(Json.toJson(response));
            } catch (Exception e) {
                Logger.error("getAllFeature", e);
            }
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result createFeatureMerchant() {
        try {
            Merchant merchant = checkMerchantAccessAuthorization();
            if (merchant != null) {
                JsonNode json = request().body().asJson();
                FeatureRequest request = objectMapper.readValue(json.toString(), FeatureRequest.class);
                Transaction trx = Ebean.beginTransaction();
                try {
                    Feature feature = new Feature(
                            request.name,
                            request.key,
                            request.section,
                            request.description,
                            true,
                            true
                    );
                    feature.save();

                    RoleFeature roleFeature = new RoleFeature(feature, merchant.role, 210);
                    roleFeature.save();

                    trx.commit();

                    response.setBaseResponse(1, 0, 0, success, feature);
                    return ok(Json.toJson(response));
                } catch (Exception e) {
                    trx.rollback();
                    logger.error("Error when create :", e);
                    response.setBaseResponse(0, 0, 0, e.getMessage(), null);
                    return badRequest(Json.toJson(response));
                } finally {
                    trx.end();
                }
            } else {
                response.setBaseResponse(0, 0, 0, unauthorized, null);
                return unauthorized(Json.toJson(response));
            }
        } catch (Exception e) {
            logger.error("Error when create : " + e.getMessage());
            response.setBaseResponse(0, 0, 0, e.getMessage(), null);
            return badRequest(Json.toJson(response));
        }
    }

}
