package models;

import com.avaje.ebean.Page;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import play.data.validation.ValidationError;
import play.libs.Json;

import javax.persistence.Column;
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
public class BlacklistEmail extends BaseModel{
    private static final String LOG_TYPE = "ADMIN";
    private static final String LOG_TABLE_NAME = "blacklist_email";

    @Column(unique=true)
    public String name;

    @JsonIgnore
    @JoinColumn(name="user_id")
    @ManyToOne
    public UserCms userCms;

    @javax.persistence.Transient
    public String save;

    @Transient
    @JsonProperty("user_creator")
    public String getUserCreator(){
        return userCms.email;
    }

    public static Finder<Long, BlacklistEmail> find = new Finder<>(Long.class, BlacklistEmail.class);

    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<>();

        if (name == null || name.isEmpty()) {
            errors.add(new ValidationError("name", "Name must not empty."));
        }

        if(errors.size() > 0)
            return errors;

        return null;
    }

    public static Page<BlacklistEmail> page(int page, int pageSize, String sortBy, String order, String filter) {
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


    public String getChangeLogData(BlacklistEmail data){
        HashMap<String, String> map = new HashMap<>();
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
        BlacklistEmail oldBlackList = BlacklistEmail.find.byId(id);
        super.update();

        ChangeLog changeLog;
        if(isDeleted == true){
            changeLog = new ChangeLog(LOG_TYPE, this.userCms.id, LOG_TABLE_NAME, this.id, "DELETE", getChangeLogData(oldBlackList), null);
        }else{
            changeLog = new ChangeLog(LOG_TYPE, this.userCms.id, LOG_TABLE_NAME, this.id, "EDIT", getChangeLogData(oldBlackList), getChangeLogData(this));
        }
        changeLog.save();

    }
}
