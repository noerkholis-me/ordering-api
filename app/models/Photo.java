package models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hokeba.aws.s3.S3Service;
import com.hokeba.util.CommonFunction;
import com.hokeba.util.Constant;
import play.mvc.Http;

import javax.imageio.ImageIO;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * Created by hendriksaragih on 2/20/17.
 */
@Entity
@Table(name = "photo")
public class Photo extends BaseModel {
    public static final int[] articleHeaderSize                     = {800,600};
    public static final int[] articleThumbSize                      = {196,110};
    public static final int[] mainBannerSize                        = {1520,450};
    public static final int[] mainBannerResponsiveSize              = {400,204};
    public static final int[] mainBannerMobileSize                  = {400,204};
    public static final int[] promoImageSize                        = {650,250};
    public static final int[] promoResponsiveImageSize              = {182,275};
    public static final int[] fullImageSize                         = {600,600};
    public static final int[] mediumImageSize                       = {300,300};
    public static final int[] thumbImageSize                        = {150,150};
    public static final int[] blurImageSize                         = {10,10};
    public static final int[] iconImageSize                         = {300,300};
    public static final int   commonMaxWidth                        = 1200;
    public static final int[] categoryBannerImageSize               = {125,100};
    public static final int[] categoryBannerDetailImageSizeBig      = {1250,560};
    public static final int[] categoryBannerDetailImageMedium    	= {852,530};
    public static final int[] categoryBannerDetailImageSmall    	= {236,345};
    public static final int[] categoryBannerMenuSize                = {331,119};
    public static final int[] mostpopularBannerImageSizeBig      	= {651,966};
    public static final int[] mostpopularBannerImageSizeMedium    	= {911,464};
    public static final int[] mostpopularBannerImageSizeSmall    	= {398,490};
    public static final int[] categoryImageSize                     = {180,180};
    public static final int[] categoryImageResponsiveL1Size         = {32,32};
    public static final int[] categoryImageResponsiveL3Size         = {64,64};
    public static final int[] subCategory1                          = {360,478};
    public static final int[] subCategory2                          = {347,410};
    //    public static final int[] subCategory3                          = {170,270};
//    public static final int[] subCategory4                          = {180,130};
    public static final int[] brandImageSize                        = {287,129};
    public static final int[] bankImageSize                         = {55,40};
    public static final int[] courrierImageSize                     = {100,100};


    /**
     *
     */
    private static final long serialVersionUID = 1L;
    @JsonProperty("file_name")
    public String fileName;
    @JsonProperty("file_name_before")
    public String fileNameBefore;
    @JsonProperty("full_url")
    public String fullUrl;
    @JsonProperty("medium_url")
    public String mediumUrl;
    @JsonProperty("thumb_url")
    public String thumbUrl;
    @JsonProperty("blur_url")
    public String blurUrl;
    @JsonProperty("user_id")
    public Long userId;
    @JsonProperty("user_type")
    public String userType;
    public String module;
    @JsonProperty("module_id")
    public Long moduleId;

    public static Finder<Long, Photo> find = new Finder<Long, Photo>(Long.class, Photo.class);

    //Alex, 29-11-2016, method ini digunakan untuk menyimpan log gambar yang disimpan.
    public static String saveRecord(String code, String saveNameF, String saveNameM, String saveNameT, String saveNameB,
                                    String fileName, Long userId, String userType, String module, Long moduleId){
        Photo model = new Photo();
        model.fileName       = saveNameF;
        model.fileNameBefore = fileName;
        model.fullUrl	     = createUrl(code, saveNameF);
        model.mediumUrl	     = saveNameM.equals("") ? "" : createUrl(saveNameM);
        model.thumbUrl	     = saveNameT.equals("") ? "" : createUrl(saveNameT);
        model.blurUrl	     = saveNameB.equals("") ? "" : createUrl(saveNameB);
        model.userId         = userId;
        model.userType       = userType;
        model.module         = module;
        model.moduleId		 = moduleId;
        model.save();
        return model.fullUrl;
    }

    //Alex, 29-11-2016, method ini digunakan untuk membentuk url tempat gambar disimpan.
    public static String createUrl(String name){
        return name;
    }

