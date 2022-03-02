package models;

import com.avaje.ebean.Page;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hendriksaragih on 2/4/17.
 */
@Entity
public class Role extends BaseModel{
    public String name;
    @Column(unique=true)
    public String key;
    public String description;
    @JsonProperty("is_active")
    public boolean isActive;

    @OneToMany(mappedBy = "role")
    public List<RoleFeature> featureList;

    @javax.persistence.Transient
    public String save;

    @javax.persistence.Transient
    public List<String> feature_list;

    public static Finder<Long, Role> find = new Finder<Long, Role>(Long.class, Role.class);

    public Role(){

    }

    public Role(String name, String description, boolean isActive){
        super();
        this.name        = name;
        this.key         = name.toLowerCase().replace(' ', '_');
        this.description = description;
        this.isActive    = isActive;
        this.featureList    = new ArrayList<RoleFeature>();
    }

    public void setFeature(RoleFeature...features){
        this.featureList = new ArrayList<RoleFeature>();
        for (RoleFeature feature : features) {
            this.featureList.add(feature);
        }
    }

    public static String validation(String name, String key, String description) {
        if (name.equals("")) {
            return "Name must not empty.";
        }
        if (key.equals("")) {
            return "Key must not empty.";
        }
        if (description.equals("")) {
            return "Description must not empty.";
        }
        return null;
    }

    public static Page<Role> page(int page, int pageSize, String sortBy, String order, String filter) {
        return
                find.where()
                        //.ilike("name", "%" + filter + "%")
                        .eq("is_deleted", false)
                        .orderBy(sortBy + " " + order)
                        .findPagingList(pageSize)
                        .setFetchAhead(false)
                        .getPage(page);
    }

    public static Integer RowCount() {
        return find.where().eq("is_deleted", false).findRowCount();
    }
}