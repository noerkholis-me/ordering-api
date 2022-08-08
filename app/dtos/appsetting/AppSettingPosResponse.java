package dtos.appsetting;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class AppSettingPosResponse {

    @JsonProperty("app_setting_payment_type")
    private AppSettingPaymentTypeResponse appSettingPaymentTypeResponse;

}
