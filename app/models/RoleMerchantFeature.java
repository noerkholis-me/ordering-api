package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import play.db.ebean.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

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

    public RoleMerchantFeature() {
    }

    public RoleMerchantFeature(Feature feature, RoleMerchant roleMerchant, Boolean isView, Boolean isAdd, Boolean isEdit, Boolean isDelete) {
        this.feature = feature;
        this.roleMerchant = roleMerchant;
        this.isView = isView;
        this.isAdd = isAdd;
        this.isEdit = isEdit;
        this.isDelete = isDelete;
    }



}
