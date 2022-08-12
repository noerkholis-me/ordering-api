package dtos.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderQueueResponse {

    @JsonProperty("order_queue")
    private int orderQueue;

    @JsonProperty("customer_name")
    private String customerName;

    @JsonProperty("order_hour")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss", timezone = "Asia/Jakarta")
    private Date orderHour;

    @JsonProperty("status")
    private String status;

}
