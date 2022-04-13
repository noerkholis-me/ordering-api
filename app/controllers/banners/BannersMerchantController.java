package controllers.banners;

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
import dtos.banners.*;
import models.Merchant;
import models.Banners;
import models.Photo;
import play.Logger;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Http;
import play.mvc.Result;
import repository.BannersRepository;

import java.io.File;
import java.util.*;
import com.avaje.ebean.Query;
import utils.ImageDirectory;
import utils.ImageUtil;

import java.io.IOException;

import java.sql.Timestamp;

@Api(value = "/merchants/banners", description = "Banners Merchant")
public class BannersMerchantController extends BaseController {

    private final static Logger.ALogger logger = Logger.of(BannersMerchantController.class);

    private static BaseResponse response = new BaseResponse();

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @ApiOperation(value = "Create Banners", notes = "Create Banners.\n" + swaggerInfo
            + "", response = BaseResponse.class, httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "banners form", dataType = "temp.swaggermap.BannersForm", required = true, paramType = "body", value = "banners form") })
    public static Result createBanners() {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            try {
                JsonNode json = request().body().asJson();
                BannersRequest request = objectMapper.readValue(json.toString(), BannersRequest.class);
                String validate = validateCreateBanners(request);
                if (validate == null) {
                    Transaction trx = Ebean.beginTransaction();
                    try {
                        Banners newBanners = new Banners();
                        newBanners.setBannerName(request.getBannerName());
                        newBanners.setBannerImageWeb(request.getBannerImageWeb());
                        newBanners.setBannerImageMobile(request.getBannerImageMobile());
                        newBanners.setMerchant(ownMerchant);
                        newBanners.setActive(request.isActive());
                        newBanners.setDateFrom(request.getDateFrom());
                        newBanners.setDateTo(request.getDateTo());
                        newBanners.save();

                        trx.commit();
                        response.setBaseResponse(1, offset, 1, success + " membuat banner", newBanners);
                        return ok(Json.toJson(response));
                    } catch (Exception e) {
                        logger.error("Error saat membuat banner", e);
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


    public static String validateCreateBanners(BannersRequest request) {
        if (request == null)
            return "Bidang tidak boleh nol atau kosong";
        if (request.getBannerName() == null)
            return "Nama Banner tidak boleh nol atau kosong";
        if (request.getBannerImageWeb() == null)
            return "Gambar banner web tidak boleh kosong";
        if (request.getBannerImageMobile() == null)
            return "Gambar banner mobile tidak boleh kosong";
        if (request.getDateFrom() == null)
            return "Tanggal Mulai tidak boleh kosong";
        if (request.getDateFrom().after(request.getDateTo()))
            return "Tanggal mulai tidak boleh lebih dari tanggal selesai";
        if (request.getDateTo() == null)
            return "Tanggal Mulai tidak boleh kosong";
        if (request.getDateTo().before(request.getDateFrom()))
            return "Tanggal selesai tidak boleh kurang dari tanggal mulai";

        return null;
    }

    @ApiOperation(value = "Get all banner list.", notes = "Returns list of banner.\n" + swaggerInfo
            + "", response = Banners.class, responseContainer = "List", httpMethod = "GET")
    public static Result listBanners(String filter, String sort, int offset, int limit) {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            Query<Banners> query = BannersRepository.find.where().eq("t0.is_deleted", false).eq("t0.merchant_id", getUserMerchant().id).order("t0.id");
            try {
                List<BannersResponse> responses = new ArrayList<>();
                List<Banners> totalData = BannersRepository.getTotalData(query);
                List<Banners> responseIndex = BannersRepository.getDataBanners(query, sort, filter, offset, limit);
                for (Banners data : responseIndex) {
                    BannersResponse response = new BannersResponse();
                    response.setId(data.id);
                    response.setBannerName(data.getBannerName());
                    response.setBannerImageWeb(data.getBannerImageWeb());
                    response.setBannerImageMobile(data.getBannerImageMobile());
                    response.setActive(data.isActive());
                    response.setDeleted(data.isDeleted());
                    response.setDateFrom(data.getDateFrom());
                    response.setDateTo(data.getDateTo());
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

    @ApiOperation(value = "Edit Banners", notes = "Edit Banners.\n" + swaggerInfo
            + "", response = BaseResponse.class, httpMethod = "PUT")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "banners form", dataType = "temp.swaggermap.BannersForm", required = true, paramType = "body", value = "banners form") })
    public static Result editBanners(Long id) {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            try {
                JsonNode json = json = request().body().asJson();
                BannersRequest request = objectMapper.readValue(json.toString(), BannersRequest.class);
                String validate = validateCreateBanners(request);
                if (validate == null) {
                    Transaction trx = Ebean.beginTransaction();
                    try {
                        Banners banners = BannersRepository.findByIdAndMerchantId(id, ownMerchant.id);
                        if (banners == null) {
                            response.setBaseResponse(0, 0, 0, error + " banner tidak tersedia.", null);
                            return badRequest(Json.toJson(response));
                        }
                        banners.setBannerName(request.getBannerName());
                        if(request.getBannerImageWeb() != null){
                            banners.setBannerImageWeb(request.getBannerImageWeb());
                        }
                        if(request.getBannerImageMobile() != null){
                            banners.setBannerImageMobile(request.getBannerImageMobile());
                        }
                        banners.setActive(request.isActive());
                        banners.setDateFrom(request.getDateFrom());
                        banners.setDateTo(request.getDateTo());
                        banners.update();

                        trx.commit();
                        response.setBaseResponse(1, offset, 1, success + " mengubah banner", banners);
                        return ok(Json.toJson(response));
                    } catch (Exception e) {
                        logger.error("Error saat mengubah banner", e);
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

    @ApiOperation(value = "Delete Banners", notes = "Delete Banners.\n" + swaggerInfo
            + "", response = BaseResponse.class, httpMethod = "DELETE")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "banners form", dataType = "temp.swaggermap.BannerForm", required = true, paramType = "body", value = "banners form") })
    public static Result deleteBrand(Long id) {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
                if (id != null) {
                    Transaction trx = Ebean.beginTransaction();
                    try {
                        Banners banners = BannersRepository.findByIdAndMerchantId(id, ownMerchant.id);
                        if (banners == null) {
                            response.setBaseResponse(0, 0, 0, error + " banner tidak tersedia.", null);
                            return badRequest(Json.toJson(response));
                        }

                        banners.isDeleted = true;
                        banners.update();
                        trx.commit();

                        response.setBaseResponse(1,offset, 1, success + " menghapus banner", banners);
                        return ok(Json.toJson(response));
                    } catch (Exception e) {
                        logger.error("Error saat menghapus banner", e);
                        e.printStackTrace();
                        trx.rollback();
                    } finally {
                        trx.end();
                    }
                    response.setBaseResponse(0, 0, 0, error, null);
                    return badRequest(Json.toJson(response));
                }
                response.setBaseResponse(0, 0, 0, "Tidak dapat menemukan banner id", null);
                return badRequest(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    @ApiOperation(value = "Read Banner", notes = "Read Banner.\n" + swaggerInfo
            + "", response = BaseResponse.class, httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "banners form", dataType = "temp.swaggermap.BannersForm", required = true, paramType = "body", value = "banners form") })
    public static Result viewBanner(Long id) {
        Merchant ownMerchant = checkMerchantAccessAuthorization();
        if (ownMerchant != null) {
            if (id != null) {
                Transaction trx = Ebean.beginTransaction();
                try {
                    Banners banners = BannersRepository.findByIdAndMerchantId(id, ownMerchant.id);
                    if (banners == null) {
                        response.setBaseResponse(0, 0, 0, error + " banner tidak tersedia.", null);
                        return badRequest(Json.toJson(response));
                    }
                    BannersResponse bannersResponse = new BannersResponse();
                    bannersResponse.setId(banners.id);
                    bannersResponse.setBannerName(banners.getBannerName());
                    bannersResponse.setBannerImageWeb(banners.getBannerImageWeb());
                    bannersResponse.setBannerImageMobile(banners.getBannerImageMobile());
                    bannersResponse.setMerchantId(banners.getMerchant().id);
                    bannersResponse.setActive(banners.isActive());
                    bannersResponse.setDateFrom(banners.getDateFrom());
                    bannersResponse.setDateTo(banners.getDateTo());

                    response.setBaseResponse(1,offset, 1, success + " menampilkan detail banner", bannersResponse);
                    return ok(Json.toJson(response));
                } catch (Exception e) {
                    logger.error("Error saat menampilkan detail banner", e);
                    e.printStackTrace();
                    trx.rollback();
                } finally {
                    trx.end();
                }
                response.setBaseResponse(0, 0, 0, error, null);
                return badRequest(Json.toJson(response));
            }
            response.setBaseResponse(0, 0, 0, "Tidak dapat menemukan banner id", null);
            return badRequest(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    @ApiOperation(value = "Status Banners", notes = "Status Banners.\n" + swaggerInfo
            + "", response = BaseResponse.class, httpMethod = "PUT")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "banner form", dataType = "temp.swaggermap.BannerForm", required = true, paramType = "body", value = "banner form") })
        public static Result setStatus(Long id) {
            Merchant ownMerchant = checkMerchantAccessAuthorization();
            if (ownMerchant != null) {
                try {
                    JsonNode json = request().body().asJson();
                    BannersRequest request = objectMapper.readValue(json.toString(), BannersRequest.class);
                    Transaction trx = Ebean.beginTransaction();
                    try {
                        Banners banners = BannersRepository.findByIdAndMerchantId(id, ownMerchant.id);
                        if (banners == null) {
                            response.setBaseResponse(0, 0, 0, error + " banners tidak tersedia.", null);
                            return badRequest(Json.toJson(response));
                        }
                        banners.setActive(request.isActive());
                        banners.update();
    
                        trx.commit();
                        response.setBaseResponse(1, offset, 1, success + " mengubah status banner", banners);
                        return ok(Json.toJson(response));
                    } catch (Exception e) {
                        logger.error("Error saat mengubah status banner", e);
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


        // BANNERS FOR HOMEPAGE
        @ApiOperation(value = "Get all banner list.", notes = "Returns list of banner.\n" + swaggerInfo
            + "", response = Banners.class, responseContainer = "List", httpMethod = "GET")
    public static Result listBannersHomepage(Long merchantId) {
        Merchant ownMerchant = Merchant.merchantGetId(merchantId);

        Timestamp dateNow = new Timestamp(System.currentTimeMillis()); 
        System.out.println(dateNow);
        if (ownMerchant != null) {
            Query<Banners> query = BannersRepository.find.where().eq("t0.is_deleted", false).eq("t0.merchant_id", merchantId).betweenProperties("t0.date_from", "t0.date_to", dateNow).order("t0.date_from");
            try {
                List<BannersResponse> responses = new ArrayList<>();
                List<Banners> totalData = BannersRepository.getTotalData(query);
                List<Banners> responseIndex = BannersRepository.getForHomeBanners(query);
                for (Banners data : responseIndex) {
                    BannersResponse response = new BannersResponse();
                    response.setId(data.id);
                    response.setBannerName(data.getBannerName());
                    response.setBannerImageWeb(data.getBannerImageWeb());
                    response.setBannerImageMobile(data.getBannerImageMobile());
                    response.setActive(data.isActive());
                    response.setDeleted(data.isDeleted());
                    response.setDateFrom(data.getDateFrom());
                    response.setDateTo(data.getDateTo());
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

}