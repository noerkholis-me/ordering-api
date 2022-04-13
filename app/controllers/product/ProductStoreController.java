package controllers.product;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Transaction;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hokeba.api.BaseResponse;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiImplicitParam;
import com.wordnik.swagger.annotations.ApiImplicitParams;
import com.wordnik.swagger.annotations.ApiOperation;
import controllers.BaseController;
import dtos.product.*;
import dtos.store.*;
import models.*;
import models.merchant.*;
import play.Logger;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Http;
import play.mvc.Result;
import repository.*;

import java.io.File;
import java.util.*;
import com.avaje.ebean.Query;
import utils.ImageDirectory;
import utils.ImageUtil;

import java.io.IOException;

@Api(value = "/merchants/productstore", description = "Product Store")
public class ProductStoreController extends BaseController {

    private final static Logger.ALogger logger = Logger.of(ProductStoreController.class);

    private static BaseResponse response = new BaseResponse();

    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    public static Result createProductStore() {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            try {
                JsonNode json = request().body().asJson();
                
                ProductStoreResponse request = objectMapper.readValue(json.toString(), ProductStoreResponse.class);
                ProductMerchant productMerchant = ProductMerchantRepository.findById(request.getProductId(), ownMerchant);
                if (productMerchant == null) {
                    response.setBaseResponse(0, 0, 0, " Product not found.", null);
                    return badRequest(Json.toJson(response));
                }
                Store store = Store.findById(request.getStoreId());
                if (store == null) {
                    response.setBaseResponse(0, 0, 0, " Product not found.", null);
                    return badRequest(Json.toJson(response));
                }
                String validate = validateCreateProductStore(request);
                if (validate == null) {
                    Transaction trx = Ebean.beginTransaction();
                    try {
                        ProductStore newProductStore = new ProductStore();
                        newProductStore.setStore(store);
                        newProductStore.setProductMerchant(productMerchant);
                        newProductStore.setMerchant(ownMerchant);
                        newProductStore.setActive(request.getIsActive());
                        newProductStore.setStorePrice(request.getStorePrice());
                        if(request.getDiscountType() != null){
                            newProductStore.setDiscountType(request.getDiscountType());
                        }
                        if(request.getDiscount() != null){
                            newProductStore.setDiscount(request.getDiscount());
                        }
                        if(request.getFinalPrice() != null){
                            newProductStore.setFinalPrice(request.getFinalPrice());
                        }
                        newProductStore.save();

                        trx.commit();
                        response.setBaseResponse(1, offset, 1, success + " membuat produk toko", newProductStore);
                        return ok(Json.toJson(response));
                    } catch (Exception e) {
                        logger.error("Error saat membuat produk toko", e);
                        e.printStackTrace();
                        trx.rollback();
                    } finally {
                        trx.end();
                    }
                    response.setBaseResponse(0, 0, 0, error, null);
                    return badRequest(Json.toJson(response));
                }
                response.setBaseResponse(0, 0, 0, validate, null);
                return badRequest(Json.toJson(response));
            } catch (Exception e) {
                logger.error("Error saat parsing json", e);
                e.printStackTrace();
            }
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }


    public static String validateCreateProductStore(ProductStoreResponse request) {
        if (request == null)
            return "Bidang tidak boleh nol atau kosong";
        if (request.getStoreId() == null)
            return "Store tidak boleh nol atau kosong";
        if (request.getStorePrice() == null)
            return "Harga tidak boleh nol atau kosong";
        if (request.getProductId() == null)
            return "Id Produk tidak boleh nol atau kosong";

        return null;
    }

