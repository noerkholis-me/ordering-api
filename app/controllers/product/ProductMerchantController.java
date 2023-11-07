package controllers.product;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Query;
import com.avaje.ebean.Transaction;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hokeba.api.BaseResponse;
import com.hokeba.util.Constant;
import controllers.BaseController;
import dtos.product.MiniPosAdditionalResponse;
import dtos.product.ProductAdditionalMerchantResponse;
import dtos.product.ProductCustomerAdditionalResponse;
import dtos.product.ProductDescriptionResponse;
import dtos.product.ProductDetailResponse;
import dtos.product.ProductRequest;
import dtos.product.ProductResponse;
import dtos.product.ProductResponseStore;
import dtos.product.ProductSpecificStoreResponse;
import dtos.product.ProductStoreResponse;
import dtos.product.ProductWithProductStoreRequest;
import dtos.product.UpdateStatusRequest;
import models.BrandMerchant;
import models.CategoryMerchant;
import models.Merchant;
import models.ProductStore;
import models.Store;
import models.SubCategoryMerchant;
import models.SubsCategoryMerchant;
import models.merchant.ProductMerchant;
import models.merchant.ProductMerchantDescription;
import models.merchant.ProductMerchantDetail;
import models.productaddon.ProductAddOn;
import models.productaddon.ProductAddOnType;
import play.Logger;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import repository.BrandMerchantRepository;
import repository.CategoryMerchantRepository;
import repository.ProductAddOnRepository;
import repository.ProductAddOnTypeRepository;
import repository.ProductMerchantDescriptionRepository;
import repository.ProductMerchantDetailRepository;
import repository.ProductMerchantRepository;
import repository.ProductStoreRepository;
import repository.SubCategoryMerchantRepository;
import repository.SubsCategoryMerchantRepository;
import service.ProductExcelService;
import validator.ProductValidator;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProductMerchantController extends BaseController {

    private final static Logger.ALogger logger = Logger.of(ProductMerchantController.class);

    private static BaseResponse response = new BaseResponse();

    private static final ObjectMapper objectMapper = new ObjectMapper();

    //TODO new create
    public static Result createProductWithProductStore() {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant == null) {
            response.setBaseResponse(0, 0, 0, unauthorized, null);
            return unauthorized(Json.toJson(response));
        }
        JsonNode json = request().body().asJson();
        try {
        	ProductWithProductStoreRequest productRequest = objectMapper.readValue(json.toString(), ProductWithProductStoreRequest.class);
            String validateRequest = ProductValidator.validateRequest(productRequest);
            if (validateRequest != null) {
                response.setBaseResponse(0, 0, 0, validateRequest, null);
                return badRequest(Json.toJson(response));
            }
            Transaction trx = Ebean.beginTransaction();
            try {
                CategoryMerchant categoryMerchant = CategoryMerchantRepository.findByIdAndMerchantId(
                        productRequest.getCategoryId(), ownMerchant);
                if (categoryMerchant == null) {
                    response.setBaseResponse(0, 0, 0, " Category not found.", null);
                    return badRequest(Json.toJson(response));
                }
                SubCategoryMerchant subCategoryMerchant = SubCategoryMerchantRepository.findByIdAndMerchantId(
                        productRequest.getSubCategoryId(), ownMerchant);
                if (subCategoryMerchant == null) {
                    response.setBaseResponse(0, 0, 0, " Sub Category not found.", null);
                    return badRequest(Json.toJson(response));
                }
                SubsCategoryMerchant subsCategoryMerchant = SubsCategoryMerchantRepository.findByIdAndMerchantId(
                        productRequest.getSubsCategoryId(), ownMerchant);
                if (subsCategoryMerchant == null) {
                    response.setBaseResponse(0, 0, 0, " Subs Category not found.", null);
                    return badRequest(Json.toJson(response));
                }
                BrandMerchant brandMerchant = BrandMerchantRepository.findByIdAndMerchantId(
                        productRequest.getBrandId(), ownMerchant);
                if (brandMerchant == null) {
                    response.setBaseResponse(0, 0, 0, " Brand not found.", null);
                    return badRequest(Json.toJson(response));
                }
                if (ownMerchant.productStoreRequired &&
                		(productRequest.getProductStoreRequests() == null || productRequest.getProductStoreRequests().isEmpty())) {
                	response.setBaseResponse(0, 0, 0, " Product needs to be assigned to store", null);
                    return badRequest(Json.toJson(response));
                }

                //create main product merchant
                ProductMerchant newProductMerchant = new ProductMerchant(ownMerchant, categoryMerchant, subCategoryMerchant, subsCategoryMerchant, brandMerchant, productRequest);
                newProductMerchant.save();

                // create product merchant detail
                ProductMerchantDetail newProductMerchantDetail = new ProductMerchantDetail(newProductMerchant, productRequest);
                newProductMerchantDetail.save();

                //create product merchant description
                ProductMerchantDescription newProductMerchantDescription = new ProductMerchantDescription(newProductMerchantDetail, productRequest);
                newProductMerchantDescription.save();
                
                //create product store (if any)
                for (ProductStoreResponse productStoreRequest : productRequest.getProductStoreRequests()) {
                    Store store = Store.findById(productStoreRequest.getStoreId());
                    if (store == null) {
                    	trx.rollback();
                        response.setBaseResponse(0, 0, 0, " Store not found.", null);
                        return badRequest(Json.toJson(response));
                    }
                    ProductStore psQuery = ProductStoreRepository.find.where().eq("productMerchant", newProductMerchant)
                            .eq("store", store).eq("t0.is_deleted", false).findUnique();
                    if (psQuery != null) {
                    	trx.rollback();
                        response.setBaseResponse(0, 0, 0,
                                "Tidak dapat menambahkan " + newProductMerchant.getProductName() + " ke toko yang sama.",
                                null);
                        return badRequest(Json.toJson(response));
                    }
                    
                    ProductStore newProductStore = new ProductStore(ownMerchant, store, newProductMerchant, productStoreRequest, true);
                    newProductStore.save();
				}
                
                trx.commit();

                response.setBaseResponse(1,offset, 1, success + " Product created successfully", newProductMerchant.id);
                return ok(Json.toJson(response));

            } catch (Exception e) {
                logger.error("Error while creating product", e);
                e.printStackTrace();
                trx.rollback();
                response.setBaseResponse(0, 0, 0, "Error while creating product" + e.toString(), null);
                return badRequest(Json.toJson(response));
            } finally {
                trx.end();
            }
        } catch (Exception e) {
            logger.error("Error while parsing json", e);
            e.printStackTrace();
            response.setBaseResponse(0, 0, 0, "Error while creating product" + e.toString(), null);
            return badRequest(Json.toJson(response));
        }
    }
    
    
    //TODO old create
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
                    if(productRequest.getProductName().length() > 50) {
                        response.setBaseResponse(0, 0, 0, "Jumlah karakter tidak boleh melebihi 50 karakter", null);
                        return badRequest(Json.toJson(response));
                    }
                    if ((productRequest.getProductDetailRequest().getProductPrice() != null) && productRequest.getProductDetailRequest().getProductPrice().compareTo(BigDecimal.ZERO) < 0){
                        response.setBaseResponse(0, 0, 0, "Harga tidak boleh kurang dari 0", null);
                        return badRequest(Json.toJson(response));
                    }
                    if ((productRequest.getProductDetailRequest().getDiscount() != null) && productRequest.getProductDetailRequest().getDiscount().compareTo(0D) < 0){
                        response.setBaseResponse(0, 0, 0, "Diskon tidak boleh kurang dari 0", null);
                        return badRequest(Json.toJson(response));
                    }
                    if ((productRequest.getProductDetailRequest().getProductPriceAfterDiscount() != null) && productRequest.getProductDetailRequest().getProductPriceAfterDiscount().compareTo(BigDecimal.ZERO) < 0){
                        response.setBaseResponse(0, 0, 0, "Harga Product Setelah Diskon Cashback tidak boleh kurang dari 0", null);
                        return badRequest(Json.toJson(response));
                    }
                    CategoryMerchant categoryMerchant = CategoryMerchantRepository.findByIdAndMerchantId(
                            productRequest.getCategoryId(), ownMerchant);
                    if (categoryMerchant == null) {
                        response.setBaseResponse(0, 0, 0, " Category not found.", null);
                        return badRequest(Json.toJson(response));
                    }
                    SubCategoryMerchant subCategoryMerchant = SubCategoryMerchantRepository.findByIdAndMerchantId(
                            productRequest.getSubCategoryId(), ownMerchant);
                    if (subCategoryMerchant == null) {
                        response.setBaseResponse(0, 0, 0, " Sub Category not found.", null);
                        return badRequest(Json.toJson(response));
                    }
                    SubsCategoryMerchant subsCategoryMerchant = SubsCategoryMerchantRepository.findByIdAndMerchantId(
                            productRequest.getSubsCategoryId(), ownMerchant);
                    if (subsCategoryMerchant == null) {
                        response.setBaseResponse(0, 0, 0, " Subs Category not found.", null);
                        return badRequest(Json.toJson(response));
                    }
                    BrandMerchant brandMerchant = BrandMerchantRepository.findByIdAndMerchantId(
                            productRequest.getBrandId(), ownMerchant);
                    if (brandMerchant == null) {
                        response.setBaseResponse(0, 0, 0, " Brand not found.", null);
                        return badRequest(Json.toJson(response));
                    }

                    //create main product merchant
                    ProductMerchant newProductMerchant = new ProductMerchant(ownMerchant, categoryMerchant, subCategoryMerchant, subsCategoryMerchant, brandMerchant, productRequest);
                    newProductMerchant.save();

                    // create product merchant detail
                    ProductMerchantDetail newProductMerchantDetail = new ProductMerchantDetail(newProductMerchant, productRequest);
                    newProductMerchantDetail.save();

                    //create product merchant description
                    ProductMerchantDescription newProductMerchantDescription = new ProductMerchantDescription(newProductMerchantDetail, productRequest);
                    newProductMerchantDescription.save();

                    trx.commit();

                    response.setBaseResponse(1,offset, 1, success + " Product created successfully", newProductMerchant.id);
                    return ok(Json.toJson(response));

                } catch (Exception e) {
                    logger.error("Error while creating product", e);
                    e.printStackTrace();
                    trx.rollback();
                    response.setBaseResponse(0, 0, 0, "Error while creating product" + e.toString(), null);
                    return badRequest(Json.toJson(response));
                } finally {
                    trx.end();
                }
            } catch (Exception e) {
                logger.error("Error while parsing json", e);
                e.printStackTrace();
                response.setBaseResponse(0, 0, 0, "Error while creating product" + e.toString(), null);
                return badRequest(Json.toJson(response));
            }
        }

        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    // TODO new update
    public static Result editProductWithProductStore(Long id) {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if(ownMerchant != null) {
            JsonNode json = request().body().asJson();
            try {
                ProductWithProductStoreRequest productRequest = objectMapper.readValue(json.toString(), ProductWithProductStoreRequest.class);
                if(productRequest.getProductName().length() > 50) {
                    response.setBaseResponse(0, 0, 0, "Jumlah karakter tidak boleh melebihi 50 karakter", null);
                    return badRequest(Json.toJson(response));
                }
                if ((productRequest.getProductDetailRequest().getProductPrice() != null) && productRequest.getProductDetailRequest().getProductPrice().compareTo(BigDecimal.ZERO) < 0){
                    response.setBaseResponse(0, 0, 0, "Harga tidak boleh kurang dari 0", null);
                    return badRequest(Json.toJson(response));
                }
                if ((productRequest.getProductDetailRequest().getDiscount() != null) && productRequest.getProductDetailRequest().getDiscount().compareTo(0D) < 0){
                    response.setBaseResponse(0, 0, 0, "Diskon tidak boleh kurang dari 0", null);
                    return badRequest(Json.toJson(response));
                }
                if ((productRequest.getProductDetailRequest().getProductPriceAfterDiscount() != null) && productRequest.getProductDetailRequest().getProductPriceAfterDiscount().compareTo(BigDecimal.ZERO) < 0){
                    response.setBaseResponse(0, 0, 0, "Harga Product Setelah Diskon Cashback tidak boleh kurang dari 0", null);
                    return badRequest(Json.toJson(response));
                }
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
                        productRequest.getCategoryId(), ownMerchant);
                    if (categoryMerchant == null) {
                        response.setBaseResponse(0, 0, 0, " Category not found.", null);
                        return badRequest(Json.toJson(response));
                    }
                    SubCategoryMerchant subCategoryMerchant = SubCategoryMerchantRepository.findByIdAndMerchantId(
                        productRequest.getSubCategoryId(), ownMerchant);
                    if (subCategoryMerchant == null) {
                        response.setBaseResponse(0, 0, 0, " Sub Category not found.", null);
                        return badRequest(Json.toJson(response));
                    }
                    SubsCategoryMerchant subsCategoryMerchant = SubsCategoryMerchantRepository.findByIdAndMerchantId(
                        productRequest.getSubsCategoryId(), ownMerchant);
                    if (subsCategoryMerchant == null) {
                        response.setBaseResponse(0, 0, 0, " Subs Category not found.", null);
                        return badRequest(Json.toJson(response));
                    }
                    BrandMerchant brandMerchant = BrandMerchantRepository.findByIdAndMerchantId(
                        productRequest.getBrandId(), ownMerchant);
                    if (brandMerchant == null) {
                        response.setBaseResponse(0, 0, 0, " Brand not found.", null);
                        return badRequest(Json.toJson(response));
                    }
                    if (ownMerchant.productStoreRequired &&
                        (productRequest.getProductStoreRequests() == null || productRequest.getProductStoreRequests().isEmpty())) {
                        response.setBaseResponse(0, 0, 0, " Product needs to be assigned to store", null);
                        return badRequest(Json.toJson(response));
                    }

                    // update product merchant
                    productMerchant.setProductMerchant(productMerchant, ownMerchant, categoryMerchant, subCategoryMerchant, subsCategoryMerchant, brandMerchant, productRequest);
                    productMerchant.update();

                    // update product merchant detail
                    ProductMerchantDetail productMerchantDetail = ProductMerchantDetailRepository.findByProduct(productMerchant);
                    productMerchantDetail.setProductMerchantDetail(productMerchantDetail, productMerchant, productRequest);
                    productMerchantDetail.update();

                    // update product merchant description
                    ProductMerchantDescription productMerchantDescription = ProductMerchantDescriptionRepository.findByProductMerchantDetail(productMerchantDetail);
                    productMerchantDescription.setProductMerchantDescription(productMerchantDescription, productMerchantDetail, productRequest);
                    productMerchantDescription.update();

                    // delete old product store
                    List<ProductStore> listProductStore = ProductStoreRepository.find.where().eq("t0.product_id", id).eq("merchant", ownMerchant).findList();
                    for (ProductStore productStore : listProductStore) {
                        ProductStore getProductStore = ProductStoreRepository.find.byId(productStore.id);
                        if (getProductStore == null) {
                            response.setBaseResponse(0, 0, 0, error + " product store tidak tersedia.", null);
                            return badRequest(Json.toJson(response));
                        } else {
                            getProductStore.delete();
                        }
                    }

                    // update product store (if any)
                    for (ProductStoreResponse productStoreRequest : productRequest.getProductStoreRequests()) {
                        Store store = Store.findById(productStoreRequest.getStoreId());
                        if (store == null) {
                            trx.rollback();
                            response.setBaseResponse(0, 0, 0, " Store not found.", null);
                            return badRequest(Json.toJson(response));
                        }

                        ProductStore psQuery = ProductStoreRepository.find.where().eq("productMerchant", productMerchant)
                            .eq("store", store).eq("t0.is_deleted", false).findUnique();
                        if (psQuery != null) {
                            trx.rollback();
                            response.setBaseResponse(0, 0, 0,
                                "Tidak dapat menambahkan " + productMerchant.getProductName() + " ke toko yang sama.",
                                null);
                            return badRequest(Json.toJson(response));
                        }

                        // insert new product store
                        ProductStore productStore = new ProductStore(ownMerchant, store, productMerchant, productStoreRequest, null);
                        productStore.save();
                    }

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

    // TODO old update
    public static Result editProduct(Long id) {

        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            JsonNode json = request().body().asJson();
            try {
                ProductRequest productRequest = objectMapper.readValue(json.toString(), ProductRequest.class);
                if(productRequest.getProductName().length() > 50) {
                    response.setBaseResponse(0, 0, 0, "Jumlah karakter tidak boleh melebihi 50 karakter", null);
                    return badRequest(Json.toJson(response));
                }

                if ((productRequest.getProductDetailRequest().getProductPrice() != null) && productRequest.getProductDetailRequest().getProductPrice().compareTo(BigDecimal.ZERO) < 0){
                    response.setBaseResponse(0, 0, 0, "Harga tidak boleh kurang dari 0", null);
                    return badRequest(Json.toJson(response));
                }
                if ((productRequest.getProductDetailRequest().getDiscount() != null) && productRequest.getProductDetailRequest().getDiscount().compareTo(0D) < 0){
                    response.setBaseResponse(0, 0, 0, "Diskon tidak boleh kurang dari 0", null);
                    return badRequest(Json.toJson(response));
                }
                if ((productRequest.getProductDetailRequest().getProductPriceAfterDiscount() != null) && productRequest.getProductDetailRequest().getProductPriceAfterDiscount().compareTo(BigDecimal.ZERO) < 0){
                    response.setBaseResponse(0, 0, 0, "Harga Product Setelah Diskon Cashback tidak boleh kurang dari 0", null);
                    return badRequest(Json.toJson(response));
                }
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
                            productRequest.getCategoryId(), ownMerchant);
                    if (categoryMerchant == null) {
                        response.setBaseResponse(0, 0, 0, " Category not found.", null);
                        return badRequest(Json.toJson(response));
                    }
                    SubCategoryMerchant subCategoryMerchant = SubCategoryMerchantRepository.findByIdAndMerchantId(
                            productRequest.getSubCategoryId(), ownMerchant);
                    if (subCategoryMerchant == null) {
                        response.setBaseResponse(0, 0, 0, " Sub Category not found.", null);
                        return badRequest(Json.toJson(response));
                    }
                    SubsCategoryMerchant subsCategoryMerchant = SubsCategoryMerchantRepository.findByIdAndMerchantId(
                            productRequest.getSubsCategoryId(), ownMerchant);
                    if (subsCategoryMerchant == null) {
                        response.setBaseResponse(0, 0, 0, " Subs Category not found.", null);
                        return badRequest(Json.toJson(response));
                    }
                    BrandMerchant brandMerchant = BrandMerchantRepository.findByIdAndMerchantId(
                            productRequest.getBrandId(), ownMerchant);
                    if (brandMerchant == null) {
                        response.setBaseResponse(0, 0, 0, " Brand not found.", null);
                        return badRequest(Json.toJson(response));
                    }

                    // update product merchant
                    productMerchant.setProductMerchant(productMerchant, ownMerchant, categoryMerchant, subCategoryMerchant, subsCategoryMerchant, brandMerchant, productRequest);
                    productMerchant.update();

                    // update product merchant detail
                    ProductMerchantDetail productMerchantDetail = ProductMerchantDetailRepository.findByProduct(productMerchant);
                    productMerchantDetail.setProductMerchantDetail(productMerchantDetail, productMerchant, productRequest);
                    productMerchantDetail.update();

                    // update product merchant description
                    ProductMerchantDescription productMerchantDescription = ProductMerchantDescriptionRepository.findByProductMerchantDetail(productMerchantDetail);
                    productMerchantDescription.setProductMerchantDescription(productMerchantDescription, productMerchantDetail, productRequest);
                    productMerchantDescription.update();

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
                int totalData = ProductMerchantRepository.findAllProduct(ownMerchant.id, isActive, sort, filter, 0, 0).size();
                List<ProductMerchant> productMerchants = ProductMerchantRepository.findAllProduct(ownMerchant.id, isActive, sort, filter, offset, limit);
                List<ProductResponse> productMerchantResponse = toResponses(productMerchants);
                response.setBaseResponse(totalData, offset, limit, "Berhasil menampilkan data product", productMerchantResponse);
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

    public static Result importProduct () {
    	Merchant merchant = checkMerchantAccessAuthorization();
    	if (merchant != null) {
    		Http.MultipartFormData body = request().body().asMultipartFormData();
			Http.MultipartFormData.FilePart file = body.getFile("import");
			if(file == null) {
				response.setBaseResponse(0, 0, 0, "File Is Null", null);
				return badRequest(Json.toJson(response));
			}
//			System.out.println("file - "+file.getFilename());
			ProductExcelService productImport = new ProductExcelService();
			if(!productImport.importProductMerchant(file, merchant, response)) {
				return badRequest(Json.toJson(response));
			}
			return ok(Json.toJson(response));
    	}
    	response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }
    
    public static Result getImportTemplate() throws IOException {
    	Merchant merchant = checkMerchantAccessAuthorization();
    	if(merchant != null) {
    		byte[] file = ProductExcelService.getImportTemplateMerchant(merchant);
//    		ByteArrayInputStream bis = new ByteArrayInputStream(file);
//    	    ZipInputStream zis = new ZipInputStream(bis);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            String filename = "ImportProductMerchant-"+simpleDateFormat.format(new Date()).toString() + ".zip";
    		response().setContentType("application/zip");
			response().setHeader("Content-disposition", "attachment; filename=" + filename);
			return ok(file);
    	}
    	response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }
    
    public static Result exportProduct() {
    	Merchant merchant = checkMerchantAccessAuthorization();
    	if(merchant != null) {
    		File file = ProductExcelService.exportProduct(merchant);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            String filename = "ExportProduct-"+simpleDateFormat.format(new Date()).toString() + ".xlsx";
    		response().setContentType("application/vnd.ms-excel");
			response().setHeader("Content-disposition", "attachment; filename=" + filename);
			return ok(file);
    	}
    	response.setBaseResponse(0, 0, 0, unauthorized, null);
    	return unauthorized(Json.toJson(response));
    }
    
    // =============================================== construct =====================================================//
    private static ProductResponse toResponse(ProductMerchant productMerchant) {
        ProductResponse productResponse = new ProductResponse(productMerchant);

        CategoryMerchant categoryMerchant = CategoryMerchantRepository.findByIdAndMerchantId(productMerchant.getCategoryMerchant().id, productMerchant.getMerchant());
        ProductResponse.CategoryResponse category = new ProductResponse.CategoryResponse(categoryMerchant);
        productResponse.setCategory(category);

        SubCategoryMerchant subCategoryMerchant = SubCategoryMerchantRepository.findByIdAndMerchantId(productMerchant.getSubCategoryMerchant().id, productMerchant.getMerchant());
        ProductResponse.SubCategoryResponse subCategory = new ProductResponse.SubCategoryResponse(subCategoryMerchant);
        productResponse.setSubCategory(subCategory);

        SubsCategoryMerchant subsCategoryMerchant = SubsCategoryMerchantRepository.findByIdAndMerchantId(productMerchant.getSubsCategoryMerchant().id, productMerchant.getMerchant());
        ProductResponse.SubsCategoryResponse subsCategory = new ProductResponse.SubsCategoryResponse(subsCategoryMerchant);
        productResponse.setSubsCategory(subsCategory);

        BrandMerchant brandMerchant = BrandMerchantRepository.findByIdAndMerchantId(productMerchant.getBrandMerchant().id, productMerchant.getMerchant());
        ProductResponse.BrandResponse brand = new ProductResponse.BrandResponse(brandMerchant);
        productResponse.setBrand(brand);

        ProductMerchantDetail productMerchantDetail = ProductMerchantDetailRepository.findByProduct(productMerchant);
        ProductDetailResponse productDetailResponse = new ProductDetailResponse(productMerchantDetail, null);
        productResponse.setProductDetail(productDetailResponse);

        ProductMerchantDescription productMerchantDescription = ProductMerchantDescriptionRepository.findByProductMerchantDetail(productMerchantDetail);
        ProductDescriptionResponse productDescriptionResponse = new ProductDescriptionResponse(productMerchantDescription);
        productResponse.setProductDescription(productDescriptionResponse);

        List<ProductStore> data = ProductStoreRepository.findAllByProductIdAndMerchant(productMerchant.id, productMerchant.getMerchant().id);
        List<ProductResponseStore.ProductStore> responsesProductStore = new ArrayList<>();
        for (ProductStore productStore : data) {
            ProductResponseStore.ProductStore productStoreRes = new ProductResponseStore.ProductStore(productStore);
            responsesProductStore.add(productStoreRes);
            productResponse.setProductStore(productStoreRes != null ? responsesProductStore : null);
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
                    productResponse.setNoSKU(productMerchant.getNoSKU());
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

                    CategoryMerchant categoryMerchant = CategoryMerchantRepository.findByIdAndMerchantId(productMerchant.getCategoryMerchant().id, productMerchant.getMerchant());
                    if (categoryMerchant != null) {
                        ProductResponse.CategoryResponse categoryResponse = ProductResponse.CategoryResponse.builder()
                                .id(categoryMerchant.id)
                                .categoryName(categoryMerchant.getCategoryName())
                                .build();
                        productResponse.setCategory(categoryResponse);
                    } else {
                        ProductResponse.CategoryResponse categoryResponse = ProductResponse.CategoryResponse.builder()
                                .categoryName("-")
                                .build();
                        productResponse.setCategory(categoryResponse);
                    }
                    SubCategoryMerchant subCategoryMerchant = SubCategoryMerchantRepository.findByIdAndMerchantId(productMerchant.getSubCategoryMerchant().id, productMerchant.getMerchant());
                    if (subCategoryMerchant != null) {
                        ProductResponse.SubCategoryResponse subCategoryResponse = ProductResponse.SubCategoryResponse.builder()
                                .id(subCategoryMerchant.id)
                                .subCategoryName(subCategoryMerchant.getSubcategoryName())
                                .build();
                        productResponse.setSubCategory(subCategoryResponse);
                    } else {
                        ProductResponse.SubCategoryResponse subCategoryResponse = ProductResponse.SubCategoryResponse.builder()
                                .subCategoryName("-")
                                .build();
                        productResponse.setSubCategory(subCategoryResponse);
                    }
                    SubsCategoryMerchant subsCategoryMerchant = SubsCategoryMerchantRepository.findByIdAndMerchantId(productMerchant.getSubsCategoryMerchant().id, productMerchant.getMerchant());
                    if (subCategoryMerchant != null) {
                        ProductResponse.SubsCategoryResponse subsCategoryResponse = ProductResponse.SubsCategoryResponse.builder()
                                .id(subsCategoryMerchant.id)
                                .subsCategoryName(subsCategoryMerchant.getSubscategoryName())
                                .build();
                        productResponse.setSubsCategory(subsCategoryResponse);
                    } else {
                        ProductResponse.SubsCategoryResponse subsCategoryResponse = ProductResponse.SubsCategoryResponse.builder()
                                .subsCategoryName("-")
                                .build();
                        productResponse.setSubsCategory(subsCategoryResponse);
                    }
                    BrandMerchant brandMerchant = BrandMerchantRepository.findByIdAndMerchantId(productMerchant.getBrandMerchant().id, productMerchant.getMerchant());
                    if (brandMerchant != null) {
                        ProductResponse.BrandResponse brandResponse = ProductResponse.BrandResponse.builder()
                                .id(brandMerchant.id)
                                .brandName(brandMerchant.getBrandName())
                                .build();
                        productResponse.setBrand(brandResponse);
                    } else {
                        ProductResponse.BrandResponse brandResponse = ProductResponse.BrandResponse.builder()
                                .brandName("-")
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

                    ProductStore productStore = ProductStoreRepository.findForCust(productId, storeId, productMerchantDetail.getProductMerchant().getMerchant());
                
                    ProductMerchant productMerchant = ProductMerchantRepository.findByIdProductRecommend(productMerchantDetail.getProductMerchant().id, merchantId);
                    productResponse.setProductId(productMerchant.id);
                    productResponse.setNoSKU(productMerchant.getNoSKU());
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
                                .stock(productStore != null ? productStore.getStock() : 0)
                                .build();
                    productResponse.setProductDetail(productDetailResponse);

                    CategoryMerchant categoryMerchant = CategoryMerchantRepository.findByIdAndMerchantId(productMerchant.getCategoryMerchant().id, productMerchant.getMerchant());
                    if (categoryMerchant != null) {
                        ProductResponse.CategoryResponse categoryResponse = ProductResponse.CategoryResponse.builder()
                                .id(categoryMerchant.id)
                                .categoryName(categoryMerchant.getCategoryName())
                                .build();
                        productResponse.setCategory(categoryResponse);
                    } else {
                        ProductResponse.CategoryResponse categoryResponse = ProductResponse.CategoryResponse.builder()
                                .categoryName("-")
                                .build();
                        productResponse.setCategory(categoryResponse);
                    }
                    SubCategoryMerchant subCategoryMerchant = SubCategoryMerchantRepository.findByIdAndMerchantId(productMerchant.getSubCategoryMerchant().id, productMerchant.getMerchant());
                    if (subCategoryMerchant != null) {
                        ProductResponse.SubCategoryResponse subCategoryResponse = ProductResponse.SubCategoryResponse.builder()
                                .id(subCategoryMerchant.id)
                                .subCategoryName(subCategoryMerchant.getSubcategoryName())
                                .build();
                        productResponse.setSubCategory(subCategoryResponse);
                    } else {
                        ProductResponse.SubCategoryResponse subCategoryResponse = ProductResponse.SubCategoryResponse.builder()
                                .subCategoryName("-")
                                .build();
                        productResponse.setSubCategory(subCategoryResponse);
                    }
                    SubsCategoryMerchant subsCategoryMerchant = SubsCategoryMerchantRepository.findByIdAndMerchantId(productMerchant.getSubsCategoryMerchant().id, productMerchant.getMerchant());
                    if (subCategoryMerchant != null) {
                        ProductResponse.SubsCategoryResponse subsCategoryResponse = ProductResponse.SubsCategoryResponse.builder()
                                .id(subsCategoryMerchant.id)
                                .subsCategoryName(subsCategoryMerchant.getSubscategoryName())
                                .build();
                        productResponse.setSubsCategory(subsCategoryResponse);
                    } else {
                        ProductResponse.SubsCategoryResponse subsCategoryResponse = ProductResponse.SubsCategoryResponse.builder()
                                .subsCategoryName("-")
                                .build();
                        productResponse.setSubsCategory(subsCategoryResponse);
                    }
                    BrandMerchant brandMerchant = BrandMerchantRepository.findByIdAndMerchantId(productMerchant.getBrandMerchant().id, productMerchant.getMerchant());
                    if (brandMerchant != null) {
                        ProductResponse.BrandResponse brandResponse = ProductResponse.BrandResponse.builder()
                                .id(brandMerchant.id)
                                .brandName(brandMerchant.getBrandName())
                                .build();
                        productResponse.setBrand(brandResponse);
                    } else {
                        ProductResponse.BrandResponse brandResponse = ProductResponse.BrandResponse.builder()
                                .brandName("-")
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
                responseData.setNoSKU(productMerchant.getNoSKU());
                responseData.setProductName(productMerchant.getProductName());
                responseData.setMerchantId(productMerchant.getMerchant().id);
                    
                Query<ProductAddOn> queryProductAddOn = ProductAddOnRepository.find.where().raw("t0.product_id = " + productMerchantDetail.getProductMerchant().id + " AND t0.is_active = " + true + " AND t0.is_deleted = " + false + " AND t0.merchant_id = " + merchantId + " " + groupBy).order("t0.product_type asc");
                List<ProductAddOn> dataAddOn = ProductAddOnRepository.getDataForAddOn(queryProductAddOn);
                List<ProductCustomerAdditionalResponse.ProductAddOn> responsesProductAddOn = new ArrayList<>();
                    
                for(ProductAddOn dataProductAddOn : dataAddOn) {

                    ProductStore productStore = ProductStoreRepository.findForCust(dataProductAddOn.getProductAssignId(), storeId, productMerchant.getMerchant());
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
                    responseData.setNoSKU(productMerchant.getNoSKU());
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

    public static Result posProductAdditional(Long productId, Long storeId){
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            List<ProductAddOnType>dataAddOnType = ProductAddOnTypeRepository.findByMerchantId(ownMerchant);
            try {                    
                List<MiniPosAdditionalResponse> responsesTypeProductAddOn = new ArrayList<>();
                for(ProductAddOnType dataProductAddOnType : dataAddOnType) {
                    MiniPosAdditionalResponse responseTypeProductAddOn = new MiniPosAdditionalResponse();
                    responseTypeProductAddOn.setProductType(dataProductAddOnType.getProductType());
                    Query<ProductAddOn> queryProductAddOn = ProductAddOnRepository.find.where().eq("t0.product_type",dataProductAddOnType.getProductType()).eq("t0.product_id", productId).eq("merchant", ownMerchant).order("t0.product_type asc");
                    List<ProductAddOn> dataAddOn = ProductAddOnRepository.getDataForAddOn(queryProductAddOn);
                    List<MiniPosAdditionalResponse.ProductAddOn> responsesProductAddOn = new ArrayList<>();
                    for(ProductAddOn dataProductAddOn : dataAddOn) {
                        ProductStore productStore = ProductStoreRepository.findForCust(dataProductAddOn.getProductAssignId(), storeId, ownMerchant);
                        ProductMerchantDetail forDetail = ProductMerchantDetailRepository.findDetailAdditionalProduct(dataProductAddOn.getProductAssignId(), dataProductAddOn.getMerchant().id);
                        ProductMerchant productMerchantAssign = ProductMerchantRepository.findByIdProductRecommend(dataProductAddOn.getProductAssignId(), dataProductAddOn.getMerchant().id);
                                            
                        MiniPosAdditionalResponse.ProductAddOn responseAddOn = new MiniPosAdditionalResponse.ProductAddOn();
                        responseAddOn.setProductId(dataProductAddOn.getProductMerchant().id);
                        responseAddOn.setProductAssignId(dataProductAddOn.getProductAssignId());
                        responseAddOn.setNoSKU(productMerchantAssign.getNoSKU());
                        responseAddOn.setProductName(productMerchantAssign.getProductName());
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
                    }
                    responseTypeProductAddOn.setProductAddOn(responsesProductAddOn != null ? responsesProductAddOn : null);
                    responsesTypeProductAddOn.add(responseTypeProductAddOn);
                }
                response.setBaseResponse(1, 0, 1, "Berhasil menampilkan data", responsesTypeProductAddOn);
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

    // FOR HOME CUSTOMER
    public static Result productListKiosk(Long brandId, Long merchantId, Long storeId, Long categoryId, String keyword, int offset, int limit) {
        try {
            // validate
            Store store = Store.find.byId(storeId);
            if (store == null) {
                response.setBaseResponse(0, 0, 0, "Store tidak ditemukan.", null);
                return notFound(Json.toJson(response));
            }

            // list & total data
            List<ProductMerchantDetail> data = ProductMerchantDetailRepository.findListProductKiosk(brandId, merchantId, storeId, categoryId, keyword, offset, limit);
            Integer totalData = ProductMerchantDetailRepository.findListProductKiosk(brandId, merchantId, storeId, categoryId, keyword, 0, 0).size();

            // set response data
            List<ProductSpecificStoreResponse> responses = new ArrayList<>();
            for (ProductMerchantDetail productMerchantDetail : data) {
                ProductStore productStore = ProductStoreRepository.findByStoreAndProductMerchant(storeId, productMerchantDetail.getProductMerchant().id);

                ProductSpecificStoreResponse response = new ProductSpecificStoreResponse(productMerchantDetail.getProductMerchant());

                ProductDetailResponse productDetailResponse = new ProductDetailResponse(productMerchantDetail, productStore);
                response.setProductDetail(productDetailResponse);

                ProductSpecificStoreResponse.Brand brand = new ProductSpecificStoreResponse.Brand(productMerchantDetail.getProductMerchant().getBrandMerchant());
                response.setBrand(brand);

                ProductSpecificStoreResponse.Category category = new ProductSpecificStoreResponse.Category(productMerchantDetail.getProductMerchant().getCategoryMerchant());
                response.setCategory(category);

                ProductSpecificStoreResponse.SubCategory subCategory = new ProductSpecificStoreResponse.SubCategory(productMerchantDetail.getProductMerchant().getSubCategoryMerchant());
                response.setSubCategory(subCategory);

                ProductSpecificStoreResponse.SubsCategory subsCategory = new ProductSpecificStoreResponse.SubsCategory(productMerchantDetail.getProductMerchant().getSubsCategoryMerchant());
                response.setSubsCategory(subsCategory);

                if (productStore != null) {
                    ProductSpecificStoreResponse.ProductStore pStore = new ProductSpecificStoreResponse.ProductStore(productStore);
                    pStore.setStock(productStore.getStock());
                    response.setProductStore(pStore);
                }

                responses.add(response);
            }
            response.setBaseResponse(totalData, offset, limit, "Berhasil menampilkan produk", responses);
            return ok(Json.toJson(response));
        } catch (Exception e) {
            Logger.info("Error: " + e.getMessage());
        }
        response.setBaseResponse(0, 0, 0, error, null);
        return badRequest(Json.toJson(response));
    }

    // PRODUCT BEST SELLER
    public static Result listBestSeller(Long merchantId){
        List sql = null;
        if (merchantId != null) {
            Transaction trx = Ebean.beginTransaction();
            try {
                // String querys = "select pmd.*, (select count(od.product_id) from order_detail od where od.product_id = pmd.product_merchant_id group by od.product_id) total_terjual from product_merchant_detail pmd where pmd.product_type = 'MAIN' and pmd.product_merchant_id in (select pm.id from product_merchant pm where pm.is_deleted = false and pm.is_active = true and pm.merchant_id = "+merchantId+") order by total_terjual desc";
				// SqlQuery sqlQuery = Ebean.createSqlQuery(querys);
				// sql = sqlQuery.findList();
                // System.out.println(sql);

                // String querySql = "t0.product_merchant_id in (select pm.id from product_merchant pm where pm.is_active = "+true+" and pm.is_deleted = "+false+" and pm.merchant_id = "+merchantId+")";
                // Query<ProductMerchantDetail> query = ProductMerchantDetailRepository.find.query().select("count(t0.product_merchant_id) as t0.total_terjual").where().raw(querySql).eq("t0.is_deleted", false).eq("t0.product_type", "MAIN").order("t0.total_terjual desc");
                // List<ProductMerchantDetail> totalDataProductDetail = ProductMerchantDetailRepository.getTotalDataPage(sqlQuery);
                List<ProductMerchantDetail> productMerchantDetails = ProductMerchantDetailRepository.forProductBestSeller(merchantId);
                List<ProductResponse> productMerchantResponse = new ArrayList<>();

                
                for(ProductMerchantDetail productMerchantDetail : productMerchantDetails){
                    if (productMerchantDetail.total_penjualan != null){
                        ProductResponse productResponse = new ProductResponse();
                        ProductMerchant productMerchant = ProductMerchantRepository.findByIdProductRecommend(productMerchantDetail.getProductMerchant().id, merchantId);
                        
                        productResponse.setProductId(productMerchantDetail.getProductMerchant().id);
                        productResponse.setNoSKU(productMerchant.getNoSKU());
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

                        CategoryMerchant categoryMerchant = CategoryMerchantRepository.findByIdAndMerchantId(productMerchant.getCategoryMerchant().id, productMerchant.getMerchant());
                        if (categoryMerchant != null) {
                            ProductResponse.CategoryResponse categoryResponse = ProductResponse.CategoryResponse.builder()
                                    .id(categoryMerchant.id)
                                    .categoryName(categoryMerchant.getCategoryName())
                                    .build();
                            productResponse.setCategory(categoryResponse);
                        } else {
                            ProductResponse.CategoryResponse categoryResponse = ProductResponse.CategoryResponse.builder()
                                    .categoryName("-")
                                    .build();
                            productResponse.setCategory(categoryResponse);
                        }
                        SubCategoryMerchant subCategoryMerchant = SubCategoryMerchantRepository.findByIdAndMerchantId(productMerchant.getSubCategoryMerchant().id, productMerchant.getMerchant());
                        if (subCategoryMerchant != null) {
                            ProductResponse.SubCategoryResponse subCategoryResponse = ProductResponse.SubCategoryResponse.builder()
                                    .id(subCategoryMerchant.id)
                                    .subCategoryName(subCategoryMerchant.getSubcategoryName())
                                    .build();
                            productResponse.setSubCategory(subCategoryResponse);
                        } else {
                            ProductResponse.SubCategoryResponse subCategoryResponse = ProductResponse.SubCategoryResponse.builder()
                                    .subCategoryName("-")
                                    .build();
                            productResponse.setSubCategory(subCategoryResponse);
                        }
                        SubsCategoryMerchant subsCategoryMerchant = SubsCategoryMerchantRepository.findByIdAndMerchantId(productMerchant.getSubsCategoryMerchant().id, productMerchant.getMerchant());
                        if (subCategoryMerchant != null) {
                            ProductResponse.SubsCategoryResponse subsCategoryResponse = ProductResponse.SubsCategoryResponse.builder()
                                    .id(subsCategoryMerchant.id)
                                    .subsCategoryName(subsCategoryMerchant.getSubscategoryName())
                                    .build();
                            productResponse.setSubsCategory(subsCategoryResponse);
                        } else {
                            ProductResponse.SubsCategoryResponse subsCategoryResponse = ProductResponse.SubsCategoryResponse.builder()
                                    .subsCategoryName("-")
                                    .build();
                            productResponse.setSubsCategory(subsCategoryResponse);
                        }
                        BrandMerchant brandMerchant = BrandMerchantRepository.findByIdAndMerchantId(productMerchant.getBrandMerchant().id, productMerchant.getMerchant());
                        if (brandMerchant != null) {
                            ProductResponse.BrandResponse brandResponse = ProductResponse.BrandResponse.builder()
                                    .id(brandMerchant.id)
                                    .brandName(brandMerchant.getBrandName())
                                    .build();
                            productResponse.setBrand(brandResponse);
                        } else {
                            ProductResponse.BrandResponse brandResponse = ProductResponse.BrandResponse.builder()
                                    .brandName("-")
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
                }
                response.setBaseResponse(productMerchantResponse.size(), 0, 10, success + " Showing data products", productMerchantResponse);
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
}