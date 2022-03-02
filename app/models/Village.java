package models;

import com.avaje.ebean.Page;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
//USED AS 'KELURAHAN' IN INDONESIA
public class Village extends BaseModel{
    private static final long serialVersionUID = 1L;

    public String code;
    public String name;
    @ManyToOne
    @JsonProperty("township_id")
    public Township township;

    public static Finder<Long, Village> find = new Finder<>(Long.class, Village.class);

    public static Page<Village> page(int page, int pageSize, String sortBy, String order, String filter) {
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

    public static void seed(String code, String name, Township township){
        Village model = new Village();
        model.code = code;
        model.name = name;
        model.township = township;
        model.isDeleted = false;
        model.save();
    }
}