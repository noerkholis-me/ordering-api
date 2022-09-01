package dtos.loyalty;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.annotation.JsonIgnore;
import utils.BigDecimalSerialize;
import java.util.Date;


import java.math.BigDecimal;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LoyaltyMemberResponse  {

    @JsonProperty("full_name")
    public String fullName;

    @JsonProperty("email")
    public String email;

    @JsonProperty("phone")
    public String phone;

    @JsonSerialize(using = BigDecimalSerialize.class)
    @JsonProperty("loyalty_point")
    public BigDecimal loyaltyPoint;

    @JsonFormat(shape = JsonFormat.Shape.STRING,  pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS", timezone="Asia/Jakarta")
    @JsonProperty("expired_date")
    public Date expiredDate;

}
