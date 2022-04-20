package dtos.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import dtos.order.PaymentDetailResponse;
import dtos.order.ProductOrderDetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class InitiatePaymentRequest {

    @JsonProperty("order_number")
    private String orderNumber;
    @JsonProperty("payment_detail")
    private PaymentDetailResponse paymentDetailResponse;
    @JsonProperty("product_detail")
    private List<ProductOrderDetail> productOrderDetails;
    @JsonProperty("merchant_name")
    private String merchantName;
    @JsonProperty("device_type")
    private String deviceType;

}
