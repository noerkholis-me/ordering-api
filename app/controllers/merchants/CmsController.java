package controllers.merchants;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Transaction;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hokeba.api.BaseResponse;
import com.hokeba.mapping.request.MapMerchantPromoRequest;
import com.hokeba.mapping.response.MapAllPromoMerchantList;
import com.hokeba.mapping.response.MapFaq;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import controllers.BaseController;
import models.*;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by nugraha on 5/25/17.
 */
@Api(value = "/merchants/cms", description = "CMS")
public class CmsController extends BaseController {
    @SuppressWarnings("rawtypes")
    private static BaseResponse response = new BaseResponse();

    public static Result index() {
        return ok();
    }

    @ApiOperation(value = "Get all promotion list.", notes = "Returns list of promotion.\n" + swaggerInfo
            + "", response = Promo.class, responseContainer = "List", httpMethod = "GET")
    public static Result allPromoLists(String type, String filter, String sort, int offset, int limit) {
        Merchant actor = checkMerchantAccessAuthorization();
        if (actor != null) {
            String message;
            try {
                if(type.equals("reject")){
                    BaseResponse<Promo> responseIndex;
                    responseIndex = Promo.getDataRequestProduct(actor, MerchantPromoRequestProduct.STATUS_REJECTED, sort, filter, offset, limit);
                    return ok(Json.toJson(responseIndex));
                }else if(type.equals("approve")){
                    BaseResponse<Promo> responseIndex;
                    responseIndex = Promo.getDataRequestProduct(actor, MerchantPromoRequestProduct.STATUS_APPROVED, sort, filter, offset, limit);
                    return ok(Json.toJson(responseIndex));
                }else if(type.equals("review2")){
                    BaseResponse<Promo> responseIndex;
                    responseIndex = Promo.getDataRequestProduct(actor, MerchantPromoRequestProduct.STATUS_PENDING, sort, filter, offset, limit);
                    return ok(Json.toJson(responseIndex));
                }else {
                    BaseResponse<Promo> responseIndex;
                    responseIndex = Promo.getDataAllMerchant(actor, type, sort, filter, offset, limit);
                    return ok(Json.toJson(responseIndex));
                }
            } catch (IOException e) {
                message = e.getMessage();
                Logger.error("allDetail", e);
            }
            response.setBaseResponse(0, 0, 0, error, message);
            return internalServerError(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    @ApiOperation(value = "Get detail promotion.", notes = "Returns detail of promotion.\n" + swaggerInfo
            + "", response = Promo.class, responseContainer = "List", httpMethod = "GET")
    public static Result detailPromo(Long id) {
        Merchant actor = checkMerchantAccessAuthorization();
        if (actor != null) {
            Promo model = Promo.find.where().eq("id", id).eq("is_deleted", false)
                    .setMaxRows(1).findUnique();
            if (model != null) {
                response.setBaseResponse(1, offset, 1, success,
                        new ObjectMapper().convertValue(model, MapAllPromoMerchantList.class));
                return ok(Json.toJson(response));
            }
            response.setBaseResponse(0, 0, 0, notFound, null);
            return notFound(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    @ApiOperation(value = "Get my promotion list.", notes = "Returns list of promotion.\n" + swaggerInfo
            + "", response = Promo.class, responseContainer = "List", httpMethod = "GET")
    public static Result myPromoLists(String type, String filter, String sort, int offset, int limit) {
        Merchant actor = checkMerchantAccessAuthorization();
        if (actor != null) {
            String message;

            try {
                if(type.equals("reject")){
                    BaseResponse<Promo> responseIndex;
                    responseIndex = Promo.getDataRequestProduct(actor, MerchantPromoRequestProduct.STATUS_REJECTED, sort, filter, offset, limit);
                    return ok(Json.toJson(responseIndex));
                }else if(type.equals("approve")){
                    BaseResponse<Promo> responseIndex;
                    responseIndex = Promo.getDataRequestProduct(actor, MerchantPromoRequestProduct.STATUS_APPROVED, sort, filter, offset, limit);
                    return ok(Json.toJson(responseIndex));
                }else if(type.equals("review2")){
                    BaseResponse<Promo> responseIndex;
                    responseIndex = Promo.getDataRequestProduct(actor, MerchantPromoRequestProduct.STATUS_PENDING, sort, filter, offset, limit);
                    return ok(Json.toJson(responseIndex));
                }else {
                    BaseResponse<Promo> responseIndex;
                    responseIndex = Promo.getDataMerchant(actor, type, sort, filter, offset, limit);
                    return ok(Json.toJson(responseIndex));
                }
            } catch (IOException e) {
                message = e.getMessage();
                Logger.error("allDetail", e);
            }
            response.setBaseResponse(0, 0, 0, error, message);
            return internalServerError(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    @ApiOperation(value = "Get promo product list.", notes = "Returns list of promotion.\n" + swaggerInfo
            + "", response = Promo.class, responseContainer = "List", httpMethod = "GET")
    public static Result promoProductLists(Long promoId, String type, String filter, String sort, int offset, int limit) {
        Merchant actor = checkMerchantAccessAuthorization();
        if (actor != null) {
            String message = "";
            if(promoId != null) {
                try {
                    BaseResponse<Promo> responseIndex;
                    responseIndex = Promo.getProductList(actor.id, promoId, type, sort, filter, offset, limit);
                    return ok(Json.toJson(responseIndex));

                } catch (IOException e) {
                    message = e.getMessage();
                    Logger.error("allDetail", e);
                }
            }
            response.setBaseResponse(0, 0, 0, error, message);
            return internalServerError(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    @ApiOperation(value = "Get promo all product list.", notes = "Returns list of promotion.\n" + swaggerInfo
            + "", response = Promo.class, responseContainer = "List", httpMethod = "GET")
    public static Result promoAllProductLists(Long promoId, String filter, String sort, int offset, int limit) {
        Merchant actor = checkMerchantAccessAuthorization();
        if (actor != null) {
            String message = "";
            if(promoId != null) {
                try {
                    BaseResponse<Promo> responseIndex;
                    responseIndex = Promo.getAllProductList(promoId, sort, filter, offset, limit);
                    return ok(Json.toJson(responseIndex));

                } catch (IOException e) {
                    message = e.getMessage();
                    Logger.error("allDetail", e);
                }
            }
            response.setBaseResponse(0, 0, 0, error, message);
            return internalServerError(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result promoApplyProduct() {
        Merchant actor = checkMerchantAccessAuthorization();
        if (actor != null) {
            JsonNode json = request().body().asJson();
            ObjectMapper mapper = new ObjectMapper();
            Transaction txn = Ebean.beginTransaction();
            try {
                MapMerchantPromoRequest map = mapper.readValue(json.toString(), MapMerchantPromoRequest.class);
                String message = Promo.applyProduct(actor, map);

                if (message.isEmpty()){
                    txn.commit();
                    response.setBaseResponse(1, offset, 1, created, null);
                    return ok(Json.toJson(response));
                }else{
                    txn.rollback();
                    response.setBaseResponse(0, 0, 0, message, null);
                    return badRequest(Json.toJson(response));
                }
            } catch (Exception e) {
                e.printStackTrace();
                txn.rollback();
            } finally {
                txn.end();
            }

            response.setBaseResponse(0, 0, 0, inputParameter, null);
            return badRequest(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result faq(String search) {
        Merchant actor = checkMerchantAccessAuthorization();
        if (actor != null) {
            Map<String, List> results = new HashMap<>();
            List<Map<String, Object>> all = new LinkedList<>();
            List<InformationCategoryGroup> groups = InformationCategoryGroup.getHomePage("faq");
            for (InformationCategoryGroup group : groups) {
                List<Faq> faqs = Faq.getHomePage(group.id, search, 1);
                if (faqs.size() > 0){
                    Map<String, Object> dt = new HashMap<>();
                    dt.put("group_id", group.id);
                    dt.put("group_name", group.name);
                    List<Map<String, Object>> details = new LinkedList<>();
                    for (Faq faq : faqs){
                        Map<String, Object> f = new HashMap<>();
                        f.put("faq_id", faq.id);
                        f.put("faq_name", faq.name);
                        f.put("faq_slug", faq.slug);
                        details.add(f);
                    }

                    dt.put("detail", details);
                    all.add(dt);
                }
            }

            List<Map<String, Object>> details = new LinkedList<>();
            Faq.getPopular(1).forEach(faq->{
                Map<String, Object> f = new HashMap<>();
                f.put("faq_id", faq.id);
                f.put("faq_name", faq.name);
                f.put("faq_slug", faq.slug);
                details.add(f);
            });

            results.put("faq_favourite", details);
            results.put("faq_list", all);


            response.setBaseResponse(results.size(), offset, results.size(), success, results);
            return ok(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result faqBySlug(String slug) {
        Merchant actor = checkMerchantAccessAuthorization();
        if (actor != null) {
            Faq model = Faq.find.where().eq("slug", slug).setMaxRows(1).findUnique();
            if (model != null) {
                model.viewCount = model.viewCount + 1;
                model.update();
                response.setBaseResponse(1, offset, 1, success,
                        new ObjectMapper().convertValue(model, MapFaq.class));
                return ok(Json.toJson(response));
            }
            response.setBaseResponse(0, 0, 0, notFound, null);
            return notFound(Json.toJson(response));
        }

        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }
}
