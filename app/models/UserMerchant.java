package models;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Update;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hokeba.util.CommonFunction;
import dtos.FeatureAndPermissionSession;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * this class for entity / domain model
 * repository / DAO create on model (need to refactor to repository pattern)
 * Don't put business logical on this class
 */

@Entity
public class UserMerchant extends BaseModel {

    private static final long serialVersionUID = 1L;

    @JsonIgnore
    @Getter @Setter
    public String password;

    @Getter @Setter
    @JsonProperty("first_name")
    public String firstName;

    @Getter @Setter
    @JsonProperty("last_name")
    public String lastName;

    @Column(unique = true)
    @Getter @Setter
    public String email;

    @JsonProperty("full_name")
    @Getter @Setter
    public String fullName;

    @Getter @Setter
    public String phone;

    @Size(max = 1)
    @Column(length = 1)
    @Setter
    public String gender;

    @Setter
    @Temporal(TemporalType.DATE)
    @Column(name = "birth_date")
    @JsonProperty("birth_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Jakarta")
    public Date birthDate;
    
    @Setter
    @JsonProperty("updated_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Jakarta")
    public Date updatedAt;

    @JsonIgnore
    @Column(name = "activation_code")
    @Getter @Setter
    public String activationCode;

    @Setter
    @JsonProperty("is_active")
    @Column(name = "is_active")
    public boolean isActive;

    @ManyToOne
    @JoinColumn(name="role_id", referencedColumnName = "id")
    @Getter @Setter
    public RoleMerchant role;

    @Getter @Setter
    @Column(name = "reset_token")
    public String resetToken;

    @Column(name = "reset_time")
    @Getter @Setter
    private Long resetTime;


//    @OneToOne
//    @JsonProperty("merchant")
//    @JoinColumn(name="merchant_id", referencedColumnName = "id")
//    @Getter @Setter
//    public Merchant merchant;


    public UserMerchant() {
        super();
    }

    @Transient
    public String getIsActive() {
        String statusName = "";
        if(isActive)
            statusName = "Active";
        else statusName = "Inactive";

        return statusName;
    }

    @Transient
    public String getGender() {
        String result = "";
        if("M".equals(gender)){
            result = "Male";
        }else if ("F".equals(gender)){
            result = "Female";
        }
        return result;
    }

    @Transient
    public String getBirthDateFormat() {
        return CommonFunction.getDate(birthDate);
    }

    public List<FeatureAndPermissionSession> checkFeatureAndPermissions() {
        List<RoleMerchantFeature> roleMerchantFeatures = RoleMerchantFeature.findByRoleId(this.role.id);
        if (roleMerchantFeatures == null || roleMerchantFeatures.isEmpty()) {
            return null;
        }
        List<RoleMerchantFeature> myFeature = this.role.getFeatureList();
        List<FeatureAndPermissionSession> featureAndPermissionSessionList = new ArrayList<>();
        for (RoleMerchantFeature feature : roleMerchantFeatures) {
            FeatureAndPermissionSession featureAndPermissionSession = new FeatureAndPermissionSession();
            featureAndPermissionSession.setFeatureName(feature.getFeature().name);
            featureAndPermissionSession.setIsView(feature.getIsView());
            featureAndPermissionSession.setIsAdd(feature.getIsAdd());
            featureAndPermissionSession.setIsEdit(feature.getIsEdit());
            featureAndPermissionSession.setIsDelete(feature.getIsDelete());
            featureAndPermissionSessionList.add(featureAndPermissionSession);
        }
        return featureAndPermissionSessionList;
    }

    public static void removeAllToken(Long id) {
        Update<MerchantLog> upd = Ebean.createUpdate(MerchantLog.class,
                "UPDATE merchant_log SET is_active=:isActive WHERE is_active=true and user_merchant_id=:memberId");
        upd.set("isActive", false);
        upd.set("memberId", id);
        upd.execute();
    }

}
