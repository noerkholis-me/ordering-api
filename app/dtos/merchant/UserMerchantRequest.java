package dtos.merchant;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserMerchantRequest  {

    @JsonProperty("first_name")
    private String firstName;
    @JsonProperty("last_name")
    private String lastName;
    private String email;
    @JsonProperty("role_id")
    private Long roleId;
    @JsonProperty("is_active")
    private Boolean isActive;

    // ======================================== //
    @JsonProperty("merchant_id")
    private Long merchantId;


}
