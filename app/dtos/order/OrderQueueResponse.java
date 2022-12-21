package dtos.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import models.transaction.*;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderQueueResponse {

    @JsonProperty("order_queue")
    private int orderQueue;

    @JsonProperty("no_transaction")
    private String transactionNo;

    @JsonProperty("customer_name")
    private String customerName;

    @JsonProperty("item")
    private List<OrderDetail> orderDetails;

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class OrderDetail {
        @JsonProperty("product_name")
        private String productName;
        private int qty;

        @JsonProperty("item-addon")
        private List<OrderDetailAddOn> orderDetailAddOn;

        @AllArgsConstructor
        @NoArgsConstructor
        @Data
        public static class OrderDetailAddOn {
            @JsonProperty("product_name")
            private String productName;
            private int qty;
        }
    }

    @JsonProperty("order_hour")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss", timezone = "Asia/Jakarta")
    private Date orderHour;

    @JsonProperty("status")
    private String status;

}
