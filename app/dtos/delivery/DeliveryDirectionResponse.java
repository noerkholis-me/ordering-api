package dtos.delivery;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DeliveryDirectionResponse {

    @JsonProperty("distance")
    private int distance;

    @JsonProperty("duration")
    private int duration;

}
