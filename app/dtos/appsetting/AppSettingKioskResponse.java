package dtos.appsetting;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class AppSettingKioskResponse {

    @JsonProperty("kiosk_name")
    public String kioskName;

    @JsonProperty("primary_color")
    public String primaryColor;

    @JsonProperty("secondary_color")
    public String secondaryColor;

    @JsonProperty("app_logo")
    public String appLogo;

    @JsonProperty("favicon")
    public String favicon;

    @JsonProperty("image_guide")
    public String imageGuide;

    @JsonProperty("app_setting_payment_type")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private AppSettingPaymentTypeResponse appSettingPaymentTypeResponse;

}
