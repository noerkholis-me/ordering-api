package dtos.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import utils.BigDecimalSerialize;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderDetailResponse {

    @JsonProperty("product_name")
    private String productName;
    @JsonProperty("qty")
    private Integer qty;
    @JsonProperty("total")
    @JsonSerialize(using = BigDecimalSerialize.class)
    private BigDecimal total;
    @JsonProperty("no_sku")
    private String noSku;
    @JsonProperty("order_detail_add_on")
    private List<OrderDetailAddOnResponse> orderDetailAddOns;

}
