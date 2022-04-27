package dtos.pupoint;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PickUpPointSetupResponse  {

    @JsonProperty("id")
    public Long id;

    @JsonProperty("image_pupoint_setup")
    public String imagePupointSetup;

    @JsonProperty("store_id")
    public Long storeId;

    @JsonProperty("merchant_id")
    public Long merchantId;

    @JsonProperty("is_deleted")
    public Boolean isDeleted;

}
