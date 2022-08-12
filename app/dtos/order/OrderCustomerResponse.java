package dtos.order;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class OrderCustomerResponse {

    @JsonProperty("order_number")
    private String orderNumber;
    @JsonProperty("store_name")
    private String storeName;
    @JsonProperty("order_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Jakarta")
    private Date orderDate;
    @JsonProperty("total_price")
    @JsonSerialize(using = BigDecimalSerialize.class)
    private BigDecimal totalPrice;
    @JsonProperty("order_status")
    private String orderStatus;
    @JsonProperty("payment_status")
    private String paymentStatus;

}
