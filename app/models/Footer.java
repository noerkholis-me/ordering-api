package models;

import com.avaje.ebean.Page;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hokeba.util.CommonFunction;
import play.data.validation.ValidationError;
import play.libs.Json;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.beans.Transient;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by hendriksaragih on 2/4/17.
 */
@Entity
public class Footer extends BaseModel{
    private static final long serialVersionUID = 1L;
    private static final String LOG_TYPE = "ADMIN";
    private static final String LOG_TABLE_NAME = "footer";

    public String name;
    public String position;
    public String title;
    public String slug;
    public String description;
    public String keyword;

    @JsonProperty("page_url")
    public String pageUrl;

    @ManyToOne
    @JsonProperty("page_id")
    public StaticPage staticPage;

    public int sequence;

    @JsonProperty("new_tab")
    public boolean newTab;

    @javax.persistence.Transient
    public String save;

    @javax.persistence.Transient
    public Long staticPageId;

    @JsonIgnore
    @JoinColumn(name="user_id")
    @ManyToOne
    public UserCms userCms;

    @Transient
    @JsonProperty("user_creator")
    public String getUserCreator(){
        return userCms.email;
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


    public static Finder<Long, Footer> find = new Finder<>(Long.class, Footer.class);

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
        if (position == null || position.isEmpty()) {
            errors.add(new ValidationError("position", "Position must not empty."));
        }

        if(errors.size() > 0)
            return errors;

        return null;
    }

    public static Page<Footer> page(int page, int pageSize, String sortBy, String order, String filter) {
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

    public static void seed(String name, String position, Long id){
        Footer model = new Footer();
        UserCms user = UserCms.find.byId(id);
        model.name = model.title = model.description = model.keyword = name;
        model.slug = CommonFunction.slugGenerate(name);
        StaticPage staticPage = StaticPage.find.where().like("name", name).findUnique();
        if(staticPage == null) staticPage = StaticPage.find.byId(new Long(1));
        model.staticPage = staticPage;
        model.pageUrl = "/static/"+staticPage.slug;
        model.newTab = false;
        model.position = position;
        model.userCms = user;
        model.save();

    }

    public static List<Footer> getFooterByPosition(String position) {
        return Footer.find.where().eq("position", position).eq("is_deleted", false).order("sequence asc").findList();
    }
    public String getChangeLogData(Footer data){
        HashMap<String, String> map = new HashMap<>();
        map.put("title",(data.title == null)? "":data.title);
        map.put("description",(data.description == null)? "":data.description);
        map.put("keyword",(data.keyword == null)? "":data.keyword);
        map.put("slug",(data.slug == null)? "":data.slug);
        map.put("name",(data.name == null)? "":data.name);
        map.put("new_tab",(data.newTab == true)? "Yes":"No");
        map.put("position",(data.position == null)? "":data.position);
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
        Footer oldFooter = Footer.find.byId(id);
        super.update();

        ChangeLog changeLog;
        if(isDeleted == true){
            changeLog = new ChangeLog(LOG_TYPE, this.userCms.id, LOG_TABLE_NAME, this.id, "DELETE", getChangeLogData(oldFooter), null);
        }else{
            changeLog = new ChangeLog(LOG_TYPE, this.userCms.id, LOG_TABLE_NAME, this.id, "EDIT", getChangeLogData(oldFooter), getChangeLogData(this));
        }
        changeLog.save();

    }
}
