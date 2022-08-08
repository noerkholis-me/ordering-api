package dtos.appsetting;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class SettingApplicationResponse {

    @JsonProperty("id")
    public Long id;

    @JsonProperty("merchant_id")
    private Long merchantId;

    @JsonProperty("app_setting_kiosk")
    private AppSettingKioskResponse appSettingKioskResponse;

    @JsonProperty("app_setting_pos")
    private AppSettingPosResponse appSettingPosResponse;

    @JsonProperty("app_setting_mobile_qr")
    private AppSettingMobileQrResponse appSettingMobileQrResponse;

}
