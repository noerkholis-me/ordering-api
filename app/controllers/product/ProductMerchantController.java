package controllers.product;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Query;
import com.avaje.ebean.Transaction;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hokeba.api.BaseResponse;
import controllers.BaseController;
import dtos.product.*;
import models.*;
import models.merchant.ProductMerchant;
import models.merchant.ProductMerchantDescription;
import models.merchant.ProductMerchantDetail;
import models.productaddon.*;
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
                    if (categoryMerchant == null) {
                        response.setBaseResponse(0, 0, 0, " Category not found.", null);
                        return badRequest(Json.toJson(response));
                    }
                    SubCategoryMerchant subCategoryMerchant = SubCategoryMerchantRepository.findByIdAndMerchantId(
                            productRequest.getSubCategoryId(), ownMerchant.id);
                    if (subCategoryMerchant == null) {
                        response.setBaseResponse(0, 0, 0, " Sub Category not found.", null);
                        return badRequest(Json.toJson(response));
                    }
                    SubsCategoryMerchant subsCategoryMerchant = SubsCategoryMerchantRepository.findByIdAndMerchantId(
                            productRequest.getSubsCategoryId(), ownMerchant.id);
                    if (subsCategoryMerchant == null) {
                        response.setBaseResponse(0, 0, 0, " Subs Category not found.", null);
                        return badRequest(Json.toJson(response));
                    }
                    BrandMerchant brandMerchant = BrandMerchantRepository.findByIdAndMerchantId(
                            productRequest.getBrandId(), ownMerchant.id);
                    if (brandMerchant == null) {
                        response.setBaseResponse(0, 0, 0, " Brand not found.", null);
                        return badRequest(Json.toJson(response));
                    }
                    constructProductEntityRequest(newProductMerchant, ownMerchant, productRequest, categoryMerchant,
                            subCategoryMerchant, subsCategoryMerchant, brandMerchant);
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
                    if (categoryMerchant == null) {
                        response.setBaseResponse(0, 0, 0, " Category not found.", null);
                        return badRequest(Json.toJson(response));
                    }
                    SubCategoryMerchant subCategoryMerchant = SubCategoryMerchantRepository.findByIdAndMerchantId(
                            productRequest.getSubCategoryId(), ownMerchant.id);
                    if (subCategoryMerchant == null) {
                        response.setBaseResponse(0, 0, 0, " Sub Category not found.", null);
                        return badRequest(Json.toJson(response));
                    }
                    SubsCategoryMerchant subsCategoryMerchant = SubsCategoryMerchantRepository.findByIdAndMerchantId(
                            productRequest.getSubsCategoryId(), ownMerchant.id);
                    if (subsCategoryMerchant == null) {
                        response.setBaseResponse(0, 0, 0, " Subs Category not found.", null);
                        return badRequest(Json.toJson(response));
                    }
                    BrandMerchant brandMerchant = BrandMerchantRepository.findByIdAndMerchantId(
                            productRequest.getBrandId(), ownMerchant.id);
                    if (brandMerchant == null) {
                        response.setBaseResponse(0, 0, 0, " Brand not found.", null);
                        return badRequest(Json.toJson(response));
                    }
                    constructProductEntityRequest(productMerchant, ownMerchant, productRequest, categoryMerchant,
                            subCategoryMerchant, subsCategoryMerchant, brandMerchant);
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

    public static Result updateStatusProduct(Long id) {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            JsonNode json = request().body().asJson();
            try {
                UpdateStatusRequest updateStatusRequest = objectMapper.readValue(json.toString(), UpdateStatusRequest.class);
                ProductMerchant productMerchant = ProductMerchantRepository.findById(id, ownMerchant);
                if (productMerchant != null) {
                    ProductMerchantDetail productMerchantDetail = ProductMerchantDetailRepository.findByProduct(productMerchant);
                    if (updateStatusRequest.getIsActive() != productMerchant.getIsActive()) {
                        productMerchant.setIsActive(updateStatusRequest.getIsActive());
                        productMerchant.update();
                    } else if (updateStatusRequest.getIsCustomizable() != productMerchantDetail.getIsCustomizable()) {
                        productMerchantDetail.setIsCustomizable(updateStatusRequest.getIsCustomizable());
                        productMerchantDetail.update();
                    } else {
                        productMerchant.setIsActive(updateStatusRequest.getIsActive());
                        productMerchantDetail.setIsCustomizable(updateStatusRequest.getIsCustomizable());
                        productMerchant.update();
                        productMerchantDetail.update();
                    }
                    response.setBaseResponse(1, 0, 1, success + " Update status data product", productMerchant.id);
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
        productResponse.setProductId(productMerchant.id);
        productResponse.setProductName(productMerchant.getProductName());
        productResponse.setIsActive(productMerchant.getIsActive());
        productResponse.setMerchantId(productMerchant.getMerchant().id);
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
        SubsCategoryMerchant subsCategoryMerchant = SubsCategoryMerchantRepository.findByIdAndMerchantId(productMerchant.getSubsCategoryMerchant().id, productMerchant.getMerchant().id);
        if (subCategoryMerchant != null) {
            ProductResponse.SubsCategoryResponse subsCategoryResponse = ProductResponse.SubsCategoryResponse.builder()
                    .id(subsCategoryMerchant.id)
                    .subsCategoryName(subsCategoryMerchant.getSubscategoryName())
                    .build();
            productResponse.setSubsCategory(subsCategoryResponse);
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
                                                      SubCategoryMerchant subCategoryMerchant, SubsCategoryMerchant subsCategoryMerchant, BrandMerchant brandMerchant) {
        newProductMerchant.setProductName(productRequest.getProductName());
        newProductMerchant.setIsActive(Boolean.TRUE);
        newProductMerchant.setCategoryMerchant(categoryMerchant);
        newProductMerchant.setSubCategoryMerchant(subCategoryMerchant);
        newProductMerchant.setSubsCategoryMerchant(subsCategoryMerchant);
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

    // PRODUK REKOMENDASI

    public static Result listRecommendedProduct(Long merchantId){
        if (merchantId != null) {
            Transaction trx = Ebean.beginTransaction();
            try {

                String querySql = "t0.product_merchant_id in (select pm.id from product_merchant pm where pm.is_active = "+true+" and pm.is_deleted = "+false+" and pm.merchant_id = "+merchantId+")";
                Query<ProductMerchantDetail> query = ProductMerchantDetailRepository.find.where().raw(querySql).eq("t0.is_deleted", false).eq("t0.product_type", "MAIN").order("random()");
                List<ProductMerchantDetail> totalDataProductDetail = ProductMerchantDetailRepository.getTotalDataPage(query);
                List<ProductMerchantDetail> productMerchantDetails = ProductMerchantDetailRepository.forProductRecommendation(query);
                List<ProductResponse> productMerchantResponse = new ArrayList<>();

                
                for(ProductMerchantDetail productMerchantDetail : productMerchantDetails){
                    ProductResponse productResponse = new ProductResponse();
                    ProductMerchant productMerchant = ProductMerchantRepository.findByIdProductRecommend(productMerchantDetail.getProductMerchant().id, merchantId);
                    
                    productResponse.setProductId(productMerchantDetail.getProductMerchant().id);
                    productResponse.setProductName(productMerchant.getProductName());
                    productResponse.setIsActive(productMerchant.getIsActive());
                    productResponse.setMerchantId(productMerchant.getMerchant().id);

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
                    SubsCategoryMerchant subsCategoryMerchant = SubsCategoryMerchantRepository.findByIdAndMerchantId(productMerchant.getSubsCategoryMerchant().id, productMerchant.getMerchant().id);
                    if (subCategoryMerchant != null) {
                        ProductResponse.SubsCategoryResponse subsCategoryResponse = ProductResponse.SubsCategoryResponse.builder()
                                .id(subsCategoryMerchant.id)
                                .subsCategoryName(subsCategoryMerchant.getSubscategoryName())
                                .build();
                        productResponse.setSubsCategory(subsCategoryResponse);
                    }
                    BrandMerchant brandMerchant = BrandMerchantRepository.findByIdAndMerchantId(productMerchant.getBrandMerchant().id, productMerchant.getMerchant().id);
                    if (brandMerchant != null) {
                        ProductResponse.BrandResponse brandResponse = ProductResponse.BrandResponse.builder()
                                .id(brandMerchant.id)
                                .brandName(brandMerchant.getBrandName())
                                .build();
                        productResponse.setBrand(brandResponse);
                    }

                    ProductMerchantDescription productMerchantDescription = ProductMerchantDescriptionRepository.findByProductMerchantDetail(productMerchantDetail);
                    if (productMerchantDescription != null) {
                        ProductDescriptionResponse productDescriptionResponse = ProductDescriptionResponse.builder()
                                .shortDescription(productMerchantDescription.getShortDescription())
                                .longDescription(productMerchantDescription.getLongDescription())
                                .build();
                        productResponse.setProductDescription(productDescriptionResponse);
                    }

                    productMerchantResponse.add(productResponse);
                }
                response.setBaseResponse(filter == null || filter.equals("") ? totalDataProductDetail.size() : productMerchantResponse.size(), offset, limit, success + " Showing data products", productMerchantResponse);
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
        response.setBaseResponse(0, 0, 0, "Merchant tidak ditemukan", null);
        return badRequest(Json.toJson(response));

    }


    // PRODUCT DETAIL CUSTOMER

    public static Result productDetail(Long productId, Long merchantId, Long storeId){
        if (merchantId != null) {
            Transaction trx = Ebean.beginTransaction();
            try {
                ProductMerchantDetail productMerchantDetail = ProductMerchantDetailRepository.findDetailProduct(productId, merchantId);
                if(productMerchantDetail != null){

                    ProductResponse productResponse = new ProductResponse();

                    ProductStore productStore = ProductStoreRepository.findForCust(productId, storeId, merchantId);
                
                    ProductMerchant productMerchant = ProductMerchantRepository.findByIdProductRecommend(productMerchantDetail.getProductMerchant().id, merchantId);
                    productResponse.setProductId(productMerchant.id);
                    productResponse.setProductName(productMerchant.getProductName());
                    productResponse.setIsActive(productMerchant.getIsActive());
                    productResponse.setMerchantId(productMerchant.getMerchant().id);

                    ProductDetailResponse productDetailResponse = ProductDetailResponse.builder()
                                .productType(productMerchantDetail.getProductType())
                                .isCustomizable(productMerchantDetail.getIsCustomizable())
                                .productPrice(productStore != null ? productStore.getStorePrice() : productMerchantDetail.getProductPrice())
                                .discountType(productStore != null ? productStore.getDiscountType() : productMerchantDetail.getDiscountType())
                                .discount(productStore != null ? productStore.getDiscount() : productMerchantDetail.getDiscount())
                                .productPriceAfterDiscount(productStore != null ? productStore.getFinalPrice() : productMerchantDetail.getProductPriceAfterDiscount())
                                .productImageMain(productMerchantDetail.getProductImageMain())
                                .productImage1(productMerchantDetail.getProductImage1())
                                .productImage2(productMerchantDetail.getProductImage2())
                                .productImage3(productMerchantDetail.getProductImage3())
                                .productImage4(productMerchantDetail.getProductImage4())
                                .build();
                    productResponse.setProductDetail(productDetailResponse);

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
                    SubsCategoryMerchant subsCategoryMerchant = SubsCategoryMerchantRepository.findByIdAndMerchantId(productMerchant.getSubsCategoryMerchant().id, productMerchant.getMerchant().id);
                    if (subCategoryMerchant != null) {
                        ProductResponse.SubsCategoryResponse subsCategoryResponse = ProductResponse.SubsCategoryResponse.builder()
                                .id(subsCategoryMerchant.id)
                                .subsCategoryName(subsCategoryMerchant.getSubscategoryName())
                                .build();
                        productResponse.setSubsCategory(subsCategoryResponse);
                    }
                    BrandMerchant brandMerchant = BrandMerchantRepository.findByIdAndMerchantId(productMerchant.getBrandMerchant().id, productMerchant.getMerchant().id);
                    if (brandMerchant != null) {
                        ProductResponse.BrandResponse brandResponse = ProductResponse.BrandResponse.builder()
                                .id(brandMerchant.id)
                                .brandName(brandMerchant.getBrandName())
                                .build();
                        productResponse.setBrand(brandResponse);
                    }

                    ProductMerchantDescription productMerchantDescription = ProductMerchantDescriptionRepository.findByProductMerchantDetail(productMerchantDetail);
                    if (productMerchantDescription != null) {
                        ProductDescriptionResponse productDescriptionResponse = ProductDescriptionResponse.builder()
                                .shortDescription(productMerchantDescription.getShortDescription())
                                .longDescription(productMerchantDescription.getLongDescription())
                                .build();
                        productResponse.setProductDescription(productDescriptionResponse);
                    }

                    // productMerchantResponse.add(productResponse);
                    response.setBaseResponse(1, 0, 1, success + " menampilkan data produk", productResponse);
                    return ok(Json.toJson(response));
                } else {
                    response.setBaseResponse(0, 0, 0, "Produk tidak ditemukan", null);
                    return badRequest(Json.toJson(response));
                }
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
        response.setBaseResponse(0, 0, 0, "Merchant tidak ditemukan", null);
        return badRequest(Json.toJson(response));

    }

    public static Result productAdditional(Long productId, Long merchantId, Long storeId){
        if (merchantId != null) {
            String querySql = "t0.product_merchant_id in (select pm.id from product_merchant pm where pm.id = "+productId+" pm.merchant_id = "+merchantId+" and pm.is_active = "+true+" and pm.is_deleted = "+false+")";
            String groupBy = "GROUP BY t0.id, t0.product_type";
            Query<ProductMerchantDetail> query = ProductMerchantDetailRepository.find.where().raw(querySql).eq("t0.is_deleted", false).eq("t0.product_type", "MAIN").order("random()");
            try {
                ProductMerchantDetail productMerchantDetail = ProductMerchantDetailRepository.findDetailProduct(productId, merchantId);
                    
                ProductCustomerAdditionalResponse responseData = new ProductCustomerAdditionalResponse();
                ProductMerchant productMerchant = ProductMerchantRepository.findByIdProductRecommend(productMerchantDetail.getProductMerchant().id, merchantId);

                responseData.setId(productMerchant.id);
                responseData.setProductId(productMerchant.id);
                responseData.setProductName(productMerchant.getProductName());
                responseData.setMerchantId(productMerchant.getMerchant().id);
                    
                Query<ProductAddOn> queryProductAddOn = ProductAddOnRepository.find.where().raw("t0.product_id = " + productMerchantDetail.getProductMerchant().id + "AND t0.is_active = " + true + " AND t0.is_deleted = " + false + " AND t0.merchant_id = " + merchantId + " " + groupBy).order("t0.product_type asc");
                List<ProductAddOn> dataAddOn = ProductAddOnRepository.getDataForAddOn(queryProductAddOn);
                List<ProductCustomerAdditionalResponse.ProductAddOn> responsesProductAddOn = new ArrayList<>();
                    
                for(ProductAddOn dataProductAddOn : dataAddOn) {

                    ProductStore productStore = ProductStoreRepository.findForCust(dataProductAddOn.getProductAssignId(), storeId, merchantId);
                    ProductMerchantDetail forDetail = ProductMerchantDetailRepository.findDetailAdditionalProduct(dataProductAddOn.getProductAssignId(), merchantId);
                    ProductMerchant productMerchantAssign = ProductMerchantRepository.findByIdProductRecommend(dataProductAddOn.getProductAssignId(), merchantId);
                    
                    ProductCustomerAdditionalResponse.ProductAddOn responseAddOn = new ProductCustomerAdditionalResponse.ProductAddOn();
                    responseAddOn.setProductId(dataProductAddOn.getProductMerchant().id);
                    responseAddOn.setProductAssignId(dataProductAddOn.getProductAssignId());
                    responseAddOn.setProductName(productMerchantAssign.getProductName());
                    responseAddOn.setProductType(dataProductAddOn.getProductType());
                    if (productStore != null) {
                        responseAddOn.setProductPrice(productStore.getStorePrice());
                        responseAddOn.setDiscountType(productStore.getDiscountType());
                        responseAddOn.setDiscount(productStore.getDiscount());
                        responseAddOn.setProductPriceAfterDiscount(productStore.getFinalPrice());
                    } else {
                        responseAddOn.setProductPrice(forDetail.getProductPrice());
                        responseAddOn.setDiscountType(forDetail.getDiscountType());
                        responseAddOn.setDiscount(forDetail.getDiscount());
                        responseAddOn.setProductPriceAfterDiscount(forDetail.getProductPriceAfterDiscount());
                    }
                    responseAddOn.setProductImageMain(forDetail.getProductImageMain());
                    responseAddOn.setProductImage1(forDetail.getProductImage1());
                    responseAddOn.setProductImage2(forDetail.getProductImage2());
                    responseAddOn.setProductImage3(forDetail.getProductImage3());
                    responseAddOn.setProductImage4(forDetail.getProductImage4());
                    responsesProductAddOn.add(responseAddOn);
                    responseData.setProductAddOn(responseAddOn != null ? responsesProductAddOn : null);
                }
                response.setBaseResponse(1, 0, 1, "Berhasil menampilkan data", responseData);
                return ok(Json.toJson(response));
            } catch (Exception e) {
                Logger.error("Error", e);
            }
            response.setBaseResponse(0, 0, 0, error, null);
            return badRequest(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, "Merchant tidak ditemukan", null);
        return badRequest(Json.toJson(response));
    }

    public static Result listAdditionalProductMerchant(){
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            String querySql = "t0.product_merchant_id in (select pm.id from product_merchant pm where pm.merchant_id = "+ownMerchant.id+" and pm.is_active = "+true+" and pm.is_deleted = "+false+")";
            Query<ProductMerchantDetail> query = ProductMerchantDetailRepository.find.where().raw(querySql).eq("t0.is_deleted", false).eq("t0.product_type", "ADDITIONAL").order("t0.id");
            try {
                List<ProductMerchantDetail> productMerchantDetailList = ProductMerchantDetailRepository.findDataAdditionalForMerchant(query, ownMerchant.id);
                List<ProductAdditionalMerchantResponse> responseDatas = new ArrayList<>();
                for(ProductMerchantDetail detail: productMerchantDetailList) {
                    ProductAdditionalMerchantResponse responseData = new ProductAdditionalMerchantResponse();
                    ProductMerchant productMerchant = ProductMerchantRepository.findByIdProductRecommend(detail.getProductMerchant().id, ownMerchant.id);
                    responseData.setId(productMerchant.id);
                    responseData.setProductName(productMerchant.getProductName());
                    responseData.setMerchantId(productMerchant.getMerchant().id);
                    responseDatas.add(responseData);
                } 
                
                response.setBaseResponse(1, 0, 1, "Berhasil menampilkan data", responseDatas);
                return ok(Json.toJson(response));
            } catch (Exception e) {
                Logger.error("Error", e);
            }
            response.setBaseResponse(0, 0, 0, error, null);
            return badRequest(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, "Akses ditolak", null);
        return badRequest(Json.toJson(response));
    }

    // FOR HOME CUSTOMER
    public static Result productListKiosk(Long brandId, Long merchantId, Long storeId, Long categoryId) {
        if (brandId != null) {
            Transaction trx = Ebean.beginTransaction();
            try {

                String querySql = "t0.product_merchant_id in (select pm.id from product_merchant pm where pm.merchant_id = "+merchantId+" and pm.subs_category_merchant_id = "+categoryId+" and pm.brand_merchant_id = "+brandId+" and pm.is_active = "+true+" and pm.is_deleted = false)";
                Query<ProductMerchantDetail> query = ProductMerchantDetailRepository.find.where().raw(querySql).eq("t0.is_deleted", false).eq("t0.product_type", "MAIN").order("t0.id asc");
                List<ProductMerchantDetail> dataProductDetail = ProductMerchantDetailRepository.getAllDataKiosK(query);

                List<ProductKiosKResponse> listProductResponseKiosK = new ArrayList<>();
                for(ProductMerchantDetail productMerchantDetail : dataProductDetail){
                    ProductKiosKResponse productResponseKiosK = new ProductKiosKResponse();
                    ProductMerchant productMerchant = ProductMerchantRepository.findByIdProductRecommend(productMerchantDetail.getProductMerchant().id, merchantId);
                    ProductStore productStore = ProductStoreRepository.findForCust(productMerchant.id, storeId, merchantId);
                    productResponseKiosK.setProductId(productMerchant.id);
                    productResponseKiosK.setProductName(productMerchant.getProductName());
                    productResponseKiosK.setProductType(productMerchantDetail.getProductType());
                    productResponseKiosK.setIsCustomizable(productMerchantDetail.getIsCustomizable());
                    productResponseKiosK.setIsActive(productMerchant.getIsActive());
                    productResponseKiosK.setMerchantId(productMerchant.getMerchant().id);
                    
                    if(productStore != null) {
                        productResponseKiosK.setProductPrice(productStore.getStorePrice());
                        productResponseKiosK.setDiscountType(productStore.getDiscountType());
                        productResponseKiosK.setDiscount(productStore.getDiscount());
                        productResponseKiosK.setProductPriceAfterDiscount(productStore.getFinalPrice());
                    } else {
                        productResponseKiosK.setProductPrice(productMerchantDetail.getProductPrice());
                        productResponseKiosK.setDiscountType(productMerchantDetail.getDiscountType());
                        productResponseKiosK.setDiscount(productMerchantDetail.getDiscount());
                        productResponseKiosK.setProductPriceAfterDiscount(productMerchantDetail.getProductPriceAfterDiscount());
                    }
    
                    ProductMerchantDescription productMerchantDescription = ProductMerchantDescriptionRepository.findByProductMerchantDetail(productMerchantDetail);
                    if (productMerchantDescription != null) {
                        productResponseKiosK.setShortDescription(productMerchantDescription.getShortDescription());
                        productResponseKiosK.setLongDescription(productMerchantDescription.getLongDescription());
                    }

                    productResponseKiosK.setProductImageMain(productMerchantDetail.getProductImageMain());
                    listProductResponseKiosK.add(productResponseKiosK);
                }
                
                response.setBaseResponse(dataProductDetail.size(), 0, 0, "Berhasil menampilkan produk", listProductResponseKiosK);
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