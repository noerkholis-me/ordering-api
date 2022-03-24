package dtos.brand;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class BrandMerchantResponse  {

    @JsonProperty("id")
    public Long id;

    @JsonProperty("brand_name")
    public String brandName;

    @JsonProperty("image_web")
    public String imageWeb;

    @JsonProperty("image_mobile")
    public String imageMobile;

    @JsonProperty("is_deleted")
    public Boolean isDeleted;

    @JsonProperty("is_active")
    public Boolean isActive;

    @JsonProperty("merchant_id")
    private Long merchantId;


}
