package controllers.product;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Transaction;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hokeba.api.BaseResponse;
import controllers.BaseController;
import dtos.product.ProductRequest;
import models.BrandMerchant;
import models.CategoryMerchant;
import models.Merchant;
import models.SubCategoryMerchant;
import models.merchant.ProductMerchant;
import models.merchant.ProductMerchantDescription;
import models.merchant.ProductMerchantDetail;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import repository.*;
import validator.ProductValidator;

public class ProductMerchantController extends BaseController {

    private final static Logger.ALogger logger = Logger.of(ProductMerchantController.class);

    private static BaseResponse response = new BaseResponse();

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Result createProduct() {

        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            JsonNode json = request().body().asJson();
            try {
                ProductRequest productRequest = objectMapper.readValue(json.toString(), ProductRequest.class);
                String validateRequest = ProductValidator.validateRequest(productRequest);
                if (validateRequest != null) {
                    response.setBaseResponse(0, 0, 0, validateRequest, null);
                    return badRequest(Json.toJson(response));
                }
                Transaction trx = Ebean.beginTransaction();
                try {
                    ProductMerchant newProductMerchant = new ProductMerchant();
                    CategoryMerchant categoryMerchant = CategoryMerchantRepository.findByIdAndMerchantId(
                            productRequest.getCategoryId(), ownMerchant.id);
                    SubCategoryMerchant subCategoryMerchant = SubCategoryMerchantRepository.findByIdAndMerchantId(
                            productRequest.getSubCategoryId(), ownMerchant.id);
                    BrandMerchant brandMerchant = BrandMerchantRepository.findByIdAndMerchantId(
                            productRequest.getBrandId(), ownMerchant.id);
                    constructProductEntityRequest(newProductMerchant, ownMerchant, productRequest, categoryMerchant,
                            subCategoryMerchant, brandMerchant);
                    newProductMerchant.save();

                    // do save to detail
                    ProductMerchantDetail newProductMerchantDetail = new ProductMerchantDetail();
                    constructProductDetailEntityRequest(newProductMerchantDetail, newProductMerchant, productRequest);
                    newProductMerchantDetail.save();

                    ProductMerchantDescription newProductMerchantDescription = new ProductMerchantDescription();
                    newProductMerchantDescription.setShortDescription(productRequest.getShortDescription());
                    newProductMerchantDescription.setLongDescription(productRequest.getLongDescription());
                    newProductMerchantDescription.setProductMerchantDetail(newProductMerchantDetail);
                    newProductMerchantDescription.save();

                    trx.commit();

                    response.setBaseResponse(1,offset, 1, success + " Product updated successfully", null);
                    return ok(Json.toJson(response));

                } catch (Exception e) {
                    logger.error("Error while creating product", e);
                    e.printStackTrace();
                    trx.rollback();
                } finally {
                    trx.end();
                }
            } catch (Exception e) {
                logger.error("Error while parsing json", e);
                e.printStackTrace();
            }
        }

        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result editProduct(Long id) {

        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            JsonNode json = request().body().asJson();
            try {
                ProductRequest productRequest = objectMapper.readValue(json.toString(), ProductRequest.class);
                String validateRequest = ProductValidator.validateRequest(productRequest);
                if (validateRequest != null) {
                    response.setBaseResponse(0, 0, 0, validateRequest, null);
                    return badRequest(Json.toJson(response));
                }
                ProductMerchant productMerchant = ProductMerchantRepository.findById(id);
                if (productMerchant == null) {
                    response.setBaseResponse(0, 0, 0, " Product not found.", null);
                    return badRequest(Json.toJson(response));
                }
                Transaction trx = Ebean.beginTransaction();
                try {
                    CategoryMerchant categoryMerchant = CategoryMerchantRepository.findByIdAndMerchantId(
                            productRequest.getCategoryId(), ownMerchant.id);
                    SubCategoryMerchant subCategoryMerchant = SubCategoryMerchantRepository.findByIdAndMerchantId(
                            productRequest.getSubCategoryId(), ownMerchant.id);
                    BrandMerchant brandMerchant = BrandMerchantRepository.findByIdAndMerchantId(
                            productRequest.getBrandId(), ownMerchant.id);
                    constructProductEntityRequest(productMerchant, ownMerchant, productRequest, categoryMerchant,
                            subCategoryMerchant, brandMerchant);
                    productMerchant.update();

                    // do save to detail
                    ProductMerchantDetail getProductMerchantDetail = ProductMerchantDetailRepository.findByProduct(productMerchant);
                    constructProductDetailEntityRequest(getProductMerchantDetail, productMerchant, productRequest);
                    getProductMerchantDetail.update();

                    ProductMerchantDescription getProductMerchantDescription = ProductMerchantDescriptionRepository.findByProductMerchantDetail(getProductMerchantDetail);
                    getProductMerchantDescription.setShortDescription(productRequest.getShortDescription());
                    getProductMerchantDescription.setLongDescription(productRequest.getLongDescription());
                    getProductMerchantDescription.setProductMerchantDetail(getProductMerchantDetail);
                    getProductMerchantDescription.update();

                    trx.commit();

                    response.setBaseResponse(1,offset, 1, success + " Product created successfully", null);
                    return ok(Json.toJson(response));

                } catch (Exception e) {
                    logger.error("Error while creating product", e);
                    e.printStackTrace();
                    trx.rollback();
                } finally {
                    trx.end();
                }
            } catch (Exception e) {
                logger.error("Error while parsing json", e);
                e.printStackTrace();
            }
        }

        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }









    // =============================================== construct =====================================================//


    private static void constructProductEntityRequest(ProductMerchant newProductMerchant, Merchant merchant,
                                                      ProductRequest productRequest, CategoryMerchant categoryMerchant,
                                                      SubCategoryMerchant subCategoryMerchant, BrandMerchant brandMerchant) {
        newProductMerchant.setProductName(productRequest.getProductName());
        newProductMerchant.setIsActive(Boolean.TRUE);
        newProductMerchant.setCategoryMerchant(categoryMerchant);
        newProductMerchant.setSubCategoryMerchant(subCategoryMerchant);
        newProductMerchant.setBrandMerchant(brandMerchant);
        newProductMerchant.setMerchant(merchant);
    }

    private static void constructProductDetailEntityRequest(ProductMerchantDetail newProductMerchantDetail, ProductMerchant newProductMerchant, ProductRequest productRequest) {
        newProductMerchantDetail.setProductType(productRequest.getProductType());
        newProductMerchantDetail.setIsCustomizable(productRequest.getIsCustomizable());
        newProductMerchantDetail.setProductPrice(productRequest.getProductPrice());
        newProductMerchantDetail.setDiscountType(productRequest.getDiscountType());
        newProductMerchantDetail.setDiscount(productRequest.getDiscount());
        newProductMerchantDetail.setProductPriceAfterDiscount(productRequest.getProductPriceAfterDiscount());
        newProductMerchantDetail.setProductImageMain(productRequest.getProductImageMain());
        newProductMerchantDetail.setProductImage1(productRequest.getProductImage1());
        newProductMerchantDetail.setProductImage2(productRequest.getProductImage2());
        newProductMerchantDetail.setProductImage3(productRequest.getProductImage3());
        newProductMerchantDetail.setProductImage4(productRequest.getProductImage4());
        newProductMerchantDetail.setProductMerchant(newProductMerchant);
    }

}
