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
import models.merchant.*;
import models.productaddon.*;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import repository.*;
import validator.ProductValidator;

import java.util.ArrayList;
import java.util.List;

public class ProductAddOnController extends BaseController {

    private final static Logger.ALogger logger = Logger.of(ProductAddOnController.class);

    private static BaseResponse response = new BaseResponse();

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Result assignProductAddOn() {

        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            JsonNode json = request().body().asJson();
            try {
                ProductAddOnRequest productAddOnRequest = objectMapper.readValue(json.toString(), ProductAddOnRequest.class);
                String validateRequest = validateRequest(productAddOnRequest);
                if (validateRequest != null) {
                    response.setBaseResponse(0, 0, 0, validateRequest, null);
                    return badRequest(Json.toJson(response));
                }
                Transaction trx = Ebean.beginTransaction();
                try {
                    ProductMerchant productMerchant = ProductMerchantRepository.findById(productAddOnRequest.getProductId(), ownMerchant);
                    if (productMerchant == null) {
                        response.setBaseResponse(0, 0, 0, " Produk tidak ditemukan.", null);
                        return badRequest(Json.toJson(response));
                    }

                    ProductAddOn newProductAddOn = new ProductAddOn();
                    newProductAddOn.setProductMerchant(productMerchant);
                    newProductAddOn.setProductAssignId(productAddOnRequest.getProductAssignId());
                    newProductAddOn.setProductType(productAddOnRequest.getProductType());
                    newProductAddOn.setIsActive(productAddOnRequest.getIsActive());
                    newProductAddOn.setMerchant(ownMerchant);
                    newProductAddOn.save();

                    trx.commit();

                    response.setBaseResponse(1, 0, 1, "Produk berhasil di assign", newProductAddOn.id);
                    return ok(Json.toJson(response));

                } catch (Exception e) {
                    logger.error("Error pada saat assign produk", e);
                    e.printStackTrace();
                    trx.rollback();
                } finally {
                    trx.end();
                }
            } catch (Exception e) {
                logger.error("Terdapat kesalahan saat parsing json", e);
                e.printStackTrace();
            }
        }

        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result editAssignProductAddOn(Long idAssignedProduct) {

        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            ProductAddOn productAddOn = ProductAddOnRepository.findByIdAndMerchant(idAssignedProduct, ownMerchant.id);
            if (productAddOn != null) {
                JsonNode json = request().body().asJson();
                try {
                    ProductAddOnRequest productAddOnRequest = objectMapper.readValue(json.toString(), ProductAddOnRequest.class);
                    String validateRequest = validateRequest(productAddOnRequest);
                    if (validateRequest != null) {
                        response.setBaseResponse(0, 0, 0, validateRequest, null);
                        return badRequest(Json.toJson(response));
                    }
                    Transaction trx = Ebean.beginTransaction();
                    try {
                        ProductMerchant productMerchant = ProductMerchantRepository.findById(productAddOnRequest.getProductId(), ownMerchant);
                        if (productMerchant == null) {
                            response.setBaseResponse(0, 0, 0, " Produk tidak ditemukan.", null);
                            return badRequest(Json.toJson(response));
                        }

                        productAddOn.setProductMerchant(productMerchant);
                        productAddOn.setProductAssignId(productAddOnRequest.getProductAssignId());
                        productAddOn.setProductType(productAddOnRequest.getProductType());
                        productAddOn.setIsActive(productAddOnRequest.getIsActive());
                        productAddOn.setMerchant(ownMerchant);
                        productAddOn.update();

                        trx.commit();

                        response.setBaseResponse(1, 0, 1, "Produk berhasil di assign", productAddOn.id);
                        return ok(Json.toJson(response));

                    } catch (Exception e) {
                        logger.error("Error pada saat assign produk", e);
                        e.printStackTrace();
                        trx.rollback();
                    } finally {
                        trx.end();
                    }
                } catch (Exception e) {
                    logger.error("Terdapat kesalahan saat parsing json", e);
                    e.printStackTrace();
                }
            } else {
                response.setBaseResponse(0, 0, 0, "Produk assign tidak ditemukan", null);
                return badRequest(Json.toJson(response));
            }
        }

        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result readAssignProductAddOn(Long idAssignedProduct) {

        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            ProductAddOn productAddOn = ProductAddOnRepository.findByIdAndMerchant(idAssignedProduct, ownMerchant.id);
            if (productAddOn != null) {
                try {
                    ProductMerchantAssignResponse responses = new ProductMerchantAssignResponse();
                    
                    ProductMerchantAssignResponse responseData = new ProductMerchantAssignResponse();
                    Query<ProductAddOn> queryProductAddOn = ProductAddOnRepository.find.where().eq("t0.product_id", productAddOn.getProductMerchant().id).eq("t0.is_deleted", false).eq("t0.merchant_id", ownMerchant.id).order("t0.id");
                    List<ProductAddOn> dataAddOn = ProductAddOnRepository.getDataForAddOn(queryProductAddOn);
                    List<ProductMerchantAssignResponse.ProductAddOn> responsesProductAddOn = new ArrayList<>();
                    responseData.setId(productAddOn.id);
                    responseData.setProductId(productAddOn.getProductMerchant().id);
                    ProductMerchant productMerchant = ProductMerchantRepository.findById(productAddOn.getProductMerchant().id, ownMerchant);
                    responseData.setProductName(productMerchant != null ? productMerchant.getProductName() : null);
                    responseData.setMerchantId(productAddOn.getMerchant().id);
                    for(ProductAddOn dataProductAddOn : dataAddOn) {
                        ProductMerchantAssignResponse.ProductAddOn responseAddOn = new ProductMerchantAssignResponse.ProductAddOn();
                        responseAddOn.setProductAssignId(dataProductAddOn.getProductAssignId());
                        ProductMerchant productMerchantAssign = ProductMerchantRepository.findById(dataProductAddOn.getProductAssignId(), ownMerchant);
                        responseAddOn.setProductName(productMerchantAssign.getProductName());
                        responseAddOn.setProductType(dataProductAddOn.getProductType());
                        responsesProductAddOn.add(responseAddOn);
                        responseData.setProductAddOn(responsesProductAddOn);
                    }
                    response.setBaseResponse(1, 0, 1, "Berhasil menampilkan data", responseData);
                    return ok(Json.toJson(response));

                } catch (Exception e) {
                    logger.error("Error pada saat menampilkan assign produk", e);
                    e.printStackTrace();
                }
            } else {
                response.setBaseResponse(0, 0, 0, "Produk assign tidak ditemukan", null);
                return badRequest(Json.toJson(response));
            }
        }

        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result deleteAssignProductAddOn(Long idAssignedProduct) {

        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            ProductAddOn productAddOn = ProductAddOnRepository.findByIdAndMerchant(idAssignedProduct, ownMerchant.id);
            if (productAddOn != null) {
                Transaction trx = Ebean.beginTransaction();
                try {
                        
                    productAddOn.isDeleted = Boolean.TRUE;
                    productAddOn.update();

                    trx.commit();

                    response.setBaseResponse(1, 0, 1, "Produk assign berhasil di hapus", null);
                    return ok(Json.toJson(response));

                } catch (Exception e) {
                    logger.error("Error pada saat menghapus produk assign", e);
                    e.printStackTrace();
                    trx.rollback();
                } finally {
                    trx.end();
                }
            } else {
                response.setBaseResponse(0, 0, 0, "Produk assign tidak ditemukan", null);
                return badRequest(Json.toJson(response));
            }
        }

        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    private static String validateRequest(ProductAddOnRequest productAddOnRequest) {
        if (productAddOnRequest == null)
        return "Bidang tidak boleh nol atau kosong";
        if (productAddOnRequest.getProductId() == null)
            return "Produk tidak boleh nol atau kosong";
        if (productAddOnRequest.getProductAssignId() == null)
            return "Produk yang akan di assign tidak boleh nol atau kosong";
        if (productAddOnRequest.getProductType() == null)
            return "Tipe produk tidak boleh nol atau kosong";

        return null;
    }

