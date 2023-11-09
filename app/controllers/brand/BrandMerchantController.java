package controllers.brand;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Query;
import com.avaje.ebean.Transaction;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hokeba.api.BaseResponse;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiImplicitParam;
import com.wordnik.swagger.annotations.ApiImplicitParams;
import com.wordnik.swagger.annotations.ApiOperation;
import controllers.BaseController;
import dtos.brand.BrandDetailResponse;
import dtos.brand.BrandMerchantResponse;
import models.BrandMerchant;
import models.Merchant;
import models.ProductStore;
import models.Store;
import models.SubsCategoryMerchant;
import models.merchant.ProductMerchant;
import models.merchant.ProductMerchantDescription;
import models.merchant.ProductMerchantDetail;
import play.Logger;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import repository.BrandMerchantRepository;
import repository.ProductMerchantDescriptionRepository;
import repository.ProductMerchantDetailRepository;
import repository.ProductMerchantRepository;
import repository.ProductStoreRepository;
import repository.SubsCategoryMerchantRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Api(value = "/merchants/brand", description = "Brand Merchant")
public class BrandMerchantController extends BaseController {

    private final static Logger.ALogger logger = Logger.of(BrandMerchantController.class);

    private static BaseResponse response = new BaseResponse();

    private static final ObjectMapper objectMapper = new ObjectMapper();

//    @ApiOperation(value = "Create Brand", notes = "Create Brand.\n" + swaggerInfo
//            + "", response = BaseResponse.class, httpMethod = "POST")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "brand form", dataType = "temp.swaggermap.BrandForm", required = true, paramType = "body", value = "brand form") })
//    @BodyParser.Of(value = BodyParser.Json.class, maxLength = 50 * 1024 * 1024)
    public static Result createBrand() {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            try {
                Http.MultipartFormData body = request().body().asMultipartFormData();
                JsonNode json = null;
                if (body != null) {
                    Map<String, String[]> data = body.asFormUrlEncoded();
                    if (data != null) {
                        json = Json.parse(data.get("data")[0]);
                    }
                } else {
                    json = request().body().asJson();
                }
                BrandMerchantResponse request = objectMapper.readValue(json.toString(), BrandMerchantResponse.class);
                String validate = validateCreateBrand(request);
                if (validate == null) {
                    Transaction trx = Ebean.beginTransaction();
                    try {
                        BrandMerchant newBrandMerchant = new BrandMerchant();
                        newBrandMerchant.setBrandName(request.getBrandName());
                        newBrandMerchant.setBrandType(request.getBrandType());
                        newBrandMerchant.setBrandDescription(request.getBrandDescription());
                        newBrandMerchant.setMerchant(ownMerchant);
                        newBrandMerchant.setActive(request.getIsActive());

                        // // ========================== update with image ========================== //
                        // /*
                        // ** do the same for the save image mobile
                        //  */
                        // // for Website
                        // Http.MultipartFormData.FilePart imageFileWeb = Objects.requireNonNull(body).getFile("image_web");
                        // File imageWeb = ImageUtil.uploadImage(imageFileWeb, "brand", "brand-web", ImageUtil.fullImageSize, "jpg");
                        // String imageWebUrl = ImageUtil.createImageUrl("brand", imageWeb != null ? imageWeb.getName() : null);

                        // // for Mobile
                        // Http.MultipartFormData.FilePart imageFileMobile = Objects.requireNonNull(body).getFile("image_mobile");
                        // File imageMobile = ImageUtil.uploadImage(imageFileMobile, "brand", "brand-mobile", ImageUtil.fullImageSize, "jpg");
                        // String imageMobileUrl = ImageUtil.createImageUrl("brand", imageMobile != null ? imageMobile.getName() : null);

                        // //for icon web
                        // Http.MultipartFormData.FilePart iconFileWeb = Objects.requireNonNull(body).getFile("icon_web");
                        // File iconWeb = ImageUtil.uploadImage(iconFileWeb, "brand", "icon-web", ImageUtil.fullImageSize, "jpg");
                        // String iconWebUrl = ImageUtil.createImageUrl("brand", iconWeb != null ? iconWeb.getName() : null);
                        
                        // //for icon mobile
                        // Http.MultipartFormData.FilePart iconFileMobile = Objects.requireNonNull(body).getFile("icon_mobile");
                        // File iconMobile = ImageUtil.uploadImage(iconFileMobile, "brand", "icon-mobile", ImageUtil.fullImageSize, "jpg");
                        // String iconMobileUrl = ImageUtil.createImageUrl("brand", iconMobile != null ? iconMobile.getName() : null);
                        // // ========================== update with image ========================== //
                        newBrandMerchant.setImageWeb(request.getImageWeb());
                        newBrandMerchant.setImageMobile(request.getImageMobile());
                        newBrandMerchant.setIconWeb(request.getIconWeb());
                        newBrandMerchant.setIconMobile(request.getIconMobile());
                        newBrandMerchant.save();

                        trx.commit();
                        response.setBaseResponse(1, offset, 1, success + " membuat brand", newBrandMerchant);
                        return ok(Json.toJson(response));
                    } catch (Exception e) {
                        logger.error("Error saat membuat brand", e);
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


    public static String validateCreateBrand(BrandMerchantResponse request) {
        if (request == null)
            return "Bidang tidak boleh nol atau kosong";
        if (request.getBrandName() == null)
            return "Nama Brand tidak boleh nol atau kosong";

        return null;
    }

    @ApiOperation(value = "Get all brand list.", notes = "Returns list of brand.\n" + swaggerInfo
            + "", response = BrandMerchant.class, responseContainer = "List", httpMethod = "GET")
    public static Result listBrand(String filter, String sort, int offset, int limit, String isActive) {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            try {
                int totalData = BrandMerchantRepository.getDataBrand(ownMerchant.id, isActive, sort, filter, 0, 0).size();
                List<BrandMerchant> responseIndex = BrandMerchantRepository.getDataBrand(ownMerchant.id, isActive, sort, filter, offset, limit);
                List<BrandMerchantResponse> responses = new ArrayList<>();
                for (BrandMerchant data : responseIndex) {
                    BrandMerchantResponse response = new BrandMerchantResponse();
                    response.setId(data.id);
                    response.setBrandName(data.getBrandName());
                    response.setBrandType(data.getBrandType());
                    response.setBrandDescription(data.getBrandDescription());
                    response.setImageWeb(data.getImageWeb());
                    response.setImageMobile(data.getImageMobile());
                    response.setIconWeb(data.getIconWeb());
                    response.setIconMobile(data.getIconMobile());
                    response.setIsDeleted(data.isDeleted);
                    response.setIsActive(data.isActive());
                    response.setMerchantId(data.getMerchant().id);
                    responses.add(response);
                }
                response.setBaseResponse(totalData, offset, limit, "Berhasil menampilkan data", responses);
                return ok(Json.toJson(response));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (ownMerchant == null) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result listBrandPOS(String filter, String sort, int offset, int limit, String isActive) {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            try {
                int totalData = BrandMerchantRepository.getDataBrand(ownMerchant.id, isActive, sort, filter, 0, 0).size();
                List<BrandMerchant> responseIndex = BrandMerchantRepository.getDataBrand(ownMerchant.id, isActive, sort, filter, offset, limit);
                List<BrandMerchantResponse> responses = new ArrayList<>();
                for (BrandMerchant data : responseIndex) {
                    BrandMerchantResponse response = new BrandMerchantResponse();
                    response.setId(data.id);
                    response.setBrandName(data.getBrandName());
                    response.setBrandType(data.getBrandType());
                    response.setBrandDescription(data.getBrandDescription());
                    response.setImageWeb(data.getImageWeb());
                    response.setImageMobile(data.getImageMobile());
                    response.setIconWeb(data.getIconWeb());
                    response.setIconMobile(data.getIconMobile());
                    response.setIsDeleted(data.isDeleted);
                    response.setIsActive(data.isActive());
                    response.setMerchantId(data.getMerchant().id);
                    responses.add(response);
                }
                response.setBaseResponse(totalData, offset, limit, "Berhasil menampilkan data", responses);
                return ok(Json.toJson(response));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (ownMerchant == null) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    // @ApiOperation(value = "Edit Brand", notes = "Edit Brand.\n" + swaggerInfo
    //         + "", response = BaseResponse.class, httpMethod = "PUT")
    // @ApiImplicitParams({
    //         @ApiImplicitParam(name = "brand form", dataType = "temp.swaggermap.BrandForm", required = true, paramType = "body", value = "brand form") })
    public static Result editBrand(Long id) {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            try {
                Http.MultipartFormData body = request().body().asMultipartFormData();
                JsonNode json = null;
                if (body != null) {
                    Map<String, String[]> data = body.asFormUrlEncoded();
                    if (data != null) {
                        json = Json.parse(data.get("data")[0]);
                    }
                } else {
                    json = request().body().asJson();
                }
                BrandMerchantResponse request = objectMapper.readValue(json.toString(), BrandMerchantResponse.class);
                String validate = validateCreateBrand(request);
                if (validate == null) {
                    Transaction trx = Ebean.beginTransaction();
                    try {
                        BrandMerchant brandMerchant = BrandMerchantRepository.findByIdAndMerchantId(id, ownMerchant);
                        if (brandMerchant == null) {
                            response.setBaseResponse(0, 0, 0, error + " brand tidak tersedia.", null);
                            return badRequest(Json.toJson(response));
                        }
                        brandMerchant.setBrandName(request.getBrandName());
                        brandMerchant.setBrandType(request.getBrandType());
                        brandMerchant.setBrandDescription(request.getBrandDescription());
                        brandMerchant.setMerchant(ownMerchant);
                        brandMerchant.setActive(request.getIsActive());

                        // // ========================== update with image ========================== //
                        // /*
                        // ** do the same for the save image mobile
                        //  */
                        // // for Website
                        // Http.MultipartFormData.FilePart imageFileWeb = Objects.requireNonNull(body).getFile("image_web");
                        // File imageWeb = ImageUtil.uploadImage(imageFileWeb, "brand", "brand-web", ImageUtil.fullImageSize, "jpg");
                        // String imageWebUrl = ImageUtil.createImageUrl("brand", imageWeb != null ? imageWeb.getName() : null);
 
                        // // for Mobile
                        // Http.MultipartFormData.FilePart imageFileMobile = Objects.requireNonNull(body).getFile("image_mobile");
                        // File imageMobile = ImageUtil.uploadImage(imageFileMobile, "brand", "brand-mobile", ImageUtil.fullImageSize, "jpg");
                        // String imageMobileUrl = ImageUtil.createImageUrl("brand", imageMobile != null ? imageMobile.getName() : null);
 
                        // //for icon web
                        // Http.MultipartFormData.FilePart iconFileWeb = Objects.requireNonNull(body).getFile("icon_web");
                        // File iconWeb = ImageUtil.uploadImage(iconFileWeb, "brand", "icon-web", ImageUtil.fullImageSize, "jpg");
                        // String iconWebUrl = ImageUtil.createImageUrl("brand", iconWeb != null ? iconWeb.getName() : null);
                         
                        // //for icon mobile
                        // Http.MultipartFormData.FilePart iconFileMobile = Objects.requireNonNull(body).getFile("icon_mobile");
                        // File iconMobile = ImageUtil.uploadImage(iconFileMobile, "brand", "icon-mobile", ImageUtil.fullImageSize, "jpg");
                        // String iconMobileUrl = ImageUtil.createImageUrl("brand", iconMobile != null ? iconMobile.getName() : null);
                        // // ========================== update with image ========================== //

                        if(request.getImageWeb() != null){
                            brandMerchant.setImageWeb(request.getImageWeb());
                        }
                        if(request.getImageMobile() != null){
                            brandMerchant.setImageMobile(request.getImageMobile());
                        }
                        if(request.getIconWeb() != null){
                            brandMerchant.setIconWeb(request.getIconWeb());
                        }
                        if(request.getIconMobile() != null){
                            brandMerchant.setIconMobile(request.getIconMobile());
                        }
                        brandMerchant.update();

                        trx.commit();
                        response.setBaseResponse(1, offset, 1, success + " mengubah brand", brandMerchant);
                        return ok(Json.toJson(response));
                    } catch (Exception e) {
                        logger.error("Error saat mengubah brand", e);
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

    @ApiOperation(value = "Delete Brand", notes = "Delete Brand.\n" + swaggerInfo
            + "", response = BaseResponse.class, httpMethod = "DELETE")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "brand form", dataType = "temp.swaggermap.BrandForm", required = true, paramType = "body", value = "brand form") })
    public static Result deleteBrand(Long id) {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
                if (id != null) {
                    Transaction trx = Ebean.beginTransaction();
                    try {
                        BrandMerchant BrandMerchant = BrandMerchantRepository.findByIdAndMerchantId(id, ownMerchant);
                        if (BrandMerchant == null) {
                            response.setBaseResponse(0, 0, 0, error + " brand tidak tersedia.", null);
                            return badRequest(Json.toJson(response));
                        }

                        List<ProductMerchant> totalData = ProductMerchantRepository.find.where().eq("brand_merchant_id", BrandMerchant.id).eq("is_deleted", Boolean.FALSE).findPagingList(0).getPage(0).getList();
                        if (totalData.size() != 0) {
                            response.setBaseResponse(0, 0, 0, "Tidak dapat menghapus Brand. " +BrandMerchant.getBrandName()+ " memiliki " + totalData.size() + " Produk.", null);
                            return badRequest(Json.toJson(response));
                        }

                        BrandMerchant.isDeleted = true;
                        BrandMerchant.update();
                        trx.commit();

                        response.setBaseResponse(1,offset, 1, success + " menghapus brand", BrandMerchant);
                        return ok(Json.toJson(response));
                    } catch (Exception e) {
                        logger.error("Error saat menghapus brand", e);
                        e.printStackTrace();
                        trx.rollback();
                    } finally {
                        trx.end();
                    }
                    response.setBaseResponse(0, 0, 0, error, null);
                    return badRequest(Json.toJson(response));
                }
                response.setBaseResponse(0, 0, 0, "Tidak dapat menemukan brand id", null);
                return badRequest(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    @ApiOperation(value = "Read Brand", notes = "Read Brand.\n" + swaggerInfo
            + "", response = BaseResponse.class, httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "brand form", dataType = "temp.swaggermap.BrandForm", required = true, paramType = "body", value = "brand form") })
    public static Result viewBrand(Long id) {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            if (id != null) {
                Transaction trx = Ebean.beginTransaction();
                try {
                    BrandMerchant BrandMerchant = BrandMerchantRepository.findByIdAndMerchantId(id, ownMerchant);
                    if (BrandMerchant == null) {
                        response.setBaseResponse(0, 0, 0, error + " brand merchant tidak tersedia.", null);
                        return badRequest(Json.toJson(response));
                    }
                    BrandMerchantResponse BrandMerchantResponse = new BrandMerchantResponse();
                    BrandMerchantResponse.setId(BrandMerchant.id);
                    BrandMerchantResponse.setBrandName(BrandMerchant.getBrandName());
                    BrandMerchantResponse.setBrandType(BrandMerchant.getBrandType());
                    BrandMerchantResponse.setBrandDescription(BrandMerchant.getBrandDescription());
                    BrandMerchantResponse.setImageWeb(BrandMerchant.getImageWeb());
                    BrandMerchantResponse.setImageMobile(BrandMerchant.getImageMobile());
                    BrandMerchantResponse.setIconWeb(BrandMerchant.getIconWeb());
                    BrandMerchantResponse.setIconMobile(BrandMerchant.getIconMobile());
                    BrandMerchantResponse.setIsDeleted(BrandMerchant.isDeleted);
                    BrandMerchantResponse.setIsActive(BrandMerchant.isActive());
                    BrandMerchantResponse.setMerchantId(BrandMerchant.getMerchant().id);

                    response.setBaseResponse(1,offset, 1, success + " menampilkan detail brand", BrandMerchantResponse);
                    return ok(Json.toJson(response));
                } catch (Exception e) {
                    logger.error("Error saat menampilkan detail brand", e);
                    e.printStackTrace();
                    trx.rollback();
                } finally {
                    trx.end();
                }
                response.setBaseResponse(0, 0, 0, error, null);
                return badRequest(Json.toJson(response));
            }
            response.setBaseResponse(0, 0, 0, "Tidak dapat menemukan brand id", null);
            return badRequest(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    @ApiOperation(value = "Status Brand", notes = "Status Brand.\n" + swaggerInfo
            + "", response = BaseResponse.class, httpMethod = "PUT")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "brand form", dataType = "temp.swaggermap.BrandForm", required = true, paramType = "body", value = "brand form") })
        public static Result setStatus(Long id) {
            Merchant ownMerchant = checkMerchantAccessAuthorization();
            if (ownMerchant != null) {
                try {
                    JsonNode json = request().body().asJson();
                    BrandMerchantResponse request = objectMapper.readValue(json.toString(), BrandMerchantResponse.class);
                    Transaction trx = Ebean.beginTransaction();
                    try {
                        BrandMerchant brandMerchant = BrandMerchantRepository.findByIdAndMerchantId(id, ownMerchant);
                        if (brandMerchant == null) {
                            response.setBaseResponse(0, 0, 0, error + " brand tidak tersedia.", null);
                            return badRequest(Json.toJson(response));
                        }
                        brandMerchant.setActive(request.getIsActive());
                        brandMerchant.update();
    
                        trx.commit();
                        response.setBaseResponse(1, offset, 1, success + " mengubah status brand", brandMerchant);
                        return ok(Json.toJson(response));
                    } catch (Exception e) {
                        logger.error("Error saat mengubah status brand", e);
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

        @ApiOperation(value = "Get all brand list.", notes = "Returns list of brand.\n" + swaggerInfo
            + "", response = BrandMerchant.class, responseContainer = "List", httpMethod = "GET")
    public static Result listBrandHomepage(Long merchantId, Long storeId, int offset) {
        Merchant ownMerchant = Merchant.merchantGetId(merchantId);
        if (ownMerchant != null) {
            Store store = Store.find.byId(storeId);
            if (store == null) {
                response.setBaseResponse(0, 0, 0, "Store tidak ditemukan", null);
                return badRequest(Json.toJson(response));
            }
            try {
                Integer totalData = BrandMerchantRepository.findListBrand(merchantId, 0, 0).size();
                List<BrandMerchant> data = BrandMerchantRepository.findListBrand(merchantId, offset, 0);
                List<BrandMerchantResponse> responses = new ArrayList<>();
                for (BrandMerchant brandMerchant : data) {
                    Integer totalProductBrand = BrandMerchantRepository.getTotalProductBrand(merchantId, brandMerchant.id);
                    BrandMerchantResponse response = new BrandMerchantResponse();
                    response.setId(brandMerchant.id);
                    response.setBrandName(brandMerchant.getBrandName());
                    response.setBrandType(brandMerchant.getBrandType());
                    response.setBrandDescription(brandMerchant.getBrandDescription());
                    response.setImageWeb(brandMerchant.getImageWeb());
                    response.setImageMobile(brandMerchant.getImageMobile());
                    response.setIconWeb(brandMerchant.getIconWeb());
                    response.setIconMobile(brandMerchant.getIconMobile());
                    response.setIsDeleted(brandMerchant.isDeleted);
                    response.setIsActive(brandMerchant.isActive());
                    response.setMerchantId(brandMerchant.getMerchant().id);
                    response.setTotalProduct(totalProductBrand);
                    responses.add(response);
                }
                response.setBaseResponse(totalData, offset, limit, "Berhasil menampilkan data", responses);
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

    // FOR HOME CUSTOMER
    public static Result detailBrandHome(Long id, Long merchantId, Long storeId) {
        if (id != null) {
            Merchant ownMerchant = Merchant.merchantGetId(merchantId);
            Transaction trx = Ebean.beginTransaction();
            try {
                BrandMerchant brandMerchant = BrandMerchantRepository.findByIdAndMerchantId(id, ownMerchant);
                if (brandMerchant == null) {
                    response.setBaseResponse(0, 0, 0, error + " brand tidak tersedia.", null);
                    return badRequest(Json.toJson(response));
                }
                // BRAND RESPONSE
                BrandDetailResponse brandDetailResponse = new BrandDetailResponse();
                brandDetailResponse.setId(brandMerchant.id);
                brandDetailResponse.setBrandName(brandMerchant.getBrandName());
                brandDetailResponse.setBrandType(brandMerchant.getBrandType());
                brandDetailResponse.setBrandDescription(brandMerchant.getBrandDescription());
                brandDetailResponse.setImageWeb(brandMerchant.getImageWeb());
                brandDetailResponse.setImageMobile(brandMerchant.getImageMobile());
                brandDetailResponse.setIconWeb(brandMerchant.getIconWeb());
                brandDetailResponse.setIconMobile(brandMerchant.getIconMobile());
                brandDetailResponse.setIsDeleted(brandMerchant.isDeleted);
                brandDetailResponse.setIsActive(brandMerchant.isActive());
                brandDetailResponse.setMerchantId(brandMerchant.getMerchant().id);
                
                //FOREACH CATEGORY AT BRAND
                Query<SubsCategoryMerchant> querySubCategory = SubsCategoryMerchantRepository.find.where().eq("t0.is_deleted", false).eq("merchant", ownMerchant).eq("t0.is_active", true).order("t0.sequence asc");
                List<SubsCategoryMerchant> dataSubCategory = SubsCategoryMerchantRepository.getForDetailKiosk(querySubCategory);
                List<BrandDetailResponse.SubsCategoryMerchant> categoryListResponses = new ArrayList<>();
                brandDetailResponse.setCategory(categoryListResponses);

                for(SubsCategoryMerchant subsCategory : dataSubCategory){
                    BrandDetailResponse.SubsCategoryMerchant categoryResponse = new BrandDetailResponse.SubsCategoryMerchant();
                	categoryResponse.setId(subsCategory.id);
                    categoryResponse.setSubscategoryName(subsCategory.getSubscategoryName());
                    categoryResponse.setImageWeb(subsCategory.getImageWeb());
                    categoryResponse.setImageMobile(subsCategory.getImageMobile());
                    categoryResponse.setIsDeleted(subsCategory.isDeleted());
                    categoryResponse.setIsActive(subsCategory.getIsActive());
                	
                    //FOREACH PRODUCT INSIDE
//                    Query<ProductMerchant> queryProduct = ProductMerchantRepository.find.where().eq("t0.subs_category_merchant_id", subsCategory.id).eq("t0.brand_merchant_id", id).eq("t0.is_deleted", false).eq("t0.is_active", true).eq("merchant", ownMerchant).order("t0.id");
//                    List<ProductMerchant> dataProduct = ProductMerchantRepository.getDataProductStore(queryProduct);
//                  List<ProductMerchantDetail> totalDataProductDetail = ProductMerchantDetailRepository.getTotalDataPage(query);
                    
                    String queryProductDetail = "t0.product_merchant_id in (select pm.id from product_merchant pm where pm.merchant_id = "+merchantId+" and pm.subs_category_merchant_id = "+subsCategory.id+" and pm.brand_merchant_id = "+id+" and pm.is_active = "+true+" and pm.is_deleted = false)";
                    Query<ProductMerchantDetail> query = ProductMerchantDetailRepository.find.where().raw(queryProductDetail).eq("t0.is_deleted", false).eq("t0.product_type", "MAIN").order("random()");
                    List<ProductMerchantDetail> productMerchantDetails = ProductMerchantDetailRepository.forProductRecommendation(query);
                    List<BrandDetailResponse.SubsCategoryMerchant.ProductMerchant> productListResponses = new ArrayList<>();
                    for(ProductMerchantDetail productMerchantDetail : productMerchantDetails){
                        BrandDetailResponse.SubsCategoryMerchant.ProductMerchant productResponses = new BrandDetailResponse.SubsCategoryMerchant.ProductMerchant();
                        ProductMerchant productMerchant = ProductMerchantRepository.findByIdProductRecommend(productMerchantDetail.getProductMerchant().id, merchantId);
                        ProductStore productStore = ProductStoreRepository.findForCust(productMerchant.id, storeId, ownMerchant);
                        List<ProductStore> listProductStore = ProductStoreRepository.find.where().eq("t0.is_deleted", false).eq("t0.is_active", true).eq("t0.product_id", productMerchantDetail.getProductMerchant().id).orderBy().desc("t0.id").findList();
                        
                        productResponses.setProductId(productMerchant.id);
                        productResponses.setProductName(productMerchant.getProductName());
                        productResponses.setProductType(productMerchantDetail.getProductType());
                        productResponses.setIsCustomizable(productMerchantDetail.getIsCustomizable());
                        
                        if(productStore != null) {
                        	//if product with store target exist
                            productListResponses.add(productResponses);
                            productResponses.setProductPrice(productStore.getStorePrice());
                            productResponses.setDiscountType(productStore.getDiscountType());
                            productResponses.setDiscount(productStore.getDiscount());
                            productResponses.setProductPriceAfterDiscount(productStore.getFinalPrice());
                        } else if (listProductStore.isEmpty()){
                        	//if product doesn't assigned to any store (global product)
                            productListResponses.add(productResponses);
                            productResponses.setProductPrice(productMerchantDetail.getProductPrice());
                            productResponses.setDiscountType(productMerchantDetail.getDiscountType());
                            productResponses.setDiscount(productMerchantDetail.getDiscount());
                            productResponses.setProductPriceAfterDiscount(productMerchantDetail.getProductPriceAfterDiscount());
                        }
    
                        ProductMerchantDescription productMerchantDescription = ProductMerchantDescriptionRepository.findByProductMerchantDetail(productMerchantDetail);
                        if (productMerchantDescription != null) {
                            BrandDetailResponse.SubsCategoryMerchant.ProductMerchant.ProductDescriptionResponse productDescriptionResponse = new BrandDetailResponse.SubsCategoryMerchant.ProductMerchant.ProductDescriptionResponse();
                            productDescriptionResponse.setShortDescription(productMerchantDescription.getShortDescription());
                            productDescriptionResponse.setLongDescription(productMerchantDescription.getLongDescription());
                            productResponses.setProductDescription(productDescriptionResponse);
                        }

                        productResponses.setProductImageMain(productMerchantDetail.getProductImageMain());
                    }
                    if (!productListResponses.isEmpty()) {
	                    categoryResponse.setProduct(productListResponses);
	                    categoryListResponses.add(categoryResponse);
                    }
                }

                response.setBaseResponse(1,offset, 1, success + " menampilkan detail brand", brandDetailResponse);
                return ok(Json.toJson(response));
            } catch (Exception e) {
                logger.error("Error saat menampilkan detail brand", e);
                e.printStackTrace();
                trx.rollback();
            } finally {
                trx.end();
            }
            response.setBaseResponse(0, 0, 0, error, null);
            return badRequest(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, "Tidak dapat menemukan brand id", null);
        return badRequest(Json.toJson(response));
    }


}