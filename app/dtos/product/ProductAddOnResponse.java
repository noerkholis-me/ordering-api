package dtos.product;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class ProductAddOnResponse {

    private Long id;

    @JsonProperty("product_assign_id")
    private Long productAssignId;

    @JsonProperty("is_active")
    private Boolean isActive;

    @JsonProperty("product_type")
    private String productType;

    @JsonProperty("product_merchant_id")
    private Long productMerchantId;
}