    public static String createUrl(String code, String name){
        Map<String, String> listDir = listDirectory();
        return listDir.get(code) + "/" + name;
    }



    //Alex, 29-11-2016, method ini digunakan untuk menyesuaikan ukuran gambar dengan lebar minimum yang disetujui
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

    public static int[] getAppliedResolutionCrop(int width, int height, int widthF, int heightF){
        float widthRatio  = (((float) widthF) / width);
        float heightRatio = (((float) heightF) / height);
        int widthR  = widthF;
        int heightR = heightF;
        if (widthRatio > heightRatio) {
            heightR = (int) (widthRatio * height);
        } else if (widthRatio < heightRatio) {
            widthR = (int) (heightRatio * width);
        }
        return new int[] {widthR, heightR};
    }

    //Alex, 29-11-2016, method ini digunakan untuk menyimpan gambar yang di-resize dengan tambahan padding
    public static File uploadImage(Http.MultipartFormData.FilePart image,
                                   String code, String resName, int[] resolution, String resFormat) throws IOException{
        File result = null;
        Map<String, String> listDir = listDirectory();
        if(image!=null){
            if(image.getContentType().startsWith("image")){

                String filePath = Constant.getInstance().getImagePath() + listDir.get(code) + File.separator;
                File dir = new File(filePath);
                if(!dir.exists()){
                    dir.mkdir();
                }

                File srcFile = image.getFile();

                result = cropImage(srcFile, resName, resolution, filePath, resFormat);

                
                //save file to S3 bucket
                if (S3Service.enabled())
                	S3Service.getInstance().saveObject(listDir.get(code) + File.separator + result.getName(), result);
                
                //cleanup
//                srcFile.delete();
//                result.delete();
            }
        }
        return result;
    }

    private static File cropImage(File srcFile, String resName, int[] resolution, String filePath, String resFormat) throws IOException{
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
        
        return dstFile;
    }

