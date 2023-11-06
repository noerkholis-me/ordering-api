package dtos.ratings;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ProductRateRequest {
    @JsonProperty("product_id")
    private Long product_id;

    @JsonProperty("store_id")
    private Long store_id;

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

    private List<Long> tags;
}
