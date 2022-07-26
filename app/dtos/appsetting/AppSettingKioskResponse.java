package dtos.appsetting;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class AppSettingKioskResponse {

    @JsonProperty("primary_color")
    public String primaryColor;

    @JsonProperty("secondary_color")
    public String secondaryColor;

    @JsonProperty("app_logo")
    public String appLogo;

    @JsonProperty("favicon")
    public String favicon;

}
