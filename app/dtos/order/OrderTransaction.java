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
public class OrderTransaction {

    @JsonProperty("store_code")
    private String storeCode;
    @JsonProperty("order_type")
    private String orderType;
    @JsonProperty("device_type")
    private String deviceType;
    @JsonProperty("merchant_name")
    private String merchantName;
    @JsonProperty("customer_email")
    private String customerEmail;
    @JsonProperty("customer_name")
    private String customerName;
    @JsonProperty("customer_phone_number")
    private String customerPhoneNumber;
    @JsonProperty("product_order_detail")
    private List<ProductOrderDetail> productOrderDetail;
    @JsonProperty("payment_detail")
    private PaymentDetailResponse paymentDetailResponse;

}
