package controllers.product;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Expr;
import com.avaje.ebean.Query;
import com.avaje.ebean.Transaction;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hokeba.api.BaseResponse;
import com.hokeba.util.Constant;
import controllers.BaseController;
import dtos.product.*;
import models.*;
import models.merchant.ProductMerchant;
import models.merchant.ProductMerchantDescription;
import models.merchant.ProductMerchantDetail;
import models.productaddon.*;
import play.Logger;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import repository.*;
import service.ProductImportService;
import service.DownloadOrderReport;
import validator.ProductValidator;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;

import com.avaje.ebean.SqlQuery;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProductMerchantController extends BaseController {

    private final static Logger.ALogger logger = Logger.of(ProductMerchantController.class);

    private static BaseResponse response = new BaseResponse();

    private static final ObjectMapper objectMapper = new ObjectMapper();

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

                //create main product merchant
                ProductMerchant newProductMerchant = new ProductMerchant();
                newProductMerchant.setNoSKU(productRequest.getNoSKU());
                newProductMerchant.setProductName(productRequest.getProductName());
                newProductMerchant.setIsActive(Boolean.TRUE);
                newProductMerchant.setCategoryMerchant(categoryMerchant);
                newProductMerchant.setSubCategoryMerchant(subCategoryMerchant);
                newProductMerchant.setSubsCategoryMerchant(subsCategoryMerchant);
                newProductMerchant.setBrandMerchant(brandMerchant);
                newProductMerchant.setMerchant(ownMerchant);
//                constructProductEntityRequest(newProductMerchant, ownMerchant, productRequest, categoryMerchant,
//                        subCategoryMerchant, subsCategoryMerchant, brandMerchant);
                newProductMerchant.save();

                // create product merchant detail
                ProductMerchantDetail newProductMerchantDetail = new ProductMerchantDetail();
                newProductMerchantDetail.setProductType(productRequest.getProductDetailRequest().getProductType());
                newProductMerchantDetail.setIsCustomizable(productRequest.getProductDetailRequest().getIsCustomizable());
                newProductMerchantDetail.setProductPrice(productRequest.getProductDetailRequest().getProductPrice());
                newProductMerchantDetail.setDiscountType(productRequest.getProductDetailRequest().getDiscountType());
                newProductMerchantDetail.setDiscount(productRequest.getProductDetailRequest().getDiscount() != null ? productRequest.getProductDetailRequest().getDiscount() : 0D);
                newProductMerchantDetail.setProductPriceAfterDiscount(productRequest.getProductDetailRequest().getProductPriceAfterDiscount() != null ? productRequest.getProductDetailRequest().getProductPriceAfterDiscount() : productRequest.getProductDetailRequest().getProductPrice());
                newProductMerchantDetail.setProductImageMain(productRequest.getProductDetailRequest().getProductImageMain());
                newProductMerchantDetail.setProductImage1(productRequest.getProductDetailRequest().getProductImage1());
                newProductMerchantDetail.setProductImage2(productRequest.getProductDetailRequest().getProductImage2());
                newProductMerchantDetail.setProductImage3(productRequest.getProductDetailRequest().getProductImage3());
                newProductMerchantDetail.setProductImage4(productRequest.getProductDetailRequest().getProductImage4());
                newProductMerchantDetail.setProductMerchant(newProductMerchant);
                newProductMerchantDetail.setProductMerchantQrCode(Constant.getInstance().getFrontEndUrl().concat("product/"+newProductMerchant.id+"/detail"));
//                constructProductDetailEntityRequest(newProductMerchantDetail, newProductMerchant, productRequest);
                newProductMerchantDetail.save();

                //create product merchant description
                ProductMerchantDescription newProductMerchantDescription = new ProductMerchantDescription();
                newProductMerchantDescription.setShortDescription(productRequest.getProductDescriptionRequest().getShortDescription());
                newProductMerchantDescription.setLongDescription(productRequest.getProductDescriptionRequest().getLongDescription());
                newProductMerchantDescription.setProductMerchantDetail(newProductMerchantDetail);
                newProductMerchantDescription.save();
                
                //create product store (if any)
                for (ProductStoreResponse productStoreRequest : productRequest.getProductStoreRequests()) {
                    Store store = Store.findById(productStoreRequest.getStoreId());
                    if (store == null) {
                        response.setBaseResponse(0, 0, 0, " Store not found.", null);
                        return badRequest(Json.toJson(response));
                    }
                    ProductStore psQuery = ProductStoreRepository.find.where().eq("productMerchant", newProductMerchant)
                            .eq("store", store).eq("t0.is_deleted", false).findUnique();
                    if (psQuery != null) {
                        response.setBaseResponse(0, 0, 0,
                                "Tidak dapat menambahkan " + newProductMerchant.getProductName() + " ke toko yang sama.",
                                null);
                        return badRequest(Json.toJson(response));
                    }
                    String validate = validateCreateProductStore(productStoreRequest);
                    if (validate != null) {
                    	
                    }
                    ProductStore newProductStore = new ProductStore();
                    newProductStore.setStore(store);
                    newProductStore.setProductMerchant(newProductMerchant);
                    newProductStore.setMerchant(ownMerchant);
                    newProductStore.setActive(productStoreRequest.getIsActive());
                    newProductStore.setStorePrice(productStoreRequest.getStorePrice());
                    newProductStore.setProductStoreQrCode(Constant.getInstance().getFrontEndUrl().concat(store.storeCode+"/"+store.id+"/"+ownMerchant.id+"/product/"+newProductMerchant.id+"/detail"));
                    if (productStoreRequest.getDiscountType() != null) {
                        newProductStore.setDiscountType(productStoreRequest.getDiscountType());
                    }
                    if (productStoreRequest.getDiscount() != null) {
                        newProductStore.setDiscount(productStoreRequest.getDiscount());
                    }
                    if (productStoreRequest.getFinalPrice() != null) {
                        newProductStore.setFinalPrice(productStoreRequest.getFinalPrice());
                    }
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
                    ProductMerchant newProductMerchant = new ProductMerchant();
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
                // Query<ProductMerchant> query = ProductMerchantRepository.findProductIsActiveAndMerchant(ownMerchant, isActive);
                Query<ProductMerchant> query = ProductMerchantRepository.find.where()
                                                .eq("isDeleted", false)
                                                .eq("isActive", isActive)
                                                .eq("merchant", ownMerchant)
                                                .or(
                                                Expr.ilike("t0.product_name", "%" + filter + "%"),
                                                Expr.ilike("t0.no_sku", "%" + filter + "%")).order("id");
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

    public static Result importProduct () {
    	Merchant merchant = checkMerchantAccessAuthorization();
    	if (merchant != null) {
    		Http.MultipartFormData body = request().body().asMultipartFormData();
			Http.MultipartFormData.FilePart file = body.getFile("import");
			if(file == null) {
				response.setBaseResponse(0, 0, 0, "File Is Null", null);
				return badRequest(Json.toJson(response));
			}
			System.out.println("file - "+file.getFilename());
			ProductImportService productImport = new ProductImportService();
			if(!productImport.importProductMerchant(file, merchant, response)) {
				return badRequest(Json.toJson(response));
			}
			return ok(Json.toJson(response));
    	}
    	response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }
    
    public static Result getImportTemplate() {
    	Merchant merchant = checkMerchantAccessAuthorization();
    	if(merchant != null) {
    		File file = ProductImportService.getImportTemplateMerchant();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            String filename = "ImportProductMerchantTemplate-"+simpleDateFormat.format(new Date()).toString() + ".xlsx";
    		response().setContentType("application/vnd.ms-excel");
			response().setHeader("Content-disposition", "attachment; filename=" + filename);
			return ok(file);
    	}
    	response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }
    
    // =============================================== construct =====================================================//
    private static ProductResponse toResponse(ProductMerchant productMerchant) {
        ProductResponse productResponse = new ProductResponse();
        productResponse.setProductId(productMerchant.id);
        productResponse.setNoSKU(productMerchant.getNoSKU());
        productResponse.setProductName(productMerchant.getProductName());
        productResponse.setIsActive(productMerchant.getIsActive());
        productResponse.setMerchantId(productMerchant.getMerchant().id);
        // ================================================================ //
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
        newProductMerchant.setNoSKU(productRequest.getNoSKU());
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
        newProductMerchantDetail.setDiscount(productRequest.getProductDetailRequest().getDiscount() != null ? productRequest.getProductDetailRequest().getDiscount() : 0D);
        newProductMerchantDetail.setProductPriceAfterDiscount(productRequest.getProductDetailRequest().getProductPriceAfterDiscount() != null ? productRequest.getProductDetailRequest().getProductPriceAfterDiscount() : productRequest.getProductDetailRequest().getProductPrice());
        newProductMerchantDetail.setProductImageMain(productRequest.getProductDetailRequest().getProductImageMain());
        newProductMerchantDetail.setProductImage1(productRequest.getProductDetailRequest().getProductImage1());
        newProductMerchantDetail.setProductImage2(productRequest.getProductDetailRequest().getProductImage2());
        newProductMerchantDetail.setProductImage3(productRequest.getProductDetailRequest().getProductImage3());
        newProductMerchantDetail.setProductImage4(productRequest.getProductDetailRequest().getProductImage4());
        newProductMerchantDetail.setProductMerchant(newProductMerchant);
        newProductMerchantDetail.setProductMerchantQrCode(Constant.getInstance().getFrontEndUrl().concat("product/"+newProductMerchant.id+"/detail"));
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
    public static Result productListKiosk(Long brandId, Long merchantId, Long storeId, Long categoryId, String keyword) {
        if (!brandId.equals(0L)) {
            Transaction trx = Ebean.beginTransaction();
            try {
                Store store = Store.find.byId(storeId);
                if (store == null) {
                    response.setBaseResponse(0, 0, 0, "Store tidak ditemukan.", null);
                    return notFound(Json.toJson(response));
                }

                String querySql;
                String searchQuery = keyword != null && keyword.length() > 0 ? " and lower(pm.product_name) like '%"+keyword+"%'" : "";
                if (categoryId > 0 ) {
                    querySql = "t0.product_merchant_id in (select pm.id from product_merchant pm where pm.merchant_id = "+merchantId+" and pm.subs_category_merchant_id = "+categoryId+" and pm.brand_merchant_id = "+brandId+" and pm.is_active = "+true+" and pm.is_deleted = false"+searchQuery+")";
                } else {
                    querySql = "t0.product_merchant_id in (select pm.id from product_merchant pm where pm.merchant_id = "+merchantId+" and pm.brand_merchant_id = "+brandId+" and pm.is_active = "+true+" and pm.is_deleted = false"+searchQuery+")";
                }
                Query<ProductMerchantDetail> query = ProductMerchantDetailRepository.find.where().raw(querySql).eq("t0.is_deleted", false).eq("t0.product_type", "MAIN").order("t0.id asc");
                List<ProductMerchantDetail> dataProductDetail = ProductMerchantDetailRepository.getAllDataKiosK(query);

                List<ProductSpecificStoreResponse> responses = new ArrayList<>();
                for(ProductMerchantDetail productMerchantDetail : dataProductDetail){
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
                response.setBaseResponse(dataProductDetail.size(), 0, 0, "Berhasil menampilkan produk", responses);
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
        } else {
            Transaction trx = Ebean.beginTransaction();
            try {

                Store store = Store.find.byId(storeId);
                if (store == null) {
                    response.setBaseResponse(0, 0, 0, "Store tidak ditemukan.", null);
                    return notFound(Json.toJson(response));
                }
                String querySql;
                String searchQuery = keyword != null && keyword.length() > 0 ? " and lower(pm.product_name) like '%"+keyword+"%'" : "";
                if (categoryId > 0 ) {
                    querySql = "t0.product_merchant_id in (select pm.id from product_merchant pm where pm.merchant_id = "+merchantId+" and pm.subs_category_merchant_id = "+categoryId+" and pm.is_active = "+true+" and pm.is_deleted = false"+searchQuery+")";
                } else {
                    querySql = "t0.product_merchant_id in (select pm.id from product_merchant pm where pm.merchant_id = "+merchantId+" and pm.is_active = "+true+" and pm.is_deleted = false"+searchQuery+")";
                }
                Query<ProductMerchantDetail> query = ProductMerchantDetailRepository.find.where().raw(querySql).eq("t0.is_deleted", false).eq("t0.product_type", "MAIN").order("t0.id asc");
                List<ProductMerchantDetail> dataProductDetail = ProductMerchantDetailRepository.getAllDataKiosK(query);

                List<ProductSpecificStoreResponse> responses = new ArrayList<>();
                for(ProductMerchantDetail productMerchantDetail : dataProductDetail){
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

                response.setBaseResponse(dataProductDetail.size(), 0, 0, "Berhasil menampilkan produk", responses);
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
//        response.setBaseResponse(0, 0, 0, "Tidak dapat menampilkan produk", null);
//        return badRequest(Json.toJson(response));
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