package controllers;

import com.wordnik.swagger.annotations.Api;
import models.Images;
import play.mvc.Controller;
import play.mvc.Result;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Created by hendriksaragih on 2/11/17.
 */
@Api(value = "/api/images", description = "Images")
public class ImagesController extends Controller {

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
}
