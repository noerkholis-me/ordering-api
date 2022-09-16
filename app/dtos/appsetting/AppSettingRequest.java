package dtos.appsetting;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AppSettingRequest {

    @JsonProperty("app_setting_kiosk")
    private AppSettingKioskResponse appSettingKioskRequest;

    @JsonProperty("app_setting_mobile_qr")
    private AppSettingMobileQrResponse appSettingMobileQrRequest;

}
