package dtos.delivery;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DeliveryFeeResponse {
   @JsonProperty("distance")
    private double distance;

    @JsonProperty("duration")
    private int duration;

    @JsonProperty("fee_delivery")
    private int feeDelivery;
  
}
