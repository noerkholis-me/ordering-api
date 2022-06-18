package dtos.profile;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ProfileMerchantRequest {

    @JsonProperty("email")
    public String email;
    
    @JsonProperty("name")
    public String name;

    @JsonProperty("merchant_url")
    public String merchantUrl;

    @JsonProperty("phone")
    public String phone;
    
    @JsonProperty("address")
    public String address;

    @JsonProperty("province_id")
    public Long provinceId;

    @JsonProperty("city_id")
    public Long cityId;

    @JsonProperty("district_id")
    public Long districtId;

    @JsonProperty("subdistrict_id")
    public Long subDistrictId;

    @JsonProperty("postal_code")
    public String postalCode;

    @JsonProperty("password")
    public String password;

    @JsonProperty("confirm_password")
    public String confirmPassword;

}
