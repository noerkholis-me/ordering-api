package dtos.merchant;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class MerchantResponse {

    @JsonProperty("id")
    private Long id;
    @JsonProperty("full_name")
    private String fullName;
    @JsonProperty("email")
    private String email;
    @JsonProperty("user_type")
    private String userType;
}
