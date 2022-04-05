package controllers;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Transaction;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hokeba.api.BaseResponse;
import com.wordnik.swagger.annotations.Api;
import controllers.masters.ShipperController;
import dtos.ImageRequest;
import models.Images;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import utils.ImageUtil;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Created by hendriksaragih on 2/11/17.
 */
@Api(value = "/api/images", description = "Images")
public class ImagesController extends BaseController {

    private final static Logger.ALogger logger = Logger.of(ImagesController.class);

    private static BaseResponse baseResponse = new BaseResponse();

    @SuppressWarnings("unchecked")
    public static Result getImages(String id) {
        Images img = Images.find.byId(id);
        if (img != null){
            try {
                return ok(Base64.getMimeDecoder().decode(img.images.replaceAll("(\r\n|\n)", "").getBytes(StandardCharsets.UTF_8))).as("image");
            }catch (Exception ex){
                return badRequest();
            }
        }else{
            return badRequest();
        }
    }


    public static Result uploadImages(String key) {
        int authority = checkAccessAuthorization("all");
        if (authority == 200 || authority == 203) {
            try {
                Integer countImage = 0;
                Http.MultipartFormData body = request().body().asMultipartFormData();
                if (body != null) {
                    Map<String, String[]> mapData = body.asFormUrlEncoded();
                    if (mapData != null) {
                        countImage = Integer.valueOf(mapData.get("image_count")[0]);
                    }
                }
                int loop = 1;
                Map<String, String> mapImage = new HashMap<>();
                try {
                    while (loop <= countImage) {
                        String imageKey = "image-" + loop;
                        Images images = Images.find.where().eq("module", key).eq("image_key", imageKey).findUnique();
                        if (images == null) {
                            Http.MultipartFormData.FilePart imageFile = body.getFile(imageKey);
                            File image = ImageUtil.uploadImage(imageFile, key, imageKey, ImageUtil.fullImageSize, "jpg");
                            String url = ImageUtil.createImageUrl(key, image.getName());
                            mapImage.put(imageKey, url);

                            Images img = new Images();
                            img.setModule(key);
                            img.setImages(url);
                            img.setImageKey(imageKey);
                            img.save();

                        } else {
                            Http.MultipartFormData.FilePart imageFile = body.getFile(imageKey);
                            File image = ImageUtil.uploadImage(imageFile, key, imageKey, ImageUtil.fullImageSize, "jpg");
                            String url = ImageUtil.createImageUrl(key, image.getName());
                            mapImage.put(imageKey, url);

                            images.setModule(key);
                            images.setImages(url);
                            images.setImageKey(imageKey);
                            images.update();

                        }
                        loop++;
                    }
                    baseResponse.setBaseResponse(1, 1, 1, success + " upload image " + key, mapImage);
                    return ok(Json.toJson(baseResponse));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception ex) {
                logger.error("Error upload image " + ex.getMessage());
                ex.printStackTrace();
            }
        } else if (authority == 403) {
            baseResponse.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(baseResponse));
        } else {
            baseResponse.setBaseResponse(0, 0, 0, unauthorized, null);
            return unauthorized(Json.toJson(baseResponse));
        }
        baseResponse.setBaseResponse(0, 0, 0, error, null);
        return ok(Json.toJson(baseResponse));
    }


}
