package models;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Page;
import com.avaje.ebean.Transaction;
import com.avaje.ebean.Update;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hokeba.util.CommonFunction;
import com.hokeba.util.Encryption;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.persistence.*;
import javax.validation.constraints.Size;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
/**
 * Created by hendriksaragih on 2/4/17.
 */
@Entity
public class UserCms extends BaseModel {
    private static final long serialVersionUID = 1L;

    @JsonIgnore
    public String password;

    @JsonProperty("first_name")
    public String firstName;
    @JsonProperty("last_name")
    public String lastName;
    @Column(unique = true)
    public String email;
    @JsonProperty("full_name")
    public String fullName;

    public String phone;
    @Size(max = 1)
    @Column(length = 1)
    public String gender;
    @Temporal(TemporalType.DATE)
    @Column(name = "birth_date")
    @JsonProperty("birth_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Jakarta")
    public Date birthDate;

    @JsonIgnore
    @Column(name = "activation_code")
    public String activationCode;
    @JsonProperty("is_active")
    @Column(name = "is_active")
    public boolean isActive;

    @ManyToOne(cascade = { CascadeType.ALL })
    public Role role;


    @javax.persistence.Transient
    public Long roleId;

    @javax.persistence.Transient
    public String save;

    @javax.persistence.Transient
    public String oldPassword;

    @javax.persistence.Transient
    public String newPassword;

    @javax.persistence.Transient
    public String newPasswordConfirmation;

    @Transient
    public String getIsActive() {
        String statusName = "";
        if(isActive)
            statusName = "Active";
        else statusName = "Inactive";

        return statusName;
    }

    @Transient
    public String getGenderView() {
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

    public static Finder<Long, UserCms> find = new Finder<Long, UserCms>(Long.class, UserCms.class);

    public static UserCms login(String username, String password) {
        String decPassword = Encryption.EncryptAESCBCPCKS5Padding(password);
        UserCms user = UserCms.find.where().eq("email", username).eq("password", decPassword).eq("is_active", true)
                .setMaxRows(1).findUnique();
        return user;
    }

    public UserCms() {
    }

    public UserCms(String password, String firstName, String lastName, String email, String phone,
                   String gender, String birthDate) throws ParseException {
        super();
        this.password = Encryption.EncryptAESCBCPCKS5Padding(password);
        this.firstName = firstName;
        this.lastName = lastName;
        this.fullName = firstName + ((lastName!=null&&!lastName.equals("")) ? " "+lastName : "");
        this.email = email;
        this.phone = phone;
        this.gender = gender;
        this.birthDate = new SimpleDateFormat("yyyy-MM-dd").parse(birthDate);
        this.isActive = true;
    }

    public String getThumbnailImageLink(){
        return "";
    }

    public static String validation(String firstName, String email, String birthDate, String gender, Long id) {
        if (!email.matches(CommonFunction.emailRegex)) {
            return "Email format not valid.";
        }
        if (firstName.equals("")) {
            return "First name must not empty";
        }
        if (!(gender.equalsIgnoreCase("M") || gender.equalsIgnoreCase("F"))) {
            return "Input gender is not valid";
        }
        if (id == null) {
            UserCms existingModel = UserCms.find.where().eq("email", email).findUnique();
            if (existingModel != null) {
                return "This email has already been registered.";
            }
        }
        try {
            Date dateCheck = new SimpleDateFormat("yyyy-MM-dd").parse(birthDate);
        } catch (ParseException e) {
            return "Birth date format is invalid";
        }
        return null;
    }

    public HashMap<String, Boolean> checkPrivilegeList() {
        LinkedHashMap<String, Boolean> result = new LinkedHashMap<String, Boolean>();
        List<Feature> allFeature = Feature.find.all();
        List<RoleFeature> myFeature = this.role.featureList;
        for (Feature targetFeature : allFeature) {
            String keyTarget = targetFeature.key;
            result.put(keyTarget, false);
        }
        for (RoleFeature feature : myFeature) {
            String keyTarget = feature.feature.key;
            result.put(keyTarget, true);
        }
        return result;
    }

    public String changePassword(String oldPass, String newPass, String conPass)
            throws InvalidKeyException, InvalidAlgorithmParameterException, NoSuchAlgorithmException,
            NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        String encPass = Encryption.EncryptAESCBCPCKS5Padding(oldPass);
        if (encPass.equals(this.password)) {
            if (!oldPass.equals(newPass)) {
                if (CommonFunction.passwordValidation(newPass)) {
                    if (conPass.equals(newPass)) {
                        Transaction txn = Ebean.beginTransaction();
                        try {
                            this.password = Encryption.EncryptAESCBCPCKS5Padding(newPass);
                            this.save();
                            Update<UserCmsLog> upd = Ebean.createUpdate(UserCmsLog.class,
                                    "UPDATE user_log SET is_active=:isActive WHERE is_active=true and user_id=:userId");
                            upd.set("isActive", false);
                            upd.set("userId", this.id);
                            upd.execute();
                            txn.commit();
                            return null;
                        } catch (Exception e) {
                            e.printStackTrace();
                            txn.rollback();
                        } finally {
                            txn.end();
                        }
                        return "500";
                    }
                    return "Password does not match the confirm password";
                }
                return "Password must be at least 8 character, has no whitespace, "
                        + "and have at least 3 variations from uppercase, lowercase, number, or symbol";
            }
            return "Your new password must be different";
        }
        return "Invalid old password";
    }


    public static Page<UserCms> page(int page, int pageSize, String sortBy, String order, String filter) {
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