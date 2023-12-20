package dtos.ratings;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class StoreRateRequest {
    @JsonProperty("store_id")
    private Long storeId;

    @JsonProperty("customer_name")
    private String customerName;

    @JsonProperty("customer_email")
    private String customerEmail;

    @JsonProperty("customer_phone_number")
    private String customerPhoneNumber;

    @JsonProperty("customer_google_id")
    private String customerGoogleId;

    private float rate;

    private String feedback;

    private List<ProductStoreRateRequest> products;

    private List<Long> tags;
}
