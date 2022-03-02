package controllers.users;

import com.avaje.ebean.Query;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hokeba.api.ApiResponse;
import com.hokeba.api.BaseResponse;
import com.hokeba.mapping.response.MapArticle;
import com.hokeba.mapping.response.MapArticleComment;
import com.hokeba.mapping.response.MapArticleDetail;
import com.wordnik.swagger.annotations.ApiImplicitParam;
import com.wordnik.swagger.annotations.ApiImplicitParams;
import com.wordnik.swagger.annotations.ApiOperation;
import controllers.BaseController;
import models.Article;
import models.ArticleComment;
import models.Member;
import play.libs.Json;
import play.mvc.Result;

import java.io.IOException;

/**
 * Created by hendriksaragih on 3/24/17.
 */
public class ArticlesController extends BaseController {
    @SuppressWarnings("rawtypes")
    private static BaseResponse response = new BaseResponse();

    public static Result index() {
        return ok();
    }


    @SuppressWarnings("unchecked")
    @ApiOperation(value = "Get all article data.", notes = "Returns list of article.\n" + swaggerInfo
            + "", response = Article.class, responseContainer = "List", httpMethod = "GET")
    public static Result all(int offset, int limit) {
        int authority = checkAccessAuthorization("all");
        if (authority == 200||authority == 203) {
            Query<Article> query = Article.find.where().eq("is_deleted", false).eq("status", Article.PUBLISH).order("id DESC");
            BaseResponse<Article> responseIndex;
            try {
                responseIndex = ApiResponse.getInstance().setResponseV2(query, "", "", offset, limit);
                responseIndex.setData(new ObjectMapper().convertValue(responseIndex.getData(), MapArticle[].class));
                return ok(Json.toJson(responseIndex));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (authority == 403) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    @SuppressWarnings("unchecked")
    @ApiOperation(value = "Get all article data.", notes = "Returns list of article.\n" + swaggerInfo
            + "", response = Article.class, responseContainer = "List", httpMethod = "GET")
    public static Result popular(int limit) {
        int authority = checkAccessAuthorization("all");
        if (authority == 200||authority == 203) {
            Query<Article> query = Article.find.where().eq("is_deleted", false).eq("status", Article.PUBLISH).order("view_count DESC");
            BaseResponse<Article> responseIndex;
            try {
                responseIndex = ApiResponse.getInstance().setResponseV2(query, "", "", 0, limit);
                responseIndex.setData(new ObjectMapper().convertValue(responseIndex.getData(), MapArticle[].class));
                return ok(Json.toJson(responseIndex));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (authority == 403) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    public static Result showBySlug(String slug) {
        int authority = checkAccessAuthorization("all");
        if (authority == 200 || authority == 203) {
            Article model = Article.find.where().eq("is_deleted", false).eq("status", Article.PUBLISH)
                    .eq("slug", slug).setMaxRows(1).findUnique();
            if (model != null) {
                model.viewCount = model.viewCount + 1;
                model.update();

                model.setComments();
                ObjectMapper mapper = new ObjectMapper();
                response.setBaseResponse(1, offset, 1, success, mapper.convertValue(model, MapArticleDetail.class));
                return ok(Json.toJson(response));
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

    @SuppressWarnings("unchecked")
    public static Result getArticleComment(Long id, int offset, int limit){
        int authority = checkAccessAuthorization("all");
        if (authority == 200 || authority==203) {
            Query<ArticleComment> query = ArticleComment.find.where().eq("article_id", id)
                    .eq("is_deleted", false).eq("status", ArticleComment.APPROVED).eq("comment_parent_id", null).order("id DESC");
            BaseResponse<ArticleComment> responseIndex;
            try {
                responseIndex = ApiResponse.getInstance().setResponseV2(query, "", filter, offset, limit);
                responseIndex.setData(new ObjectMapper().convertValue(responseIndex.getData(), MapArticleComment[].class));
                return ok(Json.toJson(responseIndex));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (authority == 403) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    @SuppressWarnings("unchecked")
    public static Result getCommentByIdReplies(Long id, int offset, int limit){
        int authority = checkAccessAuthorization("all");
        if (authority == 200 || authority==203) {
            Query<ArticleComment> query = ArticleComment.find.where().eq("status", ArticleComment.APPROVED)
                    .eq("is_deleted", false).eq("comment_parent_id", id).order("id DESC");
            BaseResponse<ArticleComment> responseIndex;
            try {
                responseIndex = ApiResponse.getInstance().setResponseV2(query, "", "", offset, limit);
                responseIndex.setData(new ObjectMapper().convertValue(responseIndex.getData(), MapArticleComment[].class));
                return ok(Json.toJson(responseIndex));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (authority == 403) {
            response.setBaseResponse(0, 0, 0, forbidden, null);
            return forbidden(Json.toJson(response));
        }
        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }

    @ApiOperation(value = "Post a comment to specific article.", notes = "Post a comment to specific article.\n"
            + swaggerInfo + "\n\n" + swaggerParamInfo + "", response = ArticleComment.class, httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "article_id", dataType = "number", required = true, paramType = "path", value = "Article id"),
            @ApiImplicitParam(name = "body", dataType = "ArticleComment", required = true, paramType = "body", value = "Comment data") })
    public static Result postComment() {
        boolean canAccess = false;
        boolean isUser = false;
        Long commenterId = null;
        if (request().headers().get(API_KEY) != null) {
            Member actor = checkMemberAccessAuthorization();
            if (actor != null) {
                commenterId = actor.id;
                isUser = false;
                canAccess = true;
            }
        }
        if (canAccess) {
            JsonNode json = request().body().asJson();
            Article article = Article.find.where()
                    .eq("id", (json.findPath("article_id").asLong()))
                    .eq("is_deleted", false).eq("status", Article.PUBLISH)
                    .findUnique();
            if (article != null) {
                if (json.has("comment")) {
                    Long parentId = json.has("reply_to") ? json.findPath("reply_to").asLong() : null;
                    String comment = json.findPath("comment").asText().trim();
                    ArticleComment model = new ArticleComment(article, parentId, comment, commenterId, isUser);
                    String check = ArticleComment.validation(model);
                    if (check != null) {
                        response.setBaseResponse(0, 0, 0, check, null);
                        return badRequest(Json.toJson(response));
                    }
                    model.save();
                    response.setBaseResponse(1, offset, 1, created, new ObjectMapper().convertValue(model, MapArticleComment.class));
                    return ok(Json.toJson(response));
                }
                response.setBaseResponse(0, 0, 0, inputParameter, null);
                return notFound(Json.toJson(response));
            }
            response.setBaseResponse(0, 0, 0, notFound, null);
            return notFound(Json.toJson(response));
        }

        response.setBaseResponse(0, 0, 0, unauthorized, null);
        return unauthorized(Json.toJson(response));
    }
}
