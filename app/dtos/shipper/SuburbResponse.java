package dtos.shipper;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

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
    @Builder
    public static class Suburb {
        private Long id;
        private String suburbName;
    }
}
