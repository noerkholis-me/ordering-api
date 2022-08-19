package dtos.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import utils.BigDecimalSerialize;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ProductOrderAddOn {

    @JsonProperty("product_id")
    private Long productId;
    @JsonProperty("product_assign_id")
    private Long productAssignId;
    @JsonProperty("product_price")
    @JsonSerialize(using = BigDecimalSerialize.class)
    private BigDecimal productPrice;
    @JsonProperty("product_qty")
    private Integer productQty;
    @JsonProperty("sub_total")
    @JsonSerialize(using = BigDecimalSerialize.class)
    private BigDecimal subTotal;
    @JsonProperty("notes")
    private String notes;

}
