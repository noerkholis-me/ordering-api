package models;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlQuery;
import com.avaje.ebean.SqlRow;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hokeba.util.CommonFunction;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.beans.Transient;
import java.util.List;


/**
 * Created by hendriksaragih on 2/4/17.
 */
@Entity
public class InformationCategoryGroup extends BaseModel {
    public String name;
    @JsonProperty("module_type")
    public String moduleType;
    public String slug;
    public int sequence;
    @JsonIgnore
    @JoinColumn(name = "user_id")
    @ManyToOne
    public UserCms userCms;

    @Transient
    @JsonProperty("user_creator")
    public String getUserCreator() {
        return userCms.email;
    }

    public static Finder<Long, InformationCategoryGroup> find = new Finder<>(Long.class,
            InformationCategoryGroup.class);

    public InformationCategoryGroup (){

    }

    public InformationCategoryGroup(String name, String moduleType){
        this.name = name;
        this.moduleType = moduleType;
        this.slug = CommonFunction.slugGenerate(name);
        this.sequence = InformationCategoryGroup.find.where().eq("is_deleted", false).eq("module_type", moduleType).findRowCount()+1;
    }

    public static List<InformationCategoryGroup> listInformationGroupIn(String moduleType) {
        return InformationCategoryGroup.find.where().eq("module_type", moduleType).findList();
    }

    public static String validation(InformationCategoryGroup model) {
        String res = null;
        InformationCategoryGroup uniqueCheck = InformationCategoryGroup.find.where()
                .eq("module_type", model.moduleType)
                .eq("slug", model.slug)
                .findUnique();
        if (model.name==null||model.name.equals("")) {
            res = "Name must not empty.";
        }
        else if (model.moduleType==null||model.moduleType.equals("")){
            res = "Empty module type.";
        }
        else if (uniqueCheck!=null && !uniqueCheck.id.equals(model.id)){
            res = "Group with similar name already exist.";
        }
        return res;
    }

    public static int getNextSequence(String moduleType){
        SqlQuery sqlQuery = Ebean.createSqlQuery(
                "select max(sequence) as max from information_category_group where is_deleted = false and module_type = :moduleType");
        sqlQuery.setParameter("moduleType", moduleType);
        SqlRow result = sqlQuery.findUnique();
        int resSequence = (result.getInteger("max")==null ? 0 : result.getInteger("max"))+1;
        return resSequence;
    }

    public static List<InformationCategoryGroup> getHomePage(String type) {
        return find.where()
                .eq("module_type", type)
                .eq("is_deleted", false)
                .orderBy("sequence ASC").findList();
    }

    public static InformationCategoryGroup seed(String name, String type, UserCms user){
        InformationCategoryGroup icg1 = new InformationCategoryGroup(name, type);
        icg1.userCms = user;
        icg1.save();

        return icg1;
    }
}