    public static Result listProductAssign(String filter, String sort, int offset, int limit) {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            String querySql = "t0.product_merchant_id in (select pm.id from product_merchant pm where pm.merchant_id = "+ownMerchant.id+" and pm.is_active = "+true+" and pm.is_deleted = "+false+")";
            Query<ProductMerchantDetail> query = ProductMerchantDetailRepository.find.where().raw(querySql).eq("t0.is_deleted", false).eq("t0.product_type", "MAIN").order("random()");
            try {
                List<ProductMerchantAssignResponse> responses = new ArrayList<>();
                List<ProductMerchantDetail> totalDataProductDetail = ProductMerchantDetailRepository.getTotalDataPage(query);
                List<ProductMerchantDetail> productMerchantDetails = ProductMerchantDetailRepository.findDetailData(query, sort, filter, offset, limit);
                for(ProductMerchantDetail productMerchantDetail : productMerchantDetails){
                    ProductMerchantAssignResponse responseData = new ProductMerchantAssignResponse();
                    ProductMerchant productMerchant = ProductMerchantRepository.findByIdProductRecommend(productMerchantDetail.getProductMerchant().id, ownMerchant.id);

                    responseData.setId(productMerchant.id);
                    responseData.setProductId(productMerchant.id);
                    responseData.setProductName(productMerchant.getProductName());
                    responseData.setMerchantId(productMerchant.getMerchant().id);
                    
                    Query<ProductAddOn> queryProductAddOn = ProductAddOnRepository.find.where().eq("t0.product_id", productMerchantDetail.getProductMerchant().id).eq("t0.is_deleted", false).eq("t0.merchant_id", ownMerchant.id).order("t0.id");
                    List<ProductAddOn> dataAddOn = ProductAddOnRepository.getDataForAddOn(queryProductAddOn);
                    List<ProductMerchantAssignResponse.ProductAddOn> responsesProductAddOn = new ArrayList<>();
                    
                    for(ProductAddOn dataProductAddOn : dataAddOn) {
                        ProductMerchantAssignResponse.ProductAddOn responseAddOn = new ProductMerchantAssignResponse.ProductAddOn();
                        responseAddOn.setProductAssignId(dataProductAddOn.getProductAssignId());
                        ProductMerchant productMerchantAssign = ProductMerchantRepository.findById(dataProductAddOn.getProductAssignId(), ownMerchant);
                        responseAddOn.setProductName(productMerchantAssign.getProductName());
                        responseAddOn.setProductType(dataProductAddOn.getProductType());
                        responsesProductAddOn.add(responseAddOn);
                        responseData.setProductAddOn(responseAddOn != null ? responsesProductAddOn : null);
                    }
                    responses.add(responseData);
                }
                response.setBaseResponse(filter == null || filter.equals("") ? totalDataProductDetail.size() : productMerchantDetails.size() , offset, limit, success + " menampilkan data", responses);
                return ok(Json.toJson(response));
            } catch (Exception e) {
                Logger.error("allDetail", e);
            }
        } else if (ownMerchant == null) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result detailProductAssign(Long idProduct) {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            String querySql = "t0.product_merchant_id in (select pm.id from product_merchant pm where pm.merchant_id = "+ownMerchant.id+" and pm.is_active = "+true+" and pm.is_deleted = "+false+")";
            Query<ProductMerchantDetail> query = ProductMerchantDetailRepository.find.where().raw(querySql).eq("t0.is_deleted", false).eq("t0.product_type", "MAIN").order("random()");
            try {
                ProductMerchantDetail productMerchantDetail = ProductMerchantDetailRepository.findDetailProduct(idProduct, ownMerchant.id);
                    
                ProductMerchantAssignResponse responseData = new ProductMerchantAssignResponse();
                ProductMerchant productMerchant = ProductMerchantRepository.findByIdProductRecommend(productMerchantDetail.getProductMerchant().id, ownMerchant.id);

                responseData.setId(productMerchant.id);
                responseData.setProductId(productMerchant.id);
                responseData.setProductName(productMerchant.getProductName());
                responseData.setMerchantId(productMerchant.getMerchant().id);
                    
                Query<ProductAddOn> queryProductAddOn = ProductAddOnRepository.find.where().eq("t0.product_id", productMerchantDetail.getProductMerchant().id).eq("t0.is_deleted", false).eq("t0.merchant_id", ownMerchant.id).order("t0.id");
                List<ProductAddOn> dataAddOn = ProductAddOnRepository.getDataForAddOn(queryProductAddOn);
                List<ProductMerchantAssignResponse.ProductAddOn> responsesProductAddOn = new ArrayList<>();
                    
                for(ProductAddOn dataProductAddOn : dataAddOn) {
                    ProductMerchantAssignResponse.ProductAddOn responseAddOn = new ProductMerchantAssignResponse.ProductAddOn();
                    responseAddOn.setProductAssignId(dataProductAddOn.getProductAssignId());
                    ProductMerchant productMerchantAssign = ProductMerchantRepository.findById(dataProductAddOn.getProductAssignId(), ownMerchant);
                    responseAddOn.setProductName(productMerchantAssign.getProductName());
                    responseAddOn.setProductType(dataProductAddOn.getProductType());
                    responsesProductAddOn.add(responseAddOn);
                    responseData.setProductAddOn(responseAddOn != null ? responsesProductAddOn : null);
                }
                response.setBaseResponse(1, 0, 1, "Berhasil menampilkan data", responseData);
                return ok(Json.toJson(response));
            } catch (Exception e) {
                Logger.error("allDetail", e);
            }
        } else if (ownMerchant == null) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }
}
