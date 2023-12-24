package dtos.ratings;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ProductStoreRateRequest {
    @JsonProperty("product_id")
    private Long product_id;

    private float rate;

    private String feedback;

    @JsonProperty("order_number")
    private String orderNumber;

}
