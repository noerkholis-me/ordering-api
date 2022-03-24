package utils;

import com.hokeba.aws.s3.S3Service;
import com.hokeba.util.CommonFunction;
import com.hokeba.util.Constant;
import play.mvc.Http;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageUtil {

    public static final int commonMaxWidth = 1200;

    private static final String IMAGE_URL_PREFIX = Constant.getInstance().getImageUrl();
    private static final String IMAGE_PATH_PREFIX = Constant.getInstance().getImagePath();


    public static String createImageUrl (String key, String fileName) {
        return IMAGE_URL_PREFIX + IMAGE_PATH_PREFIX + ImageDirectory.getImageDirectory(key) + "/" + fileName;
    }

    public static File uploadImage(Http.MultipartFormData.FilePart image,
                                   String key, String resName, int[] resolution, String formatFile) throws IOException{
        File result = null;
        if(image!=null){
            if(image.getContentType().startsWith("image")){
                String filePath = IMAGE_PATH_PREFIX + ImageDirectory.getImageDirectory(key) + File.separator;
                File dir = new File(filePath);
                if(!dir.exists()){
                    return null;
                }
                File srcFile = image.getFile();
                result = cropImage(srcFile, resName, resolution, filePath, formatFile);
            }
        }
        return result;
    }

    private static File cropImage(File srcFile, String resName, int[] resolution, String filePath, String resFormat) throws IOException {
        File dstFile = (resName==null) ?
                File.createTempFile(CommonFunction.getCurrentTime("ddMMYY-HHmmss")+"_", "."+resFormat , new File(filePath)):
                new File(filePath+CommonFunction.getCurrentTime("ddMMYY-HHmmss")+"_"+resName+"."+resFormat);
        BufferedImage imageR = ImageIO.read(srcFile);
        //original image size
        int width   = imageR.getWidth();
        int height  = imageR.getHeight();
        if(resolution==null){
            resolution = getScaledResolution(width, height);
        }
        //result image frame size
        int widthF  = resolution[0];
        int heightF = resolution[1];
        int[]size = getAppliedResolution(width, height, widthF, heightF);
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
        } else {
            b1 = new BufferedImage(widthF, heightF, BufferedImage.TYPE_INT_RGB);
            bg = b1.createGraphics();
            bg.setColor(Color.WHITE);
            bg.fillRect(0, 0, widthF, heightF);
            bg.setComposite(AlphaComposite.Src);
            bg.drawImage(imageR1, widthS, heightS, Color.WHITE, null);
        }
        bg.dispose();
        ImageIO.write(b1, resFormat, dstFile);

        return dstFile;
    }

    public static int[] getScaledResolution(int width, int height){
        if(width>commonMaxWidth){
            height = (int)(((((float)commonMaxWidth)/width)) * height);
            width  = commonMaxWidth;
        }
        return new int[] {width, height};
    }

    public static int[] getAppliedResolution(int width, int height, int widthF, int heightF){
        float widthRatio  = (((float) widthF) / width);
        float heightRatio = (((float) heightF) / height);
        int widthR  = widthF;
        int heightR = heightF;
        if (widthRatio > heightRatio) {
            widthR = (int) (heightRatio * width);
        } else if (widthRatio < heightRatio) {
            heightR = (int) (widthRatio * height);
        }
        return new int[] {widthR, heightR};
    }






}
