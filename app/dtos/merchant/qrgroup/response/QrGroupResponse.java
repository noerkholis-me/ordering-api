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

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class QrGroupResponse {

    private Long id;

    @JsonProperty("merchant_id")
    private Long merchantId;

    @JsonProperty("group_name")
    private String groupName;

    @JsonProperty("group_logo")
    private String groupLogo;

    @JsonProperty("group_code")
    private String groupCode;

    @JsonProperty("group_qr_code")
    private String groupQrCode;

    @JsonProperty("address_type")
    private Boolean addressType;

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

    @JsonProperty("url_gmap")
    private String urlGmap;

    private Double latitude;

    private Double longitude;

    @JsonProperty("store")
    private List<QrGroupStoreResponse> store;

}
