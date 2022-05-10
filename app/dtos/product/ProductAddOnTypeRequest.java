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
public class ProductAddOnTypeRequest {

    @JsonProperty("product_type")
    private String productType;
    
    @JsonProperty("is_active")
    private Boolean isActive;

    @JsonProperty("merchant_id")
    private Long merchantId;
}
