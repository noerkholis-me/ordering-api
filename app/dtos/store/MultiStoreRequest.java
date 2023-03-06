package dtos.store;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MultiStoreRequest {

    @JsonProperty("address_type")
    private String addressType;
    @JsonProperty("address")
    private String address;
    @JsonProperty("store_phone")
    private String storePhone;
    @JsonProperty("google_maps_url")
    private String googleMapsUrl;

}
