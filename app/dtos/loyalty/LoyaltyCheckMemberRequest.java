package dtos.loyalty;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LoyaltyCheckMemberRequest implements Serializable {
    @JsonProperty("phone_number")
    public String phoneNumber;
    @JsonProperty("email")
    public String email;
    @JsonProperty("full_name")
    public String fullName;
}
