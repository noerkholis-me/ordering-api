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

    @JsonProperty("order_type")
    private String orderType;
    @JsonProperty("product_order_detail")
    private List<ProductOrderDetail> productOrderDetail;
    @JsonProperty("payment_detail")
    private PaymentDetail paymentDetail;
    @JsonProperty("store_info")
    private StoreInfo storeInfo;

}
