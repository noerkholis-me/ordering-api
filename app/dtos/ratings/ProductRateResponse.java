package dtos.ratings;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductRateResponse {
    private String productName;

    private String feedback;

    private float rate;
}
