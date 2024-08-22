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
    @JsonProperty("table_id")
    private Long tableId;
    @JsonProperty("table_name")
    private String tableName;
    @JsonProperty("queue_number")
    private Integer queueNumber;
    @JsonProperty("total_amount")
    @JsonSerialize(using = BigDecimalSerialize.class)
    private BigDecimal totalAmount;
    @JsonProperty("discount_amount")
    @JsonSerialize(using = BigDecimalSerialize.class)
    private BigDecimal discountAmount;
    @JsonProperty("subtotal")
    @JsonSerialize(using = BigDecimalSerialize.class)
    private BigDecimal subtotal;
    @JsonProperty("service_fee")
    @JsonSerialize(using = BigDecimalSerialize.class)
    private BigDecimal serviceFee;
    @JsonProperty("tax")
    @JsonSerialize(using = BigDecimalSerialize.class)
    private BigDecimal tax;
    @JsonProperty("delivery_fee")
    @JsonSerialize(using = BigDecimalSerialize.class)
    private BigDecimal deliveryFee;
    @JsonProperty("loyalty_point")
    @JsonSerialize(using = BigDecimalSerialize.class)
    private BigDecimal loyaltyPoint;
    @JsonProperty("status")
    private String status;
    @JsonProperty("payment_method")
    private String paymentMethod;
    private MetaResponse metadata;
    @JsonProperty("shipper_order_id")
    private String shipperOrderId;

}
