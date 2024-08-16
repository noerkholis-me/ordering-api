package dtos.merchant.qrgroup.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import dtos.shipper.AreaResponse;
import dtos.shipper.CityResponse;
import dtos.shipper.ProvinceResponse;
import dtos.shipper.SuburbResponse;
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
public class QrGroupStoreResponse {

    private Long id;

    @JsonProperty("store_id")
    private Long storeId;

    @JsonProperty("store_name")
    private String storeName;

    @JsonProperty("address")
    private String address;

    @JsonProperty("phone")
    private String phone;

    @JsonProperty("province")
    private ProvinceResponse province;

    @JsonProperty("city")
    private CityResponse.City city;

    @JsonProperty("suburb")
    private SuburbResponse.Suburb suburb;

    @JsonProperty("area")
    private AreaResponse.Area area;

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

    @JsonProperty("status_open_store")
    private Boolean statusOpenStore;

    @JsonProperty("slug")
    private String slug;

    @JsonProperty("open_at")
    public String openAt;

    @JsonProperty("closed_at")
    public String closedAt;
}
