package controllers.product;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Transaction;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hokeba.api.BaseResponse;
import controllers.BaseController;
import dtos.product.ProductDescriptionResponse;
import dtos.product.ProductDetailResponse;
import dtos.product.ProductPosRequest;
import dtos.product.ProductResponse;
import dtos.product.ProductResponseStore;
import dtos.product.ProductSpecificStoreResponse;
import models.BrandMerchant;
import models.CategoryMerchant;
import models.Merchant;
import models.ProductStore;
import models.Store;
import models.SubCategoryMerchant;
import models.SubsCategoryMerchant;
import models.UserMerchant;
import models.merchant.ProductMerchant;
import models.merchant.ProductMerchantDescription;
import models.merchant.ProductMerchantDetail;
import play.libs.Json;
import play.mvc.Result;
import repository.BrandMerchantRepository;
import repository.CategoryMerchantRepository;
import repository.ProductMerchantDescriptionRepository;
import repository.ProductMerchantDetailRepository;
import repository.ProductMerchantRepository;
import repository.ProductStoreRepository;
import repository.SubCategoryMerchantRepository;
import repository.SubsCategoryMerchantRepository;
import validator.ProductValidator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProductPosController extends BaseController {
    private static BaseResponse response = new BaseResponse();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Result getAllProducts(Long merchantId, Long storeId, Long categoryId, String keyword, int offset, int limit) {
        UserMerchant userMerchant = checkUserMerchantAccessAuthorization();
        if (userMerchant != null) {
            try {
                Merchant merchant = Merchant.find.byId(merchantId);
                if (merchant == null) {
                    response.setBaseResponse(0, 0, 0, "Merchant tidak ditemukan.", null);
                    return notFound(Json.toJson(response));
                }
                Store store = Store.find.byId(storeId);
                if (store == null && storeId != 0L) {
                    response.setBaseResponse(0, 0, 0, "Store tidak ditemukan.", null);
                    return notFound(Json.toJson(response));
                }

                List<ProductMerchantDetail> data = ProductMerchantDetailRepository.findListProductMiniPos(merchantId, storeId, categoryId, keyword, offset, limit);
                Integer totalData = ProductMerchantDetailRepository.findListProductMiniPos(merchantId, storeId, categoryId, keyword, 0, 0).size();
                response.setBaseResponse(totalData, offset, limit, "Berhasil menampilkan data", toListProductStoreResponse(data, store));
                return ok(Json.toJson(response));
            } catch (Exception e) {
                System.out.println("Error saat menampilkan list produk : " + e.getMessage());
                e.printStackTrace();
            }
        }
        response.setBaseResponse(0, 0, 0, error, null);
        return badRequest(Json.toJson(response));
    }

    public static Result getProductById(Long id) {
        UserMerchant userMerchant = checkUserMerchantAccessAuthorization();
        if (userMerchant != null) {
            try {
                ProductMerchant productMerchant = ProductMerchantRepository.findById(id, userMerchant);
                if (productMerchant != null) {
                    ProductResponse productResponse = toDetailResponse(productMerchant);
                    response.setBaseResponse(1, 0, 1, success + " Showing data product", productResponse);
                    return ok(Json.toJson(response));
                }
            } catch (Exception e) {
                System.out.println("Error saat menampilkan detail produk : " + e.getMessage());
                e.printStackTrace();
            }
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result createProduct() {
        UserMerchant userMerchant = checkUserMerchantAccessAuthorization();
        if (userMerchant == null) {
            response.setBaseResponse(0, 0, 0, unauthorized, null);
            return notFound(Json.toJson(response));
        }

        Merchant merchant = userMerchant.getRole().getMerchant();
        JsonNode json = request().body().asJson();
        try {
            ProductPosRequest request = objectMapper.readValue(json.toString(), ProductPosRequest.class);
            String validateRequest = ProductValidator.validateRequest(request);
            if (validateRequest != null) {
                response.setBaseResponse(0, 0, 0, validateRequest, null);
                return badRequest(Json.toJson(response));
            }
            Transaction trx = Ebean.beginTransaction();
            try {
                CategoryMerchant categoryMerchant = CategoryMerchantRepository.findByIdAndMerchantId(request.getCategoryId(), merchant);
                if (categoryMerchant == null) {
                    response.setBaseResponse(0, 0, 0, "Category not found.", null);
                    return badRequest(Json.toJson(response));
                }
                SubCategoryMerchant subCategoryMerchant = SubCategoryMerchantRepository.findByIdAndMerchantId(request.getSubCategoryId(), merchant);
                if (subCategoryMerchant == null) {
                    response.setBaseResponse(0, 0, 0, "Sub Category not found.", null);
                    return badRequest(Json.toJson(response));
                }
                SubsCategoryMerchant subsCategoryMerchant = SubsCategoryMerchantRepository.findByIdAndMerchantId(request.getSubsCategoryId(), merchant);
                if (subsCategoryMerchant == null) {
                    response.setBaseResponse(0, 0, 0, "Subs Category not found.", null);
                    return badRequest(Json.toJson(response));
                }
                BrandMerchant brandMerchant = BrandMerchantRepository.findByIdAndMerchantId(request.getBrandId(), merchant);
                if (brandMerchant == null) {
                    response.setBaseResponse(0, 0, 0, "Brand not found.", null);
                    return badRequest(Json.toJson(response));
                }
                Store store = Store.findById(request.getProductStoreRequests().getStoreId());
                if (store == null) {
                    response.setBaseResponse(0, 0, 0, "Toko tidak ditemuka.", null);
                    return badRequest(Json.toJson(response));
                }

                //create main product merchant
                ProductMerchant newProductMerchant = new ProductMerchant(merchant, categoryMerchant, subCategoryMerchant, subsCategoryMerchant, brandMerchant, request);
                newProductMerchant.save();

                // create product merchant detail
                ProductMerchantDetail newProductMerchantDetail = new ProductMerchantDetail(newProductMerchant, request);
                newProductMerchantDetail.save();

                //create product merchant description
                ProductMerchantDescription newProductMerchantDescription = new ProductMerchantDescription(newProductMerchantDetail, request);
                newProductMerchantDescription.save();

                //create product store
                ProductStore newProductStore = new ProductStore(merchant, store, newProductMerchant, request, true);
                newProductStore.save();

                trx.commit();

                response.setBaseResponse(1, offset, 1, "Berhasil membuat produk", newProductMerchant.id);
                return ok(Json.toJson(response));
            } catch (Exception e) {
                System.out.println("Error saat membuat produk : " + e.getMessage());
                e.printStackTrace();
                trx.rollback();
                response.setBaseResponse(0, 0, 0, "Error saat membuat produk : " + e.toString(), null);
                return badRequest(Json.toJson(response));
            } finally {
                trx.end();
            }
        } catch (Exception e) {
            System.out.println("Error : " + e.getMessage());
            e.printStackTrace();
            response.setBaseResponse(0, 0, 0, "Error saat membuat produk : " + e.toString(), null);
            return badRequest(Json.toJson(response));
        }
    }

    public static Result editProduct(Long id) {
        UserMerchant userMerchant = checkUserMerchantAccessAuthorization();
        if (userMerchant == null) {
            response.setBaseResponse(0, 0, 0, unauthorized, null);
            return notFound(Json.toJson(response));
        }

        Merchant merchant = userMerchant.getRole().getMerchant();
        JsonNode json = request().body().asJson();
        try {
            ProductPosRequest productRequest = objectMapper.readValue(json.toString(), ProductPosRequest.class);
            String validateRequest = ProductValidator.validateRequest(productRequest);
            if (validateRequest != null) {
                response.setBaseResponse(0, 0, 0, validateRequest, null);
                return badRequest(Json.toJson(response));
            }
            ProductMerchant productMerchant = ProductMerchantRepository.findById(id, merchant);
            if (productMerchant == null) {
                response.setBaseResponse(0, 0, 0, "Product not found.", null);
                return badRequest(Json.toJson(response));
            }

            Transaction trx = Ebean.beginTransaction();
            try {
                CategoryMerchant categoryMerchant = CategoryMerchantRepository.findByIdAndMerchantId(productRequest.getCategoryId(), merchant);
                if (categoryMerchant == null) {
                    response.setBaseResponse(0, 0, 0, "Category not found.", null);
                    return badRequest(Json.toJson(response));
                }
                SubCategoryMerchant subCategoryMerchant = SubCategoryMerchantRepository.findByIdAndMerchantId(productRequest.getSubCategoryId(), merchant);
                if (subCategoryMerchant == null) {
                    response.setBaseResponse(0, 0, 0, "Sub Category not found.", null);
                    return badRequest(Json.toJson(response));
                }
                SubsCategoryMerchant subsCategoryMerchant = SubsCategoryMerchantRepository.findByIdAndMerchantId(productRequest.getSubsCategoryId(), merchant);
                if (subsCategoryMerchant == null) {
                    response.setBaseResponse(0, 0, 0, "Subs Category not found.", null);
                    return badRequest(Json.toJson(response));
                }
                BrandMerchant brandMerchant = BrandMerchantRepository.findByIdAndMerchantId(productRequest.getBrandId(), merchant);
                if (brandMerchant == null) {
                    response.setBaseResponse(0, 0, 0, "Brand not found.", null);
                    return badRequest(Json.toJson(response));
                }
                Store store = Store.findById(productRequest.getProductStoreRequests().getStoreId());
                if (store == null) {
                    response.setBaseResponse(0, 0, 0, "Toko tidak ditemukan.", null);
                    return badRequest(Json.toJson(response));
                }

                // update product merchant
                productMerchant.setProductMerchant(productMerchant, merchant, categoryMerchant, subCategoryMerchant, subsCategoryMerchant, brandMerchant, productRequest);
                productMerchant.update();

                // update product merchant detail
                ProductMerchantDetail productMerchantDetail = ProductMerchantDetailRepository.findByProduct(productMerchant);
                productMerchantDetail.setProductMerchantDetail(productMerchantDetail, productMerchant, productRequest);
                productMerchantDetail.update();

                // update product merchant description
                ProductMerchantDescription productMerchantDescription = ProductMerchantDescriptionRepository.findByProductMerchantDetail(productMerchantDetail);
                productMerchantDescription.setProductMerchantDescription(productMerchantDescription, productMerchantDetail, productRequest);
                productMerchantDescription.update();

                // update product store
                System.out.println("store : " + store.id);
                System.out.println("product : " + productMerchant.id);
                ProductStore productStore = ProductStoreRepository.findByStoreAndProductMerchant(store.id, productMerchant.id);
                productStore.setProductStore(productStore, merchant, store, productMerchant, productRequest);
                productStore.update();

                trx.commit();

                response.setBaseResponse(1, offset, 1, success + " Product updated successfully", productMerchant.id);
                return ok(Json.toJson(response));
            } catch (Exception e) {
                System.out.println("Error saat edit produk : " + e.getMessage());
                e.printStackTrace();
                trx.rollback();
                response.setBaseResponse(0, 0, 0, "Error saat edit produk : " + e.toString(), null);
                return badRequest(Json.toJson(response));
            } finally {
                trx.end();
            }
        } catch (Exception e) {
            System.out.println("Error : " + e.getMessage());
            e.printStackTrace();
            response.setBaseResponse(0, 0, 0, "Error saat edit produk : " + e.toString(), null);
            return badRequest(Json.toJson(response));
        }
    }

    public static Result deleteProduct(Long id) {
        UserMerchant userMerchant = checkUserMerchantAccessAuthorization();
        if (userMerchant != null) {
            try {
                ProductStore productStore = ProductStoreRepository.findByProductIdAndMerchantId(id, userMerchant.getRole().getMerchant());
                if (productStore == null) {
                    response.setBaseResponse(0, 0, 0, "Produk tidak tersedia.", null);
                    return badRequest(Json.toJson(response));
                }

                productStore.isDeleted = true;
                productStore.update();

                response.setBaseResponse(0, 0, 0, "Berhasil hapus produk", null);
                return ok(Json.toJson(response));
            } catch (Exception e) {
                System.out.println("Error saat menampilkan detail produk : " + e.getMessage());
                e.printStackTrace();
            }
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    // -----------------------------------------------------------------------
    private static ProductResponse toDetailResponse(ProductMerchant productMerchant) {
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

    private static List<ProductSpecificStoreResponse> toListProductStoreResponse(List<ProductMerchantDetail> data, Store store) {
        List<ProductSpecificStoreResponse> responses = new ArrayList<>();
        for (ProductMerchantDetail productMerchantDetail : data) {
            ProductStore productStore = ProductStoreRepository.findByStoreAndProductMerchant(store.id, productMerchantDetail.getProductMerchant().id);

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
                response.setProductStore(pStore);
            }

            responses.add(response);
        }

        return responses;
    }
}
