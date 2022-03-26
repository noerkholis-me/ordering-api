package dtos.shipper;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SuburbResponse {

    @JsonProperty("city_id")
    private Long cityId;
    private List<Suburb> suburbs;

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class Suburb {
        private Long id;
        private String suburbName;
    }
}
