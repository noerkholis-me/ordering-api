package dtos.shipper;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Logistic {
    @JsonProperty("regular")
    private List<RateType> regular;
    @JsonProperty("trucking")
    private List<RateType> trucking;
    @JsonProperty("instant")
    private List<RateType> instant;
    @JsonProperty("same_day")
    private List<RateType> sameDay;
    @JsonProperty("express")
    private List<RateType> express;
}
