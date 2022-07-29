package dtos.loyalty;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LoyaltyCheckMemberResponse {
    @JsonProperty("member_id")
    public Long memberId;
    @JsonProperty("full_name")
    public String fullName;
    @JsonProperty("first_name")
    public String firstName;
    @JsonProperty("last_name")
    public String lastName;
    @JsonProperty("email")
    public String email;
    @JsonProperty("phone")
    public String phone;
    @JsonProperty("is_have_loyalty_point")
    public Boolean isHaveLoyaltyPoint;
    @JsonProperty("loyalty_point")
    public Integer loyaltyPoint;
}
