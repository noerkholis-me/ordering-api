package dtos.shipper;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ProvinceResponse {

    private Long id;
    @JsonProperty("province_name")
    private String provinceName;

}
