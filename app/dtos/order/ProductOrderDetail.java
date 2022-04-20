package dtos.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ProductOrderDetail {

    @JsonProperty("product_id")
    private Long productId;
    @JsonProperty("product_price")
    private BigDecimal productPrice;
    @JsonProperty("product_qty")
    private Integer productQty;
    @JsonProperty("notes")
    private String notes;

}
