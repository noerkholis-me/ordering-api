package dtos.appsetting;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class AppSettingResponse  {

    @JsonProperty("id")
    public Long id;

    @JsonProperty("merchant_name")
    public String merchantName;

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

    @JsonProperty("merchant_id")
    private Long merchantId;

    @JsonProperty("is_deleted")
    public Boolean isDeleted;

}
