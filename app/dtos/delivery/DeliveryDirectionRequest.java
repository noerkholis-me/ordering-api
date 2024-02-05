package dtos.delivery;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class DeliveryDirectionRequest {

    @JsonProperty("long")
    public double Long;

    @JsonProperty("lat")
    public double Lat;

}
