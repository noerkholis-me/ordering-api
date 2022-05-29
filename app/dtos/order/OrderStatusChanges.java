package dtos.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class OrderStatusChanges {

    // @JsonProperty("order_id")
    // private String orderOrder;
    @JsonProperty("order_number")
    private String orderNumber;
    @JsonProperty("status_order")
    private String statusOrder;

}
