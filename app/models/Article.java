package models;

import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Page;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hokeba.util.CommonFunction;
import com.hokeba.util.Constant;
import org.jsoup.Jsoup;
import play.data.validation.ValidationError;
import play.libs.Json;

import javax.persistence.*;
import java.beans.Transient;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by hendriksaragih on 2/4/17.
 */
@Entity
public class Article extends BaseModel {
    private static final long serialVersionUID = 1L;
    public static final String DRAFT = "1";
    public static final String PUBLISH = "2";
    public static final String INACTIVE = "3";
    private static final String LOG_TYPE = "ADMIN";
    private static final String LOG_TABLE_NAME = "article";

    
    public String name;
    public String title;
    public String slug;
    public String description;
    public String keyword;

    @JsonProperty("image_header_url")
    public String imageHeaderUrl;

    @JsonProperty("image_thumbnail_url")
    public String imageThumbnailUrl;
    
    @JsonProperty("image_home_url")
    public String imageHomeUrl;
    
    @JsonProperty("image_list_url")
    public String imageListUrl;

    @JsonProperty("image_name")
    public String imageName;

    @JsonProperty("image_title")
    public String imageTitle;

    @JsonProperty("image_alternate")
    public String imageAlternate;

    @JsonProperty("image_description")
    public String imageDescription;

    @JsonProperty("view_count")
    public Integer viewCount = 0;

    @Column(columnDefinition = "TEXT")
    public String content;

    @JsonProperty("short_description")
    @Column(columnDefinition = "TEXT")
    public String shortDescription;

    @Transient
    @JsonProperty("content_text")
    public String getContentText(){
        return (content==null) ? null : Jsoup.parse(content).text();
    }

    @javax.persistence.Transient
    public String save;

    @javax.persistence.Transient
    public List<String> tags_list;

    @javax.persistence.Transient
    public Long article_category_id;

    @javax.persistence.Transient
    public String imageLink;

    public String status;

    @ManyToOne
    @JsonProperty("article_category")
    public ArticleCategory articleCategory;

    @JsonProperty("article_category_name")
    public String articleCategoryName;

    @OneToMany(mappedBy = "article", orphanRemoval = true, cascade = CascadeType.REMOVE)
    @JsonIgnore
    @Column(name = "article_comment")
    public List<ArticleComment> articleComment;

    @JsonIgnore
    @JoinColumn(name = "user_id")
    @ManyToOne
    public UserCms userCms;

    @Transient
    @JsonProperty("user_creator")
    public String getUserCreator() {
        return userCms.email;
    }

    @ManyToMany (mappedBy = "articles",cascade=CascadeType.REMOVE)
    public List<Tag> tags;

    @JsonIgnore
    @JoinColumn(name = "change_by")
    @ManyToOne
    public UserCms changeBy;

    @Transient
    @JsonProperty("title")
    public String getTitle(){
        return name;
    }

    public String getSlug(){
        return slug;
    }

    @javax.persistence.Transient
    @JsonProperty("image_url")
    public String getImageUrl(){
    	return getImageLink();
    }
    
    @JsonGetter("image_home_url")
    public String getImageHomeUrl(){
    	return getImageHomeLink();
    }
    
    @JsonGetter("image_list_url")
    public String getImageListUrl(){
        return getImageListLink();
    }
    
    @javax.persistence.Transient
    @JsonProperty("meta_title")
    public String getMetaTitle(){
        return title;
    }
    @javax.persistence.Transient
    @JsonProperty("meta_keyword")
    public String getMetaKeyword(){
        return keyword;
    }
    @javax.persistence.Transient
    @JsonProperty("meta_description")
    public String getMetaDescription(){
        return description;
    }
    @javax.persistence.Transient
    @JsonProperty("comments")
    public List<ArticleComment> comments;

    @JsonProperty("created_at_str")
    public String getCreatedAtStr() {
        return CommonFunction.getDateTime(createdAt);
    }

    public static Finder<Long, Article> find = new Finder<Long, Article>(Long.class, Article.class);

    public Article() {
        
    }

    public Article(String title, ArticleCategory category, String content, String filePath) {
        this.name = title;
        this.content = content;
        this.articleCategory = category;
        this.imageHeaderUrl = filePath;
    }

