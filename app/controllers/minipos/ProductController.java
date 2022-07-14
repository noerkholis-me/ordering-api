package controllers.minipos;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Page;
import com.avaje.ebean.Query;
import com.avaje.ebean.Transaction;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hokeba.api.BaseResponse;
import com.hokeba.http.response.global.ServiceResponse;
import com.hokeba.mapping.request.MapProductMerchant;
import com.hokeba.mapping.request.MapProductMerchant2;
import com.hokeba.mapping.response.*;
import com.hokeba.social.requests.MailchimpProductRequest;
import com.hokeba.social.requests.MailchimpProductVariantRequest;
import com.hokeba.social.service.MailchimpService;
import com.hokeba.util.CommonFunction;
import com.hokeba.util.Secured;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponses;

import assets.Tool;
import controllers.BaseController;
import models.merchant.*;
import models.merchant.*;
import models.*;
import repository.*;
import dtos.category.*;
import play.Logger;
import play.Play;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by hendriksaragih on 4/8/17.
 */
@Api(value = "/merchants/products", description = "Products")
public class ProductController extends BaseController {

    private final static Logger.ALogger logger = Logger.of(ProductController.class);
    private static final BaseResponse response = new BaseResponse();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Result listCategoryMiniPOS() {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if(ownMerchant != null){
            try {
                Query<SubsCategoryMerchant> query  = SubsCategoryMerchantRepository.find.where().eq("t0.is_deleted", false).eq("t0.is_active", true).eq("merchant", ownMerchant).order("t0.id asc");
                List<SubsCategoryMerchant> subsCategoryMerchant = SubsCategoryMerchantRepository.getDataSubsCategory(query, sort, filter, offset, limit);
                List<SubsCategoryMerchantResponse> subsCategoryMerchantList = new ArrayList<>();

                for(SubsCategoryMerchant scm: subsCategoryMerchant){
                    SubsCategoryMerchantResponse scmResponse = new SubsCategoryMerchantResponse();

                    String querySql = "t0.product_merchant_id in (select pm.id from product_merchant pm where subs_category_merchant_id = "+scm.id+" and pm.is_active = "+true+" and pm.is_deleted = "+false+")";
                    List<ProductMerchantDetail> pmdList = ProductMerchantDetailRepository.find.where().raw(querySql).eq("t0.is_deleted", false).eq("t0.product_type", "MAIN").eq("productMerchant.merchant", ownMerchant).findPagingList(0).getPage(0).getList();
                    
                    if(pmdList.size() != 0){
                        scmResponse.setId(scm.getId());
                        scmResponse.setSubscategoryName(scm.getSubscategoryName());
                        scmResponse.setImageWeb(scm.getImageWeb());
                        scmResponse.setImageMobile(scm.getImageMobile());
                        scmResponse.setIsActive(scm.getIsActive());
                        scmResponse.setIsDeleted(scm.getIsDeleted());
                        scmResponse.setImageMobile(scm.getImageMobile());
                        scmResponse.setSequence(scm.getSequence());
                        scmResponse.setCategoryId(scm.getCategoryMerchant().id);
                        scmResponse.setSubCategoryId(scm.getSubCategoryMerchant().id);
                        scmResponse.setMerchantId(scm.getMerchant().id);
                        subsCategoryMerchantList.add(scmResponse);
                    }
                }

                response.setBaseResponse(subsCategoryMerchantList.size(), 0, 0, "Berhasil menambahkan user", subsCategoryMerchantList);
                return ok(Json.toJson(response));
            } catch (Exception e) {
                logger.error("Error while parsing json", e);
                e.printStackTrace();
            }
            response.setBaseResponse(0, 0, 0, "Terjadi Kesalahan", null);
            return badRequest(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, "Unauthorize", null);
        return forbidden(Json.toJson(response));
    }

}
