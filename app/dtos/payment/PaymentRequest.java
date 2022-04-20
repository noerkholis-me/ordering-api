package dtos.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PaymentRequest {

    @JsonProperty("order_number")
    private String orderNumber;
    @JsonProperty("payment_channel")
    private String paymentChannel;

}
