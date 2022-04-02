package controllers.product;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Query;
import com.avaje.ebean.Transaction;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hokeba.api.BaseResponse;
import controllers.BaseController;
import dtos.product.ProductDescriptionResponse;
import dtos.product.ProductDetailResponse;
import dtos.product.ProductRequest;
import dtos.product.ProductResponse;
import models.*;
import models.merchant.ProductMerchant;
import models.merchant.ProductMerchantDescription;
import models.merchant.ProductMerchantDetail;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import repository.*;
import validator.ProductValidator;

import java.util.ArrayList;
import java.util.List;

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
                    newProductMerchantDescription.setShortDescription(productRequest.getProductDescriptionRequest().getShortDescription());
                    newProductMerchantDescription.setLongDescription(productRequest.getProductDescriptionRequest().getLongDescription());
                    newProductMerchantDescription.setProductMerchantDetail(newProductMerchantDetail);
                    newProductMerchantDescription.save();

                    trx.commit();

                    response.setBaseResponse(1,offset, 1, success + " Product updated successfully", newProductMerchant.id);
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
                ProductMerchant productMerchant = ProductMerchantRepository.findById(id, ownMerchant);
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
                    getProductMerchantDescription.setShortDescription(productRequest.getProductDescriptionRequest().getShortDescription());
                    getProductMerchantDescription.setLongDescription(productRequest.getProductDescriptionRequest().getLongDescription());
                    getProductMerchantDescription.setProductMerchantDetail(getProductMerchantDetail);
                    getProductMerchantDescription.update();

                    trx.commit();

                    response.setBaseResponse(1,offset, 1, success + " Product updated successfully", productMerchant.id);
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

    public static Result getAllProduct(String filter, String sort, int offset, int limit, Boolean isActive) {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            try {
                Query<ProductMerchant> query = ProductMerchantRepository.findProductIsActiveAndMerchant(ownMerchant, isActive);
                List<ProductMerchant> totalData = ProductMerchantRepository.getTotalDataPage(query);
                List<ProductMerchant> productMerchants = ProductMerchantRepository.findProductWithPaging(query, sort, filter, offset, limit);
                List<ProductResponse> productMerchantResponse = toResponses(productMerchants);
                response.setBaseResponse(filter == null || filter.equals("") ? totalData.size() : productMerchantResponse.size(), offset, limit, success + " Showing data products", productMerchantResponse);
                return ok(Json.toJson(response));
            } catch (Exception e) {
                Logger.info("Error: " + e.getMessage());
            }
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result getProductById(Long id) {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            try {
                ProductMerchant productMerchant = ProductMerchantRepository.findById(id, ownMerchant);
                if (productMerchant != null) {
                    ProductResponse productResponse = toResponse(productMerchant);
                    response.setBaseResponse(1, 0, 1, success + " Showing data product", productResponse);
                    return ok(Json.toJson(response));
                }
            } catch (Exception e) {
                Logger.info("Error: " + e.getMessage());
            }
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result deleteProduct(Long id) {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            try {
                ProductMerchant productMerchant = ProductMerchantRepository.findById(id, ownMerchant);
                if (productMerchant != null) {
                    productMerchant.setIsActive(false);
                    productMerchant.isDeleted = true;
                    productMerchant.update();
                    response.setBaseResponse(1, 0, 1, success + " Deleted data product", productMerchant.id);
                    return ok(Json.toJson(response));
                }
            } catch (Exception e) {
                Logger.info("Error: " + e.getMessage());
            }
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }


    // =============================================== construct =====================================================//

    private static ProductResponse toResponse(ProductMerchant productMerchant) {
        ProductResponse productResponse = new ProductResponse();
        productResponse.setProductName(productMerchant.getProductName());
        // ================================================================ //
        CategoryMerchant categoryMerchant = CategoryMerchantRepository.findByIdAndMerchantId(productMerchant.getCategoryMerchant().id, productMerchant.getMerchant().id);
        if (categoryMerchant != null) {
            ProductResponse.CategoryResponse categoryResponse = ProductResponse.CategoryResponse.builder()
                    .id(categoryMerchant.id)
                    .categoryName(categoryMerchant.getCategoryName())
                    .build();
            productResponse.setCategory(categoryResponse);
        }
        SubCategoryMerchant subCategoryMerchant = SubCategoryMerchantRepository.findByIdAndMerchantId(productMerchant.getSubCategoryMerchant().id, productMerchant.getMerchant().id);
        if (subCategoryMerchant != null) {
            ProductResponse.SubCategoryResponse subCategoryResponse = ProductResponse.SubCategoryResponse.builder()
                    .id(subCategoryMerchant.id)
                    .subCategoryName(subCategoryMerchant.getSubcategoryName())
                    .build();
            productResponse.setSubCategory(subCategoryResponse);
        }
        BrandMerchant brandMerchant = BrandMerchantRepository.findByIdAndMerchantId(productMerchant.getBrandMerchant().id, productMerchant.getMerchant().id);
        if (brandMerchant != null) {
            ProductResponse.BrandResponse brandResponse = ProductResponse.BrandResponse.builder()
                    .id(brandMerchant.id)
                    .brandName(brandMerchant.getBrandName())
                    .build();
            productResponse.setBrand(brandResponse);
        }
        // ================================================================ //

        ProductMerchantDetail productMerchantDetail = ProductMerchantDetailRepository.findByProduct(productMerchant);
        if (productMerchantDetail != null) {
            ProductDetailResponse productDetailResponse = ProductDetailResponse.builder()
                    .productType(productMerchantDetail.getProductType())
                    .isCustomizable(productMerchantDetail.getIsCustomizable())
                    .productPrice(productMerchantDetail.getProductPrice())
                    .discountType(productMerchantDetail.getDiscountType())
                    .discount(productMerchantDetail.getDiscount())
                    .productPriceAfterDiscount(productMerchantDetail.getProductPriceAfterDiscount())
                    .productImageMain(productMerchantDetail.getProductImageMain())
                    .productImage1(productMerchantDetail.getProductImage1())
                    .productImage2(productMerchantDetail.getProductImage2())
                    .productImage3(productMerchantDetail.getProductImage3())
                    .productImage4(productMerchantDetail.getProductImage4())
                    .build();
            productResponse.setProductDetail(productDetailResponse);
        }

        ProductMerchantDescription productMerchantDescription = ProductMerchantDescriptionRepository.findByProductMerchantDetail(productMerchantDetail);
        if (productMerchantDescription != null) {
            ProductDescriptionResponse productDescriptionResponse = ProductDescriptionResponse.builder()
                    .shortDescription(productMerchantDescription.getShortDescription())
                    .longDescription(productMerchantDescription.getLongDescription())
                    .build();
            productResponse.setProductDescription(productDescriptionResponse);
        }
        return productResponse;
    }

    private static List<ProductResponse> toResponses(List<ProductMerchant> productMerchants) {
        List<ProductResponse> productResponses = new ArrayList<>();
        for (ProductMerchant productMerchant : productMerchants) {
            productResponses.add(toResponse(productMerchant));
        }
        return productResponses;
    }

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
        newProductMerchantDetail.setProductType(productRequest.getProductDetailRequest().getProductType());
        newProductMerchantDetail.setIsCustomizable(productRequest.getProductDetailRequest().getIsCustomizable());
        newProductMerchantDetail.setProductPrice(productRequest.getProductDetailRequest().getProductPrice());
        newProductMerchantDetail.setDiscountType(productRequest.getProductDetailRequest().getDiscountType());
        newProductMerchantDetail.setDiscount(productRequest.getProductDetailRequest().getDiscount());
        newProductMerchantDetail.setProductPriceAfterDiscount(productRequest.getProductDetailRequest().getProductPriceAfterDiscount());
        newProductMerchantDetail.setProductImageMain(productRequest.getProductDetailRequest().getProductImageMain());
        newProductMerchantDetail.setProductImage1(productRequest.getProductDetailRequest().getProductImage1());
        newProductMerchantDetail.setProductImage2(productRequest.getProductDetailRequest().getProductImage2());
        newProductMerchantDetail.setProductImage3(productRequest.getProductDetailRequest().getProductImage3());
        newProductMerchantDetail.setProductImage4(productRequest.getProductDetailRequest().getProductImage4());
        newProductMerchantDetail.setProductMerchant(newProductMerchant);
    }

}
