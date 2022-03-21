package models;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Query;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import play.db.ebean.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.List;

@Entity
@Getter
@Setter
public class RoleMerchantFeature extends Model {

    @ManyToOne
    @JoinColumn(name = "feature_id", referencedColumnName = "id")
    @JsonIgnore
    public Feature feature;

    @ManyToOne
    @JoinColumn(name = "role_merchant_id", referencedColumnName = "id")
    @JsonIgnore
    public RoleMerchant roleMerchant;

    @Column(name = "is_view")
    public Boolean isView;

    @Column(name = "is_add")
    public Boolean isAdd;

    @Column(name = "is_edit")
    public Boolean isEdit;

    @Column(name = "is_delete")
    public Boolean isDelete;

    @javax.persistence.Transient
    public Long roleMerchantId;

    @javax.persistence.Transient
    public Long featureId;

    public static Finder<Long, RoleMerchantFeature> find = new Finder<>(Long.class, RoleMerchantFeature.class);

    public RoleMerchantFeature() {
    }

    public RoleMerchantFeature(Feature feature, RoleMerchant roleMerchant, Boolean isView, Boolean isAdd, Boolean isEdit, Boolean isDelete) {
        super();
        this.feature = feature;
        this.roleMerchant = roleMerchant;
        this.isView = isView;
        this.isAdd = isAdd;
        this.isEdit = isEdit;
        this.isDelete = isDelete;
    }

    public static List<RoleMerchantFeature> getFeaturesByRole(Long id){
        String sql = "SELECT role_merchant_id, feature_id, is_view, is_add, is_edit, is_delete FROM role_merchant_feature " +
                "WHERE role_merchant_id = "+id+" " +
                "ORDER BY feature_id ASC";

        RawSql rawSql = RawSqlBuilder.parse(sql)
                .columnMapping("role_merchant_id", "roleMerchantId")
                .columnMapping("feature_id", "featureId")
                .columnMapping("is_view", "isView")
                .columnMapping("is_add", "isAdd")
                .columnMapping("is_edit", "isEdit")
                .columnMapping("is_delete", "isDelete")
                .create();
        Query<RoleMerchantFeature> query = Ebean.find(RoleMerchantFeature.class);
        query.setRawSql(rawSql);
        return query.findList();
    }



}
