package dtos.pupoint;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PickUpPointResponse  {

    @JsonProperty("id")
    public Long id;

    @JsonProperty("pupoint_name")
    public String pupointName;

    @JsonProperty("store_id")
    public Long storeId;

    @JsonProperty("merchant_id")
    public Long merchantId;

    @JsonProperty("is_active")
    public Boolean isActive;

    @JsonProperty("is_deleted")
    public Boolean isDeleted;

}
