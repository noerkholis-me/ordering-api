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
public class ProductAddOnRequest {

    @JsonProperty("product_assign_id")
    private Long productAssignId;

    @JsonProperty("product_id")
    private Long productId;

    @JsonProperty("is_active")
    private Boolean isActive;

    @JsonProperty("product_type")
    private String productType;
}
