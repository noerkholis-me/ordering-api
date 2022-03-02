package controllers.users;

import com.avaje.ebean.Query;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.cleverage.elasticsearch.IndexQuery;
import com.github.cleverage.elasticsearch.IndexResults;
import com.hokeba.api.*;
import com.hokeba.http.HTTPRequest3;
import com.hokeba.http.SSLCertificateValidation;
import com.hokeba.http.response.global.ServiceResponse;
import com.hokeba.mapping.response.*;
import com.hokeba.social.requests.FirebaseNotificationHelper;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import controllers.BaseController;
import models.*;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortOrder;
import play.Logger;
import play.libs.Json;
import play.libs.F.Function;
import play.libs.F.Promise;
import play.libs.ws.WS;
import play.libs.ws.WSRequestHolder;
import play.libs.ws.WSResponse;
import play.mvc.Result;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.xml.ws.soap.AddressingFeature.Responses;

/**
 * Created by hendriksaragih on 4/8/17.
 */
@Api(value = "/users/products", description = "Products")
public class ProductsController extends BaseController {
    @SuppressWarnings("rawtypes")
    private static BaseResponse response = new BaseResponse();

    public static Result index() {
        return ok();
    }

    @ApiOperation(value = "Get all product detail.", notes = "Returns list of product detail.\n" + swaggerInfo
            + "", response = ProductDetail.class, responseContainer = "List", httpMethod = "GET")
    public static Result allDetail(Long categoryId, Long brandId, Long merchantId, String filter, String sort, int offset, int limit) {
        int authority = checkAccessAuthorization("all");
        if (authority == 200 || authority == 203) {
            Query<Product> query = Product.getQueryProductList();
            BaseResponse<Product> responseIndex;
            try {
                responseIndex = Product.getData(categoryId, brandId, merchantId, query, sort, filter, offset, limit);
                return ok(Json.toJson(responseIndex));
            } catch (IOException e) {
                Logger.error("allDetail", e);
            }
        } else if (authority == 403) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    @ApiOperation(value = "Get all top sales Product", notes = "Returns list of product.\n" + swaggerInfo
            + "", response = ProductDetail.class, responseContainer = "List", httpMethod = "GET")
    public static Result topSales(int limit) {
        int authority = checkAccessAuthorization("all");
        if (authority == 200 || authority == 203) {
            List<Product> data = Product.find.where().eq("is_deleted", false)
                    .eq("first_po_status", 1)
                    .eq("approved_status", "A")
                    .eq("status", true)
//                    .eq("is_show", true)
                    .gt("item_count", 0)
                    .orderBy("num_of_order DESC")
                    .setMaxRows(limit)
                    .findList();
            response.setBaseResponse(data.size(), offset, limit, success,
                    new ObjectMapper().convertValue(data, MapProductList[].class));
            return ok(Json.toJson(response));
        } else if (authority == 403) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    @ApiOperation(value = "Get all product detail.", notes = "Returns list of product detail.\n" + swaggerInfo
            + "", response = ProductDetail.class, responseContainer = "List", httpMethod = "GET")
    public static Result allDetailV2(Long categoryId, Long brandId, Long merchantId, String filter, String sort, int offset, int limit) {
        int authority = checkAccessAuthorization("all");
        if (authority == 200 || authority == 203) {
            try {
                IndexQuery<indexing.Product> indexQuery = indexing.Product.find.query();
//                Logger.info("sort : " + sort);
                if (!sort.isEmpty()){
                    ApiSort[] sorts = new ObjectMapper().readValue(sort, ApiSort[].class);
                    for (ApiSort apiSort : sorts) {
                        if (apiSort.getDirection().equalsIgnoreCase("asc")){
                            indexQuery.addSort(apiSort.getProperty(), SortOrder.ASC);
                        }else{
                            indexQuery.addSort(apiSort.getProperty(), SortOrder.DESC);
                        }
                    }
                }
                BoolQueryBuilder builder = QueryBuilders.boolQuery();
                if (!"".equals(filter)) {
                    ApiFilters filters = new ObjectMapper().readValue(filter, ApiFilters.class);
                    ApiFilter[] apiFilters = filters.getFilters();
                    for (int i = 0; i < apiFilters.length; i++) {
                        ApiFilter apiFilter = apiFilters[i];
                        if (apiFilter.getProperty().equals("slug")){
                            String slug = apiFilter.getValues()[0].getValue().toString();
                            BannerList list = Product.getBannerList(slug);
                            if (list != null){
                                if (list.getProducts() != null){
                                    List<Long> ids = new ArrayList<>();
                                    for(Product dt : list.getProducts()){
                                        ids.add(dt.id);
                                    }
                                    if (ids.size() > 0){
                                        builder.must(QueryBuilders.inQuery("id", ids));
                                    }
                                }
                                if (list.getMerchants() != null){
                                    List<Long> ids = new ArrayList<>();
                                    for(Merchant dt : list.getMerchants()){
                                        ids.add(dt.id);
                                    }
                                    if (ids.size() > 0){
                                        builder.must(QueryBuilders.inQuery("merchant.id", ids));
                                    }
                                }
                                if (list.getCategories1() != null){
                                    List<Long> ids = new ArrayList<>();
                                    for(Category dt : list.getCategories1()){
                                        ids.add(dt.id);
                                    }
                                    if (ids.size() > 0){
                                        builder.must(QueryBuilders.inQuery("grandParentCategory.id", ids));
                                    }
                                }
                                if (list.getCategories2() != null){
                                    List<Long> ids = new ArrayList<>();
                                    for(Category dt : list.getCategories2()){
                                        ids.add(dt.id);
                                    }
                                    if (ids.size() > 0){
                                        builder.must(QueryBuilders.inQuery("parentCategory.id", ids));
                                    }
                                }
                                if (list.getCategories3() != null){
                                    List<Long> ids = new ArrayList<>();
                                    for(Category dt : list.getCategories3()){
                                        ids.add(dt.id);
                                    }
                                    if (ids.size() > 0){
                                        builder.must(QueryBuilders.inQuery("category.id", ids));
                                    }
                                }
                                if (list.getBrands() != null){
                                    List<Long> ids = new ArrayList<>();
                                    for(Brand dt : list.getBrands()){
                                        ids.add(dt.id);
                                    }
                                    if (ids.size() > 0){
                                        builder.must(QueryBuilders.inQuery("brand.id", ids));
                                    }
                                }
                            }

                            continue;
                        }

                        switch (apiFilter.getOperator()){
                            case "not_equals":
                                builder.mustNot(QueryBuilders.termQuery(apiFilter.getProperty(), apiFilter.getValues()[0].getValue()));
                                break;
                            case "like":
                                String val = apiFilter.getValues()[0].getValue().toString().isEmpty() ? "*" : apiFilter.getValues()[0].getValue().toString();
                                builder.must(QueryBuilders.queryString(val));
                                break;
                            case "less_than_or_equals":
                                builder.must(QueryBuilders.rangeQuery(apiFilter.getProperty()).to(apiFilter.getValues()[0].getValue()));
                                break;
                            case "greater_than_or_equals":
                                builder.must(QueryBuilders.rangeQuery(apiFilter.getProperty()).from(apiFilter.getValues()[0].getValue()));
                                break;
                            case "in":
                                List<Object> ids = new ArrayList<>();
                                for (ApiFilterValue af : apiFilter.getValues()) {
                                    ids.add(af.getValue());
                                }
                                builder.must(QueryBuilders.inQuery(apiFilter.getProperty(), ids.toArray()));
                                break;
                            default:
                                builder.must(QueryBuilders.inQuery(apiFilter.getProperty(), apiFilter.getValues()[0].getValue().toString()));
                                break;
                        }

                    }
                }

                List<MapVariant> resFilter = new ArrayList<>();
                ObjectNode result = Json.newObject();
                if (categoryId != 0L){
                    Category data = Category.find.where().eq("id", categoryId).findUnique();
                    if (data != null){
                        data.viewCount = data.viewCount + 1;
                        data.update();
                    }

                    List<SubCategoryBannerDetail> banners = SubCategoryBannerDetail.find
                            .fetch("subCategoryBanner")
                            .where()
                            .eq("subCategoryBanner.category.id", categoryId)
                            .eq("subCategoryBanner.status", true)
                            .eq("subCategoryBanner.isDeleted", false)
                            .orderBy("sequence ASC").findList();
                    result.put("banner", Json.toJson(new ObjectMapper().convertValue(banners, MapCategoryBanerMenuDetail[].class)));
                    List<Brand> brands = Brand.getHomePage();
                    result.put("brand_banner", Json.toJson(new ObjectMapper().convertValue(brands, MapBrand[].class)));
                    result.put("brand", Json.toJson(null));
                    resFilter = Arrays.asList(Product.fetchAttributeData(categoryId));

                    builder.must(QueryBuilders.matchQuery("category.id", categoryId));
                }
                if (brandId != 0L){
                    Brand data = Brand.find.where().eq("id", brandId).findUnique();
                    if (data != null){
                        data.viewCount = data.viewCount + 1;
                        data.update();
                    }
                    result.put("brand", Json.toJson(new ObjectMapper().convertValue(data, MapBrand.class)));
                    result.put("banner", Json.toJson(new ArrayList<>()));
                    builder.must(QueryBuilders.matchQuery("brand.id", brandId));
                }

                builder.must(QueryBuilders.matchQuery("is_deleted", false));
                builder.must(QueryBuilders.matchQuery("status", true));
                builder.must(QueryBuilders.matchQuery("is_show", true));
//                builder.must(QueryBuilders.matchQuery("first_po_status", 1));
//                builder.must(QueryBuilders.rangeQuery("item_count").from(1));
                builder.must(QueryBuilders.matchQuery("approved_status", "A"));

                if (limit != 0){
                    indexQuery.size(limit);
                    
                    if (offset != 0){
                        indexQuery.from(offset * limit);
                    }
                }
                indexQuery.setBuilder(builder);
                IndexResults<indexing.Product> results = indexing.Product.find.search(indexQuery);
                List<MapProductList> mapProductList = new ArrayList<MapProductList>();
                ObjectMapper mapper = new ObjectMapper();

//                if (!"".equals(sort)) {
//	                List<Long> ids = new ArrayList<>();
//	                results.getResults().forEach(p-> ids.add(Long.valueOf(p.id)));
//	                Query<Product> query = Product.find.where().in("id", ids).query();
//                    ApiSort[] sorts = new ObjectMapper().readValue(sort, ApiSort[].class);
//                    for (ApiSort apiSort : sorts) {
//                        query = query.orderBy(apiSort.getProperty() + " " + apiSort.getDirection());
//                    }
//                    query.findList().forEach(p -> mapProductList.add(mapper.convertValue(p, MapProductList.class)));
//                }
//                else
//                	results.getResults().forEach(p-> mapProductList.add(mapper.convertValue(Product.find.byId(Long.valueOf(p.id)), MapProductList.class)));
//                
//                result.put("result", Json.toJson(mapProductList));
//                result.put("filter", Json.toJson(resFilter));
//                BaseResponse<Product> response = new BaseResponse<>();
//                response.setData(result);
//                response.setMeta((int)results.getTotalCount(), offset, limit);
//                response.setMessage("Success");
//
//                return ok(Json.toJson(response));
//            }catch (IOException e) {
//                Logger.error("allDetail", e);
//            }
//
//          } else if (authority == 403) {
//            response.setBaseResponse(0, 0, 0, forbidden, null);
//            return forbidden(Json.toJson(response));
//        }
//        response.setBaseResponse(0, 0, 0, unauthorized, null);
//        return unauthorized(Json.toJson(response));
//    }
//    
//    @ApiOperation(value = "Get all product detail.", notes = "Returns list of product detail.\n" + swaggerInfo
//            + "", response = ProductDetail.class, responseContainer = "List", httpMethod = "GET")
//    public static Result allDetailV3(Long categoryId, Long brandId, Long productLabelId, Long merchantId, String filter, String sort, int offset, int limit) {
//        int authority = checkAccessAuthorization("all");
//        if (authority == 200 || authority == 203) {
//            try {
//                IndexQuery<indexing.Product> indexQuery = indexing.Product.find.query();
//                if (!sort.isEmpty()){
//                    ApiSort[] sorts = new ObjectMapper().readValue(sort, ApiSort[].class);
//                    for (ApiSort apiSort : sorts) {
//                        if (apiSort.getDirection().equalsIgnoreCase("asc")){
//                            indexQuery.addSort(apiSort.getProperty(), SortOrder.ASC);
//                        }else{
//                            indexQuery.addSort(apiSort.getProperty(), SortOrder.DESC);
//                        }
//                    }
//                }
//
//
//                BoolQueryBuilder builder = QueryBuilders.boolQuery();
//                if (!"".equals(filter)) {
//                    ApiFilters filters = new ObjectMapper().readValue(filter, ApiFilters.class);
//                    ApiFilter[] apiFilters = filters.getFilters();
//                    for (int i = 0; i < apiFilters.length; i++) {
//                        ApiFilter apiFilter = apiFilters[i];
//                        if (apiFilter.getProperty().equals("slug")){
//                            String slug = apiFilter.getValues()[0].getValue().toString();
//                            BannerList list = Product.getBannerList(slug);
//                            if (list != null){
//                                if (list.getProducts() != null){
//                                    List<Long> ids = new ArrayList<>();
//                                    for(Product dt : list.getProducts()){
//                                        ids.add(dt.id);
//                                    }
//                                    if (ids.size() > 0){
//                                        builder.must(QueryBuilders.inQuery("id", ids));
//                                    }
//                                }
//                                if (list.getMerchants() != null){
//                                    List<Long> ids = new ArrayList<>();
//                                    for(Merchant dt : list.getMerchants()){
//                                        ids.add(dt.id);
//                                    }
//                                    if (ids.size() > 0){
//                                        builder.must(QueryBuilders.inQuery("merchant.id", ids));
//                                    }
//                                }
//                                if (list.getCategories1() != null){
//                                    List<Long> ids = new ArrayList<>();
//                                    for(Category dt : list.getCategories1()){
//                                        ids.add(dt.id);
//                                    }
//                                    if (ids.size() > 0){
//                                        builder.must(QueryBuilders.inQuery("grandParentCategory.id", ids));
//                                    }
//                                }
//                                if (list.getCategories2() != null){
//                                    List<Long> ids = new ArrayList<>();
//                                    for(Category dt : list.getCategories2()){
//                                        ids.add(dt.id);
//                                    }
//                                    if (ids.size() > 0){
//                                        builder.must(QueryBuilders.inQuery("parentCategory.id", ids));
//                                    }
//                                }
//                                if (list.getCategories3() != null){
//                                    List<Long> ids = new ArrayList<>();
//                                    for(Category dt : list.getCategories3()){
//                                        ids.add(dt.id);
//                                    }
//                                    if (ids.size() > 0){
//                                        builder.must(QueryBuilders.inQuery("category.id", ids));
//                                    }
//                                }
//                                if (list.getBrands() != null){
//                                    List<Long> ids = new ArrayList<>();
//                                    for(Brand dt : list.getBrands()){
//                                        ids.add(dt.id);
//                                    }
//                                    if (ids.size() > 0){
//                                        builder.must(QueryBuilders.inQuery("brand.id", ids));
//                                    }
//                                }
//                            }
//
//                            continue;
//                        }
//
//                        switch (apiFilter.getOperator()){
//                            case "not_equals":
//                                builder.mustNot(QueryBuilders.termQuery(apiFilter.getProperty(), apiFilter.getValues()[0].getValue()));
//                                break;
//                            case "like":
//                                String val = apiFilter.getValues()[0].getValue().toString().isEmpty() ? "*" : apiFilter.getValues()[0].getValue().toString();
//                                builder.must(QueryBuilders.queryString(val));
//                                break;
//                            case "less_than_or_equals":
//                                builder.must(QueryBuilders.rangeQuery(apiFilter.getProperty()).to(apiFilter.getValues()[0].getValue()));
//                                break;
//                            case "greater_than_or_equals":
//                                builder.must(QueryBuilders.rangeQuery(apiFilter.getProperty()).from(apiFilter.getValues()[0].getValue()));
//                                break;
//                            case "in":
//                                List<Object> ids = new ArrayList<>();
//                                for (ApiFilterValue af : apiFilter.getValues()) {
//                                    ids.add(af.getValue());
//                                }
//                                builder.must(QueryBuilders.inQuery(apiFilter.getProperty(), ids.toArray()));
//                                break;
//                            default:
//                                builder.must(QueryBuilders.inQuery(apiFilter.getProperty(), apiFilter.getValues()[0].getValue().toString()));
//                                break;
//                        }
//
//                    }
//                }
//
//                List<MapVariant> resFilter = new ArrayList<>();
//                ObjectNode result = Json.newObject();
//                if (categoryId != 0L){
//                    Category data = Category.find.where().eq("id", categoryId).findUnique();
//                    if (data != null){
//                        data.viewCount = data.viewCount + 1;
//                        data.update();
//                    }
//
//                    List<SubCategoryBannerDetail> banners = SubCategoryBannerDetail.find
//                            .fetch("subCategoryBanner")
//                            .where()
//                            .eq("subCategoryBanner.category.id", categoryId)
//                            .eq("subCategoryBanner.status", true)
//                            .eq("subCategoryBanner.isDeleted", false)
//                            .orderBy("sequence ASC").findList();
//                    result.put("banner", Json.toJson(new ObjectMapper().convertValue(banners, MapCategoryBanerMenuDetail[].class)));
//                    List<Brand> brands = Brand.getHomePage();
//                    result.put("brand_banner", Json.toJson(new ObjectMapper().convertValue(brands, MapBrand[].class)));
//                    result.put("brand", Json.toJson(null));
//                    resFilter = Arrays.asList(Product.fetchAttributeData(categoryId));
//
//                    builder.must(QueryBuilders.matchQuery("category.id", categoryId));
//                }
//                if (brandId != 0L){
//                    Brand data = Brand.find.where().eq("id", brandId).findUnique();
//                    if (data != null){
//                        data.viewCount = data.viewCount + 1;
//                        data.update();
//                    }
//                    result.put("brand", Json.toJson(new ObjectMapper().convertValue(data, MapBrand.class)));
//                    result.put("banner", Json.toJson(new ArrayList<>()));
//                    builder.must(QueryBuilders.matchQuery("brand.id", brandId));
//                }
//
//                builder.must(QueryBuilders.matchQuery("is_deleted", false));
//                builder.must(QueryBuilders.matchQuery("status", true));
//                builder.must(QueryBuilders.matchQuery("is_show", true));
////                builder.must(QueryBuilders.matchQuery("first_po_status", 1));
////                builder.must(QueryBuilders.rangeQuery("item_count").from(1));
//                builder.must(QueryBuilders.matchQuery("approved_status", "A"));
//
//                if (limit != 0){
//                    indexQuery.size(limit);
//
//                    if (offset != 0){
//                        indexQuery.from(offset * limit);
//                    }
//                }
//                indexQuery.setBuilder(builder);
//                IndexResults<indexing.Product> results = indexing.Product.find.search(indexQuery);
//
//                List<Long> ids = new ArrayList<>();
//                results.getResults().forEach(p-> ids.add(Long.valueOf(p.id)));
//                Query<Product> query = Product.find.where().in("id", ids).eq("is_deleted", false).query();
                

                if (!"".equals(sort)) {
	                List<Long> ids = new ArrayList<>();
	                results.getResults().forEach(p-> ids.add(Long.valueOf(p.id)));
	                Query<Product> query = Product.find.where().in("id", ids).query();
                    ApiSort[] sorts = new ObjectMapper().readValue(sort, ApiSort[].class);
                    for (ApiSort apiSort : sorts) {
                        query = query.orderBy(apiSort.getProperty() + " " + apiSort.getDirection());
                    }
                    query.findList().forEach(p -> mapProductList.add(mapper.convertValue(p, MapProductList.class)));
                }
                else
                	results.getResults().forEach(p-> mapProductList.add(mapper.convertValue(Product.find.byId(Long.valueOf(p.id)), MapProductList.class)));
                
                result.put("result", Json.toJson(mapProductList));
                result.put("filter", Json.toJson(resFilter));
                BaseResponse<Product> response = new BaseResponse<>();
                response.setData(result);
                response.setMeta((int)results.getTotalCount(), offset, limit);
                response.setMessage("Success");

                return ok(Json.toJson(response));
            }catch (IOException e) {
                Logger.error("allDetail", e);
            }

        } else if (authority == 403) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    @ApiOperation(value = "Get fast search product.", notes = "Returns list of product detail.\n" + swaggerInfo
            + "", response = ProductDetail.class, responseContainer = "List", httpMethod = "GET")
    public static Result fastSearch(String query, int offset, int limit) {
        int authority = checkAccessAuthorization("all");
        if (authority == 200 || authority == 203) {
            IndexQuery<indexing.Product> indexQuery = indexing.Product.find.query();
            indexQuery.setBuilder(QueryBuilders.queryString(query))
                    .from(offset)
                    .size(limit);
            IndexResults<indexing.Product> results = indexing.Product.find.search(indexQuery);

            response.setBaseResponse((int)results.getTotalCount(), offset, limit, success, new ObjectMapper().convertValue(results.getResults(), MapProductFastSearch[].class));
            return ok(Json.toJson(response));

          } else if (authority == 403) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result detail(Long id) {
        int authority = checkAccessAuthorization("all");
        if (authority == 200) {
            Product model = Product.find.where().eq("id", id).eq("is_deleted", false).eq("first_po_status", 1)
                    .eq("approved_status", Product.AUTHORIZED)
                    .eq("status", true)
                    .setMaxRows(1).findUnique();
            if (model != null) {
                String message;
                try {
                    ObjectNode result = Json.newObject();
                    model.setRating();
                    model.setVariant();
                    model.setColors();
//                    model.getProductDetails().get(0).setAttribute(); //TODO
                    result.put("filter", Json.toJson(new ArrayList<>()));
                    result.put("product_data", Json.toJson(new ObjectMapper().convertValue(model, MapProduct.class)));
                    result.put("product_groups", Json.toJson(new ObjectMapper().convertValue(Product.getRelatedGroups(model.id, model.productGroup), MapProductList[].class)));
                    result.put("product_variants", Json.toJson(new ObjectMapper().convertValue(model.productVariants, MapVariantGroup[].class)));
                    result.put("product_sizes", Json.toJson(new ObjectMapper().convertValue(model.sizes, MapSize[].class)));
                    result.put("product_colors", Json.toJson(new ObjectMapper().convertValue(model.productColors, MapProductColor[].class)));

                    Product.incrementViewCount(id, model.viewCount + 1);

                    response.setBaseResponse(1, offset, 1, success, result);
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

        } else if (authority == 403) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }
    
    public static Result detailV2(Long id) {
        int authority = checkAccessAuthorization("all");
        if (authority == 200) {
            Product model = Product.find.where().eq("id", id).eq("is_deleted", false)
                    .eq("approved_status", Product.AUTHORIZED)
                    .eq("status", true)
                    .setMaxRows(1).findUnique();
            
            if (model != null) {
                String message;
                try {                 
                    Product.incrementViewCount(id, model.viewCount + 1);
                    response.setBaseResponse(1, offset, 1, success, new MapProductWithDetail(model));
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

        } else if (authority == 403) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result reviews() {
        Member currentMember = checkMemberAccessAuthorization();
        if (currentMember != null) {
            JsonNode json = request().body().asJson();

            Product product = Product.find.byId(json.findPath("product_id").asLong());
            if (product != null){
                ProductReview exists = ProductReview.find.where()
                        .eq("product", product)
                        .eq("member", currentMember)
                        .setMaxRows(1).findUnique();
                if (exists == null){
                    ProductReview productReview = new ProductReview();
                    productReview.title = json.findPath("title").asText();
                    productReview.comment = json.findPath("comment").asText();
                    productReview.rating = json.findPath("rating").asInt();
                    productReview.product = product;
                    productReview.member = currentMember;
                    productReview.approvedStatus = "P";
                    productReview.isActive = true;
                    productReview.save();
                    response.setBaseResponse(1, offset, 1, success, null);
                    return ok(Json.toJson(response));
                }
                response.setBaseResponse(0, 0, 0, "Already Reviewed", null);
                return notFound(Json.toJson(response));
            }
            response.setBaseResponse(0, 0, 0, notFound, null);
            return notFound(Json.toJson(response));

        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result saveWhislist() {
        Member currentMember = checkMemberAccessAuthorization();
        if (currentMember != null) {
            JsonNode json = request().body().asJson();
            if (json.has("product_id")) {
                Long prod = json.get("product_id").asLong();
                if (WishList.find.where().eq("product_id", prod).eq("is_deleted", false).eq("member", currentMember).findRowCount() == 0){
                	List<ProductDetailVariance>  getStocks = ProductDetailVariance.find.where().eq("product_id", prod).eq("is_deleted", false).findList();
                	Product getProduct = Product.find.byId(prod);
                	WishList model = new WishList();
                    model.member = currentMember;
                    model.product = getProduct;
                    long totalStock = 0;
                    for (ProductDetailVariance getStock: getStocks) {
                    	totalStock = totalStock + getStock.totalStock;
                    }
                    System.out.println(totalStock);
                    if(totalStock > 0) {
                    	model.stockHistory = true;
                    } else if(totalStock == 0){
                    	model.stockHistory = false;
                    }
//                    if(getStock.totalStock > 0) {
//                    	model.stockHistory = true;
//                    } else {
//                    	model.stockHistory = false;
//                    }
                    model.priceHistory = getProduct.price;
                    model.notificationCount = 0;
                    model.save();

                    response.setBaseResponse(1, offset, 1, created, null);
                    return ok(Json.toJson(response));
                }
                response.setBaseResponse(0, 0, 0, "Duplicate product", null);
                return badRequest(Json.toJson(response));

            }
            response.setBaseResponse(0, 0, 0, inputParameter, null);
            return badRequest(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));

    }

    public static Result getWhislist(){
        Member actor = checkMemberAccessAuthorization();
        FirebaseNotificationHelper notificationHelper = new FirebaseNotificationHelper();
        if (actor != null) {
            List<WishList> data = WishList.find.where()
                    .eq("member", actor)
                    .eq("is_deleted", false)
                    .orderBy("created_at DESC")
                    .findList();

            List<Product> results = new ArrayList<>();
//            data.forEach(r-> results.add(r.product));
            for (WishList wishlist : data) {
            	Product target = wishlist.product;
				if (target != null && !target.isDeleted && target.getIsActive()) {
					results.add(target);
				}
				
				List<ProductDetailVariance> target2 = ProductDetailVariance.find.where().eq("product_id", target.id).eq("is_deleted", false).findList();
				long totalStock = 0;
				for(ProductDetailVariance getStock : target2) {
					totalStock = totalStock + getStock.totalStock;
				}
				if(totalStock == 1 && wishlist.notificationCount == 0) {
					String title = "Wishlist Notification";
					String message = target.name + " stoknya tinggal 1 lagi nih! Yuk, checkout sekarang sebelum kehabisan";
					ObjectNode ob = Json.newObject();
					ob.put("data", target.description);
	    			ObjectNode type = ob;
	    			String topic = "wishlist-"+actor.id;
	    			String screenMobile = "/WishlistPage";
					notificationHelper.sendToTopic(title, message, type, topic, screenMobile);
					wishlist.notificationCount = 1;
					wishlist.save();
				} else if(!target.price.equals(wishlist.priceHistory) ) {
					String title = "Wishlist Notification";
					String message = "Hooray! " + target.name + " favoritmu harganya sudah diupdate nih! Cek sekarang yuk!";
					ObjectNode ob = Json.newObject();
					ob.put("data", target.description);
	    			ObjectNode type = ob;
	    			String topic = "wishlist-"+actor.id;
	    			String screenMobile = "/WishlistPage";
					notificationHelper.sendToTopic(title, message, type, topic, screenMobile);
					wishlist.priceHistory = target.price;
					wishlist.save();
				} else if(wishlist.stockHistory == false) {
					if(totalStock > 0) {
						String title = "Wishlist Notification";
						String message = "Hooray! stok " + target.name + " favoritmu telah tersedia kembali. Yuk, Checkout sekarang jangan sampai kehabisan";
						ObjectNode ob = Json.newObject();
						ob.put("data", target.description);
		    			ObjectNode type = ob;
		    			String topic = "wishlist-"+actor.id;
		    			String screenMobile = "/WishlistPage";
						notificationHelper.sendToTopic(title, message, type, topic, screenMobile);
						wishlist.stockHistory = true;
						if(totalStock > 1) {
							wishlist.notificationCount = 0;
						}
						wishlist.save();
					}
				} else if(totalStock == 0) {
					wishlist.stockHistory = false;
					wishlist.notificationCount = 0;
					wishlist.save();
					
				}
			}
            response.setBaseResponse(results.size(), offset, results.size(), success, new ObjectMapper().convertValue(results, MapProductList[].class));
            return ok(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result deleteWishlist(Long id) {
        Member currentMember = checkMemberAccessAuthorization();
        if (currentMember != null) {
            WishList model = WishList.find.where().eq("member", currentMember).eq("is_deleted", false).eq("product_id", id).setMaxRows(1).findUnique();
            if (model != null) {
                model.isDeleted = true; // SOFT DELETE
                model.save();

                response.setBaseResponse(1, offset, 1, deleted, null);
                return ok(Json.toJson(response));
            }
            response.setBaseResponse(0, 0, 0, notFound, null);
            return notFound(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }
    
    public static Result getLatestUsdToIdrRate() {
		try {
		String url = "https://api.exchangeratesapi.io/latest?base=USD&symbols=IDR";
		OkHttpClient client = new OkHttpClient();
		Request request = new Request.Builder().url(url).get().build();
		Call call = client.newCall(request);
		Response responses = call.execute();
		System.out.println(responses.code());
		System.out.println(responses.body().source());
		JsonNode jsonResponse = new ObjectMapper().readValue(responses.body().string(), JsonNode.class);
		return ok(jsonResponse);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return badRequest("salah");
		}
	
    }

    public static Result retrieveRedeemables() {
    	int authority = checkAccessAuthorization("all");
        if (authority == 200 || authority == 203) {
        	List<Product> redeemables = Product.find.where()
        			.eq("is_deleted",false)
        			.eq("status",true)
        			.eq("approved_status", "A")
        			.eq("checkout_type",1)
                    .orderBy("created_at DESC")
        			.findList();
        	ObjectMapper map = new ObjectMapper();
        	response.setBaseResponse(redeemables.size(), offset, redeemables.size(), success,
        			map.convertValue(redeemables,MapProductList[].class));
            return ok(Json.toJson(response));
        			
        } else if (authority == 403) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

}
