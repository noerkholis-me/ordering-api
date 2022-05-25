package dtos.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import dtos.payment.MetaResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import utils.BigDecimalSerialize;

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
    @JsonProperty("total_amount")
    @JsonSerialize(using = BigDecimalSerialize.class)
    private BigDecimal totalAmount;
    private MetaResponse metadata;

}
