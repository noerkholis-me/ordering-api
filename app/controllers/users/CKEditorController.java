package controllers.users;

import com.hokeba.api.BaseResponse;
import com.hokeba.util.Constant;
import com.hokeba.util.Helper;
import com.wordnik.swagger.annotations.Api;
import controllers.BaseController;
import models.Photo;
import org.apache.commons.io.IOUtils;
import play.Logger;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hendriksaragih on 3/2/17.
 */
@Api(value = "/users/ckeditor", description = "CKEDITOR")
public class CKEditorController extends BaseController {
    private static BaseResponse response = new BaseResponse();
    private static String baseImageUrl = Constant.getInstance().getCKEditorImageUrl();

    public static Result upload() {
        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart picture = body.getFile("file");
        String fileName = picture.getFilename().toString();
        fileName = Helper.getRandomString(10)+fileName.substring(0,fileName.length()-4);
        try {
            File newFile = uploadImageCrop(picture, "", fileName, null, "jpg");
        }catch (IOException e){
            Logger.debug("error : "+e.getMessage());
        }
        String url = baseImageUrl +"/"+ fileName+".jpg";
        Map<String, String> result = new HashMap<>();
        result.put("url", url);
        return ok(Json.toJson(result));

    }

    private static File uploadImageCrop(Http.MultipartFormData.FilePart image,
                                        String directory, String resName, int[] resolution, String resFormat) throws IOException{
        File result = null;
        if(image!=null){
            if(image.getContentType().startsWith("image") || image.getContentType().equals("application/octet-stream")){
                String filePath = Constant.getInstance().getImagePath() + "ckeditor" + File.separator + directory;
                File dir = new File(filePath);
                if(!dir.exists()){
                    dir.mkdir();
                }

                File srcFile = (File) image.getFile();
                File dstFile = (resName==null) ?
                        File.createTempFile("", "."+resFormat , new File(filePath)):
                        new File(filePath + resName+"."+resFormat);

                BufferedImage imageR = ImageIO.read(srcFile);
                //original image size
                int width   = imageR.getWidth();
                int height  = imageR.getHeight();
                if(resolution==null){
                    resolution = Photo.getScaledResolution(width, height);
                }
                //result image frame size
                int widthF  = resolution[0];
                int heightF = resolution[1];
                int[]size = Photo.getAppliedResolutionCrop(width, height, widthF, heightF);
                // resize image size
                int widthR = size[0];
                int heightR = size[1];
                // start coordinate to drawing at result image frame
                int widthS = (widthF - widthR) / 2;
                int heightS = (heightF - heightR) / 2;

                Image imageR1 = imageR.getScaledInstance(widthR, heightR, Image.SCALE_SMOOTH);
                BufferedImage b1;
                Graphics2D bg;
                if(resFormat.equalsIgnoreCase("png")||resFormat.equalsIgnoreCase("gif")){
                    b1 = new BufferedImage(widthF, heightF, BufferedImage.TYPE_INT_ARGB);
                    bg = b1.createGraphics();
                    bg.setComposite(AlphaComposite.Clear);
                    bg.fillRect(0, 0, widthF, heightF);
                    bg.setComposite(AlphaComposite.Src);
                    bg.drawImage(imageR1, widthS, heightS, null);
                }
                else {
                    b1 = new BufferedImage(widthF, heightF, BufferedImage.TYPE_INT_RGB);
                    bg = b1.createGraphics();
                    bg.setColor(Color.WHITE);
                    bg.fillRect(0, 0, widthF, heightF);
                    bg.setComposite(AlphaComposite.Src);
                    bg.drawImage(imageR1, widthS, heightS, Color.WHITE, null);
                }
                bg.dispose();
                ImageIO.write(b1, resFormat, dstFile);
                result = dstFile;
            }
        }
        return result;
    }

    public static Result ckeditorImage(String filename) {
        return getCkeditorImage(Constant.getInstance().getImagePath().concat("ckeditor").concat(File.separator).concat(filename));
    }

    public static Result getCkeditorImage(String filename) {
        ByteArrayInputStream input = null;
        byte[] byteArray;

        try {
            File file = new File(filename);
            byteArray = IOUtils.toByteArray(new FileInputStream(file));
            input = new ByteArrayInputStream(byteArray);
            String[] fileType = filename.split("\\.");
            return ok(input).as("image/" + fileType[fileType.length - 1]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return notFound();
    }
}
