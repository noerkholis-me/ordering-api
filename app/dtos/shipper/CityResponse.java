package dtos.shipper;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class CityResponse {

    @JsonProperty("province_id")
    private Long provinceId;
    private List<City> cities;

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class City {
        private Long id;
        @JsonProperty("city_name")
        private String cityName;
    }

}
