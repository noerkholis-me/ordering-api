package dtos.merchant;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

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
}
