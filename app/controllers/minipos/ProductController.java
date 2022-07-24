package controllers.minipos;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Query;
import com.avaje.ebean.Transaction;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hokeba.api.BaseResponse;
import com.wordnik.swagger.annotations.Api;

import controllers.BaseController;
import dtos.product.ProductMiniPosResponse;
import dtos.product.ProductPosCategoryResponse;
import models.merchant.*;
import models.*;
import repository.*;
import dtos.category.*;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;
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
        UserMerchant userMerchant = checkUserMerchantAccessAuthorization();
        if(userMerchant != null){
            try {
                Merchant ownMerchant = userMerchant.getRole().getMerchant();
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
                        scmResponse.setIsActive(scm.isActive());
                        scmResponse.setIsDeleted(scm.isDeleted());
                        scmResponse.setImageMobile(scm.getImageMobile());
                        scmResponse.setSequence(scm.getSequence());
                        scmResponse.setCategoryId(scm.getCategoryMerchant().id);
                        scmResponse.setSubCategoryId(scm.getSubCategoryMerchant().id);
                        scmResponse.setMerchantId(scm.getMerchant().id);
                        subsCategoryMerchantList.add(scmResponse);
                    }
                }

                response.setBaseResponse(subsCategoryMerchantList.size(), 0, 0, "Berhasil menampilkan kategori produk", subsCategoryMerchantList);
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

    public static Result listProductMiniPos(int offset, int limit, Long storeId, Long categoryId) {
        UserMerchant userMerchant = checkUserMerchantAccessAuthorization();
        if (userMerchant != null) {
            Transaction trx = Ebean.beginTransaction();
            try {
                Merchant ownMerchant = userMerchant.getRole().getMerchant();
                String querySql;
                if(categoryId > 0){
                    querySql = "t0.product_merchant_id in (select pm.id from product_merchant pm where pm.merchant_id = "+ownMerchant.id+" and pm.subs_category_merchant_id = "+categoryId+" and pm.is_active = "+true+" and pm.is_deleted = false)";
                } else {
                    querySql = "t0.product_merchant_id in (select pm.id from product_merchant pm where pm.merchant_id = "+ownMerchant.id+" and pm.is_active = "+true+" and pm.is_deleted = false)";
                }
                Query<ProductMerchantDetail> query = ProductMerchantDetailRepository.find.where().raw(querySql).eq("t0.is_deleted", false).eq("t0.product_type", "MAIN").order("t0.id asc");
                List<ProductMerchantDetail> dataProductDetail = ProductMerchantDetailRepository.getDataByPagination(query, offset, limit);

                List<ProductMiniPosResponse> listProductResponsePos = new ArrayList<>();
                for(ProductMerchantDetail productMerchantDetail : dataProductDetail){
                    ProductMiniPosResponse productResponsePos = new ProductMiniPosResponse();
                    ProductMerchant productMerchant = ProductMerchantRepository.findByIdProductRecommend(productMerchantDetail.getProductMerchant().id, ownMerchant.id);
                    ProductStore productStore = ProductStoreRepository.findForCust(productMerchant.id, storeId, ownMerchant.id);
                    productResponsePos.setProductId(productMerchant.id);
                    productResponsePos.setProductName(productMerchant.getProductName());
                    productResponsePos.setProductType(productMerchantDetail.getProductType());
                    productResponsePos.setIsCustomizable(productMerchantDetail.getIsCustomizable());
                    productResponsePos.setIsActive(productMerchant.getIsActive());
                    productResponsePos.setMerchantId(productMerchant.getMerchant().id);

                    if(productStore != null) {
                        productResponsePos.setProductPrice(productStore.getStorePrice());
                        productResponsePos.setDiscountType(productStore.getDiscountType());
                        productResponsePos.setDiscount(productStore.getDiscount());
                        productResponsePos.setProductPriceAfterDiscount(productStore.getFinalPrice());
                        // set category
                        ProductPosCategoryResponse scmResponse = new ProductPosCategoryResponse();
                        SubsCategoryMerchant scm = productStore.productMerchant.getSubsCategoryMerchant();
                        scmResponse.setId(scm.getId());
                        scmResponse.setName(scm.getSubscategoryName());
                        productResponsePos.setCategory(scmResponse);
                    } else {
                        productResponsePos.setProductPrice(productMerchantDetail.getProductPrice());
                        productResponsePos.setDiscountType(productMerchantDetail.getDiscountType());
                        productResponsePos.setDiscount(productMerchantDetail.getDiscount());
                        productResponsePos.setProductPriceAfterDiscount(productMerchantDetail.getProductPriceAfterDiscount());
                        // set category
                        ProductPosCategoryResponse scmResponse = new ProductPosCategoryResponse();
                        SubsCategoryMerchant scm = productMerchantDetail.getProductMerchant().getSubsCategoryMerchant();
                        scmResponse.setId(scm.getId());
                        scmResponse.setName(scm.getSubscategoryName());
                        productResponsePos.setCategory(scmResponse);
                    }

                    ProductMerchantDescription productMerchantDescription = ProductMerchantDescriptionRepository.findByProductMerchantDetail(productMerchantDetail);
                    if (productMerchantDescription != null) {
                        productResponsePos.setShortDescription(productMerchantDescription.getShortDescription());
                        productResponsePos.setLongDescription(productMerchantDescription.getLongDescription());
                    }

                    productResponsePos.setProductImageMain(productMerchantDetail.getProductImageMain());
                    listProductResponsePos.add(productResponsePos);
                }

                response.setBaseResponse(dataProductDetail.size(), 0, 0, "Berhasil menampilkan daftar produk", listProductResponsePos);
                return ok(Json.toJson(response));
            } catch (Exception e) {
                logger.error("Error saat menampilkan produk", e);
                e.printStackTrace();
                trx.rollback();
            } finally {
                trx.end();
            }
            response.setBaseResponse(0, 0, 0, error, null);
            return badRequest(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, "Tidak dapat menampilkan produk", null);
        return badRequest(Json.toJson(response));
    }

}
