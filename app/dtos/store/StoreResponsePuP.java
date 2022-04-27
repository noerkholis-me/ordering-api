package dtos.store;

import com.fasterxml.jackson.annotation.JsonProperty;
import dtos.shipper.AreaResponse;
import dtos.shipper.CityResponse;
import dtos.shipper.ProvinceResponse;
import dtos.shipper.SuburbResponse;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class StoreResponsePuP {

    private Long id;
    @JsonProperty("store_code")
    private String storeCode;
    @JsonProperty("store_name")
    private String storeName;

}
