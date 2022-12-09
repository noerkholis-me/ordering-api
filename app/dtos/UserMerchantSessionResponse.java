package dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import dtos.store.StoreAccessResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import models.RoleMerchant;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserMerchantSessionResponse {

    @JsonProperty("email")
    private String email;
    @JsonProperty("first_name")
    private String firstName;
    @JsonProperty("last_name")
    private String lastName;
    @JsonProperty("full_name")
    private String fullName;
    @JsonProperty("phone")
    private String phone;
    @JsonProperty("gender")
    private String gender;
    @JsonProperty("birth_date")
    private String birthDate;
    @JsonProperty("is_active")
    private String isActive;
    @JsonProperty("role")
    private RoleMerchant role;
    @JsonProperty("is_open")
    private Boolean isOpen;
    @JsonProperty("store_access")
    private StoreAccessResponse storeAccess;
    @JsonProperty("merchant_type")
    public String merchantType;
    @JsonProperty("account_label")
    public String accountLabel;

}
