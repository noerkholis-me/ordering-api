package dtos.store;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class MultiStoreResponse {

    private Long id;
    @JsonProperty("multi_store_code")
    private String multiStoreCode;
    @JsonProperty("address_type")
    private String addressType;
    @JsonProperty("address")
    private String address;
    @JsonProperty("store_phone")
    private String storePhone;
    @JsonProperty("merchant_id")
    private Long merchantId;
    @JsonProperty("google_maps_url")
    private String googleMapsUrl;
    private Double latitude;
    private Double longitude;
    @JsonProperty("multi_store_qr_code")
    private String multiStoreQrCode;

}
