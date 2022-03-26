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
public class AreaResponse {

    @JsonProperty("suburb_id")
    private Long suburbId;
    private List<Area> areas;

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class Area {
        private Long id;
        @JsonProperty("area_name")
        private String areaName;
        @JsonProperty("postal_code")
        private String postalCode;
    }

}
