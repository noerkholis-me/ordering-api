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
import models.Photo;
import models.CategoryMerchant;
import models.SubCategoryMerchant;
import models.SubsCategoryMerchant;
import play.Logger;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Http;
import play.mvc.Result;
import repository.CategoryMerchantRepository;
import repository.SubCategoryMerchantRepository;
import repository.SubsCategoryMerchantRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import repository.ProductMerchantRepository;
import models.merchant.ProductMerchant;

import java.io.File;
import java.util.*;
import com.avaje.ebean.Query;
import utils.ImageDirectory;
import utils.ImageUtil;

import java.io.IOException;

@Api(value = "/merchants/subcategory", description = "Sub Category Merchant")
public class SubCategoryMerchantController extends BaseController {

    private final static Logger.ALogger logger = Logger.of(SubCategoryMerchantController.class);

    private static BaseResponse response = new BaseResponse();

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Result createSubCategory(Long id) {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            Query<SubCategoryMerchant> query = SubCategoryMerchantRepository.find.where().eq("t0.category_id", id).eq("t0.is_deleted", false).eq("merchant", ownMerchant).order("t0.id");
            try {
                Http.MultipartFormData body = request().body().asMultipartFormData();
                List<SubCategoryMerchant> totalData = SubCategoryMerchantRepository.getTotalSubCategory(query);
                // if(totalData.size() == 2) {
                //     response.setBaseResponse(0, 0, 0, error + " sub kategori sudah mencapai batas maksimum.", null);
                //     return badRequest(Json.toJson(response));
                // }
                CategoryMerchant categoryMerchant = CategoryMerchantRepository.findByIdAndMerchantId(id, ownMerchant);
                if(categoryMerchant == null) {
                    response.setBaseResponse(0, 0, 0, error + " kategori tidak ditemukan.", null);
                    return badRequest(Json.toJson(response));
                }
                JsonNode json = null;
                if (body != null) {
                    Map<String, String[]> data = body.asFormUrlEncoded();
                    if (data != null) {
                        json = Json.parse(data.get("data")[0]);
                    }
                } else {
                    json = request().body().asJson();
                }
                SubCategoryMerchantResponse request = objectMapper.readValue(json.toString(), SubCategoryMerchantResponse.class);
                String validate = validateCreateSubCategory(request);
                SubCategoryMerchant latestSubCat = SubCategoryMerchantRepository.getLatestSequence(ownMerchant);
                int lastSequence = 0;
                if(latestSubCat == null) {
                    lastSequence = lastSequence + 1;
                } else {
                    lastSequence = latestSubCat.getSequence() + 1;
                }
                if (validate == null) {
                    Transaction trx = Ebean.beginTransaction();
                    try {
                        if(request.getSubcategoryName().length() > 50) {
                            response.setBaseResponse(0, 0, 0, "Jumlah karakter tidak boleh melebihi 50 karakter", null);
                            return badRequest(Json.toJson(response));
                        }
                        SubCategoryMerchant newSubCategoryMerchant = new SubCategoryMerchant();
                        newSubCategoryMerchant.setSubcategoryName(request.getSubcategoryName());
                        newSubCategoryMerchant.setMerchant(ownMerchant);
                        newSubCategoryMerchant.setCategoryMerchant(categoryMerchant);
                        newSubCategoryMerchant.setActive(request.getIsActive());
                        newSubCategoryMerchant.setSequence(lastSequence);

                        // // ========================== update with image ========================== //
                        // /*
                        // ** do the same for the save image mobile
                        //  */
                        // // for Website
                        // Http.MultipartFormData.FilePart imageFileWeb = Objects.requireNonNull(body).getFile("image_web");
                        // File imageWeb = ImageUtil.uploadImage(imageFileWeb, "subcategory", "subcategory-web", ImageUtil.fullImageSize, "jpg");
                        // String imageWebUrl = ImageUtil.createImageUrl("subcategory", imageWeb != null ? imageWeb.getName() : null);

                        // // for Mobile
                        // Http.MultipartFormData.FilePart imageFileMobile = Objects.requireNonNull(body).getFile("image_mobile");
                        // File imageMobile = ImageUtil.uploadImage(imageFileMobile, "subcategory", "subcategory-mobile", ImageUtil.fullImageSize, "jpg");
                        // String imageMobileUrl = ImageUtil.createImageUrl("subcategory", imageMobile != null ? imageMobile.getName() : null);
                        // // ========================== update with image ========================== //
                        newSubCategoryMerchant.setImageWeb(request.getImageWeb());
                        newSubCategoryMerchant.setImageMobile(request.getImageMobile());
                        newSubCategoryMerchant.save();

                        trx.commit();
                        response.setBaseResponse(1, offset, 1, success + " membuat sub kategori", newSubCategoryMerchant);
                        return ok(Json.toJson(response));
                    } catch (Exception e) {
                        logger.error("Error saat membuat sub kategori", e);
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


    public static String validateCreateSubCategory(SubCategoryMerchantResponse request) {
        if (request == null)
            return "Bidang tidak boleh nol atau kosong";
        if (request.getSubcategoryName() == null)
            return "Nama Sub Category tidak boleh nol atau kosong";

        return null;
    }

    public static Result editSubCategory(Long id) {
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
                SubCategoryMerchantResponse request = objectMapper.readValue(json.toString(), SubCategoryMerchantResponse.class);
                String validate = validateCreateSubCategory(request);
                if (validate == null) {
                    Transaction trx = Ebean.beginTransaction();
                    try {
                        SubCategoryMerchant subCategoryMerchant = SubCategoryMerchantRepository.findByIdAndMerchantId(id, ownMerchant);
                        if (subCategoryMerchant == null) {
                            response.setBaseResponse(0, 0, 0, error + " sub kategori tidak tersedia.", null);
                            return badRequest(Json.toJson(response));
                        }
                        CategoryMerchant categoryMerchant = CategoryMerchantRepository.findByIdAndMerchantId(subCategoryMerchant.getCategoryMerchant().id, ownMerchant);
                        if(categoryMerchant == null) {
                            response.setBaseResponse(0, 0, 0, error + " kategori tidak ditemukan.", null);
                            return badRequest(Json.toJson(response));
                        }

                        if(request.getSubcategoryName().length() > 50) {
                            response.setBaseResponse(0, 0, 0, "Jumlah karakter tidak boleh melebihi 50 karakter", null);
                            return badRequest(Json.toJson(response));
                        }
                        subCategoryMerchant.setSubcategoryName(request.getSubcategoryName());
                        subCategoryMerchant.setMerchant(ownMerchant);
                        subCategoryMerchant.setCategoryMerchant(categoryMerchant);
                        subCategoryMerchant.setActive(request.getIsActive());

                        // // ========================== update with image ========================== //
                        // /*
                        // ** do the same for the save image mobile
                        //  */
                        // // for Website
                        // Http.MultipartFormData.FilePart imageFileWeb = Objects.requireNonNull(body).getFile("image_web");
                        // File imageWeb = ImageUtil.uploadImage(imageFileWeb, "subcategory", "subcategory-web", ImageUtil.fullImageSize, "jpg");
                        // String imageWebUrl = ImageUtil.createImageUrl("subcategory", imageWeb != null ? imageWeb.getName() : null);

                        // // for Mobile
                        // Http.MultipartFormData.FilePart imageFileMobile = Objects.requireNonNull(body).getFile("image_mobile");
                        // File imageMobile = ImageUtil.uploadImage(imageFileMobile, "subcategory", "subcategory-mobile", ImageUtil.fullImageSize, "jpg");
                        // String imageMobileUrl = ImageUtil.createImageUrl("subcategory", imageMobile != null ? imageMobile.getName() : null);
                        // // ========================== update with image ========================== //
                        if(request.getImageWeb() != null){
                            subCategoryMerchant.setImageWeb(request.getImageWeb());
                        }
                        if(request.getImageMobile() != null){
                            subCategoryMerchant.setImageMobile(request.getImageMobile());
                        }
                        subCategoryMerchant.update();

                        trx.commit();
                        response.setBaseResponse(1, offset, 1, success + " mengubah sub kategori", subCategoryMerchant);
                        return ok(Json.toJson(response));
                    } catch (Exception e) {
                        logger.error("Error saat mengubah sub kategori", e);
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

    public static Result deleteSubCategory(Long id) {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
                if (id != null) {
                    Transaction trx = Ebean.beginTransaction();
                    try {
                        SubCategoryMerchant subCategoryMerchant = SubCategoryMerchantRepository.findByIdAndMerchantId(id, ownMerchant);
                        if (subCategoryMerchant == null) {
                            response.setBaseResponse(0, 0, 0, error + " sub kategori tidak tersedia.", null);
                            return badRequest(Json.toJson(response));
                        }

                        List<ProductMerchant> totalData = ProductMerchantRepository.find.where().eq("sub_category_merchant_id", subCategoryMerchant.id).eq("is_deleted", Boolean.FALSE).findPagingList(0).getPage(0).getList();
                        if (totalData.size() != 0) {
                            response.setBaseResponse(0, 0, 0, "Tidak dapat menghapus sub kategori. " +subCategoryMerchant.getSubcategoryName()+ " memiliki " + totalData.size() + " Produk.", null);
                            return badRequest(Json.toJson(response));
                        }

                        subCategoryMerchant.isDeleted = true;
                        subCategoryMerchant.update();
                        trx.commit();

                        response.setBaseResponse(1,offset, 1, success + " menghapus sub kategori", subCategoryMerchant);
                        return ok(Json.toJson(response));
                    } catch (Exception e) {
                        logger.error("Error saat menghapus sub kategori", e);
                        e.printStackTrace();
                        trx.rollback();
                    } finally {
                        trx.end();
                    }
                    response.setBaseResponse(0, 0, 0, error, null);
                    return badRequest(Json.toJson(response));
                }
                response.setBaseResponse(0, 0, 0, "Tidak dapat menemukan sub kategori id", null);
                return badRequest(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result viewSubCategory(Long id) {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            if (id != null) {
                Transaction trx = Ebean.beginTransaction();
                try {
                    SubCategoryMerchant subCategoryMerchant = SubCategoryMerchantRepository.findByIdAndMerchantId(id, ownMerchant);
                    if (subCategoryMerchant == null) {
                        response.setBaseResponse(0, 0, 0, error + " sub kategori merchant tidak tersedia.", null);
                        return badRequest(Json.toJson(response));
                    }
                    SubCategoryMerchantResponse subCategoryMerchantResponse = new SubCategoryMerchantResponse();
                    subCategoryMerchantResponse.setId(subCategoryMerchant.id);
                    subCategoryMerchantResponse.setSubcategoryName(subCategoryMerchant.getSubcategoryName());
                    subCategoryMerchantResponse.setImageWeb(subCategoryMerchant.getImageWeb());
                    subCategoryMerchantResponse.setImageMobile(subCategoryMerchant.getImageMobile());
                    subCategoryMerchantResponse.setIsDeleted(subCategoryMerchant.isDeleted);
                    subCategoryMerchantResponse.setIsActive(subCategoryMerchant.isActive);
                    subCategoryMerchantResponse.setCategoryId(subCategoryMerchant.getCategoryMerchant().id);
                    subCategoryMerchantResponse.setMerchantId(subCategoryMerchant.getMerchant().id);

                    response.setBaseResponse(1,offset, 1, success + " menampilkan detail sub kategori", subCategoryMerchantResponse);
                    return ok(Json.toJson(response));
                } catch (Exception e) {
                    logger.error("Error saat menampilkan detail sub kategori", e);
                    e.printStackTrace();
                    trx.rollback();
                } finally {
                    trx.end();
                }
                response.setBaseResponse(0, 0, 0, error, null);
                return badRequest(Json.toJson(response));
            }
            response.setBaseResponse(0, 0, 0, "Tidak dapat menemukan sub kategori id", null);
            return badRequest(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result listSubCategory(String filter, String sort, int offset, int limit) {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            Query<SubCategoryMerchant> query = SubCategoryMerchantRepository.find.where().eq("t0.is_deleted", false).eq("t0.merchant", ownMerchant).order("t0.id");
            try {
                List<SubCategoryMerchantResponse> responses = new ArrayList<>();
                List<SubCategoryMerchant> totalData = SubCategoryMerchantRepository.getTotalData(query);
                List<SubCategoryMerchant> responseIndex = SubCategoryMerchantRepository.getDataSubCategory(query, sort, filter, offset, limit);
                for (SubCategoryMerchant data : responseIndex) {
                    SubCategoryMerchantResponse response = new SubCategoryMerchantResponse();
                    response.setId(data.id);
                    response.setSubcategoryName(data.getSubcategoryName());
                    response.setImageWeb(data.getImageWeb());
                    response.setImageMobile(data.getImageMobile());
                    response.setIsDeleted(data.isDeleted);
                    response.setIsActive(data.isActive);
                    response.setSequence(data.getSequence());
                    response.setMerchantId(data.getMerchant().id);
                    response.setCategoryId(data.getCategoryMerchant().id);
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

    public static Result listSequence() {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            Query<SubsCategoryMerchant> query = SubsCategoryMerchantRepository.find.where().eq("t0.is_deleted", false).eq("t0.merchant", ownMerchant).order("t0.id");
            try {
                List<SubsCategoryMerchantResponse> responses = new ArrayList<>();
                List<SubsCategoryMerchant> responseIndex = SubsCategoryMerchantRepository.getListSequence(ownMerchant);
                for (SubsCategoryMerchant data : responseIndex) {
                    SubsCategoryMerchantResponse response = new SubsCategoryMerchantResponse();
                    response.setId(data.id);
                    response.setSubscategoryName(data.getSubscategoryName());
                    response.setImageWeb(data.getImageWeb());
                    response.setImageMobile(data.getImageMobile());
                    response.setIsDeleted(data.isDeleted);
                    response.setIsActive(data.isActive());
                    response.setSequence(data.getSequence());
                    response.setMerchantId(data.getMerchant().id);
                    response.setCategoryId(data.getCategoryMerchant().id);
                    response.setSubCategoryId(data.getSubCategoryMerchant().id);
                    responses.add(response);
                }
                response.setBaseResponse(responseIndex.size() , offset, limit, success + " menampilkan data", responses);
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

    public static Result updateSequence() {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            try {
                JsonNode json = request().body().asJson();
                List<SubsCategoryMerchantResponse> request = objectMapper.convertValue(json, new TypeReference<List<SubsCategoryMerchantResponse>>(){});
                    Transaction trx = Ebean.beginTransaction();
                    try {

                        List<SubsCategoryMerchant> responses = new ArrayList<>();
                        SubsCategoryMerchant subsCategoryMerchant = new SubsCategoryMerchant();
                        for (SubsCategoryMerchantResponse data : request){
                            subsCategoryMerchant = SubsCategoryMerchantRepository.findByIdAndMerchantId(data.id, ownMerchant);
                            if (subsCategoryMerchant == null) {
                                response.setBaseResponse(0, 0, 0, error + " sub kategori tidak tersedia.", null);
                                return badRequest(Json.toJson(response));
                            }
                            subsCategoryMerchant.setSequence(data.sequence);
                            
                            subsCategoryMerchant.update();
                            responses.add(subsCategoryMerchant);
                        }

                        trx.commit();
                        response.setBaseResponse(1, offset, 1, success + " mengubah sub kategori", responses);
                        return ok(Json.toJson(response));
                    } catch (Exception e) {
                        logger.error("Error saat mengubah sub kategori", e);
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

    @ApiOperation(value = "Status Sub Category", notes = "Status Sub Category.\n" + swaggerInfo
            + "", response = BaseResponse.class, httpMethod = "PUT")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "sub category form", dataType = "temp.swaggermap.SubCategoryForm", required = true, paramType = "body", value = "sub category form") })
        public static Result setStatus(Long id) {
            Merchant ownMerchant = checkMerchantAccessAuthorization();
            if (ownMerchant != null) {
                try {
                    JsonNode json = request().body().asJson();
                    SubCategoryMerchantResponse request = objectMapper.readValue(json.toString(), SubCategoryMerchantResponse.class);
                    Transaction trx = Ebean.beginTransaction();
                    try {
                        SubCategoryMerchant subCategoryMerchant = SubCategoryMerchantRepository.findByIdAndMerchantId(id, ownMerchant);
                        if (subCategoryMerchant == null) {
                            response.setBaseResponse(0, 0, 0, error + " sub category tidak tersedia.", null);
                            return badRequest(Json.toJson(response));
                        }
                        subCategoryMerchant.setActive(request.getIsActive());
                        subCategoryMerchant.update();
    
                        trx.commit();
                        response.setBaseResponse(1, offset, 1, success + " mengubah status sub category", subCategoryMerchant);
                        return ok(Json.toJson(response));
                    } catch (Exception e) {
                        logger.error("Error saat mengubah status sub category", e);
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

        // SUB CATEGORY LEVEL 3
        public static Result createSubsCategory(Long catId, Long id) {
            Merchant ownMerchant = checkMerchantAccessAuthorization();
            if (ownMerchant != null) {
                Query<SubsCategoryMerchant> query = SubsCategoryMerchantRepository.find.where().eq("t0.subcategory_id", id).eq("t0.is_deleted", false).eq("merchant", ownMerchant).order("t0.id");
                try {
                    Http.MultipartFormData body = request().body().asMultipartFormData();
                    List<SubsCategoryMerchant> totalData = SubsCategoryMerchantRepository.getTotalSubsCategory(query);
                    // if(totalData.size() == 2) {
                    //     response.setBaseResponse(0, 0, 0, error + " sub kategori sudah mencapai batas maksimum.", null);
                    //     return badRequest(Json.toJson(response));
                    // }
                    CategoryMerchant categoryMerchant = CategoryMerchantRepository.findByIdAndMerchantId(catId, ownMerchant);
                    if(categoryMerchant == null) {
                        response.setBaseResponse(0, 0, 0, error + " sub kategori tidak ditemukan.", null);
                        return badRequest(Json.toJson(response));
                    }
                    SubCategoryMerchant subcategoryMerchant = SubCategoryMerchantRepository.findByIdAndMerchantId(id, ownMerchant);
                    if(subcategoryMerchant == null) {
                        response.setBaseResponse(0, 0, 0, error + " sub kategori tidak ditemukan.", null);
                        return badRequest(Json.toJson(response));
                    }
                    JsonNode json = request().body().asJson();

                    SubsCategoryMerchantResponse request = objectMapper.readValue(json.toString(), SubsCategoryMerchantResponse.class);
                    String validate = validateCreateSubsCategory(request);
                    SubsCategoryMerchant latestSubsCat = SubsCategoryMerchantRepository.getLatestSequence(ownMerchant);
                    int lastSequence = 0;
                    if(latestSubsCat == null) {
                        lastSequence = lastSequence + 1;
                    } else {
                        lastSequence = latestSubsCat.getSequence() + 1;
                    }
                    if (validate == null) {
                        Transaction trx = Ebean.beginTransaction();
                        try {
                            if(request.getSubscategoryName().length() > 50) {
                                response.setBaseResponse(0, 0, 0, "Jumlah karakter tidak boleh melebihi 50 karakter", null);
                                return badRequest(Json.toJson(response));
                            }
                            SubsCategoryMerchant newSubsCategoryMerchant = new SubsCategoryMerchant();
                            newSubsCategoryMerchant.setSubscategoryName(request.getSubscategoryName());
                            newSubsCategoryMerchant.setMerchant(ownMerchant);
                            newSubsCategoryMerchant.setCategoryMerchant(categoryMerchant);
                            newSubsCategoryMerchant.setSubCategoryMerchant(subcategoryMerchant);
                            newSubsCategoryMerchant.setActive(request.getIsActive());
                            newSubsCategoryMerchant.setSequence(lastSequence);
                            newSubsCategoryMerchant.setImageWeb(request.getImageWeb());
                            newSubsCategoryMerchant.setImageMobile(request.getImageMobile());
                            newSubsCategoryMerchant.save();
    
                            trx.commit();
                            response.setBaseResponse(1, offset, 1, success + " membuat sub kategori", newSubsCategoryMerchant);
                            return ok(Json.toJson(response));
                        } catch (Exception e) {
                            logger.error("Error saat membuat sub kategori", e);
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

        public static Result editSubsCategory(Long id) {
            Merchant ownMerchant = checkMerchantAccessAuthorization();
            if (ownMerchant != null) {
                try {
                    JsonNode json = request().body().asJson();
                    
                    SubsCategoryMerchantResponse request = objectMapper.readValue(json.toString(), SubsCategoryMerchantResponse.class);
                    String validate = validateCreateSubsCategory(request);
                    if (validate == null) {
                        Transaction trx = Ebean.beginTransaction();
                        try {
                            SubsCategoryMerchant subsCategoryMerchant = SubsCategoryMerchantRepository.findByIdAndMerchantId(id, ownMerchant);
                            if (subsCategoryMerchant == null) {
                                response.setBaseResponse(0, 0, 0, error + " subs kategori tidak tersedia.", null);
                                return badRequest(Json.toJson(response));
                            }
                            CategoryMerchant categoryMerchant = CategoryMerchantRepository.findByIdAndMerchantId(subsCategoryMerchant.getCategoryMerchant().id, ownMerchant);
                            if(categoryMerchant == null) {
                                response.setBaseResponse(0, 0, 0, error + " kategori tidak ditemukan.", null);
                                return badRequest(Json.toJson(response));
                            }
                            SubCategoryMerchant subCategoryMerchant = SubCategoryMerchantRepository.findByIdAndMerchantId(subsCategoryMerchant.getSubCategoryMerchant().id, ownMerchant);
                            if (subCategoryMerchant == null) {
                                response.setBaseResponse(0, 0, 0, error + " sub kategori tidak tersedia.", null);
                                return badRequest(Json.toJson(response));
                            }
                            if(request.getSubscategoryName().length() > 50) {
                                response.setBaseResponse(0, 0, 0, "Jumlah karakter tidak boleh melebihi 50 karakter", null);
                                return badRequest(Json.toJson(response));
                            }
                            subsCategoryMerchant.setSubscategoryName(request.getSubscategoryName());
                            subsCategoryMerchant.setMerchant(ownMerchant);
                            subsCategoryMerchant.setCategoryMerchant(categoryMerchant);
                            subsCategoryMerchant.setSubCategoryMerchant(subCategoryMerchant);
                            subsCategoryMerchant.setActive(request.getIsActive());
                            if(request.getImageWeb() != null){
                                subsCategoryMerchant.setImageWeb(request.getImageWeb());
                            }
                            if(request.getImageMobile() != null){
                                subsCategoryMerchant.setImageMobile(request.getImageMobile());
                            }
                            subsCategoryMerchant.update();
    
                            trx.commit();
                            response.setBaseResponse(1, offset, 1, success + " mengubah sub kategori", subsCategoryMerchant);
                            return ok(Json.toJson(response));
                        } catch (Exception e) {
                            logger.error("Error saat mengubah sub kategori", e);
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

        public static Result setStatusSubsCategory(Long id) {
            Merchant ownMerchant = checkMerchantAccessAuthorization();
            if (ownMerchant != null) {
                try {
                    JsonNode json = request().body().asJson();
                    
                    SubsCategoryMerchantResponse request = objectMapper.readValue(json.toString(), SubsCategoryMerchantResponse.class);

                    Transaction trx = Ebean.beginTransaction();
                    try {
                        SubsCategoryMerchant subsCategoryMerchant = SubsCategoryMerchantRepository.findByIdAndMerchantId(id, ownMerchant);
                        if (subsCategoryMerchant == null) {
                            response.setBaseResponse(0, 0, 0, error + " subs kategori tidak tersedia.", null);
                            return badRequest(Json.toJson(response));
                        }
                        CategoryMerchant categoryMerchant = CategoryMerchantRepository.findByIdAndMerchantId(subsCategoryMerchant.getCategoryMerchant().id, ownMerchant);
                        if(categoryMerchant == null) {
                            response.setBaseResponse(0, 0, 0, error + " kategori tidak ditemukan.", null);
                            return badRequest(Json.toJson(response));
                        }
                        SubCategoryMerchant subCategoryMerchant = SubCategoryMerchantRepository.findByIdAndMerchantId(subsCategoryMerchant.getSubCategoryMerchant().id, ownMerchant);
                        if (subCategoryMerchant == null) {
                            response.setBaseResponse(0, 0, 0, error + " sub kategori tidak tersedia.", null);
                            return badRequest(Json.toJson(response));
                        }
                        subsCategoryMerchant.setActive(request.getIsActive());
                        subsCategoryMerchant.update();

                        trx.commit();
                        response.setBaseResponse(1, offset, 1, success + " mengubah sub kategori", subsCategoryMerchant);
                        return ok(Json.toJson(response));
                    } catch (Exception e) {
                        logger.error("Error saat mengubah sub kategori", e);
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
    
        public static Result deleteSubsCategory(Long id) {
            Merchant ownMerchant = checkMerchantAccessAuthorization();
            if (ownMerchant != null) {
                    if (id != null) {
                        Transaction trx = Ebean.beginTransaction();
                        try {
                            SubsCategoryMerchant subsCategoryMerchant = SubsCategoryMerchantRepository.findByIdAndMerchantId(id, ownMerchant);
                            if (subsCategoryMerchant == null) {
                                response.setBaseResponse(0, 0, 0, error + " sub kategori tidak tersedia.", null);
                                return badRequest(Json.toJson(response));
                            }

                            List<ProductMerchant> totalData = ProductMerchantRepository.find.where().eq("subs_category_merchant_id", subsCategoryMerchant.id).eq("is_deleted", Boolean.FALSE).findPagingList(0).getPage(0).getList();
                            if (totalData.size() != 0) {
                                response.setBaseResponse(0, 0, 0, "Tidak dapat menghapus sub kategori. " +subsCategoryMerchant.getSubscategoryName()+ " memiliki " + totalData.size() + " Produk.", null);
                                return badRequest(Json.toJson(response));
                            }
    
                            subsCategoryMerchant.isDeleted = true;
                            subsCategoryMerchant.update();
                            trx.commit();
    
                            response.setBaseResponse(1,offset, 1, success + " menghapus sub kategori", subsCategoryMerchant);
                            return ok(Json.toJson(response));
                        } catch (Exception e) {
                            logger.error("Error saat menghapus sub kategori", e);
                            e.printStackTrace();
                            trx.rollback();
                        } finally {
                            trx.end();
                        }
                        response.setBaseResponse(0, 0, 0, error, null);
                        return badRequest(Json.toJson(response));
                    }
                    response.setBaseResponse(0, 0, 0, "Tidak dapat menemukan sub kategori id", null);
                    return badRequest(Json.toJson(response));
            }
            response.setBaseResponse(0, 0, 0, unauthorized, null);
            return unauthorized(Json.toJson(response));
        }
    
        public static Result viewSubsCategory(Long id) {
            Merchant ownMerchant = checkMerchantAccessAuthorization();
            if (ownMerchant != null) {
                if (id != null) {
                    Transaction trx = Ebean.beginTransaction();
                    try {
                        SubsCategoryMerchant subsCategoryMerchant = SubsCategoryMerchantRepository.findByIdAndMerchantId(id, ownMerchant);
                        if (subsCategoryMerchant == null) {
                            response.setBaseResponse(0, 0, 0, error + " sub kategori merchant tidak tersedia.", null);
                            return badRequest(Json.toJson(response));
                        }
                        SubsCategoryMerchantResponse subsCategoryMerchantResponse = new SubsCategoryMerchantResponse();
                        subsCategoryMerchantResponse.setId(subsCategoryMerchant.id);
                        subsCategoryMerchantResponse.setSubscategoryName(subsCategoryMerchant.getSubscategoryName());
                        subsCategoryMerchantResponse.setImageWeb(subsCategoryMerchant.getImageWeb());
                        subsCategoryMerchantResponse.setImageMobile(subsCategoryMerchant.getImageMobile());
                        subsCategoryMerchantResponse.setIsDeleted(subsCategoryMerchant.isDeleted);
                        subsCategoryMerchantResponse.setIsActive(subsCategoryMerchant.isActive);
                        subsCategoryMerchantResponse.setSequence(subsCategoryMerchant.getSequence());
                        subsCategoryMerchantResponse.setCategoryId(subsCategoryMerchant.getCategoryMerchant().id);
                        subsCategoryMerchantResponse.setSubCategoryId(subsCategoryMerchant.getSubCategoryMerchant().id);
                        subsCategoryMerchantResponse.setMerchantId(subsCategoryMerchant.getMerchant().id);
    
                        response.setBaseResponse(1,offset, 1, success + " menampilkan detail sub kategori", subsCategoryMerchantResponse);
                        return ok(Json.toJson(response));
                    } catch (Exception e) {
                        logger.error("Error saat menampilkan detail sub kategori", e);
                        e.printStackTrace();
                        trx.rollback();
                    } finally {
                        trx.end();
                    }
                    response.setBaseResponse(0, 0, 0, error, null);
                    return badRequest(Json.toJson(response));
                }
                response.setBaseResponse(0, 0, 0, "Tidak dapat menemukan sub kategori id", null);
                return badRequest(Json.toJson(response));
            }
            response.setBaseResponse(0, 0, 0, unauthorized, null);
            return unauthorized(Json.toJson(response));
        }

        public static Result listSubsCategory(String sort, String filter, int offset, int limit) {
            Merchant ownMerchant = checkMerchantAccessAuthorization();
            if (ownMerchant != null) {
                Transaction trx = Ebean.beginTransaction();
                try {
                    Query<SubsCategoryMerchant> query  = SubsCategoryMerchantRepository.find.where().eq("t0.is_deleted", false).eq("t0.is_active", true).eq("merchant", ownMerchant).order("t0.id asc");
                    List<SubsCategoryMerchant> subsCategoryMerchant = SubsCategoryMerchantRepository.getDataSubsCategory(query, sort, filter, offset, limit);
                    List<SubsCategoryMerchantResponse> subsCategoryMerchantList = new ArrayList<>();

                    for(SubsCategoryMerchant data : subsCategoryMerchant){
                        SubsCategoryMerchantResponse subsCategoryMerchantResponse = new SubsCategoryMerchantResponse();
                        subsCategoryMerchantResponse.setId(data.id);
                        subsCategoryMerchantResponse.setSubscategoryName(data.getSubscategoryName());
                        subsCategoryMerchantResponse.setImageWeb(data.getImageWeb());
                        subsCategoryMerchantResponse.setImageMobile(data.getImageMobile());
                        subsCategoryMerchantResponse.setIsDeleted(data.isDeleted);
                        subsCategoryMerchantResponse.setIsActive(data.isActive);
                        subsCategoryMerchantResponse.setSequence(data.getSequence());
                        subsCategoryMerchantResponse.setCategoryId(data.getCategoryMerchant().id);
                        subsCategoryMerchantResponse.setSubCategoryId(data.getSubCategoryMerchant().id);
                        subsCategoryMerchantResponse.setMerchantId(data.getMerchant().id);
                        subsCategoryMerchantList.add(subsCategoryMerchantResponse);
                    }
    
                    response.setBaseResponse(1,offset, 1, success + " menampilkan subs kategori", subsCategoryMerchantList);
                    return ok(Json.toJson(response));
                } catch (Exception e) {
                    logger.error("Error saat menampilkan data subs kategori", e);
                    e.printStackTrace();
                    trx.rollback(); 
                } finally {
                    trx.end();
                }
                response.setBaseResponse(0, 0, 0, error, null);
                return badRequest(Json.toJson(response));
            }
            response.setBaseResponse(0, 0, 0, unauthorized, null);
            return unauthorized(Json.toJson(response));
        }

        public static String validateCreateSubsCategory(SubsCategoryMerchantResponse request) {
            if (request == null)
                return "Bidang tidak boleh nol atau kosong";
            if (request.getSubscategoryName() == null)
                return "Nama Sub Category tidak boleh nol atau kosong";
    
            return null;
        }

}