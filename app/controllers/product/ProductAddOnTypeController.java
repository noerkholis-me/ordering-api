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
import models.productaddon.*;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import repository.*;
import validator.ProductValidator;

import java.util.ArrayList;
import java.util.List;

public class ProductAddOnTypeController extends BaseController {

    private final static Logger.ALogger logger = Logger.of(ProductAddOnTypeController.class);

    private static BaseResponse response = new BaseResponse();

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Result createProductType() {

        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            JsonNode json = request().body().asJson();
            try {
                ProductAddOnTypeRequest productAddOnTypeRequest = objectMapper.readValue(json.toString(), ProductAddOnTypeRequest.class);
                String validateRequest = validateRequest(productAddOnTypeRequest);
                if (validateRequest != null) {
                    response.setBaseResponse(0, 0, 0, validateRequest, null);
                    return badRequest(Json.toJson(response));
                }
                Transaction trx = Ebean.beginTransaction();
                try {
                    ProductAddOnType productAddOnType = ProductAddOnTypeRepository.findByNameAndMerchant(productAddOnTypeRequest.getProductType(), ownMerchant.id);
                    if (productAddOnType != null) {
                        response.setBaseResponse(0, 0, 0, " Produk type sudah tersedia.", null);
                        return badRequest(Json.toJson(response));
                    }

                    ProductAddOnType newProductAddOnType = new ProductAddOnType();
                    newProductAddOnType.setProductType(productAddOnTypeRequest.getProductType());
                    newProductAddOnType.setIsActive(Boolean.TRUE);
                    newProductAddOnType.setMerchant(ownMerchant);
                    newProductAddOnType.save();

                    trx.commit();

                    response.setBaseResponse(1, 0, 1, "Produk tipe berhasil di tambahkan", newProductAddOnType.id);
                    return ok(Json.toJson(response));

                } catch (Exception e) {
                    logger.error("Error pada saat menambahkan produk tipe", e);
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

    public static Result editProductType(Long idProductTpe) {

        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            ProductAddOnType productAddOnType = ProductAddOnTypeRepository.findByIdAndMerchant(idProductTpe, ownMerchant.id);
            if (productAddOnType != null) {
                JsonNode json = request().body().asJson();
                try {
                    ProductAddOnTypeRequest productAddOnTypeRequest = objectMapper.readValue(json.toString(), ProductAddOnTypeRequest.class);
                    String validateRequest = validateRequest(productAddOnTypeRequest);
                    if (validateRequest != null) {
                        response.setBaseResponse(0, 0, 0, validateRequest, null);
                        return badRequest(Json.toJson(response));
                    }
                    Transaction trx = Ebean.beginTransaction();
                    try {

                        productAddOnType.setProductType(productAddOnTypeRequest.getProductType());
                        productAddOnType.setIsActive(productAddOnTypeRequest.getIsActive());
                        productAddOnType.setMerchant(ownMerchant);
                        productAddOnType.update();

                        trx.commit();

                        response.setBaseResponse(1, 0, 1, "Produk berhasil di assign", productAddOnType.id);
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

    public static Result readProductType(Long idProductTpe) {

        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            ProductAddOnType productAddOnType = ProductAddOnTypeRepository.findByIdAndMerchant(idProductTpe, ownMerchant.id);
            if (productAddOnType != null) {
                try {
                    ProductAddOnTypeResponse responses = new ProductAddOnTypeResponse();
                    
                    responses.setId(productAddOnType.id);
                    responses.setProductType(productAddOnType.getProductType());
                    responses.setMerchantId(productAddOnType.getMerchant().id);
                    responses.setIsActive(productAddOnType.getIsActive());

                    response.setBaseResponse(1, 0, 1, "Berhasil menampilkan data", responses);
                    return ok(Json.toJson(response));

                } catch (Exception e) {
                    logger.error("Error pada saat menampilkan produk tipe", e);
                    e.printStackTrace();
                }
            } else {
                response.setBaseResponse(0, 0, 0, "Produk tipe tidak ditemukan", null);
                return badRequest(Json.toJson(response));
            }
        }

        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result deleteProductType(Long idProductTpe) {

        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            ProductAddOnType productAddOnType = ProductAddOnTypeRepository.findByIdAndMerchant(idProductTpe, ownMerchant.id);
            if (productAddOnType != null) {
                Transaction trx = Ebean.beginTransaction();
                try {
                        
                    productAddOnType.isDeleted = Boolean.TRUE;
                    productAddOnType.update();

                    trx.commit();

                    response.setBaseResponse(1, 0, 1, "Produk tipe berhasil di hapus", null);
                    return ok(Json.toJson(response));

                } catch (Exception e) {
                    logger.error("Error pada saat menghapus produk tipe", e);
                    e.printStackTrace();
                    trx.rollback();
                } finally {
                    trx.end();
                }
            } else {
                response.setBaseResponse(0, 0, 0, "Produk tipe tidak ditemukan", null);
                return badRequest(Json.toJson(response));
            }
        }

        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    private static String validateRequest(ProductAddOnTypeRequest productAddOnTypeRequest) {
        if (productAddOnTypeRequest == null)
        return "Bidang tidak boleh nol atau kosong";
        if (productAddOnTypeRequest.getProductType() == null)
            return "Produk tipe tidak boleh nol atau kosong";

        return null;
    }

    public static Result listProductTipe(String filter, String sort, int offset, int limit) {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            Query<ProductAddOnType> query = ProductAddOnTypeRepository.find.where().eq("t0.is_deleted", false).eq("merchant", ownMerchant).order("t0.id");
            try {
                List<ProductAddOnTypeResponse> responses = new ArrayList<>();
                List<ProductAddOnType> totalData = ProductAddOnTypeRepository.getTotalDataPage(query);
                List<ProductAddOnType> responseIndex = ProductAddOnTypeRepository.findProductTipeWithPaging(query, sort, filter, offset, limit);
                for (ProductAddOnType data : responseIndex) {
                    ProductAddOnTypeResponse responseData = new ProductAddOnTypeResponse();
                    responseData.setId(data.id);
                    responseData.setProductType(data.getProductType());
                    responseData.setMerchantId(data.getMerchant().id);
                    responseData.setIsActive(data.getIsActive());
                    responses.add(responseData);
                }
                response.setBaseResponse(filter == null || filter.equals("") ? totalData.size() : responseIndex.size() , offset, limit, success + " menampilkan data", responses);
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
