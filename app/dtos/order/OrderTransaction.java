package dtos.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
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
    @JsonProperty("customer_email")
    private String customerEmail;
    @JsonProperty("customer_name")
    private String customerName;
    @JsonProperty("customer_phone_number")
    private String customerPhoneNumber;
    @JsonProperty("sub_total")
    private BigDecimal subTotal;
    @JsonProperty("total_price")
    private BigDecimal totalPrice;
    @JsonProperty("product_order_detail")
    private List<ProductOrderDetail> productOrderDetail;
    @JsonProperty("payment_detail")
    private PaymentDetailResponse paymentDetailResponse;
    @JsonProperty("pickup_point_id")
    private Long pickupPointId;
    @JsonProperty("table_id")
    private Long tableId;

    // GET USAGE LOYALTY POINT
    @JsonProperty("use_loyalty")
    private Boolean useLoyalty;
    @JsonProperty("loyalty_usage")
    private BigDecimal loyaltyUsage;

}
