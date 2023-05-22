package controllers.minipos;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Query;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;
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
                List<SubsCategoryMerchant> subsCategoryMerchant = SubsCategoryMerchantRepository.findListCategoryMiniPos(ownMerchant.id);
                List<SubsCategoryMerchantResponse> subsCategoryMerchantList = new ArrayList<>();
                for(SubsCategoryMerchant scm: subsCategoryMerchant){
                    SubsCategoryMerchantResponse scmResponse = new SubsCategoryMerchantResponse();
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

    public static Result listProductMiniPos(int offset, int limit, Long storeId, Long categoryId, String keyword) {
        UserMerchant userMerchant = checkUserMerchantAccessAuthorization();
        if (userMerchant != null) {
            Transaction trx = Ebean.beginTransaction();
            try {
                Merchant ownMerchant = userMerchant.getRole().getMerchant();

                int totalData = ProductMerchantDetailRepository.findListProductMiniPos(ownMerchant.id, storeId, categoryId, keyword, 0, 0).size();
                List<ProductMerchantDetail> dataProductDetail = ProductMerchantDetailRepository.findListProductMiniPos(ownMerchant.id, storeId, categoryId, keyword, offset, limit);

                List<ProductMiniPosResponse> listProductResponsePos = new ArrayList<>();
                for(ProductMerchantDetail productMerchantDetail : dataProductDetail){
                    ProductMiniPosResponse productResponsePos = new ProductMiniPosResponse();
                    ProductMerchant productMerchant = ProductMerchantRepository.findByIdProductRecommend(productMerchantDetail.getProductMerchant().id, ownMerchant.id);
                    ProductStore productStore = ProductStoreRepository.findForCust(productMerchant.id, storeId, ownMerchant);
                    productResponsePos.setProductId(productMerchant.id);
                    productResponsePos.setProductName(productMerchant.getProductName());
                    productResponsePos.setProductType(productMerchantDetail.getProductType());
                    productResponsePos.setIsCustomizable(productMerchantDetail.getIsCustomizable());
                    productResponsePos.setIsActive(productMerchant.getIsActive());
                    productResponsePos.setMerchantId(productMerchant.getMerchant().id);

                    productResponsePos.setProductPrice(productStore != null ? productStore.getStorePrice() : productMerchantDetail.getProductPrice());
                    productResponsePos.setDiscountType(productStore != null ? productStore.getDiscountType() : productMerchantDetail.getDiscountType());
                    productResponsePos.setDiscount(productStore != null ? productStore.getDiscount() : productMerchantDetail.getDiscount());
                    productResponsePos.setProductPriceAfterDiscount(productStore != null ? productStore.getFinalPrice() : productMerchantDetail.getProductPriceAfterDiscount());

                    ProductPosCategoryResponse scmResponse = new ProductPosCategoryResponse();
                    SubsCategoryMerchant scm = productStore != null ? productStore.productMerchant.getSubsCategoryMerchant() : productMerchantDetail.getProductMerchant().getSubsCategoryMerchant();
                    scmResponse.setId(scm.getId());
                    scmResponse.setName(scm.getSubscategoryName());
                    productResponsePos.setCategory(scmResponse);

                    ProductMerchantDescription productMerchantDescription = ProductMerchantDescriptionRepository.findByProductMerchantDetail(productMerchantDetail);
                    if (productMerchantDescription != null) {
                        productResponsePos.setShortDescription(productMerchantDescription.getShortDescription());
                        productResponsePos.setLongDescription(productMerchantDescription.getLongDescription());
                    }

                    productResponsePos.setProductImageMain(productMerchantDetail.getProductImageMain());
                    listProductResponsePos.add(productResponsePos);
                }

                response.setBaseResponse(totalData, 0, 0, "Berhasil menampilkan daftar produk", listProductResponsePos);
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
