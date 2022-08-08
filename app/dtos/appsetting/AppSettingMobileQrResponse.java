package dtos.appsetting;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class AppSettingMobileQrResponse {

    @JsonProperty("mobile_qr_name")
    public String mobileQrName;

    @JsonProperty("primary_color")
    public String primaryColor;

    @JsonProperty("secondary_color")
    public String secondaryColor;

    @JsonProperty("app_logo")
    public String appLogo;

    @JsonProperty("favicon")
    public String favicon;

    @JsonProperty("threshold")
    private Integer threshold;

    @JsonProperty("image_guide")
    private String imageGuide;

    @JsonProperty("app_setting_payment_type")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private AppSettingPaymentTypeResponse appSettingPaymentTypeResponse;

}
