package dtos.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import utils.BigDecimalSerialize;

import java.math.BigDecimal;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class InitiatePaymentResponse {

    private String status;
    @JsonProperty("order_number")
    private String orderNumber;
    @JsonProperty("total_amount")
    @JsonSerialize(using = BigDecimalSerialize.class)
    private BigDecimal totalAmount;
    @JsonProperty("creation_time")
    private Date creationTime;
    @JsonProperty("expiration_date")
    private Date expirationDate;
    private MetaResponse metadata;

}
