package dtos.role;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class RoleMerchantResponse  {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("is_deleted")
    private Boolean isDeleted;
    @JsonProperty("name")
    private String name;
    @JsonProperty("key")
    private String key;
    @JsonProperty("description")
    private String description;
    @JsonProperty("merchant_id")
    private Long merchantId;
}