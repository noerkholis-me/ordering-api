package dtos.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import utils.BigDecimalSerialize;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

import java.util.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class OrderList {

    @JsonProperty("invoice_number")
    private String invoiceNumber;
    @JsonProperty("order_number")
    private String orderNumber;
    @JsonProperty("merchant_name")
    private String merchantName;
    @JsonProperty("total_amount")
    @JsonSerialize(using = BigDecimalSerialize.class)
    private BigDecimal totalAmount;
    @JsonProperty("order_type")
    private String orderType;
    // @JsonProperty("order_transaction")
    // private OrderTransactionResponse orderTransaction;
    @JsonProperty("order_detail")
    private List<ProductOrderDetail> productOrderDetail;
    @JsonProperty("status")
    private String status;
    @JsonProperty("payment_type")
    private String paymentType;
    @JsonProperty("payment_channel")
    private String paymentChannel;
    @JsonProperty("total_amount_payment")
    private BigDecimal totalAmountPayment;
    @JsonProperty("payment_date")
    private Date paymentDate;

}