    //Alex, 22-12-2016, method ini digunakan untuk menyimpan gambar yang di-crop jika dimensi gambar lebih besar dari dimensi yang diinginkan
    public static File uploadImageCrop(Http.MultipartFormData.FilePart image,
                                       String code, String resName, int[] resolution, String resFormat) throws IOException{
        File result = null;
        Map<String, String> listDir = listDirectory();
        if(image!=null){
            if(image.getContentType().startsWith("image") || image.getContentType().equals("application/octet-stream")){
                String filePath = Constant.getInstance().getImagePath() + listDir.get(code) + File.separator;
                File dir = new File(filePath);
                if(!dir.exists()){
                    dir.mkdir();
                }

                File srcFile = image.getFile();
                File dstFile = (resName==null) ?
                        File.createTempFile(CommonFunction.getCurrentTime("ddMMYY-HHmmss")+"_", "."+resFormat , new File(filePath)):
                        new File(filePath + CommonFunction.getCurrentTime("ddMMYY-HHmmss")+"_"+resName+"."+resFormat);

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
                int[]size = getAppliedResolutionCrop(width, height, widthF, heightF);
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

    //Alex, 29-11-2016, method ini digunakan untuk menyimpan list gambar
    public static List<File> uploadImages(List<Http.MultipartFormData.FilePart> images,
                                          String code, String name, int[] resolution, String imageFormat) throws IOException{
        List<File> newFiles = new ArrayList<File>();
        boolean success = true;
        int count = 1;
        for (Http.MultipartFormData.FilePart image : images) {
            File newFile = uploadImage(image, code, name+"-"+count, resolution, imageFormat);
            if(newFile!=null){
                newFiles.add(newFile);
                count++;
            }
            else{
                success = false;
                deleteFiles(newFiles);
                break;
            }
        }
        return success ? newFiles : null;
    }

    private static void deleteFiles(List<File> newFiles){
        for (File file : newFiles) {
            file.delete();
        }
    }

    public static List<File> uploadImagesRaw(List<String> images,
                                          String code, String name, int[] resolution, String imageFormat) throws IOException{
        List<File> newFiles = new ArrayList<>();
        boolean success = true;
        int count = 1;
        for (String image : images) {
            File newFile = uploadImageRaw(image, code, name+"-"+count, resolution, imageFormat);
            if(newFile!=null){
                newFiles.add(newFile);
                count++;
            }
            else{
                success = false;
                deleteFiles(newFiles);
                break;
            }
        }
        return success ? newFiles : new ArrayList<>();
    }

    public static File uploadImageRaw(String image, String code, String resName, int[] resolution, String resFormat) throws IOException{
        File result = null;
        Map<String, String> listDir = listDirectory();
        if(image!=null && !image.isEmpty()){
            String filePath = Constant.getInstance().getImagePath() + listDir.get(code) + File.separator;
            File dir = new File(filePath);
            if(!dir.exists()){
                dir.mkdir();
            }
            String fileName = "temp_"+CommonFunction.getCurrentTime("ddMMYY-HHmmss")+"_"+resName+"."+resFormat;
            File srcFile = createImageFromBase64(image, filePath, fileName);

            result = cropImage(srcFile, resName, resolution, filePath, resFormat);
            
            //save file to S3 bucket
            if (S3Service.enabled())
            	S3Service.getInstance().saveObject(listDir.get(code) + File.separator + result.getName(), result);
            
            //cleanup
//            srcFile.delete();
//            result.delete();
        }
        return result;
    }

    private static File createImageFromBase64(String encodedImg, String filePath, String fileName) throws IOException {
//        byte[] decodedImg = Base64.getDecoder().decode(encodedImg.getBytes(StandardCharsets.UTF_8));
        byte[] decodedImg = Base64.getDecoder().decode(encodedImg.substring(encodedImg.indexOf(",") + 1).getBytes(StandardCharsets.UTF_8));
        Path destinationFile = Paths.get(filePath, fileName);
        Files.write(destinationFile, decodedImg);

        return destinationFile.toFile();
    }

    private static Map<String, String> listDirectory (){
        Map<String, String> result = new HashMap<>();
        result.put("ban", "banner");
        result.put("ban-res", "banner_responsive");
        result.put("ban-mob", "banner_mobile");
        result.put("prm", "promo");
        result.put("prm-res", "promo_responsive");
        result.put("cat", "category");
        result.put("cat-res", "category_responsive");
        result.put("brd", "brand");
        result.put("atc", "article");
        result.put("atc-thumb", "article_thumbnail");
        result.put("prod", "product");
        result.put("prod-med", "product_medium");
        result.put("prod-thumb", "product_thumbnail");
        result.put("prod-icon", "product_icon");
        result.put("bank", "bank");
        result.put("cou", "courier");
        result.put("ord", "order");
        result.put("ckeditor", "ckeditor");
        result.put("tmp-prod", "tmp_product");
        result.put("tmp-prod-med", "tmp_product_medium");
        result.put("tmp-prod-thumb", "tmp_product_thumbnail");
        result.put("tmp-prod-icon", "tmp_product_icon");
        return result;
    }

    public static List<File> moveProductTemp (String code, String idImageTmp, String fileNameDest){
        List<File> resultFiles = new ArrayList<>();
        fileNameDest = CommonFunction.getCurrentTime("ddMMYY-HHmmss")+"_"+fileNameDest;

        try {
            Map<String, String> listDir = listDirectory();
            String sourcePath = Constant.getInstance().getImagePath() + listDir.get("tmp-" + code) + File.separator;
            String destPath = Constant.getInstance().getImagePath() + listDir.get(code) + File.separator;

            File dirDest = new File(destPath);
            if (!dirDest.exists()) {
                dirDest.mkdir();
            }

            File dir = new File(sourcePath);

            File[] matches = dir.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.endsWith(idImageTmp + ".jpg");
                }
            });
            int i = 0;
            for (i = 0; i < matches.length; i++) {
                Files.move(Paths.get(sourcePath + matches[i].getName()), Paths.get(destPath + fileNameDest + "_" + (String.valueOf(i + 1)) + ".jpg"), REPLACE_EXISTING);
                resultFiles.add(new File(destPath + fileNameDest + "_" + (String.valueOf(i + 1)) + ".jpg"));
            }
        }catch (IOException e){

        }

        return resultFiles;
    }
}
