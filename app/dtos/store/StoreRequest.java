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
public class StoreRequest {

    @JsonProperty("store_name")
    private String storeName;
    @JsonProperty("store_phone")
    private String storePhone;
    @JsonProperty("address")
    private String address;
    @JsonProperty("province_id")
    private Long provinceId;
    @JsonProperty("city_id")
    private Long cityId;
    @JsonProperty("suburb_id")
    private Long suburbId;
    @JsonProperty("area_id")
    private Long areaId;
    @JsonProperty("google_maps_url")
    private String googleMapsUrl;
    @JsonProperty("store_logo")
    public String storeLogo;
    @JsonProperty("status_open_store")
    public Boolean statusOpenStore;
    @JsonProperty("open_at")
    public String openAt;
    @JsonProperty("closed_at")
    public String closedAt;
}
