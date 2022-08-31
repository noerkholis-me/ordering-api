package dtos.loyalty;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
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
public class LoyaltyPointHistoryResponse  {

    @JsonProperty("full_name")
    public String fullName;

    @JsonProperty("email")
    public String email;

    @JsonProperty("phone")
    public String phone;

    @JsonProperty("order_number")
    public String orderNumber;
    
    @JsonSerialize(using = BigDecimalSerialize.class)
    @JsonProperty("point")
    public BigDecimal point;

    @JsonSerialize(using = BigDecimalSerialize.class)
    @JsonProperty("added")
    public BigDecimal added;
    
    @JsonSerialize(using = BigDecimalSerialize.class)
    @JsonProperty("used")
    public BigDecimal used;

    @JsonFormat(shape = JsonFormat.Shape.STRING,  pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS", timezone="Asia/Jakarta")
    @JsonProperty("expired")
    public Date expiredDate;

}
