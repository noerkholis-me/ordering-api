package dtos.merchant;

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
public class QrGroupRequest {

    @JsonProperty("group_name")
    private String groupName;

    @JsonProperty("group_logo")
    private String groupLogo;

    @JsonProperty("address_type")
    private Boolean addressType;

    @JsonProperty("address")
    private String address;

    @JsonProperty("phone")
    private String phone;

    @JsonProperty("province_id")
    private Long provinceId;

    @JsonProperty("city_id")
    private Long cityId;

    @JsonProperty("suburb_id")
    private Long suburbId;

    @JsonProperty("area_id")
    private Long areaId;

    @JsonProperty("url_gmap")
    private String urlGmap;

}
