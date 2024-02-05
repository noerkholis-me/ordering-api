package controllers.product;

import com.avaje.ebean.Query;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hokeba.api.BaseResponse;
import controllers.BaseController;
import dtos.product.ProductAppResponse;
import models.ProductDetail;
import models.ProductRatings;
import models.ProductStore;
import models.Store;
import models.merchant.ProductMerchant;
import models.merchant.ProductMerchantDescription;
import models.merchant.ProductMerchantDetail;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import repository.ProductMerchantDetailRepository;
import repository.ProductMerchantRepository;
import repository.ProductStoreRepository;
import repository.ratings.ProductRatingRepository;

import java.util.ArrayList;
import java.util.List;

public class ProductAppController extends BaseController {

    private final static Logger.ALogger logger = Logger.of(ProductMerchantController.class);

    private static BaseResponse response = new BaseResponse();

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Result listProduct(Long merchantId, Long storeId, String search, String key, String value, String filter, int offset, int limit) {

        Store store = Store.find.byId(storeId);
        if (store == null) {
            response.setBaseResponse(0, 0, 0, "Store tidak ditemukan", null);
            return badRequest(Json.toJson(response));
        }

        try {

            int totalData = ProductMerchantDetailRepository.countTotalDataApp(merchantId, storeId, filter, search, key, value);
            List<ProductMerchantDetail> products = ProductMerchantDetailRepository.getTotalDataApp(merchantId, storeId, filter, search, key, value, offset, limit);

            List<ProductAppResponse> responses = new ArrayList<>();

            for(ProductMerchantDetail product : products) {

                ProductMerchantDescription description = ProductMerchantDescription.find.where().eq("product_merchant_detail_id", product.id).findUnique();
                List<ProductRatings> productRatings = ProductRatingRepository.findByProductRatingByProductId(storeId, product.getProductMerchant().id);
                ProductAppResponse response = new ProductAppResponse();
                if (productRatings.size() > 0) {
                    int countProductRatings = ProductRatingRepository.getTotalRating(storeId, product.getProductMerchant().id);
                    response.setRating(countProductRatings/productRatings.size());
                }

                ProductStore productStore = ProductStoreRepository.findByStoreAndProductMerchant(storeId, product.getProductMerchant().id);

                response.setId(product.getProductMerchant().id);
                response.setProductName(product.getProductMerchant().getProductName());
                response.setDiscount(product.getDiscount());
                response.setImage(product.getProductImageMain());
                response.setCategoryId(product.getProductMerchant().getCategoryMerchant().id);
                response.setSubCategoryId(product.getProductMerchant().getSubCategoryMerchant().id);
                response.setSubsCategoryId(product.getProductMerchant().getSubsCategoryMerchant().id);
                response.setBrandId(product.getProductMerchant().getBrandMerchant().id);
                response.setDescription(description.getShortDescription());
                response.setLongDescription(description.getLongDescription());
                response.setBasePrice(product.getProductPrice().intValue());
                response.setPrice(product.getProductPriceAfterDiscount().intValue());
                response.setDiscountType(product.getDiscountType());
                if (productStore != null && productStore.stock != null) {
                    response.setStock(productStore.stock);
                }
                responses.add(response);

            }
            response.setBaseResponse(totalData, offset, limit, "Berhasil menampilkan data", responses);
            return ok(Json.toJson(response));
        } catch (Exception e) {
            Logger.error("allDetail", e);

            return internalServerError(Json.toJson(response));
        }

    }

}
