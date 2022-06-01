package dtos.order;

import com.fasterxml.jackson.annotation.JsonFormat;
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

    @JsonProperty("customer_name")
    private String customerName;

    @JsonProperty("merchant_name")
    private String merchantName;
    @JsonProperty("total_amount")
    @JsonSerialize(using = BigDecimalSerialize.class)
    private BigDecimal totalAmount;
    @JsonProperty("order_type")
    private String orderType;
    @JsonProperty("status_order")
    private String statusOrder;
    @JsonProperty("order_queue")
    private Integer orderQueue;
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
    @JsonSerialize(using = BigDecimalSerialize.class)
    private BigDecimal totalAmountPayment;
    @JsonProperty("payment_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Jakarta")
    private Date paymentDate;

}
