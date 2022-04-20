package dtos.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class InitiatePaymentResponse {

    @JsonProperty("reference_id")
    private String referenceId;
    @JsonProperty("order_number")
    private String orderNumber;
    @JsonProperty("merchant_name")
    private String merchantName;
    @JsonProperty("qr_string")
    private String qrString;
    @JsonProperty("total_amount")
    private BigDecimal totalAmount;
    @JsonProperty("creation_time")
    private Date creationTime;
    @JsonProperty("status")
    private String status;

}
