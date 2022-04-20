package dtos.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderTransactionResponse {

    @JsonProperty("invoice_number")
    private String invoiceNumber;
    @JsonProperty("order_number")
    private String orderNumber;
    @JsonProperty("merchant_name")
    private String merchantName;
    @JsonProperty("qr_string")
    private String qrString;
    @JsonProperty("total_amount")
    private BigDecimal totalAmount;

}