    @ApiOperation(value = "Get all product store.", notes = "Returns list of product store.\n" + swaggerInfo
            + "", response = Product.class, responseContainer = "List", httpMethod = "GET")
    public static Result listProductStore(String filter, String sort, int offset, int limit) {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            Query<ProductMerchant> query = ProductMerchantRepository.findProductIsActiveAndMerchant(ownMerchant, true);
            try {
                List<ProductResponse> responses = new ArrayList<>();
                List<ProductMerchant> totalData = ProductMerchantRepository.getTotalDataPage(query);
                List<ProductMerchant> productMerchants = ProductMerchantRepository.findProductWithPaging(query, sort, filter, offset, limit);
                for (ProductMerchant data : productMerchants) {
                    ProductResponse response = new ProductResponse();
                    Query<ProductStore> queryPS = ProductStoreRepository.find.where().eq("t0.product_id", data.id).eq("t0.is_deleted", false).eq("t0.merchant_id", getUserMerchant().id).order("t0.id");
                    List<ProductStore> dataPS = ProductStoreRepository.getDataProductStore(queryPS);
                    List<ProductResponse.ProductStore> responsesProductStore = new ArrayList<>();
                    response.setProductId(data.id);
                    response.setProductName(data.getProductName());
                    response.setIsActive(data.getIsActive());
                    // ================================================================ //
                    CategoryMerchant categoryMerchant = CategoryMerchantRepository.findByIdAndMerchantId(data.getCategoryMerchant().id, data.getMerchant().id);
                    if (categoryMerchant != null) {
                        ProductResponse.CategoryResponse categoryResponse = ProductResponse.CategoryResponse.builder()
                                .id(categoryMerchant.id)
                                .categoryName(categoryMerchant.getCategoryName())
                                .build();
                        response.setCategory(categoryResponse);
                    }
                    SubCategoryMerchant subCategoryMerchant = SubCategoryMerchantRepository.findByIdAndMerchantId(data.getSubCategoryMerchant().id, data.getMerchant().id);
                    if (subCategoryMerchant != null) {
                        ProductResponse.SubCategoryResponse subCategoryResponse = ProductResponse.SubCategoryResponse.builder()
                                .id(subCategoryMerchant.id)
                                .subCategoryName(subCategoryMerchant.getSubcategoryName())
                                .build();
                        response.setSubCategory(subCategoryResponse);
                    }
                    BrandMerchant brandMerchant = BrandMerchantRepository.findByIdAndMerchantId(data.getBrandMerchant().id, data.getMerchant().id);
                    if (brandMerchant != null) {
                        ProductResponse.BrandResponse brandResponse = ProductResponse.BrandResponse.builder()
                                .id(brandMerchant.id)
                                .brandName(brandMerchant.getBrandName())
                                .build();
                        response.setBrand(brandResponse);
                    }
                    // ================================================================ //

                    ProductMerchantDetail productMerchantDetail = ProductMerchantDetailRepository.findByProduct(data);
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
                        response.setProductDetail(productDetailResponse);
                    }

                    ProductMerchantDescription productMerchantDescription = ProductMerchantDescriptionRepository.findByProductMerchantDetail(productMerchantDetail);
                    if (productMerchantDescription != null) {
                        ProductDescriptionResponse productDescriptionResponse = ProductDescriptionResponse.builder()
                                .shortDescription(productMerchantDescription.getShortDescription())
                                .longDescription(productMerchantDescription.getLongDescription())
                                .build();
                        response.setProductDescription(productDescriptionResponse);
                    }
                    response.setMerchantId(data.getMerchant().id);
                    for(ProductStore dataPStore : dataPS) {
                        ProductResponse.ProductStore responsePStore = new ProductResponse.ProductStore();
                        
                        responsePStore.setId(dataPStore.getId());
                        responsePStore.setStoreId(dataPStore.getStore().id);
                        responsePStore.setProductId(dataPStore.getProductMerchant().id);
                        responsePStore.setMerchantId(dataPStore.getMerchant().id);
                        responsePStore.setIsActive(dataPStore.isActive);
                        responsePStore.setStorePrice(dataPStore.getStorePrice());
                        responsePStore.setDiscountType(dataPStore.getDiscountType());
                        responsePStore.setDiscount(dataPStore.getDiscount());
                        responsePStore.setIsDeleted(dataPStore.isDeleted);
                        responsePStore.setFinalPrice(dataPStore.getFinalPrice());
                        responsesProductStore.add(responsePStore);
                        response.setProductStore(responsePStore != null ? responsesProductStore : null);
                    }
                    responses.add(response);
                }
                response.setBaseResponse(filter == null || filter.equals("") ? totalData.size() : productMerchants.size() , offset, limit, success + " menampilkan data", responses);
                return ok(Json.toJson(response));
            } catch (IOException e) {
                Logger.error("allDetail", e);
            }
        } else if (ownMerchant == null) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result editProductStore(Long id) {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            try {
                JsonNode json = request().body().asJson();
                
                ProductStoreResponse request = objectMapper.readValue(json.toString(), ProductStoreResponse.class);
                String validate = validateCreateProductStore(request);
                if (validate == null) {
                    Transaction trx = Ebean.beginTransaction();
                    try {
                        ProductStore productStore = ProductStoreRepository.findByIdAndMerchantId(id, ownMerchant.id);
                        if (productStore == null) {
                            response.setBaseResponse(0, 0, 0, error + " product store tidak tersedia.", null);
                            return badRequest(Json.toJson(response));
                        }
                        ProductMerchant productMerchant = ProductMerchantRepository.findById(request.getProductId(), ownMerchant);
                        if (productMerchant == null) {
                            response.setBaseResponse(0, 0, 0, " Product not found.", null);
                            return badRequest(Json.toJson(response));
                        }
                        Store store = Store.findById(request.getStoreId());
                        if (productMerchant == null) {
                            response.setBaseResponse(0, 0, 0, " Product not found.", null);
                            return badRequest(Json.toJson(response));
                        }
                        productStore.setStore(store);
                        productStore.setProductMerchant(productMerchant);
                        productStore.setMerchant(ownMerchant);
                        productStore.setActive(request.getIsActive());
                        productStore.setStorePrice(request.getStorePrice());
                        if(request.getDiscountType() != null){
                            productStore.setDiscountType(request.getDiscountType());
                        }
                        if(request.getDiscount() != null){
                            productStore.setDiscount(request.getDiscount());
                        }
                        if(request.getFinalPrice() != null){
                            productStore.setFinalPrice(request.getFinalPrice());
                        }
                        productStore.update();

                        trx.commit();
                        response.setBaseResponse(1, offset, 1, success + " mengubah produk store", productStore);
                        return ok(Json.toJson(response));
                    } catch (Exception e) {
                        logger.error("Error saat mengubah produk store", e);
                        e.printStackTrace();
                        trx.rollback();
                    } finally {
                        trx.end();
                    }
                    response.setBaseResponse(0, 0, 0, error, null);
                    return badRequest(Json.toJson(response));
                }
                response.setBaseResponse(0, 0, 0, validate, null);
                return badRequest(Json.toJson(response));
            } catch (Exception e) {
                logger.error("Error saat parsing json", e);
                e.printStackTrace();
            }
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    @ApiOperation(value = "Delete Product Store", notes = "Delete Product Store.\n" + swaggerInfo
            + "", response = BaseResponse.class, httpMethod = "DELETE")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "product store form", dataType = "temp.swaggermap.ProductStoreForm", required = true, paramType = "body", value = "product store form") })
    public static Result deleteProductStore(Long id) {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
                if (id != null) {
                    Transaction trx = Ebean.beginTransaction();
                    try {
                        ProductStore productStore = ProductStoreRepository.findByIdAndMerchantId(id, ownMerchant.id);
                        if (productStore == null) {
                            response.setBaseResponse(0, 0, 0, error + " produk store tidak tersedia.", null);
                            return badRequest(Json.toJson(response));
                        }

                        productStore.isDeleted = true;
                        productStore.update();
                        trx.commit();

                        response.setBaseResponse(1,offset, 1, success + " menghapus produk store", productStore);
                        return ok(Json.toJson(response));
                    } catch (Exception e) {
                        logger.error("Error saat menghapus produk store", e);
                        e.printStackTrace();
                        trx.rollback();
                    } finally {
                        trx.end();
                    }
                    response.setBaseResponse(0, 0, 0, error, null);
                    return badRequest(Json.toJson(response));
                }
                response.setBaseResponse(0, 0, 0, "Tidak dapat menemukan produk store id", null);
                return badRequest(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    @ApiOperation(value = "Read Produk Store", notes = "Read Produk Store.\n" + swaggerInfo
            + "", response = BaseResponse.class, httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "produk store form", dataType = "temp.swaggermap.ProductStoreForm", required = true, paramType = "body", value = "product store form") })
    public static Result viewProductStore(Long id) {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            if (id != null) {
                Transaction trx = Ebean.beginTransaction();
                try {
                    ProductMerchant productMerchants = ProductMerchantRepository.findById(id, ownMerchant);
                    ProductResponse responseProduct = new ProductResponse();
                    Query<ProductStore> queryPS = ProductStoreRepository.find.where().eq("t0.product_id", productMerchants.id).eq("t0.is_deleted", false).eq("t0.merchant_id", getUserMerchant().id).order("t0.id");
                    List<ProductStore> dataPS = ProductStoreRepository.getDataProductStore(queryPS);
                    List<ProductResponse.ProductStore> responsesProductStore = new ArrayList<>();
                    responseProduct.setProductId(productMerchants.id);
                    responseProduct.setProductName(productMerchants.getProductName());
                    responseProduct.setIsActive(productMerchants.getIsActive());
                    // ================================================================ //
                    CategoryMerchant categoryMerchant = CategoryMerchantRepository.findByIdAndMerchantId(productMerchants.getCategoryMerchant().id, productMerchants.getMerchant().id);
                    if (categoryMerchant != null) {
                        ProductResponse.CategoryResponse categoryResponse = ProductResponse.CategoryResponse.builder()
                                .id(categoryMerchant.id)
                                .categoryName(categoryMerchant.getCategoryName())
                                .build();
                        responseProduct.setCategory(categoryResponse);
                    }
                    SubCategoryMerchant subCategoryMerchant = SubCategoryMerchantRepository.findByIdAndMerchantId(productMerchants.getSubCategoryMerchant().id, productMerchants.getMerchant().id);
                    if (subCategoryMerchant != null) {
                        ProductResponse.SubCategoryResponse subCategoryResponse = ProductResponse.SubCategoryResponse.builder()
                                .id(subCategoryMerchant.id)
                                .subCategoryName(subCategoryMerchant.getSubcategoryName())
                                .build();
                        responseProduct.setSubCategory(subCategoryResponse);
                    }
                    BrandMerchant brandMerchant = BrandMerchantRepository.findByIdAndMerchantId(productMerchants.getBrandMerchant().id, productMerchants.getMerchant().id);
                    if (brandMerchant != null) {
                        ProductResponse.BrandResponse brandResponse = ProductResponse.BrandResponse.builder()
                                .id(brandMerchant.id)
                                .brandName(brandMerchant.getBrandName())
                                .build();
                        responseProduct.setBrand(brandResponse);
                    }
                    // ================================================================ //

                    ProductMerchantDetail productMerchantDetail = ProductMerchantDetailRepository.findByProduct(productMerchants);
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
                        responseProduct.setProductDetail(productDetailResponse);
                    }

                    ProductMerchantDescription productMerchantDescription = ProductMerchantDescriptionRepository.findByProductMerchantDetail(productMerchantDetail);
                    if (productMerchantDescription != null) {
                        ProductDescriptionResponse productDescriptionResponse = ProductDescriptionResponse.builder()
                                .shortDescription(productMerchantDescription.getShortDescription())
                                .longDescription(productMerchantDescription.getLongDescription())
                                .build();
                        responseProduct.setProductDescription(productDescriptionResponse);
                    }
                    responseProduct.setMerchantId(productMerchants.getMerchant().id);
                    for(ProductStore dataPStore : dataPS) {
                        ProductResponse.ProductStore responsePStore = new ProductResponse.ProductStore();
                        
                        responsePStore.setId(dataPStore.getId());
                        responsePStore.setStoreId(dataPStore.getStore().id);
                        responsePStore.setProductId(dataPStore.getProductMerchant().id);
                        responsePStore.setMerchantId(dataPStore.getMerchant().id);
                        responsePStore.setIsActive(dataPStore.isActive);
                        responsePStore.setStorePrice(dataPStore.getStorePrice());
                        responsePStore.setDiscountType(dataPStore.getDiscountType());
                        responsePStore.setDiscount(dataPStore.getDiscount());
                        responsePStore.setIsDeleted(dataPStore.isDeleted);
                        responsePStore.setFinalPrice(dataPStore.getFinalPrice());

                        Store store = Store.findById(dataPStore.getStore().id);
                        if (store == null) {
                            response.setBaseResponse(0, 0, 0, " Store tidak ditemukan.", null);
                            return badRequest(Json.toJson(response));
                        }
                        responsePStore.setStoresName(store.storeName);
                        
                        responsesProductStore.add(responsePStore);
                        responseProduct.setProductStore(responsePStore != null ? responsesProductStore : null);
                    }
                    response.setBaseResponse(1, 0, 1, success + " menampilkan data", responseProduct);
                    return ok(Json.toJson(response));
                } catch (Exception e) {
                    logger.error("Error saat menampilkan detail produk store", e);
                    e.printStackTrace();
                    trx.rollback();
                } finally {
                    trx.end();
                }
                response.setBaseResponse(0, 0, 0, error, null);
                return badRequest(Json.toJson(response));
            }
            response.setBaseResponse(0, 0, 0, "Tidak dapat menemukan produk store id", null);
            return badRequest(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    @ApiOperation(value = "Status Produk Store", notes = "Status Produk Store.\n" + swaggerInfo
            + "", response = BaseResponse.class, httpMethod = "PUT")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "product store form", dataType = "temp.swaggermap.ProductStoreForm", required = true, paramType = "body", value = "product store form") })
        public static Result setStatus(Long id) {
            Merchant ownMerchant = checkMerchantAccessAuthorization();
            if (ownMerchant != null) {
                try {
                    JsonNode json = request().body().asJson();
                    ProductStoreResponse request = objectMapper.readValue(json.toString(), ProductStoreResponse.class);
                    Transaction trx = Ebean.beginTransaction();
                    try {
                        ProductStore productStore = ProductStoreRepository.findByIdAndMerchantId(id, ownMerchant.id);
                        if (productStore == null) {
                            response.setBaseResponse(0, 0, 0, error + " produk store tidak tersedia.", null);
                            return badRequest(Json.toJson(response));
                        }
                        productStore.setActive(request.getIsActive());
                        productStore.update();
    
                        trx.commit();
                        response.setBaseResponse(1, offset, 1, success + " mengubah status produk store", productStore);
                        return ok(Json.toJson(response));
                    } catch (Exception e) {
                        logger.error("Error saat mengubah status produk store", e);
                        e.printStackTrace();
                        trx.rollback();
                    } finally {
                        trx.end();
                    }
                    response.setBaseResponse(0, 0, 0, error, null);
                    return badRequest(Json.toJson(response));
                } catch (Exception e) {
                    logger.error("Error saat parsing json", e);
                    e.printStackTrace();
                }
            }
            response.setBaseResponse(0, 0, 0, unauthorized, null);
            return unauthorized(Json.toJson(response));
        }

}
