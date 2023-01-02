package controllers.product;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Transaction;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hokeba.api.BaseResponse;
import com.hokeba.util.Constant;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiImplicitParam;
import com.wordnik.swagger.annotations.ApiImplicitParams;
import com.wordnik.swagger.annotations.ApiOperation;
import controllers.BaseController;
import dtos.product.ProductDetailResponse;
import dtos.product.ProductResponseStore;
import dtos.product.ProductSpecificStoreResponse;
import dtos.product.ProductStoreResponse;
import models.Merchant;
import models.Product;
import models.ProductStore;
import models.Store;
import models.merchant.ProductMerchant;
import models.merchant.ProductMerchantDetail;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;

import java.math.BigDecimal;

import com.avaje.ebean.Query;
import repository.ProductMerchantDetailRepository;
import repository.ProductMerchantRepository;
import repository.ProductStoreRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
                ProductMerchant productMerchant = ProductMerchantRepository.findById(request.getProductId(),
                        ownMerchant);
                if (productMerchant == null) {
                    response.setBaseResponse(0, 0, 0, " Product not found.", null);
                    return badRequest(Json.toJson(response));
                }
                Store store = Store.findById(request.getStoreId());
                if (store == null) {
                    response.setBaseResponse(0, 0, 0, " Product not found.", null);
                    return badRequest(Json.toJson(response));
                }
                ProductStore psQuery = ProductStoreRepository.find.where().eq("productMerchant", productMerchant)
                        .eq("store", store).eq("t0.is_deleted", false).findUnique();
                if (psQuery != null) {
                    response.setBaseResponse(0, 0, 0,
                            "Tidak dapat menambahkan " + productMerchant.getProductName() + " ke toko yang sama.",
                            null);
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
                        newProductStore.setProductStoreQrCode(Constant.getInstance().getFrontEndUrl().concat(store.storeCode+"/"+store.id+"/"+ownMerchant.id+"/product/"+productMerchant.id+"/detail"));
                        if (request.getDiscountType() != null) {
                            newProductStore.setDiscountType(request.getDiscountType());
                        }
                        if (request.getDiscount() != null) {
                            newProductStore.setDiscount(request.getDiscount());
                        }
                        if (request.getFinalPrice() != null) {
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
        if (request.getStorePrice() == null || request.getStorePrice() == BigDecimal.ZERO)
            return "Harga tidak boleh nol atau kosong";
        if (request.getProductId() == null)
            return "Id Produk tidak boleh nol atau kosong";
        if (request.getStorePrice().compareTo(BigDecimal.ZERO) < 0)
            return "Harga tidak boleh kurang dari 0";
        if (request.getDiscount().compareTo(0D) < 0)
            return "Nilai Discount tidak boleh kurang dari 0";
        if (request.getFinalPrice().compareTo(BigDecimal.ZERO) < 0)
            return "Harga Final tidak boleh kurang dari 0";

        return null;
    }

    @ApiOperation(value = "Get all product store.", notes = "Returns list of product store.\n" + swaggerInfo
            + "", response = Product.class, responseContainer = "List", httpMethod = "GET")
    public static Result listProductStore(String filter, String sort, int offset, int limit) {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            Query<ProductMerchant> query = ProductMerchantRepository.findProductIsActiveAndMerchant(ownMerchant, true);
            try {
                List<ProductResponseStore> responses = new ArrayList<>();
                List<ProductMerchant> totalData = ProductMerchantRepository.getTotalDataPage(query);
                List<ProductMerchant> productMerchants = ProductMerchantRepository.findProductWithPaging(query, sort,
                        filter, offset, limit);
                for (ProductMerchant data : productMerchants) {
                    ProductResponseStore responseProd = new ProductResponseStore();
                    Query<ProductStore> queryPS = ProductStoreRepository.find.where().eq("t0.product_id", data.id)
                            .eq("t0.is_deleted", false).eq("merchant", ownMerchant).order("t0.id");
                    List<ProductStore> dataPS = ProductStoreRepository.getDataProductStore(queryPS);
                    List<ProductResponseStore.ProductStore> responsesProductStore = new ArrayList<>();
                    responseProd.setProductId(data.id);
                    responseProd.setProductName(data.getProductName());
                    responseProd.setIsActive(data.getIsActive());
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
                        responseProd.setProductDetail(productDetailResponse);
                    }

                    responseProd.setMerchantId(data.getMerchant().id);

                    for (ProductStore dataPStore : dataPS) {
                        ProductResponseStore.ProductStore responsePStore = new ProductResponseStore.ProductStore();

                        Store store = Store.findById(dataPStore.getStore().id);
                        if (store != null) {
                            responsePStore.setId(dataPStore.id);
                            responsePStore.setStoreId(dataPStore.getStore().id);
                            responsePStore.setProductId(dataPStore.getProductMerchant().id);
                            responsePStore.setIsActive(dataPStore.isActive);
                            responsePStore.setStorePrice(dataPStore.getStorePrice());
                            responsePStore.setDiscountType(dataPStore.getDiscountType());
                            responsePStore.setDiscount(dataPStore.getDiscount());
                            responsePStore.setIsDeleted(dataPStore.isDeleted);
                            responsePStore.setFinalPrice(dataPStore.getFinalPrice());
                            responsePStore.setStoresName(store.storeName);
                            responsesProductStore.add(responsePStore);
                        }
                        responseProd.setProductStore(responsePStore != null ? responsesProductStore : null);
                    }
                    responses.add(responseProd);
                }
                response.setBaseResponse(
                        filter == null || filter.equals("") ? totalData.size() : productMerchants.size(), offset, limit,
                        success + " menampilkan data", responses);
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

    // LIST PRODUCT STORE V2
    public static Result listProductStoreV2(Long storeId, String keyword, String sort, int offset, int limit) {
        try {
            Merchant ownMerchant = checkMerchantAccessAuthorization();
            if (ownMerchant != null) {
                if (!ownMerchant.id.equals(0L) && !storeId.equals(0L)) {
                    try {
                        Store store = Store.find.where().eq("t0.id", storeId).eq("t0.is_deleted", false).findUnique();

                        if (store != null) {
                            String searchQuery = keyword != null && keyword.length() > 0 ? " and lower(t0.product_name) like '%"+keyword+"%'" : "";
                            String querySql = " t0.is_active = "+true+searchQuery;
                            Query<ProductMerchant> query = ProductMerchantRepository.find.where().eq("t0.merchant_id", ownMerchant.id).raw(querySql).eq("t0.is_deleted", false).orderBy().desc("t0.id");
                            List<ProductMerchant> productMerchants = ProductMerchantRepository.findProductWithPaging(query, sort,
                                    filter, offset, limit);
                            ProductMerchantDetail productMerchantDetail = null;
                            List<ProductResponseStore> responses = new ArrayList<>();
                            List<ProductResponseStore.ProductStore> responsesProductStore = new ArrayList<>();
                            for (ProductMerchant productMerchant : productMerchants) {
                                ProductResponseStore responseProd = new ProductResponseStore();
                                Query<ProductStore> queryPS = ProductStoreRepository.find.where().eq("t0.product_id", productMerchant.id)
                                        .eq("t0.is_deleted", false).eq("merchant", ownMerchant).order("t0.id");
                                List<ProductStore> dataPS = ProductStoreRepository.getDataProductStore(queryPS);
                                List<ProductStore> listProductStore = ProductStoreRepository.find.where().eq("t0.is_deleted", false).eq("t0.is_active", true).eq("t0.product_id", productMerchant.id).orderBy().desc("t0.id").findList();
                                responseProd.setMerchantId(productMerchant.getMerchant().id);
                                if (listProductStore.size() > 0) {
                                    for (ProductStore productStore : listProductStore) {
                                        if (productStore.getStore().id.equals(store.id)) {
                                            responseProd.setProductId(productMerchant.id);
                                            responseProd.setProductName(productMerchant.getProductName());
                                            responseProd.setIsActive(productMerchant.getIsActive());
                                            productMerchantDetail = ProductMerchantDetailRepository.findByProduct(productMerchant);
                                            if (productMerchantDetail != null) {
                                                ProductDetailResponse productDetailResponse = ProductDetailResponse.builder()
                                                .productType(productMerchantDetail.getProductType())
                                                .isCustomizable(productMerchantDetail.getIsCustomizable())
                                                .productPrice(productStore.getStorePrice())
                                                .discountType(productStore.getDiscountType())
                                                .discount(productStore.getDiscount())
                                                .productPriceAfterDiscount(productStore.getFinalPrice())
                                                .productImageMain(productMerchantDetail.getProductImageMain())
                                                .productImage1(productMerchantDetail.getProductImage1())
                                                .productImage2(productMerchantDetail.getProductImage2())
                                                .productImage3(productMerchantDetail.getProductImage3())
                                                .productImage4(productMerchantDetail.getProductImage4())
                                                .build();
                                                responseProd.setProductDetail(productDetailResponse);
                                            }

                                            for (ProductStore dataPStore : dataPS) {
                                                ProductResponseStore.ProductStore responsePStore = new ProductResponseStore.ProductStore();

                                                Store stored = Store.findById(dataPStore.getStore().id);
                                                if (stored != null) {
                                                    responsePStore.setId(dataPStore.id);
                                                    responsePStore.setStoreId(dataPStore.getStore().id);
                                                    responsePStore.setProductId(dataPStore.getProductMerchant().id);
                                                    responsePStore.setIsActive(dataPStore.isActive);
                                                    responsePStore.setStorePrice(dataPStore.getStorePrice());
                                                    responsePStore.setDiscountType(dataPStore.getDiscountType());
                                                    responsePStore.setDiscount(dataPStore.getDiscount());
                                                    responsePStore.setIsDeleted(dataPStore.isDeleted);
                                                    responsePStore.setFinalPrice(dataPStore.getFinalPrice());
                                                    responsePStore.setStoresName(stored.storeName);
                                                    responsesProductStore.add(responsePStore);
                                                }
                                                responseProd.setProductStore(responsePStore != null ? responsesProductStore : null);
                                            }

                                            responses.add(responseProd);
                                        }
                                    }
                                } else {
                                    responseProd.setProductId(productMerchant.id);
                                    responseProd.setProductName(productMerchant.getProductName());
                                    responseProd.setIsActive(productMerchant.getIsActive());
                                    productMerchantDetail = ProductMerchantDetailRepository.findByProduct(productMerchant);
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
                                        responseProd.setProductDetail(productDetailResponse);
                                    }

                                    for (ProductStore dataPStore : dataPS) {
                                        ProductResponseStore.ProductStore responsePStore = new ProductResponseStore.ProductStore();

                                        Store stored = Store.findById(dataPStore.getStore().id);
                                        if (stored != null) {
                                            responsePStore.setId(dataPStore.id);
                                            responsePStore.setStoreId(dataPStore.getStore().id);
                                            responsePStore.setProductId(dataPStore.getProductMerchant().id);
                                            responsePStore.setIsActive(dataPStore.isActive);
                                            responsePStore.setStorePrice(dataPStore.getStorePrice());
                                            responsePStore.setDiscountType(dataPStore.getDiscountType());
                                            responsePStore.setDiscount(dataPStore.getDiscount());
                                            responsePStore.setIsDeleted(dataPStore.isDeleted);
                                            responsePStore.setFinalPrice(dataPStore.getFinalPrice());
                                            responsePStore.setStoresName(stored.storeName);
                                            responsesProductStore.add(responsePStore);
                                        }
                                        responseProd.setProductStore(responsePStore != null ? responsesProductStore : null);
                                    }

                                    responses.add(responseProd);
                                }
                            }
                            response.setBaseResponse(responses.size(), offset, limit, success + " menampilkan data", responses);
                            return ok(Json.toJson(response));
                        } else {
                            response.setBaseResponse(0, 0, 0, "Store tidak ditemukan", null);
                            return notFound(Json.toJson(response));
                        }
                    } catch (Exception e) {
                        logger.error("Error saat menampilkan produk", e);
                        e.printStackTrace();
                    }
                } else {
                    Query<ProductMerchant> query = ProductMerchantRepository.findProductIsActiveAndMerchant(ownMerchant, true);
                    try {
                        List<ProductResponseStore> responses = new ArrayList<>();
                        List<ProductMerchant> totalData = ProductMerchantRepository.getTotalDataPage(query);
                        List<ProductMerchant> productMerchants = ProductMerchantRepository.findProductWithPaging(query, sort,
                                keyword, offset, limit);
                        for (ProductMerchant data : productMerchants) {
                            ProductResponseStore responseProd = new ProductResponseStore();
                            Query<ProductStore> queryPS = ProductStoreRepository.find.where().eq("t0.product_id", data.id)
                                    .eq("t0.is_deleted", false).eq("merchant", ownMerchant).order("t0.id");
                            List<ProductStore> dataPS = ProductStoreRepository.getDataProductStore(queryPS);
                            List<ProductResponseStore.ProductStore> responsesProductStore = new ArrayList<>();
                            responseProd.setProductId(data.id);
                            responseProd.setProductName(data.getProductName());
                            responseProd.setIsActive(data.getIsActive());
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
                                responseProd.setProductDetail(productDetailResponse);
                            }

                            responseProd.setMerchantId(data.getMerchant().id);

                            for (ProductStore dataPStore : dataPS) {
                                ProductResponseStore.ProductStore responsePStore = new ProductResponseStore.ProductStore();

                                Store store = Store.findById(dataPStore.getStore().id);
                                if (store != null) {
                                    responsePStore.setId(dataPStore.id);
                                    responsePStore.setStoreId(dataPStore.getStore().id);
                                    responsePStore.setProductId(dataPStore.getProductMerchant().id);
                                    responsePStore.setIsActive(dataPStore.isActive);
                                    responsePStore.setStorePrice(dataPStore.getStorePrice());
                                    responsePStore.setDiscountType(dataPStore.getDiscountType());
                                    responsePStore.setDiscount(dataPStore.getDiscount());
                                    responsePStore.setIsDeleted(dataPStore.isDeleted);
                                    responsePStore.setFinalPrice(dataPStore.getFinalPrice());
                                    responsePStore.setStoresName(store.storeName);
                                    responsesProductStore.add(responsePStore);
                                }
                                responseProd.setProductStore(responsePStore != null ? responsesProductStore : null);
                            }
                            responses.add(responseProd);
                        }
                        response.setBaseResponse(
                                filter == null || filter.equals("") ? totalData.size() : productMerchants.size(), offset, limit,
                                success + " menampilkan data", responses);
                        return ok(Json.toJson(response));
                    } catch (IOException e) {
                        Logger.error("allDetail", e);
                    }
                }
            } else {
                response.setBaseResponse(0, 0, 0, unauthorized, null);
                return unauthorized(Json.toJson(response));
            }
        } catch (Exception e) {
            Logger.error("allDetail", e);
            response.setBaseResponse(0, 0, 0, e.toString(), null);
            return badRequest(Json.toJson(response));
        }

        response.setBaseResponse(0, 0, 0, "Something gone wrong", null);
        return badRequest(Json.toJson(response));
    }

    public static Result listProductStorePOSV2(Long storeId, String keyword, String filter, String sort, int offset, int limit) {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (!ownMerchant.id.equals(0L) && !storeId.equals(0L)) {
            try {
                Store store = Store.find.byId(storeId);
                if (store != null) {
                    String searchQuery = keyword != null && keyword.length() > 0 ? " and lower(pm.product_name) like '%"+keyword+"%'" : "";
                    String querySql = "t0.product_merchant_id in (select pm.id from product_merchant pm where pm.merchant_id = "+ownMerchant.id+" and pm.is_active = "+true+" and pm.is_deleted = false"+searchQuery+")";
                    Query<ProductMerchantDetail> query = ProductMerchantDetailRepository.find.where().raw(querySql).eq("t0.is_deleted", false).eq("t0.product_type", "MAIN").order("t0.id desc");
                    List<ProductMerchantDetail> dataProductDetail = ProductMerchantDetailRepository.getDataByPagination(query, offset, limit);

                    List<ProductSpecificStoreResponse> responses = new ArrayList<>();
                    for (ProductMerchantDetail productMerchantDetail : dataProductDetail) {
                        ProductSpecificStoreResponse responseProd = new ProductSpecificStoreResponse();
                        List<ProductStore> listProductStore = ProductStoreRepository.find.where().eq("t0.is_deleted", false).eq("t0.is_active", true).eq("t0.product_id", productMerchantDetail.getProductMerchant().id).orderBy().desc("t0.id").findList();
                        if (listProductStore.size() > 0) {
                            for (ProductStore productStore : listProductStore) {
                                if (productStore.getStore().id.equals(store.id)) {
                                    responseProd.setProductId(productMerchantDetail.getProductMerchant().id);
                                    responseProd.setProductName(productMerchantDetail.getProductMerchant().getProductName());
                                    responseProd.setIsActive(productMerchantDetail.getProductMerchant().getIsActive());
                                    ProductDetailResponse productDetailResponse = ProductDetailResponse.builder()
                                            .productType(productMerchantDetail.getProductType())
                                            .isCustomizable(productMerchantDetail.getIsCustomizable())
                                            .productPrice(productStore.getStorePrice())
                                            .discountType(productStore.getDiscountType())
                                            .discount(productStore.getDiscount())
                                            .productPriceAfterDiscount(productStore.getFinalPrice())
                                            .productImageMain(productMerchantDetail.getProductImageMain())
                                            .productImage1(productMerchantDetail.getProductImage1())
                                            .productImage2(productMerchantDetail.getProductImage2())
                                            .productImage3(productMerchantDetail.getProductImage3())
                                            .productImage4(productMerchantDetail.getProductImage4())
                                            .build();
                                    responseProd.setProductDetail(productDetailResponse);
                                    ProductSpecificStoreResponse.Brand brand =  new ProductSpecificStoreResponse.Brand();
                                    brand.setBrandId(productMerchantDetail.getProductMerchant().getBrandMerchant().id);
                                    brand.setBrandName(productMerchantDetail.getProductMerchant().getBrandMerchant().getBrandName());

                                    ProductSpecificStoreResponse.Category category = new ProductSpecificStoreResponse.Category();
                                    category.setCategoryId(productMerchantDetail.getProductMerchant().getCategoryMerchant().id);
                                    category.setCategoryName(productMerchantDetail.getProductMerchant().getCategoryMerchant().getCategoryName());

                                    ProductSpecificStoreResponse.SubCategory subCategory = new ProductSpecificStoreResponse.SubCategory();
                                    subCategory.setSubCategoryId(productMerchantDetail.getProductMerchant().getSubCategoryMerchant().id);
                                    subCategory.setSubCategoryName(productMerchantDetail.getProductMerchant().getSubCategoryMerchant().getSubcategoryName());

                                    ProductSpecificStoreResponse.SubsCategory subsCategory = new ProductSpecificStoreResponse.SubsCategory();
                                    subsCategory.setSubsCategoryId(productMerchantDetail.getProductMerchant().getSubsCategoryMerchant().id);
                                    subsCategory.setSubsCategoryName(productMerchantDetail.getProductMerchant().getSubsCategoryMerchant().getSubscategoryName());

                                    responseProd.setBrand(brand);
                                    responseProd.setCategory(category);
                                    responseProd.setSubCategory(subCategory);
                                    responseProd.setSubsCategory(subsCategory);
                                    responseProd.setMerchantId(productMerchantDetail.getProductMerchant().getMerchant().id);
                                    ProductSpecificStoreResponse.ProductStore responseStore = new  ProductSpecificStoreResponse.ProductStore();
                                    responseStore.setId(productStore.id);
                                    responseStore.setStoreId(productStore.getStore().id);
                                    responseStore.setProductId(productStore.getProductMerchant().id);
                                    responseStore.setIsActive(productStore.isActive);
                                    responseStore.setStorePrice(productStore.getStorePrice());
                                    responseStore.setDiscountType(productStore.getDiscountType());
                                    responseStore.setDiscount(productStore.getDiscount());
                                    responseStore.setIsDeleted(productStore.isDeleted);
                                    responseStore.setFinalPrice(productStore.getFinalPrice());

                                    responseStore.setStoresName(store.storeName);

                                    responseProd.setProductStore(responseStore != null ? responseStore : null);

                                    responses.add(responseProd);
                                }
                            }
                        } else {
                            responseProd.setProductId(productMerchantDetail.getProductMerchant().id);
                            responseProd.setProductName(productMerchantDetail.getProductMerchant().getProductName());
                            responseProd.setIsActive(productMerchantDetail.getProductMerchant().getIsActive());
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
                            responseProd.setProductDetail(productDetailResponse);
                            ProductSpecificStoreResponse.Brand brand =  new ProductSpecificStoreResponse.Brand();
                            brand.setBrandId(productMerchantDetail.getProductMerchant().getBrandMerchant().id);
                            brand.setBrandName(productMerchantDetail.getProductMerchant().getBrandMerchant().getBrandName());

                            ProductSpecificStoreResponse.Category category = new ProductSpecificStoreResponse.Category();
                            category.setCategoryId(productMerchantDetail.getProductMerchant().getCategoryMerchant().id);
                            category.setCategoryName(productMerchantDetail.getProductMerchant().getCategoryMerchant().getCategoryName());

                            ProductSpecificStoreResponse.SubCategory subCategory = new ProductSpecificStoreResponse.SubCategory();
                            subCategory.setSubCategoryId(productMerchantDetail.getProductMerchant().getSubCategoryMerchant().id);
                            subCategory.setSubCategoryName(productMerchantDetail.getProductMerchant().getSubCategoryMerchant().getSubcategoryName());

                            ProductSpecificStoreResponse.SubsCategory subsCategory = new ProductSpecificStoreResponse.SubsCategory();
                            subsCategory.setSubsCategoryId(productMerchantDetail.getProductMerchant().getSubsCategoryMerchant().id);
                            subsCategory.setSubsCategoryName(productMerchantDetail.getProductMerchant().getSubsCategoryMerchant().getSubscategoryName());

                            responseProd.setBrand(brand);
                            responseProd.setCategory(category);
                            responseProd.setSubCategory(subCategory);
                            responseProd.setSubsCategory(subsCategory);
                            responseProd.setMerchantId(productMerchantDetail.getProductMerchant().getMerchant().id);

                            responses.add(responseProd);
                        }
                    }
                    response.setBaseResponse(
                            responses.size(), offset, limit,
                            success + " menampilkan data", responses
                    );
                } else if (store == null) {
                    response.setBaseResponse(0, 0, 0, "Store tidak ditemukan.", null);
                    return notFound(Json.toJson(response));
                }
            } catch (Exception e) {
                logger.error("Error saat menampilkan produk", e);
                e.printStackTrace();
            }

        } else {
            response.setBaseResponse(0, 0, 0, notFound, null);
            return notFound(Json.toJson(response));
        }

        if (response.getData() != null) {
            return ok(Json.toJson(response));
        } else {
            return notFound(Json.toJson(response));
        }
    }

    public static Result listProductStoreByCategoriesPOSV2(Long merchantId, Long storeId, Long categoryId, Long subCategoryId, Long subsCategoryId, String keyword, String filter, String sort, int offset, int limit) {
        if (!merchantId.equals(0L) && !storeId.equals(0L)) {
            try {
                Merchant merchant = Merchant.find.byId(merchantId);

                if (merchant != null) {
                    Store store = Store.find.byId(storeId);

                    if (store != null) {
                        String searchQuery = keyword != null && keyword.length() > 0 ? " and lower(t0.product_name) like '%"+keyword+"%'" : "";
                        String querySql = " t0.is_active = "+true;
                        if (!categoryId.equals(0L) && !subCategoryId.equals(0L) && !subsCategoryId.equals(0L)) {
                            querySql = " t0.is_active = "+true+" and t0.category_merchant_id = "+categoryId+" and t0.sub_category_merchant_id = "+subCategoryId+" and t0.subs_category_merchant_id = "+subsCategoryId+searchQuery;
                        } else if (!categoryId.equals(0L) && !subCategoryId.equals(0L) && subsCategoryId.equals(0L)) {
                            querySql = " t0.is_active = "+true+" and t0.category_merchant_id = "+categoryId+" and t0.sub_category_merchant_id = "+subCategoryId+searchQuery;
                        } else if (!categoryId.equals(0L) && subCategoryId.equals(0L) && !subsCategoryId.equals(0L)) {
                            querySql = " t0.is_active = "+true+" and t0.category_merchant_id = "+categoryId+" and t0.subs_category_merchant_id = "+subsCategoryId+searchQuery;
                        } else if (categoryId.equals(0L) && !subCategoryId.equals(0L) && !subsCategoryId.equals(0L)) {
                            querySql = " t0.is_active = "+true+" and t0.sub_category_merchant_id = "+subCategoryId+" and t0.subs_category_merchant_id = "+subsCategoryId+searchQuery;
                        } else if (categoryId.equals(0L) && subCategoryId.equals(0L) && !subsCategoryId.equals(0L)) {
                            querySql = " t0.is_active = "+true+" and t0.subs_category_merchant_id = "+subsCategoryId+searchQuery;
                        } else if (categoryId.equals(0L) && !subCategoryId.equals(0L) && subsCategoryId.equals(0L)) {
                            querySql = " t0.is_active = "+true+" and t0.sub_category_merchant_id = "+subCategoryId+searchQuery;
                        } else if (!categoryId.equals(0L) && subCategoryId.equals(0L) && subsCategoryId.equals(0L)) {
                            querySql = " t0.is_active = "+true+" and t0.category_merchant_id = "+categoryId+searchQuery;
                        } else {
                            querySql = " t0.is_active = "+true+searchQuery;
                        }
                        Query<ProductMerchant> query = ProductMerchantRepository.find.where().eq("t0.merchant_id", merchantId).raw(querySql).eq("t0.is_deleted", false).orderBy().desc("t0.id");
                        List<ProductMerchant> productMerchants = ProductMerchantRepository.findProductWithPaging(query, sort,
                                filter, offset, limit);

                        List<ProductSpecificStoreResponse> responses = new ArrayList<>();
                        for (ProductMerchant productMerchant : productMerchants) {
                            ProductSpecificStoreResponse responseProd = new ProductSpecificStoreResponse();
                            List<ProductStore> listProductStore = ProductStoreRepository.find.where().eq("t0.is_deleted", false).eq("t0.is_active", true).eq("t0.product_id", productMerchant.id).orderBy().desc("t0.id").findList();
                            if (listProductStore.size() > 0) {
                                for (ProductStore productStore : listProductStore) {
                                    if (productStore.getStore().id.equals(store.id)) {
                                        responseProd.setProductId(productMerchant.id);
                                        responseProd.setProductName(productMerchant.getProductName());
                                        responseProd.setIsActive(productMerchant.getIsActive());
                                        ProductMerchantDetail productMerchantDetail = ProductMerchantDetailRepository.findByProduct(productMerchant);
                                        if (productMerchantDetail != null) {
                                            ProductDetailResponse productDetailResponse = ProductDetailResponse.builder()
                                                    .productType(productMerchantDetail.getProductType())
                                                    .isCustomizable(productMerchantDetail.getIsCustomizable())
                                                    .productPrice(productStore.getStorePrice())
                                                    .discountType(productStore.getDiscountType())
                                                    .discount(productStore.getDiscount())
                                                    .productPriceAfterDiscount(productStore.getFinalPrice())
                                                    .productImageMain(productMerchantDetail.getProductImageMain())
                                                    .productImage1(productMerchantDetail.getProductImage1())
                                                    .productImage2(productMerchantDetail.getProductImage2())
                                                    .productImage3(productMerchantDetail.getProductImage3())
                                                    .productImage4(productMerchantDetail.getProductImage4())
                                                    .build();
                                            responseProd.setProductDetail(productDetailResponse);
                                        }
                                        ProductSpecificStoreResponse.Brand brand =  new ProductSpecificStoreResponse.Brand();
                                        brand.setBrandId(productMerchant.getBrandMerchant().id);
                                        brand.setBrandName(productMerchant.getBrandMerchant().getBrandName());

                                        ProductSpecificStoreResponse.Category category = new ProductSpecificStoreResponse.Category();
                                        category.setCategoryId(productMerchant.getCategoryMerchant().id);
                                        category.setCategoryName(productMerchant.getCategoryMerchant().getCategoryName());

                                        ProductSpecificStoreResponse.SubCategory subCategory = new ProductSpecificStoreResponse.SubCategory();
                                        subCategory.setSubCategoryId(productMerchant.getSubCategoryMerchant().id);
                                        subCategory.setSubCategoryName(productMerchant.getSubCategoryMerchant().getSubcategoryName());

                                        ProductSpecificStoreResponse.SubsCategory subsCategory = new ProductSpecificStoreResponse.SubsCategory();
                                        subsCategory.setSubsCategoryId(productMerchant.getSubsCategoryMerchant().id);
                                        subsCategory.setSubsCategoryName(productMerchant.getSubsCategoryMerchant().getSubscategoryName());

                                        responseProd.setBrand(brand);
                                        responseProd.setCategory(category);
                                        responseProd.setSubCategory(subCategory);
                                        responseProd.setSubsCategory(subsCategory);
                                        responseProd.setMerchantId(productMerchant.getMerchant().id);
                                        ProductSpecificStoreResponse.ProductStore responseStore = new  ProductSpecificStoreResponse.ProductStore();
                                        responseStore.setId(productStore.id);
                                        responseStore.setStoreId(productStore.getStore().id);
                                        responseStore.setProductId(productStore.getProductMerchant().id);
                                        responseStore.setIsActive(productStore.isActive);
                                        responseStore.setStorePrice(productStore.getStorePrice());
                                        responseStore.setDiscountType(productStore.getDiscountType());
                                        responseStore.setDiscount(productStore.getDiscount());
                                        responseStore.setIsDeleted(productStore.isDeleted);
                                        responseStore.setFinalPrice(productStore.getFinalPrice());

                                        responseStore.setStoresName(store.storeName);

                                        responseProd.setProductStore(responseStore != null ? responseStore : null);

                                        responses.add(responseProd);
                                    }
                                }
                            } else {
                                responseProd.setProductId(productMerchant.id);
                                responseProd.setProductName(productMerchant.getProductName());
                                responseProd.setIsActive(productMerchant.getIsActive());
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
                                    responseProd.setProductDetail(productDetailResponse);
                                }
                                ProductSpecificStoreResponse.Brand brand =  new ProductSpecificStoreResponse.Brand();
                                brand.setBrandId(productMerchant.getBrandMerchant().id);
                                brand.setBrandName(productMerchant.getBrandMerchant().getBrandName());

                                ProductSpecificStoreResponse.Category category = new ProductSpecificStoreResponse.Category();
                                category.setCategoryId(productMerchant.getCategoryMerchant().id);
                                category.setCategoryName(productMerchant.getCategoryMerchant().getCategoryName());

                                ProductSpecificStoreResponse.SubCategory subCategory = new ProductSpecificStoreResponse.SubCategory();
                                subCategory.setSubCategoryId(productMerchant.getSubCategoryMerchant().id);
                                subCategory.setSubCategoryName(productMerchant.getSubCategoryMerchant().getSubcategoryName());

                                ProductSpecificStoreResponse.SubsCategory subsCategory = new ProductSpecificStoreResponse.SubsCategory();
                                subsCategory.setSubsCategoryId(productMerchant.getSubsCategoryMerchant().id);
                                subsCategory.setSubsCategoryName(productMerchant.getSubsCategoryMerchant().getSubscategoryName());

                                responseProd.setBrand(brand);
                                responseProd.setCategory(category);
                                responseProd.setSubCategory(subCategory);
                                responseProd.setSubsCategory(subsCategory);
                                responseProd.setMerchantId(productMerchant.getMerchant().id);

                                responses.add(responseProd);
                            }
                        }
                        response.setBaseResponse(
                                responses.size(), offset, limit,
                                success + " menampilkan data", responses
                        );
                    } else if (store == null) {
                        response.setBaseResponse(0, 0, 0, "Store tidak ditemukan.", null);
                        return notFound(Json.toJson(response));
                    }
                } else if (merchant == null) {
                    response.setBaseResponse(0, 0, 0, "Merchant tidak ditemukan.", null);
                    return notFound(Json.toJson(response));
                }
            } catch (Exception e) {
                logger.error("Error saat menampilkan produk", e);
                e.printStackTrace();
            }

        } else {
            response.setBaseResponse(0, 0, 0, notFound, null);
            return notFound(Json.toJson(response));
        }

        if (response.getData() != null) {
            return ok(Json.toJson(response));
        } else {
            return notFound(Json.toJson(response));
        }
    }

    @ApiOperation(value = "Get all product store.", notes = "Returns list of product store.\n" + swaggerInfo
            + "", response = Product.class, responseContainer = "List", httpMethod = "GET")
    public static Result listProductStoreForKiosk(Long merchantId, Long storeId, String keyword, String filter, String sort, int offset, int limit) {
        if (!merchantId.equals(0L) && !storeId.equals(0L)) {
            try {
                Merchant merchant = Merchant.find.byId(merchantId);

                if (merchant != null) {
                    Store store = Store.find.byId(storeId);

                    if (store != null) {
                        String searchQuery = keyword != null && keyword.length() > 0 ? " and lower(pm.product_name) like '%"+keyword+"%'" : "";
                        String querySql = "t0.product_merchant_id in (select pm.id from product_merchant pm where pm.merchant_id = "+merchantId+" and pm.is_active = "+true+" and pm.is_deleted = false"+searchQuery+")";
                        Query<ProductMerchantDetail> query = ProductMerchantDetailRepository.find.where().raw(querySql).eq("t0.is_deleted", false).eq("t0.product_type", "MAIN").order("t0.id desc");
                        List<ProductMerchantDetail> dataProductDetail = ProductMerchantDetailRepository.getDataByPagination(query, offset, limit);

                        List<ProductSpecificStoreResponse> responses = new ArrayList<>();
                        for (ProductMerchantDetail productMerchantDetail : dataProductDetail) {
                            ProductSpecificStoreResponse responseProd = new ProductSpecificStoreResponse();
                            List<ProductStore> listProductStore = ProductStoreRepository.find.where().eq("t0.is_deleted", false).eq("t0.is_active", true).eq("t0.product_id", productMerchantDetail.getProductMerchant().id).orderBy().desc("t0.id").findList();
                            if (listProductStore.size() > 0) {
                                for (ProductStore productStore : listProductStore) {
                                    if (productStore.getStore().id.equals(store.id)) {
                                        responseProd.setProductId(productMerchantDetail.getProductMerchant().id);
                                        responseProd.setProductName(productMerchantDetail.getProductMerchant().getProductName());
                                        responseProd.setIsActive(productMerchantDetail.getProductMerchant().getIsActive());
                                        ProductDetailResponse productDetailResponse = ProductDetailResponse.builder()
                                                .productType(productMerchantDetail.getProductType())
                                                .isCustomizable(productMerchantDetail.getIsCustomizable())
                                                .productPrice(productStore.getStorePrice())
                                                .discountType(productStore.getDiscountType())
                                                .discount(productStore.getDiscount())
                                                .productPriceAfterDiscount(productStore.getFinalPrice())
                                                .productImageMain(productMerchantDetail.getProductImageMain())
                                                .productImage1(productMerchantDetail.getProductImage1())
                                                .productImage2(productMerchantDetail.getProductImage2())
                                                .productImage3(productMerchantDetail.getProductImage3())
                                                .productImage4(productMerchantDetail.getProductImage4())
                                                .build();
                                        responseProd.setProductDetail(productDetailResponse);
                                        ProductSpecificStoreResponse.Brand brand =  new ProductSpecificStoreResponse.Brand();
                                        brand.setBrandId(productMerchantDetail.getProductMerchant().getBrandMerchant().id);
                                        brand.setBrandName(productMerchantDetail.getProductMerchant().getBrandMerchant().getBrandName());

                                        ProductSpecificStoreResponse.Category category = new ProductSpecificStoreResponse.Category();
                                        category.setCategoryId(productMerchantDetail.getProductMerchant().getCategoryMerchant().id);
                                        category.setCategoryName(productMerchantDetail.getProductMerchant().getCategoryMerchant().getCategoryName());

                                        ProductSpecificStoreResponse.SubCategory subCategory = new ProductSpecificStoreResponse.SubCategory();
                                        subCategory.setSubCategoryId(productMerchantDetail.getProductMerchant().getSubCategoryMerchant().id);
                                        subCategory.setSubCategoryName(productMerchantDetail.getProductMerchant().getSubCategoryMerchant().getSubcategoryName());

                                        ProductSpecificStoreResponse.SubsCategory subsCategory = new ProductSpecificStoreResponse.SubsCategory();
                                        subsCategory.setSubsCategoryId(productMerchantDetail.getProductMerchant().getSubsCategoryMerchant().id);
                                        subsCategory.setSubsCategoryName(productMerchantDetail.getProductMerchant().getSubsCategoryMerchant().getSubscategoryName());

                                        responseProd.setBrand(brand);
                                        responseProd.setCategory(category);
                                        responseProd.setSubCategory(subCategory);
                                        responseProd.setSubsCategory(subsCategory);
                                        responseProd.setMerchantId(productMerchantDetail.getProductMerchant().getMerchant().id);
                                        ProductSpecificStoreResponse.ProductStore responseStore = new  ProductSpecificStoreResponse.ProductStore();
                                        responseStore.setId(productStore.id);
                                        responseStore.setStoreId(productStore.getStore().id);
                                        responseStore.setProductId(productStore.getProductMerchant().id);
                                        responseStore.setIsActive(productStore.isActive);
                                        responseStore.setStorePrice(productStore.getStorePrice());
                                        responseStore.setDiscountType(productStore.getDiscountType());
                                        responseStore.setDiscount(productStore.getDiscount());
                                        responseStore.setIsDeleted(productStore.isDeleted);
                                        responseStore.setFinalPrice(productStore.getFinalPrice());

                                        responseStore.setStoresName(store.storeName);

                                        responseProd.setProductStore(responseStore != null ? responseStore : null);

                                        responses.add(responseProd);
                                    }
                                }
                            } else {
                                responseProd.setProductId(productMerchantDetail.getProductMerchant().id);
                                responseProd.setProductName(productMerchantDetail.getProductMerchant().getProductName());
                                responseProd.setIsActive(productMerchantDetail.getProductMerchant().getIsActive());
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
                                responseProd.setProductDetail(productDetailResponse);
                                ProductSpecificStoreResponse.Brand brand =  new ProductSpecificStoreResponse.Brand();
                                brand.setBrandId(productMerchantDetail.getProductMerchant().getBrandMerchant().id);
                                brand.setBrandName(productMerchantDetail.getProductMerchant().getBrandMerchant().getBrandName());

                                ProductSpecificStoreResponse.Category category = new ProductSpecificStoreResponse.Category();
                                category.setCategoryId(productMerchantDetail.getProductMerchant().getCategoryMerchant().id);
                                category.setCategoryName(productMerchantDetail.getProductMerchant().getCategoryMerchant().getCategoryName());

                                ProductSpecificStoreResponse.SubCategory subCategory = new ProductSpecificStoreResponse.SubCategory();
                                subCategory.setSubCategoryId(productMerchantDetail.getProductMerchant().getSubCategoryMerchant().id);
                                subCategory.setSubCategoryName(productMerchantDetail.getProductMerchant().getSubCategoryMerchant().getSubcategoryName());

                                ProductSpecificStoreResponse.SubsCategory subsCategory = new ProductSpecificStoreResponse.SubsCategory();
                                subsCategory.setSubsCategoryId(productMerchantDetail.getProductMerchant().getSubsCategoryMerchant().id);
                                subsCategory.setSubsCategoryName(productMerchantDetail.getProductMerchant().getSubsCategoryMerchant().getSubscategoryName());

                                responseProd.setBrand(brand);
                                responseProd.setCategory(category);
                                responseProd.setSubCategory(subCategory);
                                responseProd.setSubsCategory(subsCategory);
                                responseProd.setMerchantId(productMerchantDetail.getProductMerchant().getMerchant().id);

                                responses.add(responseProd);
                            }
                        }
                        response.setBaseResponse(
                                responses.size(), offset, limit,
                                success + " menampilkan data", responses
                        );
                    } else if (store == null) {
                        response.setBaseResponse(0, 0, 0, "Store tidak ditemukan.", null);
                        return notFound(Json.toJson(response));
                    }
                } else if (merchant == null) {
                    response.setBaseResponse(0, 0, 0, "Merchant tidak ditemukan.", null);
                    return notFound(Json.toJson(response));
                }
            } catch (Exception e) {
                logger.error("Error saat menampilkan produk", e);
                e.printStackTrace();
            }

        } else {
            response.setBaseResponse(0, 0, 0, notFound, null);
            return notFound(Json.toJson(response));
        }

        if (response.getData() != null) {
            return ok(Json.toJson(response));
        } else {
            return notFound(Json.toJson(response));
        }
    }

    @ApiOperation(value = "Get all product store.", notes = "Returns list of product store.\n" + swaggerInfo
            + "", response = Product.class, responseContainer = "List", httpMethod = "GET")
    public static Result listProductStoreForMobileQR(Long merchantId, Long storeId, String keyword, String filter, String sort, int offset, int limit) {
        if (!merchantId.equals(0L) && !storeId.equals(0L)) {
            try {
                Merchant merchant = Merchant.find.byId(merchantId);
                if (merchant != null) {
                    Store store = Store.find.byId(storeId);
                    if (store != null) {
                        String searchQuery = keyword != null && keyword.length() > 0 ? " and lower(pm.product_name) like '%"+keyword+"%'" : "";
                        String querySql = "t0.product_merchant_id in (select pm.id from product_merchant pm where pm.merchant_id = "+merchantId+" and pm.is_active = "+true+" and pm.is_deleted = false"+searchQuery+")";
                        Query<ProductMerchantDetail> query = ProductMerchantDetailRepository.find.where().raw(querySql).eq("t0.is_deleted", false).eq("t0.product_type", "MAIN").order("t0.id desc");
                        List<ProductMerchantDetail> dataProductDetail = ProductMerchantDetailRepository.getDataByPagination(query, offset, limit);

                        List<ProductSpecificStoreResponse> responses = new ArrayList<>();
                        for (ProductMerchantDetail productMerchantDetail : dataProductDetail) {
                            ProductSpecificStoreResponse responseProd = new ProductSpecificStoreResponse();
                            List<ProductStore> listProductStore = ProductStoreRepository.find.where().eq("t0.is_deleted", false).eq("t0.is_active", true).eq("t0.product_id", productMerchantDetail.getProductMerchant().id).orderBy().desc("t0.id").findList();
                            if (listProductStore.size() > 0) {
                                for (ProductStore productStore : listProductStore) {
                                    if (productStore.getStore().id.equals(store.id)) {
                                        responseProd.setProductId(productMerchantDetail.getProductMerchant().id);
                                        responseProd.setProductName(productMerchantDetail.getProductMerchant().getProductName());
                                        responseProd.setIsActive(productMerchantDetail.getProductMerchant().getIsActive());
                                        ProductDetailResponse productDetailResponse = ProductDetailResponse.builder()
                                                .productType(productMerchantDetail.getProductType())
                                                .isCustomizable(productMerchantDetail.getIsCustomizable())
                                                .productPrice(productStore.getStorePrice())
                                                .discountType(productStore.getDiscountType())
                                                .discount(productStore.getDiscount())
                                                .productPriceAfterDiscount(productStore.getFinalPrice())
                                                .productImageMain(productMerchantDetail.getProductImageMain())
                                                .productImage1(productMerchantDetail.getProductImage1())
                                                .productImage2(productMerchantDetail.getProductImage2())
                                                .productImage3(productMerchantDetail.getProductImage3())
                                                .productImage4(productMerchantDetail.getProductImage4())
                                                .build();
                                        responseProd.setProductDetail(productDetailResponse);
                                        ProductSpecificStoreResponse.Brand brand =  new ProductSpecificStoreResponse.Brand();
                                        brand.setBrandId(productMerchantDetail.getProductMerchant().getBrandMerchant().id);
                                        brand.setBrandName(productMerchantDetail.getProductMerchant().getBrandMerchant().getBrandName());

                                        ProductSpecificStoreResponse.Category category = new ProductSpecificStoreResponse.Category();
                                        category.setCategoryId(productMerchantDetail.getProductMerchant().getCategoryMerchant().id);
                                        category.setCategoryName(productMerchantDetail.getProductMerchant().getCategoryMerchant().getCategoryName());

                                        ProductSpecificStoreResponse.SubCategory subCategory = new ProductSpecificStoreResponse.SubCategory();
                                        subCategory.setSubCategoryId(productMerchantDetail.getProductMerchant().getSubCategoryMerchant().id);
                                        subCategory.setSubCategoryName(productMerchantDetail.getProductMerchant().getSubCategoryMerchant().getSubcategoryName());

                                        ProductSpecificStoreResponse.SubsCategory subsCategory = new ProductSpecificStoreResponse.SubsCategory();
                                        subsCategory.setSubsCategoryId(productMerchantDetail.getProductMerchant().getSubsCategoryMerchant().id);
                                        subsCategory.setSubsCategoryName(productMerchantDetail.getProductMerchant().getSubsCategoryMerchant().getSubscategoryName());

                                        responseProd.setBrand(brand);
                                        responseProd.setCategory(category);
                                        responseProd.setSubCategory(subCategory);
                                        responseProd.setSubsCategory(subsCategory);
                                        responseProd.setMerchantId(productMerchantDetail.getProductMerchant().getMerchant().id);
                                        ProductSpecificStoreResponse.ProductStore responseStore = new  ProductSpecificStoreResponse.ProductStore();
                                        responseStore.setId(productStore.id);
                                        responseStore.setStoreId(productStore.getStore().id);
                                        responseStore.setProductId(productStore.getProductMerchant().id);
                                        responseStore.setIsActive(productStore.isActive);
                                        responseStore.setStorePrice(productStore.getStorePrice());
                                        responseStore.setDiscountType(productStore.getDiscountType());
                                        responseStore.setDiscount(productStore.getDiscount());
                                        responseStore.setIsDeleted(productStore.isDeleted);
                                        responseStore.setFinalPrice(productStore.getFinalPrice());

                                        responseStore.setStoresName(store.storeName);

                                        responseProd.setProductStore(responseStore != null ? responseStore : null);

                                        responses.add(responseProd);
                                    }
                                }
                            } else {
                                responseProd.setProductId(productMerchantDetail.getProductMerchant().id);
                                responseProd.setProductName(productMerchantDetail.getProductMerchant().getProductName());
                                responseProd.setIsActive(productMerchantDetail.getProductMerchant().getIsActive());
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
                                responseProd.setProductDetail(productDetailResponse);
                                ProductSpecificStoreResponse.Brand brand =  new ProductSpecificStoreResponse.Brand();
                                brand.setBrandId(productMerchantDetail.getProductMerchant().getBrandMerchant().id);
                                brand.setBrandName(productMerchantDetail.getProductMerchant().getBrandMerchant().getBrandName());

                                ProductSpecificStoreResponse.Category category = new ProductSpecificStoreResponse.Category();
                                category.setCategoryId(productMerchantDetail.getProductMerchant().getCategoryMerchant().id);
                                category.setCategoryName(productMerchantDetail.getProductMerchant().getCategoryMerchant().getCategoryName());

                                ProductSpecificStoreResponse.SubCategory subCategory = new ProductSpecificStoreResponse.SubCategory();
                                subCategory.setSubCategoryId(productMerchantDetail.getProductMerchant().getSubCategoryMerchant().id);
                                subCategory.setSubCategoryName(productMerchantDetail.getProductMerchant().getSubCategoryMerchant().getSubcategoryName());

                                ProductSpecificStoreResponse.SubsCategory subsCategory = new ProductSpecificStoreResponse.SubsCategory();
                                subsCategory.setSubsCategoryId(productMerchantDetail.getProductMerchant().getSubsCategoryMerchant().id);
                                subsCategory.setSubsCategoryName(productMerchantDetail.getProductMerchant().getSubsCategoryMerchant().getSubscategoryName());

                                responseProd.setBrand(brand);
                                responseProd.setCategory(category);
                                responseProd.setSubCategory(subCategory);
                                responseProd.setSubsCategory(subsCategory);
                                responseProd.setMerchantId(productMerchantDetail.getProductMerchant().getMerchant().id);

                                responses.add(responseProd);
                            }
                        }
                        response.setBaseResponse(
                                responses.size(), offset, limit,
                                success + " menampilkan data", responses
                        );
                    } else if (store == null) {
                        response.setBaseResponse(0, 0, 0, "Store tidak ditemukan.", null);
                        return notFound(Json.toJson(response));
                    }
                } else if (merchant == null) {
                    response.setBaseResponse(0, 0, 0, "Merchant tidak ditemukan.", null);
                    return notFound(Json.toJson(response));
                }
            } catch (Exception e) {
                logger.error("Error saat menampilkan produk", e);
                e.printStackTrace();
            }

        } else {
            response.setBaseResponse(0, 0, 0, notFound, null);
            return notFound(Json.toJson(response));
        }

        if (response.getData() != null) {
            return ok(Json.toJson(response));
        } else {
            return notFound(Json.toJson(response));
        }
    }

    @ApiOperation(value = "Get all product store.", notes = "Returns list of product store.\n" + swaggerInfo
            + "", response = Product.class, responseContainer = "List", httpMethod = "GET")
    public static Result listProductStoreByCategoriesForKIOSK(Long merchantId, Long storeId, Long categoryId, Long subCategoryId, Long subsCategoryId, String keyword, String filter, String sort, int offset, int limit) {
        if (!merchantId.equals(0L) && !storeId.equals(0L)) {
            try {
                Merchant merchant = Merchant.find.byId(merchantId);

                if (merchant != null) {
                    Store store = Store.find.byId(storeId);

                    if (store != null) {
                        String searchQuery = keyword != null && keyword.length() > 0 ? " and lower(pm.product_name) like '%"+keyword+"%'" : "";
                        String querySql = "t0.product_merchant_id in (select pm.id from product_merchant pm where pm.merchant_id = "+merchantId+" and pm.is_active = "+true+" and pm.is_deleted = false)";
                        if (!categoryId.equals(0L) && !subCategoryId.equals(0L) && !subsCategoryId.equals(0L)) {
                            querySql = "t0.product_merchant_id in (select pm.id from product_merchant pm where pm.merchant_id = "+merchantId+" and pm.is_active = "+true
                                    +" and pm.is_deleted = false and pm.category_merchant_id = "+categoryId+" and pm.sub_category_merchant_id = "+subCategoryId
                                    +" and pm.subs_category_merchant_id = "+subsCategoryId+searchQuery+")";
                        } else if (!categoryId.equals(0L) && !subCategoryId.equals(0L) && subsCategoryId.equals(0L)) {
                            querySql = "t0.product_merchant_id in (select pm.id from product_merchant pm where pm.merchant_id = "+merchantId+" and pm.is_active = "+true
                                    +" and pm.is_deleted = false and pm.category_merchant_id = "+categoryId+" and pm.sub_category_merchant_id = "+subCategoryId
                                    +searchQuery+")";
                        } else if (!categoryId.equals(0L) && subCategoryId.equals(0L) && !subsCategoryId.equals(0L)) {
                            querySql = "t0.product_merchant_id in (select pm.id from product_merchant pm where pm.merchant_id = "+merchantId+" and pm.is_active = "+true
                                    +" and pm.is_deleted = false and pm.category_merchant_id = "+categoryId
                                    +" and pm.subs_category_merchant_id = "+subsCategoryId+searchQuery+")";
                        } else if (categoryId.equals(0L) && !subCategoryId.equals(0L) && !subsCategoryId.equals(0L)) {
                            querySql = "t0.product_merchant_id in (select pm.id from product_merchant pm where pm.merchant_id = "+merchantId+" and pm.is_active = "+true
                                    +" and pm.is_deleted = false and pm.sub_category_merchant_id = "+subCategoryId
                                    +" and pm.subs_category_merchant_id = "+subsCategoryId+searchQuery+")";
                        } else if (categoryId.equals(0L) && subCategoryId.equals(0L) && !subsCategoryId.equals(0L)) {
                            querySql = "t0.product_merchant_id in (select pm.id from product_merchant pm where pm.merchant_id = "+merchantId+" and pm.is_active = "+true
                                    +" and pm.is_deleted = false and pm.subs_category_merchant_id = "+subsCategoryId+searchQuery+")";
                        } else if (categoryId.equals(0L) && !subCategoryId.equals(0L) && subsCategoryId.equals(0L)) {
                            querySql = "t0.product_merchant_id in (select pm.id from product_merchant pm where pm.merchant_id = "+merchantId+" and pm.is_active = "+true
                                    +" and pm.is_deleted = false and pm.sub_category_merchant_id = "+subCategoryId+searchQuery+")";
                        } else if (!categoryId.equals(0L) && subCategoryId.equals(0L) && subsCategoryId.equals(0L)) {
                            querySql = "t0.product_merchant_id in (select pm.id from product_merchant pm where pm.merchant_id = "+merchantId+" and pm.is_active = "+true
                                    +" and pm.is_deleted = false and pm.category_merchant_id = "+categoryId+searchQuery+")";
                        } else {
                            querySql = "t0.product_merchant_id in (select pm.id from product_merchant pm where pm.merchant_id = "+merchantId+" and pm.is_active = "+true+" and pm.is_deleted = false"+searchQuery+")";
                        }
                        Query<ProductMerchantDetail> query = ProductMerchantDetailRepository.find.where().raw(querySql).eq("t0.is_deleted", false).eq("t0.product_type", "MAIN").order("t0.id desc");
                        List<ProductMerchantDetail> dataProductDetail = ProductMerchantDetailRepository.getDataByPagination(query, offset, limit);

                        List<ProductSpecificStoreResponse> responses = new ArrayList<>();
                        for (ProductMerchantDetail productMerchantDetail : dataProductDetail) {
                            ProductSpecificStoreResponse responseProd = new ProductSpecificStoreResponse();
                            List<ProductStore> listProductStore = ProductStoreRepository.find.where().eq("t0.is_deleted", false).eq("t0.is_active", true).eq("t0.product_id", productMerchantDetail.getProductMerchant().id).orderBy().desc("t0.id").findList();
                            if (listProductStore.size() > 0) {
                                for (ProductStore productStore : listProductStore) {
                                    if (productStore.getStore().id.equals(store.id)) {
                                        responseProd.setProductId(productMerchantDetail.getProductMerchant().id);
                                        responseProd.setProductName(productMerchantDetail.getProductMerchant().getProductName());
                                        responseProd.setIsActive(productMerchantDetail.getProductMerchant().getIsActive());
                                        ProductDetailResponse productDetailResponse = ProductDetailResponse.builder()
                                                .productType(productMerchantDetail.getProductType())
                                                .isCustomizable(productMerchantDetail.getIsCustomizable())
                                                .productPrice(productStore.getStorePrice())
                                                .discountType(productStore.getDiscountType())
                                                .discount(productStore.getDiscount())
                                                .productPriceAfterDiscount(productStore.getFinalPrice())
                                                .productImageMain(productMerchantDetail.getProductImageMain())
                                                .productImage1(productMerchantDetail.getProductImage1())
                                                .productImage2(productMerchantDetail.getProductImage2())
                                                .productImage3(productMerchantDetail.getProductImage3())
                                                .productImage4(productMerchantDetail.getProductImage4())
                                                .build();
                                        responseProd.setProductDetail(productDetailResponse);
                                        ProductSpecificStoreResponse.Brand brand =  new ProductSpecificStoreResponse.Brand();
                                        brand.setBrandId(productMerchantDetail.getProductMerchant().getBrandMerchant().id);
                                        brand.setBrandName(productMerchantDetail.getProductMerchant().getBrandMerchant().getBrandName());

                                        ProductSpecificStoreResponse.Category category = new ProductSpecificStoreResponse.Category();
                                        category.setCategoryId(productMerchantDetail.getProductMerchant().getCategoryMerchant().id);
                                        category.setCategoryName(productMerchantDetail.getProductMerchant().getCategoryMerchant().getCategoryName());

                                        ProductSpecificStoreResponse.SubCategory subCategory = new ProductSpecificStoreResponse.SubCategory();
                                        subCategory.setSubCategoryId(productMerchantDetail.getProductMerchant().getSubCategoryMerchant().id);
                                        subCategory.setSubCategoryName(productMerchantDetail.getProductMerchant().getSubCategoryMerchant().getSubcategoryName());

                                        ProductSpecificStoreResponse.SubsCategory subsCategory = new ProductSpecificStoreResponse.SubsCategory();
                                        subsCategory.setSubsCategoryId(productMerchantDetail.getProductMerchant().getSubsCategoryMerchant().id);
                                        subsCategory.setSubsCategoryName(productMerchantDetail.getProductMerchant().getSubsCategoryMerchant().getSubscategoryName());

                                        responseProd.setBrand(brand);
                                        responseProd.setCategory(category);
                                        responseProd.setSubCategory(subCategory);
                                        responseProd.setSubsCategory(subsCategory);
                                        responseProd.setMerchantId(productMerchantDetail.getProductMerchant().getMerchant().id);
                                        ProductSpecificStoreResponse.ProductStore responseStore = new  ProductSpecificStoreResponse.ProductStore();
                                        responseStore.setId(productStore.id);
                                        responseStore.setStoreId(productStore.getStore().id);
                                        responseStore.setProductId(productStore.getProductMerchant().id);
                                        responseStore.setIsActive(productStore.isActive);
                                        responseStore.setStorePrice(productStore.getStorePrice());
                                        responseStore.setDiscountType(productStore.getDiscountType());
                                        responseStore.setDiscount(productStore.getDiscount());
                                        responseStore.setIsDeleted(productStore.isDeleted);
                                        responseStore.setFinalPrice(productStore.getFinalPrice());

                                        responseStore.setStoresName(store.storeName);

                                        responseProd.setProductStore(responseStore != null ? responseStore : null);

                                        responses.add(responseProd);
                                    }
                                }
                            } else {
                                responseProd.setProductId(productMerchantDetail.getProductMerchant().id);
                                responseProd.setProductName(productMerchantDetail.getProductMerchant().getProductName());
                                responseProd.setIsActive(productMerchantDetail.getProductMerchant().getIsActive());
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
                                responseProd.setProductDetail(productDetailResponse);
                                ProductSpecificStoreResponse.Brand brand =  new ProductSpecificStoreResponse.Brand();
                                brand.setBrandId(productMerchantDetail.getProductMerchant().getBrandMerchant().id);
                                brand.setBrandName(productMerchantDetail.getProductMerchant().getBrandMerchant().getBrandName());

                                ProductSpecificStoreResponse.Category category = new ProductSpecificStoreResponse.Category();
                                category.setCategoryId(productMerchantDetail.getProductMerchant().getCategoryMerchant().id);
                                category.setCategoryName(productMerchantDetail.getProductMerchant().getCategoryMerchant().getCategoryName());

                                ProductSpecificStoreResponse.SubCategory subCategory = new ProductSpecificStoreResponse.SubCategory();
                                subCategory.setSubCategoryId(productMerchantDetail.getProductMerchant().getSubCategoryMerchant().id);
                                subCategory.setSubCategoryName(productMerchantDetail.getProductMerchant().getSubCategoryMerchant().getSubcategoryName());

                                ProductSpecificStoreResponse.SubsCategory subsCategory = new ProductSpecificStoreResponse.SubsCategory();
                                subsCategory.setSubsCategoryId(productMerchantDetail.getProductMerchant().getSubsCategoryMerchant().id);
                                subsCategory.setSubsCategoryName(productMerchantDetail.getProductMerchant().getSubsCategoryMerchant().getSubscategoryName());

                                responseProd.setBrand(brand);
                                responseProd.setCategory(category);
                                responseProd.setSubCategory(subCategory);
                                responseProd.setSubsCategory(subsCategory);
                                responseProd.setMerchantId(productMerchantDetail.getProductMerchant().getMerchant().id);

                                responses.add(responseProd);
                            }
                        }
                        response.setBaseResponse(
                                responses.size(), offset, limit,
                                success + " menampilkan data", responses
                        );
                    } else if (store == null) {
                        response.setBaseResponse(0, 0, 0, "Store tidak ditemukan.", null);
                        return notFound(Json.toJson(response));
                    }
                } else if (merchant == null) {
                    response.setBaseResponse(0, 0, 0, "Merchant tidak ditemukan.", null);
                    return notFound(Json.toJson(response));
                }
            } catch (Exception e) {
                logger.error("Error saat menampilkan produk", e);
                e.printStackTrace();
            }

        } else {
            response.setBaseResponse(0, 0, 0, notFound, null);
            return notFound(Json.toJson(response));
        }

        if (response.getData() != null) {
            return ok(Json.toJson(response));
        } else {
            return notFound(Json.toJson(response));
        }
    }

    @ApiOperation(value = "Get all product store.", notes = "Returns list of product store.\n" + swaggerInfo
            + "", response = Product.class, responseContainer = "List", httpMethod = "GET")
    public static Result listProductStoreByCategoriesForMobileQR(Long merchantId, Long storeId, Long categoryId, Long subCategoryId, Long subsCategoryId, String keyword, String filter, String sort, int offset, int limit) {
        if (!merchantId.equals(0L) && !storeId.equals(0L)) {
            try {
                Merchant merchant = Merchant.find.byId(merchantId);

                if (merchant != null) {
                    Store store = Store.find.byId(storeId);

                    if (store != null) {
                        String searchQuery = keyword != null && keyword.length() > 0 ? " and lower(pm.product_name) like '%"+keyword+"%'" : "";
                        String querySql = "t0.product_merchant_id in (select pm.id from product_merchant pm where pm.merchant_id = "+merchantId+" and pm.is_active = "+true+" and pm.is_deleted = false)";
                        if (!categoryId.equals(0L) && !subCategoryId.equals(0L) && !subsCategoryId.equals(0L)) {
                            querySql = "t0.product_merchant_id in (select pm.id from product_merchant pm where pm.merchant_id = "+merchantId+" and pm.is_active = "+true
                                    +" and pm.is_deleted = false and pm.category_merchant_id = "+categoryId+" and pm.sub_category_merchant_id = "+subCategoryId
                                    +" and pm.subs_category_merchant_id = "+subsCategoryId+searchQuery+")";
                        } else if (!categoryId.equals(0L) && !subCategoryId.equals(0L) && subsCategoryId.equals(0L)) {
                            querySql = "t0.product_merchant_id in (select pm.id from product_merchant pm where pm.merchant_id = "+merchantId+" and pm.is_active = "+true
                                    +" and pm.is_deleted = false and pm.category_merchant_id = "+categoryId+" and pm.sub_category_merchant_id = "+subCategoryId
                                    +searchQuery+")";
                        } else if (!categoryId.equals(0L) && subCategoryId.equals(0L) && !subsCategoryId.equals(0L)) {
                            querySql = "t0.product_merchant_id in (select pm.id from product_merchant pm where pm.merchant_id = "+merchantId+" and pm.is_active = "+true
                                    +" and pm.is_deleted = false and pm.category_merchant_id = "+categoryId
                                    +" and pm.subs_category_merchant_id = "+subsCategoryId+searchQuery+")";
                        } else if (categoryId.equals(0L) && !subCategoryId.equals(0L) && !subsCategoryId.equals(0L)) {
                            querySql = "t0.product_merchant_id in (select pm.id from product_merchant pm where pm.merchant_id = "+merchantId+" and pm.is_active = "+true
                                    +" and pm.is_deleted = false and pm.sub_category_merchant_id = "+subCategoryId
                                    +" and pm.subs_category_merchant_id = "+subsCategoryId+searchQuery+")";
                        } else if (categoryId.equals(0L) && subCategoryId.equals(0L) && !subsCategoryId.equals(0L)) {
                            querySql = "t0.product_merchant_id in (select pm.id from product_merchant pm where pm.merchant_id = "+merchantId+" and pm.is_active = "+true
                                    +" and pm.is_deleted = false and pm.subs_category_merchant_id = "+subsCategoryId+searchQuery+")";
                        } else if (categoryId.equals(0L) && !subCategoryId.equals(0L) && subsCategoryId.equals(0L)) {
                            querySql = "t0.product_merchant_id in (select pm.id from product_merchant pm where pm.merchant_id = "+merchantId+" and pm.is_active = "+true
                                    +" and pm.is_deleted = false and pm.sub_category_merchant_id = "+subCategoryId+searchQuery+")";
                        } else if (!categoryId.equals(0L) && subCategoryId.equals(0L) && subsCategoryId.equals(0L)) {
                            querySql = "t0.product_merchant_id in (select pm.id from product_merchant pm where pm.merchant_id = "+merchantId+" and pm.is_active = "+true
                                    +" and pm.is_deleted = false and pm.category_merchant_id = "+categoryId+searchQuery+")";
                        } else {
                            querySql = "t0.product_merchant_id in (select pm.id from product_merchant pm where pm.merchant_id = "+merchantId+" and pm.is_active = "+true+" and pm.is_deleted = false"+searchQuery+")";
                        }
                        Query<ProductMerchantDetail> query = ProductMerchantDetailRepository.find.where().raw(querySql).eq("t0.is_deleted", false).eq("t0.product_type", "MAIN").order("t0.id desc");
                        List<ProductMerchantDetail> dataProductDetail = ProductMerchantDetailRepository.getDataByPagination(query, offset, limit);

                        List<ProductSpecificStoreResponse> responses = new ArrayList<>();
                        for (ProductMerchantDetail productMerchantDetail : dataProductDetail) {
                            ProductSpecificStoreResponse responseProd = new ProductSpecificStoreResponse();
                            List<ProductStore> listProductStore = ProductStoreRepository.find.where().eq("t0.is_deleted", false).eq("t0.is_active", true).eq("t0.product_id", productMerchantDetail.getProductMerchant().id).orderBy().desc("t0.id").findList();
                            if (listProductStore.size() > 0) {
                                for (ProductStore productStore : listProductStore) {
                                    if (productStore.getStore().id.equals(store.id)) {
                                        responseProd.setProductId(productMerchantDetail.getProductMerchant().id);
                                        responseProd.setProductName(productMerchantDetail.getProductMerchant().getProductName());
                                        responseProd.setIsActive(productMerchantDetail.getProductMerchant().getIsActive());
                                        ProductDetailResponse productDetailResponse = ProductDetailResponse.builder()
                                                .productType(productMerchantDetail.getProductType())
                                                .isCustomizable(productMerchantDetail.getIsCustomizable())
                                                .productPrice(productStore.getStorePrice())
                                                .discountType(productStore.getDiscountType())
                                                .discount(productStore.getDiscount())
                                                .productPriceAfterDiscount(productStore.getFinalPrice())
                                                .productImageMain(productMerchantDetail.getProductImageMain())
                                                .productImage1(productMerchantDetail.getProductImage1())
                                                .productImage2(productMerchantDetail.getProductImage2())
                                                .productImage3(productMerchantDetail.getProductImage3())
                                                .productImage4(productMerchantDetail.getProductImage4())
                                                .build();
                                        responseProd.setProductDetail(productDetailResponse);
                                        ProductSpecificStoreResponse.Brand brand =  new ProductSpecificStoreResponse.Brand();
                                        brand.setBrandId(productMerchantDetail.getProductMerchant().getBrandMerchant().id);
                                        brand.setBrandName(productMerchantDetail.getProductMerchant().getBrandMerchant().getBrandName());

                                        ProductSpecificStoreResponse.Category category = new ProductSpecificStoreResponse.Category();
                                        category.setCategoryId(productMerchantDetail.getProductMerchant().getCategoryMerchant().id);
                                        category.setCategoryName(productMerchantDetail.getProductMerchant().getCategoryMerchant().getCategoryName());

                                        ProductSpecificStoreResponse.SubCategory subCategory = new ProductSpecificStoreResponse.SubCategory();
                                        subCategory.setSubCategoryId(productMerchantDetail.getProductMerchant().getSubCategoryMerchant().id);
                                        subCategory.setSubCategoryName(productMerchantDetail.getProductMerchant().getSubCategoryMerchant().getSubcategoryName());

                                        ProductSpecificStoreResponse.SubsCategory subsCategory = new ProductSpecificStoreResponse.SubsCategory();
                                        subsCategory.setSubsCategoryId(productMerchantDetail.getProductMerchant().getSubsCategoryMerchant().id);
                                        subsCategory.setSubsCategoryName(productMerchantDetail.getProductMerchant().getSubsCategoryMerchant().getSubscategoryName());

                                        responseProd.setBrand(brand);
                                        responseProd.setCategory(category);
                                        responseProd.setSubCategory(subCategory);
                                        responseProd.setSubsCategory(subsCategory);
                                        responseProd.setMerchantId(productMerchantDetail.getProductMerchant().getMerchant().id);
                                        ProductSpecificStoreResponse.ProductStore responseStore = new  ProductSpecificStoreResponse.ProductStore();
                                        responseStore.setId(productStore.id);
                                        responseStore.setStoreId(productStore.getStore().id);
                                        responseStore.setProductId(productStore.getProductMerchant().id);
                                        responseStore.setIsActive(productStore.isActive);
                                        responseStore.setStorePrice(productStore.getStorePrice());
                                        responseStore.setDiscountType(productStore.getDiscountType());
                                        responseStore.setDiscount(productStore.getDiscount());
                                        responseStore.setIsDeleted(productStore.isDeleted);
                                        responseStore.setFinalPrice(productStore.getFinalPrice());

                                        responseStore.setStoresName(store.storeName);

                                        responseProd.setProductStore(responseStore != null ? responseStore : null);

                                        responses.add(responseProd);
                                    }
                                }
                            } else {
                                responseProd.setProductId(productMerchantDetail.getProductMerchant().id);
                                responseProd.setProductName(productMerchantDetail.getProductMerchant().getProductName());
                                responseProd.setIsActive(productMerchantDetail.getProductMerchant().getIsActive());
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
                                responseProd.setProductDetail(productDetailResponse);
                                ProductSpecificStoreResponse.Brand brand =  new ProductSpecificStoreResponse.Brand();
                                brand.setBrandId(productMerchantDetail.getProductMerchant().getBrandMerchant().id);
                                brand.setBrandName(productMerchantDetail.getProductMerchant().getBrandMerchant().getBrandName());

                                ProductSpecificStoreResponse.Category category = new ProductSpecificStoreResponse.Category();
                                category.setCategoryId(productMerchantDetail.getProductMerchant().getCategoryMerchant().id);
                                category.setCategoryName(productMerchantDetail.getProductMerchant().getCategoryMerchant().getCategoryName());

                                ProductSpecificStoreResponse.SubCategory subCategory = new ProductSpecificStoreResponse.SubCategory();
                                subCategory.setSubCategoryId(productMerchantDetail.getProductMerchant().getSubCategoryMerchant().id);
                                subCategory.setSubCategoryName(productMerchantDetail.getProductMerchant().getSubCategoryMerchant().getSubcategoryName());

                                ProductSpecificStoreResponse.SubsCategory subsCategory = new ProductSpecificStoreResponse.SubsCategory();
                                subsCategory.setSubsCategoryId(productMerchantDetail.getProductMerchant().getSubsCategoryMerchant().id);
                                subsCategory.setSubsCategoryName(productMerchantDetail.getProductMerchant().getSubsCategoryMerchant().getSubscategoryName());

                                responseProd.setBrand(brand);
                                responseProd.setCategory(category);
                                responseProd.setSubCategory(subCategory);
                                responseProd.setSubsCategory(subsCategory);
                                responseProd.setMerchantId(productMerchantDetail.getProductMerchant().getMerchant().id);

                                responses.add(responseProd);
                            }
                        }
                        response.setBaseResponse(
                                responses.size(), offset, limit,
                                success + " menampilkan data", responses
                        );
                    } else if (store == null) {
                        response.setBaseResponse(0, 0, 0, "Store tidak ditemukan.", null);
                        return notFound(Json.toJson(response));
                    }
                } else if (merchant == null) {
                    response.setBaseResponse(0, 0, 0, "Merchant tidak ditemukan.", null);
                    return notFound(Json.toJson(response));
                }
            } catch (Exception e) {
                logger.error("Error saat menampilkan produk", e);
                e.printStackTrace();
            }

        } else {
            response.setBaseResponse(0, 0, 0, notFound, null);
            return notFound(Json.toJson(response));
        }

        if (response.getData() != null) {
            return ok(Json.toJson(response));
        } else {
            return notFound(Json.toJson(response));
        }
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
                        ProductStore productStore = ProductStoreRepository.findByIdAndMerchantId(id, ownMerchant);
                        if (productStore == null) {
                            response.setBaseResponse(0, 0, 0, error + " product store tidak tersedia.", null);
                            return badRequest(Json.toJson(response));
                        }
                        ProductMerchant productMerchant = ProductMerchantRepository.findById(request.getProductId(),
                                ownMerchant);
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
                        productStore.setProductStoreQrCode(Constant.getInstance().getFrontEndUrl().concat(store.storeCode+"/"+store.id+"/"+ownMerchant.id+"/product/"+productMerchant.id+"/detail"));
                        if (request.getDiscountType() != null) {
                            productStore.setDiscountType(request.getDiscountType());
                        }
                        if (request.getDiscount() != null) {
                            productStore.setDiscount(request.getDiscount());
                        }
                        if (request.getFinalPrice() != null) {
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
                    ProductStore productStore = ProductStoreRepository.findByIdAndMerchantId(id, ownMerchant);
                    if (productStore == null) {
                        response.setBaseResponse(0, 0, 0, error + " produk store tidak tersedia.", null);
                        return badRequest(Json.toJson(response));
                    }

                    productStore.isDeleted = true;
                    productStore.update();
                    trx.commit();

                    response.setBaseResponse(1, offset, 1, success + " menghapus produk store", productStore);
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
                    ProductResponseStore responseProduct = new ProductResponseStore();
                    Query<ProductStore> queryPS = ProductStoreRepository.find.where()
                            .eq("t0.product_id", productMerchants.id).eq("t0.is_deleted", false)
                            .eq("merchant", ownMerchant).order("t0.id");
                    List<ProductStore> dataPS = ProductStoreRepository.getDataProductStore(queryPS);
                    List<ProductResponseStore.ProductStore> responsesProductStore = new ArrayList<>();
                    responseProduct.setProductId(productMerchants.id);
                    responseProduct.setProductName(productMerchants.getProductName());
                    responseProduct.setIsActive(productMerchants.getIsActive());
                    // ================================================================ //

                    ProductMerchantDetail productMerchantDetail = ProductMerchantDetailRepository
                            .findByProduct(productMerchants);
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

                    responseProduct.setMerchantId(productMerchants.getMerchant().id);

                    for (ProductStore dataPStore : dataPS) {
                        ProductResponseStore.ProductStore responsePStore = new ProductResponseStore.ProductStore();

                        responsePStore.setId(dataPStore.id);
                        responsePStore.setStoreId(dataPStore.getStore().id);
                        responsePStore.setProductId(dataPStore.getProductMerchant().id);
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

    public static Result viewProductStoreV2(Long id, String storeCode) {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            if(storeCode != "" && storeCode != null) {
                Store dataStore = Store.findByStoreCode(storeCode);
                if (dataStore != null) {
                    ProductStore prStore = ProductStoreRepository.find.where().eq("t0.store_id", dataStore.id).eq("t0.product_id", id).eq("t0.is_deleted", false).findUnique();
                    if(prStore == null) {
                        response.setBaseResponse(0, 0, 0, "Tidak bisa mengubah data Product Non Toko", null);
                        return forbidden(Json.toJson(response));
                    }
                }
            }
            if (id != null) {
                Transaction trx = Ebean.beginTransaction();
                try {
                    ProductMerchant productMerchants = ProductMerchantRepository.findById(id, ownMerchant);
                    ProductResponseStore responseProduct = new ProductResponseStore();
                    Query<ProductStore> queryPS = ProductStoreRepository.find.where()
                            .eq("t0.product_id", productMerchants.id).eq("t0.is_deleted", false)
                            .eq("merchant", ownMerchant).order("t0.id");
                    List<ProductStore> dataPS = ProductStoreRepository.getDataProductStore(queryPS);
                    List<ProductResponseStore.ProductStore> responsesProductStore = new ArrayList<>();
                    responseProduct.setProductId(productMerchants.id);
                    responseProduct.setProductName(productMerchants.getProductName());
                    responseProduct.setIsActive(productMerchants.getIsActive());
                    // ================================================================ //

                    ProductMerchantDetail productMerchantDetail = ProductMerchantDetailRepository
                            .findByProduct(productMerchants);
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

                    responseProduct.setMerchantId(productMerchants.getMerchant().id);

                    for (ProductStore dataPStore : dataPS) {
                        ProductResponseStore.ProductStore responsePStore = new ProductResponseStore.ProductStore();

                        responsePStore.setId(dataPStore.id);
                        responsePStore.setStoreId(dataPStore.getStore().id);
                        responsePStore.setProductId(dataPStore.getProductMerchant().id);
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
                    ProductStore productStore = ProductStoreRepository.findByIdAndMerchantId(id, ownMerchant);
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
