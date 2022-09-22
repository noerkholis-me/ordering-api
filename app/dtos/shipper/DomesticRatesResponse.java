package dtos.shipper;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class DomesticRatesResponse {
    @JsonProperty("originArea")
    private String originArea;
    @JsonProperty("destinationArea")
    private String destinationArea;
    @JsonProperty("rates")
    private Rates rates;
}
