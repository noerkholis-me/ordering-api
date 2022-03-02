package controllers.merchants;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Page;
import com.avaje.ebean.Query;
import com.avaje.ebean.Transaction;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hokeba.api.BaseResponse;
import com.hokeba.http.response.global.ServiceResponse;
import com.hokeba.mapping.request.MapProductMerchant;
import com.hokeba.mapping.request.MapProductMerchant2;
import com.hokeba.mapping.response.*;
import com.hokeba.social.requests.MailchimpProductRequest;
import com.hokeba.social.requests.MailchimpProductVariantRequest;
import com.hokeba.social.service.MailchimpService;
import com.hokeba.util.CommonFunction;
import com.hokeba.util.Secured;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponses;

import assets.Tool;
import controllers.BaseController;
import models.*;
import models.Currency;
import play.Logger;
import play.Play;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by hendriksaragih on 4/8/17.
 */
@Api(value = "/merchants/products", description = "Products")
public class ProductsController extends BaseController {
    @SuppressWarnings("rawtypes")
    private static BaseResponse response = new BaseResponse();

    public static Result index() {
        return ok();
    }

    @ApiOperation(value = "Get all product list.", notes = "Returns list of product.\n" + swaggerInfo
            + "", response = ProductDetail.class, responseContainer = "List", httpMethod = "GET")
    public static Result lists(String type, String filter, String sort, int offset, int limit) {
        int authority = checkAccessAuthorization("merchant");
//        int authority = 200;
        if (authority == 200) {
            Query<Product> query = Product.find.where().eq("t0.is_deleted", false).eq("t0.merchant_id", getUserMerchant().id).order("t0.id");
//            Query<Product> query = Product.find.where().eq("is_deleted", false).eq("merchant_id", 1L).order("id");
            BaseResponse<Product> responseIndex;
            try {
                responseIndex = Product.getDataMerchant(query, type, sort, filter, offset, limit);
                return ok(Json.toJson(responseIndex));
            } catch (IOException e) {
                Logger.error("allDetail", e);
            }
        } else if (authority == 401) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result attributes() {
        int authority = checkAccessAuthorization("merchant");
        if (authority == 200) {
            List<BaseAttribute> data = BaseAttribute.getAllData();

            response.setBaseResponse(data.size(), offset, data.size(), success, new ObjectMapper().convertValue(data, MapBaseAttribute[].class));
            return ok(Json.toJson(response));
        } else if (authority == 401) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result attributeDetails(Long id) {
        int authority = checkAccessAuthorization("merchant");
        if (authority == 200) {
            List<Attribute> data = Attribute.getDataBy(id);

            response.setBaseResponse(data.size(), offset, data.size(), success, new ObjectMapper().convertValue(data, MapBaseAttribute[].class));
            return ok(Json.toJson(response));
        } else if (authority == 401) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    @BodyParser.Of(value = BodyParser.Json.class, maxLength = 50 * 1024 * 1024)
    public static Result save() {
        Merchant actor = checkMerchantAccessAuthorization();
        if (actor != null) {
            Transaction txn = Ebean.beginTransaction();
            String errorMessage = error;
            try {
                Http.MultipartFormData body = request().body().asMultipartFormData();
                JsonNode json = null;
                Integer countImage = 0;
                List<String> rawImages = new ArrayList<>();
                if (body != null) {
                    Map<String, String[]> mapData = body.asFormUrlEncoded();

                    if (mapData != null) {
                        json = Json.parse(mapData.get("data")[0]);
                        countImage = Integer.valueOf(mapData.get("image_count")[0]);
                    }
                } else {
                    json = request().body().asJson();
                }
                // TODO delete failed image
                if (json != null) {
                    ObjectMapper mapper = new ObjectMapper();
                    MapProductMerchant map = mapper.readValue(json.toString(), MapProductMerchant.class);
                    Product data = new Product(map);
                    data.merchant = actor;

                    Product uniqueCheck = Product.find.where()
                            .eq("name", data.name)
                            .eq("merchant_id", actor.id)
                            .setMaxRows(1)
                            .findUnique();
                    if (uniqueCheck != null){
                        response.setBaseResponse(0, 0, 0, "Product with similar name already exist", null);
                        return badRequest(Json.toJson(response));
                    }

                    Set<BaseAttribute> listBaseAttribute = new HashSet<>();
                    Set<Attribute> listAttribute = new HashSet<>();
                    map.getAttribute().forEach((k,v)->{
                        BaseAttribute tmp = BaseAttribute.find.byId(k);
                        listBaseAttribute.add(tmp);
                        Attribute attribute = Attribute.find.byId(v);
                        listAttribute.add(attribute);
                    });

                    Set<Size> sizes = new HashSet<>();
                    if (map.getSize() != null){
                        map.getSize().forEach(s->{
                            Size sz = Size.find.byId(s);
                            sizes.add(sz);
                        });
                    }

                    data.sizes = sizes;
                    data.baseAttributes = listBaseAttribute;
                    data.attributes = listAttribute;

                    if(data.discount == 0D ){
                        data.discountType = 0;
                        data.discountActiveFrom = null;
                        data.discountActiveTo = null;
                    }

                    Double discountAmount = 0D;
                    switch (data.discountType){
                        case 1 : discountAmount =  data.discount; break;
                        case 2 :
                            discountAmount = (data.discount/100*data.price);
                            break;
                    }

                    Double price = data.price - discountAmount;
                    data.buyPrice = price - Math.floor(data.category.getShareProfit()/100 * price);
                    data.position = 10000;
                    data.checkoutType = 0L;
                    data.save();
                    data.slug = CommonFunction.slugGenerate(data.name+"-"+data.id);

                    List<Http.MultipartFormData.FilePart> picture = new ArrayList<>();
                    int loop = 1;
                    while (loop <= countImage) {
                        Http.MultipartFormData.FilePart imageFile = body.getFile("image-" + loop);
                        picture.add(imageFile);
                        loop++;
                    }

                    List<File> newFilesFull = Photo.uploadImages(picture, "prod", data.slug, Photo.fullImageSize, "jpg");
                    List<File> newFilesMedium = Photo.uploadImages(picture, "prod-med", data.slug, Photo.mediumImageSize, "jpg");
                    List<File> newFilesThumb = Photo.uploadImages(picture, "prod-thumb", data.slug, Photo.thumbImageSize, "jpg");
                    List<File> newFilesIcon = Photo.uploadImages(picture, "prod-icon", data.slug, Photo.iconImageSize, "jpg");


                    if(newFilesMedium.size() > 0){
                        data.imageUrl = Photo.createUrl("prod-med", newFilesMedium.get(0).getName());
                    }
                    if(newFilesMedium.size() > 0){
                        data.thumbnailUrl = Photo.createUrl("prod-thumb", newFilesMedium.get(0).getName());
                    }

                    data.update();

                    ProductDetail detail = new ProductDetail();
                    detail.mainProduct = data;
                    detail.weight= map.getWeight();
                    String[] dimen = map.getDimension().split("x");
                    detail.dimension1= Double.valueOf(dimen[0]);
                    detail.dimension2= Double.valueOf(dimen[1]);
                    detail.dimension3= Double.valueOf(dimen[2]);
                    detail.warrantyType= map.getWarranty();
                    if(detail.warrantyType != 0){
                        detail.warrantyPeriod = map.getWarrantyPeriod();
                    }
                    detail.whatInTheBox = map.getWhatsInTheBox();
                    detail.description = map.getLongDescription();
                    detail.setShortDescriptions(map.getShortDescription());
                    detail.sizeGuide = map.getSizeGuide();

                    String[] fullImageUrls = new String[5];
                    String[] mediumImageUrls = new String[5];
                    String[] thumbnailImageUrls = new String[5];
                    String[] threesixtyImageUrls = new String[5];

                    int idx = 0;
                    for(File file : newFilesFull){
                        fullImageUrls[idx] = Photo.createUrl("prod", file.getName());
                        idx++;
                    }
                    idx = 0;
                    for(File file : newFilesMedium){
                        mediumImageUrls[idx] = Photo.createUrl("prod-med", file.getName());
                        idx++;
                    }
                    idx = 0;
                    for(File file : newFilesThumb){
                        thumbnailImageUrls[idx] = Photo.createUrl("prod-thumb", file.getName());
                        idx++;
                    }
                    idx = 0;
                    for(File file : newFilesIcon){
                        threesixtyImageUrls[idx] = Photo.createUrl("prod-icon", file.getName());
                        idx++;
                    }

                    detail.fullImageUrls = Json.toJson(fullImageUrls).toString();
                    detail.mediumImageUrls = Json.toJson(mediumImageUrls).toString();
                    detail.thumbnailImageUrls = Json.toJson(thumbnailImageUrls).toString();
                    detail.threesixtyImageUrls = Json.toJson(threesixtyImageUrls).toString();

                    detail.save();

//                    OdooService.getInstance().createProduct(data);

                    txn.commit();
                    response.setBaseResponse(1, offset, 1, created, null);
                    return ok(Json.toJson(response));
                }
            } catch (Exception e) {
                errorMessage = e.getMessage();
                Logger.error("create", e);
                txn.rollback();
            } finally {
                txn.end();
            }
            response.setBaseResponse(0, 0, 0, errorMessage, null);
            return badRequest(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    @BodyParser.Of(value = BodyParser.Json.class, maxLength = 50 * 1024 * 1024)
    public static Result saveJson() { //TODO
        Merchant actor = checkMerchantAccessAuthorization();
        if (actor != null) {
            Transaction txn = Ebean.beginTransaction();
            String errorMessage = error;
            try {
                JsonNode json = request().body().asJson();
                if (json != null) {
                    ObjectMapper mapper = new ObjectMapper();
                    MapProductMerchant2 map = mapper.readValue(json.toString(), MapProductMerchant2.class);
                    Product data = new Product(map);
                    data.merchant = actor;

                    Product uniqueCheck = Product.find.where()
                            .eq("name", data.name)
                            .eq("merchant_id", actor.id)
                            .setMaxRows(1)
                            .findUnique();
                    if (uniqueCheck != null){
                        response.setBaseResponse(0, 0, 0, "Product with similar name already exist", null);
                        return badRequest(Json.toJson(response));
                    }

                    Set<BaseAttribute> listBaseAttribute = new HashSet<>();
                    Set<Attribute> listAttribute = new HashSet<>();
                    map.attribute.forEach((k,v)->{
                        BaseAttribute tmp = BaseAttribute.find.byId(k);
                        listBaseAttribute.add(tmp);
                        Attribute attribute = Attribute.find.byId(v);
                        listAttribute.add(attribute);
                    });
                    data.baseAttributes = listBaseAttribute;
                    data.attributes = listAttribute;

//                    Set<Size> sizes = new HashSet<>();
//                    if (map.getSize() != null){
//                        map.getSize().forEach(s->{
//                            Size sz = Size.find.byId(s);
//                            sizes.add(sz);
//                        });
//                    }
//                    data.sizes = sizes;
                    
                    Double price = data.priceDisplay;
                    data.buyPrice = price - Math.floor(data.category.getShareProfit()/100 * price);
                    data.position = 10000;
                    data.checkoutType = 0L;

                    data.save();
                    data.slug = CommonFunction.slugGenerate(data.name+"-"+data.id);

                    List<File> newFilesFull = new ArrayList<>();
                    List<File> newFilesMedium  = new ArrayList<>();
                    List<File> newFilesThumb  = new ArrayList<>();
                    List<File> newFilesIcon  = new ArrayList<>();
                    if (map.images != null && map.images.size() > 0){
                        newFilesFull = Photo.uploadImagesRaw(map.images, "prod", data.slug, Photo.fullImageSize, "jpg");
                        newFilesMedium = Photo.uploadImagesRaw(map.images, "prod-med", data.slug, Photo.mediumImageSize, "jpg");
                        newFilesThumb = Photo.uploadImagesRaw(map.images, "prod-thumb", data.slug, Photo.thumbImageSize, "jpg");
                        newFilesIcon = Photo.uploadImagesRaw(map.images, "prod-icon", data.slug, Photo.iconImageSize, "jpg");
                    }


                    if(newFilesMedium.size() > 0){
                        data.imageUrl = Photo.createUrl("prod-med", newFilesMedium.get(0).getName());
                    }
                    if(newFilesMedium.size() > 0){
                        data.thumbnailUrl = Photo.createUrl("prod-thumb", newFilesMedium.get(0).getName());
                    }
                    
                    String[] fullImageUrls = new String[5];
                    String[] mediumImageUrls = new String[5];
                    String[] thumbnailImageUrls = new String[5];
                    String[] threesixtyImageUrls = new String[5];

                    int idx = 0;
                    for(File file : newFilesFull){
                        fullImageUrls[idx] = Photo.createUrl("prod", file.getName());
                        idx++;
                    }
                    idx = 0;
                    for(File file : newFilesMedium){
                        mediumImageUrls[idx] = Photo.createUrl("prod-med", file.getName());
                        idx++;
                    }
                    idx = 0;
                    for(File file : newFilesThumb){
                        thumbnailImageUrls[idx] = Photo.createUrl("prod-thumb", file.getName());
                        idx++;
                    }
                    idx = 0;
                    for(File file : newFilesIcon){
                        threesixtyImageUrls[idx] = Photo.createUrl("prod-icon", file.getName());
                        idx++;
                    }

                    data.fullImageUrls = Json.toJson(fullImageUrls).toString();
                    data.mediumImageUrls = Json.toJson(mediumImageUrls).toString();
                    data.thumbnailImageUrls = Json.toJson(thumbnailImageUrls).toString();
                    data.blurImageUrls = Json.toJson(threesixtyImageUrls).toString();

                    data.update();

                    txn.commit();
                    response.setBaseResponse(1, offset, 1, created, null);
                    return ok(Json.toJson(response));
                }
            } catch (Exception e) {
                errorMessage = e.getMessage();
                Logger.error("create", e);
                txn.rollback();
            } finally {
                txn.end();
            }
            response.setBaseResponse(0, 0, 0, errorMessage, null);
            return badRequest(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    @BodyParser.Of(value = BodyParser.Json.class, maxLength = 50 * 1024 * 1024)
    public static Result updateJson() { //TODO
        Merchant actor = checkMerchantAccessAuthorization();
        if (actor != null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
            Transaction txn = Ebean.beginTransaction();
            String errorMessage = error;
            try {
                JsonNode json = request().body().asJson();
                if (json != null) {
                    ObjectMapper mapper = new ObjectMapper();
                    MapProductMerchant2 map = mapper.readValue(json.toString(), MapProductMerchant2.class);
                    Product data = new Product(map);

                    Product uniqueCheck = Product.find.where()
                            .eq("name", data.name)
                            .eq("merchant_id", actor.id)
                            .ne("id", data.id)
                            .setMaxRows(1)
                            .findUnique();
                    if (uniqueCheck != null){
                        response.setBaseResponse(0, 0, 0, "Product with similar name already exist", null);
                        return badRequest(Json.toJson(response));
                    }

                    Product product = Product.find.byId(data.id);
                    product.updateDataJson(map);
                    data.slug = CommonFunction.slugGenerate(data.name+"-"+data.id);
                    product.slug = data.slug;
                    
                    String[] fullImages = new String[5];
                    for(int i = 0; i < product.getImage1().length; i++){
                        fullImages[i] = product.getImage1()[i];
                    }
                    String[] mediumImages = new String[5];
                    for(int i = 0; i < product.getImage2().length; i++){
                        mediumImages[i] = product.getImage2()[i];
                    }
                    String[] thumbnailImages = new String[5];
                    for(int i = 0; i < product.getImage3().length; i++){
                        thumbnailImages[i] = product.getImage3()[i];
                    }
                    String[] iconImages = new String[5];
                    for(int i = 0; i < product.getImage4().length; i++){
                        iconImages[i] = product.getImage4()[i];
                    }

                    int loop = 1;
                    File fileFull = null;
                    File fileThumbnail = null;

                    if (map.images != null && map.images.size() > 0){
                        while (loop <= map.images.size()) {
                            String imageFile = map.images.get(loop-1);
                            if (imageFile != null) {
	                            File new1FileFull = Photo.uploadImageRaw(imageFile, "prod", product.slug+"-"+loop, Photo.fullImageSize, "jpg");
	                            File new1FilesMedium = Photo.uploadImageRaw(imageFile, "prod-med", product.slug+"-"+loop, Photo.mediumImageSize, "jpg");
	                            File new1FilesThumb = Photo.uploadImageRaw(imageFile, "prod-thumb", product.slug+"-"+loop, Photo.thumbImageSize, "jpg");
	                            File new1FilesIcon = Photo.uploadImageRaw(imageFile, "prod-icon", product.slug+"-"+loop, Photo.iconImageSize, "jpg");
	
	                            if (new1FileFull != null) {
	                                mediumImages[loop-1] = Photo.createUrl("prod-med", new1FilesMedium.getName());
	                                fullImages[loop-1] = Photo.createUrl("prod", new1FileFull.getName());
	                                thumbnailImages[loop-1] = Photo.createUrl("prod-thumb", new1FilesThumb.getName());
	                                iconImages[loop-1] = Photo.createUrl("prod-icon", new1FilesIcon.getName());
	                            }
	
	                            if (loop == 1){
	                                fileThumbnail = new1FilesThumb;
	                                fileFull = new1FileFull;
	                            }
                            }
                            loop++;
                        }
                    }

                    product.fullImageUrls = Json.toJson(fullImages).toString();
                    product.mediumImageUrls = Json.toJson(mediumImages).toString();
                    product.thumbnailImageUrls = Json.toJson(thumbnailImages).toString();
                    product.threesixtyImageUrls = Json.toJson(iconImages).toString();

//                    ProductDetailTmp detailTmp = new ProductDetailTmp(detail);
//                    detailTmp.save();

                    if (fileThumbnail != null) {
                        product.imageUrl = Photo.createUrl("prod", fileFull.getName());
                        product.thumbnailUrl = Photo.createUrl("prod-thumb", fileThumbnail.getName());
                    }

                    Set<BaseAttribute> listBaseAttribute = new HashSet<>();
                    Set<Attribute> listAttribute = new HashSet<>();
                    map.attribute.forEach((k,v)->{
                        BaseAttribute tmp = BaseAttribute.find.byId(k);
                        Attribute attribute = Attribute.find.byId(v);
                        listBaseAttribute.add(tmp);
                        listAttribute.add(attribute);
                    });
                    product.baseAttributes = listBaseAttribute;
                    product.attributes = listAttribute;

//                    Set<Size> sizes = new HashSet<>();
//                    if (map.getSize() != null){
//                        map.getSize().forEach(s->{
//                            Size sz = Size.find.byId(s);
//                            sizes.add(sz);
//                        });
//                    }
//                    product.sizes = sizes;

//                    product.name = data.name;
//                    product.metaTitle = data.metaTitle;
//                    product.metaKeyword = data.metaKeyword;
//                    product.description = data.description;
//
//                    Double discountAmount = 0D;
//                    switch (product.discountType){
//                        case 1 : discountAmount =  product.discount; break;
//                        case 2 :
//                            discountAmount = (product.discount/100*product.price);
//                            break;
//                    }

                    Double price = product.priceDisplay;
                    product.buyPrice = price - Math.floor(product.category.getShareProfit()/100 * price);

//                    product.approvedStatus = Product.PENDING;
                    product.update();
                    
//                    if (product.approvedStatus.equals(Product.REJECTED)){
//                        product.approvedStatus = Product.PENDING;
//                        product.update();
//                    }else{
//                        ProductTmp dataTmp = new ProductTmp(product);
////                        dataTmp.productDetail = detailTmp;
//                        dataTmp.approvedStatus = Product.PENDING;
//                        dataTmp.createdAt = new Date();
//                        //dataTmp.userCms = getUserCms();
//                        dataTmp.save();
//
//                        if (product.getStock() < data.getStock()){
//                            ProductStockTmp stockTmp = new ProductStockTmp(product, data.getStock() - product.getStock());
//                            stockTmp.save();
//                        }
//                    }

                    txn.commit();
                    
                    //mailchimp
                    UpdateMailchimpProduct(product);
                    
                    response.setBaseResponse(1, offset, 1, created, null);
                    return ok(Json.toJson(response));
                }
            } catch (Exception e) {
                errorMessage = e.getMessage();
                Logger.error("create", e);
                txn.rollback();
            } finally {
                txn.end();
            }
            response.setBaseResponse(0, 0, 0, errorMessage, null);
            return badRequest(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result createDetail() {
    	Merchant actor = checkMerchantAccessAuthorization();
        if (actor != null) {
            Transaction txn = Ebean.beginTransaction();
            String errorMessage = error;
            try {
            	JsonNode json = request().body().asJson();
            	Long productId = json.findPath("product_id").asLong(0L);
            	Long colorId = json.findPath("color_id").asLong(0L);
            	Long sizeId = json.findPath("size_id").asLong(0L);
            	Integer stock = json.findPath("total_stock").asInt(0);
            	
            	ProductDetailVariance model = new ProductDetailVariance();
            	Product productTarget = Product.find.where().eq("t0.id", productId).eq("t0.merchant_id", actor.id).findUnique();
            	MasterColor colorTarget = MasterColor.find.byId(colorId);
            	Size sizeTarget = Size.find.byId(sizeId);
            	
            	model.mainProduct = productTarget;
            	model.color = colorTarget;
            	model.size = sizeTarget;
            	model.totalStock = stock;
            	
            	String errorValidation = model.validate();
            	if (errorValidation != null) {
            		response.setBaseResponse(0, 0, 0, errorValidation, null);
                    return badRequest(Json.toJson(response));
            	}
            	
            	model.save();
            	txn.commit();
            	
            	//mailchimp
            	AddMailchimpProductVariant(productTarget, model);
            	
                response.setBaseResponse(1, offset, 1, created, null);
                return ok(Json.toJson(response));
            } catch (Exception e) {
            	errorMessage = e.getMessage();
                Logger.error("createDetail", e);
            	txn.rollback();
            } finally {
            	txn.end();
            }
            response.setBaseResponse(0, 0, 0, errorMessage, null);
            return badRequest(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }
    
    public static Result updateDetail() {
    	Merchant actor = checkMerchantAccessAuthorization();
        if (actor != null) {
            Transaction txn = Ebean.beginTransaction();
            String errorMessage = error;
            try {
            	JsonNode json = request().body().asJson();            	
            	Long modelId = json.findPath("id").asLong(0L);
//            	Long productId = json.findPath("product_id").asLong(0L);
            	Long colorId = json.findPath("color_id").asLong(0L);
            	Long sizeId = json.findPath("size_id").asLong(0L);
            	Integer stock = json.findPath("total_stock").asInt(0);
            	
            	ProductDetailVariance model = ProductDetailVariance.find.where().eq("t0.id", modelId).eq("t0.is_deleted", false)
            			.eq("mainProduct.merchant.id", actor.id).findUnique();
            	
            	if (model == null) {
            		response.setBaseResponse(0, 0, 0, notFound, null);
                    return notFound(Json.toJson(response));
            	}
            	
            	MasterColor colorTarget = MasterColor.find.byId(colorId);
            	Size sizeTarget = Size.find.byId(sizeId);
            	
//            	model.mainProduct = productTarget;
            	model.color = colorTarget;
            	model.size = sizeTarget;
            	model.totalStock = stock;
            	
            	String errorValidation = model.validate();
            	if (errorValidation != null) {
            		response.setBaseResponse(0, 0, 0, errorValidation, null);
                    return badRequest(Json.toJson(response));
            	}
            	
            	model.update();
            	txn.commit();
            	
            	//mailchimp
            	UpdateMailchimpProductVariant(model.mainProduct, model);
            	
                response.setBaseResponse(1, offset, 1, created, null);
                return ok(Json.toJson(response));
            } catch (Exception e) {
            	errorMessage = e.getMessage();
                Logger.error("updateDetail", e);
            	txn.rollback();
            } finally {
            	txn.end();
            }
            response.setBaseResponse(0, 0, 0, errorMessage, null);
            return badRequest(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }
    
    public static Result deleteDetail(Long id) {
    	Merchant actor = checkMerchantAccessAuthorization();
        if (actor != null) {
        	Transaction txn = Ebean.beginTransaction();
            String errorMessage = error;
            try {
            	ProductDetailVariance model = ProductDetailVariance.find.where().eq("t0.id", id).eq("t0.is_deleted", false)
            			.eq("mainProduct.merchant.id", actor.id).findUnique();
            	
            	if (model == null) {
            		response.setBaseResponse(0, 0, 0, notFound, null);
                    return notFound(Json.toJson(response));
            	}
            	
            	model.isDeleted = true;
            	model.update();
            	txn.commit();
            	
            	//mailchimp
            	DeleteMailchimpProductVariant(model);
            	
                response.setBaseResponse(1, offset, 1, deleted, null);
                return ok(Json.toJson(response));
	        } catch (Exception e) {
	        	errorMessage = e.getMessage();
	            Logger.error("deleteDetail", e);
	        	txn.rollback();
	        } finally {
	        	txn.end();
	        }
	        response.setBaseResponse(0, 0, 0, errorMessage, null);
	        return badRequest(Json.toJson(response));
	    }
	    response.setBaseResponse(0, 0, 0, unauthorized, null);
	    return unauthorized(Json.toJson(response));
    }
    
    //TODO
    public static Result listsDetail(Long id) {
        int authority = checkAccessAuthorization("merchant");
        if (authority == 200) {
        	List<ProductDetailVariance> result = ProductDetailVariance.find.where().eq("mainProduct.id", id).eq("t0.is_deleted", false)
        			.eq("mainProduct.merchant.id", getUserMerchant().id).findList();
        	
        	response.setBaseResponse(result.size(), offset, result.size(), success, new ObjectMapper().convertValue(result, MapProductVariantListDetail[].class));
            return ok(Json.toJson(response));
        } else if (authority == 401) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }
    
    
    @BodyParser.Of(value = BodyParser.Json.class, maxLength = 50 * 1024 * 1024)
    public static Result update() {
        Merchant actor = checkMerchantAccessAuthorization();
        if (actor != null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
            Transaction txn = Ebean.beginTransaction();
            String errorMessage = error;
            try {
                Http.MultipartFormData body = request().body().asMultipartFormData();
                JsonNode json = null;
                Integer countImage = 0;
                if (body != null) {
                    Map<String, String[]> mapData = body.asFormUrlEncoded();

                    if (mapData != null) {
                        json = Json.parse(mapData.get("data")[0]);
                        countImage = Integer.valueOf(mapData.get("image_count")[0]);
                    }
                } else {
                    json = request().body().asJson();
                }
                // TODO delete failed image
                if (json != null) {
                    ObjectMapper mapper = new ObjectMapper();
                    MapProductMerchant map = mapper.readValue(json.toString(), MapProductMerchant.class);
                    Product data = new Product(map);

                    Product uniqueCheck = Product.find.where()
                            .eq("name", data.name)
                            .eq("merchant_id", actor.id)
                            .ne("id", data.id)
                            .setMaxRows(1)
                            .findUnique();
                    if (uniqueCheck != null){
                        response.setBaseResponse(0, 0, 0, "Product with similar name already exist", null);
                        return badRequest(Json.toJson(response));
                    }

                    Product product = Product.find.byId(data.id);

                    ProductDetail detail = ProductDetail.find.where().eq("mainProduct", product).findUnique();
                    detail.mainProduct = data;
                    detail.weight= map.getWeight();
                    String[] dimen = map.getDimension().split("x");
                    detail.dimension1= Double.valueOf(dimen[0]);
                    detail.dimension2= Double.valueOf(dimen[1]);
                    detail.dimension3= Double.valueOf(dimen[2]);
                    detail.warrantyType= map.getWarranty();
                    detail.sizeGuide = map.getSizeGuide();
                    if(detail.warrantyType != 0){
                        detail.warrantyPeriod = map.getWarrantyPeriod();
                    }
                    detail.whatInTheBox = map.getWhatsInTheBox();
                    detail.description = map.getLongDescription();
                    detail.setShortDescriptions(map.getShortDescription());

                    String[] fullImages = new String[5];
                    for(int i = 0; i < detail.getImage1().length; i++){
                        fullImages[i] = detail.getImage1()[i];
                    }
                    String[] mediumImages = new String[5];
                    for(int i = 0; i < detail.getImage2().length; i++){
                        mediumImages[i] = detail.getImage2()[i];
                    }
                    String[] thumbnailImages = new String[5];
                    for(int i = 0; i < detail.getImage3().length; i++){
                        thumbnailImages[i] = detail.getImage3()[i];
                    }
                    String[] iconImages = new String[5];
                    for(int i = 0; i < detail.getImage5().length; i++){
                        iconImages[i] = detail.getImage5()[i];
                    }

                    int loop = 1;
                    File fileFull = null;
                    File fileThumbnail = null;
                    while (loop <= countImage) {
                        Http.MultipartFormData.FilePart imageFile = body.getFile("image-" + loop);

                        File new1FileFull = Photo.uploadImage(imageFile, "prod", data.slug+"-"+loop, Photo.fullImageSize, "jpg");
                        File new1FilesMedium = Photo.uploadImage(imageFile, "prod-med", data.slug+"-"+loop, Photo.mediumImageSize, "jpg");
                        File new1FilesThumb = Photo.uploadImage(imageFile, "prod-thumb", data.slug+"-"+loop, Photo.thumbImageSize, "jpg");
                        File new1FilesIcon = Photo.uploadImage(imageFile, "prod-icon", data.slug+"-"+loop, Photo.iconImageSize, "jpg");

                        if (new1FileFull != null) {
                            fullImages[loop-1] = Photo.createUrl("prod", new1FileFull.getName());
                            mediumImages[loop-1] = Photo.createUrl("prod-med", new1FilesMedium.getName());
                            thumbnailImages[loop-1] = Photo.createUrl("prod-thumb", new1FilesThumb.getName());
                            iconImages[loop-1] = Photo.createUrl("prod-icon", new1FilesIcon.getName());
                        }

                        if (loop == 1){
                            fileThumbnail = new1FilesThumb;
                            fileFull = new1FileFull;
                        }

                        loop++;
                    }

                    detail.fullImageUrls = Json.toJson(fullImages).toString();
                    detail.mediumImageUrls = Json.toJson(mediumImages).toString();
                    detail.thumbnailImageUrls = Json.toJson(thumbnailImages).toString();
                    detail.threesixtyImageUrls = Json.toJson(iconImages).toString();

                    ProductDetailTmp detailTmp = new ProductDetailTmp(detail);
                    detailTmp.save();

                    if (fileThumbnail != null) {
                        product.imageUrl = Photo.createUrl("prod", fileFull.getName());
                        product.thumbnailUrl = Photo.createUrl("prod-thumb", fileThumbnail.getName());
                    }

                    product.grandParentCategory = Category.find.byId(data.grandParentCategory.id);
                    product.parentCategory = Category.find.byId(data.parentCategory.id);
                    product.category = Category.find.byId(data.category.id);
//                    if(data.brandId != null){
//                        product.brand = Brand.find.byId(data.brand.id);
//                    }
                    if(map.getBrandId() != null){
                        product.brand = Brand.find.byId(data.brand.id);
                    }
                    product.currency = Currency.find.byId(data.currency.code);
                    product.discount = data.discount;
                    product.price = data.price;
                    product.discount = data.discount;
                    product.discountType = data.discountType;
                    if(data.discount == 0 ){
                        product.discountType = 0;
                        product.discountActiveFrom = null;
                        product.discountActiveTo = null;
                    }else{
                        product.discountActiveFrom = data.discountActiveFrom;
                        product.discountActiveTo = data.discountActiveTo;
                    }


                    Set<BaseAttribute> listBaseAttribute = new HashSet<>();
                    Set<Attribute> listAttribute = new HashSet<>();
                    map.getAttribute().forEach((k,v)->{
                        BaseAttribute tmp = BaseAttribute.find.byId(k);
                        listBaseAttribute.add(tmp);
                        Attribute attribute = Attribute.find.byId(v);
                        listAttribute.add(attribute);
                    });

                    Set<Size> sizes = new HashSet<>();
                    if (map.getSize() != null){
                        map.getSize().forEach(s->{
                            Size sz = Size.find.byId(s);
                            sizes.add(sz);
                        });
                    }

                    product.sizes = sizes;

                    product.baseAttributes = listBaseAttribute;
                    product.attributes = listAttribute;
                    product.name = data.name;
                    product.metaTitle = data.metaTitle;
                    product.metaKeyword = data.metaKeyword;
                    product.description = data.description;

                    Double discountAmount = 0D;
                    switch (product.discountType){
                        case 1 : discountAmount =  product.discount; break;
                        case 2 :
                            discountAmount = (product.discount/100*product.price);
                            break;
                    }

                    Double price = product.price - discountAmount;
                    product.buyPrice = price - Math.floor(product.category.getShareProfit()/100 * price);

                    if (product.approvedStatus.equals(Product.REJECTED)){
                        product.approvedStatus = Product.PENDING;
                        product.update();
                    }else{
                        ProductTmp dataTmp = new ProductTmp(product);
                        dataTmp.productDetail = detailTmp;
                        dataTmp.approvedStatus = Product.PENDING;
                        dataTmp.createdAt = new Date();
                        //dataTmp.userCms = getUserCms();
                        dataTmp.save();

                        if (product.getStock() < data.getStock()){
                            ProductStockTmp stockTmp = new ProductStockTmp(product, data.getStock() - product.getStock());
                            stockTmp.save();
                        }
                    }

                    txn.commit();
                    response.setBaseResponse(1, offset, 1, created, null);
                    return ok(Json.toJson(response));
                }
            } catch (Exception e) {
                errorMessage = e.getMessage();
                Logger.error("create", e);
                txn.rollback();
            } finally {
                txn.end();
            }
            response.setBaseResponse(0, 0, 0, errorMessage, null);
            return badRequest(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result updateStatus(Long id) {
        int authority = checkAccessAuthorization("merchant");
        if (authority == 200) {
            Merchant actor = checkMerchantAccessAuthorization();
            JsonNode json = request().body().asJson();
            Boolean status = json.findPath("active").asBoolean();
            Product product = Product.find.where().eq("is_deleted", false)
                    .eq("id", id)
                    .eq("merchant_id", actor.id)
                    .setMaxRows(1).findUnique();
            if (product != null){
                if (product.approvedStatus.equals(Product.AUTHORIZED)){
                    product.status = status;
                    product.update();
                    response.setBaseResponse(1, 0, 1, success, null);
                    return ok(Json.toJson(response));
                }else{
                    response.setBaseResponse(0, 0, 0, "Waiting for approval", null);
                    return badRequest(Json.toJson(response));
                }
            }
            response.setBaseResponse(0, 0, 0, notFound, null);
            return badRequest(Json.toJson(response));
        } else if (authority == 401) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result delete(Long id) {
        Merchant currentMember = checkMerchantAccessAuthorization();
        if (currentMember != null) {
            Product product = Product.find.where().eq("is_deleted", false)
                    .eq("id", id)
                    .eq("merchant_id", currentMember.id)
                    .setMaxRows(1).findUnique();
            if (product != null) {
                SalesOrderDetail sod = SalesOrderDetail.find.where().eq("product_id", id).setMaxRows(1).findUnique();
                if (sod == null){
                    product.isDeleted = true; // SOFT DELETE
                    product.update();

                    response.setBaseResponse(1, offset, 1, deleted, null);
                    return ok(Json.toJson(response));
                }
                response.setBaseResponse(0, 0, 0, "Cannot delete product that already sold", null);
                return badRequest(Json.toJson(response));
            }
            response.setBaseResponse(0, 0, 0, notFound, null);
            return notFound(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result detail(Long id) {
        Merchant actor = checkMerchantAccessAuthorization();
        if (actor != null) {
            Product model = Product.find.where()
                    .eq("id", id)
                    .eq("merchant_id", actor.id)
                    .setMaxRows(1).findUnique();
            if (model != null) {
                String message;
                try {
                    model.setRating();
//                    model.getProductDetails().get(0).setAttribute(true); //TODO
//                    response.setBaseResponse(1, offset, 1, success, new ObjectMapper().convertValue(model, MapProduct.class));
                    response.setBaseResponse(1, offset, 1, success, new MapProduct(model));
                    return ok(Json.toJson(response));
                } catch (Exception e) {
                    message = e.getMessage();
                    Logger.error("show", e);
                }
                response.setBaseResponse(0, 0, 0, error, message);
                return internalServerError(Json.toJson(response));
            }
            response.setBaseResponse(0, 0, 0, notFound, null);
            return notFound(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));

    }

    public static Result masterProduct() {
        Merchant actor = checkMerchantAccessAuthorization();
        if (actor != null) {
            String id = request().getQueryString("id");
            MapMasterProduct mmp = new MapMasterProduct();
            List<BaseAttribute> data = BaseAttribute.getAllData();
            List<Category> query = Category.find.where().eq("is_active", true).eq("is_deleted", false).eq("parent_id", null).order("sequence asc").findList();
            for (Category c : query) {
                c.childCategory = Category.recGetAllChildCategory(c.id);
            }

            List<Brand> brands = Brand.getAllData();

            if (id != null && !id.isEmpty()){
                Product model = Product.find.where()
                        .eq("id", Long.valueOf(id))
                        .eq("merchant_id", actor.id)
                        .setMaxRows(1).findUnique();
                model.setRating();
//                model.getProductDetails().get(0).setAttribute(true); //TODO

                mmp.setProduct(new ObjectMapper().convertValue(model, MapProduct.class));
            }

            List<Size> sizes = Size.find.where()
                    .eq("is_deleted", false)
                    .order("sequence asc").findList();

            mmp.setAttributes(new ObjectMapper().convertValue(data, MapAttributeAll[].class));
            mmp.setCategories(new ObjectMapper().convertValue(query, MapMasterCategory[].class));
            mmp.setBrands(new ObjectMapper().convertValue(brands, MapMasterBrand[].class));
            mmp.setSizes(new ObjectMapper().convertValue(sizes, MapSize[].class));

            response.setBaseResponse(data.size(), offset, data.size(), success, mmp);
            return ok(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }
    
    public static void UpdateMailchimpProduct(Product product) {
//        boolean mailchimpEnabled = Play.application().configuration().getBoolean("whizliz.social.mailchimp.enabled");
        if (MailchimpService.isEnabled()) {
    		String url = "https://www.whizliz.com/product/" + product.slug;
    		String imageUrl = Play.application().configuration().getString("whizliz.images.url") + product.imageUrl;
			List<MailchimpProductVariantRequest> productVariants = new ArrayList<MailchimpProductVariantRequest>();
			for (ProductDetailVariance detail : product.productDetail) {
				productVariants.add(new MailchimpProductVariantRequest(detail.id.toString(), detail.getProductName() + " " + detail.getColorName() + " " + detail.getSizeName(), url, detail.sku == null? "" : detail.sku, product.buyPrice, detail.totalStock, imageUrl, product.id.toString()));
			}
			MailchimpProductRequest mailchimpProduct = new MailchimpProductRequest(product.id.toString(), product.name, url, "", product.category.name, product.merchant.name, imageUrl, productVariants);
			ServiceResponse sresponse = MailchimpService.getInstance().UpdateProduct(mailchimpProduct);
			Logger.info("== BEGIN UPDATE MAILCHIMP PRODUCT ==");
			Logger.info(Tool.prettyPrint(Json.toJson(sresponse)));
			Logger.info("== END UPDATE MAILCHIMP PRODUCT ==");
        }
    }
    
    public static void DeleteMailchimpProduct(Product product) {
//        boolean mailchimpEnabled = Play.application().configuration().getBoolean("whizliz.social.mailchimp.enabled");
        if (MailchimpService.isEnabled()) {
			ServiceResponse sresponse = MailchimpService.getInstance().DeleteProduct(product.id.toString());
			Logger.info("== BEGIN DELETE MAILCHIMP PRODUCT ==");
			Logger.info(Tool.prettyPrint(Json.toJson(sresponse)));
			Logger.info("== END DELETE MAILCHIMP PRODUCT ==");
        }
    }
    
    public static void AddMailchimpProductVariant(Product product, ProductDetailVariance detail) {
//        boolean mailchimpEnabled = Play.application().configuration().getBoolean("whizliz.social.mailchimp.enabled");
        if (MailchimpService.isEnabled()) {
    		String url = "https://www.whizliz.com/product/" + product.slug;
    		String imageUrl = Play.application().configuration().getString("whizliz.images.url") + product.imageUrl;
    		ServiceResponse result = MailchimpService.getInstance().GetProduct(product.id.toString());
    		MailchimpProductVariantRequest mailchimpProductVariant = new MailchimpProductVariantRequest(detail.id.toString(), detail.getProductName() + " " + detail.getColorName() + " " + detail.getSizeName(), url, detail.sku == null? "" : detail.sku, product.buyPrice, detail.totalStock, imageUrl, product.id.toString());
    		if (result.getCode() == 404) {
    			List<MailchimpProductVariantRequest> variants = new ArrayList<MailchimpProductVariantRequest>();
    			variants.add(mailchimpProductVariant);
    			MailchimpProductRequest mailchimpProduct = new MailchimpProductRequest(product.id.toString(), product.name, url, "", product.category.name, product.merchant.name, imageUrl, variants);
    			ServiceResponse sresponse = MailchimpService.getInstance().AddProduct(mailchimpProduct);
    			Logger.info("== BEGIN ADD MAILCHIMP PRODUCT ==");
    			Logger.info(Tool.prettyPrint(Json.toJson(sresponse)));
    			Logger.info("== END ADD MAILCHIMP PRODUCT ==");
    		}
    		else {
    			ServiceResponse sresponse = MailchimpService.getInstance().AddOrUpdateProductVariant(mailchimpProductVariant);
    			Logger.info("== BEGIN ADD MAILCHIMP PRODUCT VARIANT ==");
    			Logger.info(Tool.prettyPrint(Json.toJson(sresponse)));
    			Logger.info("== END ADD MAILCHIMP PRODUCT VARIANT ==");
    		}
        }
    }
    
    public static void UpdateMailchimpProductVariant(Product product, ProductDetailVariance detail) {
//        boolean mailchimpEnabled = Play.application().configuration().getBoolean("whizliz.social.mailchimp.enabled");
        if (MailchimpService.isEnabled()) {
    		String url = "https://www.whizliz.com/product/" + product.slug;
    		String imageUrl = Play.application().configuration().getString("whizliz.images.url") + product.imageUrl;
    		MailchimpProductVariantRequest mailchimpProductVariant = new MailchimpProductVariantRequest(detail.id.toString(), detail.getProductName() + " " + detail.getColorName() + " " + detail.getSizeName(), url, detail.sku == null? "" : detail.sku, product.buyPrice, detail.totalStock, imageUrl, product.id.toString());
    		ServiceResponse sresponse = MailchimpService.getInstance().AddOrUpdateProductVariant(mailchimpProductVariant);
    		Logger.info("== BEGIN UPDATE MAILCHIMP PRODUCT VARIANT ==");
			Logger.info(Tool.prettyPrint(Json.toJson(sresponse)));
			Logger.info("== END UPDATE MAILCHIMP PRODUCT VARIANT ==");
        }
    }
    
    public static void DeleteMailchimpProductVariant(ProductDetailVariance detail) {
//        boolean mailchimpEnabled = Play.application().configuration().getBoolean("whizliz.social.mailchimp.enabled");
        if (MailchimpService.isEnabled()) {
    		ServiceResponse sresponse = MailchimpService.getInstance().DeleteProductVariant(detail.getProductId().toString(), detail.id.toString());
    		Logger.info("== BEGIN DELETE MAILCHIMP PRODUCT VARIANT ==");
			Logger.info(Tool.prettyPrint(Json.toJson(sresponse)));
			Logger.info("== END DELETE MAILCHIMP PRODUCT VARIANT ==");
        }
    }
    
	@Security.Authenticated(Secured.class)
    public static Result productsByCategory(int page, int pageSize, String sortBy, String order, Long categoryId) {
    	try {
    		Page<Product> p = Product.page2(page, pageSize, sortBy, order, categoryId);
            response.setBaseResponse(p.getTotalPageCount(), p.getPageIndex(), pageSize, success, new ObjectMapper().convertValue(p.getList(), com.hokeba.mapping.response.kiosk.MapProduct[].class));
            return ok(Json.toJson(response));
		} catch (Exception e) {
			return internalServerError(e.getMessage());
		}
    }
    
	@Security.Authenticated(Secured.class)
    public static Result productById(Long id, Integer recomendationsTotal) {
    	try {
			Product product = Product.find.byId(id);
			if (product != null) {
				List<Product> products = Product.find.where().ne("id", id).findList();
				List<Long> productIds = new ArrayList<>(); 
				for (Product p : products) {
					productIds.add(p.id);
				}
				
				productIds = getRandomElement(productIds, recomendationsTotal);
				products = Product.find.where().eq("productType", 4).in("id", productIds).findList();
				
				Map<String, Object> map = new LinkedHashMap<>();
				map.put("detail", new ObjectMapper().convertValue(product, com.hokeba.mapping.response.kiosk.MapProduct.class));
				map.put("other_products", new ObjectMapper().convertValue(products, com.hokeba.mapping.response.kiosk.MapProduct[].class));
				
				response.setBaseResponse(1, offset, 1, success, map);
		        return ok(Json.toJson(response));
			} else {
				response.setBaseResponse(0, 0, 0, notFound, null);
		        return notFound(Json.toJson(response));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return internalServerError(e.getMessage());
		}
    }
    
	@Security.Authenticated(Secured.class)
    public static Result productsByType(Long type) {
    	try {
    		Page<Product> p = Product.productByType(type);
            response.setBaseResponse(p.getTotalRowCount(), p.getPageIndex(), 100, success, new ObjectMapper().convertValue(p.getList(), com.hokeba.mapping.response.kiosk.MapProduct[].class));
            return ok(Json.toJson(response));
		} catch (Exception e) {
			return internalServerError(e.getMessage());
		}
    }
    
	public static List<Long> getRandomElement(List<Long> list, int totalItems) {
		Random rand = new Random();

		if (list.size() < totalItems) {
			totalItems = list.size();
		}
		
		List<Long> newList = new ArrayList<>();
		for (int i = 0; i < totalItems; i++) {
			int randomIndex = rand.nextInt(list.size());
			newList.add(list.get(randomIndex));
			list.remove(randomIndex);
		}
		return newList;
	}    

}
