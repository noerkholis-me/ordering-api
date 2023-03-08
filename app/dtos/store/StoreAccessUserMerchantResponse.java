package dtos.store;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class StoreAccessUserMerchantResponse  {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("first_name")
    private String firstName;
    @JsonProperty("last_name")
    private String lastName;
    @JsonProperty("full_name")
    private String fullName;
    private String email;
    @JsonProperty("role_id")
    private Long roleId;
    @JsonProperty("role_name")
    private String roleName;
    @JsonProperty("is_active")
    private Boolean isActive;
    // private String password;

    // ======================================== //
    @JsonProperty("merchant_id")
    private Long merchantId;
    @JsonProperty("gender")
    private String gender;
}