    @SuppressWarnings("unchecked")
    public static String validation(Article model) {
        ConfigSettings query = ConfigSettings.find.byId((long) 2);
        ObjectMapper mapper = new ObjectMapper();
        try {
            Set<String> status = mapper.readValue(query.value, Set.class);
            if (!status.contains(model.status)) {
                return "status are not available";
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Article uniqueCheck = Article.find.where().eq("slug", model.slug).findUnique();
        if (model.title.equals("")) {
            return "Title must not empty.";
        }
        if (uniqueCheck != null && model.id == null) {
            return "Article with similar title already exist";
        }
        if (model.content.equals("")) {
            return "Content must not empty.";
        }
        // if (model.articleCategory == null || !model.articleCategory.moduleType.equals("article")) {
        //     // System.out.println(model.articleCategory.moduleType);
        //     return "Wrong information group.";
        // }
        // if ((model.imageHeaderUrl != null) && ((model.imageName == null || model.imageName.equals(""))
        //         || (model.imageTitle == null || model.imageTitle.equals(""))
        //         || (model.imageAlternate == null || model.imageAlternate.equals(""))
        //         || (model.imageDescription == null))) {
        //     return "Please describe all information for article's image";
        // }
        return null;
    }

    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<>();

        if (title == null || title.isEmpty()) {
            errors.add(new ValidationError("title", "Title must not empty."));
        }

        if(errors.size() > 0)
            return errors;

        return null;
    }

    public String getImageLink(){
    	return imageHeaderUrl==null || imageHeaderUrl.isEmpty() ? "" : Constant.getInstance().getImageUrl() + imageHeaderUrl;
    }
    public String getImageListLink(){
    	return imageListUrl==null || imageListUrl.isEmpty() ? "" : Constant.getInstance().getImageUrl() + imageListUrl;
    }
    public String getImageHomeLink(){
        return imageHomeUrl==null || imageHomeUrl.isEmpty() ? "" : Constant.getInstance().getImageUrl() + imageHomeUrl;
    }

    public static Page<Article> page(int page, int pageSize, String sortBy, String order, String name, String filter) {

        ExpressionList<Article> qry = Article.find
                .where()
                .ilike("name", "%" + name + "%")
                .eq("is_deleted", false);

        switch (filter){
            case Article.DRAFT: qry.eq("status", Article.DRAFT);break;
            case Article.PUBLISH: qry.eq("status", Article.PUBLISH);break;
            case Article.INACTIVE: qry.eq("status", Article.INACTIVE);break;
        }
        return
                qry.orderBy(sortBy + " " + order)
                        .findPagingList(pageSize)
                        .setFetchAhead(false)
                        .getPage(page);
    }

    public void setComments(){
        comments = ArticleComment.getComments(id);
    }

    public static int findRowCount() {
        return
                find.where()
                        .eq("is_deleted", false)
                .findRowCount();
    }

    public String getChangeLogData(Article data){
        HashMap<String, String> map = new HashMap<>();
        map.put("title",(data.title == null)? "":data.title);
        map.put("description",(data.description == null)? "":data.description);
        map.put("keyword",(data.keyword == null)? "":data.keyword);
        map.put("slug",(data.slug == null)? "":data.slug);
        map.put("name",(data.name == null)? "":data.name);
        map.put("image_header_url",(data.imageThumbnailUrl == null)? "":data.imageThumbnailUrl);
        map.put("image_thumbnail_url",(data.imageThumbnailUrl == null)? "":data.imageThumbnailUrl);
        map.put("image_name",(data.imageName == null)? "":data.imageName);
        map.put("image_title",(data.imageTitle == null)? "":data.imageTitle);
        map.put("image_alternate",(data.imageAlternate == null)? "":data.imageAlternate);
        map.put("image_description",(data.imageDescription == null)? "":data.imageDescription);
        map.put("content",(data.content == null)? "":data.content);
        map.put("category",(data.articleCategory == null)? "":data.articleCategory.name);

        if(data.status != null){
            switch (data.status){
                case DRAFT: map.put("status","Draft");break;
                case PUBLISH: map.put("status","Publish");break;
                case INACTIVE: map.put("status","Inactive");break;
            }
        } else map.put("status","");
        if(data.tags != null){
            List<String> tags = new ArrayList<>();
            for(Tag tag : data.tags){
                tags.add(tag.name);
            }
            map.put("tag",String.join(", ", tags));
        } else map.put("tag","");

        return Json.toJson(map).toString();
    }

    @Override
    public void save() {
        super.save();
        ChangeLog changeLog = new ChangeLog(LOG_TYPE, this.userCms.id, LOG_TABLE_NAME, this.id, "ADD", null, getChangeLogData(this));
        changeLog.save();
    }

    @Override
    public void update() {
        Article oldArticle = Article.find.byId(id);
        super.update();

        ChangeLog changeLog;
        if(isDeleted == true){
            changeLog = new ChangeLog(LOG_TYPE, this.userCms.id, LOG_TABLE_NAME, this.id, "DELETE", getChangeLogData(oldArticle), null);
        }else{
            changeLog = new ChangeLog(LOG_TYPE, this.userCms.id, LOG_TABLE_NAME, this.id, "EDIT", getChangeLogData(oldArticle), getChangeLogData(this));
        }
        changeLog.save();

    }

    public void updateStatus(String newStatus) {
        String oldArticleData = getChangeLogData(this);
        status = newStatus;
        super.update();

        ChangeLog changeLog;
        changeLog = new ChangeLog(LOG_TYPE, this.userCms.id, LOG_TABLE_NAME, this.id, "EDIT", oldArticleData, getChangeLogData(this));
        changeLog.save();

    }

    public static void seed(String title, ArticleCategory category, String content, String filePath, String shortDescription,
                            String imageThumbnailUrl, UserCms user) {
        Article model = new Article(title, category, content, filePath);
        model.imageThumbnailUrl =imageThumbnailUrl;
        model.shortDescription = shortDescription;
        model.title = model.keyword = model.description = model.imageAlternate = model.imageDescription
                = model.imageTitle = model.imageName = model.name;
        model.userCms = user;
        model.status = Article.PUBLISH;
        model.slug = CommonFunction.slugGenerate(model.title);
        model.save();
    }
}