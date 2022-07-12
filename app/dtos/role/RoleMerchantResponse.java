package dtos.role;

import com.fasterxml.jackson.annotation.JsonProperty;
import dtos.feature.FeatureAssignRequest;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class RoleMerchantResponse  {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("key")
    private String key;
    @JsonProperty("description")
    private String description;
    @JsonProperty("merchant_id")
    private Long merchantId;
    @JsonProperty("is_cashier")
    private Boolean isCashier;
    private List<FeatureAssignRequest> features;
}