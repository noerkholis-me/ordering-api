package models;

import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.Page;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import java.util.Calendar;
import java.util.Date;

@Entity
public class PaymentExpiration extends BaseModel{
    private static final long serialVersionUID = 1L;
    public static final boolean ACTIVE = true;
    public static final boolean INACTIVE = false;
    public static final String HOUR_TYPE = "hour";
    public static final String DAY_TYPE = "day";

    public String type;
    public int total;

    @Transient
    public String save;

    @JsonIgnore
    @JoinColumn(name = "user_id")
    @ManyToOne
    public UserCms userCms;


    @Transient
    public String getTypeName(){
        return type;
    }

    public static Finder<Long, PaymentExpiration> find = new Finder<>(Long.class, PaymentExpiration.class);

    public static Page<PaymentExpiration> page(int page, int pageSize, String sortBy, String order, String name) {
        int search = 0;
        try {
            search = Integer.parseInt(name);
        }catch (Exception e){

        }
        ExpressionList<PaymentExpiration> qry = PaymentExpiration.find
                .where()
                .eq("is_deleted", false);
        if(search != 0){
            qry.eq("total", search);
        }

        return
                qry.findPagingList(pageSize)
                        .setFetchAhead(false)
                        .getPage(page);
    }

    public static Integer RowCount() {
        return find.where().eq("is_deleted", false).findRowCount();
    }

    public static void seed(String type, int total, boolean active, UserCms userCms){
        PaymentExpiration model = new PaymentExpiration();
        model.type = type;
        model.total = total;
        model.userCms = userCms;
        model.isDeleted = false;
        model.save();
    }

    public static Date getExpired(){
        Date now = new Date();
        Calendar c = Calendar.getInstance();
        PaymentExpiration pe = PaymentExpiration.find.byId(1L);
        c.setTime(now);
        if (pe.type.equals(DAY_TYPE)){
            c.add(Calendar.DATE, pe.total);
        }else{
            c.add(Calendar.HOUR, pe.total);
        }
        return c.getTime();
    }

}