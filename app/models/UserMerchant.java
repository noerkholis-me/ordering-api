package models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hokeba.util.CommonFunction;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Date;

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
    @JsonProperty("id")
    public Long id;

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

    public String phone;

    @Size(max = 1)
    @Column(length = 1)
    @Getter @Setter
    public String gender;

    @Setter
    @Temporal(TemporalType.DATE)
    @Column(name = "birth_date")
    @JsonProperty("birth_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Jakarta")
    public Date birthDate;

    @JsonIgnore
    @Column(name = "activation_code")
    @Getter @Setter
    public String activationCode;

    @Setter
    @JsonProperty("is_active")
    @Column(name = "is_active")
    public boolean isActive;

    @ManyToOne(cascade = { CascadeType.ALL })
    @Getter @Setter
    public Role role;

    @OneToOne(cascade = { CascadeType.ALL })
    @JsonProperty("merchant")
    @Getter @Setter
    public Merchant merchant;

    @javax.persistence.Transient
    public String save;

    @javax.persistence.Transient
    public Long rolesId;

    @javax.persistence.Transient
    public Long merchantsId;

    @JsonProperty("role_id")
    @Getter @Setter
    public Long roleId;

    @JsonProperty("merchant_id")
    @Getter @Setter
    public Long merchantId;

    public UserMerchant() {
    }

    @Transient
    public String getIsActive() {
        String statusName = "";
        if(isActive)
            statusName = "Active";
        else statusName = "Inactive";

        return statusName;
    }

    // @Transient
    // public String getGender() {
    //     String result = "";
    //     if("M".equals(gender)){
    //         result = "Male";
    //     }else if ("F".equals(gender)){
    //         result = "Female";
    //     }
    //     return result;
    // }

    @Transient
    public String getBirthDateFormat() {
        return CommonFunction.getDate(birthDate);
    }

}
