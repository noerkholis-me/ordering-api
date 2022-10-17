package dtos.store;

import com.fasterxml.jackson.annotation.JsonProperty;
import dtos.shipper.AreaResponse;
import dtos.shipper.CityResponse;
import dtos.shipper.ProvinceResponse;
import dtos.shipper.SuburbResponse;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class StoreResponse {

    private Long id;
    @JsonProperty("store_code")
    private String storeCode;
    @JsonProperty("store_name")
    private String storeName;
    @JsonProperty("store_phone")
    private String storePhone;
    @JsonProperty("address")
    private String address;
    @JsonProperty("province")
    private ProvinceResponse province;
    @JsonProperty("city")
    private CityResponse.City city;
    @JsonProperty("suburb")
    private SuburbResponse.Suburb suburb;
    @JsonProperty("area")
    private AreaResponse.Area area;
    @JsonProperty("google_maps_url")
    private String googleMapsUrl;
    private Double latitude;
    private Double longitude;
    @JsonProperty("store_qr_code")
    private String storeQrCode;
    @JsonProperty("merchant_id")
    private Long merchantId;
    @JsonProperty("store_logo")
    public String storeLogo;
    @JsonProperty("merchant_type")
    public String merchantType;
    @JsonProperty("store_queue_url")
    private String storeQueueUrl;
    @JsonProperty("product_stores")
    private List<ProductStoreResponseForStore> productStoreResponses;

}
