package controllers.categories;

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
import dtos.category.CategoryAppResponse;
import dtos.category.CategoryMerchantResponse;
import models.CategoryMerchant;
import models.Merchant;
import models.Store;
import models.SubCategoryMerchant;
import models.SubsCategoryMerchant;
import models.merchant.ProductMerchant;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import repository.CategoryMerchantRepository;
import repository.ProductMerchantRepository;
import repository.StoreRepository;
import repository.SubCategoryMerchantRepository;
import repository.SubsCategoryMerchantRepository;

import java.util.ArrayList;
import java.util.List;

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
                // Http.MultipartFormData body = request().body().asMultipartFormData();
                JsonNode json = request().body().asJson();
                CategoryMerchantResponse request = objectMapper.readValue(json.toString(), CategoryMerchantResponse.class);
                String validate = validateCreateCategory(request);
                if (validate == null) {
                    Transaction trx = Ebean.beginTransaction();
                    try {
                        if(request.getCategoryName().length() > 50) {
                            response.setBaseResponse(0, 0, 0, "Jumlah karakter tidak boleh melebihi 50 karakter", null);
                            return badRequest(Json.toJson(response));
                        }
                        CategoryMerchant newCategoryMerchant = new CategoryMerchant();
                        newCategoryMerchant.setCategoryName(request.getCategoryName());
                        newCategoryMerchant.setMerchant(ownMerchant);
                        newCategoryMerchant.setIsActive(request.getIsActive());

                        // // ========================== update with image ========================== //
                        // /*
                        // ** do the same for the save image mobile
                        //  */
                        // // for Website
                        // Http.MultipartFormData.FilePart imageFileWeb = Objects.requireNonNull(body).getFile("image_web");
                        // File imageWeb = ImageUtil.uploadImage(imageFileWeb, "category", "category-web", ImageUtil.fullImageSize, "jpg");
                        // String imageWebUrl = ImageUtil.createImageUrl("category", imageWeb != null ? imageWeb.getName() : null);

                        // // for Mobile
                        // Http.MultipartFormData.FilePart imageFileMobile = Objects.requireNonNull(body).getFile("image_mobile");
                        // File imageMobile = ImageUtil.uploadImage(imageFileMobile, "category", "category-mobile", ImageUtil.fullImageSize, "jpg");
                        // String imageMobileUrl = ImageUtil.createImageUrl("category", imageMobile != null ? imageMobile.getName() : null);
                        // // ========================== update with image ========================== //
                        newCategoryMerchant.setImageWeb(request.getImageWeb());
                        newCategoryMerchant.setImageMobile(request.getImageMobile());
                        newCategoryMerchant.save();

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

    @ApiOperation(value = "Get all category list.", notes = "Returns list of category.\n" + swaggerInfo
            + "", response = CategoryMerchant.class, responseContainer = "List", httpMethod = "GET")
    public static Result listCategory(String filter, String sort, int offset, int limit) {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            try {
                int totalData = CategoryMerchantRepository.findAllByMerchant(ownMerchant.id, sort, filter, 0, 0).size();
                List<CategoryMerchant> categoryMerchants = CategoryMerchantRepository.findAllByMerchant(ownMerchant.id, sort, filter, offset, limit);

                List<CategoryMerchantResponse> responses = listResponse(categoryMerchants, ownMerchant.id);

                response.setBaseResponse(totalData, offset, limit, "Berhasil menampilkan data", responses);
                return ok(Json.toJson(response));
            } catch (Exception e) {
                Logger.error("allDetail", e);
            }
        } else {
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
                JsonNode json = request().body().asJson();
                CategoryMerchantResponse request = objectMapper.readValue(json.toString(), CategoryMerchantResponse.class);
                String validate = validateCreateCategory(request);
                if (validate == null) {
                    Transaction trx = Ebean.beginTransaction();
                    try {
                        CategoryMerchant categoryMerchant = CategoryMerchantRepository.findByIdAndMerchantId(id, ownMerchant);
                        if (categoryMerchant == null) {
                            response.setBaseResponse(0, 0, 0, error + " kategori tidak tersedia.", null);
                            return badRequest(Json.toJson(response));
                        }
                        if(request.getCategoryName().length() > 50) {
                            response.setBaseResponse(0, 0, 0, "Jumlah karakter tidak boleh melebihi 50 karakter", null);
                            return badRequest(Json.toJson(response));
                        }
                        categoryMerchant.setCategoryName(request.getCategoryName());
                        categoryMerchant.setMerchant(ownMerchant);
                        categoryMerchant.setIsActive(request.getIsActive());

                        // // ========================== update with image ========================== //
                        // /*
                        // ** do the same for the save image mobile
                        //  */
                        // // for Website
                        // Http.MultipartFormData.FilePart imageFileWeb = Objects.requireNonNull(body).getFile("image_web");
                        // File imageWeb = ImageUtil.uploadImage(imageFileWeb, "category", "category-web", ImageUtil.fullImageSize, "jpg");
                        // String imageWebUrl = ImageUtil.createImageUrl("category", imageWeb != null ? imageWeb.getName() : null);

                        // // for Mobile
                        // Http.MultipartFormData.FilePart imageFileMobile = Objects.requireNonNull(body).getFile("image_mobile");
                        // File imageMobile = ImageUtil.uploadImage(imageFileMobile, "category", "category-mobile", ImageUtil.fullImageSize, "jpg");
                        // String imageMobileUrl = ImageUtil.createImageUrl("category", imageMobile != null ? imageMobile.getName() : null);
                        // // ========================== update with image ========================== //
                        categoryMerchant.setImageWeb(request.getImageWeb());
                        categoryMerchant.setImageMobile(request.getImageMobile());
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
                    CategoryMerchant CategoryMerchant = CategoryMerchantRepository.findByIdAndMerchantId(id, ownMerchant);
                    if (CategoryMerchant == null) {
                        response.setBaseResponse(0, 0, 0, error + " kategori tidak tersedia.", null);
                        return badRequest(Json.toJson(response));
                    }

                    List<ProductMerchant> totalData = ProductMerchantRepository.find.where().eq("category_merchant_id", CategoryMerchant.id).eq("is_deleted", Boolean.FALSE).findPagingList(0).getPage(0).getList();
                    if (totalData.size() != 0) {
                        response.setBaseResponse(0, 0, 0, "Tidak dapat menghapus kategori. " +CategoryMerchant.getCategoryName()+ " memiliki " + totalData.size() + " Produk.", null);
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
                    CategoryMerchant CategoryMerchant = CategoryMerchantRepository.findByIdAndMerchantId(id, ownMerchant);
                    if (CategoryMerchant == null) {
                        response.setBaseResponse(0, 0, 0, error + " kategori merchant tidak tersedia.", null);
                        return badRequest(Json.toJson(response));
                    }

                    CategoryMerchantResponse category = detailResponse(CategoryMerchant, ownMerchant.id);

                    response.setBaseResponse(1,offset, 1, success + " menampilkan detail kategori", category);
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
                    CategoryMerchant categoryMerchant = CategoryMerchantRepository.findByIdAndMerchantId(id, ownMerchant);
                    if (categoryMerchant == null) {
                        response.setBaseResponse(0, 0, 0, error + " category tidak tersedia.", null);
                        return badRequest(Json.toJson(response));
                    }
                    categoryMerchant.setIsActive(request.getIsActive());
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

    public static Result listCategoryApp(Long merchantId, Long storeId, String filter, String sort, int offset, int limit) {
        Store store = Store.find.byId(storeId);
        if (store == null) {
            response.setBaseResponse(0, 0, 0, "Store tidak ditemukan", null);
            return badRequest(Json.toJson(response));
        }
        try {
            int totalData = CategoryMerchantRepository.findMerchantIdWithStatus(merchantId).size();
            List<CategoryMerchant> categoryMerchants = CategoryMerchantRepository.findMerchantIdWithStatus(merchantId);

            List<CategoryAppResponse> responses = new ArrayList<>();

            for (CategoryMerchant data : categoryMerchants) {
                CategoryAppResponse response = new CategoryAppResponse();
                response.setId(data.id);
                response.setCategoryName(data.categoryName);
                response.setImageMobile(data.imageMobile);
                response.setImageWeb(data.imageWeb);
                responses.add(response);
            }

            response.setBaseResponse(totalData, offset, limit, "Berhasil menampilkan data", responses);
            return ok(Json.toJson(response));
        } catch (Exception e) {
            Logger.error("allDetail", e);
        }
        response.setBaseResponse(0, 0, 0, "Merchant tidak ditemukan", null);
        return badRequest(Json.toJson(response));
    }

    public static List<CategoryMerchantResponse> listResponse(List<CategoryMerchant> categoryMerchants, Long merchantId) {
        List<CategoryMerchantResponse> responses = new ArrayList<>();
        for (CategoryMerchant category : categoryMerchants) {
            int totalCategoryData = CategoryMerchantRepository.getTotalProductCategory(merchantId, category.id);
            CategoryMerchantResponse categoryRes = new CategoryMerchantResponse(category);
            categoryRes.setTotalProduct(totalCategoryData);

            List<SubCategoryMerchant> subCategoryMerchants = SubCategoryMerchantRepository.findAllByMerchantAndCategory(merchantId, category.id);

            List<CategoryMerchantResponse.SubCategoryMerchantResponse> subResponses = new ArrayList<>();
            for(SubCategoryMerchant subCategory : subCategoryMerchants) {
                int totalProductSubCategory = CategoryMerchantRepository.getTotalProductSubCategory(merchantId, subCategory.id);
                CategoryMerchantResponse.SubCategoryMerchantResponse subRes = new CategoryMerchantResponse.SubCategoryMerchantResponse(subCategory);
                subRes.setTotalProduct(totalProductSubCategory);

                List<SubsCategoryMerchant> subsCategoryMerchants = SubsCategoryMerchantRepository.findAllByMerchantAndSubCategory(merchantId, subCategory.id);

                List<CategoryMerchantResponse.SubCategoryMerchantResponse.SubsCategoryMerchantResponse> subsResponses = new ArrayList<>();
                for(SubsCategoryMerchant subsCategory : subsCategoryMerchants) {
                    int totalProductSubsCategory = CategoryMerchantRepository.getTotalProductSubsCategory(merchantId, subsCategory.id);
                    CategoryMerchantResponse.SubCategoryMerchantResponse.SubsCategoryMerchantResponse subsRes = new CategoryMerchantResponse.SubCategoryMerchantResponse.SubsCategoryMerchantResponse(subsCategory);
                    subsRes.setTotalProduct(totalProductSubsCategory);

                    subsResponses.add(subsRes);
                    subRes.setSubsCategory(subsRes != null ? subsResponses : null);
                }

                subResponses.add(subRes);
                categoryRes.setSubCategory(subRes != null ? subResponses : null);
            }

            responses.add(categoryRes);
        }

        return responses;
    }

    public static CategoryMerchantResponse detailResponse(CategoryMerchant category, Long merchantId) {
        CategoryMerchantResponse categoryRes = new CategoryMerchantResponse(category);

        List<SubCategoryMerchant> subCategoryMerchants = SubCategoryMerchantRepository.findAllByMerchantAndCategory(merchantId, category.id);

        List<CategoryMerchantResponse.SubCategoryMerchantResponse> subResponses = new ArrayList<>();
        for (SubCategoryMerchant subCategory : subCategoryMerchants) {
            CategoryMerchantResponse.SubCategoryMerchantResponse subRes = new CategoryMerchantResponse.SubCategoryMerchantResponse(subCategory);

            List<SubsCategoryMerchant> subsCategoryMerchants = SubsCategoryMerchantRepository.findAllByMerchantAndSubCategory(merchantId, subCategory.id);

            List<CategoryMerchantResponse.SubCategoryMerchantResponse.SubsCategoryMerchantResponse> subsResponses = new ArrayList<>();
            for (SubsCategoryMerchant subsCategory : subsCategoryMerchants) {
                CategoryMerchantResponse.SubCategoryMerchantResponse.SubsCategoryMerchantResponse subsRes = new CategoryMerchantResponse.SubCategoryMerchantResponse.SubsCategoryMerchantResponse(subsCategory);

                subsResponses.add(subsRes);
                subRes.setSubsCategory(subsRes != null ? subsResponses : null);
            }

            subResponses.add(subRes);
            categoryRes.setSubCategory(subRes != null ? subResponses : null);
        }

        return categoryRes;
    }

    public static String validateCreateCategory(CategoryMerchantResponse request) {
        if (request == null)
            return "Bidang tidak boleh nol atau kosong";
        if (request.getCategoryName() == null)
            return "Nama Category tidak boleh nol atau kosong";

        return null;
    }

}
