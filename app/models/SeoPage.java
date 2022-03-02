package models;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlQuery;
import com.avaje.ebean.SqlRow;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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
@Table(name = "seo_page")
public class SeoPage extends BaseModel {
    private static final long serialVersionUID = 1L;
    private static final String LOG_TYPE = "ADMIN";
    private static final String LOG_TABLE_NAME = "seo_page";


    @Column(columnDefinition = "TEXT")
    public String content;

    @javax.persistence.Transient
    public String save;

    @JsonIgnore
    @JoinColumn(name = "user_id")
    @ManyToOne
    public UserCms userCms;

    @Transient
    @JsonProperty("user_creator")
    public String getUserCreator() {
        return userCms.email;
    }

    public static Finder<Long, SeoPage> find = new Finder<>(Long.class, SeoPage.class);

    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<>();

        if (content == null || content.isEmpty()) {
            errors.add(new ValidationError("content", "Content must not empty."));
        }

        if(errors.size() > 0)
            return errors;

        return null;
    }

    public static String getSeo(){
        SqlQuery sqlQuery = Ebean.createSqlQuery(
                "select content from seo_page");
        SqlRow result = sqlQuery.findUnique();
        String res = "";
        try {
            res = result.getString("content");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            res = "";
        }
        //String
        //int resSequence = (result.getInteger("max")==null ? 0 : result.getInteger("max"))+1;
        return res;
    }

    public String getChangeLogData(SeoPage data){
        HashMap<String, String> map = new HashMap<>();
        map.put("content",(data.content == null)? "":data.content);
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
        SeoPage oldSeoPage = SeoPage.find.byId(id);
        super.update();

        ChangeLog changeLog;
        if(isDeleted == true){
            changeLog = new ChangeLog(LOG_TYPE, this.userCms.id, LOG_TABLE_NAME, this.id, "DELETE", getChangeLogData(oldSeoPage), null);
        }else{
            changeLog = new ChangeLog(LOG_TYPE, this.userCms.id, LOG_TABLE_NAME, this.id, "EDIT", getChangeLogData(oldSeoPage), getChangeLogData(this));
        }
        changeLog.save();

    }

    public static void seed(String content, Long id){
        SeoPage model = new SeoPage();
        UserCms user = UserCms.find.byId(id);
        //model.title = model.keyword = name;
        model.content = content.isEmpty() ? "Content Seo " : content;
        model.userCms = user;
        model.save();

    }
}
