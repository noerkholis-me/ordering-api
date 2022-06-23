package dtos.profile;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ProfileUserMerchantRequest {

    @JsonProperty("email")
    public String email;
    
    @JsonProperty("first_name")
    public String firstName;
    
    @JsonProperty("last_name")
    public String lastName;

    @JsonProperty("old_password")
    public String oldPassword;

    @JsonProperty("password")
    public String password;

    @JsonProperty("confirm_password")
    public String confirmPassword;
}
