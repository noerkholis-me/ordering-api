package controllers.brand;

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
import dtos.brand.*;
import models.Merchant;
import models.BrandMerchant;
import models.Photo;
import play.Logger;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Http;
import play.mvc.Result;
import repository.BrandMerchantRepository;

import java.io.File;
import java.util.*;
import com.avaje.ebean.Query;
import utils.ImageDirectory;
import utils.ImageUtil;

import java.io.IOException;

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
                        newBrandMerchant.setMerchant(ownMerchant);
                        newBrandMerchant.setActive(Boolean.TRUE);
                        newBrandMerchant.save();

                        // ========================== update with image ========================== //
                        /*
                        ** do the same for the save image mobile
                         */
                        // for Website
                        Http.MultipartFormData.FilePart imageFileWeb = Objects.requireNonNull(body).getFile("image-web");
                        File imageWeb = ImageUtil.uploadImage(imageFileWeb, "brand", "brand-web", ImageUtil.fullImageSize, "jpg");
                        String imageWebUrl = ImageUtil.createImageUrl("brand", imageWeb != null ? imageWeb.getName() : null);

                        // for Mobile
                        Http.MultipartFormData.FilePart imageFileMobile = Objects.requireNonNull(body).getFile("image-mobile");
                        File imageMobile = ImageUtil.uploadImage(imageFileMobile, "brand", "brand-mobile", ImageUtil.fullImageSize, "jpg");
                        String imageMobileUrl = ImageUtil.createImageUrl("brand", imageMobile != null ? imageMobile.getName() : null);
                        // ========================== update with image ========================== //
                        newBrandMerchant.setImageWeb(imageWebUrl);
                        newBrandMerchant.setImageMobile(imageMobileUrl);
                        newBrandMerchant.update();

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
    public static Result listBrand(String filter, String sort, int offset, int limit) {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            Query<BrandMerchant> query = BrandMerchantRepository.find.where().eq("t0.is_deleted", false).eq("t0.merchant_id", getUserMerchant().id).order("t0.id");
            try {
                List<BrandMerchantResponse> responses = new ArrayList<>();
                List<BrandMerchant> totalData = BrandMerchantRepository.getTotalData(query);
                List<BrandMerchant> responseIndex = BrandMerchantRepository.getDataBrand(query, sort, filter, offset, limit);
                for (BrandMerchant data : responseIndex) {
                    BrandMerchantResponse response = new BrandMerchantResponse();
                    response.setId(data.id);
                    response.setBrandName(data.getBrandName());
                    response.setImageWeb(data.getImageWeb());
                    response.setImageMobile(data.getImageMobile());
                    response.setIsDeleted(data.isDeleted);
                    response.setIsActive(data.isActive());
                    response.setMerchantId(data.getMerchant().id);
                    responses.add(response);
                }
                response.setBaseResponse(filter == null || filter.equals("") ? totalData.size() : responseIndex.size() , offset, limit, success + " menampilkan data", responses);
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
                        BrandMerchant newBrandMerchant = new BrandMerchant();
                        newBrandMerchant.setBrandName(request.getBrandName());
                        newBrandMerchant.setMerchant(ownMerchant);
                        newBrandMerchant.setActive(Boolean.TRUE);
                        newBrandMerchant.save();

                        // ========================== update with image ========================== //
                        /*
                        ** do the same for the save image mobile
                         */
                        // for Website
                        Http.MultipartFormData.FilePart imageFileWeb = Objects.requireNonNull(body).getFile("image-web");
                        File imageWeb = ImageUtil.uploadImage(imageFileWeb, "brand", "brand-web", ImageUtil.fullImageSize, "jpg");
                        String imageWebUrl = ImageUtil.createImageUrl("brand", imageWeb != null ? imageWeb.getName() : null);

                        // for Mobile
                        Http.MultipartFormData.FilePart imageFileMobile = Objects.requireNonNull(body).getFile("image-mobile");
                        File imageMobile = ImageUtil.uploadImage(imageFileMobile, "brand", "brand-mobile", ImageUtil.fullImageSize, "jpg");
                        String imageMobileUrl = ImageUtil.createImageUrl("brand", imageMobile != null ? imageMobile.getName() : null);
                        // ========================== update with image ========================== //
                        newBrandMerchant.setImageWeb(imageWebUrl);
                        newBrandMerchant.setImageMobile(imageMobileUrl);
                        newBrandMerchant.update();

                        trx.commit();
                        response.setBaseResponse(1, offset, 1, success + " mengubah brand", newBrandMerchant);
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
                        BrandMerchant BrandMerchant = BrandMerchantRepository.findByIdAndMerchantId(id, ownMerchant.id);
                        if (BrandMerchant == null) {
                            response.setBaseResponse(0, 0, 0, error + " brand tidak tersedia.", null);
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
                    BrandMerchant BrandMerchant = BrandMerchantRepository.findByIdAndMerchantId(id, ownMerchant.id);
                    if (BrandMerchant == null) {
                        response.setBaseResponse(0, 0, 0, error + " role merchant not found.", null);
                        return badRequest(Json.toJson(response));
                    }
                    BrandMerchantResponse BrandMerchantResponse = new BrandMerchantResponse();
                    BrandMerchantResponse.setId(BrandMerchant.id);
                    BrandMerchantResponse.setBrandName(BrandMerchant.getBrandName());
                    BrandMerchantResponse.setImageWeb(BrandMerchant.getImageWeb());
                    BrandMerchantResponse.setImageMobile(BrandMerchant.getImageMobile());
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

}
