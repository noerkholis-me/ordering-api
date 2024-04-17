package dtos.ratings;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StoreRateResponse {
    private String storeName;

    private String feedback;

    private float rate;
}
