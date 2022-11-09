package dtos.shipper;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

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
    @JsonProperty("pricings")
    private List<Pricing> pricings;
}
