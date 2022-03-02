package models;

import com.avaje.ebean.Page;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hokeba.util.CommonFunction;
import play.data.validation.ValidationError;
import play.libs.Json;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by hendriksaragih on 2/4/17.
 */
@Entity
@Table(name = "page")
public class StaticPage extends BaseModel {
    private static final long serialVersionUID = 1L;
    private static final String LOG_TYPE = "ADMIN";
    private static final String LOG_TABLE_NAME = "page";

    @Column(columnDefinition = "TEXT")
    public String content;
    public String title;
    public String description;
    public String keyword;
    public String slug;
    public String name;

    @javax.persistence.Transient
    public String save;

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

    @JsonIgnore
    @JoinColumn(name = "user_id")
    @ManyToOne
    public UserCms userCms;

    @Transient
    @JsonProperty("user_creator")
    public String getUserCreator() {
        return userCms.email;
    }

    public static Finder<Long, StaticPage> find = new Finder<>(Long.class, StaticPage.class);

    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<>();

        if (name == null || name.isEmpty()) {
            errors.add(new ValidationError("name", "Name must not empty."));
        }
        if (title == null || title.isEmpty()) {
            errors.add(new ValidationError("title", "Meta Title must not empty."));
        }
        if (description == null || description.isEmpty()) {
            errors.add(new ValidationError("description", "Meta Description must not empty."));
        }
        if (keyword == null || keyword.isEmpty()) {
            errors.add(new ValidationError("keyword", "Meta Keyword must not empty."));
        }
        if (content == null || content.isEmpty()) {
            errors.add(new ValidationError("content", "Content must not empty."));
        }

        if(errors.size() > 0)
            return errors;

        return null;
    }

    public static Page<StaticPage> page(int page, int pageSize, String sortBy, String order, String filter) {
        return
                find.where()
                        .ilike("name", "%" + filter + "%")
                        .eq("is_deleted", false)
                        .orderBy(sortBy + " " + order)
                        .findPagingList(pageSize)
                        .setFetchAhead(false)
                        .getPage(page);
    }

    public static Integer RowCount() {
        return find.where().eq("is_deleted", false).findRowCount();
    }

    public String getChangeLogData(StaticPage data){
        HashMap<String, String> map = new HashMap<>();
        map.put("content",(data.content == null)? "":data.content);
        map.put("title",(data.title == null)? "":data.title);
        map.put("description",(data.description == null)? "":data.description);
        map.put("keyword",(data.keyword == null)? "":data.keyword);
        map.put("slug",(data.slug == null)? "":data.slug);
        map.put("name",(data.name == null)? "":data.name);
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
        StaticPage oldStaticPage = StaticPage.find.byId(id);
        super.update();

        ChangeLog changeLog;
        if(isDeleted == true){
            changeLog = new ChangeLog(LOG_TYPE, this.userCms.id, LOG_TABLE_NAME, this.id, "DELETE", getChangeLogData(oldStaticPage), null);
        }else{
            changeLog = new ChangeLog(LOG_TYPE, this.userCms.id, LOG_TABLE_NAME, this.id, "EDIT", getChangeLogData(oldStaticPage), getChangeLogData(this));
        }
        changeLog.save();

    }

    public static void seed(String name, String content, Long id){
        StaticPage model = new StaticPage();
        UserCms user = UserCms.find.byId(id);
        model.name = model.title = model.description = model.keyword = name;
        model.slug = CommonFunction.slugGenerate(name);
        model.content = content.isEmpty() ? "Content for "+name : content;
        model.userCms = user;
        model.save();

    }
}