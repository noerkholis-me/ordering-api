package dtos.payment;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import utils.BigDecimalSerialize;

import java.math.BigDecimal;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class OrderPaymentResponse {

    @JsonProperty("order_number")
    private String orderNumber;
    @JsonProperty("table_id")
    private Long tableId;
    @JsonProperty("table_name")
    private String tableName;
    @JsonProperty("queue_number")
    private Integer queueNumber;
    private String status;
    @JsonProperty("invoice_no")
    private String invoiceNo;
    @JsonProperty("payment_channel")
    private String paymentChannel;
    @JsonProperty("total_amount")
    @JsonSerialize(using = BigDecimalSerialize.class)
    private BigDecimal totalAmount;
    @JsonProperty("payment_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Jakarta")
    private Date paymentDate;
}
