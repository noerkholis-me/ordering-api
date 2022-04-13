package controllers.categories;

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
import dtos.category.*;
import models.Merchant;
import models.CategoryMerchant;
import models.SubCategoryMerchant;
import models.SubsCategoryMerchant;
import models.Photo;
import play.Logger;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Http;
import play.mvc.Result;
import repository.CategoryMerchantRepository;
import repository.SubCategoryMerchantRepository;
import repository.SubsCategoryMerchantRepository;

import java.io.File;
import java.util.*;
import com.avaje.ebean.Query;
import utils.ImageDirectory;
import utils.ImageUtil;

import java.io.IOException;

@Api(value = "/merchants/category", description = "Category Merchant")
public class CategoryMerchantController extends BaseController {

    private final static Logger.ALogger logger = Logger.of(CategoryMerchantController.class);

    private static BaseResponse response = new BaseResponse();

    private static final ObjectMapper objectMapper = new ObjectMapper();

//    @ApiOperation(value = "Create Category", notes = "Create Category.\n" + swaggerInfo
//            + "", response = BaseResponse.class, httpMethod = "POST")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "category form", dataType = "temp.swaggermap.CategoryForm", required = true, paramType = "body", value = "category form") })
//    @BodyParser.Of(value = BodyParser.Json.class, maxLength = 50 * 1024 * 1024)
    public static Result createCategory() {
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
                CategoryMerchantResponse request = objectMapper.readValue(json.toString(), CategoryMerchantResponse.class);
                String validate = validateCreateCategory(request);
                if (validate == null) {
                    Transaction trx = Ebean.beginTransaction();
                    try {
                        CategoryMerchant newCategoryMerchant = new CategoryMerchant();
                        newCategoryMerchant.setCategoryName(request.getCategoryName());
                        newCategoryMerchant.setMerchant(ownMerchant);
                        newCategoryMerchant.setActive(request.getIsActive());
                        newCategoryMerchant.save();

                        // ========================== update with image ========================== //
                        /*
                        ** do the same for the save image mobile
                         */
                        // for Website
                        Http.MultipartFormData.FilePart imageFileWeb = Objects.requireNonNull(body).getFile("image_web");
                        File imageWeb = ImageUtil.uploadImage(imageFileWeb, "category", "category-web", ImageUtil.fullImageSize, "jpg");
                        String imageWebUrl = ImageUtil.createImageUrl("category", imageWeb != null ? imageWeb.getName() : null);

                        // for Mobile
                        Http.MultipartFormData.FilePart imageFileMobile = Objects.requireNonNull(body).getFile("image_mobile");
                        File imageMobile = ImageUtil.uploadImage(imageFileMobile, "category", "category-mobile", ImageUtil.fullImageSize, "jpg");
                        String imageMobileUrl = ImageUtil.createImageUrl("category", imageMobile != null ? imageMobile.getName() : null);
                        // ========================== update with image ========================== //
                        newCategoryMerchant.setImageWeb(imageWebUrl);
                        newCategoryMerchant.setImageMobile(imageMobileUrl);
                        newCategoryMerchant.update();

                        trx.commit();
                        response.setBaseResponse(1, offset, 1, success + " membuat kategori", newCategoryMerchant);
                        return ok(Json.toJson(response));
                    } catch (Exception e) {
                        logger.error("Error saat membuat kategori", e);
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


    public static String validateCreateCategory(CategoryMerchantResponse request) {
        if (request == null)
            return "Bidang tidak boleh nol atau kosong";
        if (request.getCategoryName() == null)
            return "Nama Category tidak boleh nol atau kosong";

        return null;
    }

    @ApiOperation(value = "Get all category list.", notes = "Returns list of category.\n" + swaggerInfo
            + "", response = CategoryMerchant.class, responseContainer = "List", httpMethod = "GET")
    public static Result listCategory(String filter, String sort, int offset, int limit) {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            Query<CategoryMerchant> query = CategoryMerchantRepository.find.where().eq("t0.is_deleted", false).eq("t0.merchant_id", getUserMerchant().id).order("t0.id");
            try {
                List<CategoryMerchantResponse> responses = new ArrayList<>();
                List<CategoryMerchant> totalData = CategoryMerchantRepository.getTotalData(query);
                List<CategoryMerchant> responseIndex = CategoryMerchantRepository.getDataCategory(query, sort, filter, offset, limit);
                for (CategoryMerchant data : responseIndex) {
                    CategoryMerchantResponse response = new CategoryMerchantResponse();
                    Query<SubCategoryMerchant> querySub = SubCategoryMerchantRepository.find.where().eq("t0.category_id", data.id).eq("t0.is_deleted", false).eq("t0.merchant_id", getUserMerchant().id).order("t0.id");
                    List<SubCategoryMerchant> dataSub = SubCategoryMerchantRepository.getDataForCategory(querySub);
                    List<CategoryMerchantResponse.SubCategoryMerchant> responsesSub = new ArrayList<>();
                    response.setId(data.id);
                    response.setCategoryName(data.getCategoryName());
                    response.setImageWeb(data.getImageWeb());
                    response.setImageMobile(data.getImageMobile());
                    response.setIsDeleted(data.isDeleted);
                    response.setIsActive(data.isActive());
                    response.setMerchantId(data.getMerchant().id);
                    for(SubCategoryMerchant dataSubs : dataSub) {
                        CategoryMerchantResponse.SubCategoryMerchant responseSub = new CategoryMerchantResponse.SubCategoryMerchant();
                        Query<SubsCategoryMerchant> querySubs = SubsCategoryMerchantRepository.find.where().eq("t0.subcategory_id", dataSubs.id).eq("t0.is_deleted", false).eq("t0.merchant_id", getUserMerchant().id).order("t0.id");
                        List<SubsCategoryMerchant> dataSubThree = SubsCategoryMerchantRepository.getDataForCategory(querySubs);
                        List<CategoryMerchantResponse.SubCategoryMerchant.SubsCategoryMerchant> responsesSubs = new ArrayList<>();
                        responseSub.setId(dataSubs.id);
                        responseSub.setSubcategoryName(dataSubs.getSubcategoryName());
                        responseSub.setImageWeb(dataSubs.getImageWeb());
                        responseSub.setImageMobile(dataSubs.getImageMobile());
                        responseSub.setIsActive(dataSubs.isActive);
                        responseSub.setIsDeleted(dataSubs.isDeleted);
                        for(SubsCategoryMerchant dataSubsThree : dataSubThree) {
                            CategoryMerchantResponse.SubCategoryMerchant.SubsCategoryMerchant responseSubs = new CategoryMerchantResponse.SubCategoryMerchant.SubsCategoryMerchant();
                            responseSubs.setId(dataSubsThree.id);
                            responseSubs.setSubscategoryName(dataSubsThree.getSubscategoryName());
                            responseSubs.setImageWeb(dataSubsThree.getImageWeb());
                            responseSubs.setImageMobile(dataSubsThree.getImageMobile());
                            responseSubs.setIsActive(dataSubsThree.isActive);
                            responseSubs.setIsDeleted(dataSubsThree.isDeleted);
                            responseSubs.setSequence(dataSubsThree.getSequence());
                            responsesSubs.add(responseSubs);
                            responseSub.setSubsCategory(responseSubs != null ? responsesSubs : null);
                        }
                        
                        responsesSub.add(responseSub);
                        response.setSubCategory(responseSub != null ? responsesSub : null);
                    }
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

    // @ApiOperation(value = "Edit Category", notes = "Edit Category.\n" + swaggerInfo
    //         + "", response = BaseResponse.class, httpMethod = "PUT")
    // @ApiImplicitParams({
    //         @ApiImplicitParam(name = "category form", dataType = "temp.swaggermap.CategoryForm", required = true, paramType = "body", value = "category form") })
    public static Result editCategory(Long id) {
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
                CategoryMerchantResponse request = objectMapper.readValue(json.toString(), CategoryMerchantResponse.class);
                String validate = validateCreateCategory(request);
                if (validate == null) {
                    Transaction trx = Ebean.beginTransaction();
                    try {
                        CategoryMerchant categoryMerchant = CategoryMerchantRepository.findByIdAndMerchantId(id, ownMerchant.id);
                        if (categoryMerchant == null) {
                            response.setBaseResponse(0, 0, 0, error + " kategori tidak tersedia.", null);
                            return badRequest(Json.toJson(response));
                        }
                        categoryMerchant.setCategoryName(request.getCategoryName());
                        categoryMerchant.setMerchant(ownMerchant);
                        categoryMerchant.setActive(request.getIsActive());

                        // ========================== update with image ========================== //
                        /*
                        ** do the same for the save image mobile
                         */
                        // for Website
                        Http.MultipartFormData.FilePart imageFileWeb = Objects.requireNonNull(body).getFile("image_web");
                        File imageWeb = ImageUtil.uploadImage(imageFileWeb, "category", "category-web", ImageUtil.fullImageSize, "jpg");
                        String imageWebUrl = ImageUtil.createImageUrl("category", imageWeb != null ? imageWeb.getName() : null);

                        // for Mobile
                        Http.MultipartFormData.FilePart imageFileMobile = Objects.requireNonNull(body).getFile("image_mobile");
                        File imageMobile = ImageUtil.uploadImage(imageFileMobile, "category", "category-mobile", ImageUtil.fullImageSize, "jpg");
                        String imageMobileUrl = ImageUtil.createImageUrl("category", imageMobile != null ? imageMobile.getName() : null);
                        // ========================== update with image ========================== //
                        categoryMerchant.setImageWeb(imageWebUrl);
                        categoryMerchant.setImageMobile(imageMobileUrl);
                        categoryMerchant.update();

                        trx.commit();
                        response.setBaseResponse(1, offset, 1, success + " mengubah kategori", categoryMerchant);
                        return ok(Json.toJson(response));
                    } catch (Exception e) {
                        logger.error("Error saat mengubah kategori", e);
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

    @ApiOperation(value = "Delete Category", notes = "Delete Category.\n" + swaggerInfo
            + "", response = BaseResponse.class, httpMethod = "DELETE")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "category form", dataType = "temp.swaggermap.CategoryForm", required = true, paramType = "body", value = "category form") })
    public static Result deleteCategory(Long id) {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
                if (id != null) {
                    Transaction trx = Ebean.beginTransaction();
                    try {
                        CategoryMerchant CategoryMerchant = CategoryMerchantRepository.findByIdAndMerchantId(id, ownMerchant.id);
                        if (CategoryMerchant == null) {
                            response.setBaseResponse(0, 0, 0, error + " kategori tidak tersedia.", null);
                            return badRequest(Json.toJson(response));
                        }

                        CategoryMerchant.isDeleted = true;
                        CategoryMerchant.update();
                        trx.commit();

                        response.setBaseResponse(1,offset, 1, success + " menghapus kategori", CategoryMerchant);
                        return ok(Json.toJson(response));
                    } catch (Exception e) {
                        logger.error("Error saat menghapus kategori", e);
                        e.printStackTrace();
                        trx.rollback();
                    } finally {
                        trx.end();
                    }
                    response.setBaseResponse(0, 0, 0, error, null);
                    return badRequest(Json.toJson(response));
                }
                response.setBaseResponse(0, 0, 0, "Tidak dapat menemukan kategori id", null);
                return badRequest(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    @ApiOperation(value = "Read Category", notes = "Read Category.\n" + swaggerInfo
            + "", response = BaseResponse.class, httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "kategori form", dataType = "temp.swaggermap.CategoryForm", required = true, paramType = "body", value = "kategori form") })
    public static Result viewCategory(Long id) {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            if (id != null) {
                Transaction trx = Ebean.beginTransaction();
                try {
                    CategoryMerchant CategoryMerchant = CategoryMerchantRepository.findByIdAndMerchantId(id, ownMerchant.id);
                    if (CategoryMerchant == null) {
                        response.setBaseResponse(0, 0, 0, error + " kategori merchant tidak tersedia.", null);
                        return badRequest(Json.toJson(response));
                    }
                    CategoryMerchantResponse CategoryMerchantResponse = new CategoryMerchantResponse();
                    Query<SubCategoryMerchant> querySub = SubCategoryMerchantRepository.find.where().eq("t0.category_id", CategoryMerchant.id).eq("t0.is_deleted", false).eq("t0.merchant_id", getUserMerchant().id).order("t0.id");
                    List<SubCategoryMerchant> dataSub = SubCategoryMerchantRepository.getDataForCategory(querySub);
                    List<CategoryMerchantResponse.SubCategoryMerchant> responsesSub = new ArrayList<>();
                    CategoryMerchantResponse.setId(CategoryMerchant.id);
                    CategoryMerchantResponse.setCategoryName(CategoryMerchant.getCategoryName());
                    CategoryMerchantResponse.setImageWeb(CategoryMerchant.getImageWeb());
                    CategoryMerchantResponse.setImageMobile(CategoryMerchant.getImageMobile());
                    CategoryMerchantResponse.setIsDeleted(CategoryMerchant.isDeleted);
                    CategoryMerchantResponse.setIsActive(CategoryMerchant.isActive());
                    CategoryMerchantResponse.setMerchantId(CategoryMerchant.getMerchant().id);
                    for(SubCategoryMerchant dataSubs : dataSub) {
                        CategoryMerchantResponse.SubCategoryMerchant responseSub = new CategoryMerchantResponse.SubCategoryMerchant();
                        Query<SubsCategoryMerchant> querySubs = SubsCategoryMerchantRepository.find.where().eq("t0.subcategory_id", dataSubs.id).eq("t0.is_deleted", false).eq("t0.merchant_id", getUserMerchant().id).order("t0.id");
                        List<SubsCategoryMerchant> dataSubThree = SubsCategoryMerchantRepository.getDataForCategory(querySubs);
                        List<CategoryMerchantResponse.SubCategoryMerchant.SubsCategoryMerchant> responsesSubs = new ArrayList<>();
                        responseSub.setId(dataSubs.id);
                        responseSub.setSubcategoryName(dataSubs.getSubcategoryName());
                        responseSub.setImageWeb(dataSubs.getImageWeb());
                        responseSub.setImageMobile(dataSubs.getImageMobile());
                        responseSub.setIsActive(dataSubs.isActive);
                        responseSub.setIsDeleted(dataSubs.isDeleted);
                        for(SubsCategoryMerchant dataSubsThree : dataSubThree) {
                            CategoryMerchantResponse.SubCategoryMerchant.SubsCategoryMerchant responseSubs = new CategoryMerchantResponse.SubCategoryMerchant.SubsCategoryMerchant();
                            responseSubs.setId(dataSubsThree.id);
                            responseSubs.setSubscategoryName(dataSubsThree.getSubscategoryName());
                            responseSubs.setImageWeb(dataSubsThree.getImageWeb());
                            responseSubs.setImageMobile(dataSubsThree.getImageMobile());
                            responseSubs.setIsActive(dataSubsThree.isActive);
                            responseSubs.setIsDeleted(dataSubsThree.isDeleted);
                            responseSubs.setSequence(dataSubsThree.getSequence());
                            responsesSubs.add(responseSubs);
                            responseSub.setSubsCategory(responseSubs != null ? responsesSubs : null);
                        }
                        
                        responsesSub.add(responseSub);
                        CategoryMerchantResponse.setSubCategory(responseSub != null ? responsesSub : null);
                    }

                    response.setBaseResponse(1,offset, 1, success + " menampilkan detail kategori", CategoryMerchantResponse);
                    return ok(Json.toJson(response));
                } catch (Exception e) {
                    logger.error("Error saat menampilkan detail kategori", e);
                    e.printStackTrace();
                    trx.rollback();
                } finally {
                    trx.end();
                }
                response.setBaseResponse(0, 0, 0, error, null);
                return badRequest(Json.toJson(response));
            }
            response.setBaseResponse(0, 0, 0, "Tidak dapat menemukan kategori id", null);
            return badRequest(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    @ApiOperation(value = "Status Category", notes = "Status Category.\n" + swaggerInfo
            + "", response = BaseResponse.class, httpMethod = "PUT")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "category form", dataType = "temp.swaggermap.CategoryForm", required = true, paramType = "body", value = "category form") })
        public static Result setStatus(Long id) {
            Merchant ownMerchant = checkMerchantAccessAuthorization();
            if (ownMerchant != null) {
                try {
                    JsonNode json = request().body().asJson();
                    CategoryMerchantResponse request = objectMapper.readValue(json.toString(), CategoryMerchantResponse.class);
                    Transaction trx = Ebean.beginTransaction();
                    try {
                        CategoryMerchant categoryMerchant = CategoryMerchantRepository.findByIdAndMerchantId(id, ownMerchant.id);
                        if (categoryMerchant == null) {
                            response.setBaseResponse(0, 0, 0, error + " category tidak tersedia.", null);
                            return badRequest(Json.toJson(response));
                        }
                        categoryMerchant.setActive(request.getIsActive());
                        categoryMerchant.update();
    
                        trx.commit();
                        response.setBaseResponse(1, offset, 1, success + " mengubah status category", categoryMerchant);
                        return ok(Json.toJson(response));
                    } catch (Exception e) {
                        logger.error("Error saat mengubah status category", e);
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
